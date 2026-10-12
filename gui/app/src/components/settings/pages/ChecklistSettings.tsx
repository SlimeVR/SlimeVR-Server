import { CheckIcon } from '@/components/commons/icon/CheckIcon';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { TrackingChecklistSettings } from './components/TrackingChecklistSettings';

export function ChecklistSettings() {
  return (
    <SettingsPagePaneLayout icon={<CheckIcon size={18} />}>
      <Typography variant="main-title" id="tracking_checklist" />
      <TrackingChecklistSettings variant="settings" />
    </SettingsPagePaneLayout>
  );
}
