import { Localized, useLocalization } from '@fluent/react';
import { useConfig } from '@/hooks/config';
import { ResetType, StatusData } from 'solarxr-protocol';
import { useMemo } from 'react';
import { parseStatusToLocale, useStatusContext } from '@/hooks/status-system';
import { useAtomValue } from 'jotai';
import { flatTrackersAtom } from '@/store/app-store';
import { BVHButton } from './BVHButton';
import { TrackingPauseButton } from './TrackingPauseButton';
import { ResetButton } from './home/ResetButton';
import { OverlayWidget } from './widgets/OverlayWidget';
import { TipBox } from './commons/TipBox';
import { DeveloperModeWidget } from './widgets/DeveloperModeWidget';
import { ClearMountingButton } from './ClearMountingButton';
import { ToggleableSkeletonVisualizerWidget } from './widgets/SkeletonVisualizerWidget';
import { A } from './commons/A';

function UnprioritizedStatuses() {
  const { l10n } = useLocalization();
  const trackers = useAtomValue(flatTrackersAtom);
  const { statuses } = useStatusContext();
  const unprioritizedStatuses = useMemo(
    () => Object.values(statuses).filter((status) => !status.prioritized),
    [statuses]
  );

  return (
    <div className="w-full flex flex-col gap-3 mb-2">
      {unprioritizedStatuses.map((status) => (
        <Localized
          id={`status_system-${StatusData[status.dataType]}`}
          vars={parseStatusToLocale(status, trackers, l10n)}
          key={status.id}
          elems={{
            PublicFixLink: (
              <A href="https://docs.slimevr.dev/common-issues.html#network-profile-is-currently-set-to-public" />
            ),
          }}
        >
          <TipBox whitespace={false} hideIcon>
            {`Warning, you should fix ${StatusData[status.dataType]}`}
          </TipBox>
        </Localized>
      ))}
    </div>
  );
}

export function WidgetsComponent() {
  const { config } = useConfig();

  return (
    <>
      <div className="grid grid-cols-2 gap-2 w-full [&>*:nth-child(odd):last-of-type]:col-span-full">
        <ResetButton type={ResetType.Yaw} size="big"></ResetButton>
        <ResetButton type={ResetType.Full} size="big"></ResetButton>
        <ResetButton type={ResetType.Mounting} size="big"></ResetButton>
        <ResetButton
          type={ResetType.Mounting}
          size="big"
          bodyPartsToReset="feet"
        ></ResetButton>
        <ResetButton
          type={ResetType.Mounting}
          size="big"
          bodyPartsToReset="fingers"
        ></ResetButton>
        <ClearMountingButton></ClearMountingButton>
        {(typeof __ANDROID__ === 'undefined' || !__ANDROID__?.isThere()) && (
          <BVHButton />
        )}
        <TrackingPauseButton />
      </div>
      <div className="w-full">
        <OverlayWidget />
      </div>
      <div className="mb-2">
        <ToggleableSkeletonVisualizerWidget height={400} />
      </div>
      <UnprioritizedStatuses />
      {config?.debug && (
        <div className="w-full">
          <DeveloperModeWidget />
        </div>
      )}
    </>
  );
}
