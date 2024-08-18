package org.summery.netty.server.handler;

import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * MessageToMessageEncoder不是线程安全的，因此不能shareable
 * ProtobufToByteEncoder使用了wrappedBuffer，效率较低
 * 但不适合直接使用pooledBuffer，因为pooledBuffer的内存释放需要手动调用release，否则会内存泄漏
 * 
 * 这里的encode方法将MessageLiteOrBuilder对象转换为BinaryWebSocketFrame对象，只是逻辑上的兜底，上游如果主动将protobuf对象转换成bytebuf，那么这里就会跳过
 * 建议在上游逻辑中，主动转换protobuf对象
 */
@Slf4j
public class ProtobufToBinaryWebSocketFrameEncoder extends ProtobufEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
        super.encode(ctx, msg, out);
        if (log.isDebugEnabled()) {
            log.debug("server send protobuf message {} @{}", msg.getClass().getCanonicalName(), ctx.channel());
        }
    }
}
