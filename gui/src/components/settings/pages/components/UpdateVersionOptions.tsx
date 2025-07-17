import { Dropdown } from '@/components/commons/Dropdown';
import { WarningIcon } from '@/components/commons/icon/WarningIcon.js';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { UpdateManifest, type ChannelName } from '@slimevr/update-manifest';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { SemVer } from 'semver';

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
          <div className="bg-background-70 px-1.5 py-1 rounded-md">latest</div>
        )}
      </div>

      <Typography variant="standard" color="secondary">
        {isAlreadyInstalled && (
          <div className="text-yellow-background-300 flex gap-1 items-center">
            <WarningIcon className="size-5 min-w-5" />
            {/* TODO(devminer): add translations */}
            already installed
          </div>
        )}
        {!hasBuildsAvailableForThisPlatform && (
          <div className="text-yellow-background-300 flex gap-1 items-center">
            <WarningIcon className="size-5 min-w-5" />
            {/* TODO(devminer): add translations */}
            no build available for your platform
          </div>
        )}
      </Typography>
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
        items={Object.entries(ch.versions)
          .map(([tag, version]) => [tag, version, new SemVer(tag)] as const)
          .sort(([_11, _12, a], [_21, _22, b]) => b.compare(a))
          .map(([tag, version]) => {
            const isAlreadyInstalled = tag === __VERSION_TAG__;
            // TODO(devminer): correctly figure out the current platform
            const hasBuildAvailableForThisPlatform =
              'windows' in version.builds &&
              'x86_64' in version.builds['windows'];
            const isInstallable =
              !isAlreadyInstalled && hasBuildAvailableForThisPlatform;

            return {
              disabled: !isInstallable,
              component: (
                <div className="flex flex-row gap-2 py-1 text-left">
                  <ItemContent
                    version={tag}
                    isLatest={ch.current_version === tag}
                    isAlreadyInstalled={isAlreadyInstalled}
                    hasBuildsAvailableForThisPlatform={
                      hasBuildAvailableForThisPlatform
                    }
                  />
                </div>
              ),
              value: tag,
            };
          })}
      />
    );

  return (
    <div className="flex flex-col gap-2">
      {Object.entries(ch.versions)
        .map(([tag, version]) => [tag, version, new SemVer(tag)] as const)
        .sort(([_11, _12, a], [_21, _22, b]) => b.compare(a))
        .map(([tag, version]) => {
          const isAlreadyInstalled = tag === __VERSION_TAG__;
          // TODO(devminer): correctly figure out the current platform
          const hasBuildAvailableForThisPlatform =
            'windows' in version.builds &&
            'x86_64' in version.builds['windows'];
          const isInstallable =
            !isAlreadyInstalled && hasBuildAvailableForThisPlatform;

          return (
            <Radio
              key={tag}
              name="version"
              control={control}
              value={tag}
              className="hidden"
              disabled={!isInstallable}
            >
              <div className="flex flex-row md:gap-4 gap-2">
                <ItemContent
                  version={tag}
                  isLatest={ch.current_version === tag}
                  isAlreadyInstalled={isAlreadyInstalled}
                  hasBuildsAvailableForThisPlatform={
                    hasBuildAvailableForThisPlatform
                  }
                />
              </div>
            </Radio>
          );
        })}
    </div>
  );
}
