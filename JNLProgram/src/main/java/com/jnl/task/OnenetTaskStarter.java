package com.jnl.task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//@Component
public class OnenetTaskStarter implements CommandLineRunner {

    @Resource
    OnenetTask onenetTask;



    @Override
    public void run(String... args) throws Exception {
        onenetTask.OnenetAPI();
    }
}