package org.springboot.autoConfig;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class ShuyuAutoConfigurationImportSelector implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[0];
    }

    protected Class<?> getAnnotationClass() {
        return ShuyuEnableAutoConfiguration.class;
    }
}
