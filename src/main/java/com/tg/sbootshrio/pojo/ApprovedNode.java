package com.tg.sbootshrio.pojo;

import lombok.Data;

@Data
public class ApprovedNode {
    private Integer nodeId;
    private String nodeName;
    private String approvedName;
    private String approvedTime;
    private String approvedStatus;
    private String result;

    @Override
    public String toString() {
        return "ApprovedNode{" +
                "nodeId=" + nodeId +
                ", nodeName='" + nodeName + '\'' +
                ", approvedName='" + approvedName + '\'' +
                ", approvedTime='" + approvedTime + '\'' +
                ", approvedStatus='" + approvedStatus + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
