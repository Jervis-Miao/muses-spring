/*
 * Copyright 2019 All rights reserved.
 */

package cn.muses.springframework;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.muses.springframework.annotation.Autowired;
import cn.muses.springframework.annotation.Component;
import cn.muses.springframework.annotation.ComponentScan;
import cn.muses.springframework.annotation.Lazy;
import cn.muses.springframework.annotation.Scope;

/**
 * @author jervis
 * @date 2021/9/7.
 */
public class MusesApplicationContext {

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>(256);
    private Map<String, Object> singletonObjects = new HashMap<>(256);
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(128);

    public MusesApplicationContext(Class<?> configClass) {
        this.scan(configClass);
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            final String beanName = entry.getKey();
            final BeanDefinition beanDefinition = entry.getValue();
            if ("singleton".equals(beanDefinition.getScope()) && !beanDefinition.isLazy()) {
                this.singletonObjects.put(beanName, this.createBean(beanName, beanDefinition));
            }
        }
    }

    private void scan(Class<?> configClass) {
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            final ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
            final ClassLoader appClassLoader = this.getClass().getClassLoader();
            final String rootPath = appClassLoader.getResource("").getPath().replace("/", "\\").substring(1);
            final String packagePath = appClassLoader.getResource(componentScan.value().replace(".", "/")).getPath();
            final File packageFile = new File(packagePath);
            if (packageFile.isDirectory()) {
                for (File file : packageFile.listFiles()) {
                    final String classPath = file.getAbsolutePath().replace(rootPath, "").replace("\\", ".")
                        .replace(".class", "");

                    try {
                        final Class<?> clazz = appClassLoader.loadClass(classPath);

                        if (clazz.isAnnotationPresent(Component.class)) {

                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                beanPostProcessors.add((BeanPostProcessor)clazz.getConstructor().newInstance());
                                continue;
                            }

                            final BeanDefinition beanDefinition = new BeanDefinition(clazz);

                            if (clazz.isAnnotationPresent(Lazy.class)) {
                                beanDefinition.setLazy(true);
                            } else {
                                beanDefinition.setLazy(false);
                            }

                            if (clazz.isAnnotationPresent(Scope.class)) {
                                beanDefinition.setScope(clazz.getAnnotation(Scope.class).value());
                            } else {
                                beanDefinition.setScope("singleton");
                            }

                            final Component annotation = clazz.getAnnotation(Component.class);
                            String beanName;
                            if (null == (beanName = annotation.value()) || "".equals(beanName)) {
                                beanName = Introspector.decapitalize(clazz.getSimpleName());
                            }

                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object createBean(String beanName, BeanDefinition beanDefinition) {
        try {
            final Class<?> beanType = beanDefinition.getType();

            // 推断构造方法
            Object bean = beanType.getConstructor().newInstance();

            // 填充属性（循环依赖的AOP）
            for (Field field : beanType.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    field.set(bean, this.getBean(field.getName()));
                }
            }

            // 处理aware回调
            if ((bean instanceof BeanNameAware)) {
                ((BeanNameAware)bean).setBeanName(beanName);
            }

            // 初始化前
            for (BeanPostProcessor processor : beanPostProcessors) {
                bean = processor.postProcessBeforeInitialization(bean, beanName);
            }

            // 初始化
            if (bean instanceof InitializingBean) {
                ((InitializingBean)bean).afterPropertiesSet();
            }

            // 切面、事务、初始化后
            for (BeanPostProcessor processor : beanPostProcessors) {
                bean = processor.postProcessAfterInitialization(bean, beanName);
            }

            return bean;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> T getBean(String beanName) {
        BeanDefinition beanDefinition;
        if (null == (beanDefinition = this.beanDefinitionMap.get(beanName))) {
            throw new NullPointerException("Cannot find bean by name: " + beanName);
        }

        if ("singleton".equals(beanDefinition.getScope())) {
            Object bean;
            if (null == (bean = this.singletonObjects.get(beanName))) {
                bean = this.createBean(beanName, beanDefinition);
                this.singletonObjects.put(beanName, bean);
            }

            return (T) bean;
        } else {
            return (T) this.createBean(beanName, beanDefinition);
        }
    }
}
