import { useOnboarding } from '@/hooks/onboarding';
import { useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { useForm } from 'react-hook-form';
import { Radio } from '@/components/commons/Radio';
import { Button } from '@/components/commons/Button';

export enum UsageReason {
  VR,
  VTUBING,
  MOCAP,
}

const REASON_TO_PATH: Record<UsageReason, string> = {
  [UsageReason.MOCAP]: '/onboarding/usage/mocap/choose',
  [UsageReason.VR]: '/onboarding/usage/vr/choose',
  [UsageReason.VTUBING]: '/onboarding/usage/vtubing/choose',
};

export function UsageChoose() {
  const { l10n } = useLocalization();
  const { applyProgress } = useOnboarding();
  const { control, watch } = useForm<{
    usageReason: UsageReason;
  }>({
    defaultValues: {
      usageReason: UsageReason.VR,
    },
  });

  const usageReason = watch('usageReason');

  const ItemContent = ({ mode }: { mode: UsageReason }) => (
    <>
      <Typography variant="main-title" textAlign="text-right">
        {l10n.getString('onboarding-usage-choose-option-title', {
          mode,
        })}
      </Typography>
      <div className="flex flex-col">
        <Typography>
          {l10n.getString('onboarding-usage-choose-option-label', {
            mode,
          })}
        </Typography>
        <Typography variant="standard" color="secondary">
          {l10n.getString('onboarding-usage-choose-option-description', {
            mode,
          })}
        </Typography>
      </div>
    </>
  );

  applyProgress(0.5);

  return (
    <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
      <div className="flex flex-col w-full overflow-y-auto px-4 xs:items-center">
        <div className="flex mobile:flex-col md:gap-8 mobile:gap-4 mobile:pb-4 w-full justify-evenly">
          <div className="flex flex-col xs:max-w-sm gap-3 justify-center">
            <Typography variant="main-title">
              {l10n.getString('onboarding-usage-choose')}
            </Typography>
            <Typography color="secondary">
              {l10n.getString('onboarding-usage-choose-description')}
            </Typography>
            {Object.values(UsageReason)
              .filter(checkIfUsageReason)
              .map((mode) => (
                <Radio
                  key={mode}
                  name="usageReason"
                  control={control}
                  value={mode.toString()}
                  className="hidden"
                >
                  <div className="flex flex-row md:gap-4 gap-2">
                    <ItemContent mode={mode}></ItemContent>
                  </div>
                </Radio>
              ))}
            <div className="flex flex-row">
              <Button variant="secondary" to="/onboarding/assign-tutorial">
                {l10n.getString('onboarding-previous_step')}
              </Button>
              <Button
                variant="primary"
                to={REASON_TO_PATH[usageReason]}
                className="ml-auto"
              >
                {l10n.getString('onboarding-enter_vr-ready')}
              </Button>
            </div>
          </div>
          <img
            className="mobile:hidden"
            src="/images/reset-pose.webp"
            width="100"
          ></img>
        </div>
      </div>
    </div>
  );
}

function checkIfUsageReason(val: any): val is UsageReason {
  return typeof val === 'number';
}
