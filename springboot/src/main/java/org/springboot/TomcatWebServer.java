package org.springboot;

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class TomcatWebServer implements WebServer {

    @Override
    public void start(WebApplicationContext applicationContext) {

        Tomcat tomcat = new Tomcat();

        Server server = tomcat.getServer();
        Service service = server.findService("Tomcat");

        // Set up a connector.
        Connector connector = new Connector();
        connector.setPort(8081);

        // Set up an engine.
        Engine engine = new StandardEngine();
        engine.setDefaultHost("localhost");

        // Set up a host and connect to the engine by the host name.
        Host host = new StandardHost();
        host.setName("localhost");

        // Set up a context.
        String contextPath = "/v1/hugo";
        Context context = new StandardContext();
        context.setPath(contextPath);
        context.addLifecycleListener(new Tomcat.FixContextListener());

        // Connect the engine to the host and the host to the context.
        host.addChild(context);
        engine.addChild(host);

        // Binds engine and connector to the service.
        service.setContainer(engine);
        service.addConnector(connector);

        // connect the context, servlet and the web application container together
        tomcat.addServlet(contextPath, "anyServletName", new DispatcherServlet(applicationContext));
        context.addServletMappingDecoded("/*", "anyServletName");

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}