package com.dq.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class DqApplication {
    public static ConfigurableApplicationContext run(String[] args) {
        return SpringApplication.run(DqApplication.class, args);
    }

    public static void main(String[] args) {
        run(args);  //NOSONAR
    }
}