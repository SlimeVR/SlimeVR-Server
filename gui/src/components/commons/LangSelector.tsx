import { useLocalization } from '@fluent/react';
import { useEffect, useMemo, useContext } from 'react';
import { useForm } from 'react-hook-form';
import { useConfig } from '../../hooks/config';
import { langs, LangContext } from '../../i18n/config';
import { Dropdown, DropdownDirection } from './Dropdown';

export function LangSelector({
  direction = 'up',
  alignment = 'right',
}: {
  direction?: DropdownDirection;
  alignment?: 'right' | 'left';
}) {
  const { changeLocales } = useContext(LangContext);
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { control, watch, handleSubmit } = useForm<{ lang: string }>({
    defaultValues: { lang: config?.lang || 'en' },
  });

  const languagesItems = useMemo(
    () => langs.map(({ key, name }) => ({ label: name, value: key })),
    []
  );

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = (value: { lang: string }) => {
    changeLocales([value.lang]);
    setConfig({ lang: value.lang });
  };

  return (
    <Dropdown
      control={control}
      name="lang"
      placeholder={l10n.getString(
        'settings-general-interface-lang-placeholder'
      )}
      items={languagesItems}
      direction={direction}
      alignment={alignment}
    ></Dropdown>
  );
}
