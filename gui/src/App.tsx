import { useEffect } from 'react';
import {
  BrowserRouter as Router,
  Outlet,
  Route,
  Routes
} from 'react-router-dom';
import { Home } from './components/home/Home';
import { MainLayoutRoute } from './components/MainLayout';
import { AppContextProvider } from './components/providers/AppContext';
import { GeneralSettings } from './components/settings/pages/GeneralSettings';
import { Serial } from './components/settings/pages/Serial';
import { SettingsLayoutRoute } from './components/settings/SettingsLayout';
import {
  useProvideWebsocketApi,
  WebSocketApiContext
} from './hooks/websocket-api';

import { Event, listen } from '@tauri-apps/api/event';
import { useTranslation, withTranslation } from 'react-i18next';
import { OnboardingContextProvider } from './components/onboarding/OnboardingContextProvicer';
import { OnboardingLayout } from './components/onboarding/OnboardingLayout';
import { AutomaticProportionsPage } from './components/onboarding/pages/body-proportions/AutomaticProportions';
import { ManualProportionsPage } from './components/onboarding/pages/body-proportions/ManualProportions';
import { ConnectTrackersPage } from './components/onboarding/pages/ConnectTracker';
import { DonePage } from './components/onboarding/pages/Done';
import { EnterVRPage } from './components/onboarding/pages/EnterVR';
import { HomePage } from './components/onboarding/pages/Home';
import { AutomaticMountingPage } from './components/onboarding/pages/mounting/AutomaticMounting';
import { ManualMountingPage } from './components/onboarding/pages/mounting/ManualMounting';
import { ResetTutorialPage } from './components/onboarding/pages/ResetTutorial';
import { TrackersAssignPage } from './components/onboarding/pages/trackers-assign/TrackerAssignment';
import { WifiCredsPage } from './components/onboarding/pages/WifiCreds';
import { ConfigContextProvider } from './components/providers/ConfigContext';
import { SerialDetectionModal } from './components/SerialDetectionModal';
import { VRCOSCSettings } from './components/settings/pages/VRCOSCSettings';
import { TopBar } from './components/TopBar';
import { TrackerSettingsPage } from './components/tracker/TrackerSettings';
import { useConfig } from './hooks/config';
import { OSCRouterSettings } from './components/settings/pages/OSCRouterSettings';

function Layout() {
  const { loading } = useConfig();
  if (loading) return <></>;

  return (
    <>
      <SerialDetectionModal></SerialDetectionModal>
      <Routes>
        <Route
          path="/"
          element={
            <MainLayoutRoute>
              <Home />
            </MainLayoutRoute>
          }
        />
        <Route
          path="/tracker/:trackernum/:deviceid"
          element={
            <MainLayoutRoute background={false}>
              <TrackerSettingsPage />
            </MainLayoutRoute>
          }
        />
        <Route
          path="/settings"
          element={
            <SettingsLayoutRoute>
              <Outlet></Outlet>
            </SettingsLayoutRoute>
          }
        >
          <Route path="trackers" element={<GeneralSettings />} />
          <Route path="serial" element={<Serial />} />
          <Route path="osc/router" element={<OSCRouterSettings />} />
          <Route path="osc/vrchat" element={<VRCOSCSettings />} />
        </Route>
        <Route
          path="/onboarding"
          element={
            <OnboardingLayout>
              <Outlet></Outlet>
            </OnboardingLayout>
          }
        >
          <Route path="home" element={<HomePage />} />
          <Route path="wifi-creds" element={<WifiCredsPage />} />
          <Route path="connect-trackers" element={<ConnectTrackersPage />} />
          <Route path="trackers-assign" element={<TrackersAssignPage />} />
          <Route path="enter-vr" element={<EnterVRPage />} />
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
          <Route path="done" element={<DonePage />} />
        </Route>
        <Route path="*" element={<TopBar></TopBar>}></Route>
      </Routes>
    </>
  );
}

function App() {
  const websocketAPI = useProvideWebsocketApi();
  const { t } = useTranslation();

  useEffect(() => {
    const unlisten = listen(
      'server-status',
      (event: Event<[string, string]>) => {
        const [event_type, s] = event.payload;
        if ('stderr' === event_type) {
          // This strange invocation is what lets us lose the line information in the console
          // See more here: https://stackoverflow.com/a/48994308
          setTimeout(
            console.log.bind(
              console,
              `%c[SERVER] %c${s}`,
              'color:cyan',
              'color:red'
            )
          );
        } else if (event_type === 'stdout') {
          setTimeout(
            console.log.bind(
              console,
              `%c[SERVER] %c${s}`,
              'color:cyan',
              'color:green'
            )
          );
        } else if (event_type === 'error') {
          console.error('Error: %s', s);
        } else if (event_type === 'terminated') {
          console.error('Server Process Terminated: %s', s);
        } else if (event_type === 'other') {
          console.log('Other process event: %s', s);
        }
      }
    );
    return () => {
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      unlisten.then(() => {});
    };
  }, []);

  return (
    <Router>
      <ConfigContextProvider>
        <WebSocketApiContext.Provider value={websocketAPI}>
          <AppContextProvider>
            <OnboardingContextProvider>
              <div className="h-full w-full text-standard bg-background-80 text-background-10">
                <div className="flex-col h-full">
                  {!websocketAPI.isConnected && (
                    <>
                      <TopBar></TopBar>
                      <div className="flex w-full h-full justify-center items-center p-2">
                        {websocketAPI.isFirstConnection
                          ? t('websocket.connecting')
                          : t('websocket.connection-lost')}
                      </div>
                    </>
                  )}
                  {websocketAPI.isConnected && <Layout></Layout>}
                </div>
              </div>
            </OnboardingContextProvider>
          </AppContextProvider>
        </WebSocketApiContext.Provider>
      </ConfigContextProvider>
    </Router>
  );
}

export default withTranslation()(App);
