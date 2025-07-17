import { useEffect } from 'react';
import {
  Outlet,
  Route,
  BrowserRouter as Router,
  Routes,
} from 'react-router-dom';
import { Home } from './components/home/Home';
import { MainLayout } from './components/MainLayout';
import { AppContextProvider } from './components/providers/AppContext';
import { GeneralSettings } from './components/settings/pages/GeneralSettings';
import { Serial } from './components/settings/pages/Serial';
import { SettingsLayout } from './components/settings/SettingsLayout';
import {
  useProvideWebsocketApi,
  WebSocketApiContext,
} from './hooks/websocket-api';

import { UpdateContextProvider } from '@/components/providers/UpdateContext.js';
import { UpdateSettings } from '@/components/settings/pages/UpdateSettings.js';
import { withSentryReactRouterV6Routing } from '@sentry/react';
import { Event, listen } from '@tauri-apps/api/event';
import * as os from '@tauri-apps/plugin-os';
import { open } from '@tauri-apps/plugin-shell';
import { AppLayout } from './AppLayout';
import { FirmwareToolSettings } from './components/firmware-tool/FirmwareTool';
import { FirmwareUpdate } from './components/firmware-update/FirmwareUpdate';
import { OnboardingContextProvider } from './components/onboarding/OnboardingContextProvicer';
import { OnboardingLayout } from './components/onboarding/OnboardingLayout';
import { AssignmentTutorialPage } from './components/onboarding/pages/assignment-preparation/AssignmentTutorial';
import { AutomaticProportionsPage } from './components/onboarding/pages/body-proportions/AutomaticProportions';
import { ManualProportionsPage } from './components/onboarding/pages/body-proportions/ManualProportions';
import { ScaledProportionsPage } from './components/onboarding/pages/body-proportions/ScaledProportions';
import { CalibrationTutorialPage } from './components/onboarding/pages/CalibrationTutorial';
import { ConnectionLost } from './components/onboarding/pages/ConnectionLost';
import { ConnectTrackersPage } from './components/onboarding/pages/ConnectTracker';
import { DonePage } from './components/onboarding/pages/Done';
import { EnterVRPage } from './components/onboarding/pages/EnterVR';
import { HomePage } from './components/onboarding/pages/Home';
import { AutomaticMountingPage } from './components/onboarding/pages/mounting/AutomaticMounting';
import { ManualMountingPage } from './components/onboarding/pages/mounting/ManualMounting';
import { MountingChoose } from './components/onboarding/pages/mounting/MountingChoose';
import { ResetTutorialPage } from './components/onboarding/pages/ResetTutorial';
import { StayAlignedSetup } from './components/onboarding/pages/stay-aligned/StayAlignedSetup';
import { TrackersAssignPage } from './components/onboarding/pages/trackers-assign/TrackerAssignment';
import { WifiCredsPage } from './components/onboarding/pages/WifiCreds';
import { Preload } from './components/Preload';
import { ConfigContextProvider } from './components/providers/ConfigContext';
import { StatusProvider } from './components/providers/StatusSystemContext';
import { SerialDetectionModal } from './components/SerialDetectionModal';
import { AdvancedSettings } from './components/settings/pages/AdvancedSettings';
import { InterfaceSettings } from './components/settings/pages/InterfaceSettings';
import { OSCRouterSettings } from './components/settings/pages/OSCRouterSettings';
import { VMCSettings } from './components/settings/pages/VMCSettings';
import { VRCOSCSettings } from './components/settings/pages/VRCOSCSettings';
import { TopBar } from './components/TopBar';
import { TrackerSettingsPage } from './components/tracker/TrackerSettings';
import { UnknownDeviceModal } from './components/UnknownDeviceModal';
import { VersionUpdateModal } from './components/VersionUpdateModal';
import { VRModePage } from './components/vr-mode/VRModePage';
import { VRCWarningsPage } from './components/vrc/VRCWarningsPage';
import { useBreakpoint, useIsTauri } from './hooks/breakpoint';
import { useDiscordPresence } from './hooks/discord-presence';
import { error, log } from './utils/logging';

export const GH_REPO = 'SlimeVR/SlimeVR-Server';
export const DOCS_SITE = 'https://docs.slimevr.dev';
export const SLIMEVR_DISCORD = 'https://discord.gg/slimevr';

const SentryRoutes = withSentryReactRouterV6Routing(Routes);

function Layout() {
  const { isMobile } = useBreakpoint('mobile');
  useDiscordPresence();

  return (
    <>
      <SerialDetectionModal></SerialDetectionModal>
      <VersionUpdateModal></VersionUpdateModal>
      <UnknownDeviceModal></UnknownDeviceModal>
      <SentryRoutes>
        <Route element={<AppLayout />}>
          <Route
            path="/"
            element={
              <MainLayout isMobile={isMobile}>
                <Home />
              </MainLayout>
            }
          />
          <Route
            path="/firmware-update"
            element={
              <MainLayout isMobile={isMobile} widgets={false}>
                <FirmwareUpdate />
              </MainLayout>
            }
          />
          <Route
            path="/vr-mode"
            element={
              <MainLayout isMobile={isMobile}>
                <VRModePage />
              </MainLayout>
            }
          />
          <Route
            path="/tracker/:trackernum/:deviceid"
            element={
              <MainLayout background={false} isMobile={isMobile}>
                <TrackerSettingsPage />
              </MainLayout>
            }
          />
          <Route
            path="/vrc-warnings"
            element={
              <MainLayout isMobile={isMobile} widgets={false}>
                <VRCWarningsPage />
              </MainLayout>
            }
          />
          <Route
            path="/settings"
            element={
              <SettingsLayout>
                <Outlet />
              </SettingsLayout>
            }
          >
            <Route path="updates" element={<UpdateSettings />} />
            <Route path="firmware-tool" element={<FirmwareToolSettings />} />
            <Route path="trackers" element={<GeneralSettings />} />
            <Route path="serial" element={<Serial />} />
            <Route path="osc/router" element={<OSCRouterSettings />} />
            <Route path="osc/vrchat" element={<VRCOSCSettings />} />
            <Route path="osc/vmc" element={<VMCSettings />} />
            <Route path="interface" element={<InterfaceSettings />} />
            <Route path="advanced" element={<AdvancedSettings />} />
          </Route>
          <Route
            path="/onboarding"
            element={
              <OnboardingLayout>
                <Outlet />
              </OnboardingLayout>
            }
          >
            <Route path="home" element={<HomePage />} />
            <Route path="wifi-creds" element={<WifiCredsPage />} />
            <Route path="connect-trackers" element={<ConnectTrackersPage />} />
            <Route
              path="calibration-tutorial"
              element={<CalibrationTutorialPage />}
            />
            <Route
              path="assign-tutorial"
              element={<AssignmentTutorialPage />}
            />
            <Route path="trackers-assign" element={<TrackersAssignPage />} />
            <Route path="enter-vr" element={<EnterVRPage />} />
            <Route path="mounting/choose" element={<MountingChoose />}></Route>
            <Route path="mounting/auto" element={<AutomaticMountingPage />} />
            <Route path="mounting/manual" element={<ManualMountingPage />} />
            <Route path="reset-tutorial" element={<ResetTutorialPage />} />
            <Route
              path="body-proportions/auto"
              element={<AutomaticProportionsPage />}
            />
            <Route
              path="body-proportions/manual"
              element={<ManualProportionsPage />}
            />
            <Route
              path="body-proportions/scaled"
              element={<ScaledProportionsPage />}
            />
            <Route path="stay-aligned" element={<StayAlignedSetup />} />
            <Route path="done" element={<DonePage />} />
          </Route>
          <Route path="*" element={<TopBar></TopBar>}></Route>
        </Route>
      </SentryRoutes>
    </>
  );
}

export default function App() {
  const websocketAPI = useProvideWebsocketApi();
  const isTauri = useIsTauri();

  useEffect(() => {
    const onKeydown: (arg0: KeyboardEvent) => void = function (event) {
      // prevent search bar keybind
      if (
        event.key === 'F3' ||
        (event.ctrlKey && event.key === 'f') ||
        (event.metaKey && event.key === 'f')
      ) {
        event.preventDefault();
      }
    };

    window.addEventListener('keydown', onKeydown);
    return () => window.removeEventListener('keydown', onKeydown);
  }, []);

  if (isTauri) {
    useEffect(() => {
      const type = os.type();
      document.body.classList.add(type.toLowerCase());

      return () => document.body.classList.remove(type.toLowerCase());
    }, []);
  }

  if (isTauri) {
    useEffect(() => {
      const unlisten = listen(
        'server-status',
        (event: Event<[string, string]>) => {
          const [eventType, s] = event.payload;
          if ('stderr' === eventType) {
            // This strange invocation is what lets us lose the line information in the console
            // See more here: https://stackoverflow.com/a/48994308
            // These two are fine to keep with console.log, they are server logs
            setTimeout(
              console.log.bind(
                console,
                `%c[SERVER] %c${s}`,
                'color:cyan',
                'color:red'
              )
            );
          } else if (eventType === 'stdout') {
            setTimeout(
              console.log.bind(
                console,
                `%c[SERVER] %c${s}`,
                'color:cyan',
                'color:green'
              )
            );
          } else if (eventType === 'error') {
            error('Error: %s', s);
          } else if (eventType === 'terminated') {
            error('Server Process Terminated: %s', s);
          } else if (eventType === 'other') {
            log('Other process event: %s', s);
          }
        }
      );
      return () => {
        unlisten.then((fn) => fn());
      };
    }, []);
  }

  useEffect(() => {
    function onKeyboard(ev: KeyboardEvent) {
      if (ev.key === 'F1') {
        return open(DOCS_SITE).catch(() => window.open(DOCS_SITE, '_blank'));
      }
    }

    document.addEventListener('keyup', onKeyboard);
    return () => document.removeEventListener('keyup', onKeyboard);
  }, []);

  return (
    <Router>
      <ConfigContextProvider>
        <WebSocketApiContext.Provider value={websocketAPI}>
          <AppContextProvider>
            <OnboardingContextProvider>
              <StatusProvider>
                <UpdateContextProvider>
                  <div className="h-full w-full text-standard bg-background-80 text-background-10">
                    <Preload />
                    {!websocketAPI.isConnected && (
                      <ConnectionLost></ConnectionLost>
                    )}
                    {websocketAPI.isConnected && <Layout></Layout>}
                  </div>
                </UpdateContextProvider>
              </StatusProvider>
            </OnboardingContextProvider>
          </AppContextProvider>
        </WebSocketApiContext.Provider>
      </ConfigContextProvider>
    </Router>
  );
}
