#include "solarxr.hpp"
#include "logger.hpp"
#include "paths.hpp"

#include <stdexcept>
#include <string>
#include <system_error>

namespace fs = std::filesystem;

static std::string getenvSafe(const char *env) {
    char *v = getenv(env);
    if (v == nullptr)
        return {};

    return v;
}

fs::path SolarXRConnection::getSocketPath() {
    using namespace std::string_view_literals;

    constexpr std::string_view socketName = "SlimeVRRpc";

#define MAKE_SOCKET_PATH(PATH) \
    std::make_pair(PATH##sv, getenvSafe(PATH))

    std::array<std::pair<std::string_view, fs::path>, 5> socketPaths{
        MAKE_SOCKET_PATH("SLIMEVR_SOCKET_DIR"),
#ifndef _WIN32
        { "Steam socket path", Paths::getDataPath() },
        MAKE_SOCKET_PATH("XDG_RUNTIME_DIR"),
#endif
        { "Temp directory", Paths::getTempPath() },
    };

#undef MAKE_SOCKET_PATH

    for (auto [name, path] : socketPaths) {
        if (path.empty()) {
            Logger::get().info(
                "Skipping socket directory '{}' because it doesn't exist", name);
            continue;
        }
        fs::path socketPath = path / socketName;
        if (fs::exists(socketPath))
            return socketPath;

        Logger::get().info(
            "Skipping socket directory '{}' because socket does not exist", name);
    }

    throw std::runtime_error("Failed to find socket directory");
}

SolarXRConnection::SolarXRConnection() {
    const fs::path path = getSocketPath();
    struct sockaddr_un addr{
        .sun_family = AF_UNIX,
    };

    const auto path_str = path.u8string();
    if (path_str.size() > std::size(addr.sun_path) - 1) {
        throw std::runtime_error(std::format("Socket path ({}) is too long to fit in sun_path", path.string()));
    }
    memcpy(addr.sun_path, path_str.data(), path_str.size() + 1);

    fd = socket(AF_UNIX, SOCK_STREAM, 0);
    if (fd == InvalidSocket) {
        int e = GetLastSocketError();
        throw std::runtime_error(
            std::format("Failed to create listen socket: {}", std::error_code(e, std::system_category()).message()));
    }

    if (connect(fd, reinterpret_cast<const struct sockaddr *>(&addr), sizeof(addr)) == SocketError) {
        int e = GetLastSocketError();
        CloseSocket(fd);
        throw std::runtime_error(std::format("Failed to connect to socket {}: {}",
                                             addr.sun_path, std::error_code(e, std::system_category()).message()));
    }
}

SolarXRConnection::~SolarXRConnection() {
    if (fd != InvalidSocket)
        CloseSocket(fd);
}

bool SolarXRConnection::connected() {
    if (recv(fd, nullptr, 0, 0) == SocketError) {
        return !(errno == EBADF || errno == EINVAL);
    }

    return true;
}

void SolarXRConnection::sendMsg(flatbuffers::FlatBufferBuilder &fbb) {
    // The server expects the total size of the buffer to be written at the
    // start of the packet, including the first 4 bytes for the size
    uint32_t size = fbb.GetSize() + 4;
    // This may be wrong on mixed-endianness, but oh well...
    if constexpr (std::endian::native != std::endian::little)
        size = std::byteswap(size);

    if (send(fd, reinterpret_cast<const char *>(&size), sizeof(size), 0) == SocketError) {
        Logger::get().warning("Failed to write message size to socket: {}",
                              std::error_code(GetLastSocketError(), std::system_category()).message());
        return;
    }

    if (send(fd, reinterpret_cast<const char *>(fbb.GetBufferPointer()), fbb.GetSize(), 0) == SocketError) {
        Logger::get().warning("Failed to write message to socket: {}",
                              std::error_code(GetLastSocketError(), std::system_category()).message());
        return;
    }
}
