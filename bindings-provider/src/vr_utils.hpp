#pragma once

#include <filesystem>
#include <optional>
#include <string>
#include <tuple>

#include "openvr.h"

namespace VRUtils {
std::optional<std::string> GetStringProp(vr::TrackedDeviceIndex_t deviceIndex,
                                         vr::ETrackedDeviceProperty prop);

// Returns <app vrmanifest path, action manifest json path>
std::tuple<std::filesystem::path, std::filesystem::path> InitialiseManifests();
} // namespace VRUtils
