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
import { BodyProportions2 } from './BodyProportions';
import { useLocalization } from '@fluent/react';
import { useEffect, useMemo, useRef, useState } from 'react';
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
import { BigButton } from '@/components/commons/BigButton';
import { FullResetIcon } from '@/components/commons/icon/ResetIcon';

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
  const { computedTrackers } = useAppContext();
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
    <div className="bg-background-60 h-20 rounded-md flex gap-2">
      <BigButton
        icon={<FullResetIcon></FullResetIcon>}
        className="h-full"
        onClick={() => setShowWarning(true)}
      >
        Reset Proportions
      </BigButton>
      <BigButton
        icon={<FullResetIcon></FullResetIcon>}
        disabled={!canUseFineTuning}
        className="h-full"
      >
        Auto Fine tuning
      </BigButton>
      <BigButton icon={<FullResetIcon></FullResetIcon>} className="h-full">
        Import
      </BigButton>
      <BigButton icon={<FullResetIcon></FullResetIcon>} className="h-full">
        Export
      </BigButton>

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

  return (
    <>
      <div className="flex w-full h-full gap-2 bg-background-70 p-2">
        <div className="flex flex-col flex-grow gap-2">
          <ButtonsControl></ButtonsControl>
          <div className="bg-background-60 h-20 rounded-md flex-grow overflow-y-auto">
            <BodyProportions2
              precise={precise ?? defaultValues.precise}
              type={ratio ? 'ratio' : 'linear'}
              variant={state.alonePage ? 'alone' : 'onboarding'}
            ></BodyProportions2>
          </div>
        </div>
        <div className="flex rounded-md overflow-clip max-w-md w-full bg-background-60">
          <SkeletonVisualizerWidget />
        </div>
      </div>
    </>
  );
}
