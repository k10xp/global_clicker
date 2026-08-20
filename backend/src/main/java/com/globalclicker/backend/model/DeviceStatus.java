package com.globalclicker.backend.model;

import java.time.Instant;

public class DeviceStatus {
    private final String deviceId;
    private String status;
    private long uptime;
    private Instant lastHeartbeat;

    public DeviceStatus(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getStatus() {
        return status;
    }

    public long getUptime() {
        return uptime;
    }

    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void update(String status, long uptime) {
        this.status = status;
        this.uptime = uptime;
        this.lastHeartbeat = Instant.now();
    }

    public void markOffline() {
        this.status = "offline";
    }
}
