package com.example.yin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.example.yin.mapper")
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class YinMusicApplication {

    public static void main(String[] args) {
        SpringApplication.run(YinMusicApplication.class, args);
    }

}

