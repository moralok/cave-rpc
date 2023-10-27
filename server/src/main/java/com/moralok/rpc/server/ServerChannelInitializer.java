package com.moralok.rpc.server;

import com.moralok.rpc.common.handler.ChannelClosedHandler;
import com.moralok.rpc.common.protocol.CaveCodec;
import com.moralok.rpc.common.protocol.CaveFrameDecoder;
import com.moralok.rpc.server.handler.RpcRequestHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

class ServerChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    private final LoggingHandler loggingHandler;
    private final CaveCodec codec;
    private final RpcRequestHandler rpcRequestHandler;
    private final ChannelClosedHandler channelClosedHandler = new ChannelClosedHandler();

    public ServerChannelInitializer(LoggingHandler loggingHandler, CaveCodec codec, RpcRequestHandler rpcRequestHandler) {
        this.loggingHandler = loggingHandler;
        this.codec = codec;
        this.rpcRequestHandler = rpcRequestHandler;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new CaveFrameDecoder());
        ch.pipeline().addLast(loggingHandler);
        ch.pipeline().addLast(codec);
        ch.pipeline().addLast(rpcRequestHandler);
        ch.pipeline().addLast(channelClosedHandler);
    }
}
