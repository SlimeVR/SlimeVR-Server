import { createContext, useContext, useMemo, useState } from 'react';
import {
  AutoBoneApplyRequestT,
  AutoBoneEpochResponseT,
  AutoBoneProcessRequestT,
  AutoBoneProcessStatusResponseT,
  AutoBoneProcessType,
  RpcMessage,
  SkeletonBone,
  SkeletonPartT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';
import { useLocalization } from '@fluent/react';
import { log } from '@/utils/logging';

export enum ProcessStatus {
  PENDING,
  FULFILLED,
  REJECTED,
}

export interface AutoboneContext {
  hasRecording: ProcessStatus;
  hasCalibration: ProcessStatus;
  progress: number;
  bodyParts: { bone: SkeletonBone; label: string; value: number }[] | null;
  eta: number;
  startRecording: () => void;
  startProcessing: () => void;
  applyProcessing: () => void;
}

export function useProvideAutobone(): AutoboneContext {
  const { l10n } = useLocalization();
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const [hasRecording, setHasRecording] = useState(ProcessStatus.PENDING);
  const [hasCalibration, setHasCalibration] = useState(ProcessStatus.PENDING);
  const [progress, setProgress] = useState(0);
  const [eta, setEta] = useState(-1);
  const [skeletonParts, setSkeletonParts] = useState<SkeletonPartT[] | null>(null);

  const bodyParts = useMemo(() => {
    return (
      skeletonParts?.map(({ bone, value }) => ({
        bone,
        label: l10n.getString('skeleton_bone-' + SkeletonBone[bone]),
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
    setEta(-1);

    const processRequest = new AutoBoneProcessRequestT();
    processRequest.processType = processType;

    sendRPCPacket(RpcMessage.AutoBoneProcessRequest, processRequest);
  };

  const startRecording = () => {
    setHasCalibration(ProcessStatus.PENDING);
    setHasRecording(ProcessStatus.PENDING);
    setSkeletonParts(null);
    startProcess(AutoBoneProcessType.RECORD);
  };

  const startProcessing = () => {
    setHasCalibration(ProcessStatus.PENDING);
    startProcess(AutoBoneProcessType.PROCESS);
  };

  const applyProcessing = () => {
    sendRPCPacket(RpcMessage.AutoBoneApplyRequest, new AutoBoneApplyRequestT());
  };

  useRPCPacket(
    RpcMessage.AutoBoneProcessStatusResponse,
    (data: AutoBoneProcessStatusResponseT) => {
      if (data.completed) {
        setProgress(1);
      }

      if (data.processType) {
        if (data.total > 0 && data.current >= 0) {
          setProgress(data.current / data.total);
        }

        setEta(data.eta);

        if (data.completed) {
          log(`Process ${AutoBoneProcessType[data.processType]} has completed`);

          switch (data.processType) {
            case AutoBoneProcessType.RECORD:
              setHasRecording(
                data.success ? ProcessStatus.FULFILLED : ProcessStatus.REJECTED
              );
              startProcessing();
              break;

            case AutoBoneProcessType.PROCESS:
              setHasCalibration(
                data.success ? ProcessStatus.FULFILLED : ProcessStatus.REJECTED
              );
              break;

            // case AutoBoneProcessType.APPLY:
            //   // Update skeleton config when applied
            //   sendRPCPacket(
            //     RpcMessage.SkeletonConfigRequest,
            //     new SkeletonConfigRequestT()
            //   );
            //   break;
          }
        }
      }
    }
  );

  useRPCPacket(RpcMessage.AutoBoneEpochResponse, (data: AutoBoneEpochResponseT) => {
    setProgress(data.currentEpoch / data.totalEpochs);

    // Probably not necessary to show to the user
    log(
      'Epoch ',
      data.currentEpoch,
      '/',
      data.totalEpochs,
      ' (Error ',
      data.epochError,
      ')'
    );

    setSkeletonParts(data.adjustedSkeletonParts);
  });

  return {
    hasCalibration,
    hasRecording,
    progress,
    eta,
    bodyParts,
    startProcessing,
    startRecording,
    applyProcessing,
  };
}

export const AutoboneContextC = createContext<AutoboneContext>(undefined as any);

export function useAutobone() {
  const context = useContext<AutoboneContext>(AutoboneContextC);
  if (!context) {
    throw new Error('useAutobone must be within a AutoboneContext Provider');
  }
  return context;
}
