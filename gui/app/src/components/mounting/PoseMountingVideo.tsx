import classNames from 'classnames';
import { ArmsMountingResetMode } from 'solarxr-protocol';
import { Typography } from '@/components/commons/Typography';
import { useResetsSettings } from '@/hooks/resets-settings';

const ARMS_MODE_NAMES: Record<ArmsMountingResetMode, string> = {
  [ArmsMountingResetMode.BACK]: 'back',
  [ArmsMountingResetMode.FORWARD]: 'forward',
  [ArmsMountingResetMode.SIDE]: 'tpose-up',
};

function usePoseMounting() {
  const { resetsSettings } = useResetsSettings();
  const arms =
    ARMS_MODE_NAMES[
      resetsSettings?.armsMountingResetMode ?? ArmsMountingResetMode.BACK
    ];
  const tiptoe = !!resetsSettings?.resetMountingFeet;
  return { arms, tiptoe };
}

export function PoseMountingInstructions() {
  const { arms, tiptoe } = usePoseMounting();

  return (
    <div className="flex flex-col gap-2">
      <Typography
        id="onboarding-automatic_mounting-mounting_reset-step-0"
        vars={{
          arms: arms.replace('-', '_'),
          feet: tiptoe ? 'tiptoe' : 'flat',
        }}
      />
      <Typography id="onboarding-automatic_mounting-mounting_reset-step-1" />
    </div>
  );
}

// The renders are different sizes, so the aspect ratio is set up front to keep the layout from jumping while they load
const ASPECT_RATIOS: Record<string, string> = {
  back: '1440 / 2560',
  default: '1799 / 1652',
};

export function PoseMountingVideo({ className }: { className?: string }) {
  const { arms, tiptoe } = usePoseMounting();
  const name = `${arms}${tiptoe ? '-tiptoe' : ''}`;

  return (
    <div
      className={classNames(
        'w-fit overflow-hidden rounded-md bg-background-60',
        className
      )}
    >
      <video
        key={name}
        autoPlay
        muted
        loop
        playsInline
        className="h-full w-auto"
        style={{ aspectRatio: ASPECT_RATIOS[arms] ?? ASPECT_RATIOS.default }}
        src={`/videos/mounting-${name}.webm`}
      />
    </div>
  );
}
