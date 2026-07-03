import { Dispatch, SetStateAction } from 'react';
import { BaseModal } from '@/components/commons/BaseModal';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { TrackingChecklistSettings } from '@/components/settings/pages/HomeScreenSettings';

export function TrackingChecklistModal({
  open,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
}) {
  return (
    <BaseModal
      isOpen={open[0]}
      appendClasses={
        'max-w-xl md:w-full mobile:max-h-[400px] mobile:overflow-y-auto'
      }
      closeable
      onRequestClose={() => {
        open[1](false);
      }}
    >
      <div className="flex flex-col gap-4">
        <Typography variant="main-title" id="tracking_checklist-settings" />
        <TrackingChecklistSettings variant="modal" />
        <div className="flex justify-end">
          <Button
            variant="tertiary"
            onClick={() => open[1](false)}
            id="tracking_checklist-settings-close"
          />
        </div>
      </div>
    </BaseModal>
  );
}
