import { HomeIcon } from '@/components/commons/icon/HomeIcon';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  HomeLayoutSettings,
  homeSettingsPage,
} from './components/HomeLayoutSettings';

export function HomeScreenSettings() {
  return (
    <SettingsPagePaneLayout icon={<HomeIcon />} title={homeSettingsPage.title}>
      <HomeLayoutSettings variant="settings" />
    </SettingsPagePaneLayout>
  );
}
