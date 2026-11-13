import { Checklist } from '@/components/commons/icon/ChecklistIcon';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  TrackingChecklistSettings,
  checklistSettingsPage,
} from './components/TrackingChecklistSettings';

export function ChecklistSettings() {
  return (
    <SettingsPagePaneLayout
      icon={<Checklist size={22} />}
      title={checklistSettingsPage.title}
    >
      <TrackingChecklistSettings />
    </SettingsPagePaneLayout>
  );
}
