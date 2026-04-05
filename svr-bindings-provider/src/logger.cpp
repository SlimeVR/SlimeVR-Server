#include "logger.hpp"
#include "paths.hpp"

Logger::Logger()
    : log_stream(Paths::getLogPath() / "slimevr-bindings-provider.log",
                 std::ios::out | std::ios::app) {}

Logger &Logger::get() {
  static Logger logger;
  return logger;
}
