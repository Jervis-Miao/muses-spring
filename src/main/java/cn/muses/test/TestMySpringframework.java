/*
 * Copyright 2019 All rights reserved.
 */

package cn.muses.test;

import cn.muses.springframework.MusesApplicationContext;

/**
 * @author jervis
 * @date 2021/9/7.
 */
public class TestMySpringframework {

    public static void main(String[] args) {
        final MusesApplicationContext applicationContext = new MusesApplicationContext(AppConfig.class);
        IProxyService musesService = applicationContext.getBean("proxyServiceImpl");
        musesService.doLogical();
    }
}
