#include <Arduino.h>

const int RED_LED = 15;
const int BLUE_LED = 18;
const int GREEN_LED = 21;
const int BUTTON = 6;

void setup() {
    Serial.begin(115200);

    pinMode(RED_LED, OUTPUT);
    pinMode(BLUE_LED, OUTPUT);
    pinMode(GREEN_LED, OUTPUT);

    pinMode(BUTTON, INPUT_PULLUP);

    digitalWrite(RED_LED, LOW);
    digitalWrite(BLUE_LED, LOW);
    digitalWrite(GREEN_LED, LOW);

    Serial.println("GlobalClicker device starting!");
}

void loop() {
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

    delay(100);
}