package com.jnl;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = {"com.jnl.*"})
@MapperScan("com.jnl.mapper")
public class Main extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}



/*
@SpringBootApplication
@EnableScheduling
public class Main {

    private static final AtomicBoolean atomic = new AtomicBoolean(true);

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Scheduled(fixedDelay = 6000, initialDelay = 0)
    public void taskTest() {
        atomic.set(false);
        System.out.println("任务执行");
    }
}*/
