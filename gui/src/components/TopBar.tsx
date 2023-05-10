import { appWindow } from '@tauri-apps/api/window';
import { ReactNode, useEffect, useState } from 'react';
import { NavLink, useMatch } from 'react-router-dom';
import {
  RpcMessage,
  ServerInfosRequestT,
  ServerInfosResponseT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '../hooks/websocket-api';
import { CloseIcon } from './commons/icon/CloseIcon';
import { MaximiseIcon } from './commons/icon/MaximiseIcon';
import { MinimiseIcon } from './commons/icon/MinimiseIcon';
import { SlimeVRIcon } from './commons/icon/SimevrIcon';
import { ProgressBar } from './commons/ProgressBar';
import { Typography } from './commons/Typography';
import { DownloadIcon } from './commons/icon/DownloadIcon';
import { open } from '@tauri-apps/api/shell';
import { GH_REPO } from '../App';
import classNames from 'classnames';

export function TopBar({
  progress,
  version,
}: {
  children?: ReactNode;
  progress?: number;
  /**
   * If not falsy, we will make an update icon appear
   */
  version?: string;
}) {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
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
    <div data-tauri-drag-region className="flex gap-2 h-[38px] z-50">
      <div
        className="flex px-2 pb-1 mt-3 justify-around z-50"
        data-tauri-drag-region
      >
        <div className="flex gap-2" data-tauri-drag-region>
          <NavLink
            to="/"
            className="flex justify-around flex-col select-all"
            data-tauri-drag-region
          >
            <SlimeVRIcon></SlimeVRIcon>
          </NavLink>
          <div className="flex justify-around flex-col" data-tauri-drag-region>
            <Typography>SlimeVR</Typography>
          </div>
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
            {(__VERSION_TAG__ || __COMMIT_HASH__) +
              (__GIT_CLEAN__ ? '' : '-dirty')}
          </div>
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
        <div
          className="flex max-w-xl h-full items-center w-full"
          data-tauri-drag-region
        >
          {progress !== undefined && (
            <ProgressBar progress={progress} height={3} parts={3}></ProgressBar>
          )}
        </div>
      </div>
      <div
        className="flex justify-end items-center px-2 gap-2 z-50"
        data-tauri-drag-region
      >
        <div
          className="flex items-center justify-center hover:bg-background-60 rounded-full w-7 h-7"
          onClick={() => appWindow.minimize()}
        >
          <MinimiseIcon></MinimiseIcon>
        </div>
        <div
          className="flex items-center justify-center hover:bg-background-60 rounded-full w-7 h-7"
          onClick={() => appWindow.toggleMaximize()}
        >
          <MaximiseIcon></MaximiseIcon>
        </div>
        <div
          className="flex items-center justify-center hover:bg-background-60 rounded-full w-7 h-7"
          onClick={() => appWindow.close()}
        >
          <CloseIcon></CloseIcon>
        </div>
      </div>
    </div>
  );
}
