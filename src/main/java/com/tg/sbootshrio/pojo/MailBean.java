package com.tg.sbootshrio.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by admin on 2019/7/26.
 */
@Data
public class MailBean implements Serializable {
    private String recipient;//邮件接收人
    private String subject; //邮件主题
    private String content; //邮件内容

    @Override
    public String toString() {
        return "MailBean{" +
                "recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
