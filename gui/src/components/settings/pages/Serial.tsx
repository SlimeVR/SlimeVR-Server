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
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { Dropdown } from '@/components/commons/Dropdown';
import { Typography } from '@/components/commons/Typography';
import { Localized, useLocalization } from '@fluent/react';
import { BaseModal } from '@/components/commons/BaseModal';
import { WarningBox } from '@/components/commons/TipBox';
import { useIsTauri } from '@/hooks/breakpoint';
import { fileSave } from 'browser-fs-access';
import { save } from '@tauri-apps/plugin-dialog';
import { writeTextFile } from '@tauri-apps/plugin-fs';
import { error } from '@/utils/logging';
import { waitUntil } from '@/utils/a11y';

export interface SerialForm {
  port: string;
}

export function Serial() {
  const { l10n } = useLocalization();
  const { state } = useLocation();

  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const consoleRef = useRef<HTMLDivElement>(null);
  const [consoleContent, setConsole] = useState('');

  const [isSerialOpen, setSerialOpen] = useState(false);
  const [serialDevices, setSerialDevices] = useState<
    Omit<SerialDeviceT, 'pack'>[]
  >([]);

  const [tryFactoryReset, setTryFactoryReset] = useState(false);

  const defaultValues = { port: 'Auto' };
  const { control, watch, handleSubmit, reset } = useForm<SerialForm>({
    defaultValues,
  });

  const { port } = watch();

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = (value: SerialForm) => {
    openSerial(value.port);
    setConsole('');
  };

  const openSerial = (port: string) => {
    sendRPCPacket(RpcMessage.CloseSerialRequest, new CloseSerialRequestT());
    const req = new OpenSerialRequestT();
    req.auto = port === 'Auto';
    req.port = port;
    sendRPCPacket(RpcMessage.OpenSerialRequest, req);
  };

  useEffect(() => {
    sendRPCPacket(RpcMessage.SerialDevicesRequest, new SerialDevicesRequestT());
    const typedState: { serialPort: string } = state as any;
    if (typedState?.serialPort) {
      reset({ port: typedState.serialPort });
    }
  }, []);

  useEffect(() => {
    return () => {
      sendRPCPacket(RpcMessage.CloseSerialRequest, new CloseSerialRequestT());
    };
  }, []);

  useRPCPacket(
    RpcMessage.SerialUpdateResponse,
    (data: SerialUpdateResponseT) => {
      if (data.closed) {
        setSerialOpen(false);
      } else {
        setSerialOpen(true);
      }

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
        },
        ...(res.devices || []),
      ]);
    }
  );

  useEffect(() => {
    if (consoleRef.current)
      consoleRef.current.scrollTo({
        top: consoleRef.current.scrollHeight,
      });
  }, [consoleContent]);

  useEffect(() => {
    const id = setInterval(() => {
      if (!isSerialOpen) {
        openSerial(port ?? defaultValues.port);
      } else {
        clearInterval(id);
      }
    }, 3000);

    return () => {
      clearInterval(id);
    };
  }, [isSerialOpen]);

  const reboot = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerRebootRequest,
      new SerialTrackerRebootRequestT()
    );
  };
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

  const isTauri = useIsTauri();
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

    if (isTauri) {
      save({
        filters: [
          {
            name: l10n.getString('settings-serial-file_type'),
            extensions: ['txt'],
          },
        ],
        defaultPath: 'serial-logs.txt',
      })
        .then((path) =>
          path ? writeTextFile(path, consoleContentRef.current) : undefined
        )
        .catch((err) => {
          error(err);
        });
    } else {
      const blob = new Blob([consoleContentRef.current], {
        type: 'text/plain',
      });
      fileSave(blob, {
        fileName: 'serial-logs.txt',
        extensions: ['.txt'],
      });
    }
  };

  return (
    <>
      <BaseModal
        isOpen={tryFactoryReset}
        onRequestClose={() => setTryFactoryReset(false)}
      >
        <Localized
          id="settings-serial-factory_reset-warning"
          elems={{ b: <b></b> }}
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
                <Typography color="secondary" key={i}>
                  {line}
                </Typography>
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
                {isSerialOpen
                  ? consoleContent
                  : l10n.getString('settings-serial-connection_lost')}
              </pre>
            </div>
          </div>
          <div className="border-t-2 pt-2 border-background-60 border-solid gap-2 flex flex-row">
            <div className="xs:flex flex-grow xs:flex-wrap gap-2 grid grid-cols-2">
              <Button variant="quaternary" onClick={reboot}>
                {l10n.getString('settings-serial-reboot')}
              </Button>
              <Button
                variant="quaternary"
                onClick={() => setTryFactoryReset(true)}
              >
                {l10n.getString('settings-serial-factory_reset')}
              </Button>
              <Button variant="quaternary" onClick={getInfos}>
                {l10n.getString('settings-serial-get_infos')}
              </Button>
              <Button variant="quaternary" onClick={getWifiScan}>
                {l10n.getString('settings-serial-get_wifi_scan')}
              </Button>
              <Button
                variant="quaternary"
                onClick={saveLogToFile}
                disabled={!isSerialOpen || !consoleContent.trim()}
              >
                {l10n.getString('settings-serial-save_logs')}
              </Button>
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
                ></Dropdown>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
