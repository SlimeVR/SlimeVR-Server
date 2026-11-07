import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import { useEffect, useMemo, useState } from 'react';
import { firmwareToolBaseUrl } from '@/firmware-tool-api/firmwareToolFetcher';
import { Button } from '@/components/commons/Button';
import { fetchPostFirmwareBuild } from '@/firmware-tool-api/firmwareToolComponents';
import {
  BuildStatusBasic,
  BuildStatusDone,
} from '@/firmware-tool-api/firmwareToolSchemas';

export function BuildStep({
  isActive,
  prevStep,
  goTo,
  nextStep,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
  isActive: boolean;
}) {
  const { l10n } = useLocalization();
  const { selectedSource, setFiles, selectedDefault } = useFirmwareTool();
  const [buildStatus, setBuildStatus] = useState<
    BuildStatusDone | BuildStatusBasic
  >({ status: 'QUEUED', id: '' });

  const startBuild = async () => {
    if (!selectedSource) throw 'invalid state - no source';

    try {
      const values =
        selectedSource.default?.data.defaults[selectedSource.source.board];
      if (!values) throw 'invalid state - no values';

      const res = await fetchPostFirmwareBuild({
        body: {
          ...selectedSource.source,
          values,
        },
      });

      setBuildStatus(res);
      if (res.status !== 'DONE') {
        const events = new EventSource(
          `${firmwareToolBaseUrl}/firmware/build-status/${res.id}`
        );
        events.onmessage = ({ data }) => {
          setBuildStatus(JSON.parse(data));
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
      setFiles(buildStatus.files);
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
          <Typography>
            {l10n.getString('firmware_tool-build_step-description')}
          </Typography>
        </div>
        <div className="my-4">
          <div className="flex justify-center flex-col items-center gap-3 h-44">
            <LoaderIcon
              slimeState={
                buildStatus.status !== 'ERROR'
                  ? SlimeState.JUMPY
                  : SlimeState.SAD
              }
            />
            <Typography variant="section-title">
              {l10n.getString('firmware_tool-build-' + buildStatus.status)}
            </Typography>
          </div>
        </div>
        <div className="flex justify-end gap-2">
          <Localized id="firmware_tool-previous_step">
            <Button
              variant="secondary"
              disabled={hasPendingBuild}
              onClick={() => {
                if (selectedDefault?.flashingRules.shouldOnlyUseDefaults) {
                  goTo('SelectSource');
                } else {
                  prevStep();
                }
              }}
            />
          </Localized>
          <Localized id="firmware_tool-retry">
            <Button
              variant="secondary"
              disabled={hasPendingBuild}
              onClick={() => startBuild()}
            />
          </Localized>
        </div>
      </div>
    </>
  );
}
