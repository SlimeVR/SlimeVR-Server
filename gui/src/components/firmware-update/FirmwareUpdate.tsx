import { Localized, ReactLocalization, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { getTrackerName } from '@/hooks/tracker';
import { ComponentProps, useLayoutEffect, useMemo, useState } from 'react';
import {
  BoardType,
  DeviceDataT,
  DeviceIdTableT,
  FirmwareUpdateMethod,
  FirmwareUpdateStatus,
  FirmwareUpdateStatusResponseT,
  FirmwareUpdateStopQueuesRequestT,
  RpcMessage,
  TrackerStatus,
} from 'solarxr-protocol';
import semver from 'semver';
import classNames from 'classnames';
import { Button } from '@/components/commons/Button';
import Markdown from 'react-markdown';
import remark from 'remark-gfm';
import { useBreakpoint } from '@/hooks/breakpoint';
import { WarningBox } from '@/components/commons/TipBox';
import { useAppContext } from '@/hooks/app';
import { DeviceCardControl } from '@/components/firmware-tool/DeviceCard';
import { Control, useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import {
  firmwareUpdateErrorStatus,
  getFlashingRequests,
  SelectedDevice,
} from '@/hooks/firmware-tool';
import { yupResolver } from '@hookform/resolvers/yup';
import { object } from 'yup';

interface FirmwareUpdateForm {
  selectedDevices: { [key: string]: boolean };
}

interface UpdateStatus {
  status: FirmwareUpdateStatus;
  type: FirmwareUpdateMethod;
  progress: number;
  deviceNames: string[];
}

const deviceNames = ({ trackers }: DeviceDataT, l10n: ReactLocalization) =>
  trackers
    .map(({ info }) => getTrackerName(l10n, info))
    .filter((i): i is string => !!i);

const DeviceList = ({
  control,
  devices,
}: {
  control: Control<any>;
  devices: DeviceDataT[];
}) => {
  const { isMobile } = useBreakpoint('mobile');
  const { l10n } = useLocalization();

  return Array.from({
    ...devices,
    length: Math.max(
      devices.length,
      isMobile ? 1 : devices.length === 0 ? 4 : 5
    ),
  }).map((device, index) => {
    return device ? (
      <DeviceCardControl
        key={index}
        control={control}
        name={`selectedDevices.${device.id?.id ?? 0}`}
        deviceNames={deviceNames(device, l10n)}
      />
    ) : (
      <div
        key={index}
        className={classNames(
          'rounded-xl h-[86px] bg-background-80 animate-pulse'
        )}
      ></div>
    );
  });
};

const StatusList = ({ status }: { status: Record<string, UpdateStatus> }) => {
  const statusKeys = Object.keys(status);
  const { isMobile } = useBreakpoint('mobile');

  return Array.from({
    ...statusKeys,
    length: Math.max(statusKeys.length, isMobile ? 1 : 5),
  }).map((id, index) => {
    const val = status[id];

    return val ? (
      <DeviceCardControl
        status={val.status}
        progress={val.progress}
        key={index}
        deviceNames={val.deviceNames}
      ></DeviceCardControl>
    ) : (
      <div
        key={index}
        className={classNames(
          'rounded-xl h-[86px] bg-background-80 animate-pulse'
        )}
      ></div>
    );
  });
};

const MarkdownLink = (props: ComponentProps<'a'>) => (
  <a target="_blank" href={props.href}>
    {props.children}
  </a>
);

export function FirmwareUpdate() {
  const navigate = useNavigate();
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [selectedDevices, setSelectedDevices] = useState<SelectedDevice[]>([]);
  const { state, currentFirmwareRelease } = useAppContext();
  const [status, setStatus] = useState<Record<string, UpdateStatus>>({});

  const devices =
    state.datafeed?.devices.filter(
      ({ trackers, hardwareInfo }) =>
        trackers.length > 0 &&
        hardwareInfo?.officialBoardType === BoardType.SLIMEVR &&
        currentFirmwareRelease &&
        semver.valid(currentFirmwareRelease.version) &&
        semver.valid(hardwareInfo?.firmwareVersion?.toString() ?? 'none') &&
        semver.lt(
          hardwareInfo?.firmwareVersion?.toString() ?? 'none',
          currentFirmwareRelease.version
        ) &&
        trackers.every(({ status }) => status == TrackerStatus.OK)
    ) || [];

  useRPCPacket(
    RpcMessage.FirmwareUpdateStatusResponse,
    (data: FirmwareUpdateStatusResponseT) => {
      if (!data.deviceId) throw new Error('no device id');
      const id =
        data.deviceId instanceof DeviceIdTableT
          ? data.deviceId.id?.id
          : data.deviceId.port;
      if (!id) throw new Error('invalid device id');

      const selectedDevice = selectedDevices?.find(
        ({ deviceId }) => deviceId == id.toString()
      );

      // We skip the status as it can be old trackers still sending status
      if (!selectedDevice) return;

      setStatus((last) => ({
        ...last,
        [id.toString()]: {
          progress: data.progress / 100,
          status: data.status,
          type: selectedDevice.type,
          deviceNames: selectedDevice.deviceNames,
        },
      }));
    }
  );

  const {
    control,
    watch,
    reset,
    formState: { isValid },
  } = useForm<FirmwareUpdateForm>({
    reValidateMode: 'onChange',
    mode: 'onChange',
    defaultValues: {
      selectedDevices: devices.reduce(
        (curr, { id }) => ({ ...curr, [id?.id ?? 0]: false }),
        {}
      ),
    },
    resolver: yupResolver(
      object({
        selectedDevices: object().test(
          'at-least-one-true',
          'At least one field must be true',
          (value) => {
            if (typeof value !== 'object' || value === null) return false;
            return Object.values(value).some((val) => val === true);
          }
        ),
      })
    ),
  });

  const selectedDevicesForm = watch('selectedDevices');

  const clear = () => {
    setStatus({});
    sendRPCPacket(
      RpcMessage.FirmwareUpdateStopQueuesRequest,
      new FirmwareUpdateStopQueuesRequestT()
    );
  };

  useLayoutEffect(() => {
    if (!currentFirmwareRelease) {
      navigate('/');
    }
    return () => {
      clear();
    };
  }, []);

  const queueFlashing = (selectedDevices: SelectedDevice[]) => {
    clear();
    const firmwareFile = currentFirmwareRelease?.firmwareFile;
    if (!firmwareFile) throw new Error('invalid state - no firmware file');
    console.log(firmwareFile);
    const requests = getFlashingRequests(
      selectedDevices,
      [{ isFirmware: true, firmwareId: '', url: firmwareFile, offset: 0 }],
      { wifi: undefined, alonePage: false, progress: 0 }, // we do not use serial
      null // we do not use serial
    );

    requests.forEach((req) => {
      sendRPCPacket(RpcMessage.FirmwareUpdateRequest, req);
    });
  };

  const trackerWithErrors = Object.keys(status).filter((id) =>
    firmwareUpdateErrorStatus.includes(status[id].status)
  );

  const hasPendingTrackers = useMemo(
    () =>
      Object.keys(status).filter((id) =>
        [
          FirmwareUpdateStatus.NEED_MANUAL_REBOOT,
          FirmwareUpdateStatus.DOWNLOADING,
          FirmwareUpdateStatus.AUTHENTICATING,
          FirmwareUpdateStatus.REBOOTING,
          FirmwareUpdateStatus.SYNCING_WITH_MCU,
          FirmwareUpdateStatus.UPLOADING,
          FirmwareUpdateStatus.PROVISIONING,
        ].includes(status[id].status)
      ).length > 0,
    [status]
  );

  const shouldShowRebootWarning = useMemo(
    () =>
      Object.keys(status).find((id) =>
        [
          FirmwareUpdateStatus.REBOOTING,
          FirmwareUpdateStatus.UPLOADING,
        ].includes(status[id].status)
      ),
    [status]
  );

  const retryError = () => {
    const devices = trackerWithErrors.map((id) => {
      const device = status[id];
      return {
        type: device.type,
        deviceId: id,
        deviceNames: device.deviceNames,
      };
    });

    reset({
      selectedDevices: devices.reduce(
        (curr, { deviceId }) => ({ ...curr, [deviceId]: false }),
        {}
      ),
    });
    queueFlashing(devices);
  };

  const startUpdate = () => {
    const selectedDevices = Object.keys(selectedDevicesForm)
      .filter((d) => selectedDevicesForm[d])
      .map((id) => {
        const device = devices.find(({ id: dId }) => id == dId?.id.toString());

        if (!device) throw new Error('no device found');
        return {
          type: FirmwareUpdateMethod.OTAFirmwareUpdate,
          deviceId: id,
          deviceNames: deviceNames(device, l10n),
        };
      });
    if (!selectedDevices)
      throw new Error('invalid state - no selected devices');
    setSelectedDevices(selectedDevices);
    queueFlashing(selectedDevices);
  };

  const canStartUpdate = isValid && devices.length !== 0 && !hasPendingTrackers;
  const canRetry =
    isValid && devices.length !== 0 && trackerWithErrors.length !== 0;

  const statusKeys = Object.keys(status);

  return (
    <div className="flex flex-col p-4 w-full items-center justify-center h-screen">
      <div className="mobile:w-full w-10/12 h-full  flex flex-col gap-2">
        <Localized id="firmware-update-title">
          <Typography variant="main-title"></Typography>
        </Localized>
        <div className="grid md:grid-cols-2 xs:grid-cols-1 gap-5">
          <div className={'flex flex-col gap-2'}>
            <Localized id="firmware-update-devices">
              <Typography variant="section-title"></Typography>
            </Localized>
            <Localized id="firmware-update-devices-desc">
              <Typography variant="standard" color="secondary"></Typography>
            </Localized>
            <div
              className={'flex flex-col gap-4 overflow-y-auto xs:max-h-[530px]'}
            >
              {devices.length === 0 && !hasPendingTrackers && (
                <Localized id="firmware-update-no-devices">
                  <WarningBox className="min-h-[86px]">Warning</WarningBox>
                </Localized>
              )}
              {shouldShowRebootWarning && (
                <Localized id="firmware-tool-flashing-step-warning">
                  <WarningBox>Warning</WarningBox>
                </Localized>
              )}
              <div className="flex flex-col gap-4 h-full">
                {statusKeys.length > 0 ? (
                  <StatusList status={status}></StatusList>
                ) : (
                  <DeviceList control={control} devices={devices}></DeviceList>
                )}
              </div>
            </div>
          </div>
          <div className="h-fit w-full flex flex-col gap-2">
            <Localized
              id="firmware-update-changelog-title"
              vars={{ version: currentFirmwareRelease?.name ?? 'unknown' }}
            >
              <Typography variant="main-title"></Typography>
            </Localized>
            <div className="overflow-y-scroll max-h-[505px] md:h-[505px] bg-background-60 rounded-lg p-4">
              <Markdown
                remarkPlugins={[remark]}
                components={{ a: MarkdownLink }}
                className={classNames(
                  'w-full text-sm prose-xl prose text-background-10 prose-h1:text-background-10 prose-h2:text-background-10 prose-a:text-background-20 prose-strong:text-background-10 prose-code:text-background-20'
                )}
              >
                {currentFirmwareRelease?.changelog}
              </Markdown>
            </div>
          </div>
        </div>
        <div className="flex justify-end w-full pb-2 gap-2 mobile:flex-col">
          <Localized id="firmware-update-retry">
            <Button
              variant="secondary"
              disabled={!canRetry}
              onClick={retryError}
            ></Button>
          </Localized>
          <Localized id="firmware-update-update">
            <Button
              variant="primary"
              className="mobile:w-full"
              disabled={!canStartUpdate}
              onClick={startUpdate}
            ></Button>
          </Localized>
        </div>
      </div>
    </div>
  );
}
