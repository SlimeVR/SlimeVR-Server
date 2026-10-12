import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
import { MagnetometerToggleSetting } from './components/MagnetometerToggleSetting';
import { TimeoutSettings } from './components/TimeoutSettings';
import { HIDSettings } from './components/HIDSettings';

export function TrackersSettings() {
  return (
    <SettingsPagePaneLayout icon={<WrenchIcon />}>
      <div className="flex flex-col pb-3">
        <Typography variant="main-title" id="settings-sidebar-trackers" />
      </div>

      <MagnetometerToggleSetting settingType="general" />
      <TimeoutSettings />
      <HIDSettings />
    </SettingsPagePaneLayout>
  );
}
