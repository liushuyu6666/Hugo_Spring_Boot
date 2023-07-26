package org.springboot;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.Map;

public class ShuyuSpringApplication {
    public static void run(Class clazz) {
        // 1. Create a Spring container
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();

        // 2, Register a configuration class
        applicationContext.register(clazz);

        // 3. During the refresh process, beans in this configuration class will be scanned and created.
        applicationContext.refresh();

        // 4. Launch the web server, (Tomcat / Jetty)
        WebServer webServer = getWebServer(applicationContext); // polymorphism
        webServer.start(applicationContext);
    }

    private static WebServer getWebServer(WebApplicationContext applicationContext) {
        // Retrieve WebServer type beans.
        Map<String, WebServer> beansOfType = applicationContext.getBeansOfType(WebServer.class);

        if (beansOfType.size() == 0) {
            throw new NullPointerException("There is no WebServer beans in the container");
        }

        if (beansOfType.size() > 1) {
            throw new IllegalStateException("There is more than on WebServer beans in the container");
        }

        return beansOfType.values().stream().findFirst().get();
    }
}