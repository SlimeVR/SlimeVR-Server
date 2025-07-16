import { UpdateManifestContext } from '@/App.js';
import { SteamIcon } from '@/components/commons/icon/SteamIcon';
import { Typography } from '@/components/commons/Typography';
import { UpdateChannelOptions } from '@/components/settings/pages/components/UpdateChannelOptions.js';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { useBreakpoint } from '@/hooks/breakpoint.js';
import { defaultConfig, useConfig } from '@/hooks/config';
import { useLocalization } from '@fluent/react';
import { UpdateManifest, type ChannelName } from '@slimevr/update-manifest';
import { useContext, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

export type SettingsForm = {
  channel: ChannelName;
  autoUpdate: boolean;
};

export function UpdateSettings() {
  const { l10n } = useLocalization();
  const { config, setConfig } = useConfig();
  const { isMobile } = useBreakpoint('mobile');

  const updateManifest = useContext(UpdateManifestContext);

  const { reset, control, watch, handleSubmit, getValues, setValue } =
    useForm<SettingsForm>({
      defaultValues: {
        channel: config?.updateChannel ?? defaultConfig.updateChannel,
        autoUpdate: config?.autoUpdate ?? defaultConfig.autoUpdate,
      },
    });

  const { channel } = watch();

  const onSubmit = (values: SettingsForm) => {
    setConfig({
      updateChannel: values.channel,
      autoUpdate: values.autoUpdate,
    });
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  if (!updateManifest) {
    return null;
  }

  console.log(updateManifest);

  return (
    <SettingsPageLayout>
      <form className="flex flex-col gap-2 w-full">
        <SettingsPagePaneLayout icon={<SteamIcon />} id="channels">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-update')}
            </Typography>
            <Typography bold>
              {l10n.getString('settings-general-update-subtitle')}
            </Typography>
            <div className="flex flex-col py-2">
              {l10n
                .getString('settings-general-update-description')
                .split('\n')
                .map((line, i) => (
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
                ))}
            </div>

            <div className="flex flex-col md:gap-4 sm:gap-2 xs:gap-1 mobile:gap-4">
              <UpdateChannelOptions
                manifest={updateManifest}
                value={channel}
                variant={isMobile ? 'dropdown' : 'radio'}
                onSelect={(channel) => setValue('channel', channel)}
              />
            </div>
          </>
        </SettingsPagePaneLayout>

        <SettingsPagePaneLayout icon={<SteamIcon />} id="change_version">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-change_version')}
            </Typography>
            <Typography bold>
              {l10n.getString('settings-general-change_version-subtitle')}
            </Typography>
            <div className="flex flex-col py-2">
              {l10n
                .getString('settings-general-change_version-description')
                .split('\n')
                .map((line, i) => (
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
                ))}
            </div>

            <ChangeVersion updateManifest={updateManifest} />
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}

function ChangeVersion({ updateManifest }: { updateManifest: UpdateManifest }) {
  const { isMobile } = useBreakpoint('mobile');
  const { config } = useConfig();
  const [channel, setChannel] = useState<ChannelName>(
    config?.updateChannel ?? defaultConfig.updateChannel
  );

  return (
    <div className="flex gap-2">
      <div className="w-1/4 flex flex-col md:gap-4 sm:gap-2 xs:gap-1 mobile:gap-4">
        <UpdateChannelOptions
          manifest={updateManifest}
          value={channel}
          variant={isMobile ? 'dropdown' : 'radio'}
          onSelect={setChannel}
        />
      </div>

      <div className="w-1/4 flex flex-col md:gap-4 sm:gap-2 xs:gap-1 mobile:gap-4">
        {/* <UpdateChannelOptions
                  variant={isMobile ? 'dropdown' : 'radio'}
                /> */}
        Version
      </div>

      <div className="w-1/2 flex flex-col md:gap-4 sm:gap-2 xs:gap-1 mobile:gap-4">
        Changelog
      </div>
    </div>
  );
}
