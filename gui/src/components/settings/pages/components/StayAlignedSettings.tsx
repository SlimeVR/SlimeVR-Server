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
  standingEnabled: boolean;
  standingUpperLegAngle: number;
  standingLowerLegAngle: number;
  standingFootAngle: number;
  sittingEnabled: boolean;
  sittingUpperLegAngle: number;
  sittingLowerLegAngle: number;
  sittingFootAngle: number;
  flatEnabled: boolean;
  flatUpperLegAngle: number;
  flatLowerLegAngle: number;
  flatFootAngle: number;
};

export const defaultStayAlignedSettings: StayAlignedSettingsForm = {
  enabled: false,
  extraYawCorrection: false,
  hideYawCorrection: false,
  standingEnabled: false,
  standingUpperLegAngle: 0.0,
  standingLowerLegAngle: 0.0,
  standingFootAngle: 0.0,
  sittingEnabled: false,
  sittingUpperLegAngle: 0.0,
  sittingLowerLegAngle: 0.0,
  sittingFootAngle: 0.0,
  flatEnabled: false,
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
  serialized.standingEnabled = settings.standingEnabled;
  serialized.standingUpperLegAngle = settings.standingUpperLegAngle;
  serialized.standingLowerLegAngle = settings.standingLowerLegAngle;
  serialized.standingFootAngle = settings.standingFootAngle;
  serialized.sittingEnabled = settings.sittingEnabled;
  serialized.sittingUpperLegAngle = settings.sittingUpperLegAngle;
  serialized.sittingLowerLegAngle = settings.sittingLowerLegAngle;
  serialized.sittingFootAngle = settings.sittingFootAngle;
  serialized.flatEnabled = settings.flatEnabled;
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
      <div className="mt-2">
        <Typography color="secondary">
          {l10n.getString('settings-stay_aligned-description')}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('settings-stay_aligned-setup-description')}
        </Typography>
        <div className="flex mt-2">
          <Button
            variant="primary"
            to="/onboarding/stay-aligned"
            state={{ alonePage: true }}
          >
            {l10n.getString('settings-stay_aligned-setup-label')}
          </Button>
        </div>
      </div>
      <div className="mt-4">
        <Typography bold>
          {l10n.getString('settings-stay_aligned-general-label')}
        </Typography>
        {values.stayAligned.enabled && values.driftCompensation.enabled && (
          <div className="mt-2">
            {l10n.getString(
              'settings-stay_aligned-warnings-drift_compensation'
            )}
          </div>
        )}
        <div className="grid sm:grid-cols-2 gap-3 mt-2">
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
      </div>
      <div className="mt-4">
        <Typography bold>
          {l10n.getString('settings-stay_aligned-relaxed_poses-label')}
        </Typography>
        <div className="grid sm:grid-cols-1 gap-3 mt-2">
          {config?.debug ? (
            <RelaxedPosesSettings control={control} />
          ) : (
            <RelaxedPosesSummary values={values} />
          )}
        </div>
      </div>
    </SettingsPagePaneLayout>
  );
}
