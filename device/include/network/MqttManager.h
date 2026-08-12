#pragma once
#include <Arduino.h>

class MqttManager{
    public:
        void connect();
        void loop();
        void publishClick();
    
    private:
        static void mqttCallback(
            char* topic,
            uint8_t* payload,
            unsigned int length
        );
};