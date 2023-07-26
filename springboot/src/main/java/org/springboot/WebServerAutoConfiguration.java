package org.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebServerAutoConfiguration {

    @Bean
    @Conditional(JettyCondition.class)
    public JettyWebServer jettyWebServer() {
        return new JettyWebServer();
    }

    @Bean
    @Conditional(TomcatCondition.class)
    public TomcatWebServer tomcatWebServer() {
        return new TomcatWebServer();
    }

}