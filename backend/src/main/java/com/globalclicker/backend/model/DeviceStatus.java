package com.globalclicker.backend.model;

import java.time.Instant;

public class DeviceStatus {
    private final String deviceId;
    private final String country;
    private String status;
    private long clicks;
    private long uptime;
    private Instant lastHeartbeat;

    public DeviceStatus(String deviceId, String country) {

        this.deviceId = deviceId;
        this.country = country;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getCountry() { return country; }

    public String getStatus() {
        return status;
    }

    public long getClicks() { return clicks; }

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

    public void incrementClicks() {
        this.clicks++;
    }

    public void markOffline() {
        this.status = "offline";
    }
}
