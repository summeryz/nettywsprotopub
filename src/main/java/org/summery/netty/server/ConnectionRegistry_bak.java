//package org.summery.netty.server;
//
//import io.netty.channel.ChannelHandlerContext;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//
///**
// * 以accountId + deviceId为维度
// * 一个账号可以有多个设备
// */
//@Slf4j
//public class ConnectionRegistry_bak {
//    private final ConcurrentHashMap<String, CopyOnWriteArrayList<ChannelHandlerContext>> accountConnections = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, ChannelHandlerContext> deviceConnections = new ConcurrentHashMap<>();
//
//    public void addConnection(String accountId, String deviceId, ChannelHandlerContext ctx) {
//        accountConnections.computeIfAbsent(accountId, k -> new CopyOnWriteArrayList<>()).add(ctx);
////        deviceConnections.put(accountId + "-" + deviceId, ctx);
//    }
//
//    public CopyOnWriteArrayList<ChannelHandlerContext> getConnectionsByAccountId(String accountId) {
//        return accountConnections.getOrDefault(accountId, new CopyOnWriteArrayList<>());
//    }
//
//    public ChannelHandlerContext getConnectionByAccountIdAndDeviceId(String accountId, String deviceId) {
//        return deviceConnections.get(accountId + "-" + deviceId);
//    }
//
//    public void removeConnection(String accountId, String deviceId) {
//        CopyOnWriteArrayList<ChannelHandlerContext> accountList = accountConnections.get(accountId);
//        if (accountList != null) {
//            accountList.removeIf(ctx -> ctx.channel().id().asLongText().equals(deviceId));
//            if (accountList.isEmpty()) {
//                accountConnections.remove(accountId);
//            }
//        }
//        deviceConnections.remove(accountId + "-" + deviceId);
//    }
//}
