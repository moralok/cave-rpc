package com.moralok.rpc.client;

import com.moralok.rpc.common.handler.ChannelClosedHandler;
import com.moralok.rpc.common.protocol.CaveCodec;
import com.moralok.rpc.common.protocol.CaveFrameDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

class ClientChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    private final LoggingHandler loggingHandler;
    private final CaveCodec codec;
    private final RpcResponseHandler rpcResponseHandler;
    private final ChannelClosedHandler channelClosedHandler = new ChannelClosedHandler();

    public ClientChannelInitializer(LoggingHandler loggingHandler, CaveCodec codec, RpcResponseHandler rpcResponseHandler) {
        this.loggingHandler = loggingHandler;
        this.codec = codec;
        this.rpcResponseHandler = rpcResponseHandler;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new CaveFrameDecoder());
        ch.pipeline().addLast(loggingHandler);
        ch.pipeline().addLast(codec);
        ch.pipeline().addLast(rpcResponseHandler);
        ch.pipeline().addLast(channelClosedHandler);
    }
}
