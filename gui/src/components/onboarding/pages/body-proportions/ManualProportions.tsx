import { Control, Controller, useForm } from 'react-hook-form';
import {
  ChangeSkeletonConfigRequestT,
  ResetType,
  RpcMessage,
  SkeletonBone,
  SkeletonConfigRequestT,
  SkeletonConfigResponseT,
  SkeletonResetAllRequestT,
} from 'solarxr-protocol';
import { useOnboarding } from '@/hooks/onboarding';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { BodyProportions } from './BodyProportions';
import { Localized, useLocalization } from '@fluent/react';
import { ReactNode, useEffect, useMemo, useRef, useState } from 'react';
import { useBreakpoint, useIsTauri } from '@/hooks/breakpoint';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import { ProportionsResetModal } from './ProportionsResetModal';
import { fileOpen, fileSave } from 'browser-fs-access';
import { CURRENT_EXPORT_VERSION, MIN_HEIGHT } from '@/hooks/manual-proportions';
import { save } from '@tauri-apps/plugin-dialog';
import { writeTextFile } from '@tauri-apps/plugin-fs';
import { error } from '@/utils/logging';
import classNames from 'classnames';
import { Tooltip } from '@/components/commons/Tooltip';
import { useAtomValue } from 'jotai';
import { computedTrackersAtom } from '@/store/app-store';
import { RulerIcon } from '@/components/commons/icon/RulerIcon';
import { PercentIcon } from '@/components/commons/icon/PercentIcon';
import { UploadFileIcon } from '@/components/commons/icon/UploadFileIcon';
import { FullResetIcon } from '@/components/commons/icon/ResetIcon';
import { ImportIcon } from '@/components/commons/icon/ImportIcon';
import { HumanIcon } from '@/components/commons/icon/HumanIcon';
import { Typography } from '@/components/commons/Typography';
import { useLocaleConfig } from '@/i18n/config';
import { useNavigate } from 'react-router-dom';
import { ResetButton } from '@/components/home/ResetButton';
import { Vector3 } from 'three';
import { ArrowLink } from '@/components/commons/ArrowLink';

function IconButton({
  onClick,
  children,
  className,
  disabled,
  tooltip,
  showTooltip = false,
  icon,
}: {
  onClick: () => void;
  className?: string;
  disabled?: boolean;
  children: ReactNode;
  tooltip?: ReactNode;
  showTooltip?: boolean;
  icon: ReactNode;
}) {
  const { isMobile } = useBreakpoint('mobile');

  if (isMobile) showTooltip = true;

  return (
    <Tooltip
      disabled={!showTooltip}
      preferedDirection="bottom"
      content={tooltip ?? children}
    >
      <button
        onClick={onClick}
        disabled={disabled}
        className={classNames(
          'flex flex-col rounded-md p-2 justify-between gap-1 items-center text-standard fill-background-10',
          disabled
            ? 'cursor-not-allowed opacity-30'
            : 'hover:bg-background-50 cursor-pointer',
          className
        )}
      >
        <div className="flex justify-center items-center h-8 mobile:w-8 p-2 mobile:p-0">
          {icon}
        </div>
        <div className={classNames('mobile:hidden')}>{children}</div>
      </button>
    </Tooltip>
  );
}

function parseConfigImport(
  config: SkeletonConfigExport
): ChangeSkeletonConfigRequestT[] {
  if (!config.version) config.version = 1;
  if (config.version < 1) {
    // Add config migration stuff here, this one is just an example.
  }

  return config.skeletonParts.map((part) => {
    const bone =
      typeof part.bone === 'string' ? SkeletonBone[part.bone] : part.bone;

    return new ChangeSkeletonConfigRequestT(bone, part.value);
  });
}

type SkeletonBoneKey = keyof typeof SkeletonBone;

interface SkeletonConfigExport {
  version?: number;
  skeletonParts: {
    bone: SkeletonBoneKey | SkeletonBone;
    value: number;
  }[];
}

enum ImportStatus {
  FAILED,
  SUCCESS,
  OK,
}

function ImportExportButtons() {
  const isTauri = useIsTauri();
  const { l10n } = useLocalization();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [importState, setImportState] = useState(ImportStatus.OK);
  const exporting = useRef(false);

  useRPCPacket(
    RpcMessage.SkeletonConfigResponse,
    (data: SkeletonConfigExport) => {
      if (!exporting.current) return;
      exporting.current = false;

      // make a copy of the config as mutating it directly would be an issue
      // bc if other useRPCPacket read the same packet,
      // data would use the mutated data
      const copy: SkeletonConfigExport = JSON.parse(JSON.stringify(data));

      // Convert the skeleton part enums into a string
      copy.skeletonParts.forEach((x) => {
        if (typeof x.bone === 'number')
          x.bone = SkeletonBone[x.bone] as SkeletonBoneKey;
      });
      copy.version = CURRENT_EXPORT_VERSION;

      const blob = new Blob([JSON.stringify(copy)], {
        type: 'application/json',
      });
      if (isTauri) {
        save({
          filters: [
            {
              name: l10n.getString('onboarding-manual_proportions-file_type'),
              extensions: ['json'],
            },
          ],
          defaultPath: 'body-proportions.json',
        })
          .then((path) =>
            path ? writeTextFile(path, JSON.stringify(copy)) : undefined
          )
          .catch((err) => {
            error(err);
          });
      } else {
        fileSave(blob, {
          fileName: 'body-proportions.json',
          extensions: ['.json'],
        });
      }
    }
  );

  const onImport = async () => {
    const file = await fileOpen({
      mimeTypes: ['application/json'],
    }).catch((err) => {
      error(err);
      return null;
    });
    if (!file) return;

    const text = await file.text();
    const config = JSON.parse(text) as SkeletonConfigExport;
    if (
      !config?.skeletonParts?.length ||
      !Array.isArray(config.skeletonParts)
    ) {
      error(
        'failed to import body proportions because skeletonParts is not an array/empty'
      );
      return setImportState(ImportStatus.FAILED);
    }

    for (const bone of [...config.skeletonParts]) {
      if (
        (typeof bone.bone === 'string' && !(bone.bone in SkeletonBone)) ||
        (typeof bone.bone === 'number' &&
          typeof SkeletonBone[bone.bone] !== 'string')
      ) {
        error(
          `failed to import body proportions because ${bone.bone} is not a valid bone`
        );
        return setImportState(ImportStatus.FAILED);
      }
    }

    parseConfigImport(config).forEach((req) =>
      sendRPCPacket(RpcMessage.ChangeSkeletonConfigRequest, req)
    );
    setImportState(ImportStatus.SUCCESS);
    setTimeout(() => {
      setImportState(ImportStatus.OK);
    }, 2000);
  };

  return (
    <>
      <div className="flex">
        <IconButton
          icon={<UploadFileIcon width={25} />}
          onClick={onImport}
          className={classNames(
            'transition-colors',
            importState === ImportStatus.FAILED && 'text-status-critical',
            importState === ImportStatus.SUCCESS && 'text-status-success'
          )}
        >
          <Localized id="onboarding-manual_proportions-import">
            <Typography variant="standard" />
          </Localized>
        </IconButton>
      </div>
      <div className="flex">
        <IconButton
          icon={<ImportIcon size={25} />}
          onClick={() => {
            exporting.current = true;

            sendRPCPacket(
              RpcMessage.SkeletonConfigRequest,
              new SkeletonConfigRequestT()
            );
          }}
        >
          <Localized id="onboarding-manual_proportions-export">
            <Typography variant="standard" />
          </Localized>
        </IconButton>
      </div>
    </>
  );
}

type ManualProportionControls = Control<{
  precise: boolean;
  ratio: boolean;
}>;

function LinearRatioToggle({ control }: { control: ManualProportionControls }) {
  return (
    <Controller
      name="ratio"
      control={control}
      render={({ field: { onChange, value } }) => (
        <>
          {value ? (
            <IconButton
              icon={<PercentIcon size={25} />}
              onClick={() => onChange(!value)}
            >
              <Localized id="onboarding-manual_proportions-grouped_proportions">
                <Typography variant="standard" />
              </Localized>
            </IconButton>
          ) : (
            <IconButton
              icon={<RulerIcon width={25} />}
              onClick={() => onChange(!value)}
            >
              <Localized id="onboarding-manual_proportions-all_proportions">
                <Typography variant="standard" />
              </Localized>
            </IconButton>
          )}
        </>
      )}
    />
  );
}

function PreciseToggle({ control }: { control: ManualProportionControls }) {
  return (
    <Controller
      name="precise"
      control={control}
      render={({ field: { onChange, value } }) => (
        <>
          {!value ? (
            <IconButton
              icon={<div className="text-xl font-bold">+1</div>}
              onClick={() => onChange(!value)}
            >
              <Localized id="onboarding-manual_proportions-normal_increment">
                <Typography variant="standard" />
              </Localized>
            </IconButton>
          ) : (
            <IconButton
              icon={<div className="text-xl font-bold">+0.5</div>}
              onClick={() => onChange(!value)}
            >
              <Localized id="onboarding-manual_proportions-precise_increment">
                <Typography variant="standard" />
              </Localized>
            </IconButton>
          )}
        </>
      )}
    />
  );
}

function ButtonsControl({ control }: { control: ManualProportionControls }) {
  const { state } = useOnboarding();
  const nav = useNavigate();
  const computedTrackers = useAtomValue(computedTrackersAtom);
  const { sendRPCPacket } = useWebsocketAPI();

  const [showWarning, setShowWarning] = useState(false);
  const resetAll = () => {
    sendRPCPacket(
      RpcMessage.SkeletonResetAllRequest,
      new SkeletonResetAllRequestT()
    );
  };

  const beneathFloor = useMemo(() => {
    const hmd = computedTrackers.find(
      (tracker) =>
        tracker.tracker.trackerId?.trackerNum === 1 &&
        tracker.tracker.trackerId.deviceId?.id === undefined
    );
    return !(hmd?.tracker.position && hmd.tracker.position.y >= MIN_HEIGHT);
  }, [computedTrackers]);

  const canUseFineTuning = !beneathFloor || import.meta.env.DEV;

  return (
    <div className="bg-background-60 rounded-md flex gap-2">
      <div className="flex">
        <LinearRatioToggle control={control} />
      </div>
      <div className="flex">
        <PreciseToggle control={control} />
      </div>
      <div className="flex">
        <IconButton
          icon={<FullResetIcon width={20} />}
          onClick={() => setShowWarning(true)}
        >
          <Localized id="reset-reset_all">
            <Typography variant="standard" />
          </Localized>
        </IconButton>
      </div>
      <div className="flex">
        <IconButton
          showTooltip={!canUseFineTuning}
          tooltip={
            <Localized
              id={
                !canUseFineTuning
                  ? 'onboarding-manual_proportions-fine_tuning_button-disabled-tooltip'
                  : 'onboarding-manual_proportions-fine_tuning_button'
              }
            >
              <Typography variant="standard" />
            </Localized>
          }
          disabled={!canUseFineTuning}
          icon={<HumanIcon width={20} />}
          onClick={() =>
            nav('/onboarding/body-proportions/auto', {
              state: { alonePage: state.alonePage },
            })
          }
        >
          <Localized id={'onboarding-manual_proportions-fine_tuning_button'}>
            <Typography variant="standard" />
          </Localized>
        </IconButton>
      </div>
      <div className="flex flex-grow" />
      <ImportExportButtons />
      <ProportionsResetModal
        accept={() => {
          resetAll();
          setShowWarning(false);
        }}
        onClose={() => setShowWarning(false)}
        isOpen={showWarning}
      />
    </div>
  );
}

export function ManualProportionsPage() {
  const { applyProgress, state } = useOnboarding();
  const { useRPCPacket } = useWebsocketAPI();
  const { currentLocales } = useLocaleConfig();

  const [userHeight, setUserHeight] = useState(0);

  applyProgress(0.9);

  const savedValue = useMemo(() => localStorage.getItem('ratioMode'), []);

  const defaultValues = { precise: false, ratio: savedValue !== 'false' };

  const { control, watch } = useForm<{ precise: boolean; ratio: boolean }>({
    defaultValues,
  });
  const { precise, ratio } = watch();

  const { cmFormat } = useMemo(() => {
    const cmFormat = Intl.NumberFormat(currentLocales, {
      style: 'unit',
      unit: 'centimeter',
      maximumFractionDigits: 1,
    });
    return { cmFormat };
  }, [currentLocales]);

  useEffect(() => {
    localStorage.setItem('ratioMode', ratio?.toString() ?? 'true');
  }, [ratio]);

  useRPCPacket(
    RpcMessage.SkeletonConfigResponse,
    (data: SkeletonConfigResponseT) => {
      if (data.userHeight) setUserHeight(data.userHeight);
    }
  );

  return (
    <>
      <div className="flex w-full h-full gap-2 bg-background-70 p-2">
        <div className="flex flex-col flex-grow gap-2">
          <div className="flex gap-2">
            <Localized id="onboarding-manual_proportions-back-scaled">
              <ArrowLink
                direction="left"
                to="/onboarding/body-proportions/scaled"
                state={{ alonePage: state.alonePage }}
              >
                LINK
              </ArrowLink>
            </Localized>
          </div>
          <ButtonsControl control={control} />
          <div className="bg-background-60 h-20 rounded-md flex-grow overflow-y-auto">
            <BodyProportions
              precise={precise ?? defaultValues.precise}
              type={ratio ? 'ratio' : 'linear'}
              variant={state.alonePage ? 'alone' : 'onboarding'}
            />
          </div>
        </div>
        <div className="rounded-md overflow-clip w-1/3 bg-background-60 hidden mobile:hidden sm:flex relative">
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

          <div className="top-4 w-full px-4 absolute flex gap-2 flex-col lg:flex-row md:flex-wrap">
            <div className="h-14 flex flex-grow items-center">
              <ResetButton
                type={ResetType.Full}
                className="w-full h-full bg-background-50 hover:bg-background-40 text-background-10"
              />
            </div>
            <Tooltip
              preferedDirection="bottom"
              content={
                <Localized id="onboarding-manual_proportions-estimated_height">
                  <Typography />
                </Localized>
              }
            >
              <div className="h-14 bg-background-50 p-4 flex items-center rounded-lg min-w-36 justify-center">
                <Typography variant="main-title" whitespace="whitespace-nowrap">
                  {cmFormat.format((userHeight * 100) / 0.936)}
                </Typography>
              </div>
            </Tooltip>
          </div>
        </div>
      </div>
    </>
  );
}
