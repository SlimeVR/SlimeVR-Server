import { useSessionFlightlist } from '@/hooks/session-flightlist';
import { SessionFlightList } from './flight-list/SessionFlightList';
import { SkeletonVisualizerWidget } from './widgets/SkeletonVisualizerWidget';
import { useEffect, useLayoutEffect, useMemo, useState } from 'react';
import classNames from 'classnames';
import { Typography } from './commons/Typography';
import { useLocaleConfig } from '@/i18n/config';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import {
  RpcMessage,
  SkeletonConfigRequestT,
  SkeletonConfigResponseT,
} from 'solarxr-protocol';
import { Tooltip } from './commons/Tooltip';
import { Vector3 } from 'three';
import { RecordIcon } from './commons/icon/RecordIcon';
import { PauseIcon } from './commons/icon/PauseIcon';
import { HumanIcon } from './commons/icon/HumanIcon';
import { EyeIcon } from './commons/icon/EyeIcon';

export function Sidebar() {
  const { completion } = useSessionFlightlist();
  const [closed, setClosed] = useState(true);
  const [closing, setClosing] = useState(false);
  const [userHeight, setUserHeight] = useState('');

  const { currentLocales } = useLocaleConfig();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();

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
    } else if (completion === 'incomplete') {
      setClosed(false);
    }
  }, [completion]);

  const { cmFormat } = useMemo(() => {
    const cmFormat = Intl.NumberFormat(currentLocales, {
      style: 'unit',
      unit: 'centimeter',
      maximumFractionDigits: 1,
    });
    return { cmFormat };
  }, [currentLocales]);

  useRPCPacket(
    RpcMessage.SkeletonConfigResponse,
    (data: SkeletonConfigResponseT) => {
      if (data.userHeight)
        setUserHeight(cmFormat.format((data.userHeight * 100) / 0.936));
    }
  );

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.SkeletonConfigRequest,
      new SkeletonConfigRequestT()
    );
  }, []);

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
            'transition-opacity duration-500 delay-500 h-full relative',
            {
              'opacity-0': !closed,
              'opacity-100': closed,
            }
          )}
        >
          <Tooltip
            preferedDirection="bottom"
            content={
              <Typography id="onboarding-manual_proportions-estimated_height"></Typography>
            }
          >
            <div
              className={classNames(
                'h-10 bg-background-60 p-4 flex items-center rounded-lg justify-center cursor-help w-fit top-2 left-2 absolute',
                {
                  'opacity-0': !closed,
                  'opacity-100': closed,
                }
              )}
            >
              <Typography variant="section-title">{userHeight}</Typography>
            </div>
          </Tooltip>
          <Tooltip
            preferedDirection="bottom"
            content={<Typography>Disable rendering</Typography>}
          >
            <div className="flex justify-center items-center w-10 h-10 cursor-pointer rounded-full fill-background-10 absolute right-2 top-2 bg-background-60 hover:bg-background-50">
              <EyeIcon width={18} closed></EyeIcon>
            </div>
          </Tooltip>
          {/* <Tooltip
            preferedDirection="bottom"
            content={<Typography>Enable Rendering</Typography>}
          >
            <div className="flex justify-center items-center w-12 h-12 rounded-full fill-background-10 absolute right-2 top-2 bg-background-60 hover:bg-background-50">
              <SpeedIcon size={24}></SpeedIcon>
            </div>
          </Tooltip> */}
          <div className="absolute bottom-0 pb-4 flex justify-center w-full">
            <div className="flex bg-background-80 bg-opacity-70 rounded-lg gap-2 px-4 py-2 items-center fill-background-10">
              <Tooltip
                content={
                  <Typography variant="section-title">Record BVH</Typography>
                }
                preferedDirection="top"
              >
                <div className="flex justify-center items-center w-10 h-10 rounded-full hover:bg-background-60 cursor-pointer">
                  <RecordIcon width={20}></RecordIcon>
                </div>
              </Tooltip>
              <Tooltip
                content={
                  <Typography variant="section-title">
                    Pause tracking
                  </Typography>
                }
                preferedDirection="top"
              >
                <div className="flex justify-center items-center w-14 h-14 rounded-full bg-background-60 hover:bg-background-50 cursor-pointer">
                  <PauseIcon width={25}></PauseIcon>
                </div>
              </Tooltip>
              <Tooltip
                content={
                  <Typography variant="section-title">Mocap Mode</Typography>
                }
                preferedDirection="top"
              >
                <div className="flex justify-center items-center w-10 h-10 rounded-full hover:bg-background-60 cursor-pointer">
                  <HumanIcon width={20}></HumanIcon>
                </div>
              </Tooltip>
            </div>
          </div>
          <SkeletonVisualizerWidget
            onInit={(context) => {
              context.addView({
                left: 0,
                bottom: 0,
                width: 1,
                height: 1,
                position: new Vector3(3, 2.5, -3),
                onHeightChange(v, newHeight) {
                  v.controls.target.set(0, newHeight / 2.2, 0.1);
                  const scale = Math.max(1, newHeight) / 1.3;
                  v.camera.zoom = 1 / scale;
                },
              });
            }}
          ></SkeletonVisualizerWidget>
        </div>
      </div>
    </>
  );
}
