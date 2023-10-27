package com.moralok.rpc.server.handler;

import com.moralok.rpc.common.RpcRequest;
import com.moralok.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * RpcRequestHandler
 *
 * @author moralok
 */
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    /**
     * serviceName->ServiceBean map
     */
    private final Map<String, Object> serviceBeanMap;

    public RpcRequestHandler(Map<String, Object> serviceBeanMap) {
        logger.debug("RpcRequestHandler init");
        this.serviceBeanMap = serviceBeanMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(msg.getRequestId());
        String serviceName = msg.getServiceName();
        String methodName = msg.getMethodName();
        Class<?>[] parameterTypes = msg.getParameterTypes();
        Object[] parameterValues = msg.getParameterValues();
        Object serviceBean = serviceBeanMap.get(serviceName);
        if (serviceBean == null) {
            response.setError(new RuntimeException("serviceBean not found"));
        } else {
            Class<?> serviceBeanClass = serviceBean.getClass();
            Method method = serviceBeanClass.getMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceBean, parameterValues);
            response.setResult(result);
        }
        ctx.writeAndFlush(response);
    }
}
