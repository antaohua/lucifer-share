package com.muniu.cloud.lucifer.share;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EntityScan(basePackages = {"com.muniu.cloud.lucifer.share.service.entity"})
@MapperScan(value = {"com.muniu.cloud.lucifer.share.service.mapper"})
@EnableTransactionManagement
@EnableWebMvc
@EnableCaching
public class LuciferShareApplicationBoot {
    public static void main(String[] args) {
        SpringApplication.run(LuciferShareApplicationBoot.class, args);
    }
}