#pragma once

#include <filesystem>

#if defined(_WIN32)
#define WIN32_MEAN_AND_LEAN
#include <Windows.h>
#endif

#include "flatbuffers/flatbuffer_builder.h"

class SolarXRConnection {
private:
#if defined(__linux__)
    int fd{ -1 };
#elif defined(_WIN32)
    HANDLE pipe{ INVALID_HANDLE_VALUE };
#else
#error "Unsupported platform"
#endif

    static std::filesystem::path getSocketPath();

public:
    SolarXRConnection() noexcept(false);

    ~SolarXRConnection();

    bool connected();
    void sendMsg(flatbuffers::FlatBufferBuilder &fbb);
};
