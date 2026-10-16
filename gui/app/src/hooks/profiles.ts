import { useRef } from 'react';
import { atom, useAtomValue, useSetAtom } from 'jotai';
import {
  CreateProfileRequestT,
  DeleteProfileRequestT,
  ProfileKind,
  ProfileListT,
  ProfileOperationResponseT,
  ProfileOperationStatus,
  ProfilesRequestT,
  ProfilesResponseT,
  RenameProfileRequestT,
  RpcMessage,
  SwitchProfileRequestT,
} from 'solarxr-protocol';
import { useWebsocketAPI } from './websocket-api';

export type ProfileNames = {
  active: string;
  names: string[];
};

export type Profiles = {
  settings: ProfileNames;
  user: ProfileNames;
};

export const profilesAtom = atom<Profiles | null>(null);

function toProfileNames(list: ProfileListT | null): ProfileNames {
  return {
    active: list?.active?.toString() ?? '',
    names: list?.names.map((name) => name.toString()) ?? [],
  };
}

export function useProvideProfiles() {
  const { useRPCPacket, sendRPCPacket, useConnectedEffect } = useWebsocketAPI();
  const setProfiles = useSetAtom(profilesAtom);

  useConnectedEffect(() => {
    sendRPCPacket(RpcMessage.ProfilesRequest, new ProfilesRequestT());
  }, []);

  useRPCPacket(RpcMessage.ProfilesResponse, (res: ProfilesResponseT) =>
    setProfiles({
      settings: toProfileNames(res.settings),
      user: toProfileNames(res.user),
    })
  );
}

export function useProfiles() {
  return useAtomValue(profilesAtom);
}

export function useProfileOperations() {
  const { useRPCPacket, sendRPCPacket } = useWebsocketAPI();
  const waiting = useRef<((status: ProfileOperationStatus) => void) | null>(null);

  useRPCPacket(
    RpcMessage.ProfileOperationResponse,
    (res: ProfileOperationResponseT) => {
      waiting.current?.(res.status);
      waiting.current = null;
    }
  );

  function run(type: RpcMessage, request: Parameters<typeof sendRPCPacket>[1]) {
    return new Promise<ProfileOperationStatus>((resolve) => {
      waiting.current = resolve;
      sendRPCPacket(type, request);
    });
  }

  return {
    switchProfile: (kind: ProfileKind, name: string) =>
      run(RpcMessage.SwitchProfileRequest, new SwitchProfileRequestT(kind, name)),
    createProfile: (kind: ProfileKind, name: string, copyFrom: string) =>
      run(
        RpcMessage.CreateProfileRequest,
        new CreateProfileRequestT(kind, name, copyFrom)
      ),
    renameProfile: (kind: ProfileKind, name: string, newName: string) =>
      run(
        RpcMessage.RenameProfileRequest,
        new RenameProfileRequestT(kind, name, newName)
      ),
    deleteProfile: (kind: ProfileKind, name: string) =>
      run(RpcMessage.DeleteProfileRequest, new DeleteProfileRequestT(kind, name)),
  };
}

/** Fluent id of the message for a failed operation. */
export function profileErrorId(status: ProfileOperationStatus) {
  return `settings-profiles-error-${ProfileOperationStatus[status].toLowerCase()}`;
}
