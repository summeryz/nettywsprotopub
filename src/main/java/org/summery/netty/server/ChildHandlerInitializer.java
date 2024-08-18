package org.summery.netty.server;

import com.xhs.redim.common.transport.protobuf.dto.RedIMServerSideTransportProto;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.summery.netty.server.handler.AuthHandler;
import org.summery.netty.server.handler.BinaryWebSocketFrameToProtobufDecoder;
import org.summery.netty.server.handler.ProtobufToBinaryWebSocketFrameEncoder;



@Slf4j
public class ChildHandlerInitializer extends ChannelInitializer<SocketChannel> {
    final private ConnectionRegistry connectionRegistry;

    protected ChildHandlerInitializer(@NonNull ConnectionRegistry connectionRegistry) {
        this.connectionRegistry = connectionRegistry;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new LoggingHandler(ServerSettings.getLogLevel()));
        ch.pipeline().addLast(new HttpServerCodec());
        //支持参数对象解析，支持POST，设置聚合内容最大长度
        ch.pipeline().addLast(new HttpObjectAggregator(65536));
        //鉴权
        ch.pipeline().addLast(new AuthHandler(connectionRegistry));
        //支持server发送大数据流
        ch.pipeline().addLast(new ChunkedWriteHandler());
        //支持websocket压缩
        ch.pipeline().addLast(new WebSocketServerCompressionHandler());
        //支持websocket协议，设置访问路径为/ws
        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws", "PROTOBUFF", true));

        //处理req，解码带有长度字段的 Protobuf 消息帧
        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
        //处理resp，用于在发送方为 Protobuf 消息添加长度字段
        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
        //协议包解码
        ch.pipeline().addLast(new BinaryWebSocketFrameToProtobufDecoder());
        //协议包编码
        ch.pipeline().addLast(new ProtobufToBinaryWebSocketFrameEncoder());

        //https://blog.csdn.net/HSJ0170/article/details/119242690
        ch.pipeline().addLast(new ProtobufDecoder(RedIMServerSideTransportProto.RedIMServerSideTransport.getDefaultInstance()));
//        ch.pipeline().addLast(new SubReqServerHandler());
    }
}
