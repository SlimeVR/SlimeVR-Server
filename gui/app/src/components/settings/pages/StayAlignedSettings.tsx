import { useLocalization } from '@fluent/react';
import { useState } from 'react';
import { FieldValues } from 'react-hook-form';
import {
  StayAlignedPoseState,
  StayAlignedRelaxedPose,
  StayAlignedSettingsResponseT,
} from 'solarxr-protocol';
import { useAtomValue } from 'jotai';
import { Button } from '@/components/commons/Button';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { HorizontalAlignIcon } from '@/components/commons/icon/HorizontalAlignIcon';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import {
  SettingRow,
  SettingsAdvanced,
  SettingsGroup,
} from '@/components/settings/SettingRow';
import { settingReset } from '@/components/settings/SettingReset';
import { defineSettingsPage } from '@/components/settings/settings-definitions';
import { PoseRecorderModal } from '@/components/stay-aligned/PoseRecorderModal';
import { PoseRow } from '@/components/stay-aligned/PoseRow';
import { StayAlignedStatusRow } from '@/components/stay-aligned/StayAlignedStatusRow';
import {
  POSE_KEY,
  POSE_ORDER,
  RecordablePose,
  useStayAlignedSettings,
  useStayAlignedStatus,
} from '@/hooks/stay-aligned';
import { useSettingsDefaults } from '@/hooks/settings-defaults';
import { useLocaleConfig } from '@/i18n/config';
import { connectedIMUTrackersAtom } from '@/store/app-store';
import { bodypartToString } from '@/utils/formatting';

function CopySettingsButton({
  settings,
}: {
  settings: StayAlignedSettingsResponseT;
}) {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const numberFormat = new Intl.NumberFormat(currentLocales, {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  });

  const trackers = useAtomValue(connectedIMUTrackersAtom);

  const copySettings = () => {
    const poses = settings.poses
      .map((pose) => {
        const source = pose.recorded
          ? 'recorded'
          : pose.hasDefault
            ? 'default'
            : 'not recorded';
        const angles = `upper_leg=${numberFormat.format(pose.upperLegAngle)}, lower_leg=${numberFormat.format(pose.lowerLegAngle)}, foot=${numberFormat.format(pose.footAngle)}`;
        return `${StayAlignedRelaxedPose[pose.pose]}: ${pose.enabled ? 'enabled' : 'not enabled'}, ${source} (${angles})`;
      })
      .join('\n');

    const debug = `
Stay Aligned debug info:

GENERAL
=======
Enabled: ${settings.enabled}

RELAXED POSES
=============
${poses}

TRACKERS
========
${trackers
  .map((t) => {
    const info = t.tracker.info;
    const stayAligned = t.tracker.stayAligned;
    if (info && stayAligned) {
      return `${bodypartToString(info.bodyPart)}: correction=${numberFormat.format(stayAligned.yawCorrectionInDeg)} locked=${stayAligned.locked}`;
    }
  })
  .join('\n')}
`;

    navigator.clipboard.writeText(debug);
  };

  return (
    <Button
      variant="secondary"
      className="whitespace-nowrap"
      onClick={copySettings}
    >
      {l10n.getString('settings-stay_aligned-debug-copy-short')}
    </Button>
  );
}

export const stayAlignedSettingsPage = defineSettingsPage({
  path: 'stay-aligned',
  title: 'settings-sidebar-stay_aligned',
  groups: {
    general: {
      rows: {
        enabled: {
          title: 'settings-stay_aligned-enabled-label',
          description: 'settings-stay_aligned-enabled-label-description',
        },
      },
    },
    status: {
      title: 'settings-stay_aligned-status_group',
      rows: {},
    },
    poses: {
      title: 'settings-stay_aligned-poses_group',
      description: 'settings-stay_aligned-poses_group-description',
      rows: {
        standing: {
          title: 'settings-stay_aligned-pose-standing',
          description: 'settings-stay_aligned-pose-standing-description',
        },
        sitting: {
          title: 'settings-stay_aligned-pose-sitting',
          description: 'settings-stay_aligned-pose-sitting-description',
        },
        flat: {
          title: 'settings-stay_aligned-pose-flat',
          description: 'settings-stay_aligned-pose-flat-description',
        },
      },
    },
    debug: {
      title: 'settings-stay_aligned-debug-label',
      rows: {
        copy: {
          title: 'settings-stay_aligned-debug-copy-label',
          description: 'settings-stay_aligned-debug-description',
        },
      },
    },
  },
});

const { groups } = stayAlignedSettingsPage;

export function StayAlignedSettings() {
  const { settings, setEnabled, setPoseEnabled, resetPose } =
    useStayAlignedSettings();
  const status = useStayAlignedStatus();
  const defaults = useSettingsDefaults().stayAligned;
  const [recordingPose, setRecordingPose] = useState<RecordablePose | null>(
    null
  );

  // The defaults fill the page until the real settings arrive, the controls wait with a loading state
  const loaded = settings !== null;
  const shown = settings ?? defaults;

  return (
    <SettingsPagePaneLayout
      icon={<HorizontalAlignIcon />}
      title={stayAlignedSettingsPage.title}
    >
      <div className="flex flex-col">
        <div className="flex flex-col gap-1 pb-4 max-w-[75ch]">
          <Typography id="settings-stay_aligned-description" />
        </div>

        <SettingsGroup>
          <SettingRow
            setting={groups.general.rows.enabled}
            controlId="stay-aligned-enabled"
            {...settingReset<FieldValues, string>(
              defaults.enabled,
              shown.enabled,
              setEnabled
            )}
            control={
              <CheckboxInternal
                variant="toggle"
                name="stay-aligned-enabled"
                id="stay-aligned-enabled"
                checked={shown.enabled}
                loading={!loaded}
                onChange={(event) => setEnabled(event.currentTarget.checked)}
              />
            }
          />
        </SettingsGroup>

        <SettingsGroup group={groups.status}>
          <div className="px-3 py-1 xs:px-4">
            <StayAlignedStatusRow status={status} />
          </div>
        </SettingsGroup>

        <SettingsGroup group={groups.poses}>
          {POSE_ORDER.map((poseId) => {
            const pose = shown.poses.find(
              (candidate) => candidate.pose === poseId
            );
            if (!pose) return null;
            return (
              <PoseRow
                key={poseId}
                setting={groups.poses.rows[POSE_KEY[poseId]]}
                poseId={poseId}
                pose={pose}
                loading={!loaded}
                reset={pose.recorded}
                suggested={
                  status?.detectedPose?.pose === poseId &&
                  status.detectedPose.state ===
                    StayAlignedPoseState.NOT_RECORDED
                }
                onRecord={() => setRecordingPose(poseId)}
                onToggle={(enabled) => setPoseEnabled(poseId, enabled)}
                onReset={() => resetPose(poseId)}
              />
            );
          })}
        </SettingsGroup>

        <SettingsAdvanced>
          <SettingsGroup group={groups.debug}>
            <SettingRow
              setting={groups.debug.rows.copy}
              control={<CopySettingsButton settings={shown} />}
            />
          </SettingsGroup>
        </SettingsAdvanced>
      </div>

      <PoseRecorderModal
        pose={recordingPose}
        status={status}
        onClose={() => setRecordingPose(null)}
      />
    </SettingsPagePaneLayout>
  );
}
