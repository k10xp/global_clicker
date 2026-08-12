#include "firmware/Firmware.h"
#include "config/Pins.h"
#include "network/MqttManager.h"
#include <Arduino.h>


void Firmware::initialize()
{
    Serial.println();
    Serial.println("------------------------------------");
    Serial.println("Firmware initialization");
    Serial.println("Device: Clicking Machine");
    Serial.println("------------------------------------");

    wifi.connect();
    mqtt.connect();

    led.initialize();
    button.initialize();

    Serial.println("GlobalClicker device starting!");
}


void Firmware::update()
{
    mqtt.loop();

    if (button.wasClicked())
    {
        Serial.println("Button clicked");
        mqtt.publishClick();
    }

    delay(100);
}