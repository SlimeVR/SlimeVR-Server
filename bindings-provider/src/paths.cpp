#include "paths.hpp"

namespace fs = std::filesystem;

fs::path Paths::getDataPath() {
    fs::path basePath{};

#if defined(__linux__)
    if (const char *dataHomeOverride = getenv("XDG_DATA_HOME")) {
        basePath = dataHomeOverride;
    } else {
        const char *homeDir = getenv("HOME");
        if (homeDir == nullptr)
            throw std::runtime_error("HOME is unset");

        basePath = fs::path(homeDir) / ".local" / "share";
    }
#elif defined(_WIN32)
    {
        const char *appData = getenv("APPDATA");
        if (appData == nullptr)
            throw std::runtime_error("APPDATA is unset");

        basePath = appData;
    }
#else
#error "Unsupported platform"
#endif

    return basePath / "dev.slimevr.SlimeVR";
}

fs::path Paths::getLogPath() { return Paths::getDataPath() / "logs"; }

fs::path Paths::getTempPath() noexcept {
#ifdef _WIN32
    WCHAR tmp_dir[MAX_PATH + 1];
    GetTempPathW(std::size(tmp_dir), tmp_dir);
    return tmp_dir;
#else
    if (const char *tmp_dir = getenv("TMPDIR")) {
        return tmp_dir;
    }

    return "/tmp";
#endif
}
