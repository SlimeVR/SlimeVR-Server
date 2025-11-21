import { useOnboarding } from '@/hooks/onboarding';
import { Typography } from '@/components/commons/Typography';
import {
  HeightContextC,
  useProvideHeightContext,
} from '@/hooks/height';
import { Button } from '@/components/commons/Button';
import { Localized, useLocalization } from '@fluent/react';
import { WarningBox } from '@/components/commons/TipBox';
import { useAtomValue } from 'jotai';
import { serverGuardsAtom } from '@/store/app-store';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useState } from 'react';
import { RpcMessage, StartUserHeightCalibationT, UserHeightCalibrationStatus, UserHeightRecordingStatusResponseT } from 'solarxr-protocol';

export function ScaledProportionsPage() {
  const { l10n } = useLocalization();
  const { applyProgress, state } = useOnboarding();
  const heightContext = useProvideHeightContext();

  const serverGuards = useAtomValue(serverGuardsAtom);

  const [status, setState] = useState<UserHeightRecordingStatusResponseT>();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();


  applyProgress(0.9);


  useRPCPacket(RpcMessage.UserHeightRecordingStatusResponse, (res: UserHeightRecordingStatusResponseT) => {
    setState(res);
  })

  const start = () => {
    sendRPCPacket(RpcMessage.StartUserHeightCalibation, new StartUserHeightCalibationT());
  }

  return (
    <HeightContextC.Provider value={heightContext}>
      <div className="flex flex-col gap-5 h-full items-center w-full xs:justify-center overflow-y-auto overflow-x-hidden relative px-4 pb-4">
        <div className="flex flex-col w-full xs:h-full xs:justify-center max-w-3xl gap-5">
          <div className="flex flex-col max-w-lg gap-3">
            <Typography variant="main-title">
              {l10n.getString('onboarding-scaled_proportions-title')}
            </Typography>
            <div>
              <Typography>
                {l10n.getString('onboarding-scaled_proportions-description')}
              </Typography>
            </div>
          </div>

          {!serverGuards?.canDoUserHeightCalibration && (
            <WarningBox>
              <Localized
                id="onboarding-scaled_proportions-manual_height-warning"
                elems={{ b: <b /> }}
              >
                <Typography
                  whitespace="whitespace-pre-line"
                  color="text-background-60"
                />
              </Localized>
              <ul className="list-disc ml-8">
                <Localized id="onboarding-scaled_proportions-manual_height-warning-no_hmd">
                  <li />
                </Localized>
              </ul>
            </WarningBox>
          )}
          <div className="flex min-h-0">
            <Button onClick={start} variant='primary'>Start</Button>
            <pre>
              {JSON.stringify(status)}
            </pre>
            {/* <StepperSlider
              variant={state.alonePage ? 'alone' : 'onboarding'}
              steps={
                !canDoAuto
                  ? [
                      { type: 'numbered', component: ManualHeightStep },
                      { type: 'numbered', component: ResetProportionsStep },
                      { type: 'fullsize', component: DoneStep },
                    ]
                  : [
                      { type: 'numbered', component: CheckHeightStep },
                      { type: 'numbered', component: CheckFloorHeightStep },
                      { type: 'numbered', component: ResetProportionsStep },
                      { type: 'fullsize', component: DoneStep },
                    ]
              }
              back={() => navigate('/onboarding/reset-tutorial', { state })}
            /> */}



          </div>
          {state.alonePage && (
            <div className="flex justify-end">
              <Localized id="onboarding-manual_proportions-title">
                <Button
                  to="/onboarding/body-proportions/manual"
                  variant="secondary"
                  state={{ alonePage: state.alonePage }}
                />
              </Localized>
            </div>
          )}
        </div>
      </div>
    </HeightContextC.Provider>
  );
}
