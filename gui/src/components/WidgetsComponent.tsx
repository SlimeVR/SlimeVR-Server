import { Localized } from '@fluent/react';
import { BVHButton } from './BVHButton';
import { ClearDriftCompensationButton } from './ClearDriftCompensationButton';
import { TrackingPauseButton } from './TrackingPauseButton';
import { ResetButton } from './home/ResetButton';
import { OverlayWidget } from './widgets/OverlayWidget';
import { TipBox } from './commons/TipBox';
import { DeveloperModeWidget } from './widgets/DeveloperModeWidget';
import { useConfig } from '../hooks/config';
import {
  ResetType,
  RpcMessage,
  SettingsResponseT,
  StatusData,
} from 'solarxr-protocol';
import { useMemo, useState } from 'react';
import { parseStatusToLocale, useStatusContext } from '../hooks/status-system';
import { useWebsocketAPI } from '../hooks/websocket-api';
import { useAppContext } from '../hooks/app';

export function WidgetsComponent() {
  const { config } = useConfig();
  const { useRPCPacket } = useWebsocketAPI();
  const [driftCompensationEnabled, setDriftCompensationEnabled] =
    useState(false);
  const { trackers } = useAppContext();
  const { statuses } = useStatusContext();
  const unprioritizedStatuses = useMemo(
    () => Object.values(statuses).filter((status) => !status.prioritized),
    [statuses]
  );

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    if (settings.driftCompensation != null)
      setDriftCompensationEnabled(settings.driftCompensation.enabled);
  });

  return (
    <>
      <div className="grid grid-cols-2 gap-2 w-full [&>*:nth-child(odd):last-of-type]:col-span-full">
        <ResetButton type={ResetType.Yaw} variant="big"></ResetButton>
        <ResetButton type={ResetType.Full} variant="big"></ResetButton>
        {config?.debug && (
          <ResetButton type={ResetType.Mounting} variant="big"></ResetButton>
        )}
        <BVHButton></BVHButton>
        <TrackingPauseButton></TrackingPauseButton>
        {driftCompensationEnabled && (
          <ClearDriftCompensationButton></ClearDriftCompensationButton>
        )}
      </div>
      <div className="w-full">
        <OverlayWidget></OverlayWidget>
      </div>
      <div className="w-full flex flex-col max-h-[33%] gap-3 overflow-y-auto mb-2">
        {unprioritizedStatuses.map((status) => (
          <Localized
            id={`status_system-${StatusData[status.dataType]}`}
            vars={parseStatusToLocale(status, trackers)}
            key={status.id}
          >
            <TipBox whitespace={false} hideIcon={true}>
              {`Warning, you should fix ${StatusData[status.dataType]}`}
            </TipBox>
          </Localized>
        ))}
      </div>
      {config?.debug && (
        <div className="w-full">
          <DeveloperModeWidget></DeveloperModeWidget>
        </div>
      )}
    </>
  );
}
