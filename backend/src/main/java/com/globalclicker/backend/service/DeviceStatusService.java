package com.globalclicker.backend.service;

import com.globalclicker.backend.model.DeviceStatus;
import com.globalclicker.backend.websocket.WebSocketService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceStatusService {
    private static final long OFFLINE_TIMEOUT_SECONDS = 30;
    private final Map<String, DeviceStatus> devices = new ConcurrentHashMap<>();

    public DeviceStatusService() {
        addDevice("ESP32-NODEMCU", "DE");
        addDevice("ESP32-S3", "SE");
        //When we know our third device we add it here
    }

    private void addDevice(String deviceId, String country) {
        DeviceStatus device = new DeviceStatus(deviceId, country);
        device.markOffline();
        devices.put(deviceId, device);
    }

    public void updateHeartbeat(String deviceId, String status, long uptime) {
        DeviceStatus device = devices.get(deviceId);

        if (device == null) {
            System.err.println("Unknown device heartbeat: " + deviceId);
            return;
        }

        device.update("online", uptime);

        System.out.println(
                "Device " + deviceId +
                        " status=online" +
                        " uptime=" + uptime
        );
    }

    public void registerClick(String country) {

        for (DeviceStatus device : devices.values()) {

            if (device.getCountry()
                    .equalsIgnoreCase(country)) {

                device.incrementClicks();

                System.out.println(
                        "Click registered for "
                                + country
                                + ". Total clicks: "
                                + device.getClicks()
                );

                return;
            }
        }

        System.err.println(
                "Unknown click country: "
                        + country
        );
    }

    @Scheduled(fixedRate = 10_000)
    public void checkDevices() {
        Instant now = Instant.now();

        for (DeviceStatus device : devices.values()) {

            Instant lastHeartbeat = device.getLastHeartbeat();

            if (lastHeartbeat == null) {
                continue;
            }

            long secondsSinceHeartbeat = Duration.between(lastHeartbeat, now).getSeconds();

            if (secondsSinceHeartbeat > OFFLINE_TIMEOUT_SECONDS
                    && !"offline".equals(device.getStatus())) {

                device.markOffline();

                System.out.println(
                        "Device " + device.getDeviceId() +
                                " is OFFLINE. Last heartbeat " +
                                secondsSinceHeartbeat + " seconds ago."
                );
            }
        }
    }

    public DeviceStatus getDevice(String deviceId) {
        return devices.get(deviceId);
    }

    public Collection<DeviceStatus> getDevices() {
        return devices.values();
    }
}
