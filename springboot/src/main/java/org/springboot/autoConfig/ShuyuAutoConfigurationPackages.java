package org.springboot.autoConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;

public abstract class ShuyuAutoConfigurationPackages {

    private static final Log logger = LogFactory.getLog(ShuyuAutoConfigurationPackages.class);

    private static final String BEAN = ShuyuAutoConfigurationPackages.class.getName();

    public static void register(BeanDefinitionRegistry registry, String... packageNames) {
        if (registry.containsBeanDefinition(BEAN)) {
            BasePackagesBeanDefinition beanDefinition = (BasePackagesBeanDefinition) registry.getBeanDefinition(BEAN);
            beanDefinition.addBasePackages(packageNames);
            System.out.println(BEAN + " exists in the Bean Definition Registry, base package are added into this bean definition.");
        } else {
            registry.registerBeanDefinition(BEAN, new BasePackagesBeanDefinition(packageNames));
            System.out.println(BEAN + "doesn't exist, register this bean with base packages into the Bean Definition Registry.");
        }
    }
    static class Registrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
            PackageImports packageImports = new PackageImports(metadata);
            String[] packageNames = packageImports.getPackageNames().toArray(new String[0]);
            register(registry, packageNames);
        }

        public Set<Object> determineImports(AnnotationMetadata metadata) {
            return Collections.singleton(new PackageImports(metadata));
        }

    }

    private static final class PackageImports {

        private final List<String> packageNames;

        PackageImports(AnnotationMetadata metadata) {
            Map<String, Object> annotatedTypeMetadata = metadata.getAnnotationAttributes(ShuyuAutoConfigurationPackage.class.getName(), false);
            assert annotatedTypeMetadata != null;
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotatedTypeMetadata); // {basePackageClasses=[], basePackages=[]}
            List<String> packageNames = new ArrayList<>(Arrays.asList(attributes.getStringArray("basePackages")));
            for (Class<?> basePackageClass : attributes.getClassArray("basePackageClasses")) {
                packageNames.add(basePackageClass.getPackage().getName());
            }
            System.out.println("All default packages in the @ShuyuAutoConfigurationPackage is: ");
            packageNames.forEach(System.out::println);
            if (packageNames.isEmpty()) {
                String className = metadata.getClassName();
                String packageName = ClassUtils.getPackageName(className);
                packageNames.add(packageName);
                System.out.println("Since the @ShuyuAutoConfigurationPackage does not provide any default packages, the PackageImports feature inserts the package " + packageName + ".");
            }
            this.packageNames = Collections.unmodifiableList(packageNames);
        }

        List<String> getPackageNames() {
            return this.packageNames;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.packageNames.equals(((PackageImports) obj).packageNames);
        }

        @Override
        public int hashCode() {
            return this.packageNames.hashCode();
        }

        @Override
        public String toString() {
            return "Package Imports " + this.packageNames;
        }

    }

    static final class BasePackages {

        private final List<String> packages;

        private boolean loggedBasePackageInfo;

        BasePackages(String... names) {
            List<String> packages = new ArrayList<>();
            for (String name : names) {
                if (StringUtils.hasText(name)) {
                    packages.add(name);
                }
            }
            this.packages = packages;
        }

        List<String> get() {
            if (!this.loggedBasePackageInfo) {
                if (this.packages.isEmpty()) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("@EnableAutoConfiguration was declared on a class "
                                + "in the default package. Automatic @Repository and "
                                + "@Entity scanning is not enabled.");
                    }
                }
                else {
                    if (logger.isDebugEnabled()) {
                        String packageNames = StringUtils.collectionToCommaDelimitedString(this.packages);
                        logger.debug("@EnableAutoConfiguration was declared on a class in the package '" + packageNames
                                + "'. Automatic @Repository and @Entity scanning is enabled.");
                    }
                }
                this.loggedBasePackageInfo = true;
            }
            return this.packages;
        }

    }

    static final class BasePackagesBeanDefinition extends GenericBeanDefinition {

        private final Set<String> basePackages = new LinkedHashSet<>();

        BasePackagesBeanDefinition(String... basePackages) {
            setBeanClass(BasePackages.class);
            setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            addBasePackages(basePackages);
        }

        @Override
        public Supplier<?> getInstanceSupplier() {
            return () -> new BasePackages(StringUtils.toStringArray(this.basePackages));
        }

        private void addBasePackages(String[] additionalBasePackages) {
            this.basePackages.addAll(Arrays.asList(additionalBasePackages));
        }

    }
}
