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
import './MainLayout.scss';
import { Toolbar } from './Toolbar';
import { SkeletonVisualizerWidget } from './widgets/SkeletonVisualizerWidget';
import { SessionFlightList } from './SessionFlightList';

export function MainLayout({
  children,
  background = true,
  full = false,
  isMobile = undefined,
}: {
  children: ReactNode;
  background?: boolean;
  isMobile?: boolean;
  full?: boolean;
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
    <div
      className={classNames(
        'main-layout w-full h-screen',
        !isMobile && full && 'full'
      )}
    >
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
          'flex flex-col rounded-md',
          background && 'bg-background-70'
        )}
      >
        {children}
      </div>
      {!isMobile && full && (
        <>
          <div style={{ gridArea: 'r' }}>
            <Toolbar></Toolbar>
          </div>
          <div
            style={{ gridArea: 'l' }}
            className="overflow-y-auto mr-2 my-2 rounded-md bg-background-70 flex flex-col gap-2 p-2"
          >
            <SessionFlightList></SessionFlightList>
          </div>
          <div
            style={{ gridArea: 'p' }}
            className="overflow-y-auto mr-2 mb-2 rounded-md bg-background-70 flex flex-col"
          >
            {/* <WidgetsComponent></WidgetsComponent> */}
            <SkeletonVisualizerWidget />
          </div>
        </>
      )}
    </div>
  );
}
