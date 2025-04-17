import { useLocaleConfig } from '@/i18n/config';
import { ReactLocalization, useLocalization } from '@fluent/react';
import {
  DetectStayAlignedRelaxedPoseRequestT,
  RpcMessage,
  StayAlignedPoseT,
  StayAlignedRelaxedPose,
} from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import { SettingsForm } from '@/components/settings/pages/GeneralSettings';
import { useAppContext } from '@/hooks/app';
import { Control } from 'react-hook-form';
import { NumberSelector } from '@/components/commons/NumberSelector';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { MouseEventHandler } from 'react';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';

/**
 * Creates a pose angle formatter that formats an positive angle as "$angle
 * outwards" and a negative angle as "$angle inwards". Useful for describing
 * leg angles in relaxed poses.
 */
function PoseAngleFormat(l10n: ReactLocalization, currentLocales: string[]) {
  const degreeFormat = new Intl.NumberFormat(currentLocales, {
    style: 'unit',
    unit: 'degree',
    maximumFractionDigits: 0,
  });

  return {
    format: (angle: number) => {
      angle = Math.round(angle);
      const angleStr = degreeFormat.format(Math.abs(angle));
      if (angle >= 1) {
        return l10n.getString('settings-stay_aligned-relaxed_poses-outwards', {
          angle: angleStr,
        });
      } else if (angle <= -1) {
        return l10n.getString('settings-stay_aligned-relaxed_poses-inwards', {
          angle: angleStr,
        });
      } else {
        return angleStr;
      }
    },
  };
}

function relaxedPoseKey(pose: StayAlignedRelaxedPose) {
  switch (pose) {
    case StayAlignedRelaxedPose.STANDING:
      return 'settings-stay_aligned-relaxed_poses-standing';
    case StayAlignedRelaxedPose.SITTING:
      return 'settings-stay_aligned-relaxed_poses-sitting';
    case StayAlignedRelaxedPose.FLAT:
      return 'settings-stay_aligned-relaxed_poses-flat';
  }
}

/**
 * Read-only view of a relaxed pose.
 */
export function RelaxedPose({
  pose,
  enabled,
  upperLegAngleInDeg,
  lowerLegAngleInDeg,
  footAngleInDeg,
}: {
  pose: StayAlignedRelaxedPose;
  enabled: boolean;
  upperLegAngleInDeg: number;
  lowerLegAngleInDeg: number;
  footAngleInDeg: number;
}) {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const angleFormat = PoseAngleFormat(l10n, currentLocales);

  if (enabled) {
    return (
      <div>
        <Typography color="primary">
          {l10n.getString(relaxedPoseKey(pose))}
        </Typography>
        <Typography color="secondary">
          {l10n.getString(
            'settings-stay_aligned-relaxed_poses-upper_leg_angle'
          )}
          : {angleFormat.format(upperLegAngleInDeg)}
        </Typography>
        <Typography color="secondary">
          {l10n.getString(
            'settings-stay_aligned-relaxed_poses-lower_leg_angle'
          )}
          : {angleFormat.format(lowerLegAngleInDeg)}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('settings-stay_aligned-relaxed_poses-foot_angle')}:{' '}
          {angleFormat.format(footAngleInDeg)}
        </Typography>
      </div>
    );
  } else {
    return (
      <div>
        <Typography color="primary">
          {l10n.getString(relaxedPoseKey(pose))}
        </Typography>
        <Typography color="secondary">
          {l10n.getString('settings-stay_aligned-relaxed_poses-disabled')}
        </Typography>
      </div>
    );
  }
}

/**
 * Read-only view of the current pose's relaxed angles.
 */
export function CurrentRelaxedPose() {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const angleFormat = PoseAngleFormat(l10n, currentLocales);

  const { state } = useAppContext();
  const stayAligned =
    state.datafeed?.stayAlignedPose || new StayAlignedPoseT(0.0, 0.0, 0.0);

  return (
    <div>
      <Typography color="primary">
        {l10n.getString('settings-stay_aligned-relaxed_poses-current_angles')}
      </Typography>
      <Typography color="secondary">
        {l10n.getString('settings-stay_aligned-relaxed_poses-upper_leg_angle')}:{' '}
        {angleFormat.format(stayAligned.upperLegAngleInDeg)}
      </Typography>
      <Typography color="secondary">
        {l10n.getString('settings-stay_aligned-relaxed_poses-lower_leg_angle')}:{' '}
        {angleFormat.format(stayAligned.lowerLegAngleInDeg)}
      </Typography>
      <Typography color="secondary">
        {l10n.getString('settings-stay_aligned-relaxed_poses-foot_angle')}:{' '}
        {angleFormat.format(stayAligned.footAngleInDeg)}
      </Typography>
    </div>
  );
}

/**
 * Read-only view of all the relaxed poses, and the current pose's angles.
 */
export function RelaxedPosesSummary({ values }: { values: SettingsForm }) {
  return (
    <div className="grid sm:grid-cols-4 gap-3 pb-3">
      <div className="rounded-lg bg-background-60 gap-2 w-full p-3">
        <RelaxedPose
          pose={StayAlignedRelaxedPose.STANDING}
          enabled={values.stayAligned.standingEnabled}
          upperLegAngleInDeg={values.stayAligned.standingUpperLegAngle}
          lowerLegAngleInDeg={values.stayAligned.standingLowerLegAngle}
          footAngleInDeg={values.stayAligned.standingFootAngle}
        />
      </div>
      <div className="rounded-lg bg-background-60 gap-2 w-full p-3">
        <RelaxedPose
          pose={StayAlignedRelaxedPose.SITTING}
          enabled={values.stayAligned.sittingEnabled}
          upperLegAngleInDeg={values.stayAligned.sittingUpperLegAngle}
          lowerLegAngleInDeg={values.stayAligned.sittingLowerLegAngle}
          footAngleInDeg={values.stayAligned.sittingFootAngle}
        />
      </div>
      <div className="rounded-lg bg-background-60 gap-2 w-full p-3">
        <RelaxedPose
          pose={StayAlignedRelaxedPose.FLAT}
          enabled={values.stayAligned.flatEnabled}
          upperLegAngleInDeg={values.stayAligned.flatUpperLegAngle}
          lowerLegAngleInDeg={values.stayAligned.flatLowerLegAngle}
          footAngleInDeg={values.stayAligned.flatFootAngle}
        />
      </div>
      <div className="rounded-lg bg-background-60 gap-2 w-full p-3">
        <CurrentRelaxedPose />
      </div>
    </div>
  );
}

/**
 * Tells the server to set a relaxed pose to the current pose's angles.
 */
export function DetectRelaxedPoseButton({
  pose,
  onClick,
}: {
  pose: StayAlignedRelaxedPose;
  onClick?: MouseEventHandler<HTMLButtonElement>;
}) {
  const { sendRPCPacket } = useWebsocketAPI();
  const { l10n } = useLocalization();

  return (
    <Button
      variant="primary"
      onClick={(e) => {
        const req = new DetectStayAlignedRelaxedPoseRequestT();
        req.pose = pose;
        sendRPCPacket(RpcMessage.DetectStayAlignedRelaxedPoseRequest, req);
        if (onClick) {
          onClick(e);
        }
      }}
    >
      {l10n.getString('settings-stay_aligned-relaxed_poses-detect_pose')}
    </Button>
  );
}

/**
 * Tells the server to reset the angles in a relaxed pose.
 */
export function ResetRelaxedPoseButton({
  pose,
  onClick,
  children,
}: {
  pose: StayAlignedRelaxedPose;
  onClick?: MouseEventHandler<HTMLButtonElement>;
} & React.PropsWithChildren) {
  const { sendRPCPacket } = useWebsocketAPI();
  const { l10n } = useLocalization();

  return (
    <Button
      variant="primary"
      onClick={(e) => {
        const req = new DetectStayAlignedRelaxedPoseRequestT();
        req.pose = pose;
        sendRPCPacket(RpcMessage.ResetStayAlignedRelaxedPoseRequest, req);
        if (onClick) {
          onClick(e);
        }
      }}
    >
      {children ||
        l10n.getString('settings-stay_aligned-relaxed_poses-reset_pose')}
    </Button>
  );
}

/**
 * Control to edit the angles of a pose.
 */
function RelaxedPoseControl({
  pose,
  enabledKey,
  upperLegSettingsKey,
  lowerLegSettingsKey,
  footSettingsKey,
  control,
}: {
  pose: StayAlignedRelaxedPose;
  enabledKey: string;
  upperLegSettingsKey: string;
  lowerLegSettingsKey: string;
  footSettingsKey: string;
  control: Control<SettingsForm, any>;
}) {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const angleFormat = PoseAngleFormat(l10n, currentLocales);

  return (
    <div className="grid sm:grid-cols-1 gap-3">
      <Typography color="primary">
        {l10n.getString(relaxedPoseKey(pose))}
      </Typography>
      <NumberSelector
        control={control}
        name={upperLegSettingsKey}
        valueLabelFormat={(value) =>
          `${l10n.getString(
            'settings-stay_aligned-relaxed_poses-upper_leg_angle'
          )}: ${angleFormat.format(value)}`
        }
        min={-90.0}
        max={90.0}
        step={1.0}
      />
      <NumberSelector
        control={control}
        name={lowerLegSettingsKey}
        valueLabelFormat={(value) =>
          `${l10n.getString(
            'settings-stay_aligned-relaxed_poses-lower_leg_angle'
          )}: ${angleFormat.format(value)}`
        }
        min={-90.0}
        max={90.0}
        step={1.0}
      />
      <NumberSelector
        control={control}
        name={footSettingsKey}
        valueLabelFormat={(value) =>
          `${l10n.getString(
            'settings-stay_aligned-relaxed_poses-foot_angle'
          )}: ${angleFormat.format(value)}`
        }
        min={-90.0}
        max={90.0}
        step={1.0}
      />
      <CheckBox
        variant="toggle"
        outlined
        control={control}
        name={enabledKey}
        label={l10n.getString('settings-stay_aligned-enabled-label')}
      />
      <DetectRelaxedPoseButton pose={pose} />
      <ResetRelaxedPoseButton pose={pose} />
    </div>
  );
}

/**
 * Control that displays the current pose's relaxed angles, in a similar layout
 * to <RelaxedPoseControl />.
 */
function CurrentRelaxedPoseControl({
  control,
}: {
  control: Control<SettingsForm, any>;
}) {
  const { l10n } = useLocalization();
  const { currentLocales } = useLocaleConfig();
  const angleFormat = PoseAngleFormat(l10n, currentLocales);

  const { state } = useAppContext();

  return (
    <div className="grid sm:grid-cols-1 gap-3">
      <Typography color="primary">
        {l10n.getString('settings-stay_aligned-relaxed_poses-current_angles')}
      </Typography>
      <NumberSelector
        control={control}
        name=""
        disabled
        valueLabelFormat={() =>
          `${l10n.getString(
            'settings-stay_aligned-relaxed_poses-upper_leg_angle'
          )}: ${angleFormat.format(
            state.datafeed?.stayAlignedPose?.upperLegAngleInDeg || 0.0
          )}`
        }
        min={-90.0}
        max={90.0}
        step={1.0}
      />
      <NumberSelector
        control={control}
        name=""
        disabled
        valueLabelFormat={() =>
          `${l10n.getString(
            'settings-stay_aligned-relaxed_poses-lower_leg_angle'
          )}: ${angleFormat.format(
            state.datafeed?.stayAlignedPose?.lowerLegAngleInDeg || 0.0
          )}`
        }
        min={-90.0}
        max={90.0}
        step={1.0}
      />
      <NumberSelector
        control={control}
        name=""
        disabled
        valueLabelFormat={() =>
          `${l10n.getString(
            'settings-stay_aligned-relaxed_poses-foot_angle'
          )}: ${angleFormat.format(
            state.datafeed?.stayAlignedPose?.footAngleInDeg || 0.0
          )}`
        }
        min={-90.0}
        max={90.0}
        step={1.0}
      />
    </div>
  );
}

/**
 * Control to edit the angles of all the relaxed poses.
 */
export function RelaxedPosesSettings({
  control,
}: {
  control: Control<SettingsForm, any>;
}) {
  return (
    <div className="grid sm:grid-cols-4 gap-3 pb-3">
      <div className="rounded-lg bg-background-60 gap-2 w-full p-3">
        <RelaxedPoseControl
          pose={StayAlignedRelaxedPose.STANDING}
          enabledKey="stayAligned.standingEnabled"
          upperLegSettingsKey="stayAligned.standingUpperLegAngle"
          lowerLegSettingsKey="stayAligned.standingLowerLegAngle"
          footSettingsKey="stayAligned.standingFootAngle"
          control={control}
        />
      </div>
      <div className="rounded-lg bg-background-60 gap-2 w-full p-3">
        <RelaxedPoseControl
          pose={StayAlignedRelaxedPose.SITTING}
          enabledKey="stayAligned.sittingEnabled"
          upperLegSettingsKey="stayAligned.sittingUpperLegAngle"
          lowerLegSettingsKey="stayAligned.sittingLowerLegAngle"
          footSettingsKey="stayAligned.sittingFootAngle"
          control={control}
        />
      </div>
      <div className="rounded-lg bg-background-60 gap-2 w-full p-3">
        <RelaxedPoseControl
          pose={StayAlignedRelaxedPose.FLAT}
          enabledKey="stayAligned.flatEnabled"
          upperLegSettingsKey="stayAligned.flatUpperLegAngle"
          lowerLegSettingsKey="stayAligned.flatLowerLegAngle"
          footSettingsKey="stayAligned.flatFootAngle"
          control={control}
        />
      </div>
      <div className="rounded-lg bg-background-60 gap-2 w-full p-3">
        <CurrentRelaxedPoseControl control={control} />
      </div>
    </div>
  );
}
