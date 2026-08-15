package com.globalclicker.backend.service;

import com.globalclicker.backend.model.DeviceStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceStatusService {
    private final Map<String, DeviceStatus> devices = new ConcurrentHashMap<>();

    public void updateHeartbeat(String deviceId, String status, long uptime) {
        DeviceStatus device = devices.computeIfAbsent(deviceId, DeviceStatus::new);
        device.update(status, uptime);

        System.out.println(
                "Device " + deviceId +
                        " status=" + status +
                        " uptime=" + uptime
        );
    }

    public DeviceStatus getDevice(String deviceId) {
        return devices.get(deviceId);
    }

    public Collection<DeviceStatus> getDevices() {
        return devices.values();
    }
}
