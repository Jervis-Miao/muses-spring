/*
 * Copyright 2019 All rights reserved.
 */

package cn.muses.springframework.annotation;

/**
 * @author jervis
 * @date 2021/9/7.
 */
public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
