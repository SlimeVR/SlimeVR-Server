import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { useGetFirmwaresVersions } from '@/firmware-tool-api/firmwareToolComponents';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import classNames from 'classnames';
import { Button } from '@/components/commons/Button';
import { useMemo } from 'react';
import { CheckBox } from '@/components/commons/Checkbox';
import { useForm } from 'react-hook-form';

export function SelectFirmwareStep({
  nextStep,
  prevStep,
  goTo,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
}) {
  const { l10n } = useLocalization();
  const { selectVersion, newConfig, defaultConfig } = useFirmwareTool();
  const { isFetching, data: firmwares } = useGetFirmwaresVersions({});

  const { control, watch } = useForm<{ thirdParty: boolean }>({});

  const showThirdParty = watch('thirdParty');

  const getName = (name: string) => {
    return showThirdParty ? name : name.substring(name.indexOf('/') + 1);
  };

  const filteredFirmwares = useMemo(() => {
    return firmwares?.filter(
      ({ name }) => name.split('/')[0] === 'SlimeVR' || showThirdParty
    );
  }, [firmwares, showThirdParty]);

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex justify-between items-center mobile:flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware_tool-select_firmware_step-description')}
          </Typography>
          <div>
            <Localized
              id="firmware_tool-select_firmware_step-show-third-party"
              attrs={{ label: true }}
            >
              <CheckBox
                control={control}
                name="thirdParty"
                label="Show third party firmwares"
              ></CheckBox>
            </Localized>
          </div>
        </div>
        <div className="my-4">
          {!isFetching && (
            <div className="flex flex-col gap-4">
              <div className="xs-settings:max-h-96 xs-settings:overflow-y-auto xs-settings:px-2">
                <div className="grid sm:grid-cols-2 mobile-settings:grid-cols-1 gap-2">
                  {filteredFirmwares?.map((firmware) => (
                    <div
                      key={firmware.id}
                      className={classNames(
                        'p-3 rounded-md hover:bg-background-50',
                        {
                          'bg-background-50 text-background-10':
                            newConfig?.version === firmware.name,
                          'bg-background-60':
                            newConfig?.version !== firmware.name,
                        }
                      )}
                      onClick={() => {
                        selectVersion(firmware.name);
                      }}
                    >
                      {getName(firmware.name)}
                    </div>
                  ))}
                </div>
              </div>
              <div className="flex justify-between">
                <Localized id="firmware_tool-previous_step">
                  <Button
                    variant="tertiary"
                    onClick={() => {
                      if (defaultConfig?.shouldOnlyUseDefaults) {
                        goTo('SelectBoard');
                      } else {
                        prevStep();
                      }
                    }}
                  ></Button>
                </Localized>
                <Localized id="firmware_tool-next_step">
                  <Button
                    variant="primary"
                    disabled={!newConfig?.version}
                    onClick={nextStep}
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
