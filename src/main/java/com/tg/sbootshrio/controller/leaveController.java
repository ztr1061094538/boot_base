package com.tg.sbootshrio.controller;


import com.tg.sbootshrio.common.CommonResult;
import com.tg.sbootshrio.mapper.*;
import com.tg.sbootshrio.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by admin on 2019/7/4.
 */
@RequestMapping("/activiti")
@Controller
@Slf4j
public class leaveController {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    IdentityService identityService;
    @Autowired
    TaskService taskService;
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    HistoryService historyService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    private ZProcessMapper zProcessMapper;//流程类型

    @Autowired
    private ZApplyMapper zApplyMapper;

    @Autowired
    private ZApproveMapper zApproveMapper;

    @Autowired
    private ZProcessNodeMapper zProcessNodeMapper;


    /**
     * 写成随着项目启动自动部署   部署的时候一定要是   bpmn文件  不然流程定义表中没有数据
     * 需要开始流程的时候可以  带着名字 (将所有部署的请假流程列出来 供选择) ‘审批流程111’ 去表里面查询部署id   在去流程定义表中拿到 key  开启流程
     * 发起一个流程的时候 带上申请人的信息（发起人）
     */


    @RequestMapping("/prcessNodes/{deploymentId}")
    @ResponseBody
    public String prcessNodes(@PathVariable("deploymentId") String deploymentId) {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());


        System.out.println("bpmnModel = " + bpmnModel);
        if (bpmnModel != null) {
//            Process mainProcess = bpmnModel.getMainProcess();
//            Collection<FlowElement> flowElements = mainProcess.getFlowElements();
//            System.out.println("flowElements.size():"+flowElements.size());
//            for (FlowElement flowElement : flowElements) {
//                System.out.println("flowElement = " + flowElement);
//                String name = flowElement.getName();
//                System.out.println("name = " + name);
//            }


            Process process = bpmnModel.getProcesses().get(0);

            List<UserTask> UserTaskList = process.findFlowElementsOfType(UserTask.class);
            for (UserTask userTask : UserTaskList) {
                String name = userTask.getName();
                System.out.println("name = " + name);
                String assignee = userTask.getAssignee();
                System.out.println("assignee = " + assignee);
            }

        }

        return "succes";
    }


    /**
     * 登陆后发起流程    必须登陆
     *
     * @param zProcessId 流程类型id
     * @param userId
     * @param request
     * @return
     */
    @PostMapping("/startActiviti/{zProcessId}/{userId}")
    @ResponseBody
    @Transactional
    public CommonResult startActiviti(@PathVariable("zProcessId") Long zProcessId, @PathVariable("userId") String userId
            , HttpServletRequest request) {

        System.out.println("发起流程...zProcessId:" + zProcessId + "    userId:" + userId);
        //当前登陆人的姓名
        String currentName = (String) request.getSession().getAttribute(userId);
        if (currentName == null || currentName.equals("")) {
            System.out.println("暂无用户登陆..");
            log.info("暂无用户登陆..");
            return null;
        }
        // DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery().deploymentId(deploymentId);
        ZProcess zProcess = new ZProcess();
        zProcess.setId(zProcessId);
        String zpName = zProcessMapper.select(zProcess).get(0).getName();
        //通过流程名称查询部署表的 相同名字的最新部署的数据
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentName(zpName).orderByDeploymenTime().desc().singleResult();
        //通过 部署id 查询流程定义表
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        String key = processDefinition.getKey();//流程定义的key
        Map<String, Object> variables = new HashMap<String, Object>();
        //  variables.put("useBossr", userBoss.getId());//组长
//        variables.put("smallManager", "smallManager");
//        variables.put("bigManager", "bigManager");
//        variables.put("leader", "leader");
        //variables.put("days",u.getDays());
        variables.put("userId", currentName);//申请人 userid
        ProcessInstance processInstanceByKey = runtimeService.startProcessInstanceByKey(key, variables);
        String processInstanceId = processInstanceByKey.getId();//流程实例id
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        taskService.complete(task.getId());

        //插入流程表：
        ZApply zApply = new ZApply();
        zApply.setApplyType(zProcessId);
        zApply.setIsDel(0);
        zApply.setCreateId(Long.valueOf(userId));//发起人
        zApply.setProcessInstanceId(processInstanceId);//流程实例id
        zApply.setProcessStatus(1);//审批中
        zApply.setReason("测试原因");

        //当前节点
        Example zpExample = new Example(ZProcessNode.class);
        Example.Criteria criteria = zpExample.createCriteria();
        criteria.andEqualTo("type", zProcessId);
        zpExample.orderBy("nodeId").asc();
        ZProcessNode zProcessNode = zProcessNodeMapper.selectByExample(zpExample).get(0);
        zApply.setCurrentNode(zProcessNode.getNodeId());//当前审批节点
        zApplyMapper.insert(zApply);
        System.out.println(currentName + "发起+" + zpName + "成功...");
        log.info("{} 发起 {} 流程成功", currentName, zpName);
        return new CommonResult(200, "succes", null);
    }


    /**
     * 流程字典列表
     */
    @GetMapping("/getZprocessList")
    @ResponseBody
    public CommonResult getZprocessList() {
        ZProcess zProcess = new ZProcess();
        zProcess.setIsDel(0);
        List<ZProcess> select = zProcessMapper.select(zProcess);
        return new CommonResult(200, "succes", select);
    }


    /**
     * 查询代办任务
     */
    @GetMapping("/ckeckTask/{userId}")
    @ResponseBody
    public CommonResult ckeckTask(HttpServletRequest request, @PathVariable("userId") String userId) {
        String currentName = (String) request.getSession().getAttribute(userId);
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(currentName).list();
        List<ZApply> zApplies = zApplyMapper.selectAll();
        if (zApplies.isEmpty()) {
            return null;
        }
        Map<String, ZApply> applyMap = zApplies.stream().collect(Collectors.toMap(ZApply::getProcessInstanceId, Function.identity()));
        ZProcess zProcess = new ZProcess();
        zProcess.setIsDel(0);
        List<ZProcess> select = zProcessMapper.select(zProcess);
        Map<Long, ZProcess> zProcessMap = select.stream().collect(Collectors.toMap(ZProcess::getId, Function.identity()));
        List<ZprocessRsp> zprocessRspList = new ArrayList<>();
        ZprocessRsp zprocessRsp = null;
        for (Task task : tasks) {
            zprocessRsp = new ZprocessRsp();
            String processInstanceId = task.getProcessInstanceId();
            zprocessRsp.setProcessInstanceId(processInstanceId);//流程实例id
            ZApply zApply = applyMap.get(processInstanceId);
            zprocessRsp.setStartTime(zApply.getCreateTime());//发起时间
            zprocessRsp.setProcessStatus(zApply.getProcessStatus());//流程状态
            ZProcess zProcess1 = zProcessMap.get(zApply.getApplyType());
            zprocessRsp.setProcessName(zProcess1.getName());//流程名称
            zprocessRsp.setTaskId(task.getId());
            zprocessRspList.add(zprocessRsp);
        }
        return new CommonResult(200, "succes", zprocessRspList);
    }


    /**
     * 我发起的
     */
    @GetMapping("/mystart/{userId}")
    @ResponseBody
    public CommonResult mystart(HttpServletRequest request, @PathVariable("userId") String userId) {

        ZApply zapply = new ZApply();
        zapply.setCreateId(Long.valueOf(userId));
        zapply.setIsDel(0);
        List<ZApply> zApplies = zApplyMapper.select(zapply);
        ZProcess zProcess = new ZProcess();
        zProcess.setIsDel(0);
        List<ZProcess> select = zProcessMapper.select(zProcess);
        Map<Long, ZProcess> zProcessMap = select.stream().collect(Collectors.toMap(ZProcess::getId, Function.identity()));
        List<ZprocessRsp> zprocessRspList = new ArrayList<>();
        ZprocessRsp zprocessRsp = null;
        for (ZApply apply : zApplies) {
            zprocessRsp = new ZprocessRsp();
            zprocessRsp.setProcessInstanceId(apply.getProcessInstanceId());//流程实例id
            zprocessRsp.setStartTime(apply.getCreateTime());//发起时间
            zprocessRsp.setProcessStatus(apply.getProcessStatus());//流程状态
            ZProcess zProcess1 = zProcessMap.get(apply.getApplyType());
            zprocessRsp.setProcessName(zProcess1.getName());//流程名称
            zprocessRspList.add(zprocessRsp);
        }
        return new CommonResult(200, "succes", zprocessRspList);
    }


    /**
     * 我审批的
     */
    @GetMapping("/myApproved/{userId}")
    @ResponseBody
    public CommonResult myApproved(HttpServletRequest request, @PathVariable("userId") String userId) {
        List<ZprocessRsp> zprocessRspList = new ArrayList<>();

        ZApprove zApprove = new ZApprove();
        zApprove.setApprovedUserId(Long.valueOf(userId));
        //默认  一个流程中  同一个人只能审批一次
        List<ZApprove> zApproves = zApproveMapper.select(zApprove);
        if (zApproves.isEmpty()) {
            return new CommonResult(200, "succes", zprocessRspList);
        }

        List<String> instanceIds = zApproves.stream().map(e -> e.getProcessInstanceId()).collect(Collectors.toList());
        Example applyExample = new Example(ZApply.class);
        Example.Criteria criteria = applyExample.createCriteria();
        criteria.andIn("processInstanceId", instanceIds);
        List<ZApply> zApplies = zApplyMapper.selectByExample(applyExample);
        Map<String, ZApply> applyMap = zApplies.stream().collect(Collectors.toMap(ZApply::getProcessInstanceId, Function.identity()));
        ZProcess zProcess = new ZProcess();
        zProcess.setIsDel(0);
        List<ZProcess> select = zProcessMapper.select(zProcess);
        Map<Long, ZProcess> zProcessMap = select.stream().collect(Collectors.toMap(ZProcess::getId, Function.identity()));
        ZprocessRsp zprocessRsp = null;
        for (ZApprove approve : zApproves) {
            zprocessRsp = new ZprocessRsp();
            ZApply apply = applyMap.get(approve.getProcessInstanceId());
            zprocessRsp.setProcessInstanceId(apply.getProcessInstanceId());//流程实例id
            zprocessRsp.setStartTime(apply.getCreateTime());//发起时间
            zprocessRsp.setProcessStatus(apply.getProcessStatus());//流程状态
            ZProcess zProcess1 = zProcessMap.get(apply.getApplyType());
            zprocessRsp.setProcessName(zProcess1.getName());//流程名称
            zprocessRspList.add(zprocessRsp);
        }
        return new CommonResult(200, "succes", zprocessRspList);
    }

    /**
     * 审批  只有我的代办
     *
     * @return
     */
    @PostMapping("/approve/{taskId}/{result}/{instanceId}/{userId}")
    @ResponseBody
    @Transactional
    public CommonResult doTask(@PathVariable("taskId") String taskId, @PathVariable("result") Integer result, @PathVariable("instanceId") String instanceId
            , @PathVariable("userId") String userId) {
        Map<String, Object> variables = new HashMap<>();
        if (result == 1) {//通过
            variables.put("approved", true);
        } else {
            variables.put("approved", false);
        }
        taskService.complete(taskId, variables);

        //入审批表
        ZApprove zApprove = new ZApprove();
        zApprove.setProcessInstanceId(instanceId);
        zApprove.setApprovedUserId(Long.valueOf(userId));
        zApprove.setResult(result);
        zApprove.setRemark("remark 评论先写死");
        zApproveMapper.insert(zApprove);

        //审批的时候要改变 流程表里面的节点和状态
        ZApply zapp = new ZApply();
        zapp.setProcessInstanceId(instanceId);
        ZApply zApply = zApplyMapper.select(zapp).get(0);
        Long applyType = zApply.getApplyType();
        Integer currentNode = zApply.getCurrentNode();//当前节点

        //找当前节点的下一个节点
        Example zpExample = new Example(ZProcessNode.class);
        Example.Criteria criteria = zpExample.createCriteria();
        criteria.andEqualTo("type", applyType);
        criteria.andGreaterThan("nodeId", currentNode);
        criteria.andEqualTo("isDel", 0);
        zpExample.orderBy("nodeId");
        List<ZProcessNode> nodeList = zProcessNodeMapper.selectByExample(zpExample);
        Integer nextNodeId = null;
        if (!nodeList.isEmpty()) {
            nextNodeId = nodeList.get(0).getNodeId();
            zApply.setCurrentNode(nextNodeId);
            //跟新apply节点
        } else {
            // 没有下一个节点了的话   不更新节点  但是把状态 改为已完成
            zApply.setProcessStatus(2);
        }
        zApplyMapper.updateByPrimaryKeySelective(zApply);
        System.out.println("完成任务  ...");
        return new CommonResult(200, "succes", null);
    }


    /**
     * 流程详情(表单+流程节点)
     */
    @PostMapping("/detail/{instanceId}")
    @ResponseBody
    @Transactional
    public CommonResult detail(@PathVariable("instanceId") String instanceId) {

        //表单
        ZApply apply = new ZApply();
        apply.setProcessInstanceId(instanceId);
        ZApply zApply = zApplyMapper.select(apply).get(0);
        Long userId = zApply.getCreateId();//发起人id
        Date createTime = zApply.getCreateTime();//发起时间
        String reason = zApply.getReason();//发起说明
        Integer days = zApply.getDays();//天数
        User user = userMapper.selectByPrimaryKey(userId);
        String userName = user.getUserName();//发起人姓名
        ZprocessRsp zprocessRsp = new ZprocessRsp();
        zprocessRsp.setStartUserID(userId);
        zprocessRsp.setStartTime(createTime);
        zprocessRsp.setReason(reason);
        zprocessRsp.setDays(days);
        zprocessRsp.setStartUserName(userName);

        //流程节点
        List<ApprovedNode> nodeList = new ArrayList<>();
        ApprovedNode zNode = new ApprovedNode();
        zNode.setApprovedName(userName);
        zNode.setNodeName("发起申请");
        zNode.setApprovedTime(createTime + "");
        nodeList.add(zNode);
        /**
         * 审批完成
         */
        ZApprove zapprove = new ZApprove();
        zapprove.setProcessInstanceId(instanceId);
        List<ZApprove> zApproveList = zApproveMapper.select(zapprove);
        if (!zApproveList.isEmpty()) {
            ApprovedNode zNodeapproved = null;
            for (ZApprove zApprove : zApproveList) {
                zNodeapproved = new ApprovedNode();
                zNodeapproved.setApprovedTime(zApprove.getCreateTime() + "");
                if (zApprove.getResult() == 1) {
                    zNodeapproved.setResult("通过");
                } else {
                    zNodeapproved.setResult("拒绝");
                }
                zNodeapproved.setApprovedStatus("已审批");
                Long approvedUserId = zApprove.getApprovedUserId();
                User user1 = userMapper.selectByPrimaryKey(approvedUserId);
                zNodeapproved.setApprovedName(user1.getUserName());
                nodeList.add(zNodeapproved);
            }
        }


        /**
         * 审批中 和 待审批
         */
        Example zpExample = new Example(ZProcessNode.class);
        Example.Criteria criteria = zpExample.createCriteria();
        criteria.andEqualTo("type", zApply.getApplyType());
        criteria.andGreaterThanOrEqualTo("nodeId", zApply.getCurrentNode());
        criteria.andEqualTo("isDel", 0);
        zpExample.orderBy("nodeId");
        List<ZProcessNode> zProcessNodes = zProcessNodeMapper.selectByExample(zpExample);
        List<ZProcessNode> cNodes = new ArrayList<>();
        ZProcessNode zProcessNode = zProcessNodes.get(0);
        cNodes.add(zProcessNode);
        //审批中
        String assigneeName = zProcessNode.getAssigneeName();
        ApprovedNode currentNode = new ApprovedNode();
        currentNode.setApprovedStatus("审批中");
        currentNode.setApprovedName(assigneeName);
        nodeList.add(currentNode);

        zProcessNodes.removeAll(cNodes);
        //待审批
        ApprovedNode zNodeNext = null;
        for (ZProcessNode processNode : zProcessNodes) {
            zNodeNext = new ApprovedNode();
            zNodeNext.setApprovedStatus("待审批");
            zNodeNext.setApprovedName(processNode.getAssigneeName());
            nodeList.add(zNodeNext);
        }
        return new CommonResult(200, "succes", nodeList);
    }


    @RequestMapping("/getBPMN")
    public void DDDD(HttpServletResponse response) {

        System.out.println("yinqin===" + processEngine);

        try {
            InputStream is = getDiagram("5");
            if (is == null)
                return;

            response.setContentType("image/png");

            BufferedImage image = ImageIO.read(is);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "png", out);

            is.close();
            out.close();
        } catch (Exception ex) {
            System.out.println("查看流程图失败" + ex);
        }
    }

    public InputStream getDiagram(String processInstanceId) {
        System.out.println("流程实例id=" + processInstanceId);
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = StringUtils.EMPTY;

        if (processInstance == null) {
            System.out.println("走到这里了没~~~·");
            //查询已经结束的流程实例
            HistoricProcessInstance processInstanceHistory =
                    historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(processInstanceId).singleResult();
            if (processInstanceHistory == null)
                return null;
            else
                processDefinitionId = processInstanceHistory.getProcessDefinitionId();
        } else {
            processDefinitionId = processInstance.getProcessDefinitionId();
        }

        //使用宋体
        String fontName = "宋体";
        //获取BPMN模型对象
        BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
        //获取流程实例当前的节点，需要高亮显示
        List<String> currentActs = Collections.EMPTY_LIST;
        if (processInstance != null)
            currentActs = runtimeService.getActiveActivityIds(processInstance.getId());

        return processEngine.getProcessEngineConfiguration()
                .getProcessDiagramGenerator()
                .generateDiagram(model, "png", currentActs, new ArrayList<String>(),
                        fontName, fontName, fontName, null, 1.0);

    }


}
