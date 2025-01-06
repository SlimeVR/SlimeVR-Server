import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  FirmwareToolContextC,
  useFirmwareToolContext,
} from '@/hooks/firmware-tool';
import { AddImusStep } from './AddImusStep';
import { SelectBoardStep } from './SelectBoardStep';
import { BoardPinsStep } from './BoardPinsStep';
import VerticalStepper from '@/components/commons/VerticalStepper';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { Button } from '@/components/commons/Button';
import { SelectFirmwareStep } from './SelectFirmwareStep';
import { BuildStep } from './BuildStep';
import { FlashingMethodStep } from './FlashingMethodStep';
import { FlashingStep } from './FlashingStep';
import { FlashBtnStep } from './FlashBtnStep';
import { FirmwareUpdateMethod } from 'solarxr-protocol';
import { useMemo } from 'react';

function FirmwareToolContent() {
  const { l10n } = useLocalization();
  const context = useFirmwareToolContext();
  const { isError, isGlobalLoading: isLoading, retry, isCompatible } = context;

  const steps = useMemo(() => {
    const steps = [
      {
        id: 'SelectBoard',
        component: SelectBoardStep,
        title: l10n.getString('firmware_tool-board_step'),
      },
      {
        component: BoardPinsStep,
        title: l10n.getString('firmware_tool-board_pins_step'),
      },
      {
        component: AddImusStep,
        title: l10n.getString('firmware_tool-add_imus_step'),
      },
      {
        id: 'SelectFirmware',
        component: SelectFirmwareStep,
        title: l10n.getString('firmware_tool-select_firmware_step'),
      },
      {
        component: FlashingMethodStep,
        id: 'FlashingMethod',
        title: l10n.getString('firmware_tool-flash_method_step'),
      },
      {
        component: BuildStep,
        title: l10n.getString('firmware_tool-build_step'),
      },
      {
        component: FlashingStep,
        title: l10n.getString('firmware_tool-flashing_step'),
      },
    ];

    if (
      context.defaultConfig?.needBootPress &&
      context.selectedDevices?.find(
        ({ type }) => type === FirmwareUpdateMethod.SerialFirmwareUpdate
      )
    ) {
      steps.splice(5, 0, {
        component: FlashBtnStep,
        title: l10n.getString('firmware_tool-flashbtn_step'),
      });
    }
    return steps;
  }, [context.defaultConfig?.needBootPress, context.selectedDevices, l10n]);

  return (
    <FirmwareToolContextC.Provider value={context}>
      <div className="flex flex-col bg-background-70 p-4 rounded-md">
        <Typography variant="main-title">
          {l10n.getString('firmware_tool')}
        </Typography>
        <div className="flex flex-col pt-2 pb-4">
          <>
            {l10n
              .getString('firmware_tool-description')
              .split('\n')
              .map((line, i) => (
                <Typography color="secondary" key={i}>
                  {line}
                </Typography>
              ))}
          </>
        </div>
        <div className="m-4 h-full">
          {isError && (
            <div className="w-full flex flex-col justify-center items-center gap-3 h-full">
              <LoaderIcon slimeState={SlimeState.SAD}></LoaderIcon>
              {!isCompatible ? (
                <Localized id="firmware_tool-not_compatible">
                  <Typography variant="section-title"></Typography>
                </Localized>
              ) : (
                <Localized id="firmware_tool-not_available">
                  <Typography variant="section-title"></Typography>
                </Localized>
              )}
              <Localized id="firmware_tool-retry">
                <Button variant="primary" onClick={retry}></Button>
              </Localized>
            </div>
          )}
          {isLoading && (
            <div className="w-full flex flex-col justify-center items-center gap-3 h-full">
              <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
              <Localized id="firmware_tool-loading">
                <Typography variant="section-title"></Typography>
              </Localized>
            </div>
          )}
          {!isError && !isLoading && <VerticalStepper steps={steps} />}
        </div>
      </div>
    </FirmwareToolContextC.Provider>
  );
}

export function FirmwareToolSettings() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        refetchOnWindowFocus: false, // default: true
      },
    },
  });
  return (
    <QueryClientProvider client={queryClient}>
      <FirmwareToolContent />
    </QueryClientProvider>
  );
}
