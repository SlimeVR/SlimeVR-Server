import { createContext, useContext, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  AutoBoneEpochResponseT,
  AutoBoneProcessRequestT,
  AutoBoneProcessStatusResponseT,
  AutoBoneProcessType,
  RpcMessage,
  SkeletonBone,
  SkeletonConfigRequestT,
  SkeletonPartT
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';

export interface AutoboneContext {
  hasRecording: boolean;
  hasCalibration: boolean;
  progress: number;
  bodyParts: { bone: SkeletonBone; label: string; value: number }[] | null;
  startRecording: () => void;
  startProcessing: () => void;
  applyProcessing: () => void;
}

export function useProvideAutobone(): AutoboneContext {
  const { t } = useTranslation();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [hasRecording, setHasRecording] = useState(false);
  const [hasCalibration, setHasCalibration] = useState(false);
  const [progress, setProgress] = useState(0);
  const [skeletonParts, setSkeletonParts] = useState<SkeletonPartT[] | null>(
    null
  );

  const bodyParts = useMemo(() => {
    return (
      skeletonParts?.map(({ bone, value }) => ({
        bone,
        label: t('skeleton-bone.' + SkeletonBone[bone]),
        value,
      })) || []
    );
  }, [skeletonParts]);

  const startProcess = (processType: AutoBoneProcessType) => {
    // Don't allow multiple processes at once (for now atleast)
    // if (isProcessRunning) {
    //   return;
    // }

    setProgress(0);

    const processRequest = new AutoBoneProcessRequestT();
    processRequest.processType = processType;

    sendRPCPacket(RpcMessage.AutoBoneProcessRequest, processRequest);
  };

  const startRecording = () => {
    setHasCalibration(false);
    setHasRecording(false);
    setSkeletonParts(null);
    startProcess(AutoBoneProcessType.RECORD);
  };

  const startProcessing = () => {
    setHasCalibration(false);
    startProcess(AutoBoneProcessType.PROCESS);
  };

  const applyProcessing = () => {
    startProcess(AutoBoneProcessType.APPLY);
  };

  useRPCPacket(
    RpcMessage.AutoBoneProcessStatusResponse,
    (data: AutoBoneProcessStatusResponseT) => {
      if (data.completed) {
        setProgress(1);
      }

      if (data.processType) {
        if (data.message) {
          console.log(
            AutoBoneProcessType[data.processType],
            ': ',
            data.message
          );
        }

        if (data.total > 0 && data.current >= 0) {
          setProgress(data.current / data.total);
        }

        if (data.completed) {
          console.log(
            'Process ',
            AutoBoneProcessType[data.processType],
            ' has completed'
          );

          switch (data.processType) {
            case AutoBoneProcessType.RECORD:
              setHasRecording(data.success);
              startProcessing();
              break;

            case AutoBoneProcessType.PROCESS:
              setHasCalibration(data.success);

              break;

            case AutoBoneProcessType.APPLY:
              // Update skeleton config when applied
              sendRPCPacket(
                RpcMessage.SkeletonConfigRequest,
                new SkeletonConfigRequestT()
              );
              break;
          }
        }
      }
    }
  );

  useRPCPacket(
    RpcMessage.AutoBoneEpochResponse,
    (data: AutoBoneEpochResponseT) => {
      setProgress(data.currentEpoch / data.totalEpochs);

      // Probably not necessary to show to the user
      console.log(
        'Epoch ',
        data.currentEpoch,
        '/',
        data.totalEpochs,
        ' (Error ',
        data.epochError,
        ')'
      );

      setSkeletonParts(data.adjustedSkeletonParts);
    }
  );

  return {
    hasCalibration,
    hasRecording,
    progress,
    bodyParts,
    startProcessing,
    startRecording,
    applyProcessing,
  };
}

export const AutoboneContextC = createContext<AutoboneContext>(
  undefined as any
);

export function useAutobone() {
  const context = useContext<AutoboneContext>(AutoboneContextC);
  if (!context) {
    throw new Error('useAutobone must be within a AutoboneContext Provider');
  }
  return context;
}
