package com.moralok.rpc.client;

import com.moralok.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RpcResponseHandler
 *
 * @author moralok
 */
@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(RpcResponseHandler.class);

    /**
     * requestId->promise map
     */
    public static final Map<Long, Promise<Object>> promiseMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        logger.debug("receive msg: {}", msg);
        Promise<Object> promise = promiseMap.get(msg.getRequestId());
        promiseMap.remove(msg.getRequestId());
        if (promise != null) {
            Object result = msg.getResult();
            Exception error = msg.getError();
            if (error != null) {
                promise.setFailure(error);
            } else {
                promise.setSuccess(result);
            }
        }
    }
}
