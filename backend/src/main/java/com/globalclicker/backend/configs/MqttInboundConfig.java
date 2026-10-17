package com.globalclicker.backend.configs;

import com.globalclicker.backend.model.ClickCommand;
import com.globalclicker.backend.model.DeviceHeartbeat;
import com.globalclicker.backend.model.JoinRequest;
import com.globalclicker.backend.service.DeviceStatusService;
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
    private DeviceStatusService deviceStatusService;
    private final ObjectMapper objectMapper;

    public MqttInboundConfig(MqttService mqttService, ObjectMapper objectMapper, DeviceStatusService deviceStatusService) {
        this.mqttService = mqttService;
        this.objectMapper = objectMapper;
        this.deviceStatusService = deviceStatusService;
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
                        "global/click",   // topics to subscribe to
                        "global/device/+/heartbeat",
                        "global/join"           // topic when a new device joins.
                                                // Needed to add device to the map
                );
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
                if ("global/click".equals(topic)) {

                    ClickCommand command =
                            objectMapper.readValue(
                                    payload,
                                    ClickCommand.class
                            );

                    System.out.println(
                            "Received on "
                                    + topic
                                    + ": "
                                    + command.getType()
                    );

                    mqttService.click(
                            command.getCountry()
                    );

                    return;
                }
                else if (topic != null && topic.matches("global/device/[^/]+/heartbeat")) {
                    String[] topicParts = topic.split("/");

                    String deviceId = topicParts[2];
                    DeviceHeartbeat heartbeat = objectMapper.readValue(payload, DeviceHeartbeat.class);
                    mqttService.heartbeat(deviceId, heartbeat);

                    return;
                }
                else if ("global/join".equals(topic)) {
                    JoinRequest request = objectMapper.readValue(
                            payload, JoinRequest.class
                    );

                    deviceStatusService.addDevice(
                            request.getId(),
                            request.getCountry()
                    );

                    mqttService.registerDevice(
                            request.getCountry(),
                            request.getId()
                    );

                    System.out.println(
                            "New device joined: " + request.getCountry()
                                    + "-" + request.getId()
                    );
                    return;
                }

                System.err.println("Received message on unexpected topic: " + topic);

            } catch (Exception e) {
                System.err.println("Bad payload on " + topic + ": " + payload);
            }
        };
    }
}