import { Dropdown } from '@/components/commons/Dropdown';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { UpdateManifest, type ChannelName } from '@slimevr/update-manifest';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { SemVer } from 'semver';

const ItemContent = ({
  version,
  isLatest,
}: {
  version: string;
  isLatest: boolean;
}) => {
  return (
    <div className="flex gap-2 items-center">
      <Typography>{version}</Typography>

      {isLatest && (
        <div className="bg-background-70 px-1.5 py-1 rounded-md">latest</div>
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
        items={Object.keys(ch.versions)
          .map((tag) => [tag, new SemVer(tag)] as const)
          .sort(([_1, a], [_2, b]) => b.compare(a))
          .map(([tag]) => ({
            component: (
              <div className="flex flex-row gap-2 py-1 text-left">
                <ItemContent
                  version={tag}
                  isLatest={ch.current_version === tag}
                />
              </div>
            ),
            value: version,
          }))}
      />
    );

  return (
    <div className="flex flex-col gap-2">
      {Object.keys(ch.versions)
        .map((tag) => [tag, new SemVer(tag)] as const)
        .sort(([_1, a], [_2, b]) => b.compare(a))
        .map(([tag]) => (
          <Radio
            key={tag}
            name="version"
            control={control}
            value={tag}
            className="hidden"
          >
            <div className="flex flex-row md:gap-4 gap-2">
              <ItemContent
                version={tag}
                isLatest={ch.current_version === tag}
              />
            </div>
          </Radio>
        ))}
    </div>
  );
}
