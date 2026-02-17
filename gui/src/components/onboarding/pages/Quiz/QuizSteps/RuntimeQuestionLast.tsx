import { useOnboarding } from '@/hooks/onboarding';
import classNames from 'classnames';
import { useState, useEffect } from 'react';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Localized } from '@fluent/react';
import {
  RpcMessage,
  SettingsResponseT,
  ChangeSettingsRequestT,
  SettingsRequestT,
  VRCOSCSettingsT,
} from 'solarxr-protocol';

export function QuizRuntimeQuestion() {
  const { applyProgress, setRuntime, slimeSet, update, runtime } =
    useOnboarding();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [settings, setSettings] = useState<SettingsResponseT>();
  const [to, setTo] = useState('');
  const [disabled, setDisabled] = useState(true);

  applyProgress(0.4);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    setSettings(settings);
  });

  const updateTo = () => {
    if (slimeSet === 'butterfly') {
      setTo('/onboarding/dongle');
    } else {
      if (update === true) {
        setTo('/onboarding/firmware-tool');
      } else {
        setTo('/onboarding/wifi-creds');
      }
    }
  };

  const applySettings = () => {
    if (!settings?.modelSettings || !settings?.vrcOsc)
      throw 'settings should be set';
    const req = new ChangeSettingsRequestT();
    const oscSettings = new VRCOSCSettingsT();

    if (runtime === 'standalone') {
      const osc = Object.assign(oscSettings, settings.vrcOsc.oscSettings);
      osc.enabled = true;
      oscSettings.oscSettings = osc;
      req.vrcOsc = oscSettings;
    }

    sendRPCPacket(RpcMessage.ChangeSettingsRequest, req);
  };

  return (
    <div className="flex flex-col w-full h-full xs:justify-center items-center">
      <div className="flex flex-col gap-2">
        <div className="flex gap-2 items-center">
          <Typography variant="main-title" id="onboarding-quiz-runtime-title" />
        </div>
        <div className="">
          <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
            <Typography
              whitespace="whitespace-pre-wrap"
              id="onboarding-quiz-runtime-description"
            />
          </div>
          <div className="flex gap-2 px-2 p-6">
            <div
              onClick={() => {
                setRuntime('steamvr');
                updateTo();
                setDisabled(false);
              }}
              className={classNames(
                'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear hover:bg-background-50 cursor-pointer bg-background-60',
                runtime === 'steamvr' &&
                  'outline outline-3 outline-accent-background-40'
              )}
            >
              <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border">
                <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
                  <Typography id="onboarding-quiz-runtime-answer-steamvr" />
                </div>
              </div>
            </div>
            <div
              onClick={() => {
                setRuntime('standalone');
                updateTo();
                setDisabled(false);
              }}
              className={classNames(
                'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear hover:bg-background-50 cursor-pointer bg-background-60',
                runtime === 'standalone' &&
                  'outline outline-3 outline-accent-background-40'
              )}
            >
              <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border">
                <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
                  <Typography id="onboarding-quiz-runtime-answer-standalone" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="flex px-2 p-6">
          <Localized id="onboarding-quiz_continue">
            <Button
              to={to}
              children="Continue"
              variant="primary"
              onClick={applySettings}
              disabled={disabled}
            />
          </Localized>
        </div>
      </div>
    </div>
  );
}
