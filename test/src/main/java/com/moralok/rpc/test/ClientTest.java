package com.moralok.rpc.test;

import com.moralok.rpc.client.ChannelManager;
import com.moralok.rpc.client.ServiceProxyFactory;
import com.moralok.rpc.client.discovery.ServiceDiscovery;
import com.moralok.rpc.client.discovery.ZookeeperServiceDiscovery;
import com.moralok.rpc.client.loadbalance.LoadBalance;
import com.moralok.rpc.client.loadbalance.RandomLoadBalance;
import com.moralok.rpc.common.util.Snowflake;
import com.moralok.rpc.common.zookeeper.CuratorZookeeperClient;
import com.moralok.rpc.common.zookeeper.ZookeeperClient;
import com.moralok.rpc.test.service.HelloService;
import com.moralok.rpc.test.service.HiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientTest.class);

    public static void main(String[] args) throws IOException {
        Snowflake snowflake = new Snowflake(0, 0, 0);
        ZookeeperClient zkClient = new CuratorZookeeperClient("192.168.46.135:2181,192.168.46.135:2182,192.168.46.135:2183");
        ServiceDiscovery serviceDiscovery = new ZookeeperServiceDiscovery(zkClient);
        LoadBalance loadBalance = new RandomLoadBalance();
        ServiceProxyFactory serviceProxyFactory = new ServiceProxyFactory(snowflake, serviceDiscovery, loadBalance);

        HelloService helloService = serviceProxyFactory.createService(HelloService.class);
        HiService hiService = serviceProxyFactory.createService(HiService.class);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            executorService.submit(() -> {
                logger.debug("print: {}", helloService.sayHello("Tom " + finalI));
            });
            executorService.submit(() -> {
                logger.debug("print: {}", hiService.sayHi("Cat " + finalI));
            });
        }
        executorService.shutdown();
        System.in.read();
        ChannelManager.getChannelManager().close();
    }
}
