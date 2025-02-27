import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import {
  boardTypeToFirmwareToolBoardType,
  useFirmwareTool,
} from '@/hooks/firmware-tool';
import { Control, UseFormReset, UseFormWatch, useForm } from 'react-hook-form';
import { Radio } from '@/components/commons/Radio';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useEffect, useLayoutEffect, useState } from 'react';
import { yupResolver } from '@hookform/resolvers/yup';

import {
  BoardType,
  DeviceDataT,
  FirmwareUpdateMethod,
  NewSerialDeviceResponseT,
  RpcMessage,
  SerialDeviceT,
  SerialDevicesRequestT,
  SerialDevicesResponseT,
  TrackerStatus,
} from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { useAppContext } from '@/hooks/app';
import { Input } from '@/components/commons/Input';
import { Dropdown } from '@/components/commons/Dropdown';
import { useOnboarding } from '@/hooks/onboarding';
import { DeviceCardControl } from './DeviceCard';
import { getTrackerName } from '@/hooks/tracker';
import { ObjectSchema, object, string } from 'yup';

interface FlashingMethodForm {
  flashingMethod?: string;
  serial?: {
    selectedDevicePort: string;
    ssid: string;
    password?: string;
  };
  ota?: {
    selectedDevices: { [key: string]: boolean };
  };
}

function SerialDevicesList({
  control,
  watch,
  reset,
}: {
  control: Control<FlashingMethodForm>;
  watch: UseFormWatch<FlashingMethodForm>;
  reset: UseFormReset<FlashingMethodForm>;
}) {
  const { l10n } = useLocalization();
  const { selectDevices } = useFirmwareTool();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [devices, setDevices] = useState<Record<string, SerialDeviceT>>({});
  const { state, setWifiCredentials } = useOnboarding();

  useLayoutEffect(() => {
    sendRPCPacket(RpcMessage.SerialDevicesRequest, new SerialDevicesRequestT());
    selectDevices(null);
    reset({
      flashingMethod: FirmwareUpdateMethod.SerialFirmwareUpdate.toString(),
      serial: {
        ...state.wifi,
        selectedDevicePort: undefined,
      },
      ota: undefined,
    });
  }, []);

  useRPCPacket(
    RpcMessage.SerialDevicesResponse,
    (res: SerialDevicesResponseT) => {
      setDevices((old) =>
        res.devices.reduce(
          (curr, device) => ({
            ...curr,
            [device?.port?.toString() ?? 'unknown']: device,
          }),
          old
        )
      );
    }
  );

  useRPCPacket(
    RpcMessage.NewSerialDeviceResponse,
    ({ device }: NewSerialDeviceResponseT) => {
      if (device?.port)
        setDevices((old) => ({
          ...old,
          [device?.port?.toString() ?? 'unknown']: device,
        }));
    }
  );

  const serialValues = watch('serial');

  useEffect(() => {
    if (!serialValues) {
      selectDevices(null);
      return;
    }

    setWifiCredentials(serialValues.ssid, serialValues.password);
    if (
      serialValues.selectedDevicePort &&
      devices[serialValues.selectedDevicePort]
    ) {
      selectDevices([
        {
          type: FirmwareUpdateMethod.SerialFirmwareUpdate,
          deviceId: serialValues.selectedDevicePort,
          deviceNames: [
            devices[serialValues.selectedDevicePort].name?.toString() ??
              'unknown',
          ],
        },
      ]);
    } else {
      selectDevices(null);
    }
  }, [JSON.stringify(serialValues), devices]);

  return (
    <>
      <Localized id="firmware_tool-flash_method_serial-wifi">
        <Typography variant="section-title"></Typography>
      </Localized>
      <div className="grid xs-settings:grid-cols-2 mobile-settings:grid-cols-1 gap-3 text-background-10">
        <Localized
          id="onboarding-wifi_creds-ssid"
          attrs={{ placeholder: true, label: true }}
        >
          <Input
            control={control}
            name="serial.ssid"
            label="SSID"
            variant="secondary"
          />
        </Localized>
        <Localized
          id="onboarding-wifi_creds-password"
          attrs={{ placeholder: true, label: true }}
        >
          <Input
            control={control}
            name="serial.password"
            type="password"
            variant="secondary"
          />
        </Localized>
      </div>
      <Localized id="firmware_tool-flash_method_serial-devices-label">
        <Typography variant="section-title"></Typography>
      </Localized>
      {Object.keys(devices).length === 0 ? (
        <Localized id="firmware_tool-flash_method_serial-no_devices">
          <Typography variant="standard" color="secondary"></Typography>
        </Localized>
      ) : (
        <Dropdown
          control={control}
          name="serial.selectedDevicePort"
          items={Object.keys(devices).map((port) => ({
            label: devices[port].name?.toString() ?? 'unknown',
            value: port,
          }))}
          placeholder={l10n.getString(
            'firmware_tool-flash_method_serial-devices-placeholder'
          )}
          display="block"
          direction="down"
        ></Dropdown>
      )}
    </>
  );
}

function OTADevicesList({
  control,
  watch,
  reset,
}: {
  control: Control<FlashingMethodForm>;
  watch: UseFormWatch<FlashingMethodForm>;
  reset: UseFormReset<FlashingMethodForm>;
}) {
  const { l10n } = useLocalization();
  const { selectDevices, newConfig } = useFirmwareTool();
  const { state } = useAppContext();

  const devices =
    state.datafeed?.devices.filter(({ trackers, hardwareInfo }) => {
      // We make sure the device is not one of these types
      if (
        hardwareInfo?.officialBoardType === BoardType.SLIMEVR_LEGACY ||
        hardwareInfo?.officialBoardType === BoardType.SLIMEVR_DEV ||
        hardwareInfo?.officialBoardType === BoardType.CUSTOM ||
        hardwareInfo?.officialBoardType === BoardType.OWOTRACK ||
        hardwareInfo?.officialBoardType === BoardType.WRANGLER ||
        hardwareInfo?.officialBoardType === BoardType.MOCOPI ||
        hardwareInfo?.officialBoardType === BoardType.HARITORA ||
        hardwareInfo?.officialBoardType === BoardType.DEV_RESERVED
      )
        return false;

      // if the device has no trackers it is prob misconfigured so we skip for safety
      if (trackers.length <= 0) return false;

      // We make sure that the tracker is in working condition before doing ota as an error (that could be hardware)
      // could cause an error during the update
      if (!trackers.every(({ status }) => status === TrackerStatus.OK))
        return false;

      const boardType = hardwareInfo?.officialBoardType ?? BoardType.UNKNOWN;
      return (
        boardTypeToFirmwareToolBoardType[boardType] ===
        newConfig?.boardConfig?.type
      );
    }) || [];

  const deviceNames = ({ trackers }: DeviceDataT) =>
    trackers
      .map(({ info }) => getTrackerName(l10n, info))
      .filter((i): i is string => !!i);

  const selectedDevices = watch('ota.selectedDevices');

  useLayoutEffect(() => {
    reset({
      flashingMethod: FirmwareUpdateMethod.OTAFirmwareUpdate.toString(),
      ota: {
        selectedDevices: devices.reduce(
          (curr, { id }) => ({ ...curr, [id?.id ?? 0]: false }),
          {}
        ),
      },
      serial: undefined,
    });
    selectDevices(null);
  }, []);

  useEffect(() => {
    if (selectedDevices) {
      selectDevices(
        Object.keys(selectedDevices)
          .filter((d) => selectedDevices[d])
          .map((id) => id.substring('id-'.length))
          .map((id) => {
            const device = devices.find(
              ({ id: dId }) => id === dId?.id.toString()
            );

            if (!device) throw new Error('no device found');
            return {
              type: FirmwareUpdateMethod.OTAFirmwareUpdate,
              deviceId: id,
              deviceNames: deviceNames(device),
            };
          })
      );
    }
  }, [JSON.stringify(selectedDevices)]);

  return (
    <>
      <Localized id="firmware_tool-flash_method_ota-devices">
        <Typography variant="section-title"></Typography>
      </Localized>
      {devices.length === 0 && (
        <Localized id="firmware_tool-flash_method_ota-no_devices">
          <Typography color="secondary"></Typography>
        </Localized>
      )}
      <div className="grid xs-settings:grid-cols-2 mobile-settings:grid-cols-1 gap-2">
        {devices.map((device) => (
          <DeviceCardControl
            control={control}
            key={device.id?.id ?? 0}
            name={`ota.selectedDevices.id-${device.id?.id ?? 0}`}
            deviceNames={deviceNames(device)}
          ></DeviceCardControl>
        ))}
      </div>
    </>
  );
}

export function FlashingMethodStep({
  nextStep,
  prevStep,
}: {
  nextStep: () => void;
  prevStep: () => void;
  isActive: boolean;
}) {
  const { l10n } = useLocalization();
  const { isGlobalLoading, selectedDevices } = useFirmwareTool();

  const {
    control,
    watch,
    reset,
    formState: { isValid },
  } = useForm<FlashingMethodForm>({
    reValidateMode: 'onChange',
    mode: 'onChange',
    resolver: yupResolver(
      object({
        flashingMethod: string().optional(),
        serial: object().when('flashingMethod', {
          is: FirmwareUpdateMethod.SerialFirmwareUpdate.toString(),
          then: (s) =>
            s
              .shape({
                selectedDevicePort: string().required(),
                ssid: string().required(
                  l10n.getString('onboarding-wifi_creds-ssid-required')
                ),
                password: string(),
              })
              .required(),
          otherwise: (s) => s.optional(),
        }),
        ota: object().when('flashingMethod', {
          is: FirmwareUpdateMethod.OTAFirmwareUpdate.toString(),
          then: (s) =>
            s
              .shape({
                selectedDevices: object(),
              })
              .required(),
          otherwise: (s) => s.optional(),
        }),
      }) as ObjectSchema<FlashingMethodForm>
    ),
  });

  const flashingMethod = watch('flashingMethod');

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware_tool-flash_method_step-description')}
          </Typography>
        </div>
        <div className="my-4">
          {!isGlobalLoading && (
            <div className="flex flex-col gap-3">
              <div className="grid xs-settings:grid-cols-2 mobile-settings:grid-cols-1 gap-3">
                <Localized
                  id="firmware_tool-flash_method_step-ota"
                  attrs={{ label: true, description: true }}
                >
                  <Radio
                    control={control}
                    name="flashingMethod"
                    value={FirmwareUpdateMethod.OTAFirmwareUpdate.toString()}
                    label=""
                  ></Radio>
                </Localized>
                <Localized
                  id="firmware_tool-flash_method_step-serial"
                  attrs={{ label: true, description: true }}
                >
                  <Radio
                    control={control}
                    name="flashingMethod"
                    value={FirmwareUpdateMethod.SerialFirmwareUpdate.toString()}
                    label=""
                  ></Radio>
                </Localized>
              </div>
              {flashingMethod ===
                FirmwareUpdateMethod.SerialFirmwareUpdate.toString() && (
                <SerialDevicesList
                  control={control}
                  watch={watch}
                  reset={reset}
                ></SerialDevicesList>
              )}
              {flashingMethod ===
                FirmwareUpdateMethod.OTAFirmwareUpdate.toString() && (
                <OTADevicesList
                  control={control}
                  watch={watch}
                  reset={reset}
                ></OTADevicesList>
              )}
              <div className="flex justify-between">
                <Localized id="firmware_tool-previous_step">
                  <Button variant="secondary" onClick={prevStep}></Button>
                </Localized>
                <Localized id="firmware_tool-next_step">
                  <Button
                    variant="primary"
                    disabled={
                      !isValid ||
                      selectedDevices === null ||
                      selectedDevices.length === 0
                    }
                    onClick={nextStep}
                  ></Button>
                </Localized>
              </div>
            </div>
          )}
          {isGlobalLoading && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
              <Localized id="firmware_tool-loading">
                <Typography color="secondary"></Typography>
              </Localized>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
