package com.classmanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 班级事务与学风管理系统启动类
 */
@SpringBootApplication
@EnableScheduling
public class ClassManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassManageApplication.class, args);
    }
}
