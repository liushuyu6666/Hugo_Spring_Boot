- [Overview](#overview)
  - [Branch Overview](#branch-overview)
- [HTTP Request handling Process](#http-request-handling-process)
  - [Tomcat](#tomcat)
- [`ShuyuSpringApplication` Class](#shuyuspringapplication-class)
  - [`run()` function](#run-function)
  - [`getWebServer` method](#getwebserver-method)
  - [`startTomcat()` function](#starttomcat-function)
- [`WebServerAutoConfiguration` class](#webserverautoconfiguration-class)
  - [`tomcatWebServer` bean](#tomcatwebserver-bean)
  - [`jettyWebServer` bean](#jettywebserver-bean)
- [Rules](#rules)
  - [Beans Generation](#beans-generation)
  - [Dependencies](#dependencies)




# Overview
This is a tutorial on handwritten Spring Boot source code, designed to simulate the Spring Boot framework. It aims to implement and comprehend the operational mechanisms of various classes and annotations within the Spring Boot framework.

## Branch Overview
1. master: a simple version of Spring Boot powered by Tomcat. it runs on `localhost:8081` and the API is `localhost:8081:test`.
2. p1-multiple-webservers: Use `WebServerAutoConfiguration` to seamlessly switch between Tomcat and Jetty.

# HTTP Request handling Process
1. When the application is executed, it initializes all beans from the `.com.liushuyu.user` package, `MyApplication` class as well as `WebServerAutoConfiguration` class. Subsequently, web server starts running, and the `Connector` begins listening to incoming HTTP requests.

2. Upon receiving HTTP requests, the web server parses the URLs to send them to the right controller bean.

## Tomcat
This section provides a detailed explanation of how the embedded Tomcat handles HTTP requests when functioning as the web server. The discussion is based on the code located in `springboot/src/main/java/org/springboot/TomcatWebServer.java`.

Tomcat is composed of several key components:
1. `Service`: Each service can have multiple `Connectors` but only one `Engine`.
2. `Connector`: Listens for incoming requests on a specified port (e.g., port `8081` in this project) and forwards them to the `Engine`. Configuration of the port number is done within the `Connector`, and it is added to the `Service` using `service.addConnector(connector)`.
3. `Engine`: Hosts multiple virtual hosts (`host` in the code). It uses the request's `Host` header to determine the appropriate virtual host when receiving requests from the `Connector`. The `Engine` is added to the Service with `service.setContainer(engine)`.
4. `Host`: Represents the mentioned virtual host. It can contain multiple `Contexts`. The `Host` routes requests to the corresponding `Context` (web application) based on the request's context path (e.g., `/v1/hugo`). Configuration of the host header is done within the `Host`, and it is added to the `Engine` using `engine.addChild(host)`.
5. `Context`: Represents a web application within the web server, bound to only one web application. Configuration of the context path is done within the `Context`.
6. `DispatcherServlet`: This front controller servlet hosts multiple controller beans. It parses the URL to extract its suffix (e.g., `http://localhost:8081/v1/hugo/${suffix}`) and forwards the request to the respective controller beans (handlers) for processing. The servlet connects to the `Context` with the context path by `tomcat.addServlet(contextPath, "anyServletName", new DispatcherServlet(applicationContext))`. The `DispatcherServlet` is able to handle all requests (`/*`) within the Context by `context.addServletMappingDecoded("/*", "dispatcher");`.
7. Web Application: This code `tomcat.addServlet(contextPath, "anyServletName", new DispatcherServlet(applicationContext))` also connects the web application container to the servlet.

This image provides a concise overview of the request flow.
![request](static/web_server.png)

# `ShuyuSpringApplication` Class
We will focus on methods in the `ShuyuSpringApplication` Class to see what happens.

## `run()` function
In the `run()` function:
1. Create a Spring container `applicationContext`.
2. Register the `MyApplication` class as a configuration class within the `applicationContext`.
3. During the refresh process, the Spring container scans and creates beans defined in the `MyApplication` class. This process involves the following steps:
    1. Identify functions annotated with `@Bean` within the `MyApplication` class and include them as beans in the container. For example
    ```java
    import java.beans.BeanProperty;public class MyApplication {
        // ... main ...
   
        @Bean
        public void beanComponents() {
            // This function will be scanned as a bean
        }    
    }
    ```
    2. Parse the `@ShuyuSpringBootApplication` annotation, which is used to annotate the configuration class.
        1. Extract configuration information from the `@ComponentScan` annotations within the `ShuyuSpringBootApplication` interface.
        2. If no specific scan path is provided, the Spring container will scan the package where `MyApplication` is located, which is `com.liushuyu.user`.
        3. The container scans and registers bean components, such as `UserController`, found within the `com.liushuyu.user` package.
    3. After this process, bean components defined in both the `MyApplication` class and the `com.liushuyu.user` package will be scanned and created.
4. Using `getWebServer` employs polymorphism to obtain the user-selected web server. Then the chosen web server is launched with the Spring container. This enables the application to be deployed and accessible through the web server.

## `getWebServer` method
1. Two conditional web server beans, `jettyWebServer` and `tomcatWebServer`, are defined in the `WebServerAutoConfiguration` class. However, only one of them will be instantiated at runtime.
2. During execution, this function retrieves the singular web server bean that has been created and returns it.

## `startTomcat()` function
1. Configure Tomcat parameters to set up the web server environment.
2. Integrate a `DispatcherServlet` into the `applicationContext` container:
    1. The `DispatcherServlet` plays a crucial role in processing incoming HTTP requests and mapping them to corresponding beans based on the URL suffix (e.g., `http://localhost:8081${suffix}`).
    2. By analyzing the URL, the `DispatcherServlet` identifies the appropriate controller bean responsible for handling the request and forwards it accordingly for further processing.

# `WebServerAutoConfiguration` class
This configuration class dynamically generates a web server bean based on specified conditions. To enable this feature, ensure you include the `@Import` annotation in the `ShuyuSpringBootApplication` interface.

## `tomcatWebServer` bean
This is a conditional bean that is created only when a specified condition evaluates to true. The condition checks for the existence of the class `org.apache.catalina.startup.Tomcat`, typically indicating the presence of the Tomcat dependency.

## `jettyWebServer` bean
This is a conditional bean that is created only when a specified condition evaluates to true. The condition checks for the existence of the class `org.eclipse.jetty.server.Server`, typically indicating the presence of the Jetty dependency. However, the Jetty web server dependency is marked as optional, ensuring that the `user` application remains independent of it.

# Rules
## Beans Generation
1. Methods that annotated by `@Beans` in the `MyApplication` class.
    1. No beans are instantiated within this method in the current branch.
2. Beans components within the `com.liushuyu.user` package.
    1. `UserController` in the current branch.
3. `WebServerAutoConfiguration` class contains beans imported by the `@Import` annotation in the `ShuyuSpringBootApplication` interface.
    1. `tomcatWebServer` or `jettyWebServer`

## Dependencies
1. Dependency Transitivity:
   The `user` application relies on our Spring Boot framework and therefore depends on all essential dependencies except for the optional ones provided by the framework.
2. Optional Dependency:
   To avoid the `user` application directly depends on Jetty within the Spring Boot framework, we can mark it as an optional dependency. Optional dependencies do not impact the current module's dependencies.
    ```.xml
        <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-server</artifactId>
            <version>9.4.48.v20220622</version>
            <optional>true</optional>
        </dependency>
    ```
   After setting Jetty as optional, the image illustrates its absence.
   ![Both_Jetty_and_Tomcat](static/depends_on_Jetty_and_Tomcat.png)

   ![Both_Jetty_and_Tomcat](static/depends_on_Tomcat_only.png)
3. Exclude Dependency:
   To utilize Jetty as the web server, we exclude Tomcat from the `user` application and incorporate the Jetty dependency.
    ```.xml
        <dependency>
            <groupId>org.springboot</groupId>
            <artifactId>springboot</artifactId>
            <version>1.0-SNAPSHOT</version>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.tomcat.embed</groupId>
                    <artifactId>tomcat-embed-core</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-server</artifactId>
            <version>9.4.48.v20220622</version>
        </dependency>
    ```

(<< Prev Branch)[https://github.com/liushuyu6666/Hugo_Spring_Boot/blob/master/Readme.md]

(>> Next Branch)[https://github.com/liushuyu6666/Hugo_Spring_Boot/blob/p2-conditional-annotation/Readme.md]