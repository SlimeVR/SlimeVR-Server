import { useOnboarding } from '@/hooks/onboarding';
import classNames from 'classnames';
import { useState, useEffect, createContext } from 'react';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Localized } from '@fluent/react';
import {
  RpcMessage,
  SettingsResponseT,
  ChangeSettingsRequestT,
  ModelSettingsT,
  SettingsRequestT,
  ResetsSettingsT,
  ModelTogglesT,
} from 'solarxr-protocol';
export const LevelContext = createContext(1);

export function QuizMocapPosQuestion() {
  const { applyProgress, setMocapPos, usage, slimeSet, update, mocapPos } =
    useOnboarding();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const [settings, setSettings] = useState<SettingsResponseT>();
  const [to, setTo] = useState('');
  const [disabled, setDisabled] = useState(true);

  applyProgress(0.2);

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
      if (update === 'Yes') {
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
    const modelSettings = new ModelSettingsT();
    const resetSettings = new ResetsSettingsT();

    if (usage === 'mocap') {
      const toggles = Object.assign(
        new ModelTogglesT(),
        settings.modelSettings.toggles
      );
      toggles.selfLocalization = true;
      modelSettings.toggles = toggles;
      req.modelSettings = modelSettings;

      if (mocapPos === 'Forehead') {
        const resets = Object.assign(resetSettings, settings.resetsSettings);
        resets.resetHmdPitch = true;
        req.resetsSettings = resets;
      }
    }

    sendRPCPacket(RpcMessage.ChangeSettingsRequest, req);
  };

  applyProgress(0.2);

  return (
    <div className="flex flex-col w-full h-full xs:justify-center items-center">
      <div className="flex flex-col gap-2">
        <div className="flex gap-2 items-center">
          <Typography variant="main-title" id="onboarding-quiz-q4-title" />
        </div>
        <div className="">
          <div className={classNames('flex flex-col gap-2 flex-grow p-2')}>
            <Typography
              whitespace="whitespace-pre-wrap"
              id="onboarding-quiz-q4-description"
            />
          </div>
          <div className="flex gap-2 px-2 p-6">
            <div
              onClick={() => {
                setMocapPos('Forehead');
                updateTo();
                setDisabled(false);
              }}
              className={classNames(
                'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear hover:bg-background-50 cursor-pointer bg-background-60',
                mocapPos === 'Forehead' &&
                  'outline outline-3 outline-accent-background-40'
              )}
            >
              <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border">
                <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
                  <Typography id="onboarding-quiz-q4-answer-1" />
                </div>
              </div>
            </div>
            <div
              onClick={() => {
                setMocapPos('Face');
                updateTo();
                setDisabled(false);
              }}
              className={classNames(
                'rounded-lg overflow-hidden transition-[box-shadow] duration-200 ease-linear hover:bg-background-50 cursor-pointer bg-background-60',
                mocapPos === 'Face' &&
                  'outline outline-3 outline-accent-background-40'
              )}
            >
              <div className="flex flex-col justify-center rounded-md py-3 pr-4 pl-4 w-full gap-2 box-border">
                <div className="min-h-9 flex text-default justify-center gap-5 flex-wrap items-center">
                  <Typography id="onboarding-quiz-q4-answer-2" />
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
