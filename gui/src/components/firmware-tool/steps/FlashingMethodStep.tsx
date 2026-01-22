import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { SelectedDevice, useFirmwareTool } from '@/hooks/firmware-tool';
import { Control, UseFormReset, UseFormWatch, useForm } from 'react-hook-form';
import { Radio } from '@/components/commons/Radio';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useEffect, useLayoutEffect, useState } from 'react';
import { yupResolver } from '@hookform/resolvers/yup';

import {
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
import { Input } from '@/components/commons/Input';
import { Dropdown } from '@/components/commons/Dropdown';
import { useOnboarding } from '@/hooks/onboarding';
import { getTrackerName } from '@/hooks/tracker';
import { ObjectSchema, object, string } from 'yup';
import { useAtomValue } from 'jotai';
import { devicesAtom } from '@/store/app-store';
import { DeviceCardControl } from '@/components/firmware-tool/DeviceCard';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { TipBox } from '@/components/commons/TipBox';

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
  isActive,
  control,
  watch,
  reset,
}: {
  isActive: boolean;
  control: Control<FlashingMethodForm>;
  watch: UseFormWatch<FlashingMethodForm>;
  reset: UseFormReset<FlashingMethodForm>;
}) {
  const { l10n } = useLocalization();
  const { selectDevices } = useFirmwareTool();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [devices, setDevices] = useState<Record<string, SerialDeviceT>>({});
  const [loading, setLoading] = useState(false);
  const { state, setWifiCredentials } = useOnboarding();

  useLayoutEffect(() => {
    setLoading(true);
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
      setDevices(
        res.devices.reduce(
          (curr, device) => ({
            ...curr,
            [device?.port?.toString() ?? 'unknown']: device,
          }),
          {}
        )
      );
      setLoading(false);
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
      setLoading(false);
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

  useEffect(() => {
    if (isActive) {
      const id = setInterval(() => {
        console.log('request');
        sendRPCPacket(
          RpcMessage.SerialDevicesRequest,
          new SerialDevicesRequestT()
        );
      }, 3000);

      return () => {
        clearInterval(id);
      };
    }
  });

  return (
    <div className="p-4 rounded-lg bg-background-60 w-full flex flex-col gap-3">
      <Localized id="firmware_tool-flash_method_serial-title">
        <Typography variant="main-title" />
      </Localized>
      <Localized id="firmware_tool-flash_method_serial-wifi">
        <Typography variant="section-title" />
      </Localized>
      <div className="flex flex-col gap-3 text-background-10">
        <TipBox>
          <Localized
            id={'firmware_tool-flash_method_step-ota-info'}
            elems={{ b: <b /> }}
          >
            <Typography whitespace="whitespace-pre-wrap" />
          </Localized>
        </TipBox>
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
        <Typography variant="section-title" />
      </Localized>
      {Object.keys(devices).length === 0 && !loading ? (
        <div className="flex justify-center items-center flex-col gap-4 py-4">
          <LoaderIcon slimeState={SlimeState.SAD} />
          <Localized id="firmware_tool-flash_method_serial-no_devices">
            <Typography variant="standard" />
          </Localized>
        </div>
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
          variant="secondary"
        />
      )}
    </div>
  );
}

function OTADevicesList({
  isActive,
  control,
  watch,
  reset,
}: {
  isActive: boolean;
  control: Control<FlashingMethodForm>;
  watch: UseFormWatch<FlashingMethodForm>;
  reset: UseFormReset<FlashingMethodForm>;
}) {
  const { l10n } = useLocalization();
  const { selectDevices } = useFirmwareTool();
  const allDevices = useAtomValue(devicesAtom);

  const devices =
    allDevices.filter(({ hardwareInfo, trackers }) => {
      // filter out devices we can't update
      if (!hardwareInfo?.officialBoardType) return false;

      // if the device has no trackers it is prob misconfigured so we skip for safety
      if (trackers.length <= 0) return false;

      // We make sure that the tracker is in working condition before doing ota as an error (that could be hardware)
      // could cause an error during the update
      if (!trackers.every(({ status }) => status === TrackerStatus.OK))
        return false;
      return true;
    }) || [];

  const deviceNames = ({ trackers }: DeviceDataT) =>
    trackers
      .map(({ info }) => getTrackerName(l10n, info))
      .filter((i): i is string => !!i);

  const selectedDevices = watch('ota.selectedDevices');

  useLayoutEffect(() => {
    if (isActive) {
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
    }
  }, [isActive]);

  useEffect(() => {
    if (selectedDevices) {
      selectDevices(
        Object.keys(selectedDevices).reduce((curr, id) => {
          if (!selectedDevices[id]) return curr;
          const deviceId = id.substring('id-'.length);
          const device = devices.find(
            ({ id: dId }) => deviceId === dId?.id.toString()
          );
          if (!device) return curr;
          return [
            ...curr,
            {
              type: FirmwareUpdateMethod.OTAFirmwareUpdate,
              deviceId,
              deviceNames: deviceNames(device),
            },
          ];
        }, [] as SelectedDevice[])
      );
    }
  }, [JSON.stringify(selectedDevices)]);

  return (
    <div className="p-4 rounded-lg bg-background-60 w-full flex flex-col gap-3">
      <Localized id="firmware_tool-flash_method_ota-title">
        <Typography variant="main-title" />
      </Localized>
      <Localized id="firmware_tool-flash_method_ota-devices">
        <Typography variant="section-title" />
      </Localized>
      {devices.length === 0 && (
        <div className="flex justify-center items-center flex-col gap-4 py-4">
          <LoaderIcon slimeState={SlimeState.SAD} />
          <Localized id="firmware_tool-flash_method_ota-no_devices">
            <Typography variant="standard" />
          </Localized>
        </div>
      )}
      <div className="grid xs-settings:grid-cols-2 mobile-settings:grid-cols-1 gap-2">
        {devices.map((device) => (
          <DeviceCardControl
            control={control}
            key={device.id?.id ?? 0}
            name={`ota.selectedDevices.id-${device.id?.id ?? 0}`}
            deviceNames={deviceNames(device)}
            color="bg-background-70"
          />
        ))}
      </div>
    </div>
  );
}

export function FlashingMethodStep({
  nextStep,
  goTo,
  isActive,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (to: string) => void;
  isActive: boolean;
}) {
  const { l10n } = useLocalization();
  const { selectedDevices, selectedDefault } = useFirmwareTool();

  const {
    control,
    watch,
    reset,
    formState: { isValid },
  } = useForm<FlashingMethodForm>({
    reValidateMode: 'onChange',
    mode: 'onChange',
    defaultValues: {
      flashingMethod: FirmwareUpdateMethod.OTAFirmwareUpdate.toString(),
    },
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

  console.log(
    !isValid,
    selectedDevices === null,
    selectedDevices?.length === 0
  );

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography>
            {l10n.getString('firmware_tool-flash_method_step-description')}
          </Typography>
        </div>
        <div className="my-4 flex flex-col gap-4 w-full">
          <div className="flex gap-4 w-full flex-col md:flex-row">
            <div className="flex flex-col gap-3 md:w-1/3">
              <Localized
                id="firmware_tool-flash_method_step-ota-v2"
                attrs={{ label: true, description: true }}
              >
                <Radio
                  control={control}
                  name="flashingMethod"
                  value={FirmwareUpdateMethod.OTAFirmwareUpdate.toString()}
                  label=""
                />
              </Localized>
              <Localized
                id="firmware_tool-flash_method_step-serial-v2"
                attrs={{ label: true, description: true }}
              >
                <Radio
                  control={control}
                  name="flashingMethod"
                  value={FirmwareUpdateMethod.SerialFirmwareUpdate.toString()}
                  label=""
                />
              </Localized>
            </div>
            <div className="flex flex-grow">
              {flashingMethod ===
                FirmwareUpdateMethod.SerialFirmwareUpdate.toString() && (
                <SerialDevicesList
                  isActive={isActive}
                  control={control}
                  watch={watch}
                  reset={reset}
                />
              )}
              {flashingMethod ===
                FirmwareUpdateMethod.OTAFirmwareUpdate.toString() && (
                <OTADevicesList
                  isActive={isActive}
                  control={control}
                  watch={watch}
                  reset={reset}
                />
              )}
            </div>
          </div>
          <div className="flex justify-between">
            <Localized id="firmware_tool-previous_step">
              <Button
                variant="secondary"
                onClick={() => {
                  if (selectedDefault?.flashingRules.shouldOnlyUseDefaults) {
                    goTo('SelectSource');
                  } else {
                    goTo('Defaults');
                  }
                }}
              />
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
              />
            </Localized>
          </div>
        </div>
      </div>
    </>
  );
}
