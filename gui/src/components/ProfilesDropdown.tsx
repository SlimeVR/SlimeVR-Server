import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useConfig } from '@/hooks/config';
import { useWebsocketAPI as _ } from '@/hooks/websocket-api';
import { useLocalization } from '@fluent/react';
import { Dropdown } from './commons/Dropdown';
import { log } from '@/utils/logging';

export function ProfilesDropdown({
  paddingX,
  paddingY,
  minHeight,
}: {
  // Used by the profile quick access in topbar, may be removed
  paddingX?: number;
  paddingY?: number;
  minHeight?: string | number;
} & React.HTMLAttributes<HTMLDivElement>) {
  const { l10n } = useLocalization();
  const { config, changeProfile } = useConfig();

  const { control, watch, handleSubmit } = useForm<{ profile: string }>({
    defaultValues: { profile: config?.profile || 'default' },
  });

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = (values: { profile: string }) => {
    log(`Setting profile to ${values.profile}`);
    changeProfile(values.profile);
  };

  return (
    <Dropdown
      control={control}
      name="profile"
      display="block"
      placeholder={l10n.getString('settings-utils-profiles-default')}
      direction="down"
      minHeight={minHeight}
      paddingX={paddingX}
      paddingY={paddingY}
      items={[
        {
          label: l10n.getString('settings-utils-profiles-default'),
          value: 'default',
        },
        { label: 'Lexend', value: 'Lexend' },
        { label: 'Ubuntu', value: 'Ubuntu' },
      ]}
    ></Dropdown>
  );
}
