import { useForm } from 'react-hook-form';
import {
  ChangeSkeletonConfigRequestT,
  RpcMessage,
  SkeletonBone,
  SkeletonConfigRequestT,
  SkeletonResetAllRequestT,
} from 'solarxr-protocol';
import { useOnboarding } from '@/hooks/onboarding';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { BodyProportions } from './BodyProportions';
import { Localized, useLocalization } from '@fluent/react';
import { useEffect, useMemo, useRef, useState } from 'react';
import { useBreakpoint, useIsTauri } from '@/hooks/breakpoint';
import { SkeletonVisualizerWidget } from '@/components/widgets/SkeletonVisualizerWidget';
import { ProportionsResetModal } from './ProportionsResetModal';
import { fileOpen, fileSave } from 'browser-fs-access';
import { CURRENT_EXPORT_VERSION, MIN_HEIGHT } from '@/hooks/manual-proportions';
import { save } from '@tauri-apps/plugin-dialog';
import { writeTextFile } from '@tauri-apps/plugin-fs';
import { error } from '@/utils/logging';
import classNames from 'classnames';
import { useAppContext } from '@/hooks/app';
import { Tooltip } from '@/components/commons/Tooltip';

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

  const importStatusKey = useMemo(() => {
    switch (importState) {
      case ImportStatus.FAILED:
        return 'onboarding-manual_proportions-import-failed';
      case ImportStatus.SUCCESS:
        return 'onboarding-manual_proportions-import-success';
      case ImportStatus.OK:
        return 'onboarding-manual_proportions-import';
    }
  }, [importState]);

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
  };

  return (
    <>
      <Button
        variant="secondary"
        onClick={() => {
          exporting.current = true;

          sendRPCPacket(
            RpcMessage.SkeletonConfigRequest,
            new SkeletonConfigRequestT()
          );
        }}
      >
        {l10n.getString('onboarding-manual_proportions-export')}
      </Button>
      <Button
        variant="secondary"
        className={classNames(
          'transition-colors',
          importState === ImportStatus.FAILED && 'bg-status-critical',
          importState === ImportStatus.SUCCESS && 'bg-status-success'
        )}
        onClick={onImport}
      >
        {l10n.getString(importStatusKey)}
      </Button>
    </>
  );
}

function ButtonsControl() {
  const { l10n } = useLocalization();
  const { state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();

  const [showWarning, setShowWarning] = useState(false);
  const resetAll = () => {
    sendRPCPacket(
      RpcMessage.SkeletonResetAllRequest,
      new SkeletonResetAllRequestT()
    );
  };

  return (
    <div className="gap-2 flex mobile:grid grid-cols-2">
      <div className="flex flex-grow mobile:contents">
        <Button
          variant="secondary"
          state={{ alonePage: state.alonePage }}
          to="/onboarding/body-proportions/scaled"
        >
          {l10n.getString('onboarding-scaled_proportions-title')}
        </Button>
      </div>
      <div className="flex gap-2 mobile:contents">
        <ImportExportButtons></ImportExportButtons>
        <Button variant="secondary" onClick={() => setShowWarning(true)}>
          {l10n.getString('reset-reset_all')}
        </Button>
        <ProportionsResetModal
          accept={() => {
            resetAll();
            setShowWarning(false);
          }}
          onClose={() => setShowWarning(false)}
          isOpen={showWarning}
        ></ProportionsResetModal>
      </div>
    </div>
  );
}

export function ManualProportionsPage() {
  const { isMobile } = useBreakpoint('mobile');
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();
  const { computedTrackers } = useAppContext();

  applyProgress(0.9);

  const savedValue = useMemo(() => localStorage.getItem('ratioMode'), []);

  const defaultValues = { precise: false, ratio: savedValue !== 'false' };

  const { control, watch } = useForm<{ precise: boolean; ratio: boolean }>({
    defaultValues,
  });
  const { precise, ratio } = watch();

  useEffect(() => {
    localStorage.setItem('ratioMode', ratio?.toString() ?? 'true');
  }, [ratio]);

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
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center relative">
        <div className="flex flex-col w-full h-full xs:max-w-5xl xs:justify-center">
          <div className="flex gap-8 justify-center h-full xs:items-center">
            <div className="flex flex-col w-full xs:max-w-2xl gap-3 items-center mobile:justify-around">
              <div className="flex flex-col mx-4">
                <Typography variant="main-title">
                  {l10n.getString('onboarding-manual_proportions-title')}
                </Typography>
                <CheckBox
                  control={control}
                  label={l10n.getString('onboarding-manual_proportions-ratio')}
                  name="ratio"
                  variant="toggle"
                ></CheckBox>
                <CheckBox
                  control={control}
                  label={l10n.getString(
                    'onboarding-manual_proportions-precision'
                  )}
                  name="precise"
                  variant="toggle"
                ></CheckBox>
                {isMobile && (
                  <div className="flex gap-3 justify-between">
                    <ButtonsControl></ButtonsControl>
                  </div>
                )}
              </div>
              <Tooltip
                content={
                  <Localized id="onboarding-manual_proportions-fine_tuning_button-disabled-tooltip">
                    <Typography></Typography>
                  </Localized>
                }
                preferedDirection="top"
                disabled={canUseFineTuning}
              >
                <Button
                  variant="secondary"
                  to="/onboarding/body-proportions/auto"
                  state={{ alonePage: state.alonePage }}
                  disabled={!canUseFineTuning}
                >
                  {l10n.getString(
                    'onboarding-manual_proportions-fine_tuning_button'
                  )}
                </Button>
              </Tooltip>

              <div className="w-full px-2">
                <BodyProportions
                  precise={precise ?? defaultValues.precise}
                  type={ratio ? 'ratio' : 'linear'}
                  variant={state.alonePage ? 'alone' : 'onboarding'}
                ></BodyProportions>
              </div>
            </div>
            <div className="flex-col flex-grow gap-3 rounded-xl fill-background-50 items-center hidden md:flex">
              <SkeletonVisualizerWidget height="65vh" maxHeight={600} />
            </div>
          </div>
          {!isMobile && (
            <div className="my-5 mx-4">
              <ButtonsControl></ButtonsControl>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
