package com.tg.sbootshrio.init;

import com.tg.sbootshrio.mapper.ZProcessMapper;
import com.tg.sbootshrio.mapper.ZProcessNodeMapper;
import com.tg.sbootshrio.pojo.ZProcess;
import com.tg.sbootshrio.pojo.ZProcessNode;
import com.tg.sbootshrio.websoket.MyWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@Slf4j
public class InitDeployProcess implements CommandLineRunner {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private ZProcessMapper zProcessMapper;//流程类型

    @Autowired
    private ZProcessNodeMapper ZProcessNodeMapper;

    @Override
    public void run(String... args) throws Exception {


//        Thread.sleep(10000);
//        log.info("初始化流程 开始....");
//        String processName = "测试流程0827001";
//        Deployment deployment = repositoryService//获取流程定义和部署对象相关的Service
//                .createDeployment()//创建部署对象
//                .name(processName)//声明流程的名称
//                .addClasspathResource("processes/leave.bpmn")//加载资源文件，一次只能加载一个文件
//                .addClasspathResource("processes/leave.png")
//                .deploy();//完成部署
//        //入流程类型表（字典表）
//        ZProcess zProcess = new ZProcess();
//        zProcess.setName(processName);
//        zProcess.setIsDel(0);
//        zProcessMapper.insert(zProcess);
//        Long id = zProcess.getId();
//        System.out.println("插入zProcess返回的主键id = " + id);
//        //入流程节点表
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
//        if (bpmnModel != null) {
//            Process process = bpmnModel.getProcesses().get(0);
//            List<UserTask> UserTaskList = process.findFlowElementsOfType(UserTask.class);
//            for (UserTask userTask : UserTaskList) {
//                ZProcessNode zProcessNode=new ZProcessNode();
//                zProcessNode.setIsDel(0);
//                zProcessNode.setNodeName(userTask.getName());
//                zProcessNode.setType(id);
//                zProcessNode.setAssigneeName(userTask.getAssignee());
//                ZProcessNodeMapper.insert(zProcessNode);
//            }
//        }
//        log.info("初始化流程 结束....");
    }
}
