import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { fetchPostFirmwaresBuild } from '@/firmware-tool-api/firmwareToolComponents';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import {
  BuildResponseDTO,
  CreateBuildFirmwareDTO,
} from '@/firmware-tool-api/firmwareToolSchemas';
import { useEffect } from 'react';
import { firmwareToolBaseUrl } from '@/firmware-tool-api/firmwareToolFetcher';

export function BuildStep({
  isActive,
  nextStep,
}: {
  nextStep: () => void;
  prevStep: () => void;
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
      setBuildStatus({ id: '', status: 'ERROR' });
    }
  };

  useEffect(() => {
    if (!isActive) return;
    startBuild();
  }, [isActive]);

  useEffect(() => {
    if (buildStatus.status === 'DONE') {
      nextStep();
    }
  }, [buildStatus]);

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware-tool-build-step-description')}
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
                {l10n.getString('firmware-tool-build-' + buildStatus.status)}
              </Typography>
            </div>
          )}
          {isGlobalLoading && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
              <Localized id="firmware-tool-loading">
                <Typography color="secondary"></Typography>
              </Localized>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
