#pragma once

#include <filesystem>

namespace Paths {
// Throws when path cannot be found
std::filesystem::path GetDataPath() noexcept(false);

// Throws when path cannot be found
std::filesystem::path GetLogPath() noexcept(false);

std::filesystem::path GetTempPath() noexcept;
} // namespace Paths
