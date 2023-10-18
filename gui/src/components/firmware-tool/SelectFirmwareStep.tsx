import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { useGetFirmwaresVersions } from '@/firmware-tool-api/firmwareToolComponents';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import classNames from 'classnames';
import { Button } from '@/components/commons/Button';

export function SelectFirmwareStep({
  nextStep,
  prevStep,
}: {
  nextStep: () => void;
  prevStep: () => void;
}) {
  const { l10n } = useLocalization();
  const { selectVersion, newConfig } = useFirmwareTool();
  const { isFetching, data: firmwares } = useGetFirmwaresVersions({});

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-grow flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware-tool-select-firmware-step-description')}
          </Typography>
        </div>
        <div className="my-4">
          {!isFetching && (
            <div className="flex flex-col gap-4">
              <div className="grid sm:grid-cols-2 mobile-settings:grid-cols-1 gap-2 xs-settings:h-96 xs-settings:overflow-y-auto xs-settings:px-2">
                {firmwares?.map((firmwares) => (
                  <div
                    key={firmwares.id}
                    className={classNames(
                      'p-3 rounded-md hover:bg-background-50',
                      {
                        'bg-background-50':
                          newConfig?.version === firmwares.name,
                        'bg-background-60':
                          newConfig?.version !== firmwares.name,
                      }
                    )}
                    onClick={() => {
                      selectVersion(firmwares.name);
                    }}
                  >
                    {firmwares.name}
                  </div>
                ))}
              </div>
              <div className="flex justify-between">
                <Localized id="firmware-tool-previous-step">
                  <Button variant="tertiary" onClick={prevStep}></Button>
                </Localized>
                <Localized id="firmware-tool-next-step">
                  <Button
                    variant="primary"
                    disabled={!newConfig?.version}
                    onClick={() => {
                      nextStep();
                    }}
                  ></Button>
                </Localized>
              </div>
            </div>
          )}
          {isFetching && (
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
