package com.tg.sbootshrio.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;

/**
 * Created by admin on 2019/8/13.
 */
@Data
public class ZProcess {
    @GeneratedValue(generator = "JDBC")
    private Long id;
    private String name;
    private Integer isDel;

    @Override
    public String toString() {
        return "ZProcess{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isDel=" + isDel +
                '}';
    }
}
