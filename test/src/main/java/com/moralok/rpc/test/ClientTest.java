package com.moralok.rpc.test;

import com.moralok.rpc.client.ChannelManager;
import com.moralok.rpc.client.RpcClient;
import com.moralok.rpc.test.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientTest.class);

    public static void main(String[] args) throws IOException {
        HelloService service = RpcClient.createService(HelloService.class);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorService.submit(() -> {
                logger.debug("print: {}", service.sayHello("Tom " + finalI));
            });
        }
        executorService.shutdown();
        System.in.read();
        ChannelManager.close();
    }
}
