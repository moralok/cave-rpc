package com.moralok.rpc.server;

import com.moralok.rpc.common.protocol.CaveCodec;
import com.moralok.rpc.common.serialization.JdkSerializer;
import com.moralok.rpc.server.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
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

    private Thread thread;

    private final Map<String, Object> serviceBeanMap = new ConcurrentHashMap<>();

    public RpcServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void addService(String serviceName, Object serviceBean) {
        serviceBeanMap.put(serviceName, serviceBean);
    }

    public void start() {
        thread = new Thread(() -> {
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();

            LoggingHandler loggingHandler = new LoggingHandler();
            CaveCodec codec = new CaveCodec(new JdkSerializer());

            RpcRequestHandler rpcRequestHandler = new RpcRequestHandler(serviceBeanMap);

            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ServerChannelInitializer(loggingHandler, codec, rpcRequestHandler));
                ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                logger.error("rpc server error: {}", e.getMessage());
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
        thread.start();
    }

    public void stop() {
        logger.info("rpc server stop...");
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
