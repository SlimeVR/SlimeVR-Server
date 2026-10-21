import { useLocalization } from '@fluent/react';
import { useMemo } from 'react';
import {
  BodyPart,
  StayAlignedPoseState,
  StayAlignedStatusResponseT,
} from 'solarxr-protocol';
import {
  StatusBadge,
  StatusRow,
  StatusVariant,
} from '@/components/commons/StatusBadge';
import { Typography } from '@/components/commons/Typography';
import { useLocaleConfig } from '@/i18n/config';
import { PART_LABEL, POSITION_LABEL } from '@/hooks/stay-aligned';

interface StatusLine {
  variant: StatusVariant;
  badge: string;
  text: string;
}

const POSE_STATUS: Record<StayAlignedPoseState, StatusLine> = {
  [StayAlignedPoseState.ENABLED]: {
    variant: 'success',
    badge: 'settings-stay_aligned-status-badge-pose-ready',
    text: 'settings-stay_aligned-status-pose-ready',
  },
  [StayAlignedPoseState.NOT_RECORDED]: {
    variant: 'warning',
    badge: 'settings-stay_aligned-status-badge-pose-not_recorded',
    text: 'settings-stay_aligned-status-pose-not_recorded',
  },
  [StayAlignedPoseState.DISABLED]: {
    variant: 'neutral',
    badge: 'settings-stay_aligned-status-badge-pose-disabled',
    text: 'settings-stay_aligned-status-pose-disabled',
  },
};

const POSE_UNKNOWN: StatusLine = {
  variant: 'warning',
  badge: 'settings-stay_aligned-status-badge-pose-unknown',
  text: 'settings-stay_aligned-status-pose-unknown',
};

function correction(status: StayAlignedStatusResponseT): StatusLine {
  if (!status.enabled) {
    return {
      variant: 'neutral',
      badge: 'settings-stay_aligned-status-badge-disabled',
      text: 'settings-stay_aligned-status-text-disabled',
    };
  }
  if (status.missingParts.length > 0) {
    return {
      variant: 'warning',
      badge: 'settings-stay_aligned-status-badge-not_correcting',
      text: 'settings-stay_aligned-trackers-missing',
    };
  }
  if (status.detectedPose?.state !== StayAlignedPoseState.ENABLED) {
    return {
      variant: 'neutral',
      badge: 'settings-stay_aligned-status-badge-not_correcting',
      text: 'settings-stay_aligned-status-text-pose_unusable',
    };
  }
  return {
    variant: 'success',
    badge: 'settings-stay_aligned-status-badge-correcting',
    text: 'settings-stay_aligned-status-text-correcting',
  };
}

function position(status: StayAlignedStatusResponseT): StatusLine {
  const line = status.detectedPose
    ? POSE_STATUS[status.detectedPose.state]
    : POSE_UNKNOWN;
  // Nothing is corrected while Stay Aligned is off, so no pose is good or bad
  if (!status.enabled) return { ...line, variant: 'neutral' };
  return line;
}

function PositionText({
  status,
  text,
}: {
  status: StayAlignedStatusResponseT;
  text: string;
}) {
  const { l10n } = useLocalization();
  if (!status.detectedPose) return <Typography id={text} />;
  return (
    <p>
      <Typography
        tag="span"
        bold
        id="settings-stay_aligned-status-pose-name"
        vars={{
          pose: l10n.getString(POSITION_LABEL[status.detectedPose.pose]),
        }}
      />{' '}
      <Typography tag="span" id={text} />
    </p>
  );
}

function CorrectionValue({ status }: { status: StayAlignedStatusResponseT }) {
  const { currentLocales } = useLocaleConfig();
  const degreeFormat = useMemo(
    () =>
      new Intl.NumberFormat(currentLocales, {
        style: 'unit',
        unit: 'degree',
        unitDisplay: 'narrow',
        minimumFractionDigits: 1,
        maximumFractionDigits: 1,
      }),
    [currentLocales]
  );

  return (
    <span className="px-2 py-1">
      <Typography bold whitespace="whitespace-nowrap">
        {degreeFormat.format(status.largestCorrection?.degrees ?? 0)}
      </Typography>
    </span>
  );
}

function CorrectionText({ status }: { status: StayAlignedStatusResponseT }) {
  const { l10n } = useLocalization();
  if (!status.enabled) {
    return (
      <Typography id="settings-stay_aligned-status-largest_correction-off" />
    );
  }
  if (!status.largestCorrection) {
    return (
      <Typography id="settings-stay_aligned-status-largest_correction-none" />
    );
  }
  return (
    <Typography
      id="settings-stay_aligned-status-largest_correction-part"
      vars={{
        part: l10n.getString(
          'body_part-' + BodyPart[status.largestCorrection.bodyPart]
        ),
      }}
    />
  );
}

export function StayAlignedStatusRow({
  status,
}: {
  status: StayAlignedStatusResponseT | null;
}) {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const partList = useMemo(
    () => new Intl.ListFormat(currentLocales, { type: 'conjunction' }),
    [currentLocales]
  );
  const shown = status && correction(status);
  const pose = status && position(status);

  return (
    <div className="flex flex-col divide-y divide-background-60">
      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-stay_aligned-status-correction"
          />
        }
        badge={
          shown && <StatusBadge variant={shown.variant} id={shown.badge} />
        }
      >
        {status && shown && (
          <Typography
            id={shown.text}
            vars={{
              parts: partList.format(
                status.missingParts.map((part) =>
                  l10n.getString(PART_LABEL[part])
                )
              ),
            }}
          />
        )}
      </StatusRow>
      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-stay_aligned-status-position"
          />
        }
        badge={pose && <StatusBadge variant={pose.variant} id={pose.badge} />}
      >
        {status && pose && <PositionText status={status} text={pose.text} />}
      </StatusRow>
      <StatusRow
        label={
          <Typography
            variant="section-title"
            id="settings-stay_aligned-status-largest_correction"
          />
        }
        badge={status && <CorrectionValue status={status} />}
      >
        {status && <CorrectionText status={status} />}
      </StatusRow>
    </div>
  );
}
