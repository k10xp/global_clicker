#pragma once
#include "network/WifiManager.h"

class Firmware {
    public:
        void initialize();
        void update();
    private:
        WifiManager wifi;
};