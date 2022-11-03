import { ReactNode } from 'react';
import { ResetType } from 'solarxr-protocol';
import { useLayout } from '../hooks/layout';
import { BVHButton } from './BVHButton';
import { Navbar } from './Navbar';
import { ResetButton } from './home/ResetButton';
import { TopBar } from './TopBar';
import classNames from 'classnames';
import { OverlayWidget } from './widgets/OverlayWidget';

export function MainLayoutRoute({
  children,
  background = true,
  widgets = true,
}: {
  children: ReactNode;
  background?: boolean;
  widgets?: boolean;
}) {
  const { layoutHeight, ref } = useLayout<HTMLDivElement>();
  const { layoutWidth, ref: refw } = useLayout<HTMLDivElement>();

  return (
    <>
      <TopBar></TopBar>
      <div ref={ref} className="flex-grow" style={{ height: layoutHeight }}>
        <div className="flex h-full pb-3">
          <Navbar></Navbar>
          <div
            className="flex gap-2 pr-3 w-full"
            ref={refw}
            style={{ minWidth: layoutWidth }}
          >
            <div
              className={classNames(
                'flex flex-col rounded-xl w-full overflow-hidden',
                background && 'bg-background-70'
              )}
            >
              {children}
            </div>
            {widgets && (
              <div className="flex flex-col px-2 min-w-[274px] w-[274px] gap-2 pt-2 rounded-xl overflow-y-auto bg-background-70">
                <div className="grid grid-cols-3 gap-2 w-full">
                  <ResetButton type={ResetType.Quick}></ResetButton>
                  <ResetButton type={ResetType.Full}></ResetButton>
                  <ResetButton type={ResetType.Mounting}></ResetButton>
                  <BVHButton></BVHButton>
                </div>
                <div className="w-full">
                  <OverlayWidget></OverlayWidget>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
