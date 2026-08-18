#include "firmware/Firmware.h"
#include "config/Pins.h"
#include "network/MqttManager.h"
#include <Arduino.h>
#include <cstring>


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

    startTime = millis();
    lastTimeSentHB = millis();
}


void Firmware::update()
{
    mqtt.loop();

    if (button.wasClicked())
    {
        Serial.println("Button clicked");

        mqtt.publishClick();
    }

    if (mqtt.hasReceivedClick())
    {
        const char* country = mqtt.getReceivedCountry();

        Serial.print("Lighting country: ");
        Serial.println(country);

        led.allOff();

        if (strcmp(country, "SE") == 0)
        {
            led.greenOn();
        }
        else if (strcmp(country, "DE") == 0)
        {
            led.redOn();
        }
        else if (strcmp(country, "US") == 0)
        {
            led.blueOn();
        }

        ledActive = true;
        ledOffTime = millis() + 1000;
    }

    if (ledActive && millis() >= ledOffTime)
    {
        led.allOff();
        ledActive = false;

        Serial.println("LED off");
    }

    if(millis() >= lastTimeSentHB + 5000) {
        lastTimeSentHB = millis();
        uptime = (millis() - startTime)/1000;
        mqtt.publishHB(uptime);
    }

    delay(10);
}