package com.tg.sbootshrio.pojo;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String userName;
    private String password;
    private String phone;
    private String email;
    private Integer sex;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", sex=" + sex +
                '}';
    }
}
