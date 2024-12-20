import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import { Button } from '@/components/commons/Button';
import { useForm } from 'react-hook-form';
import { Input } from '@/components/commons/Input';
import { useEffect } from 'react';
import { CheckBox } from '@/components/commons/Checkbox';
import { CreateBoardConfigDTO } from '@/firmware-tool-api/firmwareToolSchemas';
import { Dropdown } from '@/components/commons/Dropdown';
import classNames from 'classnames';
import { useGetFirmwaresBatteries } from '@/firmware-tool-api/firmwareToolComponents';

export type BoardPinsForm = Omit<CreateBoardConfigDTO, 'type'>;

export function BoardPinsStep({
  nextStep,
  prevStep,
}: {
  nextStep: () => void;
  prevStep: () => void;
}) {
  const { l10n } = useLocalization();
  const {
    isStepLoading: isLoading,
    defaultConfig,
    updatePins,
  } = useFirmwareTool();
  const { isFetching, data: batteryTypes } = useGetFirmwaresBatteries({});

  const { reset, control, watch, formState } = useForm<BoardPinsForm>({
    reValidateMode: 'onChange',
    defaultValues: {
      batteryResistances: [0, 0, 0],
    },
    mode: 'onChange',
  });

  const formValue = watch();
  const ledEnabled = watch('enableLed');
  const batteryType = watch('batteryType');

  useEffect(() => {
    if (!defaultConfig) return;
    const { type, ...resetConfig } = defaultConfig.boardConfig;
    reset({
      ...resetConfig,
    });
  }, [defaultConfig]);

  return (
    <>
      <div className="flex flex-col w-full justify-between text-background-10">
        <div className="flex flex-col gap-4">
          <Typography color="secondary">
            {l10n.getString('firmware_tool-board_pins_step-description')}
          </Typography>
        </div>
        <div className="my-4 p-2">
          {!isLoading && !isFetching && batteryTypes && (
            <form className="flex flex-col gap-2">
              <div className="grid xs-settings:grid-cols-2 mobile-settings:grid-cols-1 gap-2">
                <label className="flex flex-col justify-end">
                  {/* Allows to have the right spacing at the top of the checkbox */}
                  <CheckBox
                    control={control}
                    color="tertiary"
                    name="enableLed"
                    variant="toggle"
                    outlined
                    label={l10n.getString(
                      'firmware_tool-board_pins_step-enable_led'
                    )}
                  ></CheckBox>
                </label>
                <Localized
                  id="firmware_tool-board_pins_step-led_pin"
                  attrs={{ placeholder: true, label: true }}
                >
                  <Input
                    control={control}
                    rules={{ required: true }}
                    type="text"
                    name="ledPin"
                    variant="secondary"
                    disabled={!ledEnabled}
                  ></Input>
                </Localized>
              </div>
              <div
                className={classNames(
                  batteryType === 'BAT_EXTERNAL' &&
                    'bg-background-80 p-2 rounded-md',
                  'transition-all duration-500 flex-col flex gap-2'
                )}
              >
                <Dropdown
                  control={control}
                  name="batteryType"
                  variant="primary"
                  placeholder={l10n.getString(
                    'firmware_tool-board_pins_step-battery_type'
                  )}
                  direction="up"
                  display="block"
                  items={batteryTypes.map((battery) => ({
                    label: l10n.getString(
                      'firmware_tool-board_pins_step-battery_type-' + battery
                    ),
                    value: battery,
                  }))}
                ></Dropdown>
                {batteryType === 'BAT_EXTERNAL' && (
                  <div className="grid grid-cols-2 gap-2">
                    <Localized
                      id="firmware_tool-board_pins_step-battery_sensor_pin"
                      attrs={{ placeholder: true, label: true }}
                    >
                      <Input
                        control={control}
                        rules={{ required: true }}
                        type="text"
                        name="batteryPin"
                        variant="secondary"
                      ></Input>
                    </Localized>
                    <Localized
                      id="firmware_tool-board_pins_step-battery_resistor"
                      attrs={{ placeholder: true, label: true }}
                    >
                      <Input
                        control={control}
                        rules={{ required: true, min: 0 }}
                        type="number"
                        name="batteryResistances[0]"
                        variant="secondary"
                        label="Battery Resistor"
                        placeholder="Battery Resistor"
                      ></Input>
                    </Localized>
                    <Localized
                      id="firmware_tool-board_pins_step-battery_shield_resistor-0"
                      attrs={{ placeholder: true, label: true }}
                    >
                      <Input
                        control={control}
                        rules={{ required: true, min: 0 }}
                        type="number"
                        name="batteryResistances[1]"
                        variant="secondary"
                      ></Input>
                    </Localized>
                    <Localized
                      id="firmware_tool-board_pins_step-battery_shield_resistor-1"
                      attrs={{ placeholder: true, label: true }}
                    >
                      <Input
                        control={control}
                        rules={{ required: true, min: 0 }}
                        type="number"
                        name="batteryResistances[2]"
                        variant="secondary"
                      ></Input>
                    </Localized>
                  </div>
                )}
              </div>
            </form>
          )}
          {(isLoading || isFetching) && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
              <Localized id="firmware_tool-loading">
                <Typography color="secondary"></Typography>
              </Localized>
            </div>
          )}
        </div>
        <div className="flex justify-between">
          <Localized id="firmware_tool-previous_step">
            <Button variant="tertiary" onClick={prevStep}></Button>
          </Localized>
          <Localized id="firmware_tool-ok">
            <Button
              variant="primary"
              disabled={Object.keys(formState.errors).length !== 0}
              onClick={() => {
                updatePins(formValue);
                nextStep();
              }}
            ></Button>
          </Localized>
        </div>
      </div>
    </>
  );
}
