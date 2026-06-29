package com.meishuosoft.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@MapperScan("com.meishuosoft.rag.**.mapper")
@SpringBootApplication
public class EnterpriseRagApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseRagApplication.class, args);
    }
}
