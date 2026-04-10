#include "vr_utils.hpp"
#include "logger.hpp"
#include "paths.hpp"
#include "resources/resources.hpp"

#include <array>
#include <fstream>
#include <string_view>
#include <utility>

namespace fs = std::filesystem;

std::optional<std::string>
VRUtils::getStringProp(vr::TrackedDeviceIndex_t deviceIndex,
                       vr::ETrackedDeviceProperty prop) {
  vr::IVRSystem *sys = vr::VRSystem();
  vr::ETrackedPropertyError err{vr::TrackedProp_Success};

  uint32_t required_len =
      sys->GetStringTrackedDeviceProperty(deviceIndex, prop, nullptr, 0, &err);
  if (err != vr::TrackedProp_BufferTooSmall) {
    Logger::get().info(
        "Failed to get size of string property {} for device {}: {}",
        std::to_underlying(prop), deviceIndex,
        sys->GetPropErrorNameFromEnum(err));
    return std::nullopt;
  }

  std::string s(required_len - 1, '\0');
  sys->GetStringTrackedDeviceProperty(deviceIndex, prop, s.data(), required_len,
                                      &err);
  if (err != vr::TrackedProp_Success) {
    Logger::get().info("Failed to get string property {} for device {}: {}",
                       std::to_underlying(prop), deviceIndex,
                       sys->GetPropErrorNameFromEnum(err));
    return std::nullopt;
  }

  return s;
}

std::tuple<fs::path, fs::path> VRUtils::initialiseManifest() {
  fs::path manifestDir = Paths::getDataPath() / "bindings-provider";
  std::error_code ec;
  fs::create_directories(manifestDir, ec);
  if (ec) {
    throw std::runtime_error(
        std::format("Failed to create manifest folder: {}", ec.message()));
  }

  using namespace std::string_view_literals;
  // Make sure the vrmanifest and action manifest are 1st and 2nd respectively
  // in the array
  std::array requiredFiles{
      std::make_pair("manifest.vrmanifest"sv, SVR_VRAPP_MANIFEST),
      std::make_pair("action_manifest.json"sv, SVR_VRAPP_ACTION_MANIFEST),
      std::make_pair("generic.json"sv, SVR_VRAPP_GENERIC_BINDS),
      std::make_pair("knuckles.json"sv, SVR_VRAPP_KNUCKLES_BINDS),
      std::make_pair("oculus_touch.json"sv, SVR_VRAPP_OCULUS_BINDS),
      std::make_pair("vive_controller.json"sv, SVR_VRAPP_VIVE_BINDS),
  };

  for (auto &[name, contents] : requiredFiles) {
    fs::path path = manifestDir / name;
    if (!fs::exists(path)) {
      std::ofstream stream(path);
      stream << contents;
    }
  }

  return {manifestDir / requiredFiles.at(0).first,
          manifestDir / requiredFiles.at(1).first};
}
