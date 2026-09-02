#pragma once

#include <filesystem>

#ifdef _WIN32
#define WIN32_MEAN_AND_LEAN
#define NOMINMAX
#include <Winsock2.h>
#include <afunix.h>
#else
#include <sys/socket.h>
#include <sys/un.h>
#endif

#include "flatbuffers/flatbuffer_builder.h"

class SolarXRConnection {
private:
#ifdef _WIN32
    using Socket = SOCKET;
    constexpr static Socket InvalidSocket = INVALID_SOCKET;
    constexpr static int SocketError = SOCKET_ERROR;
#else
    using Socket = int;
    constexpr static Socket InvalidSocket = -1;
    constexpr static int SocketError = -1;
#endif

    static inline int GetLastSocketError() {
#ifdef _WIN32
        return WSAGetLastError();
#else
        return errno;
#endif
    }

    static inline int CloseSocket(Socket fd) {
#ifdef _WIN32
        return closesocket(fd);
#else
        return close(fd);
#endif
    }

    Socket fd = InvalidSocket;

    static std::filesystem::path getSocketPath();

public:
    SolarXRConnection() noexcept(false);

    ~SolarXRConnection();

    bool connected();
    void sendMsg(flatbuffers::FlatBufferBuilder &fbb);
};
