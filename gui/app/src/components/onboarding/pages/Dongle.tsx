import { Localized } from '@fluent/react';
import { useOnboarding } from '@/hooks/onboarding';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import classNames from 'classnames';
import { USBIcon } from '@/components/commons/icon/UsbIcon';
import { WarningBox } from '@/components/commons/TipBox';
import { MoreSetsConfirm } from './quiz/MoreSetsConfirm';

export function DongleSectionContent() {
  const { state } = useOnboarding();

  return (
    <div
      className={classNames(
        'flex flex-col gap-4 p-6 rounded-lg justify-between border border-background-50/50',
        state.alonePage ? 'bg-background-60' : 'bg-background-70'
      )}
    >
      <div className="flex flex-col gap-4">
        <div className="flex gap-3 items-center">
          <div className="bg-accent-background-30 rounded-full p-3 fill-background-10">
            <USBIcon size={28} />
          </div>
          <Typography
            variant="main-title"
            id="onboarding-wifi_creds-dongle-title"
          />
        </div>
        <div className="flex flex-col gap-3 flex-grow">
          <Typography
            whitespace="whitespace-pre-wrap"
            id="onboarding-wifi_creds-dongle-description"
          />
          <Localized id="onboarding-wifi_creds-dongle-wip">
            <WarningBox whitespace>WARNING</WarningBox>
          </Localized>
        </div>
      </div>
      <div className="flex pt-4">
        {state.alonePage && (
          <Button
            variant="primary"
            to={'/'}
            id="onboarding-wifi_creds-dongle-continue"
          />
        )}
        {!state.alonePage && <MoreSetsConfirm />}
      </div>
    </div>
  );
}

export function DonglePage() {
  const { applyProgress } = useOnboarding();

  applyProgress(0.5);

  return (
    <div className="flex flex-col w-full h-full xs:justify-center items-center">
      <DongleSectionContent />
    </div>
  );
}
