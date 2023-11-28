package com.moralok.rpc.client;

import com.moralok.rpc.common.Node;
import com.moralok.rpc.common.protocol.CaveCodec;
import com.moralok.rpc.common.serialization.JdkSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Channel manager.
 *
 * @author moralok
 */
public class ChannelManager {

    private static final Logger logger = LoggerFactory.getLogger(ChannelManager.class);

    private final Object lock = new Object();

    private final Map<Node, Channel> channelMap = new ConcurrentHashMap<>();

    private static final ChannelManager channelManager = new ChannelManager();

    private ChannelManager() {
    }

    public Channel getChannel(Node node) {
        Channel channel = channelMap.get(node);
        if (channel != null) {
            return channel;
        }
        synchronized (lock) {
            channel = channelMap.get(node);
            if (channel != null) {
                return channel;
            }
            channel = initChannel(node);
            channelMap.put(node, channel);
            return channel;
        }
    }

    public void close() {
        synchronized (lock) {
            Iterator<Map.Entry<Node, Channel>> iterator = channelMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Node, Channel> next = iterator.next();
                try {
                    next.getValue().close().sync();
                } catch (InterruptedException e) {
                    logger.error("error occurred when close Channel: {}", next.getKey());
                }
                iterator.remove();
            }
        }
    }

    private Channel initChannel(Node node) {
        logger.info("create channel for node: {}", node);
        NioEventLoopGroup group = new NioEventLoopGroup();

        CaveCodec codec = new CaveCodec(new JdkSerializer());

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ClientChannelInitializer(codec));

        try {
            Channel channel = bootstrap.connect(node.getHost(), node.getPort()).sync().channel();

            channel.closeFuture().addListener(future -> {
                logger.info("channel {} closed", channel);
                group.shutdownGracefully();
            });
            return channel;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ChannelManager getChannelManager() {
        return channelManager;
    }
}
