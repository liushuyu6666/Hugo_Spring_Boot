package org.springboot;

import org.springframework.web.context.WebApplicationContext;


public class JettyWebServer implements WebServer {
    @Override
    public void start(WebApplicationContext applicationContext) {
        System.out.println("start jetty");
    }
}