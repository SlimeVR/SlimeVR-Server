import { CheckBox } from '@/components/commons/Checkbox';
import {
  trackingchecklistIdtoLabel,
  useTrackingChecklist,
} from '@/hooks/tracking-checklist';
import { useLocalization } from '@fluent/react';
import { useEffect } from 'react';
import { FieldPath, useForm } from 'react-hook-form';
import { TrackingChecklistStepId } from 'solarxr-protocol';
import { SettingsGroup } from '@/components/settings/SettingRow';

type StepsForm = { steps: Record<TrackingChecklistStepId, boolean> };
export function TrackingChecklistSettings({
  variant,
}: {
  variant: 'settings' | 'modal';
}) {
  const { l10n } = useLocalization();
  const { ignoredSteps, steps, ignoreStep } = useTrackingChecklist();

  const { control, reset, handleSubmit } = useForm<StepsForm>({
    defaultValues: {
      steps: steps.reduce(
        (curr, { id }) => ({ [id]: !ignoredSteps.includes(id), ...curr }),
        {}
      ),
    },
    mode: 'onChange',
  });

  useEffect(() => {
    reset({
      steps: steps.reduce(
        (curr, { id }) => ({ [id]: !ignoredSteps.includes(id), ...curr }),
        {}
      ),
    });
  }, [ignoredSteps]);

  const onSubmit = (values: StepsForm) => {
    for (const [id, value] of Object.entries(values.steps)) {
      const stepId = +id;
      if (!stepId) continue;

      // doing it this way prevents calling ignore step for every step.
      // that prevent sending a packet for steps that didn't change
      if (!value && !ignoredSteps.includes(stepId)) {
        ignoreStep(stepId, true, false);
      }

      if (value && ignoredSteps.includes(stepId)) {
        ignoreStep(stepId, false, false);
      }
    }
  };

  return (
    <SettingsGroup
      title="settings-tracking_checklist-active_steps"
      description="settings-tracking_checklist-active_steps-desc"
    >
      <form
        className="grid md:grid-cols-2 gap-2 p-3 xs:p-4 mobile:flex flex-col"
        onChange={handleSubmit(onSubmit)}
      >
        {steps
          .filter((step) => step.enabled)
          .map((step) => (
            <div key={step.id}>
              <CheckBox
                control={control}
                name={`steps.${step.id}` as FieldPath<StepsForm>}
                disabled={!step.ignorable || !step.enabled}
                label={l10n.getString(trackingchecklistIdtoLabel[step.id])}
                outlined
                color={variant === 'settings' ? 'primary' : 'secondary'}
              />
            </div>
          ))}
      </form>
    </SettingsGroup>
  );
}
