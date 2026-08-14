package com.globalclicker.backend.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttOutboundConfig {

    @Value("${mqtt.client-id}")
    private String clientId;
    @Value("${mqtt.default-topic}")
    private String defaultTopic;

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(MqttPahoClientFactory factory) {
        MqttPahoMessageHandler handler =
                new MqttPahoMessageHandler(clientId + "-pub", factory);
        handler.setAsync(true);
        handler.setDefaultTopic(defaultTopic);
        handler.setDefaultQos(1);
        return handler;
    }
}