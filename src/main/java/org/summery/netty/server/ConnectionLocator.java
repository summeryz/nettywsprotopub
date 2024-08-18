package org.summery.netty.server;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@Data
public class ConnectionLocator {
    private Long accountId;
    private String deviceId;

    public ConnectionLocator(@NonNull Long accountId, @NonNull String deviceId) {
        this.accountId = accountId;
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionLocator that = (ConnectionLocator) o;
        return Objects.equals(accountId, that.accountId) && Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, deviceId);
    }
}
