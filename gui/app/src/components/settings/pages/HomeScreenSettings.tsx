import { HomeIcon } from '@/components/commons/icon/HomeIcon';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { HomeLayoutSettings } from './components/HomeLayoutSettings';

export function HomeScreenSettings() {
  return (
    <SettingsPagePaneLayout icon={<HomeIcon />} title="home-settings">
      <HomeLayoutSettings variant="settings" />
    </SettingsPagePaneLayout>
  );
}
