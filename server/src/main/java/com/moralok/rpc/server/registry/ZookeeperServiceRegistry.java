package com.moralok.rpc.server.registry;

import com.moralok.rpc.common.Node;
import com.moralok.rpc.common.zookeeper.CuratorZookeeperClient;
import com.moralok.rpc.common.zookeeper.ZookeeperClient;
import com.moralok.rpc.server.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.moralok.rpc.common.CommonConstants.*;

public class ZookeeperServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    private final ZookeeperClient zkClient;

    private final RpcServer rpcServer;

    public ZookeeperServiceRegistry(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
        this.zkClient = new CuratorZookeeperClient(rpcServer.getRegistryAddress());
    }

    @Override
    public void registerService(String serviceName) {
        logger.info("register service: {}, Node: {}", serviceName, rpcServer.getNode());
        zkClient.create(toPath(serviceName, rpcServer.getNode()), true);
    }

    @Override
    public void unregisterService(String serviceName) {

    }

    @Override
    public void init() {
        logger.info("zookeeper registry init.");
        Map<String, Object> serviceBeanMap = rpcServer.getServiceBeanMap();
        for (String serviceName : serviceBeanMap.keySet()) {
            registerService(serviceName);
        }
    }

    private String toPath(String serviceKey, Node node) {
        return PATH_SEPARATOR + CAVE + PATH_SEPARATOR + serviceKey + PATH_SEPARATOR + PROVIDERS_CATEGORY + PATH_SEPARATOR + node.getHost() + ":" + node.getPort();
    }
}
