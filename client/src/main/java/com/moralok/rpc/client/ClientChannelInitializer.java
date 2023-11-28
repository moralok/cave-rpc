package com.moralok.rpc.client;

import com.moralok.rpc.common.handler.ChannelClosedHandler;
import com.moralok.rpc.common.protocol.CaveCodec;
import com.moralok.rpc.common.protocol.CaveFrameDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

class ClientChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private final LoggingHandler loggingHandler;

    private final RpcResponseHandler rpcResponseHandler;

    private final ChannelClosedHandler channelClosedHandler;

    private final CaveCodec codec;

    public ClientChannelInitializer(CaveCodec codec) {
        this.loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        this.rpcResponseHandler = new RpcResponseHandler();
        this.channelClosedHandler = new ChannelClosedHandler();
        this.codec = codec;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new CaveFrameDecoder());
        // ch.pipeline().addLast(loggingHandler);
        ch.pipeline().addLast(codec);
        ch.pipeline().addLast(rpcResponseHandler);
        ch.pipeline().addLast(channelClosedHandler);
    }
}
