/*
 * Copyright 2019 All rights reserved.
 */

package cn.muses.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.muses.springframework.BeanPostProcessor;
import cn.muses.springframework.annotation.Component;

/**
 * @author jervis
 * @date 2021/9/7.
 */
@Component
public class ProxyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        final Class<?> beanClass = bean.getClass();
        if (IProxyService.class.isAssignableFrom(beanClass)) {
            return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getName().equals("doLogical")) {
                            System.out.println("代理逻辑");
                        }
                        return method.invoke(bean, args);
                    }
                });
        } else {
            return bean;
        }
    }
}
