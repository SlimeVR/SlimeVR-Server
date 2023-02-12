import { useEffect, useState } from 'react';
import {
  KeyValuesT,
  MessageT,
  PubSubUnion,
  SubscriptionRequestT,
  Topic,
  TopicHandleT,
  TopicIdT,
  TopicMappingT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';

export type PayloadData = MessageT['payload'];
export type PayloadType = MessageT['payloadType'];

export function usePubSub() {
  const { sendPubSubPacket, usePubSubPacket } = useWebsocketAPI();
  const [handleTopics, setHandleTopics] = useState<{ [key: number]: TopicIdT }>({});

  const subscribe = (
    topicId: TopicIdT,
    callback: (data: PayloadData, type: PayloadType) => void
  ): void => {
    useEffect(() => {
      const subRequest = new SubscriptionRequestT();
      subRequest.topicType = Topic.TopicId;
      subRequest.topic = topicId;
      sendPubSubPacket(PubSubUnion.SubscriptionRequest, subRequest);
    }, [sendPubSubPacket, usePubSubPacket]);

    usePubSubPacket(PubSubUnion.Message, (message: MessageT) => {
      if (message.topicType == Topic.TopicHandle) {
        const messageHandleId = message.topic as TopicHandleT;
        if (handleTopics[messageHandleId.id]) {
          callback(message.payload, message.payloadType);
        }
      }
      if (message.topicType == Topic.TopicId) {
        const messageTopicId = message.topic as TopicIdT;
        if (
          messageTopicId.appName === topicId.appName &&
          messageTopicId.organization === topicId.organization &&
          messageTopicId.topic === topicId.topic
        ) {
          callback(message.payload, message.payloadType);
        }
      }
    });
  };

  usePubSubPacket(PubSubUnion.TopicMapping, (data: TopicMappingT) => {
    setHandleTopics((handleTopics) => ({
      ...handleTopics,
      ...(data.handle?.id && data.id ? { [data.handle?.id]: data.id } : {}),
    }));
  });

  const publish = (message: MessageT) => {
    sendPubSubPacket(PubSubUnion.Message, message);
  };

  return {
    subscribe,
    publish,
    keyValues: (payload: PayloadData): Record<string, string> => {
      const keyValuesPayload = payload as KeyValuesT;
      return keyValuesPayload.keys.reduce(
        (curr, _, index) => ({
          ...curr,
          [keyValuesPayload.keys[index]]: keyValuesPayload.values[index],
        }),
        {}
      );
    },
  };
}

export const topic = ({ appName, organization, topic }: Omit<TopicIdT, 'pack'>) =>
  Object.assign(new TopicIdT(), { appName, organization, topic });

export const OVERLAY_DISPLAY_SETTINGS_TOPIC = topic({
  appName: 'overlay',
  organization: 'slimevr.dev',
  topic: 'display_settings',
});
