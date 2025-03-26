import { Localized, ReactLocalization, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { getTrackerName } from '@/hooks/tracker';
import { ComponentProps, useEffect, useMemo, useState } from 'react';
import {
  BoardType,
  DeviceDataT,
  DeviceIdTableT,
  FirmwareUpdateMethod,
  FirmwareUpdateStatus,
  FirmwareUpdateStatusResponseT,
  FirmwareUpdateStopQueuesRequestT,
  HardwareInfoT,
  RpcMessage,
  TrackerStatus,
} from 'solarxr-protocol';
import semver from 'semver';
import classNames from 'classnames';
import { Button } from '@/components/commons/Button';
import Markdown from 'react-markdown';
import remark from 'remark-gfm';
import { WarningBox } from '@/components/commons/TipBox';
import { FirmwareRelease, useAppContext } from '@/hooks/app';
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
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { A } from '@/components/commons/A';

export function checkForUpdate(
  currentFirmwareRelease: FirmwareRelease,
  hardwareInfo: HardwareInfoT
) {
  return (
    // TODO: This is temporary, end goal is to support all board types
    hardwareInfo.officialBoardType === BoardType.SLIMEVR &&
    semver.valid(currentFirmwareRelease.version) &&
    semver.valid(hardwareInfo.firmwareVersion?.toString() ?? 'none') &&
    semver.lt(
      hardwareInfo.firmwareVersion?.toString() ?? 'none',
      currentFirmwareRelease.version
    )
  );
}

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
  const { l10n } = useLocalization();

  return devices.map((device, index) => (
    <DeviceCardControl
      key={index}
      control={control}
      name={`selectedDevices.${device.id?.id ?? 0}`}
      deviceNames={deviceNames(device, l10n)}
    />
  ));
};

const StatusList = ({ status }: { status: Record<string, UpdateStatus> }) => {
  const statusKeys = Object.keys(status);

  return statusKeys.map((id, index) => {
    const val = status[id];

    if (!val) throw new Error('there should always be a val');
    const { state } = useAppContext();
    const device = state.datafeed?.devices.find(
      ({ id: dId }) => id === dId?.id.toString()
    );

    return (
      <DeviceCardControl
        status={val.status}
        progress={val.progress}
        key={index}
        deviceNames={val.deviceNames}
        online={device?.trackers.some(
          ({ status }) => status === TrackerStatus.OK
        )}
      ></DeviceCardControl>
    );
  });
};

const MarkdownLink = (props: ComponentProps<'a'>) => (
  <A href={props.href}>{props.children}</A>
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
        currentFirmwareRelease &&
        hardwareInfo &&
        checkForUpdate(currentFirmwareRelease, hardwareInfo) &&
        trackers.every(({ status }) => status === TrackerStatus.OK)
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
        ({ deviceId }) => deviceId === id.toString()
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

  useEffect(() => {
    if (!currentFirmwareRelease) {
      navigate('/');
      return;
    }
    return () => {
      clear();
    };
  }, []);

  const queueFlashing = (selectedDevices: SelectedDevice[]) => {
    clear();
    const firmwareFile = currentFirmwareRelease?.firmwareFile;
    if (!firmwareFile) throw new Error('invalid state - no firmware file');
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

  const trackerWithErrors = useMemo(
    () =>
      Object.keys(status).filter((id) =>
        firmwareUpdateErrorStatus.includes(status[id].status)
      ),
    [status]
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

  const shouldShowRebootWarning = useMemo(() => {
    const statuses = Object.keys(status);
    return (
      statuses.length > 0 &&
      statuses.find(
        (id) =>
          ![FirmwareUpdateStatus.DONE, ...firmwareUpdateErrorStatus].includes(
            status[id].status
          )
      )
    );
  }, [status]);

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
        (curr, { deviceId }) => ({ ...curr, [deviceId]: true }),
        {}
      ),
    });
    queueFlashing(devices);
  };

  const startUpdate = () => {
    const selectedDevices = Object.keys(selectedDevicesForm)
      .filter((d) => selectedDevicesForm[d])
      .map((id) => {
        const device = devices.find(({ id: dId }) => id === dId?.id.toString());

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

  const exit = () => {
    clear();
    navigate('/');
  };

  const canStartUpdate =
    isValid &&
    devices.length !== 0 &&
    !hasPendingTrackers &&
    trackerWithErrors.length === 0;
  const canRetry =
    !hasPendingTrackers &&
    isValid &&
    devices.length !== 0 &&
    trackerWithErrors.length !== 0;

  const statusKeys = Object.keys(status);

  return (
    <div className="flex flex-col p-4 w-full items-center justify-center">
      <div className="mobile:w-full w-10/12 h-full flex flex-col gap-2">
        <Localized id="firmware_update-title">
          <Typography variant="main-title"></Typography>
        </Localized>
        <div className="grid md:grid-cols-2 xs:grid-cols-1 gap-5">
          <div className="flex flex-col gap-2">
            <Localized id="firmware_update-devices">
              <Typography variant="section-title"></Typography>
            </Localized>
            <Localized id="firmware_update-devices-description">
              <Typography variant="standard" color="secondary"></Typography>
            </Localized>
            <div className="flex flex-col gap-4 overflow-y-auto xs:max-h-[530px]">
              {devices.length === 0 &&
                !hasPendingTrackers &&
                statusKeys.length == 0 && (
                  <Localized id="firmware_update-no_devices">
                    <WarningBox>Warning</WarningBox>
                  </Localized>
                )}
              {shouldShowRebootWarning && (
                <Localized id="firmware_tool-flashing_step-warning">
                  <WarningBox>Warning</WarningBox>
                </Localized>
              )}
              <div className="flex flex-col gap-4 h-full">
                {statusKeys.length > 0 ? (
                  <StatusList status={status}></StatusList>
                ) : (
                  <DeviceList control={control} devices={devices}></DeviceList>
                )}
                {devices.length === 0 && statusKeys.length === 0 && (
                  <div
                    className={classNames(
                      'rounded-xl bg-background-60 justify-center flex-col items-center flex pb-10 py-5 gap-5'
                    )}
                  >
                    <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
                    <Localized id="firmware_update-looking_for_devices">
                      <Typography></Typography>
                    </Localized>
                  </div>
                )}
              </div>
            </div>
          </div>
          <div className="h-fit w-full flex flex-col gap-2">
            <Localized
              id="firmware_update-changelog-title"
              vars={{ version: currentFirmwareRelease?.name ?? 'unknown' }}
            >
              <Typography variant="main-title"></Typography>
            </Localized>
            <div className="overflow-y-scroll max-h-[430px] md:h-[430px] bg-background-60 rounded-lg p-4">
              <Markdown
                remarkPlugins={[remark]}
                components={{ a: MarkdownLink }}
                className={classNames(
                  'w-full text-sm prose-xl prose text-background-10 prose-h1:text-background-10',
                  'prose-h2:text-background-10 prose-a:text-background-20 prose-strong:text-background-10',
                  'prose-code:text-background-20'
                )}
              >
                {currentFirmwareRelease?.changelog}
              </Markdown>
            </div>
          </div>
        </div>
        <div className="flex justify-end pb-2 gap-2 mobile:flex-col">
          <Localized id="firmware_update-retry">
            <Button
              variant="secondary"
              disabled={!canRetry}
              onClick={retryError}
            ></Button>
          </Localized>
          <Localized id="firmware_update-update">
            <Button
              variant="primary"
              disabled={!canStartUpdate}
              onClick={startUpdate}
            ></Button>
          </Localized>
          <Localized id="firmware_update-exit">
            <Button
              variant="primary"
              onClick={exit}
              disabled={hasPendingTrackers}
            ></Button>
          </Localized>
        </div>
      </div>
    </div>
  );
}
