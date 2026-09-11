import { useLocalization } from '@fluent/react';
import { useEffect, useMemo, useContext } from 'react';
import { useForm } from 'react-hook-form';
import { useConfig } from '@/hooks/config';
import { LangContext } from '@/i18n/config';
import { langs } from '@/i18n/names';
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
    () =>
      langs.map(({ key, name, emoji }) => ({
        label: (
          <div>
            <img
              draggable="false"
              className="inline-block w-auto h-[1em] -translate-y-[0.05em]"
              src={emoji}
            />
            {' ' + name}
          </div>
        ),
        value: key,
      })),
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
    />
  );
}
