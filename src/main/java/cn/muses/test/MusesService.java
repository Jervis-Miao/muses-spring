/*
 * Copyright 2019 All rights reserved.
 */

package cn.muses.test;

import cn.muses.springframework.BeanNameAware;
import cn.muses.springframework.annotation.Autowired;
import cn.muses.springframework.annotation.Component;

/**
 * @author jervis
 * @date 2021/9/7.
 */
@Component("musesService")
public class MusesService implements BeanNameAware {

    @Autowired
    private FieldService fieldService;

    private String beanName;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void test() {
        System.out.println(this.beanName);
    }
}
