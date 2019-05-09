package com.practice.flowable.config;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineConfiguration {

    // Are you ready kids
    // Aye aye captain
    // I can't hear you
    // Aye aye captain
    // Oooooooh
    // Who live in @Bean under the ApplicationContext
    @Bean("processEngine")
    public ProcessEngine initProcessEngine() {

        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setEngineName("processEngine")
                .setDatabaseSchemaUpdate(ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_TRUE)
                .setJdbcUrl("jdbc:h2:mem:flowable-processEngine;DB_CLOSE_DELAY=1000")
                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setJdbcDriver("org.h2.Driver");
        return cfg.buildProcessEngine();
    }
    //I know you read it with his voice, don't deny it
}
