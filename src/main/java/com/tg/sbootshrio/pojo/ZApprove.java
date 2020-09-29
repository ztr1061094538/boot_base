package com.tg.sbootshrio.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created by admin on 2019/8/13.
 */
@Data
public class ZApprove {
    private Long id;
    private String processInstanceId;
    private Long approvedUserId;
    private Integer result;
    private String remark;
    private Date createTime;

    @Override
    public String toString() {
        return "ZApprove{" +
                "id=" + id +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", approvedUserId=" + approvedUserId +
                ", result=" + result +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
