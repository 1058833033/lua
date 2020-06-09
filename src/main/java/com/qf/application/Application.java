package com.qf.application;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ChenJie
 * @date 2020-06-08 11:18:03
 * 功能说明
 */
@SpringBootApplication(scanBasePackages = "com.qf")
@MapperScan("com.qf.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
