package com.moralok.rpc.common.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ChannelClosedHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ChannelClosedHandler.class);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel {} inactive", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("channel {} closed with exception {}", ctx.channel(), cause.getCause());
    }
}
