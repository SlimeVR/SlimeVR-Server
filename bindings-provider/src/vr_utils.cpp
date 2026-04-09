#include "vr_utils.hpp"
#include "logger.hpp"
#include "paths.hpp"
#include "resources/resources.hpp"

#include <fstream>
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

  fs::path appManifestPath = manifestDir / "manifest.vrmanifest";
  if (!fs::exists(appManifestPath)) {
    std::ofstream appManifestStream(appManifestPath);
    appManifestStream << SVR_VRAPP_MANIFEST;
  }

  fs::path actionManifestPath = manifestDir / "action_manifest.json";
  if (!fs::exists(actionManifestPath)) {
    std::ofstream actionManifestStream(actionManifestPath);
    actionManifestStream << SVR_VRAPP_ACTION_MANIFEST;
  }

  return {appManifestPath, actionManifestPath};
}
