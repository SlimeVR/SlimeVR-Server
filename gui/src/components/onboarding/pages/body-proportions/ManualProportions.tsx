import { useForm } from 'react-hook-form';
import { RpcMessage, SkeletonResetAllRequestT } from 'solarxr-protocol';
import { useOnboarding } from '@/hooks/onboarding';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { PersonFrontIcon } from '@/components/commons/PersonFrontIcon';
import { Typography } from '@/components/commons/Typography';
import { BodyProportions } from './BodyProportions';
import { useLocalization } from '@fluent/react';
import { useEffect, useMemo } from 'react';
import { useBreakpoint } from '@/hooks/breakpoint';

export function ButtonsControl() {
  const { l10n } = useLocalization();
  const { state } = useOnboarding();
  const { sendRPCPacket } = useWebsocketAPI();

  const resetAll = () => {
    sendRPCPacket(
      RpcMessage.SkeletonResetAllRequest,
      new SkeletonResetAllRequestT()
    );
  };

  return (
    <>
      <Button
        variant="secondary"
        state={{ alonePage: state.alonePage }}
        to="/onboarding/body-proportions/choose"
      >
        {l10n.getString('onboarding-previous_step')}
      </Button>
      <Button variant="secondary" onClick={resetAll}>
        {l10n.getString('reset-reset_all')}
      </Button>
      {!state.alonePage && (
        <Button variant="primary" className="ml-auto" to="/onboarding/done">
          {l10n.getString('onboarding-continue')}
        </Button>
      )}
    </>
  );
}

export function ManualProportionsPage() {
  const { isMobile } = useBreakpoint('mobile');
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();

  applyProgress(0.9);

  const savedValue = useMemo(() => localStorage.getItem('ratioMode'), []);

  const { control, watch } = useForm<{ precise: boolean; ratio: boolean }>({
    defaultValues: { precise: false, ratio: savedValue !== 'false' },
  });
  const { precise, ratio } = watch();

  useEffect(() => {
    localStorage.setItem('ratioMode', ratio.toString());
  }, [ratio]);

  return (
    <>
      <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center overflow-y-auto relative">
        <div className="flex flex-col w-full h-full xs:max-w-5xl xs:justify-center">
          <div className="flex gap-8 justify-center h-full xs:items-center">
            <div className="flex flex-col w-full xs:max-w-2xl gap-3 items-center mobile:justify-around">
              <div className="flex flex-col">
                <Typography variant="main-title">
                  {l10n.getString('onboarding-manual_proportions-title')}
                </Typography>
                <CheckBox
                  control={control}
                  label={l10n.getString('onboarding-manual_proportions-ratio')}
                  name="ratio"
                  variant="toggle"
                ></CheckBox>
                <CheckBox
                  control={control}
                  label={l10n.getString(
                    'onboarding-manual_proportions-precision'
                  )}
                  name="precise"
                  variant="toggle"
                ></CheckBox>
                {isMobile && (
                  <div className="flex gap-3 justify-between">
                    <ButtonsControl></ButtonsControl>
                  </div>
                )}
              </div>
              <div className="w-full px-2">
                <BodyProportions
                  precise={precise}
                  type={ratio ? 'ratio' : 'linear'}
                  variant={state.alonePage ? 'alone' : 'onboarding'}
                ></BodyProportions>
              </div>
            </div>
            <div className="flex-col flex-grow gap-3 rounded-xl fill-background-50 items-center hidden md:flex">
              <PersonFrontIcon width={200}></PersonFrontIcon>
            </div>
          </div>
          {!isMobile && (
            <div className="flex gap-3 my-5 mx-4 justify-between">
              <ButtonsControl></ButtonsControl>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
