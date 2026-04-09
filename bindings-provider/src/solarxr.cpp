#include "solarxr.hpp"
#include "logger.hpp"

#include <stdexcept>

#if defined(__linux__)
#include <sys/socket.h>
#include <sys/un.h>
#elif defined(_WIN32)
#include <Windows.h>
#else
#error "Unsupported platform"
#endif

namespace fs = std::filesystem;

fs::path SolarXRConnection::getSocketPath() {
  using namespace std::string_view_literals;
#if defined(__linux__)
#define MAKE_SOCKET_PATH(PATH)                                                 \
  std::make_pair(PATH##sv, static_cast<const char *>(getenv(PATH)))

  constexpr std::string_view socketName = "SlimeVRRpc";
  std::array<std::pair<std::string_view, const char *>, 4> socketPaths{
      MAKE_SOCKET_PATH("SLIMEVR_SOCKET_DIR"),
      MAKE_SOCKET_PATH("XDG_RUNTIME_DIR"),
      MAKE_SOCKET_PATH("TMPDIR"),
      {"/tmp", "/tmp"}};

#undef MAKE_SOCKET_PATH

  for (auto [name, path] : socketPaths) {
    if (path == nullptr) {
      Logger::get().info(
          "Skipping socket directory {} because it doesn't exist", name);
      continue;
    }
    fs::path socketPath = fs::path(path) / socketName;
    if (fs::exists(socketPath))
      return socketPath;

    Logger::get().info(
        "Skipping socket directory {} because socket does not exist", name);
  }
#elif defined(_WIN32)
  throw std::logic_error(
      "SolarXRConnection::getSocketPath should not be called on Windows");
#else
#error "Unsupported platform"
#endif

  throw std::runtime_error("Failed to find socket directory");
}

SolarXRConnection::SolarXRConnection() {
#if defined(__linux__)
  sockaddr_un addr{};
  addr.sun_family = AF_UNIX;
  strcpy(addr.sun_path, getSocketPath().string().data());

  fd = socket(AF_UNIX, SOCK_STREAM, 0);
  if (fd == -1) {
    Logger::get().error("Failed to create listen socket: {}", strerror(errno));
    throw std::runtime_error("Failed to create socket");
  }

  if (connect(fd, reinterpret_cast<sockaddr *>(&addr), sizeof(addr)) == -1) {
    Logger::get().error("Failed to connect to socket {}: {}", addr.sun_path,
                        strerror(errno));
  }

#elif defined(_WIN32)
  const char *pipeName = R"(\\.\pipe\SlimeVRRpc)";
  if (!WaitNamedPipe(pipeName, 5000)) {
    throw std::runtime_error("Timed out waiting for pipe");
  }

  pipe = CreateFile(pipeName, GENERIC_READ | GENERIC_WRITE, 0, nullptr,
                    OPEN_EXISTING, 0, nullptr);
  if (pipe == INVALID_HANDLE_VALUE) {
    throw std::runtime_error(
        std::format("Unable to open pipe (error={})", GetLastError()));
  }
#else
#error "Unsupported platform"
#endif
}

SolarXRConnection::~SolarXRConnection() {
#if defined(__linux__)
  if (fd != -1)
    close(fd);
#elif defined(_WIN32)
  if (pipe != INVALID_HANDLE_VALUE)
    CloseHandle(pipe);
#else
#error "Unsupported platform"
#endif
}

void SolarXRConnection::sendMsg(flatbuffers::FlatBufferBuilder &fbb) {
  // The server expects the total size of the buffer to be written at the
  // start of the packet, including the first 4 bytes for the size
  int size = fbb.GetSize() + 4;
  // This may be wrong on mixed-endianness, but oh well...
  if constexpr (std::endian::native != std::endian::little)
    size = std::byteswap(size);

#if defined(__linux__)
  if (write(fd, &size, sizeof(size)) == -1) {
    Logger::get().warning("Failed to write message size to socket: {}",
                          strerror(errno));
    return;
  }

  if (write(fd, fbb.GetBufferPointer(), fbb.GetSize()) == -1) {
    Logger::get().warning("Failed to write message to socket: {}",
                          strerror(errno));
    return;
  }
#elif defined(_WIN32)
  uint8_t tempBuf[2048];
  if (fbb.GetSize() > 2048 - 4) {
    throw std::runtime_error("Message is too big for temporary buffer");
  }

  *reinterpret_cast<int *>(&tempBuf[0]) = size;
  memcpy(&tempBuf[4], fbb.GetBufferPointer(), fbb.GetSize());
  if (!WriteFile(pipe, tempBuf, fbb.GetSize() + 4, nullptr, nullptr)) {
    Logger::get().warning("Failed to write message to pipe (error={})",
                          GetLastError());
    return;
  }
#else
#error "Unsupported platform"
#endif
}
