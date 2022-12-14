package com.jt.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching //开启spring cache注解方式的缓存功能
public class ReggieTalkOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTalkOutApplication.class, args);
        log.info("项目启动成功...");
    }

}
