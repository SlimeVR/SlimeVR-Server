import { atom, useAtomValue, useSetAtom } from 'jotai';
import {
  BodyPart,
  BodyPartPrerequisitesRequestT,
  BodyPartPrerequisitesResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';

export type BodyPartPrerequisites = Partial<Record<BodyPart, BodyPart[][]>>;

const bodyPartPrerequisitesAtom = atom<BodyPartPrerequisites>({});

export function useProvideBodyPartPrerequisites() {
  const { useRPCPacket } = useWebsocketAPI();
  const setPrerequisites = useSetAtom(bodyPartPrerequisitesAtom);

  useRPCPacket(
    RpcMessage.BodyPartPrerequisitesResponse,
    ({ parts }: BodyPartPrerequisitesResponseT) =>
      setPrerequisites(
        Object.fromEntries(
          parts.map(({ bodyPart, requires }) => [
            bodyPart,
            requires.map(({ anyOf }) => anyOf),
          ])
        )
      )
  );
}

export function useBodyPartPrerequisites() {
  const { sendRPCPacket, useConnectedEffect } = useWebsocketAPI();

  useConnectedEffect(() => {
    sendRPCPacket(
      RpcMessage.BodyPartPrerequisitesRequest,
      new BodyPartPrerequisitesRequestT()
    );
  }, []);

  return useAtomValue(bodyPartPrerequisitesAtom);
}

export function unmetRequirements(
  groups: BodyPart[][],
  assigned: Set<BodyPart>
): BodyPart[][] {
  return groups.filter((group) => !group.some((part) => assigned.has(part)));
}
