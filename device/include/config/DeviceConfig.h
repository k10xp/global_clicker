#pragma once

#if defined(DEVICE_S3)

constexpr const char* DEVICE_NAME = "ESP32-S3";

#elif defined(DEVICE_C6)

constexpr const char* DEVICE_NAME = "ESP32-C6";

#else

constexpr const char* DEVICE_NAME = "UNKNOWN";

#endif