package com.globalclicker.backend.websocket;

import com.globalclicker.backend.model.ClickCommand;
import com.globalclicker.backend.service.MqttService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Component
public class DeviceWebSocketHandler extends TextWebSocketHandler {
    private final WebSocketService webSocketService;
    private final MqttService mqttService;
    private final ObjectMapper objectMapper;

    public DeviceWebSocketHandler(
            WebSocketService webSocketService,
            MqttService mqttService,
            ObjectMapper objectMapper
    ) {
        this.webSocketService = webSocketService;
        this.mqttService = mqttService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(
            WebSocketSession session
    ) {

        System.out.println(
                "WebSocket connected: "
                        + session.getId()
        );

        webSocketService.addSession(session);
    }

    @Override
    public void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ) {

        String payload = message.getPayload();

        System.out.println(
                "WebSocket received: " + payload
        );

        try {

            ClickCommand command =
                    objectMapper.readValue(
                            payload,
                            ClickCommand.class
                    );

            if ("CLICK".equalsIgnoreCase(command.getType())) {

                mqttService.click(
                        command.getCountry()
                );

            } else {

                System.err.println(
                        "Unknown WebSocket message type: "
                                + command.getType()
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "Invalid WebSocket message: "
                            + payload
            );

            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(
            WebSocketSession session,
            Throwable exception
    ) {

        System.err.println(
                "WebSocket error: "
                        + exception.getMessage()
        );

        webSocketService.removeSession(session);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus closeStatus
    ) {

        System.out.println(
                "WebSocket disconnected: "
                        + session.getId()
        );

        webSocketService.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
