import { Dispatch, SetStateAction } from 'react';
import { BaseModal } from '@/components/commons/BaseModal';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { TrackingChecklistSettings } from '@/components/settings/pages/components/TrackingChecklistSettings';

export function TrackingChecklistModal({
  open,
}: {
  open: [boolean, Dispatch<SetStateAction<boolean>>];
}) {
  return (
    <BaseModal
      isOpen={open[0]}
      appendClasses={'max-w-xl md:w-full'}
      closeable
      onRequestClose={() => {
        open[1](false);
      }}
    >
      <div className="flex flex-col gap-4">
        <Typography variant="main-title" id="tracking_checklist-settings" />
        <div className="max-h-[60vh] overflow-y-auto min-w-0 -mr-2 pr-2">
          <TrackingChecklistSettings />
        </div>
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
