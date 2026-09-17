#include <csignal>
#include <map>
#include <optional>
#include <thread>
#include <tuple>

// These must be included before Windows.h because of some macro collisions.
#include "flatbuffers/flatbuffers.h"
#include "solarxr_protocol/generated/all_generated.h"

#include "bridge/BridgeClient.hpp"
#include "logger.hpp"
#include "vr_utils.hpp"

#include "openvr.h"

#ifdef _WIN32
#define WIN32_MEAN_AND_LEAN
#define NOMINMAX
#include <Windows.h>
#else
#include <cstdlib>
#endif

namespace fs = std::filesystem;
using namespace std::chrono_literals;
using namespace solarxr_protocol;

static void ShutdownVR(vr::IVRSystem* _sys) { vr::VR_Shutdown(); }

static void OnYawReset(BridgeTransport& conn) {
    flatbuffers::FlatBufferBuilder fbb;

    auto resetReq = rpc::CreateResetRequest(fbb, rpc::ResetType::YAW, 0, 0.f);
    auto msgHeader = rpc::CreateRpcMessageHeader(fbb, 0, 0, rpc::RpcMessage::ResetRequest, resetReq.Union());

    auto rpcMsgs = fbb.CreateVector({ msgHeader });
    auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs);
    fbb.Finish(bundle);
    conn.SendMessage(fbb);
}
static void OnFullReset(BridgeTransport& conn) {
    flatbuffers::FlatBufferBuilder fbb;

    auto resetReq = rpc::CreateResetRequest(fbb, rpc::ResetType::FULL, 0, 0.f);
    auto msgHeader = rpc::CreateRpcMessageHeader(fbb, 0, 0, rpc::RpcMessage::ResetRequest, resetReq.Union());

    auto rpcMsgs = fbb.CreateVector({ msgHeader });
    auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs);
    fbb.Finish(bundle);
    conn.SendMessage(fbb);
}
static void OnMountingCalibration(BridgeTransport& conn) {
    flatbuffers::FlatBufferBuilder fbb;

    auto resetReq = rpc::CreateResetRequest(fbb, rpc::ResetType::POSE_MOUNTING, 0, 0.f);
    auto msgHeader = rpc::CreateRpcMessageHeader(fbb, 0, 0, rpc::RpcMessage::ResetRequest, resetReq.Union());

    auto rpcMsgs = fbb.CreateVector({ msgHeader });
    auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs);
    fbb.Finish(bundle);
    conn.SendMessage(fbb);
}
static void OnFeetMountingCalibration(BridgeTransport& conn) {
    flatbuffers::FlatBufferBuilder fbb;

    auto bodyParts = fbb.CreateVector(
        { datatypes::BodyPart::LEFT_FOOT, datatypes::BodyPart::RIGHT_FOOT });
    auto resetReq = rpc::CreateResetRequest(fbb, rpc::ResetType::POSE_MOUNTING, bodyParts, 0.f);
    auto msgHeader = rpc::CreateRpcMessageHeader(fbb, 0, 0, rpc::RpcMessage::ResetRequest, resetReq.Union());

    auto rpcMsgs = fbb.CreateVector({ msgHeader });
    auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs);
    fbb.Finish(bundle);
    conn.SendMessage(fbb);
}
static void OnToggleTracking(BridgeTransport& conn) {
    static bool shouldPause = false;
    flatbuffers::FlatBufferBuilder fbb;

    shouldPause = !shouldPause;
    auto toggleReq = rpc::CreateSetPauseTrackingRequest(fbb, shouldPause);
    auto msgHeader = rpc::CreateRpcMessageHeader(fbb, 0, 0, rpc::RpcMessage::SetPauseTrackingRequest, toggleReq.Union());

    auto rpcMsgs = fbb.CreateVector({ msgHeader });
    auto bundle = CreateMessageBundle(fbb, 0, rpcMsgs);
    fbb.Finish(bundle);
    conn.SendMessage(fbb);
}

sig_atomic_t should_exit = 0;

static void OnSignal(int signal) {
    Logger::Get().Info("Received signal {}", signal);
    should_exit = 1;
}

int main() {
    auto& logger = Logger::Get();

    // Steam and SteamVR respectively set these environment variables on applications that
    // they spawn, which the SteamVR client library (vrclient) will then use as the
    // application key when initiating the connection with vrserver. This may not
    // be ideal if we are launched by another Steam or OpenVR application, as we will inherit
    // their app key through the environment, which means SteamVR will load the wrong bindings.
    // A real-world example of this is if someone uses an application such as OpenVR-Autostarter
    // to start the SlimeVR Server. OpenVR-Autostarter installs an application manifest with the
    // app key "dreiekk.openvr-autostarter", so when it starts SlimeVR Server, which then starts us,
    // the STEAMVR_APPKEY="dreiekk.openvr-autostarter" environment variable will be set.
    //
    // We want SteamVR to use the appkey of the Steam version of our application if possible.
    // Unfortunately, we cannot install an application vrmanifest to force the app key,
    // as SteamVR requires a binary path in the manifest to consider it valid. We do not want to install
    // a manifest with a binary path because we want the SlimeVR Server to start us when the
    // driver initiates a connection, rather than getting auto-started by SteamVR.
    //
    // This is not documented anywhere publicly, see CVRClient::SendConnectMessage in vrclient instead.

    constexpr const char* STEAM_APPID = "3245490";
    constexpr const char* STEAMVR_APPKEY = "steam.overlay.3245490";

#ifdef _WIN32
    SetEnvironmentVariableA("SteamAppId", STEAM_APPID);
    SetEnvironmentVariableA("STEAMVR_APPKEY", STEAMVR_APPKEY);
#else
    setenv("SteamAppId", STEAM_APPID, 1);
    setenv("STEAMVR_APPKEY", STEAMVR_APPKEY, 1);
#endif

    try {
        BridgeClient conn(nullptr, nullptr, [] { should_exit = 1; }, [](std::exception&) { should_exit = 1; });
        conn.Start();

        std::unique_ptr<vr::IVRSystem, decltype(&ShutdownVR)> sys{ nullptr, ShutdownVR };

        // On vrlink, VR_Init returns
        // VRInitError_Driver_WirelessHmdNotConnected while the connection
        // is initialising, so keep calling VR_Init until it ends up
        // succeeding

        vr::EVRInitError err{ vr::VRInitError_Driver_WirelessHmdNotConnected };
        while (err == vr::VRInitError_Driver_WirelessHmdNotConnected) {
            sys.reset(vr::VR_Init(&err, vr::VRApplication_Background));
            if (err == vr::VRInitError_Driver_WirelessHmdNotConnected)
                std::this_thread::sleep_for(500ms);
        }

        if (sys == nullptr or err != vr::VRInitError_None) {
            logger.Error("Failed to init OpenVR: {} ({})",
                         vr::VR_GetVRInitErrorAsSymbol(err),
                         vr::VR_GetVRInitErrorAsEnglishDescription(err));
            return 1;
        }

        logger.Info("Initialised OpenVR, HMD model '{}'", VRUtils::GetStringProp(vr::k_unTrackedDeviceIndex_Hmd, vr::Prop_ModelNumber_String).value_or("<unknown>"));

        vr::IVRApplications* app = vr::VRApplications();
        vr::IVRInput* input = vr::VRInput();

        fs::path actionManifestPath;
        std::tie(std::ignore, actionManifestPath) = VRUtils::InitialiseManifests();

        // We don't want our app key to randomly change if SteamVR decides to honour
        // application manifests with no binary path. Instead let it generate an
        // app key based on the executable name (system.generated.[lowercase executable name])
#if false
        if (auto err = app->AddApplicationManifest(appManifestPath.string().data(), true);
            err != vr::VRApplicationError_None) {
            logger.error("Failed to add application manifest: {}",
                         app->GetApplicationsErrorNameFromEnum(err));
            return 1;
        }
#endif

        // SetActionManifestPath may return IPCError if vrserver is busy and takes
        // too long to reply, so keep invoking until it succeeds
        {
            vr::EVRInputError err;
            constexpr int max_tries = 5;
            int tries = 0;
            while ((err = input->SetActionManifestPath(actionManifestPath.string().data())) == vr::VRInputError_IPCError && tries++ < max_tries) {
                logger.Debug("IPC error loading action manifest, retrying ({}/{})", tries, max_tries);
                std::this_thread::sleep_for(20ms);
            }

            if (err != vr::VRInputError_None) {
                logger.Error("Failed to set action manifest path: {}", std::to_underlying(err));
                return 1;
            }
        }

        vr::VRActionSetHandle_t action_set;
        if (auto err = input->GetActionSetHandle("/actions/main", &action_set);
            err != vr::VRInputError_None) {
            logger.Error("Failed to get main action set handle: {}", std::to_underlying(err));
            return 1;
        }

        std::map<std::string, std::tuple<vr::VRActionHandle_t, std::function<void(BridgeTransport&)>>>
            actions{
                { "/actions/main/in/YawReset", std::make_tuple(vr::k_ulInvalidActionHandle, OnYawReset) },
                { "/actions/main/in/FullReset", std::make_tuple(vr::k_ulInvalidActionHandle, OnFullReset) },
                { "/actions/main/in/MountingCalibration", std::make_tuple(vr::k_ulInvalidActionHandle, OnMountingCalibration) },
                { "/actions/main/in/FeetMountingCalibration", std::make_tuple(vr::k_ulInvalidActionHandle, OnFeetMountingCalibration) },
                { "/actions/main/in/ToggleTracking", std::make_tuple(vr::k_ulInvalidActionHandle, OnToggleTracking) },
            };

        for (auto& [name, tuple] : actions) {
            auto& [handle, _] = tuple;
            if (auto err = input->GetActionHandle(name.c_str(), &handle);
                err != vr::VRInputError_None || handle == vr::k_ulInvalidActionHandle) {
                logger.Warning("Failed to get action handle for action {}: {}", name,
                               std::to_underlying(err));
            }
        }

        constexpr auto interval = 1000ms / 30;

        signal(SIGINT, OnSignal);
        signal(SIGTERM, OnSignal);
        while (!should_exit) {
            vr::VREvent_t event{};
            while (sys->PollNextEvent(&event, sizeof(event))) {
                switch (event.eventType) {
                case vr::VREvent_Quit:
                    logger.Info("OpenVR runtime requested quit");
                    should_exit = 1;
                    break;
                case vr::VREvent_Input_BindingLoadFailed: {
                    auto& loadData = event.data.inputBinding;
                    logger.Debug("Binding load failed (ulAppContainer={} pathMessage={} pathUrl={} pathControllerType={})",
                                 loadData.ulAppContainer, loadData.pathMessage, loadData.pathUrl, loadData.pathControllerType);
                    break;
                }
                case vr::VREvent_Input_BindingLoadSuccessful: {
                    auto& loadData = event.data.inputBinding;
                    logger.Debug("Binding load successful (ulAppContainer={} pathMessage={} pathUrl={} pathControllerType={})",
                                 loadData.ulAppContainer, loadData.pathMessage, loadData.pathUrl, loadData.pathControllerType);
                    break;
                }
                case vr::VREvent_Input_ActionManifestReloaded:
                    logger.Debug("Action manifest reloaded");
                    break;
                case vr::VREvent_Input_ActionManifestLoadFailed: {
                    auto& manifestData = event.data.actionManifest;
                    logger.Debug(
                        "Action manifest load failed (pathAppKey={} pathMessage={} pathMessageParam={} pathManifestPath={})",
                        manifestData.pathAppKey, manifestData.pathMessage, manifestData.pathMessageParam, manifestData.pathManifestPath);
                    break;
                }
                default:
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
                logger.Warning("Error when updating action states: {}",
                               std::to_underlying(err));
            }

            for (auto& [name, tuple] : actions) {
                auto& [handle, callback] = tuple;
                vr::InputDigitalActionData_t action_data{};
                if (auto err = input->GetDigitalActionData(
                        handle, &action_data, sizeof(action_data),
                        vr::k_ulInvalidInputValueHandle);
                    err != vr::VRInputError_None) {
                    logger.Warning("Failed to get action state for {} ({}): {}", name, handle, std::to_underlying(err));
                    continue;
                }

                if (action_data.bActive && action_data.bChanged && action_data.bState) {
                    logger.Debug("Action {} triggered", name);
                    callback(conn);
                }
            }

            logger.Flush();
            std::this_thread::sleep_for(interval);
        }

        logger.Info("Main loop done");
    } catch (std::exception& ex) {
        logger.Error("Exception in main: {}", ex.what());
        logger.Flush();
        return 1;
    }

    logger.Flush();
    return 0;
}
