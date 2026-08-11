#include "firmware/Firmware.h"
#include "config/Pins.h"
#include <Arduino.h>



void Firmware::initialize()
{
    Serial.println();
    Serial.println("------------------------------------");
    Serial.println("Firmware initialization");
    Serial.println("Device: Clicking Machine");
    Serial.println("------------------------------------");


    wifi.connect();

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
    bool pressed = digitalRead(BUTTON) == LOW;

    if (pressed) {
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

    delay(5000);
}