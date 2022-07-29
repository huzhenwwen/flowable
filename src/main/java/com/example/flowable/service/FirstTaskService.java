package com.example.flowable.service;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * flowable
 *
 * @author huzhenwen
 * @description TODO
 * @createDate 2022-7-28
 */
@Service
public class FirstTaskService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Transactional
    public void start(String key) {
        runtimeService.startProcessInstanceByKey(key);
    }

    @Transactional
    public long deploy() {
        // 资源路径
        String path = "single-task.bpmn20.xml";
        //创建部署构造器
        repositoryService.createDeployment().addClasspathResource(path).deploy();
        long singleTask = repositoryService.createDeploymentQuery().processDefinitionKey("singleTask").count();
        return singleTask;
    }

    @Transactional
    public long deployV2() {
        // 资源路径
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions\n" +
                "        xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
                "        xmlns:flowable=\"http://flowable.org/bpmn\"\n" +
                "        targetNamespace=\"Examples\">\n" +
                "    <process id=\"singleTask2\" name=\"The One Task Process\">\n" +
                "        <startEvent id=\"theStart\"/>\n" +
                "        <sequenceFlow id=\"flow1\" sourceRef=\"theStart\" targetRef=\"theTask\"/>\n" +
                "        <userTask id=\"theTask\" name=\"my test task\" flowable:assignee=\"huzw\"/>\n" +
                "        <sequenceFlow id=\"flow2\" sourceRef=\"theTask\" targetRef=\"theEnd\"/>\n" +
                "        <endEvent id=\"theEnd\"/>\n" +
                "    </process>\n" +
                "</definitions>\n";
        //创建部署构造器
        repositoryService.createDeployment().addString("single-task2.bpmn20.xml", text).deploy();
        long singleTask = repositoryService.createDeploymentQuery().processDefinitionKey("singleTask2").count();
        return singleTask;
    }

    public void deployV3() throws FileNotFoundException {
        String fileName = "path/multi-task.zip";
        ZipInputStream inputStream = new ZipInputStream(new FileInputStream(fileName));
        repositoryService.createDeployment()
                .name("multi-task.zip")
                .addZipInputStream(inputStream)
                .deploy();
    }

    @Transactional
    public List<Task> getTasks(String assignee) {
        return taskService.createTaskQuery().taskAssignee(assignee).list();
    }
}
