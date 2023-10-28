package com.moralok.rpc.client;

import com.moralok.rpc.client.discovery.ServiceDiscovery;
import com.moralok.rpc.client.loadbalance.LoadBalance;
import com.moralok.rpc.common.Node;
import com.moralok.rpc.common.RpcRequest;
import com.moralok.rpc.common.util.Snowflake;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ServiceInvocationHandler
 *
 * @author moralok
 */
public class ServiceInvocationHandler  implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServiceInvocationHandler.class);

    private final Class<?> serviceClass;

    private final Snowflake snowflake;

    private final ServiceDiscovery serviceDiscovery;

    private final LoadBalance loadBalance;

    public ServiceInvocationHandler(Class<?> serviceClass, Snowflake snowflake, ServiceDiscovery serviceDiscovery, LoadBalance loadBalance) {
        this.serviceClass = serviceClass;
        this.snowflake = snowflake;
        this.serviceDiscovery = serviceDiscovery;
        this.loadBalance = loadBalance;
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

        // service discovery
        List<Node> nodeList = serviceDiscovery.getNodeList(serviceClass.getName());

        // load balance
        Node node = loadBalance.select(nodeList);
        if (node == null) {
            logger.error("provider does not exist.");
            throw new RuntimeException("provider does not exist.");
        }

        Channel channel = ChannelManager.getChannelManager().getChannel(node);
        logger.debug("serviceName: {}, channel: {}", serviceClass, channel);

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
