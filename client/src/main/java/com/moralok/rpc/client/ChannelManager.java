package com.moralok.rpc.client;

import com.moralok.rpc.common.protocol.CaveCodec;
import com.moralok.rpc.common.serialization.JdkSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Channel manager.
 */
public class ChannelManager {

    private static final Logger logger = LoggerFactory.getLogger(ChannelManager.class);

    private static volatile Channel channel = null;

    private static final Object lock = new Object();

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (lock) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    public static void close() {
        synchronized (lock) {
            if (channel == null) {
                return;
            }
            channel.close();
            channel = null;
        }
    }

    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();

        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        CaveCodec codec = new CaveCodec(new JdkSerializer());
        RpcResponseHandler rpcResponseHandler = new RpcResponseHandler();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ClientChannelInitializer(loggingHandler, codec, rpcResponseHandler));

        try {
            channel = bootstrap.connect("localhost", 18077).sync().channel();

            channel.closeFuture().addListener(future -> {
                logger.info("channel {} closed", channel);
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
