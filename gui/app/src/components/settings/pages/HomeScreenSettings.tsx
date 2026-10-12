import { HomeIcon } from '@/components/commons/icon/HomeIcon';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { HomeLayoutSettings } from './components/HomeLayoutSettings';

export function HomeScreenSettings() {
  return (
    <SettingsPagePaneLayout icon={<HomeIcon />}>
      <Typography variant="main-title" id="home-settings" />
      <HomeLayoutSettings variant="settings" />
    </SettingsPagePaneLayout>
  );
}
