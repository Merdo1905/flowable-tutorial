package com.practice.flowable.controller;

import lombok.extern.java.Log;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("task")
@Log
public class TaskController {

    /*
        Process engine autowired with default engine
        autowiring this equals the code >> ProcessEngine autoWiredProcessEngine = (ProcessEngine) applicationContext.getBean(ProcessEngine.class);
        Since there is only one bean at the start we don't need to use bean name. You can do this by using @Qualifier annotation
     */
    @Autowired
    private ProcessEngine autoWiredProcessEngine;

    /*
     * We want to use taskService in this controller and we don't want to call .getTaskService() each time.
     * So we created a TaskService Instance and initiate it after the context loaded.
     * You can check starting cycle of spring boot on google if you want to know
     * */
    private TaskService taskService;

    @PostConstruct
    private void initTaskService() {
        taskService = autoWiredProcessEngine.getTaskService();
    }

    @GetMapping("defaultEngine/{groupName}/{id}")
    public ResponseEntity getTaskFromDefaultEngine(@PathVariable("groupName") String groupName, @PathVariable("id") Integer id) {
        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup(groupName).list();
        Task task = taskList.get(id);
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        return new ResponseEntity(processVariables, HttpStatus.OK);
    }

    @PutMapping("defaultEngine/{groupName}/{id}")
    public ResponseEntity forwardUserTaskWithDefaultEngine(@PathVariable("groupName") String groupName, @PathVariable("id") Integer id, @RequestBody Map<String, Object> variables) {

        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(groupName).list();
        Task task = tasks.get(id);
        taskService.complete(task.getId(), variables);
        return new ResponseEntity(HttpStatus.OK);
    }

    /*
     * Here goes my sick way to use multi engine/multi schema with flowable.
     */
    @Autowired
    ApplicationContext applicationContext;

    // @RequestParam means variables after ? with a & between each other on the url
    @GetMapping("{groupName}/{id}")
    public ResponseEntity getTaskByRoleName(@PathVariable("groupName") String groupName, @PathVariable("id") Integer id, @RequestParam String tenantId) {

        ProcessEngine processEngine = (ProcessEngine) applicationContext.getBean(tenantId);
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = processEngine.getTaskService().createTaskQuery().taskCandidateGroup(groupName).list();
        Task task = tasks.get(id);
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        return new ResponseEntity(processVariables, HttpStatus.OK);
    }

    // All hail HttpRequest.PUT -tin
    @PutMapping("{groupName}/{id}")
    public ResponseEntity forwardUserTask(@PathVariable("groupName") String groupName, @PathVariable("id") Integer id, @RequestBody Map<String, Object> variables, @RequestParam String tenantId) {
        //RequestBody is the body of HttpRequest. Just go to postman, turn the request type to put open the body tab, SpringLikesItRaw,
        // click the orange "Text" text and choose JSON.
        ProcessEngine processEngine = (ProcessEngine) applicationContext.getBean(tenantId);
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(groupName).list();
        Task task = tasks.get(id);
        taskService.complete(task.getId(), variables);
        //No you repeat
        return new ResponseEntity(HttpStatus.OK);
    }

}
