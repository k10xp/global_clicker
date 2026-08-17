#pragma once
#include <Arduino.h>

class MqttManager{
    public:
        void connect();
        void loop();
        void publishClick();
        void publishHB(unsigned long uptime);
        
        const char* getReceivedCountry();
        bool hasReceivedClick();
    
    private:
        static void mqttCallback(
            char* topic,
            uint8_t* payload,
            unsigned int length
        );
};