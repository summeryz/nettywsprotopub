package org.summery.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyWebsocketServer {
    private final int bossGroupThreads = 1;  // 通常一个线程即可
    private final int workerGroupThreads = Runtime.getRuntime().availableProcessors() * 2;
    private final ConnectionRegistry connectionRegistry = new ConnectionRegistry();

    public void createAndStart(int port) {
        // 设置泄漏检测级别为 PARANOID (最严格)
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);

        EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupThreads);
        EventLoopGroup workGroup = new NioEventLoopGroup(workerGroupThreads);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(ServerSettings.getLogLevel()))
                    .childHandler(new ChildHandlerInitializer(connectionRegistry))
//                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_SNDBUF, 1024 * 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            //
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("server started @{}", port);
            future.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                log.info("server shutdown @{}", port);
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
                log.info("server shutdown @{} complete", port);
            });
            future.channel().close().sync();
        } catch (InterruptedException e) {
            log.error("server error @{}", port, e);
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
