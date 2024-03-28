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

function FirmwareToolContent() {
  const { l10n } = useLocalization();
  const context = useFirmwareToolContext();
  const { isError, isGlobalLoading: isLoading, retry } = context;

  return (
    <FirmwareToolContextC.Provider value={context}>
      <div className="flex flex-col bg-background-70 p-4 rounded-md overflow-y-auto h-full">
        <Typography variant="main-title">
          {l10n.getString('firmware-tool')}
        </Typography>
        <div className="flex flex-col pt-2 pb-4">
          <>
            {l10n
              .getString('firmware-tool-description')
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
            <div className="w-full flex flex-col justify-center items-center gap-3 h-full ">
              <LoaderIcon slimeState={SlimeState.SAD}></LoaderIcon>
              <Localized id="firmware-tool-not-available">
                <Typography variant="section-title"></Typography>
              </Localized>
              <Localized id="firmware-tool-retry">
                <Button variant="primary" onClick={retry}></Button>
              </Localized>
            </div>
          )}
          {isLoading && (
            <div className="w-full flex flex-col justify-center items-center gap-3 h-full ">
              <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
              <Localized id="firmware-tool-loading">
                <Typography variant="section-title"></Typography>
              </Localized>
            </div>
          )}
          {!isError && !isLoading && (
            <VerticalStepper
              steps={[
                {
                  component: SelectBoardStep,
                  title: l10n.getString('firmware-tool-board-step'),
                },
                {
                  component: BoardPinsStep,
                  title: l10n.getString('firmware-tool-board-pins-step'),
                },
                {
                  component: AddImusStep,
                  title: l10n.getString('firmware-tool-add-imus-step'),
                },
                {
                  component: SelectFirmwareStep,
                  title: l10n.getString('firmware-tool-select-firmware-step'),
                },
                {
                  component: FlashingMethodStep,
                  id: 'FlashingMethod',
                  title: l10n.getString('firmware-tool-flash-method-step'),
                },
                {
                  component: BuildStep,
                  title: l10n.getString('firmware-tool-build-step'),
                },
                {
                  component: FlashingStep,
                  title: l10n.getString('firmware-tool-flashing-step'),
                },
              ]}
            />
          )}
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
