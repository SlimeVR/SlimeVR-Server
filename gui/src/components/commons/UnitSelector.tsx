import { defaultConfig, UnitType, useConfig } from '@/hooks/config';
import { Dropdown, DropdownDirection } from './Dropdown';
import { useForm } from 'react-hook-form';
import { useEffect, useMemo } from 'react';
import { useLocalization } from '@fluent/react';

export function UnitSelector({
  direction = 'up',
  alignment = 'right',
}: {
  direction?: DropdownDirection;
  alignment?: 'right' | 'left';
}) {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { control, watch, handleSubmit } = useForm<{ unitSystem: UnitType }>({
    defaultValues: {
      unitSystem: config?.unitSystem ?? defaultConfig.unitSystem,
    },
  });

  const unitItems = useMemo(
    () =>
      Object.values(UnitType).map((type) => ({
        label: l10n.getString(
          `settings-interface-appearance-unit_system-${type}`
        ),
        value: type,
      })),
    [l10n]
  );

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = (value: { unitSystem: UnitType }) => {
    setConfig({ unitSystem: value.unitSystem });
  };

  return (
    <Dropdown
      control={control}
      name="unitSystem"
      placeholder={l10n.getString(
        'settings-interface-appearance-unit_system-placeholder'
      )}
      items={unitItems}
      direction={direction}
      alignment={alignment}
    ></Dropdown>
  );
}
