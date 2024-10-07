import classNames from 'classnames';
import { ReactNode, useEffect, useState } from 'react';
import {
  LegTweaksTmpChangeT,
  LegTweaksTmpClearT,
  RpcMessage,
  SettingsRequestT,
} from 'solarxr-protocol';
import { Navbar } from './Navbar';
import { TopBar } from './TopBar';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { WidgetsComponent } from './WidgetsComponent';
import './MainLayout.scss';

export function MainLayout({
  children,
  background = true,
  widgets = true,
  isMobile = undefined,
}: {
  children: ReactNode;
  background?: boolean;
  isMobile?: boolean;
  widgets?: boolean;
}) {
  const { sendRPCPacket } = useWebsocketAPI();
  const [ProportionsLastPageOpen, setProportionsLastPageOpen] = useState(true);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  function usePageChanged(callback: () => void) {
    useEffect(() => {
      callback();
    }, [location.pathname]);
  }

  usePageChanged(() => {
    if (location.pathname.includes('body-proportions')) {
      const tempSettings = new LegTweaksTmpChangeT();
      tempSettings.skatingCorrection = false;
      tempSettings.floorClip = false;
      tempSettings.toeSnap = false;
      tempSettings.footPlant = false;

      sendRPCPacket(RpcMessage.LegTweaksTmpChange, tempSettings);
    } else if (ProportionsLastPageOpen) {
      const resetSettings = new LegTweaksTmpClearT();
      resetSettings.skatingCorrection = true;
      resetSettings.floorClip = true;
      resetSettings.toeSnap = true;
      resetSettings.footPlant = true;

      sendRPCPacket(RpcMessage.LegTweaksTmpClear, resetSettings);
    }
    setProportionsLastPageOpen(location.pathname.includes('body-proportions'));
  });

  return (
    <div className="">
      <div className="main-layout w-full h-screen">
        <div style={{ gridArea: 't' }}>
          <TopBar></TopBar>
        </div>
        <div style={{ gridArea: 's' }} className="overflow-y-auto">
          <Navbar></Navbar>
        </div>
        <div
          style={{ gridArea: 'c' }}
          className={classNames(
            'overflow-y-auto mr-2 my-2 mobile:m-0',
            'flex flex-col rounded-xl',
            background && 'bg-background-70'
          )}
        >
          {children}
        </div>
        {!isMobile && widgets && (
          <div
            style={{ gridArea: 'w' }}
            className="overflow-y-auto mr-2 my-2 rounded-xl bg-background-70 flex flex-col gap-2 p-2 widgets"
          >
            <WidgetsComponent></WidgetsComponent>
          </div>
        )}
      </div>
    </div>
  );
}
