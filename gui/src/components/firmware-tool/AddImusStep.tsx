import { Localized, useLocalization } from '@fluent/react';
import { Typography } from '@/components/commons/Typography';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';
import { useFirmwareTool } from '@/hooks/firmware-tool';
import { Button } from '@/components/commons/Button';
import { Control, useForm } from 'react-hook-form';
import {
  CreateImuConfigDTO,
  Imudto,
} from '@/firmware-tool-api/firmwareToolSchemas';
import { Dropdown } from '@/components/commons/Dropdown';
import { TrashIcon } from '@/components/commons/icon/TrashIcon';
import { Input } from '@/components/commons/Input';
import {
  ArrowDownIcon,
  ArrowUpIcon,
} from '@/components/commons/icon/ArrowIcons';
import { useEffect, useRef, useState } from 'react';
import classNames from 'classnames';
import { useElemSize } from '@/hooks/layout';
import { useGetFirmwaresImus } from '@/firmware-tool-api/firmwareToolComponents';
import { CheckBox } from '@/components/commons/Checkbox';
import { Tooltip } from '@/components/commons/Tooltip';
import { A } from '@/components/commons/A';

function IMUCard({
  control,
  imuTypes,
  hasIntPin,
  index,
  onDelete,
}: {
  imuTypes: Imudto[];
  hasIntPin: boolean;
  control: Control<{ imus: CreateImuConfigDTO[] }, any>;
  index: number;
  onDelete: () => void;
}) {
  const { l10n } = useLocalization();
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement | null>(null);
  const { height } = useElemSize(ref);

  return (
    <div className="rounded-lg flex flex-col text-background-10">
      <div className="flex gap-3 p-4 shadow-md bg-background-50 rounded-md">
        <div className="bg-accent-background-40 rounded-full h-8 w-9 mt-[28px] flex flex-col items-center justify-center">
          <Typography variant="section-title" bold>
            {index + 1}
          </Typography>
        </div>
        <div className={'w-full flex flex-col gap-2'}>
          <div className="grid xs-settings:grid-cols-2 mobile-settings:grid-cols-1 gap-3 fill-background-10">
            <label className="flex flex-col justify-end gap-1">
              <Localized id="firmware_tool-add_imus_step-imu_type-label"></Localized>
              <Dropdown
                control={control}
                name={`imus[${index}].type`}
                items={imuTypes.map(({ type }) => ({
                  label: type.split('_').slice(1).join(' '),
                  value: type,
                }))}
                variant="secondary"
                maxHeight="25vh"
                placeholder={l10n.getString(
                  'firmware_tool-add_imus_step-imu_type-placeholder'
                )}
                direction="down"
                display="block"
              ></Dropdown>
            </label>
            <Input
              control={control}
              rules={{
                required: true,
              }}
              type="number"
              name={`imus[${index}].rotation`}
              variant="primary"
              label={
                <div>
                  <Tooltip
                    preferedDirection="bottom"
                    mode="corner"
                    content={l10n.getString(
                      'firmware_tool-add_imus_step-imu_rotation-tooltip'
                    )}
                  >
                    <div className="flex cursor-help group">
                      <A
                        href="https://docs.slimevr.dev/firmware/configuring-project.html#adjust-imu-board-rotation"
                        className="hover:underline"
                      >
                        {l10n.getString(
                          'firmware_tool-add_imus_step-imu_rotation-tooltip-label'
                        )}
                      </A>
                      <div className="group-hover:opacity-100 group-hover:underline opacity-65 ml-1 scale-[0.65] border-2 border-solid text-xs w-5 h-5 flex justify-center items-center rounded-full">
                        <A href="https://docs.slimevr.dev/firmware/configuring-project.html#adjust-imu-board-rotation">
                          i
                        </A>
                      </div>
                    </div>
                  </Tooltip>
                </div>
              }
              placeholder={l10n.getString(
                'firmware_tool-add_imus_step-imu_rotation-tooltip-placeholder'
              )}
              autocomplete="off"
            ></Input>
          </div>
          <div
            className="duration-500 transition-[height] overflow-hidden"
            style={{ height: open ? height : 0 }}
          >
            <div
              ref={ref}
              className="grid xs-settings:grid-cols-2 mobile-settings:grid-cols-1 gap-2"
            >
              <Localized
                id="firmware_tool-add_imus_step-scl_pin"
                attrs={{ label: true, placeholder: true }}
              >
                <Input
                  control={control}
                  rules={{ required: true }}
                  type="text"
                  name={`imus[${index}].sclPin`}
                  variant="primary"
                  autocomplete="off"
                ></Input>
              </Localized>
              <Localized
                id="firmware_tool-add_imus_step-sda_pin"
                attrs={{ label: true, placeholder: true }}
              >
                <Input
                  control={control}
                  rules={{ required: true }}
                  type="text"
                  name={`imus[${index}].sdaPin`}
                  variant="primary"
                  label="SDA Pin"
                  placeholder="SDA Pin"
                  autocomplete="off"
                ></Input>
              </Localized>

              {hasIntPin && (
                <Localized
                  id="firmware_tool-add_imus_step-int_pin"
                  attrs={{ label: true, placeholder: true }}
                >
                  <Input
                    control={control}
                    rules={{ required: true }}
                    type="text"
                    name={`imus[${index}].intPin`}
                    variant="primary"
                    autocomplete="off"
                  ></Input>
                </Localized>
              )}
              <label className="flex flex-col justify-end gap-1 md:pt-3 sm:pt-3">
                <Localized
                  id="firmware_tool-add_imus_step-optional_tracker"
                  attrs={{ label: true }}
                >
                  <CheckBox
                    control={control}
                    name={`imus[${index}].optional`}
                    variant="toggle"
                    color="tertiary"
                    label=""
                  ></CheckBox>
                </Localized>
              </label>
            </div>
          </div>
        </div>
        <div className="flex flex-col items-center mt-[25px] fill-background-10">
          <Button variant="quaternary" rounded onClick={onDelete}>
            <TrashIcon size={15}></TrashIcon>
          </Button>
        </div>
      </div>
      <div
        className="items-center flex justify-center hover:bg-background-60 bg-background-80 -mt-0.5 transition-colors duration-300  fill-background-10 rounded-b-lg pt-1 pb-0.5"
        onClick={() => setOpen(!open)}
      >
        <Typography>
          {l10n.getString(
            open
              ? 'firmware_tool-add_imus_step-show_less'
              : 'firmware_tool-add_imus_step-show_more'
          )}
        </Typography>
        {!open && <ArrowDownIcon></ArrowDownIcon>}
        {open && <ArrowUpIcon></ArrowUpIcon>}
      </div>
    </div>
  );
}

export function AddImusStep({
  nextStep,
  prevStep,
  isActive,
}: {
  nextStep: () => void;
  prevStep: () => void;
  goTo: (id: string) => void;
  isActive: boolean;
}) {
  const { l10n } = useLocalization();
  const {
    isStepLoading: isLoading,
    newConfig,
    defaultConfig,
    updateImus,
  } = useFirmwareTool();

  const {
    control,
    formState: { isValid: isValidState },
    reset,
    watch,
  } = useForm<{ imus: CreateImuConfigDTO[] }>({
    defaultValues: {
      imus: [],
    },
    reValidateMode: 'onChange',
    mode: 'onChange',
  });

  useEffect(() => {
    reset({
      imus: newConfig?.imusConfig || [],
    });
  }, [isActive]);

  const { isFetching, data: imuTypes } = useGetFirmwaresImus({});

  const isAckchuallyLoading = isFetching || isLoading;
  const form = watch();

  const addImu = () => {
    if (!newConfig || !defaultConfig) throw new Error('unreachable');

    const imuPinToAdd =
      defaultConfig.imuDefaults[form.imus.length ?? 0] ??
      defaultConfig.imuDefaults[0];
    const imuTypeToAdd: CreateImuConfigDTO['type'] =
      form.imus[0]?.type ?? 'IMU_BNO085';
    reset({
      imus: [...form.imus, { ...imuPinToAdd, type: imuTypeToAdd }],
    });
  };
  const deleteImu = (index: number) => {
    reset({ imus: form.imus.filter((_, i) => i !== index) });
  };

  return (
    <>
      <div className="flex flex-col w-full">
        <div className="flex flex-col gap-4">
          <Typography>
            {l10n.getString('firmware_tool-add_imus_step-description')}
          </Typography>
        </div>
        <div className="my-4 flex flex-col gap-4">
          {!isAckchuallyLoading && imuTypes && newConfig && (
            <>
              <div className="flex flex-col gap-3">
                <div
                  className={classNames(
                    'grid gap-2 px-2',
                    form.imus.length > 1
                      ? 'md:grid-cols-2 mobile-settings:grid-cols-1'
                      : 'grid-cols-1'
                  )}
                >
                  {form.imus.map((imu, index) => (
                    <IMUCard
                      control={control}
                      imuTypes={imuTypes}
                      key={`${index}:${imu.type}`}
                      hasIntPin={
                        imuTypes?.find(({ type: t }) => t == imu.type)
                          ?.hasIntPin ?? false
                      }
                      index={index}
                      onDelete={() => deleteImu(index)}
                    ></IMUCard>
                  ))}
                </div>
                <div className="flex justify-center">
                  <Localized id="firmware_tool-add_imus_step-add_more">
                    <Button variant="primary" onClick={addImu}></Button>
                  </Localized>
                </div>
              </div>
              <div className="flex justify-between">
                <Localized id="firmware_tool-previous_step">
                  <Button variant="tertiary" onClick={prevStep}></Button>
                </Localized>
                <Localized id="firmware_tool-next_step">
                  <Button
                    variant="primary"
                    disabled={!isValidState || form.imus.length === 0}
                    onClick={() => {
                      updateImus(form.imus);
                      nextStep();
                    }}
                  ></Button>
                </Localized>
              </div>
            </>
          )}
          {isAckchuallyLoading && (
            <div className="flex justify-center flex-col items-center gap-3 h-44">
              <LoaderIcon slimeState={SlimeState.JUMPY}></LoaderIcon>
              <Localized id="firmware_tool-loading">
                <Typography></Typography>
              </Localized>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
