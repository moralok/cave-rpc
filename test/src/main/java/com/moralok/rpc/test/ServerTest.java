package com.moralok.rpc.test;

import com.moralok.rpc.server.RpcServer;
import com.moralok.rpc.test.service.HelloService;
import com.moralok.rpc.test.service.HelloServiceImpl;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        RpcServer rpcServer = new RpcServer("localhost", 18077);
        rpcServer.addService(HelloService.class.getName(), new HelloServiceImpl());
        rpcServer.start();
        System.in.read();
        rpcServer.stop();
    }
}