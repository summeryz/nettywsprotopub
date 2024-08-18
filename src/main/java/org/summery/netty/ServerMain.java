package org.summery.netty;

import org.summery.netty.server.NettyWebsocketServer;

public class ServerMain {
    public static void main(String[] args) {
        System.out.println("server main started");

        NettyWebsocketServer server = new NettyWebsocketServer();
        server.createAndStart(8188);
    }
}
