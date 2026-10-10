import { useEffect } from 'react';
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
const requestedAtom = atom(false);

export function useBodyPartPrerequisites() {
  const { sendRPCPacket, useRPCPacket, isConnected } = useWebsocketAPI();
  const prerequisites = useAtomValue(bodyPartPrerequisitesAtom);
  const setPrerequisites = useSetAtom(bodyPartPrerequisitesAtom);
  const requested = useAtomValue(requestedAtom);
  const setRequested = useSetAtom(requestedAtom);

  useEffect(() => {
    if (!isConnected) {
      setRequested(false);
      return;
    }
    if (requested) return;

    setRequested(true);
    sendRPCPacket(
      RpcMessage.BodyPartPrerequisitesRequest,
      new BodyPartPrerequisitesRequestT()
    );
  }, [isConnected, requested]);

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

  return prerequisites;
}

export function unmetRequirements(
  groups: BodyPart[][],
  assigned: Set<BodyPart>
): BodyPart[][] {
  return groups.filter((group) => !group.some((part) => assigned.has(part)));
}
