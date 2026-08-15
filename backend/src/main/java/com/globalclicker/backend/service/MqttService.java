package com.globalclicker.backend.service;

import com.globalclicker.backend.model.DeviceHeartbeat;
import com.globalclicker.backend.model.MqttGateway;
import org.springframework.stereotype.Component;

@Component
public class MqttService {

    private final MqttGateway mqttGateway;
    private final DeviceStatusService deviceStatusService;

    public MqttService(MqttGateway mqttGateway, DeviceStatusService deviceStatusService) {
        this.mqttGateway = mqttGateway;
        this.deviceStatusService = deviceStatusService;
    }

    public void lightLed(String country) {
        mqttGateway.publish("global/command", "{'type': 'click', 'country': '" + country + "'}");
    }

    public void heartbeat(String deviceId, DeviceHeartbeat heartbeat) {

        deviceStatusService.updateHeartbeat(
                deviceId,
                heartbeat.getStatus(),
                heartbeat.getUptime()
        );
    }

}
