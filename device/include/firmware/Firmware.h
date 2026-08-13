#pragma once
#include "network/WifiManager.h"
#include "network/MqttManager.h"
#include "device/Led.h"
#include "device/Button.h"


class Firmware {
    public:
        void initialize();
        void update();
    private:
        WifiManager wifi;
        MqttManager mqtt;
        Led led;
        Button button;

        unsigned long ledOffTime = 0;
        bool ledActive = false;
};