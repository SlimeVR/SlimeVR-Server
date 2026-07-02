import { SettingsPageLayout } from '@/components/settings/SettingsPageLayout';
import { OutputTrackersSettings } from './components/OutputSettings';
import { StayAlignedSettings } from './components/StayAlignedSettings';
import { ResetsSettings } from './components/ResetsSettings';
import { HIDSettings } from './components/HIDSettings';
import { SkeletonSettings } from './components/SkeletonSettings';
import { TapDetectionSettings } from './components/TapDetectionSettings';

export function GeneralSettings() {
  return (
    <SettingsPageLayout>
      <form className="flex flex-col gap-2 w-full">
        <OutputTrackersSettings />
        <StayAlignedSettings />
        <ResetsSettings />
        <HIDSettings />
        <SkeletonSettings />
        <TapDetectionSettings />
      </form>
    </SettingsPageLayout>
  );
}
