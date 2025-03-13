import { Control } from 'react-hook-form';
import { StayAlignedSettingsT } from 'solarxr-protocol';
import { SettingsForm } from '@/components/settings/pages/GeneralSettings';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPageLayout';
import { useLocalization } from '@fluent/react';
import { useConfig } from '@/hooks/config';
import {
  RelaxedPosesSettings,
  RelaxedPosesSummary,
} from '@/components/stay-aligned/RelaxedPose';

export type StayAlignedSettingsForm = {
  enabled: boolean;
  extraYawCorrection: boolean;
  hideYawCorrection: boolean;
  standingUpperLegAngle: number;
  standingLowerLegAngle: number;
  standingFootAngle: number;
  sittingUpperLegAngle: number;
  sittingLowerLegAngle: number;
  sittingFootAngle: number;
  flatUpperLegAngle: number;
  flatLowerLegAngle: number;
  flatFootAngle: number;
};

export const defaultStayAlignedSettings: StayAlignedSettingsForm = {
  enabled: false,
  extraYawCorrection: false,
  hideYawCorrection: false,
  standingUpperLegAngle: 0.0,
  standingLowerLegAngle: 0.0,
  standingFootAngle: 0.0,
  sittingUpperLegAngle: 0.0,
  sittingLowerLegAngle: 0.0,
  sittingFootAngle: 0.0,
  flatUpperLegAngle: 0.0,
  flatLowerLegAngle: 0.0,
  flatFootAngle: 0.0,
};

export function serializeStayAlignedSettings(
  settings: StayAlignedSettingsForm
): StayAlignedSettingsT {
  const serialized = new StayAlignedSettingsT();
  serialized.enabled = settings.enabled;
  serialized.extraYawCorrection = settings.extraYawCorrection;
  serialized.hideYawCorrection = settings.hideYawCorrection;
  serialized.standingUpperLegAngle = settings.standingUpperLegAngle;
  serialized.standingLowerLegAngle = settings.standingLowerLegAngle;
  serialized.standingFootAngle = settings.standingFootAngle;
  serialized.sittingUpperLegAngle = settings.sittingUpperLegAngle;
  serialized.sittingLowerLegAngle = settings.sittingLowerLegAngle;
  serialized.sittingFootAngle = settings.sittingFootAngle;
  serialized.flatUpperLegAngle = settings.flatUpperLegAngle;
  serialized.flatLowerLegAngle = settings.flatLowerLegAngle;
  serialized.flatFootAngle = settings.flatFootAngle;
  return serialized;
}

export function deserializeStayAlignedSettings(
  serialized: StayAlignedSettingsT
): StayAlignedSettingsForm {
  return serialized;
}

export function StayAlignedSettings({
  values,
  control,
}: {
  values: SettingsForm;
  control: Control<SettingsForm, any>;
}) {
  const { l10n } = useLocalization();
  const { config } = useConfig();

  return (
    <SettingsPagePaneLayout icon={<WrenchIcon />} id="stayaligned">
      <Typography variant="main-title">
        {l10n.getString('settings-stay_aligned')}
      </Typography>
      <div className="flex flex-col pt-2 pb-4">
        {l10n.getString('settings-stay_aligned-description')}
        {values.stayAligned.enabled && values.driftCompensation.enabled && (
          <div className="pt-2">
            {l10n.getString(
              'settings-stay_aligned-warnings-drift_compensation'
            )}
          </div>
        )}
      </div>
      <div className="grid sm:grid-cols-1 gap-3 pb-4">
        <div className="grid sm:grid-cols-2 gap-3 pb-3">
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="stayAligned.enabled"
            label={l10n.getString('settings-stay_aligned-enabled-label')}
          />
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="stayAligned.extraYawCorrection"
            label={l10n.getString(
              'settings-stay_aligned-extra_yaw_correction-label'
            )}
          />
          <CheckBox
            variant="toggle"
            outlined
            control={control}
            name="stayAligned.hideYawCorrection"
            label={l10n.getString(
              'settings-stay_aligned-hide_yaw_correction-label'
            )}
          />
        </div>
        <div className="flex flex-col pt-2">
          <Typography bold>
            {l10n.getString('settings-stay_aligned-relaxed_poses-label')}
          </Typography>
          <Typography color="secondary">
            {l10n.getString('settings-stay_aligned-relaxed_poses-description')}
          </Typography>
        </div>
        {config?.debug ? (
          <RelaxedPosesSettings control={control} />
        ) : (
          <RelaxedPosesSummary values={values} />
        )}
        <Button
          variant="primary"
          to="/onboarding/stay-aligned"
          state={{ alonePage: true }}
        >
          {l10n.getString('settings-stay_aligned-setup-label')}
        </Button>
      </div>
    </SettingsPagePaneLayout>
  );
}
