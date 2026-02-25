import { ReactNode, useContext, useEffect, useState } from 'react';
import { NavLink, useMatch } from 'react-router-dom';
import {
  RpcMessage,
  ServerInfosRequestT,
  ServerInfosResponseT,
  TrackerStatus,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CloseIcon } from './commons/icon/CloseIcon';
import { MaximiseIcon } from './commons/icon/MaximiseIcon';
import { MinimiseIcon } from './commons/icon/MinimiseIcon';
import { SlimeVRIcon } from './commons/icon/SimevrIcon';
import { ProgressBar } from './commons/ProgressBar';
import { Typography } from './commons/Typography';
import { DownloadIcon } from './commons/icon/DownloadIcon';
import { DOCS_SITE, GH_REPO, VersionContext } from '@/App';
import classNames from 'classnames';
import { QuestionIcon } from './commons/icon/QuestionIcon';
import { useBreakpoint } from '@/hooks/breakpoint';
import { GearIcon } from './commons/icon/GearIcon';
import { TrackersStillOnModal } from './TrackersStillOnModal';
import { useConfig } from '@/hooks/config';
import { TrayOrExitModal } from './TrayOrExitModal';
import { ErrorConsentModal } from './ErrorConsentModal';
import { UdevRulesModal } from './onboarding/UdevRulesModal';
import { useAtomValue } from 'jotai';
import { connectedIMUTrackersAtom } from '@/store/app-store';
import { useElectron } from '@/hooks/electron';
import { openUrl } from '@/hooks/crossplatform';

export function VersionTag() {
  return (
    <div
      className={classNames(
        'flex justify-around flex-col text-standard-bold',
        'text-status-success bg-status-success bg-opacity-20 rounded-lg',
        'px-3 select-text cursor-pointer'
      )}
      onClick={() => {
        const url = `https://github.com/${GH_REPO}/releases`;
        openUrl(url);
      }}
    >
      {(__VERSION_TAG__ || __COMMIT_HASH__) + (__GIT_CLEAN__ ? '' : '-dirty')}
    </div>
  );
}

export function TopBar({
  progress,
}: {
  children?: ReactNode;
  progress?: number;
}) {
  const electron = useElectron();
  const { isMobile } = useBreakpoint('mobile');
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const connectedIMUTrackers = useAtomValue(connectedIMUTrackersAtom);
  const { config, setConfig, saveConfig } = useConfig();
  const version = useContext(VersionContext);
  const [localIp, setLocalIp] = useState<string | null>(null);
  const [showConnectedTrackersWarning, setConnectedTrackerWarning] =
    useState(false);
  const [showTrayOrExitModal, setShowTrayOrExitModal] = useState(false);
  const doesMatchSettings = useMatch({
    path: '/settings/*',
  });

  const closeApp = async () => {
    if (!electron.isElectron) throw 'no electron';

    await saveConfig();
    electron.api.close();
  };
  const tryCloseApp = async (dontTray = false) => {
    if (!electron.isElectron) throw 'no electron';

    if (config?.useTray === null) {
      setShowTrayOrExitModal(true);
      return;
    }

    if (config?.useTray && !dontTray) {
      electron.api.minimize();
    } else if (
      config?.connectedTrackersWarning &&
      connectedIMUTrackers.filter(
        (t) => t.tracker.status !== TrackerStatus.TIMED_OUT
      ).length > 0
    ) {
      setConnectedTrackerWarning(true);
    } else {
      await closeApp();
    }
  };

  // useEffect(() => {
  //   if (!electron.isElectron) return;

  //   const unlistenTrayClose = listen('try-close', async () => {
  //     const window = getCurrentWindow();
  //     await window.show();
  //     await window.requestUserAttention(UserAttentionType.Critical);
  //     await window.setFocus();
  //     if (isTrayAvailable) await invoke('update_tray_text');
  //     await tryCloseApp(true);
  //   });

  //   const unlistenCloseRequested = getCurrentWindow().listen(
  //     TauriEvent.WINDOW_CLOSE_REQUESTED,
  //     async (data) => {
  //       const ev = new CloseRequestedEvent(data);
  //       ev.preventDefault();
  //       await tryCloseApp();
  //     }
  //   );

  //   return () => {
  //     unlistenTrayClose.then((fn) => fn());
  //     unlistenCloseRequested.then((fn) => fn());
  //   };
  // }, [
  //   config?.useTray,
  //   config?.connectedTrackersWarning,
  //   JSON.stringify(connectedIMUTrackers.map((t) => t.tracker.status)),
  // ]);

  useEffect(() => {
    sendRPCPacket(RpcMessage.ServerInfosRequest, new ServerInfosRequestT());
  }, []);

  useRPCPacket(
    RpcMessage.ServerInfosResponse,
    ({ localIp }: ServerInfosResponseT) => {
      if (localIp) setLocalIp(localIp.toString());
    }
  );

  return (
    <>
      <div className="flex gap-0 flex-col">
        <div className="h-[3px]" />
        <div className="flex gap-2 h-[38px] z-49">
          <div className="flex px-2 py-2 justify-around z-49">
            <div className="flex gap-2">
              {!isMobile && (
                <NavLink
                  to="/"
                  className="flex justify-around flex-col select-all"
                >
                  <SlimeVRIcon />
                </NavLink>
              )}
              {!isMobile && (
                <div
                  className={classNames('flex justify-around flex-col')}
                  data-electron-drag-region
                >
                  <Typography>SlimeVR</Typography>
                </div>
              )}
              {(!doesMatchSettings || !isMobile) && <VersionTag />}
              {doesMatchSettings && (
                <div
                  className={classNames(
                    'flex justify-around flex-col text-standard-bold text-status-special',
                    'bg-status-special bg-opacity-20 rounded-lg px-3 select-text'
                  )}
                >
                  {localIp || 'unknown local ip'}
                </div>
              )}

              {version && electron.isElectron && (
                <div
                  className="cursor-pointer"
                  onClick={() => {
                    const url =
                      electron.data().os.type === 'windows'
                        ? 'https://slimevr.dev/download'
                        : `https://github.com/${GH_REPO}/releases/latest`;
                    openUrl(url);
                  }}
                >
                  <DownloadIcon />
                </div>
              )}
            </div>
          </div>
          <div
            className="flex flex-grow items-center h-full justify-center z-50"
            data-electron-drag-region
          >
            {!isMobile && (
              <>
                <div
                  className="flex max-w-xl h-full items-center w-full"
                  data-electron-drag-region
                >
                  {progress !== undefined && (
                    <ProgressBar progress={progress} height={3} parts={3} />
                  )}
                </div>
              </>
            )}
          </div>
          <div className="flex justify-end items-center px-2 gap-2 z-50">
            <NavLink
              to="/settings/trackers"
              className="flex justify-around flex-col select-all fill-background-50"
              state={{ scrollTo: 'steamvr' }}
            >
              <GearIcon />
            </NavLink>

            {!isMobile && (
              <div
                className={classNames(
                  'flex items-center justify-center stroke-window-icon',
                  'hover:bg-background-60 rounded-full w-7 h-7 cursor-pointer'
                )}
                onClick={() => openUrl(DOCS_SITE)}
              >
                <QuestionIcon />
              </div>
            )}
            {electron.isElectron && (
              <>
                <div
                  className="flex items-center justify-center hover:bg-background-60 rounded-full w-7 h-7"
                  onClick={() => electron.api.minimize()}
                >
                  <MinimiseIcon />
                </div>
                <div
                  className="flex items-center justify-center hover:bg-background-60 rounded-full w-7 h-7"
                  onClick={() => electron.api.maximize()}
                >
                  <MaximiseIcon />
                </div>
                <div
                  className="flex items-center justify-center hover:bg-background-60 rounded-full w-7 h-7"
                  onClick={() => tryCloseApp()}
                >
                  <CloseIcon />
                </div>
              </>
            )}
          </div>
        </div>
        {isMobile && progress !== undefined && (
          <div className="flex gap-2 px-2 h-6 mb-2 justify-center flex-col border-b border-accent-background-30">
            <ProgressBar progress={progress} height={3} parts={3} />
          </div>
        )}
      </div>
      {electron.isElectron && (
        <TrayOrExitModal
          isOpen={showTrayOrExitModal}
          accept={async (useTray) => {
            await setConfig({ useTray });
            setShowTrayOrExitModal(false);

            // Doing this in here just in case config doesn't get updated in time
            if (useTray) {
              electron.api.minimize();
              // await invoke('update_tray_text');
            } else if (
              config?.connectedTrackersWarning &&
              connectedIMUTrackers.filter(
                (t) => t.tracker.status !== TrackerStatus.TIMED_OUT
              ).length > 0
            ) {
              setConnectedTrackerWarning(true);
            } else {
              await closeApp();
            }
          }}
          cancel={() => setShowTrayOrExitModal(false)}
        />
      )}
      <TrackersStillOnModal
        isOpen={showConnectedTrackersWarning}
        accept={() => closeApp()}
        cancel={() => {
          setConnectedTrackerWarning(false);
        }}
      />
      <ErrorConsentModal
        isOpen={config?.errorTracking === false}
        accept={() => setConfig({ errorTracking: true })}
        cancel={() => setConfig({ errorTracking: false })}
      />
      <UdevRulesModal />
    </>
  );
}
