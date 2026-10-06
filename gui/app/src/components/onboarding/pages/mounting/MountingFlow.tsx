import { ReactNode, useEffect, useRef, useState } from 'react';
import {
  MountingMethod,
  ResetAvailability,
  ResetLifecycle,
  ResetType,
} from 'solarxr-protocol';
import { useAtomValue } from 'jotai';
import { Localized } from '@fluent/react';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { Timeline, TimelineItem } from '@/components/commons/Timeline';
import { useOnboarding } from '@/hooks/onboarding';
import { useCancelReset, useReset } from '@/hooks/reset';
import { resetStatusAtom, serverGuardsAtom } from '@/store/app-store';

export type MountingPhase = 'prepare' | 'mount' | 'done';

export interface MountingFlowProps {
  titleId: string;
  descriptionId: string;
  phases: { phase: MountingPhase; labelId: string }[];
  current: MountingPhase;
}

export interface MountingPhaseProps {
  flow: MountingFlowProps;
  back: ReactNode;
  next: () => void;
  restart: () => void;
  // The phase was reached by finishing the previous one rather than by going back
  advanced: boolean;
}

export function MountingFlowLayout({
  flow,
  body,
  media,
  actions,
}: {
  flow: MountingFlowProps;
  body: ReactNode;
  media: ReactNode;
  actions: ReactNode;
}) {
  const currentIndex = flow.phases.findIndex((p) => p.phase === flow.current);

  return (
    <div className="w-full h-full flex mobile:flex-col xs:flex-row min-h-0 mobile:overflow-y-auto gap-4 px-4 xs:px-8 py-4">
      <div className="flex flex-col w-full xs:w-[360px] gap-5 shrink-0 min-h-0 xs:overflow-y-auto">
        <div className="flex flex-col gap-2">
          <Typography variant="main-title" id={flow.titleId} />
          <Typography color="secondary" id={flow.descriptionId} />
        </div>
        <Timeline>
          {flow.phases.map(({ phase, labelId }, index) => {
            const state =
              index < currentIndex
                ? 'done'
                : index === currentIndex
                  ? 'current'
                  : 'todo';
            return (
              <TimelineItem
                key={phase}
                state={state}
                number={index + 1}
                last={index === flow.phases.length - 1}
                title={<Localized id={labelId} />}
              >
                {state === 'current' && (
                  <div className="flex flex-col gap-2 pt-3">{body}</div>
                )}
              </TimelineItem>
            );
          })}
        </Timeline>
        <div className="flex gap-3 mobile:justify-between xs:mt-auto">
          {actions}
        </div>
      </div>
      <div className="relative flex-1 min-h-0 mobile:min-h-96 rounded-xl bg-background-60 fill-background-50">
        <div className="absolute inset-6 flex items-center justify-center">
          {media}
        </div>
      </div>
    </div>
  );
}

export function useMountingPhases({
  titleId,
  descriptionId,
  mountLabelId,
}: {
  titleId: string;
  descriptionId: string;
  mountLabelId: string;
}) {
  const { state } = useOnboarding();
  const serverGuards = useAtomValue(serverGuardsAtom);
  const needsFullReset =
    serverGuards?.mountingReset === ResetAvailability.NEEDS_FULL_RESET;

  // Stays true once seen so the row remains after the full reset is done
  const [showPrepare, setShowPrepare] = useState(needsFullReset);
  useEffect(() => {
    if (needsFullReset) setShowPrepare(true);
  }, [needsFullReset]);

  const [picked, setPicked] = useState<MountingPhase | null>(null);
  const [advanced, setAdvanced] = useState(false);
  const current = picked ?? (showPrepare ? 'prepare' : 'mount');

  const flow: MountingFlowProps = {
    titleId,
    descriptionId,
    current,
    phases: [
      ...(showPrepare
        ? [
            {
              phase: 'prepare' as const,
              labelId: 'onboarding-automatic_mounting-preparation-title',
            },
          ]
        : []),
      { phase: 'mount', labelId: mountLabelId },
      { phase: 'done', labelId: 'onboarding-automatic_mounting-done-title' },
    ],
  };

  const variant = state.alonePage ? 'tertiary' : 'secondary';
  const back =
    current === 'mount' && showPrepare ? (
      <Button
        variant={variant}
        onClick={() => {
          setAdvanced(false);
          setPicked('prepare');
        }}
        id="onboarding-automatic_mounting-prev_step"
      />
    ) : current === 'done' ? null : (
      <Button
        variant={variant}
        to="/onboarding/mounting/choose"
        state={{ alonePage: !!state.alonePage }}
        id="onboarding-automatic_mounting-prev_step"
      />
    );

  const next = () => {
    setAdvanced(true);
    setPicked(current === 'prepare' ? 'mount' : 'done');
  };

  return {
    flow,
    back,
    next,
    restart: () => {
      setAdvanced(false);
      setPicked('mount');
    },
    advanced,
    alonePage: !!state.alonePage,
  };
}

// Right after the full reset the user is already in place, so start without a click
export function useAutoStartMounting(
  advanced: boolean,
  method: MountingMethod
) {
  const { triggerReset } = useReset({
    type: ResetType.MOUNTING,
    group: 'default',
  });
  const serverGuards = useAtomValue(serverGuardsAtom);
  const [started, setStarted] = useState(false);
  const available = serverGuards?.mountingReset === ResetAvailability.AVAILABLE;

  useEffect(() => {
    if (!advanced || started || !available) return;
    setStarted(true);
    triggerReset(method);
  }, [advanced, started, available]);
}

// Leaving the mounting phase or the page stops a calibration still running
export function useCancelMountingOnLeave() {
  const cancel = useCancelReset();
  const resetStatus = useAtomValue(resetStatusAtom);
  const runningRef = useRef(false);
  const cancelRef = useRef(cancel);

  runningRef.current =
    resetStatus?.resetType === ResetType.MOUNTING &&
    resetStatus.lifecycle === ResetLifecycle.RUNNING;
  cancelRef.current = cancel;

  useEffect(
    () => () => {
      if (runningRef.current) cancelRef.current();
    },
    []
  );
}
