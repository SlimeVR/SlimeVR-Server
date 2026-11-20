import { defineSettingsPage } from '@/components/settings/settings-definitions';
import classNames from 'classnames';
import {
  KeyboardEvent,
  useEffect,
  useLayoutEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { useForm } from 'react-hook-form';
import { useLocation } from 'react-router-dom';
import {
  CloseSerialRequestT,
  OpenSerialRequestT,
  RpcMessage,
  SerialConsoleStatus,
  SerialDeviceT,
  SerialTrackerFactoryResetRequestT,
  SerialTrackerGetInfoRequestT,
  SerialTrackerRebootRequestT,
  SerialUpdateResponseT,
  SerialTrackerGetWifiScanRequestT,
  SerialTrackerCustomCommandRequestT,
  SerialDeviceType,
  SerialKeepaliveRequestT,
  LogLevel,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useSerialDevices } from '@/hooks/serial';
import { Button } from '@/components/commons/Button';
import { Dropdown } from '@/components/commons/Dropdown';
import { Typography } from '@/components/commons/Typography';
import { Localized, useLocalization } from '@fluent/react';
import { BaseModal } from '@/components/commons/BaseModal';
import { WarningBox } from '@/components/commons/TipBox';
import { saveFile } from '@/utils/save-file';
import { waitUntil } from '@/utils/a11y';
import { PopoverMenu, PopoverMenuItem } from '@/components/commons/PopoverMenu';
import { ArrowDownIcon } from '@/components/commons/icon/ArrowIcons';
import { DownloadIcon } from '@/components/commons/icon/DownloadIcon';
import { USBIcon } from '@/components/commons/icon/UsbIcon';
import { ButterflyIcon } from '@/components/commons/icon/ButterflyIcon';
import { SlimeNormalIcon } from '@/components/commons/icon/SlimeNormalIcon';
import { SettingsPagePaneLayout } from '@/components/settings/SettingsPagePaneLayout';
import { LogView } from '@/components/commons/log-view/LogView';
import {
  DotLabel,
  FilterDropdown,
  FilterPlace,
  LevelFilter,
  LogFilterBar,
} from '@/components/commons/log-view/LogFilterBar';
import {
  ClearLogButton,
  LogFooter,
  PauseLogButton,
  usePausableLines,
} from '@/components/commons/log-view/LogFooter';
import {
  nameColor,
  passesLevel,
  passesQuery,
} from '@/components/commons/log-view/log-line';
import {
  appendTextLog,
  clearTextLog,
  EMPTY_TEXT_LOG,
  textLogLines,
} from '@/components/commons/log-view/text-log-stream';
import { useBreakpoint } from '@/hooks/breakpoint';
import {
  espLogParser,
  LogLineParser,
  zephyrLogParser,
} from '@/utils/log-line-parsers';

export interface SerialForm {
  port: string;
}

interface SerialCommand {
  id: string;
  run: () => void;
  danger?: boolean;
}

const MAX_COMMAND_HISTORY = 50;

const consoleStatusMessage: Partial<Record<SerialConsoleStatus, string>> = {
  [SerialConsoleStatus.OPENING]: 'settings-serial-opening',
  [SerialConsoleStatus.WAITING]: 'settings-serial-connection_lost',
  [SerialConsoleStatus.BUSY]: 'settings-serial-busy',
  [SerialConsoleStatus.OPEN_FAILED]: 'settings-serial-open_failed',
};

const MAX_CONSOLE_LINES = 5_000;

// Several of these fit in the server's grace window, so one dropped message costs nothing
const SERIAL_KEEPALIVE_MS = 4_000;

const ALL_MODULES = '*';

const SERIAL_LOG_PARSERS: Record<SerialDeviceType, LogLineParser | null> = {
  [SerialDeviceType.ESP_TRACKER]: espLogParser,
  [SerialDeviceType.HID_RECEIVER]: zephyrLogParser,
  [SerialDeviceType.HID_TRACKER]: zephyrLogParser,
  [SerialDeviceType.UNKNOWN]: null,
};

function serialLogParser(type: SerialDeviceType | undefined) {
  return SERIAL_LOG_PARSERS[type ?? SerialDeviceType.UNKNOWN];
}

function SerialDeviceIcon({
  type,
  name,
}: {
  type: SerialDeviceType;
  name: string;
}) {
  switch (type) {
    case SerialDeviceType.ESP_TRACKER:
      return <SlimeNormalIcon width={16} />;
    case SerialDeviceType.HID_RECEIVER:
      return <USBIcon size={16} />;
    case SerialDeviceType.HID_TRACKER:
      return name.toLowerCase().includes('butterfly') ? (
        <ButterflyIcon width={16} />
      ) : (
        <SlimeNormalIcon width={16} />
      );
    default:
      return null;
  }
}

export const serialSettingsPage = defineSettingsPage({
  path: 'serial',
  title: 'settings-serial',
  groups: {},
});

export function Serial() {
  const { l10n } = useLocalization();
  const { state } = useLocation();

  const { useRPCPacket, sendRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const [consoleState, setConsole] = useState(EMPTY_TEXT_LOG);

  const [consoleStatus, setConsoleStatus] =
    useState<SerialConsoleStatus | null>(null);
  const [openedSerialDevice, setOpenedSerialDevice] = useState<Omit<
    SerialDeviceT,
    'pack'
  > | null>(null);
  const consoleLines = useMemo(
    () => textLogLines(consoleState, serialLogParser(openedSerialDevice?.type)),
    [consoleState, openedSerialDevice?.type]
  );
  const pausable = usePausableLines(consoleLines);

  const [tryFactoryReset, setTryFactoryReset] = useState(false);
  const [trySendCustomCommand, setTrySendCustomCommand] = useState(false);

  const [acceptedCustomCommandWarning, setAcceptedCustomCommandWarning] =
    useState(false);
  const [command, setCommand] = useState('');
  const [history, setHistory] = useState<string[]>([]);
  const [historyIndex, setHistoryIndex] = useState<number | null>(null);
  const [query, setQuery] = useState('');
  const [minLevel, setMinLevel] = useState(LogLevel.DEBUG);
  const [module, setModule] = useState(ALL_MODULES);
  const { isNmd: compactBar } = useBreakpoint('nmd');

  const typedState: { serialPort?: string } | null = state;
  const { control, watch, setValue } = useForm<SerialForm>({
    defaultValues: { port: typedState?.serialPort ?? '' },
  });

  const port = watch('port');
  const serialDevices = useSerialDevices();

  // The server holds every port this page opened and takes them back if the keepalive stops, so a
  // tab that dies without unmounting doesn't leave them locked
  useConnectedEffect(() => {
    const keepalive = setInterval(() => {
      sendRPCPacket(
        RpcMessage.SerialKeepaliveRequest,
        new SerialKeepaliveRequestT()
      );
    }, SERIAL_KEEPALIVE_MS);
    return () => {
      clearInterval(keepalive);
      sendRPCPacket(RpcMessage.CloseSerialRequest, new CloseSerialRequestT());
    };
  }, []);

  useEffect(() => {
    if (!serialDevices?.length) return;
    if (!serialDevices.some((device) => device.port === port)) {
      setValue('port', serialDevices[0].port?.toString() ?? '');
    }
  }, [serialDevices]);

  useConnectedEffect(() => {
    if (!port) return;
    setConsole(EMPTY_TEXT_LOG);
    pausable.resume();
    setModule(ALL_MODULES);
    setOpenedSerialDevice(null);
    setConsoleStatus(SerialConsoleStatus.OPENING);
    const req = new OpenSerialRequestT();
    req.port = port;
    sendRPCPacket(RpcMessage.OpenSerialRequest, req);
  }, [port]);

  useRPCPacket(
    RpcMessage.SerialUpdateResponse,
    (data: SerialUpdateResponseT) => {
      setConsoleStatus(data.status);
      if (data.status === SerialConsoleStatus.OPEN && data.device) {
        setOpenedSerialDevice(data.device);
      } else {
        setOpenedSerialDevice(null);
      }

      if (data.log) {
        const log = data.log as string;
        const nowMs = Date.now();
        const parser = serialLogParser(data.device?.type);
        setConsole((console) =>
          appendTextLog(console, log, nowMs, parser, MAX_CONSOLE_LINES)
        );
      }
    }
  );

  const sendCommand = (text: string) => {
    sendRPCPacket(
      RpcMessage.SerialTrackerCustomCommandRequest,
      new SerialTrackerCustomCommandRequestT(text)
    );
  };
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
    sendCommand(command);
    setHistory((current) =>
      [...current.filter((entry) => entry !== command), command].slice(
        -MAX_COMMAND_HISTORY
      )
    );
    setHistoryIndex(null);
    setTrySendCustomCommand(false);
    setCommand('');
  };

  const browseHistory = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'ArrowUp') {
      if (history.length === 0) return;
      e.preventDefault();
      const index =
        historyIndex === null
          ? history.length - 1
          : Math.max(historyIndex - 1, 0);
      setHistoryIndex(index);
      setCommand(history[index]);
      return;
    }
    if (e.key === 'ArrowDown') {
      if (historyIndex === null) return;
      e.preventDefault();
      const index = historyIndex + 1;
      if (index >= history.length) {
        setHistoryIndex(null);
        setCommand('');
        return;
      }
      setHistoryIndex(index);
      setCommand(history[index]);
    }
  };

  const clearConsole = () => {
    setConsole(clearTextLog);
    pausable.clearFrozen();
  };

  const consoleLinesRef = useRef(consoleLines);
  useLayoutEffect(() => {
    consoleLinesRef.current = consoleLines;
  }, [consoleLines]);
  const consoleText = () =>
    consoleLinesRef.current.map((line) => line.text).join('\n');

  const saveLogToFile = async () => {
    // Check if we have getInfos and fetch them if we don't
    if (!consoleText().includes('GET INFO')) {
      getInfos();
      await waitUntil(() => consoleText().includes('GET INFO'), 100, 10);
    }

    const blob = new Blob([consoleText()], {
      type: 'text/plain',
    });
    await saveFile(blob, 'serial-logs.txt', ['.txt']);
  };

  const deviceType = openedSerialDevice?.type;
  const isEspDevice = deviceType === SerialDeviceType.ESP_TRACKER;
  // Pairing, DFU and calibration are HID firmware commands, unrecognized devices get none of them
  const isHidDevice =
    deviceType === SerialDeviceType.HID_RECEIVER ||
    deviceType === SerialDeviceType.HID_TRACKER;

  const commands: SerialCommand[] = [
    { id: 'settings-serial-reboot', run: reboot },
    ...(isEspDevice
      ? [{ id: 'settings-serial-get_wifi_scan', run: getWifiScan }]
      : []),
    ...(isHidDevice
      ? [
          {
            id: 'settings-serial-enter_pairing',
            run: () => sendCommand('pair'),
          },
        ]
      : []),
    ...(deviceType === SerialDeviceType.HID_RECEIVER
      ? [{ id: 'settings-serial-exit_pairing', run: () => sendCommand('exit') }]
      : []),
    ...(deviceType === SerialDeviceType.HID_TRACKER
      ? [
          {
            id: 'settings-serial-calibrate',
            run: () => sendCommand('calibrate'),
          },
          {
            id: 'settings-serial-six_side_calibrate',
            run: () => sendCommand('6-side'),
          },
        ]
      : []),
    ...(isHidDevice
      ? [
          { id: 'settings-serial-dfu', run: () => sendCommand('dfu') },
          { id: 'settings-serial-meow', run: () => sendCommand('meow') },
        ]
      : []),
    ...(isEspDevice
      ? [
          {
            id: 'settings-serial-factory_reset',
            run: () => setTryFactoryReset(true),
            danger: true,
          },
        ]
      : []),
  ];

  const modules = useMemo(
    () =>
      [
        ...new Set(
          consoleLines.flatMap((line) => (line.label ? [line.label] : []))
        ),
      ].sort((a, b) => a.localeCompare(b)),
    [consoleLines]
  );
  const visibleLines = useMemo(
    () =>
      pausable.lines.filter(
        (line) =>
          passesLevel(line.level, minLevel) &&
          (module === ALL_MODULES || line.label === module) &&
          passesQuery(line.text, query)
      ),
    [pausable.lines, minLevel, module, query]
  );

  const filtersActive = minLevel !== LogLevel.DEBUG || module !== ALL_MODULES;

  const renderFilters = (place: FilterPlace) => (
    <>
      <LevelFilter
        place={place}
        name="serial-level"
        value={minLevel}
        onChange={setMinLevel}
      />
      {modules.length > 0 && (
        <FilterDropdown
          place={place}
          name="serial-module"
          label={l10n.getString('settings-serial-module')}
          value={module}
          onChange={setModule}
          items={[
            {
              value: ALL_MODULES,
              label: <Typography tag="span" id="settings-serial-module-all" />,
            },
            ...modules.map((name) => ({
              value: name,
              label: <DotLabel color={nameColor(name)}>{name}</DotLabel>,
            })),
          ]}
        />
      )}
    </>
  );

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
      <SettingsPagePaneLayout
        icon={<USBIcon size={24} />}
        title={serialSettingsPage.title}
        className="h-full"
        contentClassName="flex flex-col flex-grow"
      >
        <div className="flex flex-col pb-2">
          <>
            {l10n
              .getString('settings-serial-description')
              .split('\n')
              .map((line, i) => (
                <Typography key={i}>{line}</Typography>
              ))}
          </>
        </div>
        <div className="flex flex-col gap-2 flex-grow h-0">
          <LogFilterBar
            renderFilters={renderFilters}
            filtersActive={filtersActive}
            searchName="serial-search"
            searchPlaceholder={l10n.getString('settings-serial-search')}
            query={query}
            onQueryChange={setQuery}
            leading={
              <div className="w-72 mobile:w-full">
                <Dropdown
                  control={control}
                  name="port"
                  display="block"
                  direction="down"
                  placeholder={l10n.getString('settings-serial-serial_select')}
                  items={(serialDevices ?? []).map((device) => ({
                    label: (
                      <span className="flex items-center gap-2 min-w-0">
                        <span className="flex w-4 shrink-0 justify-center fill-background-10">
                          <SerialDeviceIcon
                            type={device.type}
                            name={device.name?.toString() ?? ''}
                          />
                        </span>
                        <span className="truncate">
                          {device.name?.toString() || 'error'}
                        </span>
                      </span>
                    ),
                    value: device.port?.toString() || 'error',
                  }))}
                />
              </div>
            }
          />
          <div className="bg-background-80 rounded-lg flex-grow h-0 min-h-[160px] flex flex-col p-2">
            {openedSerialDevice !== null ? (
              <LogView lines={visibleLines} query={query} />
            ) : (
              <div className="flex-grow">
                <pre>
                  {l10n.getString(
                    (consoleStatus !== null &&
                      consoleStatusMessage[consoleStatus]) ||
                      'settings-serial-no_port'
                  )}
                </pre>
              </div>
            )}
            {openedSerialDevice !== null && (
              <>
                <div className="mt-1.5 flex gap-2 items-stretch">
                  <form
                    className="flex-grow min-w-0 flex items-center gap-2 bg-background-70 rounded-md pl-3 font-mono text-[12px] focus-within:ring-2 focus-within:ring-accent-background-30"
                    onSubmit={(e) => {
                      e.preventDefault();
                      if (!command.trim()) return;
                      if (!acceptedCustomCommandWarning) {
                        setTrySendCustomCommand(true);
                        return;
                      }
                      sendCustomSerialCommand();
                    }}
                  >
                    <span className="text-background-10 select-none">&gt;</span>
                    <input
                      className="flex-grow min-w-0 bg-transparent border-0 outline-none focus:ring-0 px-0 py-2 text-[12px] text-background-10 placeholder:text-background-10"
                      value={command}
                      spellCheck={false}
                      autoComplete="off"
                      placeholder={l10n.getString(
                        'settings-serial-send_command-placeholder'
                      )}
                      onChange={(e) => {
                        setCommand(e.target.value);
                        setHistoryIndex(null);
                      }}
                      onKeyDown={browseHistory}
                    />
                    <Button
                      variant="quaternary"
                      size="small"
                      type="submit"
                      disabled={!command.trim()}
                      id="settings-serial-send_command"
                    />
                  </form>
                  <PopoverMenu
                    drawerTitle={
                      <Typography
                        variant="section-title"
                        id="settings-serial-commands"
                      />
                    }
                    trigger={(triggerProps, open) => (
                      <Button variant="primary" size="small" {...triggerProps}>
                        <span className="flex items-center gap-2">
                          <Typography
                            tag="span"
                            id="settings-serial-commands"
                          />
                          <span
                            className={classNames(
                              'flex fill-background-10 transition-transform',
                              open && 'rotate-180'
                            )}
                          >
                            <ArrowDownIcon size={12} />
                          </span>
                        </span>
                      </Button>
                    )}
                  >
                    {({ close }) =>
                      commands.map(({ id, run, danger }) => (
                        <PopoverMenuItem
                          key={id}
                          className={classNames(
                            danger && 'border-t border-background-50'
                          )}
                          onClick={() => {
                            close();
                            run();
                          }}
                        >
                          <Typography
                            bold
                            color={danger ? 'text-status-critical' : undefined}
                            id={id}
                          />
                        </PopoverMenuItem>
                      ))
                    }
                  </PopoverMenu>
                </div>
                <LogFooter
                  status={pausable.paused ? 'paused' : 'live'}
                  shown={visibleLines.length}
                  total={pausable.lines.length}
                >
                  <Button
                    variant="secondary"
                    size="small"
                    onClick={saveLogToFile}
                    disabled={!consoleLines.some((line) => line.text.trim())}
                    icon={
                      <DownloadIcon width={14} className="fill-background-10" />
                    }
                    id="settings-serial-save_logs"
                    iconOnly={compactBar}
                  />
                  <ClearLogButton
                    onClick={clearConsole}
                    disabled={consoleLines.length === 0}
                    iconOnly={compactBar}
                  />
                  <PauseLogButton
                    paused={pausable.paused}
                    onToggle={pausable.togglePause}
                    iconOnly={compactBar}
                  />
                </LogFooter>
              </>
            )}
          </div>
        </div>
      </SettingsPagePaneLayout>
    </>
  );
}
