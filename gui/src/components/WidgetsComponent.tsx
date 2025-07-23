import { BVHButton } from './BVHButton';
import { TrackingPauseButton } from './TrackingPauseButton';
import { OverlayWidget } from './widgets/OverlayWidget';

export function WidgetsComponent() {
  return (
    <>
      <div className="grid grid-cols-2 gap-2 w-full [&>*:nth-child(odd):last-of-type]:col-span-full">
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
