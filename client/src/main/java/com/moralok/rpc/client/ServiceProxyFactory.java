package com.moralok.rpc.client;

import com.moralok.rpc.client.discovery.ServiceDiscovery;
import com.moralok.rpc.client.loadbalance.LoadBalance;
import com.moralok.rpc.common.util.Snowflake;

import java.lang.reflect.Proxy;

/**
 * Service proxy factory which creates client stub for service.
 *
 * @author moralok
 */
public class ServiceProxyFactory {

    private final Snowflake snowflake;

    private final ServiceDiscovery serviceDiscovery;

    private final LoadBalance loadBalance;

    public ServiceProxyFactory(Snowflake snowflake, ServiceDiscovery serviceDiscovery, LoadBalance loadBalance) {
        this.snowflake = snowflake;
        this.serviceDiscovery = serviceDiscovery;
        this.loadBalance = loadBalance;
    }

    @SuppressWarnings("unchecked")
    public <T> T createService(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new ServiceInvocationHandler(interfaceClass, snowflake, serviceDiscovery, loadBalance));
    }
}
