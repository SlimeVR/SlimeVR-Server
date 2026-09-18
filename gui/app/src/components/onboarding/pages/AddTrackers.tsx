import { useOnboarding } from '@/hooks/onboarding';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { WifiIcon } from '@/components/commons/icon/WifiIcon';
import { DongleSectionContent } from './Dongle';
import classNames from 'classnames';

function WifiSectionCard() {
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
            <WifiIcon variant="navbar" value={1} size={28} />
          </div>
          <Typography variant="main-title" id="onboarding-wifi_creds-v2" />
        </div>
        <div className="flex flex-col gap-2 flex-grow">
          <Typography
            whitespace="whitespace-pre-wrap"
            id="onboarding-wifi_creds-description-v2"
          />
        </div>
      </div>
      <div className="flex pt-4">
        <Button
          variant="primary"
          to="/onboarding/connect-trackers"
          state={{ alonePage: state.alonePage }}
          id="onboarding-wifi_creds-continue"
        />
      </div>
    </div>
  );
}

export function AddTrackersPage() {
  const { applyProgress } = useOnboarding();

  applyProgress(0.3);

  return (
    <div className="flex flex-col w-full h-full justify-center items-center p-6 overflow-y-auto">
      <div className="grid gap-6 max-w-5xl w-full md:grid-cols-2 items-stretch">
        <DongleSectionContent />
        <WifiSectionCard />
      </div>
    </div>
  );
}
