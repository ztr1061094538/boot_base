package com.tg.sbootshrio.pojo;

import lombok.Data;

/**
 * Created by admin on 2019/8/13.
 */
@Data
public class UserRole {
    private Integer id;
    private String userId;
    private Integer roleId;
    private String roleName;

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
