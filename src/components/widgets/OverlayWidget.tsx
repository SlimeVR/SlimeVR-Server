import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { KeyValuesT } from 'solarxr-protocol/protocol/typescript/dist/solarxr-protocol/pub-sub/key-values';
import { MessageT } from 'solarxr-protocol/protocol/typescript/dist/solarxr-protocol/pub-sub/message';
import { Payload } from 'solarxr-protocol/protocol/typescript/dist/solarxr-protocol/pub-sub/payload';
import { Topic } from 'solarxr-protocol/protocol/typescript/dist/solarxr-protocol/pub-sub/topic';
import {
  OVERLAY_DISPLAY_SETTINGS_TOPIC,
  PayloadData,
  usePubSub,
} from '../../hooks/pubSub';
import { CheckBox } from '../commons/Checkbox';

export function OverlayWidget() {
  const { publish, subscribe, keyValues } = usePubSub();

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

  return (
    <form className="bg-background-60 flex flex-col w-full rounded-md px-2">
      <CheckBox
        control={control}
        name="isVisible"
        variant="toggle"
        label="Show overlay in SteamVR"
      ></CheckBox>
      <CheckBox
        control={control}
        name="isMirrored"
        variant="toggle"
        label="display overlay as mirror"
      ></CheckBox>
    </form>
  );
}
