package com.globalclicker.backend.controller;

import com.globalclicker.backend.model.MqttGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final MqttGateway mqttGateway;

    public TestController(MqttGateway mqttGateway) {
        this.mqttGateway = mqttGateway;
    }

    @GetMapping("/test")
    public void sendDE() {
        mqttGateway.publish("global/command", "{'type': 'click', 'country': 'DE'}");
    }

}
