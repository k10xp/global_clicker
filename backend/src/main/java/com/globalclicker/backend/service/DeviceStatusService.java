package com.globalclicker.backend.service;

import com.globalclicker.backend.model.DeviceStatus;
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
        addDevice("ESP32-NODEMCU");
        addDevice("ESP32-S3");
        //When we know our third device we add it here
    }

    private void addDevice(String deviceId) {
        DeviceStatus device = new DeviceStatus(deviceId);
        device.markOffline();
        devices.put(deviceId, device);
    }

    public void updateHeartbeat(String deviceId, String status, long uptime) {
        DeviceStatus device =
                devices.computeIfAbsent(deviceId, id -> {
                    DeviceStatus newDevice = new DeviceStatus(id);
                    newDevice.markOffline();
                    return newDevice;
                });

        device.update("online", uptime);

        System.out.println(
                "Device " + deviceId +
                        " status=online" +
                        " uptime=" + uptime
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
