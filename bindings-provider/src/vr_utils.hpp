#pragma once

#include <filesystem>
#include <optional>
#include <string>
#include <tuple>

#include "openvr.h"

namespace VRUtils {
std::optional<std::string> getStringProp(vr::TrackedDeviceIndex_t deviceIndex,
                                         vr::ETrackedDeviceProperty prop);

// Returns <app vrmanifest path, action manifest json path>
std::tuple<std::filesystem::path, std::filesystem::path> initialiseManifest();
} // namespace VRUtils
