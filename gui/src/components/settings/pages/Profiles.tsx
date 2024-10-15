import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import { Button } from '@/components/commons/Button';

import { error, log } from '@/utils/logging';
import { defaultConfig as defaultGUIConfig, useConfig } from '@/hooks/config';
import {
  defaultValues as defaultDevConfig,
  defaultValues,
} from '@/components/widgets/DeveloperModeWidget';
import { ProfilesDropdown } from '@/components/ProfilesDropdown';
import { DeleteProfileModal } from '../DeleteProfileModal';
import { Input } from '@/components/commons/Input';
import { useForm } from 'react-hook-form';

interface NewProfileForm {
  name: string;
}

export function ProfileSettings() {
  const { l10n } = useLocalization();
  const { setConfig } = useConfig();

  const [showWarning, setShowWarning] = useState(false);

  const { control, handleSubmit } = useForm<NewProfileForm>({
    defaultValues: { name: '' },
  });

  const onSubmit = (data: NewProfileForm) => {
    log('Profile name: ' + data.name);

    setConfig({
      profile: data.name,
    });
  };

  return (
    <SettingsPageLayout>
      <form
        className="flex flex-col gap-2 w-full"
        onSubmit={handleSubmit(onSubmit)}
      >
        <SettingsPagePaneLayout icon={<WrenchIcon />} id="profiles">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-utils-profiles')}
            </Typography>
            <div className="flex flex-col pt-2 pb-4">
              <>
                {l10n
                  .getString('settings-utils-profiles-description')
                  .split('\n')
                  .map((line, i) => (
                    <Typography color="secondary" key={i}>
                      {line}
                    </Typography>
                  ))}
              </>
            </div>

            <div>
              <Typography bold>
                {l10n.getString('settings-utils-profiles-profile')}
              </Typography>
              <div className="flex flex-col pt-1 pb-2">
                <Typography color="secondary">
                  {l10n.getString(
                    'settings-utils-profiles-profile-description'
                  )}
                </Typography>
              </div>
              <div className="grid sm:grid-cols-1 pb-4">
                <ProfilesDropdown></ProfilesDropdown>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2 mobile:grid-cols-1">
              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-profiles-new')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString('settings-utils-profiles-new-description')}
                  </Typography>
                </div>
                <div className="flex gap-2 mobile:flex-col">
                  <div style={{ flexBasis: '65%' }}>
                    <Input
                      control={control}
                      rules={{ required: true }}
                      name="name"
                      type="text"
                      placeholder="Enter name"
                      variant="secondary"
                      className="flex-grow"
                    />
                  </div>

                  <Button
                    variant="secondary"
                    className="flex-grow"
                    style={{ flexBasis: '35%' }}
                    type="submit"
                  >
                    {l10n.getString('settings-utils-profiles-new-label')}
                  </Button>
                </div>
              </div>

              <div>
                <Typography bold>
                  {l10n.getString('settings-utils-profiles-delete')}
                </Typography>
                <div className="flex flex-col pt-1 pb-2">
                  <Typography color="secondary">
                    {l10n.getString(
                      'settings-utils-profiles-delete-description'
                    )}
                  </Typography>
                </div>
                <div className="flex gap-2 mobile:flex-col">
                  <div style={{ flexBasis: '65%' }}>
                    <ProfilesDropdown></ProfilesDropdown>
                  </div>
                  <Button
                    variant="secondary"
                    onClick={() => setShowWarning(true)}
                    style={{ flexBasis: '35%' }}
                  >
                    {l10n.getString('settings-utils-profiles-delete-label')}
                  </Button>
                  <DeleteProfileModal
                    accept={() => {
                      log('Deleting profile');
                      // TODO: actually delete profile
                      setShowWarning(false);
                    }}
                    onClose={() => setShowWarning(false)}
                    isOpen={showWarning}
                    profile="default"
                  ></DeleteProfileModal>
                </div>
              </div>
            </div>
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}
