#pragma once

constexpr int RED_LED   = 15;
constexpr int BLUE_LED  = 18;
constexpr int GREEN_LED = 21;

#if defined(DEVICE_S3)

constexpr int BUTTON = 6;

#elif defined(DEVICE_C6)

constexpr int BUTTON = 4;

#else

#error "No device selected"

#endif