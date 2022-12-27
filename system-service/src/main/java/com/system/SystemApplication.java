package com.system;

import com.securit.config.annotation.EnableResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RefreshScope
@EnableResourceServer
@EnableTransactionManagement
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.system.**.dao"})
//@EnableFeignClients(basePackages = {"com.**.feign.client"})
@EnableFeignClients(basePackages = {"com.**.feign.client"})
@SpringBootApplication
@ComponentScan(basePackages = {"com.securit.*", "com.system.*"})
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

}
