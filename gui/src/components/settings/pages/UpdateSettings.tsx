import { Button } from '@/components/commons/Button.js';
import { SteamIcon } from '@/components/commons/icon/SteamIcon';
import { Input } from '@/components/commons/Input.js';
import { MarkdownLink } from '@/components/commons/MarkdownLink.js';
import { Typography } from '@/components/commons/Typography';
import { UpdateChannelOptions } from '@/components/settings/pages/components/UpdateChannelOptions.js';
import { UpdateChannelVersionOptions } from '@/components/settings/pages/components/UpdateVersionOptions.js';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { useBreakpoint } from '@/hooks/breakpoint.js';
import { defaultConfig, useConfig } from '@/hooks/config';
import { useUpdateContext } from '@/hooks/update.js';
import { useLocalization } from '@fluent/react';
import { type ChannelName, type Version } from '@slimevr/update-manifest';
import classNames from 'classnames';
import { useCallback, useEffect, useState } from 'react';
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

  const { manifest: updateManifest } = useUpdateContext();

  const { reset, control, watch, handleSubmit, getValues, setValue } =
    useForm<SettingsForm>({
      defaultValues: {
        channel: config?.updateChannel ?? defaultConfig.updateChannel,
        autoUpdate:
          config?.notifyOnAvailableUpdates ??
          defaultConfig.notifyOnAvailableUpdates,
      },
    });

  const { channel } = watch();

  const onSubmit = (values: SettingsForm) => {
    setConfig({
      updateChannel: values.channel,
      notifyOnAvailableUpdates: values.autoUpdate,
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
                asList={false}
              />
            </div>
          </>
        </SettingsPagePaneLayout>

        <SettingsPagePaneLayout icon={<SteamIcon />} id="change_system">
          <>
            <Typography variant="main-title">
              {l10n.getString('settings-general-change_system')}
            </Typography>
            <Typography bold>
              {l10n.getString('settings-general-change_system-subtitle')}
            </Typography>
            <div className="flex flex-col py-2">
              {l10n
                .getString('settings-general-change_system-description')
                .split('\n')
                .map((line, i) => (
                  <Typography color="secondary" key={i}>
                    {line}
                  </Typography>
                ))}
            </div>

            <ChangeSystem />
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

            <ChangeVersion />
          </>
        </SettingsPagePaneLayout>
      </form>
    </SettingsPageLayout>
  );
}

function ChangeVersion() {
  const { isMobile } = useBreakpoint('mobile');
  const { config } = useConfig();
  const {
    manifest,
    channel: currentChannel,
    checkCompatibility,
  } = useUpdateContext();
  const [channel, setChannel] = useState<ChannelName>(currentChannel);
  const [version, setVersion] = useState<Version | null>(
    manifest && __VERSION_TAG__ in manifest.channels[channel].versions
      ? (__VERSION_TAG__ as Version)
      : null
  );

  const res = version && checkCompatibility(channel, version);

  const install = useCallback(() => {}, []);

  return (
    <div className="flex md-max:flex-col gap-2">
      <div className="md:w-1/4">
        <Typography>Channel</Typography>
        <div className="flex flex-col md:gap-4 sm:gap-2 xs:gap-1 mobile:gap-4 max-h-[384px] md:max-h-[512px] pr-1">
          {manifest && (
            <UpdateChannelOptions
              manifest={manifest}
              value={channel}
              variant={isMobile ? 'dropdown' : 'radio'}
              onSelect={(channel) => {
                setChannel(channel);
                setVersion(null);
              }}
            />
          )}
        </div>
      </div>

      {config?.debug && (
        <div className="md:w-1/4">
          <Typography>Version</Typography>
          <div className="flex flex-col md:gap-4 sm:gap-2 xs:gap-1 mobile:gap-4 max-h-[384px] md:max-h-[512px] overflow-auto pr-1">
            {manifest && (
              <UpdateChannelVersionOptions
                manifest={manifest}
                channel={channel}
                value={version ?? ''}
                variant={isMobile ? 'dropdown' : 'radio'}
                onSelect={(version) =>
                  setVersion(version === '' ? null : (version as Version))
                }
              />
            )}
          </div>
        </div>
      )}

      <div className="md:w-1/2 flex flex-col gap-2">
        {res?.version && (
          <>
            <Typography>Release Notes</Typography>
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
                {res.version.release_notes}
              </Markdown>
            </div>
            <div className="inline ml-auto w-fit">
              {/* TODO(devminer): add translations */}
              <Button variant="primary" disabled={!res.isInstallable}>
                Install
              </Button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

type ChangeSYstemForm = {
  platform: string;
  architecture: string;
};

function ChangeSystem() {
  const { platform, architecture, changeSystem } = useUpdateContext();

  const { reset, control, watch, handleSubmit, getValues, setValue } =
    useForm<ChangeSYstemForm>({
      defaultValues: {
        platform,
        architecture,
      },
    });

  const onSubmit = (values: ChangeSYstemForm) => {
    console.log(values);
    changeSystem(values.platform, values.architecture);
  };

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  return (
    <form className="flex flex-col gap-2" onSubmit={handleSubmit(onSubmit)}>
      <div className="grid gap-2 items-center grid-rows-2 grid-cols-[fit-content(20%),1fr]">
        <Typography bold variant="vr-accessible">
          Platform
        </Typography>
        <Input control={control} name="platform" placeholder={platform} />
        <Typography bold variant="vr-accessible">
          Architecture
        </Typography>
        <Input
          control={control}
          name="architecture"
          placeholder={architecture}
        />
      </div>

      <div className="flex justify-end gap-2">
        <Button
          variant="secondary"
          type="button"
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            reset();
          }}
        >
          Reset to real values
        </Button>
      </div>
    </form>
  );
}
