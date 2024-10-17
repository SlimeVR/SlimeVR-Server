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
import { DeleteProfileModal } from '../DeleteProfileModal';
import { Input } from '@/components/commons/Input';
import { useForm } from 'react-hook-form';
import { Dropdown } from '@/components/commons/Dropdown';

export function ProfileSettings() {
  const { l10n } = useLocalization();
  const { config, setConfig, changeProfile, getProfiles } = useConfig();
  const [showWarning, setShowWarning] = useState(false);

  // is there a better way to do this?
  const { control: nameControl, handleSubmit: handleNameSubmit } = useForm<{
    newName: string;
  }>({
    defaultValues: { newName: '' },
  });

  const {
    control: profileControl,
    watch: watchProfileSubmit,
    handleSubmit: handleProfileSubmit,
  } = useForm<{
    profile: string;
  }>({
    defaultValues: { profile: config?.profile },
  });

  const {
    control: deleteControl,
    handleSubmit: handleDeleteControl,
  } = useForm<{
    profile: string;
  }>({
    defaultValues: { profile: config?.profile || 'default' },
  });

  useEffect(() => {
    const subscription = watchProfileSubmit(() =>
      handleProfileSubmit(onSelectSubmit)()
    );
    return () => subscription.unsubscribe();
  }, []);

  const onNameSubmit = async (data: { newName: string }) => {
    if (!data.newName || data.newName === '' || data.newName === 'default')
      return;

    const profiles = await getProfiles();
    if (profiles.includes(data.newName)) {
      error(`Profile with name ${data.newName} already exists`);
      return;
    }

    log(`Creating new profile with name ${data.newName}`);
    setConfig({
      profile: data.newName,
    });
    changeProfile(data.newName);
  };

  const onSelectSubmit = (data: { profile: string }) => {
    log(`Switching to profile ${data.profile}`);
    changeProfile(data.profile);
  };

  const onDeleteSelectSubmit = (data: { profile: string }) => {
    if (data.profile === 'default') {
      error('Cannot delete default profile');
      return;
    }
    log(`Deleting profile ${data.profile}`);
  };

  return (
    <SettingsPageLayout>
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
                {l10n.getString('settings-utils-profiles-profile-description')}
              </Typography>
            </div>
              <div className="grid sm:grid-cols-1 pb-4">
                <Dropdown
                  control={profileControl}
                  name="profile"
                  display="block"
                  placeholder={l10n.getString(
                    'settings-utils-profiles-default'
                  )}
                  direction="down"
                  items={[
                    {
                      label: l10n.getString('settings-utils-profiles-default'),
                      value: 'default',
                    },
                    { label: 'Lexend', value: 'Lexend' },
                    { label: 'Ubuntu', value: 'Ubuntu' },
                  ]}
                ></Dropdown>
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
              <form onSubmit={handleNameSubmit(onNameSubmit)}>
                <div className="flex gap-2 mobile:flex-col">
                  <div style={{ flexBasis: '65%' }}>
                    <Input
                      control={nameControl}
                      rules={{ required: true }}
                      name="newName"
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
              </form>
            </div>

            <div>
              <Typography bold>
                {l10n.getString('settings-utils-profiles-delete')}
              </Typography>
              <div className="flex flex-col pt-1 pb-2">
                <Typography color="secondary">
                  {l10n.getString('settings-utils-profiles-delete-description')}
                </Typography>
              </div>
              <div className="flex gap-2 mobile:flex-col">
                <div style={{ flexBasis: '65%' }}>
                  <Dropdown
                    control={deleteControl}
                    name="profile"
                    display="block"
                    placeholder={l10n.getString(
                      'settings-utils-profiles-default'
                    )}
                    direction="down"
                    items={[
                      {
                        label: l10n.getString(
                          'settings-utils-profiles-default'
                        ),
                        value: 'default',
                      },
                      { label: 'Lexend', value: 'Lexend' },
                      { label: 'Ubuntu', value: 'Ubuntu' },
                    ]}
                  ></Dropdown>
                </div>
                <Button
                  variant="secondary"
                  onClick={handleDeleteControl(onDeleteSelectSubmit)}
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
    </SettingsPageLayout>
  );
}
