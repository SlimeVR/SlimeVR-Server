import { BVHButton } from './BVHButton';
import { TrackingPauseButton } from './TrackingPauseButton';
import { OverlayWidget } from './widgets/OverlayWidget';

export function WidgetsComponent() {
  return (
    <>
      <div className="grid grid-cols-2 gap-2 w-full [&>*:nth-child(odd):last-of-type]:col-span-full">
        {/* <ResetButton type={ResetType.Yaw} size="big"></ResetButton>
        <ResetButton type={ResetType.Full} size="big"></ResetButton>
        <ResetButton type={ResetType.Mounting} size="big"></ResetButton>
        <ClearMountingButton></ClearMountingButton> */}
        {/* <ResetButton
          type={ResetType.Mounting}
          size="big"
          bodyPartsToReset="feet"
        ></ResetButton>
        <ResetButton
          type={ResetType.Mounting}
          size="big"
          bodyPartsToReset="fingers"
        ></ResetButton> */}
        {(typeof __ANDROID__ === 'undefined' || !__ANDROID__?.isThere()) && (
          <BVHButton></BVHButton>
        )}
        <TrackingPauseButton></TrackingPauseButton>
      </div>
      <div className="w-full">
        <OverlayWidget></OverlayWidget>
      </div>
    </>
  );
}
