#include "firmware/Firmware.h"
#include "config/Pins.h"
#include "network/MqttManager.h"
#include <Arduino.h>


MqttManager mqtt;

void Firmware::initialize()
{
    Serial.println();
    Serial.println("------------------------------------");
    Serial.println("Firmware initialization");
    Serial.println("Device: Clicking Machine");
    Serial.println("------------------------------------");


    wifi.connect();
    mqtt.connect();

    pinMode(RED_LED, OUTPUT);
    pinMode(BLUE_LED, OUTPUT);
    pinMode(GREEN_LED, OUTPUT);

    pinMode(BUTTON, INPUT_PULLUP);

    digitalWrite(RED_LED, LOW);
    digitalWrite(BLUE_LED, LOW);
    digitalWrite(GREEN_LED, LOW);

    Serial.println("GlobalClicker device starting!");
    
}


void Firmware::update()
{
    mqtt.loop();
    static bool previousPressed = false;
    bool pressed = digitalRead(BUTTON) == LOW;

    if (pressed && !previousPressed) {
        mqtt.publishClick();


        digitalWrite(GREEN_LED, HIGH);
        digitalWrite(RED_LED, LOW);
        digitalWrite(BLUE_LED, LOW);

        Serial.println("Button pressed");
    } else {
        digitalWrite(GREEN_LED, LOW);
        digitalWrite(RED_LED, HIGH);
        digitalWrite(BLUE_LED, HIGH);

        Serial.println("Button released");
    }

    previousPressed = pressed;
    delay(500);
}