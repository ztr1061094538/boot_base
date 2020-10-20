package com.tg.sbootshrio.pojo;

import lombok.Data;

/**
 * Created by admin on 2019/8/14.
 */
@Data
public class QueryTaskParam {
    private String userId;
    private Integer type;
    private Integer result;//1通过
    private String instanceId;
    private String taskId;
}
