import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  FirmwareToolContextC,
  provideFirmwareTool,
} from '@/hooks/firmware-tool';
import VerticalStepper, {
  VerticalStep,
} from '@/components/commons/VerticalStepper';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { Button } from '@/components/commons/Button';
import { useMemo } from 'react';
import {
  useGetHealth,
  useGetIsCompatibleVersion,
} from '@/firmware-tool-api/firmwareToolComponents';
import { SelectSourceSetep } from './steps/SelectSourceStep';
import { BoardDefaultsStep } from './steps/BoardDefaultsStep';
import { BuildStep } from './steps/BuildStep';
import { FlashingMethodStep } from './steps/FlashingMethodStep';
import { FirmwareUpdateMethod } from 'solarxr-protocol';
import { FlashBtnStep } from './steps/FlashBtnStep';
import { FlashingStep } from './steps/FlashingStep';

function FirmwareToolContent() {
  const { l10n } = useLocalization();
  const context = provideFirmwareTool();
  const { isError, isLoading: isInitialLoading, refetch } = useGetHealth({});
  const compatibilityCheckEnabled = !!__VERSION_TAG__;
  const { isLoading: isCompatibilityLoading, data: compatibilityData } =
    useGetIsCompatibleVersion(
      { pathParams: { version: __VERSION_TAG__ } },
      { enabled: compatibilityCheckEnabled }
    );

  const isLoading = isInitialLoading || isCompatibilityLoading;
  const isCompatible =
    !compatibilityCheckEnabled || (compatibilityData?.success ?? false);

  const steps = useMemo(() => {
    const steps: VerticalStep[] = [
      {
        id: 'SelectSource',
        component: SelectSourceSetep,
        title: l10n.getString('firmware_tool-select_source'),
      },
      {
        id: 'Defaults',
        component: BoardDefaultsStep,
        title: l10n.getString('firmware_tool-board_defaults'),
      },
      {
        id: 'Build',
        component: BuildStep,
        title: l10n.getString('firmware_tool-build_step'),
      },
      {
        id: 'FlashingMethod',
        component: FlashingMethodStep,
        title: l10n.getString('firmware_tool-flash_method_step'),
      },
      {
        component: FlashingStep,
        title: l10n.getString('firmware_tool-flashing_step'),
      },
    ];

    if (
      context.selectedDefault?.flashingRules.needBootPress &&
      context.selectedDevices?.find(
        ({ type }) => type === FirmwareUpdateMethod.SerialFirmwareUpdate
      )
    ) {
      steps.splice(4, 0, {
        component: FlashBtnStep,
        title: l10n.getString('firmware_tool-flashbtn_step'),
      });
    }
    return steps;
  }, [
    context.selectedDefault?.flashingRules.needBootPress,
    context.selectedDevices,
    l10n,
  ]);

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
                <Typography key={i}>{line}</Typography>
              ))}
          </>
        </div>
        <div className="m-4 h-full">
          {isError && (
            <div className="w-full flex flex-col justify-center items-center gap-3 h-full">
              <LoaderIcon slimeState={SlimeState.SAD} />
              {!isCompatible ? (
                <Localized id="firmware_tool-not_compatible">
                  <Typography variant="section-title" />
                </Localized>
              ) : (
                <Localized id="firmware_tool-not_available">
                  <Typography variant="section-title" />
                </Localized>
              )}
              <Localized id="firmware_tool-retry">
                <Button variant="primary" onClick={() => refetch()} />
              </Localized>
            </div>
          )}
          {isLoading && (
            <div className="w-full flex flex-col justify-center items-center gap-3 h-full">
              <LoaderIcon slimeState={SlimeState.JUMPY} />
              <Localized id="firmware_tool-loading">
                <Typography variant="section-title" />
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
