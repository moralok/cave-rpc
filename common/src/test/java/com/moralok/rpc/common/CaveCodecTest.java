package com.moralok.rpc.common;

import com.moralok.rpc.common.protocol.CaveCodec;
import com.moralok.rpc.common.serialization.JdkSerializer;
import com.moralok.rpc.common.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

class CaveCodecTest {

    private LoggingHandler loggingHandler;
    private Serializer serializer;
    private CaveCodec caveCodec;
    private RpcResponse response;

    @BeforeEach
    void setUp() {
        loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        serializer = new JdkSerializer();
        caveCodec = new CaveCodec(serializer);

        response = new RpcResponse();
        response.setRequestId(1L);
        response.setResult("Hello World");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void encode() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                loggingHandler,
                caveCodec);
        embeddedChannel.writeOutbound(response);
    }

    @Test
    void decode() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                loggingHandler,
                new LengthFieldBasedFrameDecoder(1024, 16, 4, 0, 0),
                caveCodec);
        ByteBuf buf = encode(response);
        embeddedChannel.writeInbound(buf);
    }

    private ByteBuf encode(AbstractMessage abstractMessage) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        // 4 字节的魔数
        buf.writeBytes(CaveCodec.MAGIC_NUMBER);
        // 1 字节的消息类型，标识请求还是响应
        buf.writeByte(abstractMessage.getMessageType());
        // 1 字节的版本号
        buf.writeByte(1);
        // 1 字节的序列化类型
        buf.writeByte(1);
        buf.writeByte(0xff);
        // 8 字节的请求 ID
        buf.writeLong(abstractMessage.getRequestId());
        byte[] content = serializer.serialize(abstractMessage);
        // 4 字节的序列化后的内容长度
        buf.writeInt(content.length);
        // 序列化后的内容
        buf.writeBytes(content);
        return buf;
    }
}