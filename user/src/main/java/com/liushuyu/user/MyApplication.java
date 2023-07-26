package com.liushuyu.user;


import org.springboot.JettyWebServer;
import org.springboot.ShuyuSpringApplication;
import org.springboot.ShuyuSpringBootApplication;
import org.springframework.context.annotation.Bean;

@ShuyuSpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        ShuyuSpringApplication.run(MyApplication.class);
    }

}