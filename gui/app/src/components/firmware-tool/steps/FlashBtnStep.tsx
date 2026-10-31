import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import { VerticalStepComponentProps } from '@/components/commons/VerticalStepper';

export function FlashBtnStep({
  nextStep,
  prevStep,
}: VerticalStepComponentProps) {
  const { l10n } = useLocalization();
  const { selectedSource } = useFirmwareTool();

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography>
            {l10n.getString('firmware_tool-flashbtn_step-description')}
          </Typography>
          {selectedSource?.source.board === 'BOARD_SLIMEVR' ||
          selectedSource?.source.board === 'BOARD_SLIMEVR_V1_2' ? (
            <>
              <Typography variant="standard" whitespace="whitespace-pre-wrap">
                {l10n.getString('firmware_tool-flashbtn_step-board_SLIMEVR')}
              </Typography>
              <div className="gap-2 grid lg:grid-cols-3 md:grid-cols-2 mobile:grid-cols-1">
                <div className="bg-background-80 p-2 rounded-lg gap-2 flex flex-col justify-between">
                  <Typography variant="main-title">R11</Typography>
                  <Typography variant="standard">
                    {l10n.getString(
                      'firmware_tool-flashbtn_step-board_SLIMEVR-r11-v2'
                    )}
                  </Typography>
                  <img src="/images/R11_board_reset.webp" />
                </div>
                <div className="bg-background-80 p-2 rounded-lg gap-2 flex flex-col justify-between">
                  <Typography variant="main-title">R12</Typography>
                  <Typography variant="standard">
                    {l10n.getString(
                      'firmware_tool-flashbtn_step-board_SLIMEVR-r12-v2'
                    )}
                  </Typography>
                  <img src="/images/R12_board_reset.webp" />
                </div>

                <div className="bg-background-80 p-2 rounded-lg gap-2 flex flex-col justify-between">
                  <Typography variant="main-title">R14</Typography>
                  <Typography variant="standard">
                    {l10n.getString(
                      'firmware_tool-flashbtn_step-board_SLIMEVR-r14-v2'
                    )}
                  </Typography>
                  <img src="/images/R14_board_reset_sw.webp" />
                </div>
              </div>
            </>
          ) : (
            <>
              <Typography variant="standard" whitespace="whitespace-pre">
                {l10n.getString('firmware_tool-flashbtn_step-board_OTHER')}
              </Typography>
            </>
          )}
          <div className="flex justify-between">
            <Localized id="firmware_tool-previous_step">
              <Button
                variant="secondary"
                onClick={() => {
                  prevStep();
                }}
              />
            </Localized>
            <Localized id="firmware_tool-next_step">
              <Button
                variant="primary"
                onClick={() => {
                  nextStep();
                }}
              />
            </Localized>
          </div>
        </div>
      </div>
    </>
  );
}
