#pragma once
#include "network/WifiManager.h"
#include "network/MqttManager.h"


class Firmware {
    public:
        void initialize();
        void update();
    private:
        WifiManager wifi;
        MqttManager mqtt;
};