import { useSessionFlightlist } from '@/hooks/session-flightlist';
import { SessionFlightList } from './flight-list/SessionFlightList';
import { SkeletonVisualizerWidget } from './widgets/SkeletonVisualizerWidget';
import { useEffect, useLayoutEffect, useState } from 'react';
import classNames from 'classnames';

export function Sidebar() {
  const { completion } = useSessionFlightlist();
  const [closed, setClosed] = useState(true);
  const [closing, setClosing] = useState(false);

  const closedHight = '90px';
  const flightlistSize = closed ? closedHight : 'calc(100% - 16px)';
  const previewSize = closed ? `calc(100% - ${closedHight} - 24px)` : '0%';

  const toggleClosed = () => setClosed((closed) => !closed);

  useLayoutEffect(() => {
    setClosing(true);
    const ref = setTimeout(() => setClosing(false), 1000);
    return () => {
      clearTimeout(ref);
      setClosing(false);
    };
  }, [closed]);

  useEffect(() => {
    if (completion === 'complete') {
      setClosed(true);
    } else {
      setClosed(false);
    }
  }, [completion]);

  return (
    <>
      <div
        className="transition-[height] duration-500 rounded-lg my-2 bg-background-70 overflow-clip"
        style={{ height: flightlistSize }}
      >
        <SessionFlightList
          closed={closed}
          closing={closing}
          toggleClosed={toggleClosed}
        ></SessionFlightList>
      </div>
      <div
        className="transition-[height] duration-500 rounded-lg my-2 bg-background-70 overflow-clip"
        style={{ height: previewSize }}
      >
        <div
          className={classNames(
            'transition-opacity duration-500 delay-500 h-full',
            {
              'opacity-0': !closed,
              'opacity-100': closed,
            }
          )}
        >
          <SkeletonVisualizerWidget></SkeletonVisualizerWidget>
        </div>
      </div>
    </>
  );
}
