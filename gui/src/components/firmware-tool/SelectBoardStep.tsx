import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import {
  firmwareToolToBoardType,
  useFirmwareTool,
} from '@/hooks/firmware-tool';
import { CreateBoardConfigDTO } from '@/firmware-tool-api/firmwareToolSchemas';
import classNames from 'classnames';
import { Button } from '@/components/commons/Button';
import { useGetFirmwaresBoards } from '@/firmware-tool-api/firmwareToolComponents';
import { BoardType } from 'solarxr-protocol';

export function SelectBoardStep({
  nextStep,
  goTo,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
}) {
  const { l10n } = useLocalization();
  const { selectBoard, newConfig, defaultConfig } = useFirmwareTool();
  const { isFetching, data: boards } = useGetFirmwaresBoards({});

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware_tool-board_step-description')}
          </Typography>
        </div>
        <div className="my-4">
          {!isFetching && (
            <div className="gap-2 flex flex-col">
              <div className="grid sm:grid-cols-2 mobile-settings:grid-cols-1 gap-2">
                {boards?.map((board) => (
                  <div
                    key={board}
                    className={classNames(
                      'p-3 rounded-md hover:bg-background-50',
                      {
                        'bg-background-50 text-background-10':
                          newConfig?.boardConfig?.type === board,
                        'bg-background-60':
                          newConfig?.boardConfig?.type !== board,
                      }
                    )}
                    onClick={() => {
                      selectBoard(board as CreateBoardConfigDTO['type']);
                    }}
                  >
                    {l10n.getString(
                      `board_type-${
                        BoardType[
                          firmwareToolToBoardType[
                            board as CreateBoardConfigDTO['type']
                          ] ?? BoardType.UNKNOWN
                        ]
                      }`
                    )}
                  </div>
                ))}
              </div>
              <div className="flex justify-end">
                <Localized id="firmware_tool-next_step">
                  <Button
                    variant="primary"
                    disabled={!newConfig?.boardConfig?.type}
                    onClick={() => {
                      if (defaultConfig?.shouldOnlyUseDefaults) {
                        goTo('SelectFirmware');
                      } else {
                        nextStep();
                      }
                    }}
                  ></Button>
                </Localized>
              </div>
            </div>
          )}
          {isFetching && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
              <Localized id="firmware_tool-loading">
                <Typography color="secondary"></Typography>
              </Localized>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
