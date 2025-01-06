import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { fetchPostFirmwaresBuild } from '@/firmware-tool-api/firmwareToolComponents';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import {
  BuildResponseDTO,
  CreateBuildFirmwareDTO,
} from '@/firmware-tool-api/firmwareToolSchemas';
import { useEffect, useMemo } from 'react';
import { firmwareToolBaseUrl } from '@/firmware-tool-api/firmwareToolFetcher';
import { Button } from '@/components/commons/Button';

export function BuildStep({
  isActive,
  goTo,
  nextStep,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
  isActive: boolean;
}) {
  const { l10n } = useLocalization();
  const { isGlobalLoading, newConfig, setBuildStatus, buildStatus } =
    useFirmwareTool();

  const startBuild = async () => {
    try {
      const res = await fetchPostFirmwaresBuild({
        body: newConfig as CreateBuildFirmwareDTO,
      });

      setBuildStatus(res);
      if (res.status !== 'DONE') {
        const events = new EventSource(
          `${firmwareToolBaseUrl}/firmwares/build-status/${res.id}`
        );
        events.onmessage = ({ data }) => {
          const buildEvent: BuildResponseDTO = JSON.parse(data);
          setBuildStatus(buildEvent);
        };
      }
    } catch (e) {
      console.error(e);
      setBuildStatus({ id: '', status: 'ERROR' });
    }
  };

  useEffect(() => {
    if (!isActive) return;
    startBuild();
  }, [isActive]);

  useEffect(() => {
    if (!isActive) return;
    if (buildStatus.status === 'DONE') {
      nextStep();
    }
  }, [buildStatus]);

  const hasPendingBuild = useMemo(
    () => !['DONE', 'ERROR'].includes(buildStatus.status),
    [buildStatus.status]
  );

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware_tool-build_step-description')}
          </Typography>
        </div>
        <div className="my-4">
          {!isGlobalLoading && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon
                slimeState={
                  buildStatus.status !== 'ERROR'
                    ? SlimeState.JUMPY
                    : SlimeState.SAD
                }
              ></LoaderIcon>
              <Typography variant="section-title" color="secondary">
                {l10n.getString('firmware_tool-build-' + buildStatus.status)}
              </Typography>
            </div>
          )}
          {isGlobalLoading && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
              <Localized id="firmware_tool-loading">
                <Typography color="secondary"></Typography>
              </Localized>
            </div>
          )}
        </div>
        <div className="flex justify-end">
          <Localized id="firmware_tool-retry">
            <Button
              variant="secondary"
              disabled={hasPendingBuild}
              onClick={() => goTo('FlashingMethod')}
            ></Button>
          </Localized>
        </div>
      </div>
    </>
  );
}
