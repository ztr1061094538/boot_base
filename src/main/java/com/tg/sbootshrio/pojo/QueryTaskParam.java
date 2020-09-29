package com.tg.sbootshrio.pojo;

import lombok.Data;

/**
 * Created by admin on 2019/8/14.
 */
@Data
public class QueryTaskParam {
    private String userId;

    private Integer type;//查询的类型    代办？发起？审批的？

}
