package com.globalclicker.backend.service;

import com.globalclicker.backend.model.DeviceHeartbeat;
import com.globalclicker.backend.model.MqttGateway;
import com.globalclicker.backend.websocket.WebSocketService;
import org.springframework.stereotype.Component;

@Component
public class MqttService {

    private final MqttGateway mqttGateway;
    private final DeviceStatusService deviceStatusService;
    private final WebSocketService webSocketService;

    public MqttService(MqttGateway mqttGateway, DeviceStatusService deviceStatusService,
                       WebSocketService webSocketService) {
        this.mqttGateway = mqttGateway;
        this.deviceStatusService = deviceStatusService;
        this.webSocketService = webSocketService;
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
        webSocketService.broadcastState();
    }

    public void click(String country) {

        deviceStatusService.registerClick(country);

        lightLed(country);

        webSocketService.broadcastState();
    }

}
