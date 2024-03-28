package com.kirisaki.kirisakirpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂(用来创造代理对象)
 */
public class ServiceProxyFactory {
    private ServiceProxyFactory() {

    }

    public static <T> T getProxy(Class<T> serverClass) {
        return (T) Proxy.newProxyInstance(serverClass.getClassLoader(), new Class[]{serverClass}, new ServiceProxy());
    }
}
