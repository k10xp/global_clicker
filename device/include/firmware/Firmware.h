#pragma once
#include "network/WifiManager.h"
#include "network/MqttManager.h"
#include "device/Led.h"
#include "device/Button.h"


class Firmware {
    public:
        void initialize();
        void update();
        void sendHeartBeat();
    private:
        WifiManager wifi;
        MqttManager mqtt;
        Led led;
        Button button;

        unsigned long ledOffTime = 0;
        bool ledActive = false;
        unsigned long uptime = 0;
        unsigned long startTime;
        unsigned long lastTimeSentHB;
};