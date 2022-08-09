import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import {
  OverlayDisplayModeChangeRequestT,
  OverlayDisplayModeRequestT,
  OverlayDisplayModeResponseT,
  RpcMessage,
} from 'solarxr-protocol';
import { useWebsocketAPI } from '../../hooks/websocket-api';
import { CheckBox } from '../commons/Checkbox';

export function OverlayWidget() {
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const { reset, control, handleSubmit, watch, setValue } = useForm<{
    isVisible: boolean;
    isMirrored: boolean;
  }>({
    defaultValues: {
      isVisible: false,
      isMirrored: false,
    },
  });

  useRPCPacket(
    RpcMessage.OverlayDisplayModeResponse,
    (res: OverlayDisplayModeResponseT) => {
      reset({
        isMirrored: res.isMirrored,
        isVisible: res.isVisible,
      });
    }
  );

  useEffect(() => {
    sendRPCPacket(
      RpcMessage.OverlayDisplayModeRequest,
      new OverlayDisplayModeRequestT()
    );
  }, []);

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = (val: { isVisible: boolean; isMirrored: boolean }) => {
    const req = new OverlayDisplayModeChangeRequestT();
    req.isMirrored = val.isMirrored;
    req.isVisible = val.isVisible;
    sendRPCPacket(RpcMessage.OverlayDisplayModeChangeRequest, req);
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
