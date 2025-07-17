import { BVHButton } from './BVHButton';
import { TrackingPauseButton } from './TrackingPauseButton';
import { ResetButton } from './home/ResetButton';
import { OverlayWidget } from './widgets/OverlayWidget';
import { DeveloperModeWidget } from './widgets/DeveloperModeWidget';
import { useConfig } from '@/hooks/config';
import { ResetType } from 'solarxr-protocol';
import { ClearMountingButton } from './ClearMountingButton';
import { ToggleableSkeletonVisualizerWidget } from './widgets/SkeletonVisualizerWidget';

function UnprioritizedStatuses() {
  return <div className="w-full flex flex-col gap-3 mb-2"></div>;
}

export function WidgetsComponent() {
  const { config } = useConfig();

  return (
    <>
      <div className="grid grid-cols-2 gap-2 w-full [&>*:nth-child(odd):last-of-type]:col-span-full">
        <ResetButton type={ResetType.Yaw} size="big"></ResetButton>
        <ResetButton type={ResetType.Full} size="big"></ResetButton>
        <ResetButton type={ResetType.Mounting} size="big"></ResetButton>
        <ClearMountingButton></ClearMountingButton>
        {(typeof __ANDROID__ === 'undefined' || !__ANDROID__?.isThere()) && (
          <BVHButton></BVHButton>
        )}
        <TrackingPauseButton></TrackingPauseButton>
      </div>
      <div className="w-full">
        <OverlayWidget></OverlayWidget>
      </div>
      <div className="mb-2">
        <ToggleableSkeletonVisualizerWidget height={400} />
      </div>
      <UnprioritizedStatuses></UnprioritizedStatuses>
      {config?.debug && (
        <div className="w-full">
          <DeveloperModeWidget></DeveloperModeWidget>
        </div>
      )}
    </>
  );
}
