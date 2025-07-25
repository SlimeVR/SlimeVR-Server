import { Dispatch, SetStateAction, useEffect } from 'react';
import { BaseModal } from '@/components/commons/BaseModal';
import {
  flightlistIdtoLabel,
  useSessionFlightlist,
} from '@/hooks/session-flightlist';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { useForm } from 'react-hook-form';
import { useLocalization } from '@fluent/react';
import { FlightListStepId } from 'solarxr-protocol';

type StepsForm = { steps: Record<FlightListStepId, boolean> };
export function FlightListSettingsModal({
  open,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
}) {
  const { l10n } = useLocalization();
  const { ignoredSteps, steps, ignoreStep } = useSessionFlightlist();

  const { control, reset, handleSubmit, watch, getValues } = useForm<StepsForm>(
    {
      defaultValues: {
        steps: steps.reduce(
          (curr, { id }) => ({ [id]: !ignoredSteps.includes(id), ...curr }),
          {}
        ),
      },
      mode: 'onChange',
    }
  );

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
      // that prevent sending a packet for steps that didnt change
      if (!value && !ignoredSteps.includes(stepId)) {
        ignoreStep(stepId, true);
      }

      if (value && ignoredSteps.includes(stepId)) {
        ignoreStep(stepId, false);
      }
    }
  };

  return (
    <BaseModal
      isOpen={open[0]}
      appendClasses={'max-w-xl w-full'}
      closeable
      onRequestClose={() => {
        open[1](false);
      }}
    >
      <div className="flex flex-col gap-4">
        <Typography variant="main-title">
          Tracking Checklist Settings
        </Typography>
        <div className="flex flex-col">
          <div className="flex flex-col pt-4 pb-2">
            <Typography bold>Active Steps</Typography>
            <Typography color="secondary">
              List all the steps that will show in the tracking checklist. You
              can either disable or enable ignorable steps
            </Typography>
          </div>
          <form
            className="grid grid-cols-2 gap-2"
            onChange={handleSubmit(onSubmit)}
          >
            {steps
              .filter((step) => step.enabled)
              .map((step) => (
                <div key={step.id}>
                  <CheckBox
                    control={control}
                    name={`steps.${step.id}`}
                    disabled={!step.ignorable || !step.enabled}
                    label={l10n.getString(flightlistIdtoLabel[step.id])}
                    outlined
                    color={'secondary'}
                  ></CheckBox>
                </div>
              ))}
          </form>
        </div>

        <div className="flex justify-end">
          <Button variant="tertiary" onClick={() => open[1](false)}>
            Close
          </Button>
        </div>
      </div>
    </BaseModal>
  );
}
