package org.summery.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 以accountId + deviceId为维度
 * 一个账号可以有多个设备
 */
@Slf4j
public class ConnectionRegistry {
    static final private AttributeKey<ConnectionLocator> CONNECTION_LOCATOR_KEY = AttributeKey.valueOf("ConnectionLocator");

    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<ChannelHandlerContext>> accountConnections = new ConcurrentHashMap<>();

    public void addConnection(@NonNull ConnectionLocator locator, @NonNull  ChannelHandlerContext ctx) {
        log.info("ConnectionRegistry|addConnection {}", locator);

        ctx.channel().attr(CONNECTION_LOCATOR_KEY).set(locator);
        accountConnections.computeIfAbsent(locator.getAccountId(), k -> new CopyOnWriteArrayList<>()).add(ctx);
    }

    public CopyOnWriteArrayList<ChannelHandlerContext> getConnectionByAccount(Long accountId) {
        return accountConnections.getOrDefault(accountId, new CopyOnWriteArrayList<>());
    }

    public void removeConnection(@NonNull ChannelHandlerContext ctx) {
        ConnectionLocator locator = ctx.channel().attr(CONNECTION_LOCATOR_KEY).get();
        log.info("ConnectionRegistry|removeConnection {}", locator);

        accountConnections.computeIfPresent(locator.getAccountId(), (key, accountList) -> {
            accountList.removeIf(e -> e.channel().attr(ConnectionRegistry.CONNECTION_LOCATOR_KEY).get().getDeviceId().equals(locator.getDeviceId()));
            return accountList.isEmpty() ? null : accountList;
        });
    }
}
