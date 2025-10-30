import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { AssignMode, defaultConfig, useConfig } from '@/hooks/config';
import { ASSIGNMENT_MODES } from '@/components/onboarding/BodyAssignment';
import { useLocalization } from '@fluent/react';
import { useForm } from 'react-hook-form';
import { useEffect } from 'react';
import { Dropdown } from '@/components/commons/Dropdown';
import { useAtomValue } from 'jotai';
import { connectedIMUTrackersAtom } from '@/store/app-store';

// Ordered collection of assign modes with the number of IMU trackers
const ASSIGN_MODE_OPTIONS = [
  AssignMode.LowerBody,
  AssignMode.Core,
  AssignMode.EnhancedCore,
  AssignMode.FullBody,
  AssignMode.All,
].reduce(
  (options, mode) => ({ ...options, [mode]: ASSIGNMENT_MODES[mode].length }),
  {} as Record<AssignMode, number>
);

const ItemContent = ({
  mode,
  trackersCount,
}: {
  mode: string;
  trackersCount: number;
}) => {
  const { l10n } = useLocalization();

  return (
    <>
      <Typography variant="main-title" textAlign="text-right">
        {l10n.getString('onboarding-assign_trackers-option-amount', {
          trackersCount,
        })}
      </Typography>
      <div className="flex flex-col">
        <Typography>
          {l10n.getString('onboarding-assign_trackers-option-label', {
            mode,
          })}
        </Typography>
        <Typography variant="standard">
          {l10n.getString('onboarding-assign_trackers-option-description', {
            mode,
          })}
        </Typography>
      </div>
    </>
  );
};

export function TrackerAssignOptions({
  variant = 'radio',
}: {
  variant: 'radio' | 'dropdown';
}) {
  const connectedIMUTrackers = useAtomValue(connectedIMUTrackersAtom);

  const { config, setConfig } = useConfig();
  const { control, watch, setValue } = useForm<{
    assignMode: AssignMode;
  }>({
    defaultValues: {
      assignMode: config?.assignMode ?? defaultConfig.assignMode,
    },
  });
  const { assignMode } = watch();

  useEffect(() => {
    setConfig({ assignMode });
  }, [assignMode]);

  useEffect(() => {
    if (connectedIMUTrackers.length <= ASSIGN_MODE_OPTIONS[assignMode]) return;

    const selectedAssignMode =
      (Object.entries(ASSIGN_MODE_OPTIONS).find(
        ([_, count]) => count >= connectedIMUTrackers.length
      )?.[0] as AssignMode) ?? AssignMode.All;

    if (assignMode !== selectedAssignMode) {
      setValue('assignMode', selectedAssignMode);
    }
  }, [connectedIMUTrackers, assignMode]);

  if (variant == 'dropdown')
    return (
      <Dropdown
        control={control}
        name="assignMode"
        display="block"
        direction="down"
        placeholder={''}
        items={Object.entries(ASSIGN_MODE_OPTIONS).map(
          ([mode, trackersCount]) => ({
            label: (
              <div className="flex flex-row gap-2 py-1 text-left">
                <ItemContent
                  mode={mode}
                  trackersCount={trackersCount}
                ></ItemContent>
              </div>
            ),
            value: mode,
          })
        )}
      ></Dropdown>
    );

  return Object.entries(ASSIGN_MODE_OPTIONS).map(([mode, trackersCount]) => (
    <Radio
      key={mode}
      name="assignMode"
      control={control}
      value={mode}
      disabled={
        connectedIMUTrackers.length > trackersCount && mode !== AssignMode.All
      }
      className="hidden"
    >
      <div className="flex flex-row md:gap-4 gap-2">
        <ItemContent mode={mode} trackersCount={trackersCount}></ItemContent>
      </div>
    </Radio>
  ));
}
