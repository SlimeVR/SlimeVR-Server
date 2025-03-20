import { Control, Controller, useForm } from 'react-hook-form';
import {
  ChangeSkeletonConfigRequestT,
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
import { useIsTauri } from '@/hooks/breakpoint';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import { ProportionsResetModal } from './ProportionsResetModal';
import { fileOpen, fileSave } from 'browser-fs-access';
import { CURRENT_EXPORT_VERSION, MIN_HEIGHT } from '@/hooks/manual-proportions';
import { save } from '@tauri-apps/plugin-dialog';
import { writeTextFile } from '@tauri-apps/plugin-fs';
import { error } from '@/utils/logging';
import classNames from 'classnames';
import { useAppContext } from '@/hooks/app';
import { RulerIcon } from '@/components/commons/icon/RulerIcon';
import { Tooltip } from '@/components/commons/Tooltip';
import { PercentIcon } from '@/components/commons/icon/PercentIcon';
import { UploadFileIcon } from '@/components/commons/icon/UploadFileIcon';
import { FullResetIcon } from '@/components/commons/icon/ResetIcon';
import { ImportIcon } from '@/components/commons/icon/ImportIcon';
import { HumanIcon } from '@/components/commons/icon/HumanIcon';
import { Typography } from '@/components/commons/Typography';
import { useLocaleConfig } from '@/i18n/config';
import { useNavigate } from 'react-router-dom';

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
      // Convert the skeleton part enums into a string
      data.skeletonParts.forEach((x) => {
        if (typeof x.bone === 'number')
          x.bone = SkeletonBone[x.bone] as SkeletonBoneKey;
      });
      data.version = CURRENT_EXPORT_VERSION;

      const blob = new Blob([JSON.stringify(data)], {
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
            path ? writeTextFile(path, JSON.stringify(data)) : undefined
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
    });

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
          tooltip={
            <Localized id="onboarding-manual_proportions-import">
              <Typography variant="standard"></Typography>
            </Localized>
          }
          onClick={onImport}
          className={classNames(
            'transition-colors',
            importState === ImportStatus.FAILED && 'text-status-critical',
            importState === ImportStatus.SUCCESS && 'text-status-success'
          )}
        >
          <UploadFileIcon width={25}></UploadFileIcon>
        </IconButton>
      </div>
      <div className="flex">
        <IconButton
          tooltip={
            <Localized id="onboarding-manual_proportions-export">
              <Typography variant="standard"></Typography>
            </Localized>
          }
          onClick={() => {
            exporting.current = true;

            sendRPCPacket(
              RpcMessage.SkeletonConfigRequest,
              new SkeletonConfigRequestT()
            );
          }}
        >
          <ImportIcon size={25}></ImportIcon>
        </IconButton>
      </div>
    </>
  );
}

type ManualProportionControls = Control<{
  precise: boolean;
  ratio: boolean;
}>;

function IconButton({
  onClick,
  tooltip,
  children,
  className,
  disabled,
}: {
  onClick: () => void;
  tooltip: ReactNode;
  className?: string;
  disabled?: boolean;
  children: ReactNode;
}) {
  return (
    <Tooltip preferedDirection="bottom" content={tooltip}>
      <button
        onClick={onClick}
        disabled={disabled}
        className={classNames(
          'flex flex-col justify-center rounded-md p-3 gap-1 items-center w-14 h-14 text-standard',
          disabled
            ? 'fill-background-70 cursor-not-allowed'
            : 'fill-background-10 bg-background-60 hover:bg-background-50 cursor-pointer',
          className
        )}
      >
        {children}
      </button>
    </Tooltip>
  );
}

function LinearRatioToggle({ control }: { control: ManualProportionControls }) {
  return (
    <Controller
      name="ratio"
      control={control}
      render={({ field: { onChange, value } }) => (
        <>
          {value ? (
            <IconButton
              tooltip={
                <Localized id="onboarding-manual_proportions-grouped_proportions">
                  <Typography variant="standard"></Typography>
                </Localized>
              }
              onClick={() => onChange(!value)}
            >
              <PercentIcon size={25}></PercentIcon>
            </IconButton>
          ) : (
            <IconButton
              tooltip={
                <Localized id="onboarding-manual_proportions-all_proportions">
                  <Typography variant="standard"></Typography>
                </Localized>
              }
              onClick={() => onChange(!value)}
            >
              <RulerIcon width={25}></RulerIcon>
            </IconButton>
          )}
        </>
      )}
    ></Controller>
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
              tooltip={
                <Localized id="onboarding-manual_proportions-normal_increment">
                  <Typography variant="standard"></Typography>
                </Localized>
              }
              onClick={() => onChange(!value)}
            >
              <div className="text-xl font-bold">+1</div>
            </IconButton>
          ) : (
            <IconButton
              tooltip={
                <Localized id="onboarding-manual_proportions-precise_increment">
                  <Typography variant="standard"></Typography>
                </Localized>
              }
              onClick={() => onChange(!value)}
            >
              <div className="text-xl font-bold">+0.5</div>
            </IconButton>
          )}
        </>
      )}
    ></Controller>
  );
}

function ButtonsControl({ control }: { control: ManualProportionControls }) {
  const { state } = useOnboarding();
  const nav = useNavigate();
  const { computedTrackers } = useAppContext();
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
        <LinearRatioToggle control={control}></LinearRatioToggle>
      </div>
      <div className="flex">
        <PreciseToggle control={control}></PreciseToggle>
      </div>
      <div className="flex">
        <IconButton
          tooltip={
            <Localized id="reset-reset_all">
              <Typography variant="standard"></Typography>
            </Localized>
          }
          onClick={() => setShowWarning(true)}
        >
          <FullResetIcon width={20}></FullResetIcon>
        </IconButton>
      </div>
      <div className="flex">
        <IconButton
          disabled={!canUseFineTuning}
          tooltip={
            <Localized
              id={
                !canUseFineTuning
                  ? 'onboarding-manual_proportions-fine_tuning_button-disabled-tooltip'
                  : 'onboarding-manual_proportions-fine_tuning_button'
              }
            >
              <Typography variant="standard"></Typography>
            </Localized>
          }
          onClick={() =>
            nav('/onboarding/body-proportions/auto', {
              state: { alonePage: state.alonePage },
            })
          }
        >
          <HumanIcon width={20}></HumanIcon>
        </IconButton>
      </div>
      <div className="flex flex-grow mobile:hidden"></div>
      <ImportExportButtons></ImportExportButtons>
      <ProportionsResetModal
        accept={() => {
          resetAll();
          setShowWarning(false);
        }}
        onClose={() => setShowWarning(false)}
        isOpen={showWarning}
      ></ProportionsResetModal>
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
          <ButtonsControl control={control}></ButtonsControl>
          <div className="bg-background-60 h-20 rounded-md flex-grow overflow-y-auto">
            <BodyProportions
              precise={precise ?? defaultValues.precise}
              type={ratio ? 'ratio' : 'linear'}
              variant={state.alonePage ? 'alone' : 'onboarding'}
            ></BodyProportions>
          </div>
        </div>
        <div className="rounded-md overflow-clip w-1/3 bg-background-60 hidden mobile:hidden sm:flex relative">
          <SkeletonVisualizerWidget />
          <Tooltip
            preferedDirection="bottom"
            content={
              <Localized id="onboarding-manual_proportions-estimated_height">
                <Typography></Typography>
              </Localized>
            }
          >
            <div className="absolute h-14 bg-background-50 p-4 flex items-center rounded-lg right-4 top-4">
              <Typography variant="main-title">
                {cmFormat.format((userHeight * 100) / 0.936)}
              </Typography>
            </div>
          </Tooltip>
        </div>
      </div>
    </>
  );
}
