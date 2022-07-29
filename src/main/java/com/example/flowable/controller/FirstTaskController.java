package com.example.flowable.controller;

import com.example.flowable.service.FirstTaskService;
import com.example.flowable.utils.HttpUtils;
import com.example.flowable.vo.TaskList;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * flowable
 *
 * @author huzhenwen
 * @description TODO
 * @createDate 2022-7-28
 */
@RestController
@RequestMapping("/firstTask")
public class FirstTaskController {

    @Autowired
    private FirstTaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    //在springboot环境下，resources/processes目录下的任何BPMN 2.0流程定义都会被自动部署。
    /**
     * 根据文件路径部署
     * @return
     */
    @GetMapping("/deploy")
    private long deploy(){
        return taskService.deploy();
    }

    /**
     * 接口方式部署
     */
    @GetMapping("/v2/deploy")
    private long deployV2(){
      return taskService.deployV2();
    }

    /**
     * 打包部署
     */
    @GetMapping("/v3/deploy")
    private void deployV3() throws FileNotFoundException {
        taskService.deployV3();
    }

    /**------------------流程管理--------------------*/

    /**
     * 获取部署的流程列表
     * @return
     */
    @GetMapping("/getDeployList")
    private List getDeployList(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        List<TaskList> collect = list.stream().map(a -> {
            TaskList taskList = new TaskList();
            taskList.setName(a.getName());
            taskList.setId(a.getId());
            taskList.setKey(a.getKey());
            return taskList;
        }).collect(Collectors.toList());
        return collect;
    }


    /**
     * 读取流程图片
     */
    @GetMapping("/getImage")
    public void getImage(HttpServletResponse resp, String key){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).singleResult();
        String diagramResourceName = processDefinition.getDiagramResourceName();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), diagramResourceName);
        HttpUtils.writeFile(resp, resourceAsStream);
    }

    /**
     * 读取流程定义的xml
     */
    @GetMapping("/getXml")
    public void getXml(HttpServletResponse resp,String id, String name){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), name);
        HttpUtils.writeFile(resp, resourceAsStream);
    }

    /**
     * 删除部署的流程以及级联的实例等
     * @param id
     */
    @GetMapping("/deleteDeployment")
    public void deleteDeployment(String id){
        repositoryService.deleteDeployment(id, true);
    }

    /**
     * 启动流程实例
     */
    @GetMapping("/start")
    private void start(String key){
        taskService.start(key);
    }

    /**
     * 查询任务列表
     * @param assignee
     * @return
     */
    @GetMapping(value = "/getList",produces = MediaType.APPLICATION_JSON_VALUE)
    private List<TaskList> getList(String assignee){
        List<Task> tasks = taskService.getTasks(assignee);
        List<TaskList> collect = tasks.stream().map(a -> {
            TaskList taskList = new TaskList();
            taskList.setId(a.getId());
            taskList.setName(a.getName());
            return taskList;
        }).collect(Collectors.toList());
        return collect;
    }


}
