import { ReactChild } from 'react';
import { useLayout } from '../../hooks/layout';
import { Navbar } from '../Navbar';
import { TopBar } from '../TopBar';
import { SettingsSidebar } from './SettingsSidebar';

export function SettingsLayoutRoute({ children }: { children: ReactChild }) {
  const { layoutHeight, ref } = useLayout<HTMLDivElement>();

  return (
    <>
      <TopBar></TopBar>
      <div ref={ref} className="flex-grow" style={{ height: layoutHeight }}>
        <div className="flex h-full pb-3">
          <Navbar></Navbar>
          <div className="h-full w-full gap-2 flex">
            <SettingsSidebar></SettingsSidebar>
            <div className="w-full flex flex-col overflow-y-auto pr-1 mr-1">
              {children}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
