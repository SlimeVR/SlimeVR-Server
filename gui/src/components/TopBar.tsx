import { getCurrent } from '@tauri-apps/plugin-window';
import { ReactNode, useContext, useEffect, useState } from 'react';
import { NavLink, useMatch } from 'react-router-dom';
import {
  RpcMessage,
  ServerInfosRequestT,
  ServerInfosResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CloseIcon } from './commons/icon/CloseIcon';
import { MaximiseIcon } from './commons/icon/MaximiseIcon';
import { MinimiseIcon } from './commons/icon/MinimiseIcon';
import { SlimeVRIcon } from './commons/icon/SimevrIcon';
import { ProgressBar } from './commons/ProgressBar';
import { Typography } from './commons/Typography';
import { DownloadIcon } from './commons/icon/DownloadIcon';
import { open } from '@tauri-apps/plugin-shell';
import { GH_REPO, VersionContext, DOCS_SITE } from '@/App';
import classNames from 'classnames';
import { QuestionIcon } from './commons/icon/QuestionIcon';
import { useBreakpoint, useIsTauri } from '@/hooks/breakpoint';
import { GearIcon } from './commons/icon/GearIcon';
import { invoke } from '@tauri-apps/api';

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
        open(url).catch(() => window.open(url, '_blank'));
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
  const isTauri = useIsTauri();
  const { isMobile } = useBreakpoint('mobile');
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const version = useContext(VersionContext);
  const [localIp, setLocalIp] = useState<string | null>(null);
  const doesMatchSettings = useMatch({
    path: '/settings/*',
  });

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
        <div className="h-[3px]"></div>
        <div data-tauri-drag-region className="flex gap-2 h-[38px] z-50">
          <div
            className="flex px-2 pb-1 mt-3 justify-around z-50"
            data-tauri-drag-region
          >
            <div className="flex gap-2 mobile:w-5" data-tauri-drag-region>
              <NavLink
                to="/"
                className="flex justify-around flex-col select-all"
                data-tauri-drag-region
              >
                <SlimeVRIcon></SlimeVRIcon>
              </NavLink>
              {(isTauri || !isMobile) && (
                <div
                  className={classNames('flex justify-around flex-col')}
                  data-tauri-drag-region
                >
                  <Typography>SlimeVR</Typography>
                </div>
              )}
              {!isMobile && (
                <>
                  <VersionTag></VersionTag>
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
                </>
              )}

              {version && (
                <div
                  className="cursor-pointer"
                  onClick={() => {
                    const url = document.body.classList.contains('windows_nt')
                      ? 'https://slimevr.dev/download'
                      : `https://github.com/${GH_REPO}/releases/latest`;
                    open(url).catch(() => window.open(url, '_blank'));
                  }}
                >
                  <DownloadIcon></DownloadIcon>
                </div>
              )}
            </div>
          </div>
          <div
            className="flex flex-grow items-center h-full justify-center z-50"
            data-tauri-drag-region
          >
            {!isMobile && (
              <>
                <div
                  className="flex max-w-xl h-full items-center w-full"
                  data-tauri-drag-region
                >
                  {progress !== undefined && (
                    <ProgressBar
                      progress={progress}
                      height={3}
                      parts={3}
                    ></ProgressBar>
                  )}
                </div>
              </>
            )}

            {!isTauri && (
              <div className="flex flex-row gap-2">
                <div
                  className="flex justify-around flex-col xs:hidden"
                  data-tauri-drag-region
                >
                  <Typography variant="section-title">SlimeVR</Typography>
                </div>
              </div>
            )}
          </div>
          <div
            className="flex justify-end items-center px-2 gap-2 z-50"
            data-tauri-drag-region
          >
            <NavLink
              to="/settings/trackers"
              className="flex justify-around flex-col select-all fill-background-50"
              data-tauri-drag-region
              state={{ scrollTo: 'steamvr' }}
            >
              <GearIcon></GearIcon>
            </NavLink>

            {!isMobile && (
              <div
                className={classNames(
                  'flex items-center justify-center stroke-window-icon',
                  'hover:bg-background-60 rounded-full w-7 h-7 cursor-pointer'
                )}
                onClick={() =>
                  open(DOCS_SITE).catch(() => window.open(DOCS_SITE, '_blank'))
                }
              >
                <QuestionIcon></QuestionIcon>
              </div>
            )}

            {isTauri && (
              <>
                <div
                  className="flex items-center justify-center hover:bg-background-60 rounded-full w-7 h-7"
                  onClick={() => getCurrent().minimize()}
                >
                  <MinimiseIcon></MinimiseIcon>
                </div>
                <div
                  className="flex items-center justify-center hover:bg-background-60 rounded-full w-7 h-7"
                  onClick={() => getCurrent().toggleMaximize()}
                >
                  <MaximiseIcon></MaximiseIcon>
                </div>
                <div
                  className="flex items-center justify-center hover:bg-background-60 rounded-full w-7 h-7"
                  onClick={async () => {
                    await invoke('update_window_state');
                    getCurrent().close();
                  }}
                >
                  <CloseIcon></CloseIcon>
                </div>
              </>
            )}
          </div>
        </div>
        {isMobile && progress !== undefined && (
          <div className="flex gap-2 px-2 h-6 mb-2 justify-center flex-col border-b border-accent-background-30">
            <ProgressBar progress={progress} height={3} parts={3}></ProgressBar>
          </div>
        )}
      </div>
    </>
  );
}
