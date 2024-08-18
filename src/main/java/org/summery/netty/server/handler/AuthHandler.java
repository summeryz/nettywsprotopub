package org.summery.netty.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.summery.netty.server.ConnectionLocator;
import org.summery.netty.server.ConnectionRegistry;

@Slf4j
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    static final public String SSO_EMAIL = "SSO-Email";
    static final public String DEVICE_ID = "device-id";

    final private ConnectionRegistry connectionRegistry;

    public AuthHandler(@NonNull ConnectionRegistry connectionRegistry) {
        this.connectionRegistry = connectionRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        log.info("AuthHandler channelRead0 {}", msg);

        String ssoEmail = msg.headers().get(SSO_EMAIL);
        String deviceId = msg.headers().get(DEVICE_ID);
        ConnectionLocator locator = checkAuthentication(ssoEmail, deviceId);

        //鉴权失败
        if (locator == null) {
            FullHttpResponse response = buildNoAuthResponse();
            ctx.writeAndFlush(response).addListener(future -> {
                //无论操作是成功、失败还是被取消，都要处理
                log.warn("Authentication failed, close conn after flush {} {}", ssoEmail, deviceId);
                ReferenceCountUtil.release(response);
                ctx.close();
            });

            log.warn("Authentication failed {} {}", ssoEmail, deviceId);
            return;
        }

        //鉴权成功, 添加连接to registry
        connectionRegistry.addConnection(locator, ctx);

        //将在下一个handler中被release
        ctx.fireChannelRead(msg.retain());
        ctx.pipeline().remove(this);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught in AuthHandler", cause);
        //先remove
        connectionRegistry.removeConnection(ctx);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //先remove
        connectionRegistry.removeConnection(ctx);
        super.channelInactive(ctx);
    }

    private ConnectionLocator checkAuthentication(String ssoEmail, String deviceId) {
        log.warn("Authentication check {} {}", ssoEmail, deviceId);
        try {
            // Implement your authentication logic here
            // For example, check headers, tokens, etc.
            return new ConnectionLocator(1L, "aaa"); // Replace with actual authentication check
        } catch (Exception e) {
            log.warn("Authentication failed {} {}", ssoEmail, deviceId, e);
            return null;
        }
    }

    private FullHttpResponse buildNoAuthResponse() {
       return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
    }
//    private FullHttpResponse buildResponse(int code, String message) {
//        GlobalResponse globalResponse = new GlobalResponse();
//        globalResponse.setSuccess(false);
//        globalResponse.setErrorCode(code);
////        globalResponse.setData();
//        globalResponse.setStatusCode(code);
//        globalResponse.setAlertMsg(StringUtils.abbreviate(message, 100));
//        globalResponse.setErrorMsg(StringUtils.abbreviate(message, 100));
////        globalResponse.setTraceLogId();
////        globalResponse.setExtMap();
//
//        // 响应HTML
//        byte[] responseBytes = GsonUtil.toJson(globalResponse).getBytes(StandardCharsets.UTF_8);
//        int contentLength = responseBytes.length;
//
//        FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.wrappedBuffer(responseBytes));
//        res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8");
//        res.headers().set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(contentLength));
//
//        return res;
//    }
}
