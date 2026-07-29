#include "logger.hpp"
#include "paths.hpp"

#ifdef _WIN32
#define WIN32_MEAN_AND_LEAN
#include <Windows.h>
#endif

Logger::Logger()
    : log_stream(Paths::getLogPath() / "slimevr-bindings-provider.log",
                 std::ios::out | std::ios::app)
#ifdef _WIN32
    , should_log_to_std_streams(GetConsoleWindow() != nullptr)
#endif
{
}

Logger &Logger::get() {
    static Logger logger;
    return logger;
}
