package com.practice.flowable.controller;

import lombok.extern.java.Log;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("engine")
@Log //Go log yourself
public class EngineController {

    @Autowired
    ApplicationContext applicationContext;

    @PostMapping("create/{tenantId}")
    public ResponseEntity createEngine(@PathVariable("tenantId") String tenantId) {

        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setEngineName(tenantId)
                .setDatabaseSchemaUpdate(ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_TRUE)
                .setJdbcUrl("jdbc:h2:mem:flowable-" + tenantId + ";DB_CLOSE_DELAY=1000")
                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setJdbcDriver("org.h2.Driver");
        //cfg.registerTenant(tenantId, createDataSource("jdbc:h2:mem:flowable-" + tenantId + ";DB_CLOSE_DELAY=1000", "sa", ""));

        ConfigurableApplicationContext configContext = (ConfigurableApplicationContext) applicationContext;
        // These two lines allow us to serve MultipleEngines at the same time.
        // The other way is to Create a shared engine and switch between connections but this is better.
        // If you don't know why having multiple engines is better, You can go and run more docker containers in case of thousands of request coming at the same time
        SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();
        beanRegistry.registerSingleton(tenantId, cfg.buildProcessEngine());

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PostMapping("drop/{tenantId}")
    public ResponseEntity dropEngine(@PathVariable("tenantId") String tenantId) {
        //Release resources if they are not used. It would be better if you put this on a task so unused beans destroyed.
        ConfigurableApplicationContext configContext = (ConfigurableApplicationContext) applicationContext;
        SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();
        ((DefaultListableBeanFactory) beanRegistry).destroySingleton(tenantId);

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

}
