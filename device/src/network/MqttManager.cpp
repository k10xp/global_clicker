#include <Arduino.h>
#include "network/MqttManager.h"
#include <WiFi.h>
#include "config/MqttSecrets.h"
#include <PubSubClient.h>
#include "config/DeviceConfig.h"
#include <ArduinoJson.h>


WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

bool clickReceived = false;
char receivedCountry[3] = "";
char myDeviceId[13];

void getDeviceId(char* id) {
    uint8_t mac[6];
    esp_read_mac(mac, ESP_MAC_WIFI_STA);
    snprintf(id, 13, "%02x%02x%02x%02x%02x%02x",
            mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
}


void MqttManager::connect()
{
    Serial.println();
    Serial.println("------------------------------------");
    Serial.println("Connecting MQTT...");
    Serial.println("------------------------------------");

    mqttClient.setBufferSize(1024);

    Serial.print("MQTT server: ");
    Serial.println(MQTT_SERVER);

    Serial.print("MQTT port: ");
    Serial.println(MQTT_PORT);


    mqttClient.setServer(
        MQTT_SERVER,
        MQTT_PORT
    );


    mqttClient.setCallback(
        MqttManager::mqttCallback
    );


    int attempts = 0;

    getDeviceId(myDeviceId);

    while(!mqttClient.connected() && attempts < 10)
    {

        if(mqttClient.connect(myDeviceId, MQTT_USER, MQTT_PASS))
        {
            Serial.println(".");
            Serial.println("MQTT connected");


            mqttClient.subscribe(
                "global/command"
            );


            // after connecting to the mqqt broker,
            // an esp32 publishes to global/join to
            // let the backend know a device has joined
            // so that the backend can save this device
            // to the HashMap of devices 
            {
                JsonDocument doc;

                doc["id"] = myDeviceId;
                doc["country"] = DEVICE_COUNTRY;

                char buffer[128];

                serializeJson(doc, buffer);

                bool success = mqttClient.publish(
                    "global/join", buffer
                );

                if (success)
                {
                    Serial.println("Join request sent");
                }
                else
                {
                    Serial.println("Join request failed");
                }
            }
            


            Serial.println("Subscribed: global/command");

            return;
        }
        else
        {
            Serial.print("MQTT failed, state=");
            Serial.println(mqttClient.state());
        }


        attempts++;

        delay(1000);
    }


    Serial.println();

    if(!mqttClient.connected())
    {
        Serial.println("MQTT connection failed");
    }
}


void MqttManager::loop()
{
    mqttClient.loop();
}

void MqttManager::mqttCallback(
    char* topic,
    uint8_t* payload,
    unsigned int length
)
{
    Serial.print("MQTT message received: ");
    Serial.println(topic);

    if (strcmp(topic, "global/command") != 0)
    {
        return;
    }

    JsonDocument doc;

    DeserializationError error =
        deserializeJson(doc, payload, length);

    if (error)
    {
        Serial.print("JSON parse failed: ");
        Serial.println(error.c_str());
        return;
    }

    const char* type = doc["type"];

    if (strcmp(type, "click") != 0)
    {
        return;
    }

    const char* country = doc["country"];

    Serial.print("Click received from: ");
    Serial.println(country);

    strncpy(receivedCountry, country, 2);
    receivedCountry[2] = '\0';

    clickReceived = true;
}

void MqttManager::publishClick()
{
    if (!mqttClient.connected())
    {
        Serial.println("MQTT not connected, click not published");
        return;
    }

    JsonDocument doc;

    doc["type"] = "click";
    doc["country"] = DEVICE_COUNTRY;

    char buffer[128];

    serializeJson(doc, buffer);

    Serial.print("Publishing: ");
    Serial.println(buffer);

    bool success = mqttClient.publish(
        "global/click",
        buffer
    );

    if (success)
    {
        Serial.println("Click published");
    }
    else
    {
        Serial.println("Click publish failed");
    }
}

void MqttManager::publishHB(unsigned long uptime)
{
    if (!mqttClient.connected())
    {
        Serial.println("MQTT not connected, heartbeat not published");
        return;
    }

    JsonDocument doc;

    doc["status"] = "online";
    doc["uptime"] = uptime;

    char topicBuffer[64];
    sprintf(topicBuffer, "global/device/%s/heartbeat", myDeviceId);

    char buffer[128];

    serializeJson(doc, buffer);

    Serial.print("Publishing: ");
    Serial.println(buffer);

    bool success = mqttClient.publish(
        topicBuffer,
        buffer
    );

    if (success)
    {
        Serial.println("Heartbeat published");
    }
    else
    {
        Serial.println("Heartbeat publish failed");
    }
}

bool MqttManager::hasReceivedClick()
{
    if (!clickReceived)
    {
        return false;
    }

    clickReceived = false;

    return true;
}

const char* MqttManager::getReceivedCountry()
{
    return receivedCountry;
}