#include <cassert>
#include <csignal>
#include <map>
#include <optional>
#include <thread>
#include <tuple>

// These must be included before Windows.h because of some macro collisions.
#include "flatbuffers/flatbuffers.h"
#include "solarxr_protocol/generated/all_generated.h"

#include "logger.hpp"
#include "solarxr.hpp"
#include "vr_utils.hpp"

#include "openvr.h"

using namespace std::chrono_literals;
using namespace solarxr_protocol;

static void shutdown_vr(vr::IVRSystem *_sys) { vr::VR_Shutdown(); }

static void onYawReset(SolarXRConnection &conn) {
  flatbuffers::FlatBufferBuilder fbb;

  auto resetReq = rpc::CreateResetRequest(fbb, rpc::ResetType::Yaw, 0, 0.f);
  auto msgHeader = rpc::CreateRpcMessageHeader(
      fbb, nullptr, rpc::RpcMessage::ResetRequest, resetReq.Union());

  auto rpcMsgs = fbb.CreateVector({msgHeader});
  auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs, 0);
  fbb.Finish(bundle);
  conn.sendMsg(fbb);
}
static void onFullReset(SolarXRConnection &conn) {
  flatbuffers::FlatBufferBuilder fbb;

  auto resetReq = rpc::CreateResetRequest(fbb, rpc::ResetType::Full, 0, 0.f);
  auto msgHeader = rpc::CreateRpcMessageHeader(
      fbb, nullptr, rpc::RpcMessage::ResetRequest, resetReq.Union());

  auto rpcMsgs = fbb.CreateVector({msgHeader});
  auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs, 0);
  fbb.Finish(bundle);
  conn.sendMsg(fbb);
}
static void onMountingCalibration(SolarXRConnection &conn) {
  flatbuffers::FlatBufferBuilder fbb;

  auto resetReq =
      rpc::CreateResetRequest(fbb, rpc::ResetType::Mounting, 0, 0.f);
  auto msgHeader = rpc::CreateRpcMessageHeader(
      fbb, nullptr, rpc::RpcMessage::ResetRequest, resetReq.Union());

  auto rpcMsgs = fbb.CreateVector({msgHeader});
  auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs, 0);
  fbb.Finish(bundle);
  conn.sendMsg(fbb);
}
static void onFeetMountingCalibration(SolarXRConnection &conn) {
  flatbuffers::FlatBufferBuilder fbb;

  auto bodyParts = fbb.CreateVector(
      {datatypes::BodyPart::LEFT_FOOT, datatypes::BodyPart::RIGHT_FOOT});
  auto resetReq =
      rpc::CreateResetRequest(fbb, rpc::ResetType::Mounting, bodyParts, 0.f);
  auto msgHeader = rpc::CreateRpcMessageHeader(
      fbb, nullptr, rpc::RpcMessage::ResetRequest, resetReq.Union());

  auto rpcMsgs = fbb.CreateVector({msgHeader});
  auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs, 0);
  fbb.Finish(bundle);
  conn.sendMsg(fbb);
}
static void onToggleTracking(SolarXRConnection &conn) {
  static bool shouldPause = false;
  flatbuffers::FlatBufferBuilder fbb;

  auto toggleReq = rpc::CreateSetPauseTrackingRequest(fbb, shouldPause);
  auto msgHeader = rpc::CreateRpcMessageHeader(
      fbb, nullptr, rpc::RpcMessage::SetPauseTrackingRequest,
      toggleReq.Union());

  auto rpcMsgs = fbb.CreateVector({msgHeader});
  auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs, 0);
  fbb.Finish(bundle);
  conn.sendMsg(fbb);
}

sig_atomic_t should_exit = 0;

static void signal_handler(int signal) {
  Logger::get().info("Received signal {}", signal);
  should_exit = 1;
}

int main() {
  auto &logger = Logger::get();

  try {
    SolarXRConnection conn;
    vr::EVRInitError err{};
    std::unique_ptr<vr::IVRSystem, decltype(&shutdown_vr)> sys = {
        vr::VR_Init(&err, vr::VRApplication_Background), shutdown_vr};
    if (sys == nullptr or err != vr::VRInitError_None) {
      logger.error("Failed to init OpenVR: {} ({})",
                   vr::VR_GetVRInitErrorAsSymbol(err),
                   vr::VR_GetVRInitErrorAsEnglishDescription(err));
      return 1;
    }

    logger.info("Initialised OpenVR, HMD model '{}'",
                VRUtils::getStringProp(vr::k_unTrackedDeviceIndex_Hmd,
                                       vr::Prop_ModelNumber_String)
                    .value_or("<unknown>"));

    vr::IVRApplications *app = vr::VRApplications();
    vr::IVRInput *input = vr::VRInput();

    auto [appManifestPath, actionManifestPath] = VRUtils::initialiseManifest();

    if (auto err =
            app->AddApplicationManifest(appManifestPath.string().data(), true);
        err != vr::VRApplicationError_None) {
      logger.error("Failed to add application manifest: {}",
                   app->GetApplicationsErrorNameFromEnum(err));
      return 1;
    }

    if (auto err =
            input->SetActionManifestPath(actionManifestPath.string().data());
        err != vr::VRInputError_None) {
      logger.error("Failed to set action manifest path: {}",
                   std::to_underlying(err));
      return 1;
    }

    vr::VRActionSetHandle_t action_set;
    if (auto err = input->GetActionSetHandle("/actions/main", &action_set);
        err != vr::VRInputError_None) {
      logger.error("Failed to get main action set handle: {}",
                   std::to_underlying(err));
      return 1;
    }

    std::map<std::string, std::tuple<vr::VRActionHandle_t,
                                     std::function<void(SolarXRConnection &)>>>
        actions{
            {"/actions/main/in/YawReset",
             std::make_tuple(vr::k_ulInvalidActionHandle, onYawReset)},
            {"/actions/main/in/FullReset",
             std::make_tuple(vr::k_ulInvalidActionHandle, onFullReset)},
            {"/actions/main/in/MountingCalibration",
             std::make_tuple(vr::k_ulInvalidActionHandle,
                             onMountingCalibration)},
            {"/actions/main/in/FeetMountingCalibration",
             std::make_tuple(vr::k_ulInvalidActionHandle,
                             onFeetMountingCalibration)},
            {"/actions/main/in/ToggleTracking",
             std::make_tuple(vr::k_ulInvalidActionHandle, onToggleTracking)},
        };

    for (auto &[name, tuple] : actions) {
      auto &[handle, _] = tuple;
      if (auto err = input->GetActionHandle(name.c_str(), &handle);
          err != vr::VRInputError_None ||
          handle == vr::k_ulInvalidActionHandle) {
        logger.warning("Failed to get action handle for action {}: {}", name,
                       std::to_underlying(err));
      }
    }

    constexpr auto interval = 1000ms / 30;

    signal(SIGINT, signal_handler);
    signal(SIGTERM, signal_handler);
    while (!should_exit) {
      vr::VREvent_t event{};
      while (sys->PollNextEvent(&event, sizeof(event))) {
        switch (event.eventType) {
        case vr::VREvent_Quit:
          logger.info("OpenVR runtime requested quit");
          should_exit = 1;
          break;
        default:
          logger.debug("Received event {}", event.eventType);
          break;
        }
      }

      vr::VRActiveActionSet_t set{
          .ulActionSet = action_set,
          .ulRestrictedToDevice = vr::k_ulInvalidInputValueHandle,
          .ulSecondaryActionSet = vr::k_ulInvalidActionSetHandle,
          .nPriority = 0,
      };
      if (auto err = input->UpdateActionState(&set, sizeof(set), 1);
          err != vr::VRInputError_None) {
        logger.warning("Error when updating action states: {}",
                       std::to_underlying(err));
      }

      for (auto &[name, tuple] : actions) {
        auto &[handle, callback] = tuple;
        vr::InputDigitalActionData_t action_data{};
        if (auto err = input->GetDigitalActionData(
                handle, &action_data, sizeof(action_data),
                vr::k_ulInvalidInputValueHandle);
            err != vr::VRInputError_None) {
          logger.warning("Failed to get action state for {} ({}): {}", name,
                         handle, std::to_underlying(err));
          continue;
        }

        if (action_data.bActive && action_data.bChanged && action_data.bState) {
          logger.debug("Action {} triggered", name);
          callback(conn);
        }
      }

      std::this_thread::sleep_for(interval);
    }

    logger.info("Main loop done");
  } catch (std::exception &ex) {
    logger.error("Exception in main: {}", ex.what());
    return 1;
  }

  return 0;
}
