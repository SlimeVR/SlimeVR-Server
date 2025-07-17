import { Dropdown } from '@/components/commons/Dropdown';
import { Radio } from '@/components/commons/Radio';
import { Typography } from '@/components/commons/Typography';
import { ASSIGNMENT_MODES } from '@/components/onboarding/BodyAssignment';
import { AssignMode, defaultConfig } from '@/hooks/config';
import { UpdateManifest, type ChannelName } from '@slimevr/update-manifest';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';

// Ordered collection of assign modes with the number of IMU trackers
const ASSIGN_MODE_OPTIONS = [
  AssignMode.LowerBody,
  AssignMode.Core,
  AssignMode.EnhancedCore,
  AssignMode.FullBody,
  AssignMode.All,
].reduce(
  (options, mode) => ({ ...options, [mode]: ASSIGNMENT_MODES[mode].length }),
  {} as Record<AssignMode, number>
);

const ItemContent = ({
  name: channel,
  description,
}: {
  name: ChannelName;
  description?: string;
}) => {
  return (
    <div className="flex flex-col">
      <div className="flex gap-2 items-center">
        <Typography>{channel}</Typography>

        {channel === defaultConfig.updateChannel && (
          <div className="bg-background-70 px-1.5 py-1 rounded-md">default</div>
        )}
      </div>

      <Typography variant="standard" color="secondary">
        {description}
      </Typography>
    </div>
  );
};

export function UpdateChannelOptions({
  manifest,
  value,
  variant = 'radio',
  onSelect,
}: {
  manifest: UpdateManifest;
  value: ChannelName;
  variant: 'radio' | 'dropdown';
  onSelect: (channel: ChannelName) => void;
}) {
  const { control, watch } = useForm<{
    updateChannel: ChannelName;
  }>({
    defaultValues: {
      updateChannel: value,
    },
  });
  const { updateChannel } = watch();

  useEffect(() => {
    onSelect(updateChannel);
  }, [updateChannel]);

  if (variant == 'dropdown')
    return (
      <Dropdown
        control={control}
        name="updateChannel"
        display="block"
        direction="down"
        placeholder=""
        items={Object.entries(manifest.channels).map(([channel, data]) => ({
          component: (
            <div className="flex flex-row gap-2 py-1 text-left">
              <ItemContent
                name={channel as ChannelName}
                description={data.description}
              />
            </div>
          ),
          value: channel,
        }))}
      ></Dropdown>
    );

  return (
    <div className="flex flex-col gap-2">
      {Object.entries(manifest.channels).map(([channel, data]) => (
        <Radio
          key={channel}
          name="updateChannel"
          control={control}
          value={channel}
          className="hidden"
        >
          <div className="flex flex-row md:gap-4 gap-2">
            <ItemContent
              name={channel as ChannelName}
              description={data.description}
            />
          </div>
        </Radio>
      ))}
    </div>
  );
}
