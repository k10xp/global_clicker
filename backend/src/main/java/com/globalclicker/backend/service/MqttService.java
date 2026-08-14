package com.globalclicker.backend.service;

import com.globalclicker.backend.model.MqttGateway;
import org.springframework.stereotype.Component;

@Component
public class MqttService {

    private final MqttGateway mqttGateway;

    public MqttService(MqttGateway mqttGateway) {
        this.mqttGateway = mqttGateway;
    }

    public void lightLed(String country) {
        mqttGateway.publish("global/command", "{'type': 'click', 'country': '" + country + "'}");
    }

}
