import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { AssignMode, defaultConfig, useConfig } from '@/hooks/config';
import { ASSIGNMENT_MODES } from '@/components/onboarding/BodyAssignment';
import { useLocalization } from '@fluent/react';
import { useTrackers } from '@/hooks/tracker';
import { useForm } from 'react-hook-form';
import { useEffect } from 'react';
import { Dropdown } from '@/components/commons/Dropdown';

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

export function TrackerAssignOptions({
  variant = 'radio',
}: {
  variant: 'radio' | 'dropdown';
}) {
  const { l10n } = useLocalization();
  const { useConnectedIMUTrackers } = useTrackers();
  const connectedIMUTrackers = useConnectedIMUTrackers().length;

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
    if (connectedIMUTrackers <= ASSIGN_MODE_OPTIONS[assignMode]) return;

    const selectedAssignMode =
      (Object.entries(ASSIGN_MODE_OPTIONS).find(
        ([_, count]) => count >= connectedIMUTrackers
      )?.[0] as AssignMode) ?? AssignMode.All;

    if (assignMode !== selectedAssignMode) {
      setValue('assignMode', selectedAssignMode);
    }
  }, [connectedIMUTrackers, assignMode]);

  const ItemContent = ({
    mode,
    trackersCount,
  }: {
    mode: string;
    trackersCount: number;
  }) => (
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
        <Typography variant="standard" color="secondary">
          {l10n.getString('onboarding-assign_trackers-option-description', {
            mode,
          })}
        </Typography>
      </div>
    </>
  );

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
            component: (
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
      disabled={connectedIMUTrackers > trackersCount && mode !== AssignMode.All}
      className="hidden"
    >
      <div className="flex flex-row md:gap-4 gap-2">
        <ItemContent mode={mode} trackersCount={trackersCount}></ItemContent>
      </div>
    </Radio>
  ));
}
