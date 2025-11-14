import { useTrackingChecklist } from '@/hooks/tracking-checklist';
import { TrackingChecklist } from './tracking-checklist/TrackingChecklist';
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
import { useConfig } from '@/hooks/config';
import { useBHV } from '@/hooks/bvh';
import { usePauseTracking } from '@/hooks/pause-tracking';
import { PlayIcon } from './commons/icon/PlayIcon';

export function PreviewControls({ open }: { open: boolean }) {
  const [userHeight, setUserHeight] = useState('');
  const { currentLocales } = useLocaleConfig();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();

  const {
    state: bvhState,
    toggle: toggleBVH,
    available: bvhAvailable,
  } = useBHV();
  const { paused, toggle: toggleTracking } = usePauseTracking();

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
      <Tooltip
        preferedDirection="bottom"
        content={
          <Typography id="onboarding-manual_proportions-estimated_height" />
        }
      >
        <div
          className={classNames(
            'h-10 bg-background-60 p-4 flex items-center rounded-lg justify-center cursor-help w-fit top-2 left-2 absolute',
            {
              'opacity-0': !open,
              'opacity-100': open,
            }
          )}
        >
          <Typography variant="section-title">{userHeight}</Typography>
        </div>
      </Tooltip>
      <div className="absolute bottom-0 pb-4 flex justify-center w-full">
        <div className="flex bg-background-80 bg-opacity-70 rounded-lg gap-2 px-4 py-2 items-center fill-background-10">
          {bvhAvailable && (
            <Tooltip
              content={
                <Typography
                  variant="section-title"
                  id={
                    bvhState === 'idle'
                      ? 'bvh-start_recording'
                      : 'bvh-stop_recording'
                  }
                />
              }
              preferedDirection="top"
            >
              <div
                className={classNames(
                  'flex justify-center items-center w-10 h-10 rounded-full hover:bg-background-60 cursor-pointer',
                  { 'bg-background-60': bvhState !== 'idle' }
                )}
                onClick={() => toggleBVH()}
              >
                {bvhState === 'idle' && <RecordIcon width={20} />}
                {bvhState !== 'idle' && (
                  <div className="w-5 h-5 rounded-full bg-status-critical animate-pulse" />
                )}
              </div>
            </Tooltip>
          )}
          <Tooltip
            content={
              <Typography
                variant="section-title"
                id={paused ? 'tracking-paused' : 'tracking-unpaused'}
              />
            }
            preferedDirection="top"
          >
            <div
              className="flex justify-center items-center w-14 h-14 rounded-full bg-background-60 hover:bg-background-50 cursor-pointer"
              onClick={() => toggleTracking()}
            >
              {!paused && <PauseIcon width={25} />}
              {paused && <PlayIcon width={25} />}
            </div>
          </Tooltip>
          <Tooltip
            content={
              <Typography
                variant="section-title"
                id="preview-mocap_mode_soon"
              />
            }
            preferedDirection="top"
          >
            <div className="flex justify-center items-center w-10 h-10 rounded-full cursor-not-allowed">
              <HumanIcon width={20} />
            </div>
          </Tooltip>
        </div>
      </div>
    </>
  );
}

function PreviewSection({ open }: { open: boolean }) {
  const { config, setConfig } = useConfig();
  const [disabledRender, setDisabledRender] = useState(config?.skeletonPreview);

  const toggleRender = () => {
    setConfig({ skeletonPreview: disabledRender });
  };

  useLayoutEffect(() => {
    // need useLayoutEffect to make sure that the state is corect before the first render of the skeleton
    setDisabledRender(!config?.skeletonPreview);
  }, [config]);

  return (
    <div
      className={classNames(
        'transition-opacity duration-500 delay-500 h-full relative',
        {
          'opacity-0': !open,
          'opacity-100': open,
        }
      )}
    >
      <SkeletonVisualizerWidget
        disabled={disabledRender}
        toggleDisabled={() => toggleRender()}
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
      />
      <Tooltip
        preferedDirection="bottom"
        content={<Typography id="preview-disable_render" />}
      >
        <div
          className="flex justify-center items-center w-10 h-10 cursor-pointer rounded-full fill-background-10 absolute right-2 top-2 bg-background-60 hover:bg-background-50"
          onClick={() => toggleRender()}
        >
          <EyeIcon width={18} closed={!disabledRender} />
        </div>
      </Tooltip>
      <PreviewControls open={open} />
    </div>
  );
}

export function Sidebar() {
  const { completion } = useTrackingChecklist();
  const [closed, setClosed] = useState(true);
  const [closing, setClosing] = useState(false);

  const closedHight = '90px';
  const checklistSize = closed ? closedHight : 'calc(100% - 16px)';
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

  return (
    <>
      <div
        className="transition-[height] duration-500 rounded-lg my-2 bg-background-70 overflow-clip"
        style={{ height: checklistSize }}
      >
        <TrackingChecklist
          closed={closed}
          closing={closing}
          toggleClosed={toggleClosed}
        />
      </div>
      <div
        className="transition-[height] duration-500 rounded-lg my-2 bg-background-70 overflow-clip"
        style={{ height: previewSize }}
      >
        <PreviewSection open={closed} />
      </div>
    </>
  );
}
