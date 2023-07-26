- [Overview](#overview)
  - [Branch Overview](#branch-overview)
- [Higher View of All Processes](#higher-view-of-all-processes)
- [`ShuyuSpringApplication` Class](#shuyuspringapplication-class)
  - [`run()` function](#run-function)
  - [`startTomcat()` function](#starttomcat-function)
- [Rules](#rules)
  - [Beans Generation](#beans-generation)


# Overview
This is a tutorial on handwritten Spring Boot source code, designed to simulate the Spring Boot framework. It aims to implement and comprehend the operational mechanisms of various classes and annotations within the Spring Boot framework.

## Branch Overview
1. master: a simple version of Spring Boot powered by Tomcat. it runs on `localhost:8081` and the API is `localhost:8081:test`.

# Higher View of All Processes
1. When the application is executed, it initializes all beans from the `.com.liushuyu.user` package and the `MyApplication` class. Subsequently, Tomcat starts running, and the `DispatcherServlet` begins listening to incoming HTTP requests.

2. Upon receiving HTTP requests, the `DispatcherServlet` parses the URLs to extract their suffixes and forwards them to the respective controller beans for processing.

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
4. The Tomcat server is launched with the Spring container using the `startTomcat()` function. This enables the application to be deployed and accessible through the Tomcat server.

## `startTomcat()` function
1. Configure Tomcat parameters to set up the web server environment.
2. Integrate a `DispatcherServlet` into the `applicationContext` container:
    1. The `DispatcherServlet` plays a crucial role in processing incoming HTTP requests and mapping them to corresponding beans based on the URL suffix (e.g., `http://localhost:8081${suffix}`).
    2. By analyzing the URL, the `DispatcherServlet` identifies the appropriate controller bean responsible for handling the request and forwards it accordingly for further processing.

# Rules
## Beans Generation
1. Methods that annotated by `@Beans` in the `MyApplication` class.
2. Beans components within the `com.liushuyu.user` package.


[>> Next Branch](https://github.com/liushuyu6666/Hugo_Spring_Boot/tree/p1-multiple-webservers)