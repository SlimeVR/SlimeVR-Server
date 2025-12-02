import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useConfig } from '@/hooks/config';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { CheckBox } from '@/components/commons/Checkbox';
import { useLocalization } from '@fluent/react';

export interface DeveloperModeWidgetForm {
  highContrast: boolean;
  preciseRotation: boolean;
  fastDataFeed: boolean;
  filterSlimesAndHMD: boolean;
  sortByName: boolean;
  rawSlimeRotation: boolean;
  moreInfo: boolean;
}

export const defaultValues: DeveloperModeWidgetForm = {
  highContrast: false,
  preciseRotation: false,
  fastDataFeed: false,
  filterSlimesAndHMD: false,
  sortByName: false,
  rawSlimeRotation: false,
  moreInfo: false,
};

export function DeveloperModeWidget() {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { reconnect } = useWebsocketAPI();

  const { reset, control, handleSubmit, watch } =
    useForm<DeveloperModeWidgetForm>({
      defaultValues: defaultValues,
    });

  useEffect(() => {
    reset(config?.devSettings || {});
  }, []);

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = async (formData: DeveloperModeWidgetForm) => {
    const needReconnect =
      config?.devSettings?.fastDataFeed !== formData.fastDataFeed;
    await setConfig({ devSettings: formData });
    if (needReconnect) reconnect();
  };

  const makeToggle = ([name, label]: string[], index: number) => (
    <CheckBox
      key={index}
      control={control}
      variant="toggle"
      outlined
      name={name}
      label={l10n.getString(`widget-developer_mode-${label}`)}
    />
  );

  const toggles = {
    highContrast: 'high_contrast',
    preciseRotation: 'precise_rotation',
    fastDataFeed: 'fast_data_feed',
    filterSlimesAndHMD: 'filter_slimes_and_hmd',
    sortByName: 'sort_by_name',
    rawSlimeRotation: 'raw_slime_rotation',
    moreInfo: 'more_info',
  };

  return (
    <form className="grid grid-cols-2 w-full rounded-md gap-2">
      {Object.entries(toggles).map(makeToggle)}
    </form>
  );
}
