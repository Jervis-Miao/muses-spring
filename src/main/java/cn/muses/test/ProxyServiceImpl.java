/*
 * Copyright 2019 All rights reserved.
 */

package cn.muses.test;

import cn.muses.springframework.annotation.Component;

/**
 * @author jervis
 * @date 2021/9/7.
 */
@Component
public class ProxyServiceImpl implements IProxyService {

    @Override
    public void doLogical() {
        System.out.println("业务逻辑");
    }
}
