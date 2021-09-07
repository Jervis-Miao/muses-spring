/*
 * Copyright 2019 All rights reserved.
 */

package cn.muses.test;

import cn.muses.springframework.InitializingBean;
import cn.muses.springframework.annotation.Component;
import cn.muses.springframework.annotation.Scope;

/**
 * @author jervis
 * @date 2021/9/7.
 */
@Component
@Scope("prototype")
public class FieldService implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化");
    }
}
