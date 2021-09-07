/*
 * Copyright 2019 All rights reserved.
 */

package cn.muses.springframework;

/**
 * @author jervis
 * @date 2021/9/7.
 */
public interface InitializingBean {

    void afterPropertiesSet() throws Exception;
}
