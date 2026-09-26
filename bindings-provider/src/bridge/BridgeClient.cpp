// SPDX-License-Identifier: MIT OR Apache-2.0
// SPDX-FileCopyrightText: (c) 2026 Eiren Rain and SlimeVR Contributors
#include "BridgeClient.hpp"
#include "BridgeTransport.hpp"

#include <system_error>

using namespace std::literals::chrono_literals;
namespace fs = std::filesystem;

void BridgeClient::CreateConnection() {
    fs::path path = GetSocketPath();

    Socket fd = socket(AF_UNIX, SOCK_STREAM, 0);
    if (fd == InvalidSocket) {
        int err = GetLastSocketError();
        throw std::system_error(err, std::system_category(), "socket() failed");
    }

    if (last_path_ != path) {
        Logger::Get().Info("Trying to connect to socket {}", path.string());
        last_path_ = path;
    }

    struct sockaddr_un addr{
        .sun_family = AF_UNIX,
    };

    const std::u8string path_str = path.u8string();
    if (path_str.size() > std::size(addr.sun_path) - 1) {
        CloseSocket(fd);
        throw std::runtime_error("Socket path too long to fit in sun_path");
    }
    memcpy(addr.sun_path, path_str.data(), path_str.size() + 1);

    int ret = connect(fd, reinterpret_cast<const struct sockaddr*>(&addr), sizeof(addr));
    if (ret == SocketError) {
        int err = GetLastSocketError();
        CloseSocket(fd);
        throw std::system_error(err, std::system_category(), "connect() failed");
    }

    Logger::Get().Info("Connected to {}", path.string());

    ret = SetNonBlocking(fd);
    if (ret == SocketError) {
        int err = GetLastSocketError();
        Logger::Get().Warning("Failed to set socket into non-blocking mode: {}", std::error_code(err, std::system_category()).message());
    }

    fd_ = fd;
}
