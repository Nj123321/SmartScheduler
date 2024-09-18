package com.SmartScheduler.Scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration
public class SchedulerApplication{
    public static void main(String[] args){
        SpringApplication.run(SchedulerApplication.class, args);
    }
}
