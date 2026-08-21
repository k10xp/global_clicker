package com.globalclicker.backend.websocket;

import com.globalclicker.backend.model.DeviceStatus;
import com.globalclicker.backend.service.DeviceStatusService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {
    private final ObjectMapper objectMapper;
    private final DeviceStatusService deviceStatusService;

    private final Map<String, WebSocketSession> sessions =
            new ConcurrentHashMap<>();

    public WebSocketService(
            ObjectMapper objectMapper,
            DeviceStatusService deviceStatusService
    ) {
        this.objectMapper = objectMapper;
        this.deviceStatusService = deviceStatusService;
    }

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);

        // Send current state immediately
        sendState(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
    }

    public void broadcastState() {
        for (WebSocketSession session : sessions.values()) {
            sendState(session);
        }
    }

    private void sendState(WebSocketSession session) {

        if (!session.isOpen()) {
            removeSession(session);
            return;
        }

        try {
            StateMessage state = createStateMessage();

            String json = objectMapper.writeValueAsString(state);

            synchronized (session) {
                session.sendMessage(new TextMessage(json));
            }

        } catch (IOException e) {
            System.err.println(
                    "Failed to send WebSocket message: "
                            + e.getMessage()
            );

            removeSession(session);
        }
    }

    private StateMessage createStateMessage() {

        Map<String, DeviceStateMessage> devices =
                new ConcurrentHashMap<>();

        for (DeviceStatus device : deviceStatusService.getDevices()) {

            devices.put(
                    device.getDeviceId(),
                    new DeviceStateMessage(
                            "online".equals(device.getStatus()),
                            device.getClicks(),
                            device.getUptime()
                    )
            );
        }

        return new StateMessage(
                "STATE",
                devices
        );
    }

    public record StateMessage(
            String type,
            Map<String, DeviceStateMessage> devices
    ) {}

    public record DeviceStateMessage(
            boolean online,
            long clicks,
            long uptime
    ) {}
}
