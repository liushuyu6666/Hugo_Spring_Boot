- [Overview](#overview)
- [Packages](#packages)
  - [`AnnotatedTypeMetadata` Interface](#annotatedtypemetadata-interface)
  - [`AnnotationMetadata` Interface](#annotationmetadata-interface)
  - [`AnnotationAttributes` Interface](#annotationattributes-interface)
- [Rules](#rules)



# Overview
In the context of annotations, there are several interfaces responsible for extracting information from them. These interfaces are provided by `springframework`, not `springframework:boot`.

# Packages
1. `AnnotatedTypeMetadata` serves as the parent interface for `AnnotatedTypeMetadata`.
2. `AnnotationAttributes` functions as an auxiliary interface specifically designed to assist with annotations.

// TODO: maybe need a picture here if there are more interfaces.

## `AnnotatedTypeMetadata` Interface
1. `getAnnotationAttributes`: This method attempts to retrieve the attributes of the given annotation. In Java, annotation attributes are represented as methods within the annotation. To obtain a key-value pair structure, the method typically works in conjunction with `AnnotationAttributes.fromMap()`. For example, if we have a custom `HugoClass` annotation:
    ```java
        public @interface ShuyuAutoConfigurationPackage {
            String[] basePackages() default {"hello", "Hugo"};

            Class<?>[] basePackageClasses() default {};
        }
    ```
    Then we use,
    ```java
        Map<String, Object> annotatedTypeMetadata = metadata.getAnnotationAttributes(HugoClass.class.getName(), false);
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotatedTypeMetadata); // {basePackageClasses=[], basePackages=[hello, Hugo]}
    ```


## `AnnotationMetadata` Interface
`getClassName`: This method, inherited from the interface `org.springframework.core.type.ClassMetadata`, retrieves the name of the underlying class, specifically `com.liushuyu.user.MyApplication`, within our project.

## `AnnotationAttributes` Interface
This interface serves as a utility for managing data structures related to annotation attributes. Specifically, it provides the following method:
1. `fromMap()`: Converts a `Map<String, Object>` into a key-value paired structure. For further examples and usage, refer to the `getAnnotationAttributes` method.


# Rules
1. Annotation's name: the annotation `@RequestMapping` has the name `org.springframework.web.bind.annotation.RequestMapping`.
2. Annotated type: The "annotated type" of `@RequestMapping` depends on where the annotation is used.
    1. When `@RequestMapping` is applied at the class level,
        ```java
            @Controller
            @RequestMapping("/users")
            public class UserController {
                // ...
            }
        ```
       The annotated type is `UserController`.
    2. When `@RequestMapping` is applied at the method level,
        ```java
            @Controller
            @RequestMapping("/users")
            public class UserController {
 
                @RequestMapping("/list")
                public String userList() {
                    // ...
                }
 
                @RequestMapping("/create")
                public String createUser() {
                    // ...
                }
            }
        ```
        In this case, the "annotated type" refers to the `userList()` and `createUser()` methods, which are annotated with `@RequestMapping` at the method level.
3. Annotation attributes: In the annotation definition, we use methods to represent attributes (a.k.a input parameter). For example:
    ```java
        @Conditional(ShuyuCondition.class)
        public @interface ShuyuConditionalOnClass {
            String value();
        }
    ```
    Here the `ShuyuConditionalOnClass` will take a string variable as the input parameter. So, when we are using it we need to use it in this way:
    ```java
    @ShuyuConditionalOnClass("org.apache.catalina.startup.Tomcat")
    public class MyConditionalBean {
        // Bean definition here
    }
    ```
4. When an annotation comprises multiple sub-annotations, it can be considered a combination of these sub-annotations. This implies that when the program encounters such an annotation, it will internally process and execute each of the sub-annotations contained within it. For example
    ```java
        @Target({ ElementType.TYPE, ElementType.METHOD })
        @Retention(RetentionPolicy.RUNTIME)
        @Conditional(ShuyuCondition.class)
        public @interface ShuyuConditionalOnClass {
            String value();
        }
    ```
    Introducing `@ShuyuConditionalOnClass`, an annotation with multiple sub-annotations. When used in the code as shown below:
    ```java
        @ShuyuConditionalOnClass("org.eclipse.jetty.server.Server")
        public JettyWebServer jettyWebServer() {
            return new JettyWebServer();
        }
    ```
    Consider mentally substituting `@ShuyuConditionalOnClass` with the following three annotations: `@Target`, `@Retention`, and `@Conditional`."