import classNames from 'classnames';
import { StayAlignedPoseT } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { CheckboxInternal } from '@/components/commons/Checkbox';
import { StatusBadge, StatusVariant } from '@/components/commons/StatusBadge';
import { Tooltip } from '@/components/commons/Tooltip';
import { Typography } from '@/components/commons/Typography';
import { SettingResetButton } from '@/components/settings/SettingReset';
import { useFocusSetting } from '@/components/settings/SettingRow';
import { SettingDef } from '@/components/settings/settings-definitions';
import {
  POSE_DESCRIPTION,
  POSE_IMAGE,
  POSE_KEY,
  POSE_TITLE,
  RecordablePose,
} from '@/hooks/stay-aligned';

function sourceBadge(pose: StayAlignedPoseT): {
  id: string;
  variant: StatusVariant;
} {
  if (pose.recorded) {
    return { id: 'settings-stay_aligned-badge-recorded', variant: 'success' };
  }
  if (pose.hasDefault) {
    return { id: 'settings-stay_aligned-badge-default', variant: 'special' };
  }
  return { id: 'settings-stay_aligned-badge-not_recorded', variant: 'neutral' };
}

export function PoseRow({
  setting,
  poseId,
  pose,
  onRecord,
  onToggle,
  onReset,
  loading,
  suggested,
  reset,
}: {
  setting: SettingDef;
  poseId: RecordablePose;
  pose: StayAlignedPoseT;
  loading: boolean;
  suggested: boolean;
  reset: boolean;
  onRecord: () => void;
  onToggle: (enabled: boolean) => void;
  onReset: () => void;
}) {
  const { ref, highlighted } = useFocusSetting<HTMLFieldSetElement>(
    setting.key
  );
  const key = POSE_KEY[poseId];
  const badge = sourceBadge(pose);
  const toggleBlocked = !pose.recorded && !pose.hasDefault;
  const recordId = pose.recorded
    ? 'settings-stay_aligned-record_again'
    : 'settings-stay_aligned-record';

  return (
    <fieldset
      ref={ref}
      className={classNames(
        'flex items-start gap-2 min-w-0 pl-1 pr-3 py-3 xs:pl-2 xs:pr-4 xs:py-4 transition-colors',
        highlighted ? 'bg-accent-background-40/30' : 'hover:bg-background-70/30'
      )}
    >
      <div className="w-7 h-6 shrink-0 flex items-center justify-center">
        <SettingResetButton modified={reset} onReset={onReset} />
      </div>
      <div className="grid flex-1 min-w-0 items-start gap-x-4 gap-y-2 grid-cols-[64px_minmax(0,1fr)_auto] mobile:grid-cols-[48px_minmax(0,1fr)_auto] mobile:gap-x-3">
        <div className="row-span-2 mobile:row-span-1 flex h-16 w-16 mobile:h-12 mobile:w-12 items-center justify-center overflow-hidden rounded-md bg-background-70">
          <img
            src={POSE_IMAGE[poseId]}
            className="max-h-full max-w-full object-contain"
          />
        </div>
        <div className="col-start-2 flex min-w-0 flex-wrap items-center gap-x-2.5 gap-y-1 self-center">
          <Typography variant="section-title" id={POSE_TITLE[poseId]} />
          <StatusBadge variant={badge.variant} id={badge.id} />
        </div>
        <div className="col-start-2 min-w-0 max-w-[75ch] mobile:col-span-3 mobile:col-start-1">
          <Typography id={POSE_DESCRIPTION[poseId]} />
        </div>
        <div className="col-start-3 row-start-1 flex items-center gap-3 self-center xs:row-span-2 xs:row-start-1 xs:gap-5">
          <Button
            variant={suggested ? 'primary' : 'secondary'}
            className="w-36 shrink-0 whitespace-nowrap mobile:hidden"
            onClick={onRecord}
            disabled={loading}
            id={recordId}
          />
          <div className="w-10 shrink-0">
            <Tooltip
              content={
                <Typography id="settings-stay_aligned-toggle-record_first" />
              }
              preferedDirection="top"
              disabled={!toggleBlocked}
            >
              <CheckboxInternal
                variant="toggle"
                name={`stay-aligned-${key}`}
                id={`stay-aligned-${key}`}
                checked={pose.enabled}
                loading={loading}
                disabled={toggleBlocked}
                onChange={(event) => onToggle(event.currentTarget.checked)}
              />
            </Tooltip>
          </div>
        </div>
        <Button
          variant={suggested ? 'primary' : 'secondary'}
          className="col-span-3 w-full xs:hidden"
          onClick={onRecord}
          disabled={loading}
          id={recordId}
        />
      </div>
    </fieldset>
  );
}
