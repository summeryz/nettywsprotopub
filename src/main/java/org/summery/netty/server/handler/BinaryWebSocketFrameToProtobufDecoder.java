package org.summery.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * MessageToMessageDecoder不是线程安全
 * 不能用shareable
 */
@Slf4j
public class BinaryWebSocketFrameToProtobufDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("client@{} receive frame", ctx.channel());
        }

        //正常不应该走到这里，所以info打印
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame f = (TextWebSocketFrame)frame;
            log.info("client@{} receive text frame {}", ctx.channel(), f.text());
            return;
        }

        //protobuf数据从这里进入
        if (frame instanceof BinaryWebSocketFrame) {
            if (log.isDebugEnabled()) {
                log.debug("client@{} receive binary frame", ctx.channel());
            }

            ByteBuf buf = frame.content();
            out.add(buf);
            //自旋累加数据
            buf.retain();
            return;
        }

        if (frame instanceof PongWebSocketFrame) {
            log.debug("client@{} receive pong frame", ctx.channel());
            return;
        }

        if (frame instanceof CloseWebSocketFrame) {
            log.debug("client@{} receive close", ctx.channel());
            return;
        }
    }
}
