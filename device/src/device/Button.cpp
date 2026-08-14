#include "device/Button.h"
#include "config/Pins.h"
#include <Arduino.h>

void Button::initialize()
{
    Serial.println("BUTTON IS: ");
    Serial.println(BUTTON);
    pinMode(BUTTON, INPUT_PULLUP);

    previousState = digitalRead(BUTTON) == LOW;
}

bool Button::wasClicked()
{
    bool pressed = digitalRead(BUTTON) == LOW;
    bool clicked = pressed && !previousState;
    previousState = pressed;

    return clicked;
}