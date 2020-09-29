package com.tg.sbootshrio.pojo;

import lombok.Data;

@Data
public class ZProcessNode {
    private Integer nodeId;
    private String nodeName;
    private String assigneeName;
    private Long type;
    private Integer isDel;

    @Override
    public String toString() {
        return "ZProcessNode{" +
                "nodeId=" + nodeId +
                ", nodeName='" + nodeName + '\'' +
                ", assigneeName='" + assigneeName + '\'' +
                ", type=" + type +
                ", isDel=" + isDel +
                '}';
    }
}
