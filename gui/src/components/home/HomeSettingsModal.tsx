import { Dispatch, SetStateAction } from 'react';
import { BaseModal } from '@/components/commons/BaseModal';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { HomeLayoutSettings } from '@/components/settings/pages/HomeScreenSettings';

export function HomeSettingsModal({
  open,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
}) {
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
        <Typography variant="main-title" id="home-settings" />
        <HomeLayoutSettings variant="modal" />
        <div className="flex justify-end">
          <Button
            variant="tertiary"
            onClick={() => open[1](false)}
            id="home-settings-close"
          />
        </div>
      </div>
    </BaseModal>
  );
}
