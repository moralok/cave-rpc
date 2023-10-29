package com.moralok.rpc.test;

import com.moralok.rpc.server.RpcServer;
import com.moralok.rpc.test.service.HelloService;
import com.moralok.rpc.test.service.HelloServiceImpl;
import com.moralok.rpc.test.service.HiService;
import com.moralok.rpc.test.service.HiServiceImpl;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        RpcServer rpcServer = new RpcServer("192.168.46.1", 18079);
        rpcServer.setRegistryAddress("192.168.46.135:2181,192.168.46.135:2182,192.168.46.135:2183");
        rpcServer.addService(HelloService.class.getName(), new HelloServiceImpl());
        rpcServer.addService(HiService.class.getName(), new HiServiceImpl());
        rpcServer.start();
        System.in.read();
        rpcServer.stop();
    }
}