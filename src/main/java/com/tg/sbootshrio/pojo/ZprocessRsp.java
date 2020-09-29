package com.tg.sbootshrio.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class ZprocessRsp {

    private Long startUserID;

    private String startUserName;

    private Date startTime;//发起时间

    private Date approvedTime;//审批时间

    private String processName; //流程名称

    private String processInstanceId;//流程实例id  或者流程编号

    private Integer processStatus;//先给数字 前段自己判断 1审批中  2审批完成

    private String taskId;//任务id

    private String reason;//描述

    private Integer days;

    @Override
    public String toString() {
        return "ZprocessRsp{" +
                "startUserID=" + startUserID +
                ", startUserName='" + startUserName + '\'' +
                ", startTime=" + startTime +
                ", approvedTime=" + approvedTime +
                ", processName='" + processName + '\'' +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", processStatus=" + processStatus +
                ", taskId='" + taskId + '\'' +
                ", reason='" + reason + '\'' +
                ", days=" + days +
                '}';
    }
}
