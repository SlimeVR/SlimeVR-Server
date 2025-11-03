import { Control } from 'react-hook-form';
import { StayAlignedSettingsT } from 'solarxr-protocol';
import { SettingsForm } from '@/components/settings/pages/GeneralSettings';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import { Typography } from '@/components/commons/Typography';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPageLayout';
import { Localized, useLocalization } from '@fluent/react';
import { useAtomValue } from 'jotai';
import { connectedIMUTrackersAtom } from '@/store/app-store';
import { bodypartToString } from '@/utils/formatting';
import { useLocaleConfig } from '@/i18n/config';
import {
  FlatRelaxedPoseModal,
  SittingRelaxedPoseModal,
  StandingRelaxedPoseModal,
} from './StayAlignedPoseModal';
import { useState } from 'react';

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
  setupComplete: boolean;
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
  setupComplete: false,
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

function CopySettingsButton({ values }: { values: SettingsForm }) {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const numberFormat = new Intl.NumberFormat(currentLocales, {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  });

  const trackers = useAtomValue(connectedIMUTrackersAtom);

  function boolify(value: boolean) {
    return value ? 'true' : 'false';
  }

  const copySettings = () => {
    const config = values.stayAligned;

    const debug = `
Stay Aligned

GENERAL
=======
Enabled: ${config.enabled ? 'true' : 'false'}
Extra yaw correction: ${boolify(config.extraYawCorrection)}
Setup complete: ${boolify(config.setupComplete)}

RELAXED POSES
=============
Standing: ${config.standingEnabled ? `Enabled (upper_leg=${numberFormat.format(config.standingUpperLegAngle)}, lower_leg=${numberFormat.format(config.standingLowerLegAngle)}, foot=${numberFormat.format(config.standingFootAngle)})` : 'Not enabled'}
Sitting: ${config.sittingEnabled ? `Enabled (upper_leg=${numberFormat.format(config.sittingUpperLegAngle)}, lower_leg=${numberFormat.format(config.sittingLowerLegAngle)}, foot=${numberFormat.format(config.sittingFootAngle)})` : 'Not enabled'}
Flat: ${config.flatEnabled ? `Enabled (upper_leg=${numberFormat.format(config.flatUpperLegAngle)}, lower_leg=${numberFormat.format(config.flatLowerLegAngle)}, foot=${numberFormat.format(config.flatFootAngle)})` : 'Not enabled'}

TRACKERS
========
${trackers
  .map((t) => {
    const info = t.tracker.info;
    const stayAligned = t.tracker.stayAligned;
    if (info && stayAligned) {
      return `${bodypartToString(info.bodyPart)}: correction=${numberFormat.format(stayAligned.yawCorrectionInDeg)} locked=${stayAligned.locked ? `true locked_error=${numberFormat.format(stayAligned.lockedErrorInDeg)}` : 'false'} center_error=${numberFormat.format(stayAligned.centerErrorInDeg)} neighbor_error=${numberFormat.format(stayAligned.neighborErrorInDeg)}`;
    }
  })
  .join('\n')}

OTHER
=====
Filtering: type=${values.filtering.type} amount=${numberFormat.format(values.filtering.amount)}
Enforce constraints: ${boolify(values.toggles.enforceConstraints)}
Skating correction: ${boolify(values.toggles.skatingCorrection)}
`;

    navigator.clipboard.writeText(debug);
  };

  return (
    <Button variant="primary" onClick={copySettings}>
      {l10n.getString('settings-stay_aligned-debug-copy-label')}
    </Button>
  );
}

export function StayAlignedSettings({
  values,
  control,
}: {
  values: SettingsForm;
  control: Control<SettingsForm, any>;
}) {
  const { l10n } = useLocalization();

  const config = values.stayAligned;
  const hasStandingPose =
    config.standingEnabled ||
    config.standingUpperLegAngle !== 0.0 ||
    config.standingLowerLegAngle !== 0.0 ||
    config.standingFootAngle !== 0.0;
  const hasSittingPose =
    config.sittingEnabled ||
    config.sittingUpperLegAngle !== 0.0 ||
    config.sittingLowerLegAngle !== 0.0 ||
    config.sittingFootAngle !== 0.0;
  const hasFlatPose =
    config.flatEnabled ||
    config.flatUpperLegAngle !== 0.0 ||
    config.flatLowerLegAngle !== 0.0 ||
    config.flatFootAngle !== 0.0;

  const openStanding = useState(false);
  const openSitting = useState(false);
  const openFlat = useState(false);

  return (
    <>
      <SettingsPagePaneLayout icon={<WrenchIcon />} id="stayaligned">
        <Typography variant="main-title">
          {l10n.getString('settings-stay_aligned')}
        </Typography>
        <div className="mt-2">
          <Typography>
            {l10n.getString('settings-stay_aligned-description')}
          </Typography>
          <Typography>
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
        <div className="mt-6">
          <Typography variant="section-title">
            {l10n.getString('settings-stay_aligned-general-label')}
          </Typography>
          <div className="grid sm:grid-cols-2 gap-3 mt-2">
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="stayAligned.enabled"
              label={l10n.getString('settings-stay_aligned-enabled-label')}
              disabled={!config.setupComplete}
            />
            <CheckBox
              variant="toggle"
              outlined
              control={control}
              name="stayAligned.hideYawCorrection"
              label={l10n.getString(
                'settings-stay_aligned-hide_yaw_correction-label'
              )}
              disabled={!config.setupComplete}
            />
          </div>
        </div>
        <div className="mt-6">
          <Typography variant="section-title">
            {l10n.getString('settings-stay_aligned-relaxed_poses-label')}
          </Typography>
          <div className="mt-2">
            <Typography>
              {l10n.getString(
                'settings-stay_aligned-relaxed_poses-description'
              )}
            </Typography>
          </div>
          <div className="grid sm:grid-cols-1 gap-3 mt-2">
            <div className="flex gap-2">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="stayAligned.standingEnabled"
                label={l10n.getString(
                  'settings-stay_aligned-relaxed_poses-standing'
                )}
                disabled={!config.setupComplete || !hasStandingPose}
              />
              <StandingRelaxedPoseModal open={openStanding} />
              <Localized id="settings-stay_aligned-relaxed_poses-save_pose">
                <Button
                  variant="primary"
                  className="w-full max-w-32"
                  disabled={!config.setupComplete}
                  onClick={() => openStanding[1](true)}
                />
              </Localized>
            </div>
            <div className="flex gap-2">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="stayAligned.sittingEnabled"
                label={l10n.getString(
                  'settings-stay_aligned-relaxed_poses-sitting'
                )}
                disabled={!config.setupComplete || !hasSittingPose}
              />
              <SittingRelaxedPoseModal open={openSitting} />
              <Localized id="settings-stay_aligned-relaxed_poses-save_pose">
                <Button
                  variant="primary"
                  className="w-full max-w-32"
                  disabled={!config.setupComplete}
                  onClick={() => openSitting[1](true)}
                />
              </Localized>
            </div>
            <div className="flex gap-2">
              <CheckBox
                variant="toggle"
                outlined
                control={control}
                name="stayAligned.flatEnabled"
                label={l10n.getString(
                  'settings-stay_aligned-relaxed_poses-flat'
                )}
                disabled={!config.setupComplete || !hasFlatPose}
              />
              <FlatRelaxedPoseModal open={openFlat} />
              <Localized id="settings-stay_aligned-relaxed_poses-save_pose">
                <Button
                  variant="primary"
                  className="w-full max-w-32"
                  disabled={!config.setupComplete}
                  onClick={() => openFlat[1](true)}
                />
              </Localized>
            </div>
          </div>
        </div>
        <div className="mt-6">
          <Typography variant="section-title">
            {l10n.getString('settings-stay_aligned-debug-label')}
          </Typography>
          <div className="mt-2">
            <Typography>
              {l10n.getString('settings-stay_aligned-debug-description')}
            </Typography>
          </div>
          <div className="mt-2">
            <CopySettingsButton values={values} />
          </div>
        </div>
      </SettingsPagePaneLayout>
    </>
  );
}
