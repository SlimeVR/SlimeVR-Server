#pragma once

#include <filesystem>

#include "flatbuffers/flatbuffer_builder.h"

class SolarXRConnection {
private:
#if defined(__linux__)
  int fd{-1};
#elif defined(_WIN32)
#error "TODO: actually implement this lol"
#else
#error "Unsupported platform"
#endif

  static std::filesystem::path getSocketPath();

public:
  SolarXRConnection();

  ~SolarXRConnection();

  void sendMsg(flatbuffers::FlatBufferBuilder &fbb);
};
