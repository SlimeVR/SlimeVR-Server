import { useEffect, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { useConfig } from '../../hooks/config';
import i18next, { langs } from '../../i18n/config';
import { Dropdown, DropdownDirection } from './Dropdown';

export function LangSelector({
  direction = 'up',
}: {
  direction?: DropdownDirection;
}) {
  const { t } = useTranslation();
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
    i18next.changeLanguage(value.lang);
    setConfig({ lang: value.lang });
  };

  return (
    <Dropdown
      control={control}
      name="lang"
      placeholder={t('settings.interface.lang.placeholder')}
      items={languagesItems}
      direction={direction}
    ></Dropdown>
  );
}
