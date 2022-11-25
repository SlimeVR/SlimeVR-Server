import { useEffect, useRef, useState } from 'react';
import {
  CloseSerialRequestT,
  OpenSerialRequestT,
  RpcMessage,
  SerialTrackerFactoryResetRequestT,
  SerialTrackerGetInfoRequestT,
  SerialTrackerRebootRequestT,
  SerialUpdateResponseT,
} from 'solarxr-protocol';
import { useElemSize, useLayout } from '../../../hooks/layout';
import { useWebsocketAPI } from '../../../hooks/websocket-api';
import { Button } from '../../commons/Button';
import { Typography } from '../../commons/Typography';

export interface WifiForm {
  ssid: string;
  password: string;
}

export function Serial() {
  const {
    layoutHeight,
    layoutWidth,
    ref: consoleRef,
  } = useLayout<HTMLDivElement>();

  const toolbarRef = useRef<HTMLDivElement>(null);
  const { height } = useElemSize(toolbarRef);

  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  // const consoleRef = useRef<HTMLPreElement>(null);
  const [consoleContent, setConsole] = useState('');
  const [isSerialOpen, setSerialOpen] = useState(false);

  useEffect(() => {
    sendRPCPacket(RpcMessage.OpenSerialRequest, new OpenSerialRequestT());
    return () => {
      sendRPCPacket(RpcMessage.CloseSerialRequest, new CloseSerialRequestT());
    };
  }, []);

  useRPCPacket(
    RpcMessage.SerialUpdateResponse,
    (data: SerialUpdateResponseT) => {
      if (data.closed) {
        setSerialOpen(false);
        setTimeout(() => {
          sendRPCPacket(RpcMessage.OpenSerialRequest, new OpenSerialRequestT());
        }, 1000);
      }

      if (!data.closed) {
        setSerialOpen(true);
      }

      if (data.log && consoleRef.current) {
        setConsole((console) => console + data.log);
      }
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
      if (!isSerialOpen)
        sendRPCPacket(RpcMessage.OpenSerialRequest, new OpenSerialRequestT());
      else clearInterval(id);
    }, 1000);

    return () => {
      clearInterval(id);
    };
  }, [isSerialOpen, sendRPCPacket]);

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
  };
  const getInfos = () => {
    sendRPCPacket(
      RpcMessage.SerialTrackerGetInfoRequest,
      new SerialTrackerGetInfoRequestT()
    );
  };

  return (
    <div className="flex flex-col bg-background-70 h-full p-5 rounded-md">
      <div className="flex flex-col pb-2">
        <Typography variant="main-title">Serial Console</Typography>
        <Typography color="secondary">
          This is a live information feed for serial communication.
        </Typography>
        <Typography color="secondary">
          May be useful if you need to know the firmware is acting up.
        </Typography>
      </div>
      <div className="bg-background-80 rounded-lg flex flex-col p-2">
        <div
          ref={consoleRef}
          className="overflow-x-auto overflow-y-auto"
          style={{
            height: layoutHeight - height - 30,
            width: layoutWidth - 24,
          }}
        >
          <div className="flex select-text px-3">
            <pre>
              {isSerialOpen
                ? consoleContent
                : 'Connection to serial lost, Reconnecting...'}
            </pre>
          </div>
        </div>
        <div className="" ref={toolbarRef}>
          <div className="border-t-2 pt-2  border-background-60 border-solid m-2 gap-2 flex flex-row">
            <Button variant="quaternary" onClick={reboot}>
              Reboot
            </Button>
            <Button variant="quaternary" onClick={factoryReset}>
              Factory Reset
            </Button>
            <Button variant="quaternary" onClick={getInfos}>
              Get Infos
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
