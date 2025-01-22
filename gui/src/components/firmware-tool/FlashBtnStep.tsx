import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { Button } from '@/components/commons/Button';
import {
  boardTypeToFirmwareToolBoardType,
  useFirmwareTool,
} from '@/hooks/firmware-tool';
import { BoardType } from 'solarxr-protocol';

export function FlashBtnStep({
  nextStep,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
  isActive: boolean;
}) {
  const { l10n } = useLocalization();
  const { defaultConfig } = useFirmwareTool();

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware_tool-flashbtn_step-description')}
          </Typography>
          {defaultConfig?.boardConfig.type ===
          boardTypeToFirmwareToolBoardType[BoardType.SLIMEVR] ? (
            <>
              <Typography variant="standard" whitespace="whitespace-pre">
                {l10n.getString('firmware_tool-flashbtn_step-board_SLIMEVR')}
              </Typography>
              <div className="gap-2 grid lg:grid-cols-3 md:grid-cols-2 mobile:grid-cols-1">
                <div className="bg-background-80 p-2 rounded-lg gap-2 flex flex-col justify-between">
                  <Typography variant="main-title">R11</Typography>
                  <Typography variant="standard">
                    {l10n.getString(
                      'firmware_tool-flashbtn_step-board_SLIMEVR-r11'
                    )}
                  </Typography>
                  <img src="/images/R11_board_reset.webp"></img>
                </div>
                <div className="bg-background-80 p-2 rounded-lg gap-2 flex flex-col justify-between">
                  <Typography variant="main-title">R12</Typography>
                  <Typography variant="standard">
                    {l10n.getString(
                      'firmware_tool-flashbtn_step-board_SLIMEVR-r12'
                    )}
                  </Typography>
                  <img src="/images/R12_board_reset.webp"></img>
                </div>

                <div className="bg-background-80 p-2 rounded-lg gap-2 flex flex-col justify-between">
                  <Typography variant="main-title">R14</Typography>
                  <Typography variant="standard">
                    {l10n.getString(
                      'firmware_tool-flashbtn_step-board_SLIMEVR-r14'
                    )}
                  </Typography>
                  <img src="/images/R14_board_reset_sw.webp"></img>
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
          <div className="flex justify-end">
            <Localized id="firmware_tool-next_step">
              <Button
                variant="primary"
                onClick={() => {
                  nextStep();
                }}
              ></Button>
            </Localized>
          </div>
        </div>
      </div>
    </>
  );
}
