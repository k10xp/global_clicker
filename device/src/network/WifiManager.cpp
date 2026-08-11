#include "network/WifiManager.h"
#include <Arduino.h>
#include <WiFi.h>
#include "config/WifiSecrets.h"

void WifiManager::connect()
{
    Serial.println();
    Serial.println("------------------------------------");
    Serial.println("Connecting WiFi...");
    Serial.println("------------------------------------");


    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);


    int attempts = 0;

    while(WiFi.status() != WL_CONNECTED && attempts < 20)
    {
        delay(500);

        Serial.print(".");

        attempts++;
    }


    Serial.println();


    if(WiFi.status() == WL_CONNECTED)
    {
        Serial.println("WiFi connected");

        Serial.print("IP address: ");
        Serial.println(WiFi.localIP());
    }
    else
    {
        Serial.println("WiFi connection failed");
    }
}


bool WifiManager::isConnected()
{
    return WiFi.status() == WL_CONNECTED;
}