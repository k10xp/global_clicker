#include "device/Led.h"
#include "config/Pins.h"
#include <Arduino.h>


void Led::initialize()
{
    pinMode(RED_LED, OUTPUT);
    pinMode(BLUE_LED, OUTPUT);
    pinMode(GREEN_LED, OUTPUT);

    allOff();
}

void Led::redOn()
{
    digitalWrite(RED_LED, HIGH);
}

void Led::redOff()
{
    digitalWrite(RED_LED, LOW);
}

void Led::blueOn()
{
    digitalWrite(BLUE_LED, HIGH);
}

void Led::blueOff()
{
    digitalWrite(BLUE_LED, LOW);
}

void Led::greenOn()
{
    digitalWrite(GREEN_LED, HIGH);
}

void Led::greenOff()
{
    digitalWrite(GREEN_LED, LOW);
}

void Led::allOff()
{
    redOff();
    blueOff();
    greenOff();
}