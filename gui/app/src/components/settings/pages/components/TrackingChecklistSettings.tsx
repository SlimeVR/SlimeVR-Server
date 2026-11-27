import { defineSettingsPage } from '@/components/settings/settings-definitions';
import { CheckBox } from '@/components/commons/Checkbox';
import {
  trackingchecklistIdtoLabel,
  trackingchecklistIdtoSettingsDescription,
  useTrackingChecklist,
} from '@/hooks/tracking-checklist';
import { useEffect } from 'react';
import { FieldPath, useForm } from 'react-hook-form';
import { TrackingChecklistStepId } from 'solarxr-protocol';
import { SettingRow, SettingsGroup } from '@/components/settings/SettingRow';

type StepsForm = { steps: Record<TrackingChecklistStepId, boolean> };

const stepIds = Object.values(TrackingChecklistStepId).filter(
  (id): id is TrackingChecklistStepId =>
    typeof id === 'number' && id !== TrackingChecklistStepId.UNKNOWN
);
const toFormSteps = (ignoredSteps: TrackingChecklistStepId[]) =>
  Object.fromEntries(
    stepIds.map((id) => [id, !ignoredSteps.includes(id)])
  ) as Record<TrackingChecklistStepId, boolean>;

const stepRowName = (id: TrackingChecklistStepId) =>
  TrackingChecklistStepId[id];

export const checklistSettingsPage = defineSettingsPage({
  path: 'checklist',
  title: 'settings-sidebar-checklist',
  groups: {
    activeSteps: {
      title: 'settings-tracking_checklist-active_steps',
      description: 'settings-tracking_checklist-active_steps-desc',
      rows: Object.fromEntries(
        stepIds.map((id) => [
          stepRowName(id),
          {
            title: trackingchecklistIdtoLabel[id],
            description: trackingchecklistIdtoSettingsDescription[id],
          },
        ])
      ),
    },
  },
});

export function TrackingChecklistSettings() {
  const { ignoredSteps, steps, ignoreStep } = useTrackingChecklist();

  const { control, reset, handleSubmit } = useForm<StepsForm>({
    defaultValues: {
      steps: toFormSteps(ignoredSteps),
    },
    mode: 'onChange',
  });

  useEffect(() => {
    reset({
      steps: toFormSteps(ignoredSteps),
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

  const group = checklistSettingsPage.groups.activeSteps;
  const position = (id: TrackingChecklistStepId) => {
    const index = steps.findIndex((step) => step.id === id);
    return index === -1 ? steps.length : index;
  };
  const orderedIds = [...stepIds].sort((a, b) => position(a) - position(b));

  return (
    <form onChange={handleSubmit(onSubmit)}>
      <SettingsGroup group={group}>
        {orderedIds.map((id) => (
          <SettingRow
            key={id}
            setting={group.rows[stepRowName(id)]}
            controlId={`checklist-step-${id}`}
            control={
              <CheckBox
                variant="toggle"
                control={control}
                name={`steps.${id}` as FieldPath<StepsForm>}
                id={`checklist-step-${id}`}
              />
            }
          />
        ))}
      </SettingsGroup>
    </form>
  );
}
