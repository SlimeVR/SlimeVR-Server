import { useLocalization } from '@fluent/react';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { KeyValuesT, MessageT, Payload, Topic } from 'solarxr-protocol';
import {
  OVERLAY_DISPLAY_SETTINGS_TOPIC,
  PayloadData,
  usePubSub,
} from '@/hooks/pubSub';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';

export function OverlayWidget() {
  const { l10n } = useLocalization();
  const { publish, subscribe, keyValues } = usePubSub();
  const [loading, setLoading] = useState(true);
  const { reset, control, handleSubmit, watch } = useForm<{
    isVisible: boolean;
    isMirrored: boolean;
  }>({
    defaultValues: {
      isVisible: false,
      isMirrored: false,
    },
  });

  subscribe(
    OVERLAY_DISPLAY_SETTINGS_TOPIC,
    (payload: PayloadData, type: Payload) => {
      setLoading(false);
      if (type !== Payload.KeyValues) throw new Error('Invalid payload');
      const obj = keyValues(payload);
      reset({
        isMirrored: obj['is_mirrored'] === 'true',
        isVisible: obj['is_visible'] === 'true',
      });
    }
  );

  useEffect(() => {
    const message = new MessageT();
    message.topic = OVERLAY_DISPLAY_SETTINGS_TOPIC;
    message.topicType = Topic.TopicId;
    message.payloadType = Payload.NONE;
    publish(message);
  }, []);

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = (val: { isVisible: boolean; isMirrored: boolean }) => {
    const message = new MessageT();

    message.topic = OVERLAY_DISPLAY_SETTINGS_TOPIC;
    message.topicType = Topic.TopicId;

    message.payloadType = Payload.KeyValues;
    const keyValues = new KeyValuesT();
    keyValues.keys = ['is_mirrored', 'is_visible'];
    keyValues.values = [`${val.isMirrored}`, `${val.isVisible}`];
    message.payload = keyValues;

    publish(message);
  };

  return !loading ? (
    <form className="bg-background-60 flex flex-col w-full rounded-md px-2">
      <div className="mt-2 px-1">
        <Typography color="secondary">
          {l10n.getString('widget-overlay')}
        </Typography>
      </div>
      <CheckBox
        control={control}
        name="isVisible"
        variant="toggle"
        label={l10n.getString('widget-overlay-is_visible_label')}
      ></CheckBox>
      <CheckBox
        control={control}
        name="isMirrored"
        variant="toggle"
        label={l10n.getString('widget-overlay-is_mirrored_label')}
      ></CheckBox>
    </form>
  ) : (
    <></>
  );
}
