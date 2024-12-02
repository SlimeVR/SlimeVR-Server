import { Button } from '@/components/commons/Button';
import { PausableVideo } from '@/components/commons/PausableVideo';
import { Typography } from '@/components/commons/Typography';
import { useOnboarding } from '@/hooks/onboarding';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { VRCHAT_OSC_VIDEO } from '@/utils/tauri';
import { useLocalization } from '@fluent/react';
import classNames from 'classnames';
import { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ChangeSettingsRequestT,
  OSCSettingsT,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
  VRCOSCSettingsT,
} from 'solarxr-protocol';

export function StandaloneUsageSetup() {
  const { applyProgress, state } = useOnboarding();
  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
  const fetchedSettings = useRef<OSCSettingsT | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  const toggleVrc = (bool: boolean) => {
    const oldOscSettings = fetchedSettings.current;

    const settings = new ChangeSettingsRequestT();
    const vrcOsc = new VRCOSCSettingsT();
    vrcOsc.oscSettings = new OSCSettingsT(
      bool,
      oldOscSettings?.portIn,
      oldOscSettings?.portOut,
      oldOscSettings?.address
    );

    settings.vrcOsc = vrcOsc;
    sendRPCPacket(RpcMessage.ChangeSettingsRequest, settings);
  };

  useRPCPacket(
    RpcMessage.SettingsResponse,
    (oldSettings: SettingsResponseT) => {
      fetchedSettings.current = oldSettings.vrcOsc?.oscSettings ?? null;

      toggleVrc(true);
    }
  );

  applyProgress(0.6);

  return (
    <div className="flex items-center justify-center h-full w-full mobile:pt-10">
      <div className="flex mobile:flex-col items-center xs:justify-center gap-5">
        <div className="mb-auto">
          <Typography variant="main-title">
            {l10n.getString('onboarding-usage-vr-standalone-title')}
          </Typography>
        </div>
        <div
          className={classNames(
            'flex flex-col rounded-lg p-6 gap-5 w-10/12 max-w-[600px]',
            !state.alonePage && 'bg-background-70',
            state.alonePage && 'bg-background-60'
          )}
        >
          <div className="self-center">
            {l10n
              .getString('settings-osc-vrchat-description-guide')
              .split('\n')
              .map((line, i) => (
                <Typography color="secondary" key={i}>
                  {line}
                </Typography>
              ))}
          </div>
          <div className="self-center">
            <PausableVideo src={VRCHAT_OSC_VIDEO} />
          </div>
          <div className="flex">
            <Button
              variant={!state.alonePage ? 'secondary' : 'tertiary'}
              className="self-start mt-auto"
              onClick={() => {
                toggleVrc(false);
                navigate('/onboarding/usage/vr/choose', {
                  state: { alonePage: state.alonePage },
                });
              }}
            >
              {l10n.getString('onboarding-previous_step')}
            </Button>
            <Button
              variant="primary"
              to=""
              className="ml-auto"
              state={{ alonePage: state.alonePage }}
            >
              {l10n.getString('onboarding-usage-vr-standalone-next')}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
