package com.moralok.rpc.client;

import com.moralok.rpc.common.RpcRequest;
import com.moralok.rpc.common.util.Snowflake;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceInvocationHandler  implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServiceInvocationHandler.class);

    private final Class<?> serviceClass;
    private final Snowflake snowflake;

    public ServiceInvocationHandler(Class<?> serviceClass, Snowflake snowflake) {
        this.serviceClass = serviceClass;
        this.snowflake = snowflake;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long requestId = snowflake.nextId();
        RpcRequest request = new RpcRequest();
        request.setRequestId(requestId);
        request.setServiceName(serviceClass.getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameterValues(args);
        Channel channel = ChannelManager.getChannel();
        logger.debug("channel: {}", channel);

        DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
        RpcResponseHandler.promiseMap.put(requestId, promise);
        channel.writeAndFlush(request);
        promise.await();
        if (promise.isSuccess()) {
            return promise.getNow();
        } else {
            throw new RuntimeException(promise.cause());
        }
    }
}
