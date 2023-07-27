# Overview
These interfaces should be sourced from the `springframework` package rather than the `springframework:boot` package.

# Packages

## `BeanDefinitionRegistry`
`BeanDefinitionRegistry` is a key component in Spring Framework responsible for managing bean definitions.

It acts as a centralized repository where bean definitions are stored and can be accessed for further processing during the application's runtime. By offering methods for querying and registering bean definitions, it plays a crucial role in the dynamic configuration and lifecycle management of beans within the Spring container.

1. `containsBeanDefinition`: This method allows checking whether a specific bean definition exists within the registry by providing the bean's name.
2. `registerBeanDefinition`: Used for registering a new bean definition in the registry. It enables the addition of new beans programmatically.