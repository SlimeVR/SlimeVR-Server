import { UpdateManifestContext } from '@/App.js';
import { A } from '@/components/commons/A.js';
import { SteamIcon } from '@/components/commons/icon/SteamIcon';
import { Typography } from '@/components/commons/Typography';
import { UpdateChannelOptions } from '@/components/settings/pages/components/UpdateChannelOptions.js';
import { UpdateChannelVersionOptions } from '@/components/settings/pages/components/UpdateVersionOptions.js';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { useBreakpoint } from '@/hooks/breakpoint.js';
import { defaultConfig, useConfig } from '@/hooks/config';
import { useLocalization } from '@fluent/react';
import { UpdateManifest, type ChannelName } from '@slimevr/update-manifest';
import classNames from 'classnames';
import { ComponentProps, useContext, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import Markdown from 'react-markdown';
import remark from 'remark-gfm';

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

const MarkdownLink = (props: ComponentProps<'a'>) => (
  <A href={props.href}>{props.children}</A>
);

function ChangeVersion({ updateManifest }: { updateManifest: UpdateManifest }) {
  const { isMobile } = useBreakpoint('mobile');
  const { config } = useConfig();
  const [channel, setChannel] = useState<ChannelName>(
    config?.updateChannel ?? defaultConfig.updateChannel
  );
  const [version, setVersion] = useState(
    __VERSION_TAG__ in updateManifest.channels[channel].versions
      ? __VERSION_TAG__
      : ''
  );

  console.log(channel, version);

  const ch = updateManifest.channels[channel] ?? null;
  const v = ch?.versions[version] ?? null;

  return (
    <div className="flex gap-2">
      <div className="w-1/4 flex flex-col md:gap-4 sm:gap-2 xs:gap-1 mobile:gap-4">
        <UpdateChannelOptions
          manifest={updateManifest}
          value={channel}
          variant={isMobile ? 'dropdown' : 'radio'}
          onSelect={(channel) => {
            setChannel(channel);
            setVersion('');
          }}
        />
      </div>

      <div className="w-1/4 flex flex-col md:gap-4 sm:gap-2 xs:gap-1 mobile:gap-4">
        <UpdateChannelVersionOptions
          manifest={updateManifest}
          channel={channel}
          value={version}
          variant={isMobile ? 'dropdown' : 'radio'}
          onSelect={setVersion}
        />
      </div>

      <div className="w-1/2 flex">
        {v && (
          <div className="bg-background-60 rounded-lg px-3 py-2 max-h-[512px] overflow-auto">
            <Markdown
              remarkPlugins={[remark]}
              components={{ a: MarkdownLink }}
              className={classNames(
                'w-full text-sm prose-xl prose text-background-10 prose-h1:text-background-10',
                'prose-h2:text-background-10 prose-h3:text-background-10 prose-a:text-background-20 prose-strong:text-background-10',
                'prose-code:text-background-20'
              )}
            >
              {v.release_notes}
            </Markdown>
          </div>
        )}
      </div>
    </div>
  );
}
