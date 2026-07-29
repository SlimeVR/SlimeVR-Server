import { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useLocation } from 'react-router-dom';
import {
  CloseSerialRequestT,
  OpenSerialRequestT,
  RpcMessage,
  SerialDevicesRequestT,
  SerialDevicesResponseT,
  SerialDeviceT,
  SerialTrackerFactoryResetRequestT,
  SerialTrackerGetInfoRequestT,
  SerialTrackerRebootRequestT,
  SerialUpdateResponseT,
  SerialTrackerGetWifiScanRequestT,
  SerialTrackerCustomCommandRequestT,
  SerialDeviceType,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { Dropdown } from '@/components/commons/Dropdown';
import { Typography } from '@/components/commons/Typography';
import { Localized, useLocalization } from '@fluent/react';
import { BaseModal } from '@/components/commons/BaseModal';
import { WarningBox } from '@/components/commons/TipBox';
import { fileSave } from 'browser-fs-access';
import { waitUntil } from '@/utils/a11y';
import { Input } from '@/components/commons/Input';
import { PauseIcon } from '@/components/commons/icon/PauseIcon';
import { PlayIcon } from '@/components/commons/icon/PlayIcon';

export interface SerialForm {
  port: string;
  customCommand: string;
}

export function Serial() {
  const { l10n } = useLocalization();
  const { state } = useLocation();

  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const consoleRef = useRef<HTMLDivElement>(null);
  const [consoleContent, setConsole] = useState('');

  const [openedSerialDevice, setOpenedSerialDevice] = useState<Omit<
    SerialDeviceT,
    'pack'
  > | null>(null);
  const [serialDevices, setSerialDevices] = useState<
    Omit<SerialDeviceT, 'pack'>[]
  >([]);

  const [tryFactoryReset, setTryFactoryReset] = useState(false);
  const [trySendCustomCommand, setTrySendCustomCommand] = useState(false);

  const [acceptedCustomCommandWarning, setAcceptedCustomCommandWarning] =
    useState(false);

  const defaultValues = { port: 'Auto' };
  const { control, watch, reset, setValue, subscribe } = useForm<SerialForm>({
    defaultValues,
  });

  const port = watch('port');
  const customCommand = watch('customCommand');

  useEffect(() => {
    const callback = subscribe({
      name: 'port',
      exact: true,
      formState: {
        values: true,
      },
      callback: ({ values }) => {
        openSerial(values.port);
        setConsole('');
      },
    });

    return () => callback();
  }, [subscribe]);

  const [isPaused, setPaused] = useState(false);

  const openSerial = (port: string) => {
    sendRPCPacket(RpcMessage.CloseSerialRequest, new CloseSerialRequestT());
    const req = new OpenSerialRequestT();
    req.auto = port === 'Auto';
    req.port = port;
    sendRPCPacket(RpcMessage.OpenSerialRequest, req);
  };

  useEffect(() => {
    const typedState: { serialPort: string } = state as any;
    if (typedState?.serialPort) {
      reset({ port: typedState.serialPort });
    }

    sendRPCPacket(RpcMessage.SerialDevicesRequest, new SerialDevicesRequestT());
    const interval = setInterval(() => {
      sendRPCPacket(
        RpcMessage.SerialDevicesRequest,
        new SerialDevicesRequestT()
      );
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    return () => {
      sendRPCPacket(RpcMessage.CloseSerialRequest, new CloseSerialRequestT());
    };
  }, []);

  useRPCPacket(
    RpcMessage.SerialUpdateResponse,
    (data: SerialUpdateResponseT) => {
      if (data.device) setOpenedSerialDevice(data.device);
      else if (data.closed) setOpenedSerialDevice(null);

      if (data.log) {
        setConsole((console) => console + data.log);
      }
    }
  );

  useRPCPacket(
    RpcMessage.SerialDevicesResponse,
    (res: SerialDevicesResponseT) => {
      setSerialDevices([
        {
          name: l10n.getString('settings-serial-auto_dropdown_item'),
          port: 'Auto',
          type: SerialDeviceType.ESP_TRACKER,
        },
        ...(res.devices || []),
      ]);
    }
  );

  useEffect(() => {
    if (isPaused) {
      return;
    }
    if (!consoleRef.current) {
      return;
    }

    consoleRef.current.scrollTo({
      top: consoleRef.current.scrollHeight,
    });
  }, [consoleContent]);

  useEffect(() => {
    if (!consoleRef.current) {
      return;
    }

    consoleRef.current.addEventListener('scroll', () => {
      if (!consoleRef.current) {
        return;
      }
      const bottomAllowance = 10;
      setPaused(
        consoleRef.current.scrollTop +
          consoleRef.current.getBoundingClientRect().height <
          consoleRef.current.scrollHeight - bottomAllowance
      );
    });
  }, [consoleRef.current]);

  useEffect(() => {
    const id = setInterval(() => {
      if (openedSerialDevice === null) {
        openSerial(port ?? defaultValues.port);
      } else {
        clearInterval(id);
      }
    }, 3000);

    return () => {
      clearInterval(id);
    };
  }, [openedSerialDevice]);

  const reboot = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerRebootRequest,
      new SerialTrackerRebootRequestT()
    );
  };

  // ESP
  const factoryReset = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerFactoryResetRequest,
      new SerialTrackerFactoryResetRequestT()
    );

    setTryFactoryReset(false);
  };
  const getInfos = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerGetInfoRequest,
      new SerialTrackerGetInfoRequestT()
    );
  };
  const getWifiScan = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerGetWifiScanRequest,
      new SerialTrackerGetWifiScanRequestT()
    );
  };
  const sendCustomSerialCommand = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerCustomCommandRequest,
      new SerialTrackerCustomCommandRequestT(customCommand)
    );

    setTrySendCustomCommand(false);
    setValue('customCommand', '');
  };

  // HID
  const enterPairing = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerCustomCommandRequest,
      new SerialTrackerCustomCommandRequestT('pair')
    );
  };
  const dfu = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerCustomCommandRequest,
      new SerialTrackerCustomCommandRequestT('dfu')
    );
  };
  const meow = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerCustomCommandRequest,
      new SerialTrackerCustomCommandRequestT('meow')
    );
  };

  // HID receiver
  const exitPairing = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerCustomCommandRequest,
      new SerialTrackerCustomCommandRequestT('exit')
    );
  };

  // HID tracker
  const calibrate = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerCustomCommandRequest,
      new SerialTrackerCustomCommandRequestT('calibrate')
    );
  };
  const sixSideCalibrate = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerCustomCommandRequest,
      new SerialTrackerCustomCommandRequestT('6-side')
    );
  };

  const consoleContentRef = useRef(consoleContent);
  useLayoutEffect(() => {
    consoleContentRef.current = consoleContent;
  }, [consoleContent]);

  const saveLogToFile = async () => {
    // Check if we have getInfos and fetch them if we don't
    if (!consoleContentRef.current.includes('GET INFO')) {
      getInfos();
      await waitUntil(
        () => consoleContentRef.current.includes('GET INFO'),
        100,
        10
      );
    }

    const blob = new Blob([consoleContentRef.current], {
      type: 'text/plain',
    });
    fileSave(blob, {
      fileName: 'serial-logs.txt',
      extensions: ['.txt'],
    });
  };

  const pauseScroll = () => {
    setPaused(!isPaused);

    consoleRef.current?.scrollTo({
      top: consoleRef.current.scrollHeight,
    });
  };

  return (
    <>
      <BaseModal
        isOpen={tryFactoryReset}
        onRequestClose={() => setTryFactoryReset(false)}
      >
        <Localized
          id="settings-serial-factory_reset-warning"
          elems={{ b: <b /> }}
        >
          <WarningBox>
            <b>Warning:</b> This will reset the tracker to factory settings.
            Which means Wi-Fi and calibration settings <b>will all be lost!</b>
          </WarningBox>
        </Localized>
        <div className="flex flex-row gap-3 pt-5 place-content-center">
          <Button variant="secondary" onClick={() => setTryFactoryReset(false)}>
            {l10n.getString('settings-serial-factory_reset-warning-cancel')}
          </Button>
          <Button variant="primary" onClick={factoryReset}>
            {l10n.getString('settings-serial-factory_reset-warning-ok')}
          </Button>
        </div>
      </BaseModal>
      <BaseModal
        isOpen={trySendCustomCommand}
        onRequestClose={() => setTrySendCustomCommand(false)}
      >
        <Localized
          id="settings-serial-send_command-warning"
          elems={{ b: <b /> }}
        >
          <WarningBox>
            <b>Warning:</b> Running serial commands can lead to data loss or
            brick the trackers.
          </WarningBox>
        </Localized>
        <div className="flex flex-row gap-3 pt-5 place-content-center">
          <Button
            variant="secondary"
            onClick={() => setTrySendCustomCommand(false)}
          >
            {l10n.getString('settings-serial-send_command-warning-cancel')}
          </Button>
          <Button
            variant="primary"
            onClick={() => {
              setAcceptedCustomCommandWarning(true);
              sendCustomSerialCommand();
            }}
          >
            {l10n.getString('settings-serial-send_command-warning-ok')}
          </Button>
        </div>
      </BaseModal>
      <div className="flex flex-col bg-background-70 h-full p-4 mobile:p-2 rounded-md">
        <div className="flex flex-col pb-2 mobile:pt-4">
          <Typography variant="main-title">
            {l10n.getString('settings-serial')}
          </Typography>
          <>
            {l10n
              .getString('settings-serial-description')
              .split('\n')
              .map((line, i) => (
                <Typography key={i}>{line}</Typography>
              ))}
          </>
        </div>
        <div className="bg-background-80 rounded-lg flex-grow h-0 flex flex-col p-2">
          <div
            className="flex-grow overflow-x-auto overflow-y-auto"
            ref={consoleRef}
          >
            <div className="flex select-text">
              <pre>
                {openedSerialDevice !== null
                  ? consoleContent
                  : l10n.getString('settings-serial-connection_lost')}
              </pre>
            </div>
          </div>
          <div className="border-t-2 pt-2 border-background-60 border-solid gap-2 flex flex-row">
            <div className="xs:flex flex-grow xs:flex-wrap gap-2 grid grid-cols-2">
              {openedSerialDevice !== null && (
                <>
                  <Button variant="quaternary" onClick={reboot}>
                    {l10n.getString('settings-serial-reboot')}
                  </Button>
                  {openedSerialDevice?.type ===
                    SerialDeviceType.ESP_TRACKER && (
                    <>
                      <Button
                        variant="quaternary"
                        onClick={() => setTryFactoryReset(true)}
                      >
                        {l10n.getString('settings-serial-factory_reset')}
                      </Button>
                      <Button variant="quaternary" onClick={getWifiScan}>
                        {l10n.getString('settings-serial-get_wifi_scan')}
                      </Button>
                    </>
                  )}
                  {openedSerialDevice?.type !==
                    SerialDeviceType.ESP_TRACKER && (
                    <Button variant="quaternary" onClick={enterPairing}>
                      {l10n.getString('settings-serial-enter_pairing')}
                    </Button>
                  )}
                  {openedSerialDevice?.type ===
                    SerialDeviceType.HID_RECEIVER && (
                    <Button variant="quaternary" onClick={exitPairing}>
                      {l10n.getString('settings-serial-exit_pairing')}
                    </Button>
                  )}
                  {openedSerialDevice?.type ===
                    SerialDeviceType.HID_TRACKER && (
                    <>
                      <Button variant="quaternary" onClick={calibrate}>
                        {l10n.getString('settings-serial-calibrate')}
                      </Button>
                      <Button variant="quaternary" onClick={sixSideCalibrate}>
                        {l10n.getString('settings-serial-six_side_calibrate')}
                      </Button>
                    </>
                  )}
                  {openedSerialDevice?.type !==
                    SerialDeviceType.ESP_TRACKER && (
                    <>
                      <Button variant="quaternary" onClick={dfu}>
                        {l10n.getString('settings-serial-dfu')}
                      </Button>
                      <Button variant="quaternary" onClick={meow}>
                        {l10n.getString('settings-serial-meow')}
                      </Button>
                    </>
                  )}
                  <Button
                    variant="quaternary"
                    onClick={saveLogToFile}
                    disabled={
                      openedSerialDevice === null || !consoleContent.trim()
                    }
                  >
                    {l10n.getString('settings-serial-save_logs')}
                  </Button>
                  <div className="ml-auto">
                    <Button
                      variant="quaternary"
                      onClick={pauseScroll}
                      icon={
                        isPaused ? (
                          <PlayIcon width={16} />
                        ) : (
                          <PauseIcon width={16} />
                        )
                      }
                    />
                  </div>
                  <form
                    className="w-full flex flex-row gap-2 mobile:col-span-2"
                    onSubmit={(e) => {
                      if (openedSerialDevice === null) {
                        return;
                      }
                      e.preventDefault();
                      if (!acceptedCustomCommandWarning) {
                        setTrySendCustomCommand(true);
                      } else {
                        sendCustomSerialCommand();
                      }
                    }}
                  >
                    <div className="flex-grow">
                      <Input
                        control={control}
                        name="customCommand"
                        className="flex-grow"
                        placeholder={l10n.getString(
                          'settings-serial-send_command-placeholder'
                        )}
                      />
                    </div>
                    <Button
                      variant="quaternary"
                      disabled={openedSerialDevice === null}
                      type="submit"
                    >
                      {l10n.getString('settings-serial-send_command')}
                    </Button>
                  </form>
                </>
              )}

              <div className="w-full mobile:col-span-2">
                <Dropdown
                  control={control}
                  name="port"
                  display="block"
                  placeholder={l10n.getString('settings-serial-serial_select')}
                  items={serialDevices.map((device) => ({
                    label: device.name?.toString() || 'error',
                    value: device.port?.toString() || 'error',
                  }))}
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
