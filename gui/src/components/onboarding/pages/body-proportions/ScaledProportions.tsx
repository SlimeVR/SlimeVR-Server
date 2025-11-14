import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import {
  DEFAULT_FULL_HEIGHT,
  EYE_HEIGHT_TO_HEIGHT_RATIO,
  HeightContextC,
  useHeightContext,
  useProvideHeightContext,
} from '@/hooks/height';
import { TipBox } from '@/components/commons/TipBox';
import { useEffect, useMemo, useState } from 'react';
import { useAtomValue } from 'jotai';
import { flatTrackersAtom } from '@/store/app-store';
import {
  BodyPart,
  ChangeSettingsRequestT,
  HeightRequestT,
  HeightResponseT,
  ModelSettingsT,
  RpcMessage,
  SkeletonHeightT,
  SkeletonResetAllRequestT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocaleConfig } from '@/i18n/config';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import { Vector3 } from 'three';
import convert from 'convert';
import classNames from 'classnames';
import { Button } from '@/components/commons/Button';
import { useBreakpoint } from '@/hooks/breakpoint';
import { Tooltip } from '@/components/commons/Tooltip';
import { Localized } from '@fluent/react';

function IncrementButton({
  value,
  unit,
  disabled = false,
  onClick,
}: {
  value: number;
  disabled?: boolean;
  unit: 'ft' | 'inch' | 'cm';
  onClick: () => void;
}) {
  const { isMd } = useBreakpoint('md');

  return (
    <div
      className={classNames(
        'flex md:aspect-square rounded-md items-center justify-center flex-row md:flex-col  w-fit gap-1 p-3 md:p-2 md:w-auto',
        {
          'cursor-not-allowed bg-background-80 opacity-50': disabled,
          'bg-background-60 hover:bg-background-50 cursor-pointer': !disabled,
        }
      )}
      onClick={() => !disabled && onClick()}
    >
      <Typography
        variant={isMd ? 'main-title' : 'section-title'}
        color={disabled ? 'text-background-40' : 'primary'}
      >
        {value > 0 ? `+${value}` : value}
      </Typography>
      <Typography
        id={`unit-${unit}`}
        color={disabled ? 'text-background-40' : 'primary'}
       />
    </div>
  );
}

function UnitSelector({
  name,
  active,
  onClick,
}: {
  name: string;
  active: boolean;
  onClick: () => void;
}) {
  const { isSm } = useBreakpoint('sm');

  return (
    <div
      className={classNames(
        {
          'bg-accent-background-30': active,
          'hover:bg-background-40 bg-background-50': !active,
        },
        'flex items-center justify-center rounded-md outline-background-10 cursor-pointer'
      )}
      onClick={onClick}
    >
      <Typography
        variant={isSm ? 'standard' : 'section-title'}
        id={name}
       />
    </div>
  );
}

function formatHeightWithIntl(meters: number, locale: string[]) {
  const totalInches = convert(meters, 'meter').to('inch');
  const feet = Math.trunc(totalInches / 12);
  const inches = Math.round(totalInches % 12);

  const feetFormatter = new Intl.NumberFormat(locale, {
    style: 'unit',
    unit: 'foot',
    unitDisplay: 'narrow',
    maximumFractionDigits: 0,
  });

  const inchFormatter = new Intl.NumberFormat(locale, {
    style: 'unit',
    unit: 'inch',
    unitDisplay: 'narrow',
    maximumFractionDigits: 0,
  });

  return `${feetFormatter.format(feet)} ${inchFormatter.format(inches)}`;
}

const roundHeight = (value: number): number => Math.round(value * 1000) / 1000;

function HeightSelectionInput({ auto }: { auto: boolean }) {
  const { sendRPCPacket } = useWebsocketAPI();
  const [unit, setUnit] = useState<'meter' | 'foot'>('meter');
  const { currentLocales } = useLocaleConfig();
  const { currentHeight, setHmdHeight } = useHeightContext();

  const formatedHeight = useMemo(() => {
    const fullHeight = roundHeight(
      (currentHeight && currentHeight / EYE_HEIGHT_TO_HEIGHT_RATIO) ||
        DEFAULT_FULL_HEIGHT
    );
    if (unit === 'meter')
      return new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'meter',
        maximumFractionDigits: 2,
        minimumFractionDigits: 2,
      }).format(fullHeight);
    else return formatHeightWithIntl(fullHeight, currentLocales);
  }, [currentHeight, unit]);

  const increment = (unit: 'inch' | 'cm', value: number) => {
    const headsetHeight = roundHeight(
      ((currentHeight && currentHeight / EYE_HEIGHT_TO_HEIGHT_RATIO) ||
        DEFAULT_FULL_HEIGHT) * EYE_HEIGHT_TO_HEIGHT_RATIO
    );

    const newValue = headsetHeight + convert(value, unit).to('cm') / 100;
    setHmdHeight(newValue);
    const settingsRequest = new ChangeSettingsRequestT();
    settingsRequest.modelSettings = new ModelSettingsT(
      null,
      null,
      null,
      new SkeletonHeightT(newValue, 0)
    );
    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settingsRequest);

    sendRPCPacket(
      RpcMessage.SkeletonResetAllRequest,
      new SkeletonResetAllRequestT()
    );
  };

  const minimalHeight = (currentHeight ?? 0) <= 0.81;
  const minimalHeight10cm = (currentHeight ?? 0) <= 0.91;

  return (
    <div className="flex gap-2 md:h-[75px] w-full flex-col md:flex-row items-center">
      <div className="flex gap-2 h-full">
        {unit === 'foot' && (
          <>
            <div className="aspect-square bg-background-80 opacity-50 rounded-md items-center justify-center flex-col hidden md:flex" />
            <IncrementButton
              value={-1}
              unit={'inch'}
              onClick={() => increment('inch', -1)}
              disabled={auto || minimalHeight}
             />
          </>
        )}
        {unit === 'meter' && (
          <>
            <IncrementButton
              value={-10}
              unit={'cm'}
              onClick={() => increment('cm', -10)}
              disabled={auto || minimalHeight10cm}
             />
            <IncrementButton
              value={-1}
              unit={'cm'}
              onClick={() => increment('cm', -1)}
              disabled={auto || minimalHeight}
             />
          </>
        )}
      </div>
      <div className="flex w-full md:w-auto md:flex-grow bg-background-60 rounded-md px-2 py-2 h-full">
        <div className="h-full flex items-center flex-grow justify-center min-w-24">
          <Typography variant="main-title">{formatedHeight}</Typography>
        </div>
        <div className="w-28 md:w-20 h-full gap-2 grid grid-rows-1 grid-cols-2 md:grid-rows-2 md:grid-cols-1 p-1">
          <UnitSelector
            active={unit === 'meter'}
            name="unit-meter"
            onClick={() => setUnit('meter')}
           />
          <UnitSelector
            active={unit === 'foot'}
            name="unit-foot"
            onClick={() => setUnit('foot')}
           />
        </div>
      </div>
      <div className="flex gap-2 h-full">
        {unit === 'foot' && (
          <>
            <IncrementButton
              value={1}
              unit={'inch'}
              onClick={() => increment('inch', 1)}
              disabled={auto}
             />
            <div className="aspect-square bg-background-80 opacity-50 rounded-md items-center justify-center flex-col hidden md:flex" />
          </>
        )}
        {unit === 'meter' && (
          <>
            <IncrementButton
              value={1}
              unit={'cm'}
              onClick={() => increment('cm', 1)}
              disabled={auto}
             />
            <IncrementButton
              value={10}
              unit={'cm'}
              onClick={() => increment('cm', 10)}
              disabled={auto}
             />
          </>
        )}
      </div>
    </div>
  );
}

export function ScaledProportionsPage() {
  const [auto, setAuto] = useState<false | 'height' | 'floor'>(false);
  const { applyProgress, state } = useOnboarding();
  const heightContext = useProvideHeightContext();
  const trackers = useAtomValue(flatTrackersAtom);

  const { hasHmd, hasHandControllers } = useMemo(() => {
    const hasHmd = trackers.some(
      (tracker) =>
        tracker.tracker.info?.bodyPart === BodyPart.HEAD &&
        (tracker.tracker.info.isHmd || tracker.tracker.position?.y)
    );
    const hasHandControllers =
      trackers.filter(
        (tracker) =>
          tracker.tracker.info?.bodyPart === BodyPart.LEFT_HAND ||
          tracker.tracker.info?.bodyPart === BodyPart.RIGHT_HAND
      ).length >= 2;

    return { hasHmd, hasHandControllers };
  }, [trackers]);

  const canDoAuto = hasHmd && hasHandControllers;

  applyProgress(0.9);

  const { isXs } = useBreakpoint('xs');

  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  useEffect(() => {
    if (auto) {
      sendRPCPacket(RpcMessage.HeightRequest, new HeightRequestT());
    }
  }, [auto]);

  useRPCPacket(RpcMessage.HeightResponse, ({ maxHeight }: HeightResponseT) => {
    heightContext.setHmdHeight((val) =>
      val === null ? maxHeight : Math.max(maxHeight, val)
    );
  });

  return (
    <HeightContextC.Provider value={heightContext}>
      <div className="h-full w-full flex gap-2 mobile:flex-col bg-background-80">
        <div className="bg-background-70 rounded-md flex-grow overflow-y-auto">
          <div className="flex flex-col gap-4 xs:p-8 p-4 flex-grow h-full">
            <div className="flex flex-col gap-2">
              <Typography variant="main-title">Body Proportions</Typography>
              <div className="max-w-xl">
                <Typography>
                  SlimeVR needs to know your body proportions to function. This
                  is a crucial step to get accurate tracking! Fortunately you
                  only need to do this once per user!
                </Typography>
              </div>
            </div>
            <div className="flex flex-grow flex-col gap-4 items-center justify-center">
              <div className="flex w-full flex-col xs:items-center gap-2 xs:max-w-2xl">
                {!auto && (
                  <Typography variant={isXs ? 'main-title' : 'section-title'}>
                    What is your Height?
                  </Typography>
                )}
                {auto === 'height' && (
                  <Typography variant={isXs ? 'main-title' : 'section-title'}>
                    Is this your Height?
                  </Typography>
                )}
                {auto === 'floor' && (
                  <Typography variant={isXs ? 'main-title' : 'section-title'}>
                    And now? Is your height correct?
                  </Typography>
                )}
                <HeightSelectionInput
                  auto={auto !== false}
                 />
              </div>
              {auto && (
                <>
                  <div className="grid grid-cols-2 gap-4">
                    <Button
                      variant="secondary"
                      onClick={() => setAuto('floor')}
                    >
                      <Typography variant="main-title">No</Typography>
                    </Button>
                    <Button variant="primary" onClick={() => setAuto(false)}>
                      <Typography variant="main-title">Yes</Typography>
                    </Button>
                  </div>
                  <div className="">
                    <TipBox>
                      The calculated height is an estimation based on the
                      highest recorded position of your headset.
                    </TipBox>
                  </div>
                </>
              )}
            </div>
            <div className="flex gap-2 justify-between w-full">
              {state.alonePage && (
                <Button
                  variant="secondary"
                  to="/onboarding/body-proportions/manual"
                  state={{ alonePage: state.alonePage }}
                >
                  Advanced body proportions
                </Button>
              )}
              <Tooltip
                // disabled={canDoAuto}
                preferedDirection="top"
                content={
                  <>
                    <Typography
                      whitespace="whitespace-pre-line"
                      variant="standard"
                    >
                      To be able to use the automatic height detection please:
                    </Typography>
                    <ul className="list-disc ml-8 text-standard">
                      {!hasHmd && (
                        <Localized id="onboarding-scaled_proportions-manual_height-warning-no_hmd">
                          <li />
                        </Localized>
                      )}
                      {!hasHandControllers && (
                        <Localized id="onboarding-scaled_proportions-manual_height-warning-no_controllers">
                          <li />
                        </Localized>
                      )}
                    </ul>
                  </>
                }
              >
                <Button
                  variant="secondary"
                  // disabled={!canDoAuto}
                  onClick={() => setAuto('height')}
                >
                  I do not know my height
                </Button>
              </Tooltip>
            </div>
          </div>
        </div>

        <div className="bg-background-70 rounded-md xs:max-w-xs md:max-w-sm sm:max-w-sm lg:max-w-md w-full mobile:h-[30%] relative">
          {!auto && (
            <>
              <SkeletonVisualizerWidget
                onInit={(context) => {
                  context.addView({
                    left: 0,
                    bottom: 0,
                    width: 1,
                    height: 1,
                    position: new Vector3(3, 2.5, -3),
                    onHeightChange(v, newHeight) {
                      // retouch the target and scale settings so the height element doesnt hide the head
                      v.controls.target.set(0, newHeight / 1.7, 0);
                      const scale = Math.max(1, newHeight) / 1.2;
                      v.camera.zoom = 1 / scale;
                    },
                  });
                }}
              />
              <div className="absolute top-0 left-0 w-full p-4 hidden xs:flex">
                <TipBox>
                  Use the preview to see if the skeleton align with your
                  movements. Move around. Try sitting, laying down.
                </TipBox>
              </div>
            </>
          )}
          {auto === 'height' && (
            <div className="flex flex-col p-4 gap-2 h-full">
              <div className="flex gap-4 items-center">
                <div className="rounded-full bg-background-50 flex items-center justify-center h-[40px] w-[40px]">
                  <div
                    className={classNames(
                      'h-[21px] w-[21px] rounded-full bg-status-critical animate-pulse'
                    )}
                   />
                </div>
                <Typography variant="main-title">
                  Recording your Height...
                </Typography>
              </div>
              <div className="flex gap-1 flex-col">
                <Typography>
                  Stand upright and make sure to look forward.
                </Typography>
                <Typography>
                  Your feets must stay flat on the ground.
                </Typography>
                <Typography>
                  Once your height seems correct, press the "Yes" button
                </Typography>
              </div>
              <div className="flex w-full flex-grow">
                <img
                  src="/images/front-standing-pose.webp"
                  className="object-contain aspect-video"
                  alt="Reset position"
                />
              </div>
            </div>
          )}
          {auto === 'floor' && (
            <div className="flex flex-col p-4 gap-2 h-full">
              <div className="flex gap-4 items-center">
                <div className="rounded-full bg-background-50 flex items-center justify-center h-[40px] w-[40px]">
                  <div
                    className={classNames(
                      'h-[21px] w-[21px] rounded-full bg-status-critical animate-pulse'
                    )}
                   />
                </div>
                <Typography variant="main-title">
                  Recording your Floor Height...
                </Typography>
              </div>
              <div className="flex gap-1 flex-col">
                <Typography>
                  Put your controllers on the floor then stand upright. Make
                  sure to look forward.
                </Typography>
                <Typography>
                  Your feets must stay flat on the ground.
                </Typography>
                <Typography>
                  Once your height seems correct, press the "Yes" button
                </Typography>
              </div>
              <div className="flex w-full flex-grow">
                <img
                  src="/images/front-standing-pose.webp"
                  className="object-contain aspect-video"
                  alt="Reset position"
                />
              </div>
            </div>
          )}
        </div>
      </div>
    </HeightContextC.Provider>
  );
}
