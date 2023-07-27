package org.springboot.autoConfig;

import org.springboot.autoConfig.ShuyuAutoConfigurationImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ShuyuAutoConfigurationPackage
//@Import(ShuyuAutoConfigurationImportSelector.class)
public @interface ShuyuEnableAutoConfiguration {

    /**
     * Environment property that can be used to override when auto-configuration is
     * enabled.
     */
    String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

    /**
     * Exclude specific auto-configuration classes such that they will never be applied.
     * @return the classes to exclude
     */
    Class<?>[] exclude() default {};

    /**
     * Exclude specific auto-configuration class names such that they will never be
     * applied.
     * @return the class names to exclude
     * @since 1.3.0
     */
    String[] excludeName() default {};

}
