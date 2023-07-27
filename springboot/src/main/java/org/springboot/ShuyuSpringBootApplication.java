package org.springboot;

import org.springboot.autoConfig.ShuyuEnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ComponentScan
@ShuyuEnableAutoConfiguration
@Import(WebServerAutoConfiguration.class)
public @interface ShuyuSpringBootApplication {
}