package com.moralok.rpc.common.protocol;

import com.moralok.rpc.common.AbstractMessage;
import com.moralok.rpc.common.RpcRequest;
import com.moralok.rpc.common.RpcResponse;
import com.moralok.rpc.common.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * Codec for custom protocol.
 *
 * @author moralok
 */
@ChannelHandler.Sharable
public class CaveCodec extends MessageToMessageCodec<ByteBuf, AbstractMessage> {

    /**
     * magic number
     */
    public static final byte[] MAGIC_NUMBER = new byte[] {2, 0, 2, 3};

    /**
     * serializer
     */
    private final Serializer serializer;

    public CaveCodec(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, AbstractMessage abstractMessage, List<Object> list) throws Exception {
        ByteBuf buf = channelHandlerContext.alloc().buffer();
        // magic number(4 bytes)
        buf.writeBytes(MAGIC_NUMBER);
        // messageType(1 byte)
        buf.writeByte(abstractMessage.getMessageType());
        // version(1 byte)
        buf.writeByte(1);
        // serializationType(1 byte)
        buf.writeByte(1);
        buf.writeByte(0xff);
        // requestId(8 bytes)
        buf.writeLong(abstractMessage.getRequestId());
        byte[] bytes = serializer.serialize(abstractMessage);
        // length(4 bytes)
        buf.writeInt(bytes.length);
        // byte data(variable part)
        buf.writeBytes(bytes);
        list.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        int magicNumber = buf.readInt();
        byte messageType = buf.readByte();
        Class<?> messageClass = messageType == 0 ? RpcRequest.class : RpcResponse.class;
        byte version = buf.readByte();
        byte serializationType = buf.readByte();
        buf.readByte();
        long requestId = buf.readLong();
        int contentLength = buf.readInt();
        byte[] content = new byte[contentLength];
        buf.readBytes(content, 0, contentLength);
        Object message = serializer.deserialize(content, messageClass);
        list.add(message);
    }
}
