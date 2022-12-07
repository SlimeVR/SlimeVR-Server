import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { KeyValuesT, MessageT, Payload, Topic } from 'solarxr-protocol';
import {
  OVERLAY_DISPLAY_SETTINGS_TOPIC,
  PayloadData,
  usePubSub
} from '../../hooks/pubSub';
import { CheckBox } from '../commons/Checkbox';

export function OverlayWidget() {
  const { t } = useTranslation();
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
        label={t('overlay.is-visible-label')}
      ></CheckBox>
      <CheckBox
        control={control}
        name="isMirrored"
        variant="toggle"
        label={t('overlay.is-mirrored-label')}
      ></CheckBox>
    </form>
  );
}
