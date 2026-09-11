#pragma once

#include <filesystem>

namespace Paths {
// Throws when path cannot be found
std::filesystem::path getDataPath() noexcept(false);

// Throws when path cannot be found
std::filesystem::path getLogPath() noexcept(false);

std::filesystem::path getTempPath() noexcept;
} // namespace Paths
