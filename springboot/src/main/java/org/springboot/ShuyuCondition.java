package org.springboot;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;
import java.util.Objects;

public class ShuyuCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ShuyuConditionalOnClass.class.getName());
        if (annotationAttributes == null) {
            throw new IllegalStateException("@ShuyuConditionalOnClass should have the class name");
        }
        String className = (String) annotationAttributes.get("value");
        try {
            Objects.requireNonNull(context.getClassLoader()).loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}