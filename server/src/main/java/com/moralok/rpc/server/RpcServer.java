package com.moralok.rpc.server;

import com.moralok.rpc.common.Node;
import com.moralok.rpc.common.protocol.CaveCodec;
import com.moralok.rpc.common.serialization.JdkSerializer;
import com.moralok.rpc.server.handler.RpcRequestHandler;
import com.moralok.rpc.server.registry.ServiceRegistry;
import com.moralok.rpc.server.registry.ZookeeperServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RpcServer
 *
 * @author moralok
 */
public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private final String host;

    private final int port;

    private final Node node;

    private Thread thread;

    private String registryAddress;

    private ServiceRegistry serviceRegistry;

    private final Map<String, Object> serviceBeanMap = new ConcurrentHashMap<>();

    public RpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        node = new Node(host, port);
    }

    public void addService(String serviceName, Object serviceBean) {
        serviceBeanMap.put(serviceName, serviceBean);
    }

    public void start() {
        thread = new Thread(() -> {
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();

            CaveCodec codec = new CaveCodec(new JdkSerializer());
            RpcRequestHandler rpcRequestHandler = new RpcRequestHandler(serviceBeanMap);

            try {
                // start rpc server
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ServerChannelInitializer(codec, rpcRequestHandler));
                ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();

                // init service registry
                serviceRegistry = new ZookeeperServiceRegistry(this);
                serviceRegistry.init();

                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("rpc server stopped", e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }, "nio-start");
        thread.start();
    }

    public void stop() {
        logger.info("rpc server stop...");
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    public Node getNode() {
        return node;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public Map<String, Object> getServiceBeanMap() {
        return serviceBeanMap;
    }
}
