import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { OutputTrackersSettings } from './components/OutputSettings';
import { StayAlignedSettings } from './components/StayAlignedSettings';
import { ResetsSettings } from './components/ResetsSettings';
import { HIDSettings } from './components/HIDSettings';
import { TrackingSettings } from './components/TrackingSettings';
import { TapDetectionSettings } from './components/TapDetectionSettings';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcon';
import { MagnetometerToggleSetting } from './components/MagnetometerToggleSetting';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { FullResetIcon } from '@/components/commons/icon/ResetIcon';

export function GroupedResetsSettings() {
  const { l10n } = useLocalization();

  return (
    <SettingsPagePaneLayout icon={<FullResetIcon width={20} />} id="resets">
      <>
        <div className="flex flex-col pb-3">
          <Typography variant="main-title">
            {l10n.getString('settings-general-fk_settings-resets_settings')}
          </Typography>
        </div>

        <TapDetectionSettings />
        <ResetsSettings />
      </>
    </SettingsPagePaneLayout>
  );
}

export function GroupedTrackersSettings() {
  const { l10n } = useLocalization();

  return (
    <SettingsPagePaneLayout icon={<WrenchIcon />} id="trackers">
      <>
        <div className="flex flex-col pb-3">
          <Typography variant="main-title">
            {l10n.getString('settings-general-trackers_settings')}
          </Typography>
        </div>

        <HIDSettings />
        <MagnetometerToggleSetting
          settingType="general"
          id="mechanics-magnetometer"
        />
      </>
    </SettingsPagePaneLayout>
  );
}

export function GeneralSettings() {
  return (
    <SettingsPageLayout>
      <div className="flex flex-col gap-2 w-full">
        <OutputTrackersSettings />
        <StayAlignedSettings />
        <TrackingSettings />
        <GroupedResetsSettings />
        <GroupedTrackersSettings />
      </div>
    </SettingsPageLayout>
  );
}
