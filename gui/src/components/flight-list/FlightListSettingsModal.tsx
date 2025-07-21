import { Dispatch, SetStateAction } from 'react';
import { BaseModal } from '@/components/commons/BaseModal';
import {
  flightlistIdtoLabel,
  useSessionFlightlist,
} from '@/hooks/session-flightlist';
import { Typography } from '@/components/commons/Typography';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import { Button } from '../commons/Button';
import { CheckBox } from '../commons/Checkbox';
import { Form, useForm } from 'react-hook-form';

export function FlightListSettingsModal({
  open,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
}) {
  const { ignoredSteps, steps, toggle } = useSessionFlightlist();

  const { control } = useForm<{ steps: boolean[] }>({
    defaultValues: { steps: steps.map(({ id }) => ignoredSteps.includes(id)) },
  });

  return (
    <BaseModal
      isOpen={open[0]}
      appendClasses={'max-w-xl w-full'}
      closeable
      onRequestClose={() => {
        open[1](false);
      }}
    >
      <div className="flex flex-col gap-2">
        <Typography variant="main-title">Flight List Settings</Typography>
        <Typography>Ignored Steps</Typography>
        <form className="grid grid-cols-2 gap-2">
          {steps.map((step) => (
            <div
              className="bg-background-70 p-3 flex justify-between rounded-md gap-2 fill-background-10 items-center"
              onClick={() => toggle(step.id)}
            >
              <Typography id={flightlistIdtoLabel[step.id]}></Typography>
              <div>
                <CheckBox
                  control={control}
                  name={`steps.${step.id}`}
                  label=""
                ></CheckBox>
              </div>
            </div>
          ))}
        </form>
        <div className="flex justify-end">
          <Button variant="tertiary">Close</Button>
        </div>
      </div>
    </BaseModal>
  );
}
