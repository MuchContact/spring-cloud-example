package org.skywalking.springcloud.test.projectd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan("org.skywalking.springcloud.test")
public class ProjectD_Main {

    public static void main(String[] args) {
        SpringApplication.run(ProjectD_Main.class, args);
    }
}
