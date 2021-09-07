/*
 * Copyright 2019 All rights reserved.
 */

package cn.muses.springframework;

/**
 * @author jervis
 * @date 2021/9/7.
 */
public class BeanDefinition {

    private Class<?> type;

    private String scope;

    private boolean isLazy;

    public BeanDefinition() {}

    public BeanDefinition(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }

}
