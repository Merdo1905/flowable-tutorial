package com.practice.flowable.controller;

import com.practice.flowable.dto.ProcessStartDTO;
import lombok.extern.java.Log;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("process")
@Log
public class ProcessController {

    /*
     *      Since this tutorial shows both SingleEngine/SingleScheme and MultiEngine/MultiSchema
     *      we used both autowired engine and applicationContext to get an engine for a tenant
     */
    @Autowired
    private ProcessEngine autoWiredProcessEngine;

    @PostMapping("defaultEngine/deploy")
    public ResponseEntity deployProcessWithDefaultEngine(@RequestParam String processXml) {

        Deployment deployment = autoWiredProcessEngine.getRepositoryService().createDeployment()
                .addClasspathResource(processXml)
                .deploy();

        return new ResponseEntity(deployment, HttpStatus.CREATED);
    }

    @GetMapping("defaultEngine/name")
    public ResponseEntity getProcessNamesFromDefaultEngine(@RequestParam String deploymentId) {
        ProcessDefinition processDefinition = autoWiredProcessEngine.getRepositoryService().createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .singleResult();

        return new ResponseEntity("Found process definition : " + processDefinition.getName(), HttpStatus.OK);
    }
    @PutMapping("defaultEngine/start")
    public ResponseEntity startProcessWithDefaultEngine(ProcessStartDTO processStartDTO) {

        ProcessInstance processInstance = startProcessIntstance(autoWiredProcessEngine, processStartDTO);

        return new ResponseEntity(processInstance.getStartTime(), HttpStatus.OK);
    }
    // Do you even code bro

    @Autowired
    ApplicationContext applicationContext;

    @PostMapping("deploy")
    public ResponseEntity deployProcess(@RequestParam String processXml, @RequestParam String tenantId) {

        ProcessEngine processEngine = (ProcessEngine) applicationContext.getBean(tenantId);
        Deployment deployment = processEngine.getRepositoryService().createDeployment()
                .addClasspathResource(processXml)
                .deploy();
        //We could implement a MultipartFile but nooooo
        return new ResponseEntity(deployment, HttpStatus.CREATED);
    }

    @GetMapping("name")
    public ResponseEntity getProcessNames(@RequestParam String deploymentId, @RequestParam String tenantId) {
        ProcessEngine processEngine = (ProcessEngine) applicationContext.getBean(tenantId);
        ProcessDefinition processDefinition = processEngine.getRepositoryService().createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .singleResult();

        return new ResponseEntity("Found process definition : " + processDefinition.getName(), HttpStatus.OK);
    }

    @PutMapping("start")
    public ResponseEntity startProcess(ProcessStartDTO processStartDTO, @RequestParam String tenantId) {

        ProcessEngine processEngine = (ProcessEngine) applicationContext.getBean(tenantId);
        ProcessInstance processInstance = startProcessIntstance(processEngine, processStartDTO);
        //I pity the fool who doesn't do null checks - Mr T.
        return new ResponseEntity(processInstance.getStartTime(), HttpStatus.OK);
    }

    private ProcessInstance startProcessIntstance(ProcessEngine processEngine, ProcessStartDTO processStartDTO) {

        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", processStartDTO.getEmployee());
        variables.put("nrOfHolidays", processStartDTO.getNumberOfHolidays());
        variables.put("description", processStartDTO.getDescription());

        return runtimeService.startProcessInstanceByKey(processStartDTO.getProcessName(), variables);
    }
}
