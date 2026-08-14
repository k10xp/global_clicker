package com.globalclicker.backend.configs;

import com.globalclicker.backend.model.ClickCommand;
import com.globalclicker.backend.service.MqttService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class MqttInboundConfig {

    @Value("${mqtt.client-id}")
    private String clientId;

    private MqttService mqttService;
    private final ObjectMapper objectMapper;

    public MqttInboundConfig(MqttService mqttService, ObjectMapper objectMapper) {
        this.mqttService = mqttService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound(MqttPahoClientFactory factory) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        clientId + "-sub", factory,
                        "global/click");   // topics to subscribe to
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public MessageHandler handler() {
        return message -> {
            String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
            String payload = message.getPayload().toString();
            try {
                ClickCommand command = objectMapper.readValue(payload, ClickCommand.class);
                System.out.println("Received on " + topic + ": " + command.getType());
                mqttService.lightLed(command.getCountry());
            } catch (Exception e) {
                System.err.println("Bad payload on " + topic + ": " + payload);
            }
        };
    }
}