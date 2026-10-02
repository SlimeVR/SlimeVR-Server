import { ArmsResetMode } from 'solarxr-protocol';
import { useResetsSettings } from '@/hooks/resets-settings';

const ARMS_MODE_NAMES: Record<ArmsResetMode, string> = {
  [ArmsResetMode.BACK]: 'back',
  [ArmsResetMode.FORWARD]: 'forward',
  [ArmsResetMode.T_POSE_UP]: 'tpose-up',
  [ArmsResetMode.T_POSE_DOWN]: 'tpose-down',
};

/** The pose to hold for a pose mounting, following the arms reset mode and whether the feet are included */
export function PoseMountingVideo({ className }: { className?: string }) {
  const { resetsSettings } = useResetsSettings();
  const armsMode =
    ARMS_MODE_NAMES[resetsSettings?.armsResetMode ?? ArmsResetMode.BACK];
  const tiptoe = resetsSettings?.resetMountingFeet ? '-tiptoe' : '';

  return (
    <video
      key={`${armsMode}${tiptoe}`}
      autoPlay
      muted
      loop
      playsInline
      className={className}
      src={`/videos/mounting-${armsMode}${tiptoe}.webm`}
    />
  );
}
