package com.tg.sbootshrio.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created by admin on 2019/8/13.
 */
@Data
public class ZApply {
    private Long id;
    private String processInstanceId;
    private Long createId;
    private Integer processStatus;
    private Integer currentNode;
    private Long applyType;//流程类型id
    private Integer days;
    private String reason;
    private Integer isDel;
    private Date createTime;

    @Override
    public String toString() {
        return "ZApply{" +
                "id=" + id +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", createId=" + createId +
                ", processStatus=" + processStatus +
                ", currentNode=" + currentNode +
                ", applyType=" + applyType +
                ", days=" + days +
                ", reason='" + reason + '\'' +
                ", isDel=" + isDel +
                ", createTime=" + createTime +
                '}';
    }
}
