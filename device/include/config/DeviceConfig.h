#pragma once

#if defined(DEVICE_S3)

constexpr const char* DEVICE_NAME = "ESP32-S3";
constexpr const char* DEVICE_ID = "esp32-sweden";
constexpr const char* DEVICE_COUNTRY = "SE";

#elif defined(DEVICE_C6)

constexpr const char* DEVICE_NAME = "ESP32-C6";
constexpr const char* DEVICE_ID = "esp32-usa";
constexpr const char* DEVICE_COUNTRY = "US";

#else

constexpr const char* DEVICE_NAME = "UNKNOWN";

#endif