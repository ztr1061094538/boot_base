package com.tg.sbootshrio;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@MapperScan("com.tg.sbootshrio.mapper")
@EnableScheduling
public class SbootShrioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbootShrioApplication.class, args);
    }

}
