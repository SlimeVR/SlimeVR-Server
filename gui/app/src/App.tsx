import { createContext, useEffect, useState } from 'react';
import {
  HashRouter as Router,
  Navigate,
  Outlet,
  Route,
  Routes,
} from 'react-router-dom';
import { Home } from './components/home/Home';
import { MainLayout } from './components/MainLayout';
import { AppContextProvider } from './components/providers/AppContext';
import { SettingsLayout } from './components/settings/SettingsLayout';
import { settingsPages } from './components/settings/settings-pages';
import { QuickSearchProvider } from './components/settings/QuickSearch';
import {
  useProvideWebsocketApi,
  WebSocketApiContext,
} from './hooks/websocket-api';

import { OnboardingContextProvider } from './components/onboarding/OnboardingContextProvider';
import { OnboardingLayout } from './components/onboarding/OnboardingLayout';
import { AutomaticProportionsPage } from './components/onboarding/pages/body-proportions/AutomaticProportions';
import { ManualProportionsPage } from './components/onboarding/pages/body-proportions/ManualProportions';
import { ConnectTrackersPage } from './components/onboarding/pages/connect-trackers/ConnectTrackers';
import { AddTrackersPage } from './components/onboarding/pages/AddTrackers';
import { HomePage } from './components/onboarding/pages/Home';
import { AutomaticMountingPage } from './components/onboarding/pages/mounting/AutomaticMounting';
import { ManualMountingPage } from './components/onboarding/pages/mounting/ManualMounting';
import { TrackersAssignPage } from './components/onboarding/pages/trackers-assign/TrackerAssignment';
import { DonglePage } from './components/onboarding/pages/Dongle';
import { ConfigContextProvider } from './components/providers/ConfigContext';
import { TopBar } from './components/TopBar';
import { TrackerSettingsPage } from './components/tracker/TrackerSettings';
import { DongleSettingsPage } from './components/tracker/DongleSettings';
import { MountingChoose } from './components/onboarding/pages/mounting/MountingChoose';
import { VersionUpdateModal } from './components/VersionUpdateModal';
import semver from 'semver';
import { useBreakpoint } from './hooks/breakpoint';
import { VRModePage } from './components/vr-mode/VRModePage';
import { error, log } from './utils/logging';
import { FirmwareToolSettings } from './components/firmware-tool/FirmwareTool';
import { AppLayout } from './AppLayout';
import { Preload } from './components/Preload';
import { useDiscordPresence } from './hooks/discord-presence';
import { useProvideSettingsDefaults } from './hooks/settings-defaults';
import { useProvideUnknownDevices } from './hooks/unknown-devices';
import { useControllerNav } from './hooks/controller-nav';
import { useInternalLinkGuard } from './hooks/internal-links';
import * as Sentry from '@sentry/react';
import { CrashScreen } from './components/CrashScreen';
import { ScaledProportionsPage } from './components/onboarding/pages/body-proportions/ScaledProportions';
import { FirmwareUpdate } from './components/firmware-update/FirmwareUpdate';
import { ConnectionLost } from './components/onboarding/pages/ConnectionLost';
import { TrackingChecklistProvider } from './components/tracking-checklist/TrackingChecklistProvider';
import { ChecklistPage } from './components/tracking-checklist/TrackingChecklist';
import { QuizSlimeSetQuestion } from './components/onboarding/pages/quiz/SlimeSetQuestion';
import { QuizUsageQuestion } from './components/onboarding/pages/quiz/UsageQuestion';
import { QuizRuntimeQuestion } from './components/onboarding/pages/quiz/RuntimeQuestion';
import { QuizMocapPosQuestion } from './components/onboarding/pages/quiz/MocapPreferencesQuestions';
import { ElectronContextC, provideElectron } from './hooks/electron';
import { AppLocalizationProvider } from './i18n/config';
import { openUrl } from './hooks/crossplatform';
import { UdevRulesModal } from './components/onboarding/UdevRulesModal';
import { StepMountingStatusModal } from './components/mounting/StepMountingStatusModal';
import { StepMountingPage } from './components/onboarding/pages/mounting/StepMounting';
import { ErrorReportingConsentPage } from './components/ErrorReportingConsent';
import {
  AppStatusGate,
  CONNECTING_PATH,
  ERROR_REPORTING_CONSENT_PATH,
} from './components/AppStatusGate';
import { useProvideErrorReporting } from './hooks/error-reporting';
import { useProvideProfiles } from './hooks/profiles';

export const GH_REPO = 'SlimeVR/SlimeVR-Server';
export const VersionContext = createContext('');
export const DOCS_SITE = 'https://docs.slimevr.dev';
export const SLIMEVR_DISCORD = 'https://discord.gg/slimevr';

const SentryRoutes = Sentry.withSentryReactRouterV6Routing(Routes);

function ConnectedApp() {
  return (
    <AppContextProvider>
      <OnboardingContextProvider>
        <TrackingChecklistProvider>
          <VersionUpdateModal />
          <UdevRulesModal />
          <StepMountingStatusModal />
          <QuickSearchProvider>
            <AppLayout />
          </QuickSearchProvider>
        </TrackingChecklistProvider>
      </OnboardingContextProvider>
    </AppContextProvider>
  );
}

function AppRoutes() {
  const { isMobile } = useBreakpoint('mobile');
  useProvideErrorReporting();
  useProvideProfiles();
  useProvideSettingsDefaults();
  useProvideUnknownDevices();
  useDiscordPresence();
  useControllerNav();
  useInternalLinkGuard();

  return (
    <SentryRoutes>
      <Route element={<AppStatusGate />}>
        <Route path={CONNECTING_PATH} element={<ConnectionLost />} />
        <Route
          path={ERROR_REPORTING_CONSENT_PATH}
          element={<ErrorReportingConsentPage />}
        />
        <Route element={<ConnectedApp />}>
          <Route
            path="/"
            element={
              <MainLayout isMobile={isMobile} full>
                <Home />
              </MainLayout>
            }
          />
          <Route
            path="/firmware-update"
            element={
              <MainLayout isMobile={isMobile}>
                <FirmwareUpdate />
              </MainLayout>
            }
          />
          <Route
            path="/vr-mode"
            element={
              <MainLayout isMobile={isMobile} full>
                <VRModePage />
              </MainLayout>
            }
          />
          <Route
            path="/checklist"
            element={
              <MainLayout isMobile={isMobile}>
                <ChecklistPage />
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
            path="/dongle/:dongleid"
            element={
              <MainLayout background={false} isMobile={isMobile}>
                <DongleSettingsPage />
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
            <Route index element={<Navigate to="tracking" replace />} />
            {settingsPages.map(({ page, element }) => (
              <Route key={page.path} path={page.path} element={element} />
            ))}
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
            <Route path="quiz/slime-set" element={<QuizSlimeSetQuestion />} />
            <Route path="quiz/usage" element={<QuizUsageQuestion />} />
            <Route path="quiz/runtime" element={<QuizRuntimeQuestion />} />
            <Route path="quiz/mocap-pos" element={<QuizMocapPosQuestion />} />
            <Route path="dongle" element={<DonglePage />} />
            <Route path="firmware-tool" element={<FirmwareToolSettings />} />
            <Route path="add-trackers" element={<AddTrackersPage />} />
            <Route path="connect-trackers" element={<ConnectTrackersPage />} />
            <Route path="trackers-assign" element={<TrackersAssignPage />} />
            <Route path="mounting/choose" element={<MountingChoose />} />
            <Route path="mounting/auto" element={<AutomaticMountingPage />} />
            <Route path="mounting/step" element={<StepMountingPage />} />
            <Route path="mounting/manual" element={<ManualMountingPage />} />
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
          </Route>
          <Route path="*" element={<TopBar />} />
        </Route>
      </Route>
    </SentryRoutes>
  );
}

export default function App() {
  const websocketAPI = useProvideWebsocketApi();
  const [updateFound, setUpdateFound] = useState('');
  const electron = provideElectron();

  const fetchReleases = async () => {
    // don't show update stuff when on android
    if (window.__ANDROID__?.isThere()) return;
    // or for steam
    if (electron.isElectron && (await electron.api.isSteam())) return;

    if (!semver.valid(__VERSION_TAG__)) {
      log(
        { version: __VERSION_TAG__ || 'development' },
        'Non semver version, skipping the server update check'
      );
      return;
    }

    const releases = await fetch(
      `https://api.github.com/repos/${GH_REPO}/releases`
    )
      .then((res) => res.json())
      .catch(() => null)
      .then((json: any[]) => json.filter((rl) => rl?.prerelease === false));

    if (!releases) return;

    if (typeof releases[0].tag_name !== 'string') return;

    const version = semver.coerce(releases[0].tag_name);

    if (version && semver.gt(version, __VERSION_TAG__)) {
      setUpdateFound(releases[0].tag_name);
    }
  };

  useEffect(() => {
    fetchReleases().catch((e) => error(e, 'failed to fetch releases'));
  }, []);

  useEffect(() => {
    function onKeyboard(ev: KeyboardEvent) {
      if (ev.key === 'F1') {
        return openUrl(DOCS_SITE);
      }
    }

    document.addEventListener('keyup', onKeyboard);
    return () => document.removeEventListener('keyup', onKeyboard);
  }, []);

  return (
    <ElectronContextC.Provider value={electron}>
      <AppLocalizationProvider>
        <Sentry.ErrorBoundary
          fallback={({ eventId }) => <CrashScreen eventId={eventId} />}
        >
          <Router>
            <ConfigContextProvider>
              <WebSocketApiContext.Provider value={websocketAPI}>
                <VersionContext.Provider value={updateFound}>
                  <div className="h-full w-full text-standard bg-background-80 text-background-10">
                    <Preload />
                    <AppRoutes />
                  </div>
                </VersionContext.Provider>
              </WebSocketApiContext.Provider>
            </ConfigContextProvider>
          </Router>
        </Sentry.ErrorBoundary>
      </AppLocalizationProvider>
    </ElectronContextC.Provider>
  );
}
