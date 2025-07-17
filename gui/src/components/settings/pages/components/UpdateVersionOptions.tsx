import { Dropdown } from '@/components/commons/Dropdown';
import { WarningIcon } from '@/components/commons/icon/WarningIcon.js';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { useUpdateContext } from '@/hooks/update.js';
import {
  UpdateManifest,
  Version,
  type ChannelName,
} from '@slimevr/update-manifest';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { compare } from 'semver';

const ItemContent = ({
  version,
  isLatest,
  isAlreadyInstalled,
  hasBuildsAvailableForThisPlatform,
}: {
  version: string;
  isLatest: boolean;
  isAlreadyInstalled: boolean;
  hasBuildsAvailableForThisPlatform: boolean;
}) => {
  return (
    <div className="flex flex-col">
      <div className="flex gap-2 items-center">
        <Typography>{version}</Typography>

        {isLatest && (
          /* TODO(devminer): add translations */
          <div className="bg-background-70 px-1.5 py-0.5 rounded-md leading-[1rem] text-[0.625rem]">
            latest
          </div>
        )}
      </div>

      {isAlreadyInstalled && (
        <div className="text-yellow-background-300 flex gap-1 items-center">
          <WarningIcon className="size-5 min-w-5" />
          {/* TODO(devminer): add translations */}
          <Typography variant="standard" color="secondary">
            already installed
          </Typography>
        </div>
      )}
      {!hasBuildsAvailableForThisPlatform && (
        <div className="text-yellow-background-300 flex gap-1 items-center">
          <WarningIcon className="size-5 min-w-5" />
          {/* TODO(devminer): add translations */}
          <Typography variant="standard" color="secondary">
            no build available for your platform
          </Typography>
        </div>
      )}
    </div>
  );
};

export function UpdateChannelVersionOptions({
  manifest,
  channel,
  value,
  variant = 'radio',
  onSelect,
}: {
  manifest: UpdateManifest;
  channel: ChannelName;
  value: string;
  variant: 'radio' | 'dropdown';
  onSelect: (version: string) => void;
}) {
  const { channel: currentChannel, checkCompatibilityFromVersionInfo } =
    useUpdateContext();
  const { control, watch } = useForm<{
    version: string;
  }>({
    defaultValues: {
      version: value,
    },
  });
  const { version } = watch();

  const ch = manifest.channels[channel];

  useEffect(() => {
    onSelect(version);
  }, [version]);

  if (variant == 'dropdown')
    return (
      <Dropdown
        control={control}
        name="version"
        display="block"
        direction="down"
        placeholder=""
        maxHeight="300px"
        items={Object.entries(ch.versions)
          .map(([tag, version]) => [tag as Version, version] as const)
          .sort(([a, _12], [b, _22]) => compare(b, a))
          .map(([version, versionInfo]) => {
            const res = checkCompatibilityFromVersionInfo(
              channel,
              version,
              versionInfo
            );

            return {
              value: version,
              component: (
                <div className="flex flex-row gap-2 py-1 text-left">
                  <ItemContent
                    version={version}
                    isLatest={ch.current_version === version}
                    isAlreadyInstalled={res.alreadyInstalled}
                    hasBuildsAvailableForThisPlatform={!!res.build}
                  />
                </div>
              ),
            };
          })}
      />
    );

  return (
    <div className="flex flex-col gap-2">
      {Object.entries(ch.versions)
        .map(([tag, version]) => [tag as Version, version] as const)
        .sort(([a, _12], [b, _22]) => compare(b, a))
        .map(([version, versionInfo]) => {
          const res = checkCompatibilityFromVersionInfo(
            channel,
            version,
            versionInfo
          );

          return (
            <Radio
              key={version}
              name="version"
              control={control}
              value={version}
              className="hidden"
            >
              <div className="flex flex-row md:gap-4 gap-2">
                <ItemContent
                  version={version}
                  isLatest={ch.current_version === version}
                  isAlreadyInstalled={res.alreadyInstalled}
                  hasBuildsAvailableForThisPlatform={!!res.build}
                />
              </div>
            </Radio>
          );
        })}
    </div>
  );
}
