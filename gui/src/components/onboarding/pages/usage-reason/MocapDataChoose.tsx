import { useOnboarding } from '@/hooks/onboarding';
import { useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { useForm } from 'react-hook-form';
import { Radio } from '@/components/commons/Radio';
import classNames from 'classnames';
import { useMemo } from 'react';
import { MocapVMCSetup } from './MocapVMCSetup';
import { Button } from '@/components/commons/Button';

export enum MocapDataType {
  BVH,
  STEAMVR,
  VMC,
}

export function MocapDataChoose() {
  const { l10n } = useLocalization();
  const { applyProgress } = useOnboarding();
  const { control } = useForm<{
    usageReason: MocapDataType;
  }>({
    defaultValues: {
      usageReason: MocapDataType.VMC,
    },
  });

  // const usageReason = watch('usageReason');

  const ItemContent = ({ mode }: { mode: MocapDataType }) => (
    <>
      <div
        className={classNames(
          'flex bg-background-60 py-2 px-4 group-hover/radio:bg-background-50 rounded-t-md'
        )}
      >
        <Typography variant="main-title">
          {l10n.getString('onboarding-usage-mocap-data_choose-option-title', {
            mode: MocapDataType[mode],
          })}
        </Typography>
      </div>
      <div className="flex flex-col bg-background-70 group-hover/radio:bg-background-60 rounded-b-md py-2 px-4">
        <Typography>
          {l10n.getString('onboarding-usage-mocap-data_choose-option-label', {
            mode: MocapDataType[mode],
          })}
        </Typography>
        <Typography variant="standard" color="secondary">
          {l10n.getString(
            'onboarding-usage-mocap-data_choose-option-description',
            {
              mode: MocapDataType[mode],
            }
          )}
        </Typography>
      </div>
    </>
  );

  const usages = useMemo(
    () =>
      Object.values(MocapDataType)
        .filter(checkIfUsageReason)
        .map((mode) => (
          <Radio
            key={mode}
            name="usageReason"
            control={control}
            value={mode.toString()}
            variant="none"
            className="hidden"
          >
            <div>
              <ItemContent mode={mode}></ItemContent>
            </div>
          </Radio>
        )),
    [control, l10n]
  );

  applyProgress(0.6);

  return (
    <div className="flex flex-col gap-5 h-full items-center w-full justify-center">
      <div className="flex flex-col w-full overflow-y-auto px-4 xs:items-center">
        <div className="flex mobile:flex-col xs:gap-8 mobile:gap-4 mobile:pb-4 w-full justify-center">
          <div className="flex flex-col xs:max-w-sm gap-3 justify-center">
            <Typography variant="main-title">
              {l10n.getString('onboarding-usage-mocap-data_choose')}
            </Typography>
            <Typography color="secondary">
              {l10n.getString('onboarding-usage-mocap-data_choose-description')}
            </Typography>
            {usages}
            <div className="flex flex-row">
              <Button
                variant="secondary"
                to="/onboarding/usage/mocap/head-choose"
              >
                {l10n.getString('onboarding-previous_step')}
              </Button>
            </div>
          </div>
          <MocapVMCSetup />
        </div>
      </div>
    </div>
  );
}

function checkIfUsageReason(val: any): val is MocapDataType {
  return typeof val === 'number';
}
