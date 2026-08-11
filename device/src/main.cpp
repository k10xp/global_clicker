#include <Arduino.h>
#include "firmware/Firmware.h"
#include "config/DeviceConfig.h"
Firmware firmware;



void setup() {
    Serial.begin(115200);

    delay(1000);

    Serial.println();
    Serial.println("====================================");
    Serial.println("Global Clicker Firmware");
    Serial.println("Version : 0.1.0");
    Serial.print("Device  : ");
    Serial.println(DEVICE_NAME);
    Serial.println("Status  : Boot successful");
    Serial.println("====================================");


    firmware.initialize();
}

void loop() {
    firmware.update();
}