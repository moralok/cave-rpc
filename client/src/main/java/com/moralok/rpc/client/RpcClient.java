package com.moralok.rpc.client;

import com.moralok.rpc.common.util.Snowflake;

import java.lang.reflect.Proxy;

public class RpcClient {

    private static final Snowflake snowflake = new Snowflake(0, 0, 0);

    @SuppressWarnings("unchecked")
    public static <T> T createService(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new ServiceInvocationHandler(interfaceClass, snowflake));
    }
}
