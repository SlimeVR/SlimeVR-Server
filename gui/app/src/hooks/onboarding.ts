import {
  createContext,
  Reducer,
  useContext,
  useEffect,
  useLayoutEffect,
  useReducer,
  useState,
} from 'react';
import { useLocation } from 'react-router-dom';
import { useConfig } from './config';
import { useWebsocketAPI } from './websocket-api';
import {
  ChangeResetsSettingsRequestT,
  ChangeSkeletonSettingsRequestT,
  ResetsSettingsRequestT,
  ResetsSettingsResponseT,
  RpcMessage,
  SkeletonBone,
  SkeletonSettingsRequestT,
  SkeletonSettingsResponseT,
  SkeletonTogglesT,
  VRCOSCSettingsRequestT,
  VRCOSCSettingsResponseT,
} from 'solarxr-protocol';
import { useManualProportions } from './manual-proportions';

type OnboardingAction =
  | { type: 'progress'; value: number }
  | { type: 'alone-page'; value: boolean }
  | { type: 'wifi-creds'; ssid: string; password: string };

interface OnboardingState {
  progress: number;
  wifi?: { ssid: string; password: string };
  alonePage: boolean;
}

export function reducer(state: OnboardingState, action: OnboardingAction) {
  switch (action.type) {
    case 'wifi-creds':
      return {
        ...state,
        wifi: { ssid: action.ssid, password: action.password },
      };
    case 'progress':
      return {
        ...state,
        progress: action.value,
      };
    case 'alone-page':
      return {
        ...state,
        alonePage: action.value,
      };
    default:
      throw new Error(`unhandled state action ${(action as any).type}`);
  }
}

export type OnboardingContext = ReturnType<typeof useProvideOnboarding>;

export function useProvideOnboarding() {
  const { setConfig } = useConfig();
  const [slimeSet, setSlimeSet] = useState<
    'butterfly' | 'slime-v1' | 'dongle-slime' | 'wifi-slime'
  >();
  const [usage, setUsage] = useState<'vr-gaming' | 'mocap' | 'vtubing'>();
  const [vrcOsc, setVrcOSC] = useState<boolean>();
  const [mocapPos, setMocapPos] = useState<'forehead' | 'face'>();
  const [playspace, setPlayspace] = useState<'sitting' | 'standing'>();
  const [state, dispatch] = useReducer<Reducer<OnboardingState, OnboardingAction>>(
    reducer,
    {
      progress: 0,
      alonePage: false,
    }
  );

  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();

  const { state: locatioState } = useLocation();
  const [skeletonSettings, setSkeletonSettings] = useState<SkeletonSettingsResponseT>();
  const [resetsSettings, setResetsSettings] = useState<ResetsSettingsResponseT>();
  const [vrcOscSettings, setVrcOscSettings] = useState<VRCOSCSettingsResponseT>();

  useLayoutEffect(() => {
    const { alonePage = false }: { alonePage?: boolean } = (locatioState as any) || {};

    if (alonePage !== state.alonePage)
      dispatch({ type: 'alone-page', value: alonePage });
  }, [locatioState, state]);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SkeletonSettingsRequest, new SkeletonSettingsRequestT());
    sendRPCPacket(RpcMessage.ResetsSettingsRequest, new ResetsSettingsRequestT());
    sendRPCPacket(RpcMessage.VRCOSCSettingsRequest, new VRCOSCSettingsRequestT());
  }, []);

  useRPCPacket(
    RpcMessage.SkeletonSettingsResponse,
    (settings: SkeletonSettingsResponseT) => {
      setSkeletonSettings(settings);
    }
  );
  useRPCPacket(
    RpcMessage.ResetsSettingsResponse,
    (settings: ResetsSettingsResponseT) => {
      setResetsSettings(settings);
    }
  );
  useRPCPacket(
    RpcMessage.VRCOSCSettingsResponse,
    (settings: VRCOSCSettingsResponseT) => {
      setVrcOscSettings(settings);
    }
  );

  const { changeBoneValue } = useManualProportions({
    type: 'linear',
  });

  const onboardingEnded = () => {
    setConfig({ doneOnboarding: true });
    if (!skeletonSettings || !resetsSettings || !vrcOscSettings) return;

    const mocap = usage === 'mocap' || usage === 'vtubing';

    const toggles = Object.assign(new SkeletonTogglesT(), skeletonSettings.toggles);
    toggles.mocapMode = mocap && playspace === 'standing';

    const skeletonReq = new ChangeSkeletonSettingsRequestT();
    skeletonReq.toggles = toggles;
    skeletonReq.ratios = skeletonSettings.ratios;
    skeletonReq.filtering = skeletonSettings.filtering;
    sendRPCPacket(RpcMessage.ChangeSkeletonSettingsRequest, skeletonReq);

    const resetsReq = Object.assign(new ChangeResetsSettingsRequestT(), resetsSettings);
    resetsReq.resetHmdPitch = mocapPos === 'forehead';
    sendRPCPacket(RpcMessage.ChangeResetsSettingsRequest, resetsReq);

    const osc = Object.assign(new VRCOSCSettingsResponseT(), vrcOscSettings);
    osc.enabled = vrcOsc ?? false;
    sendRPCPacket(RpcMessage.ChangeVRCOSCSettingsRequest, osc);

    if (mocap) {
      changeBoneValue({ bone: SkeletonBone.HAND_Z, type: 'bone', newValue: 0 });
    }
  };

  const onboardingStarted = () => {
    setSlimeSet(undefined);
    setUsage(undefined);
    setVrcOSC(undefined);
    setMocapPos(undefined);
    setPlayspace(undefined);
  };

  return {
    state,
    slimeSet,
    usage,
    vrcOsc,
    mocapPos,
    playspace,
    setPlayspace,
    applyProgress: (value: number) => {
      useLayoutEffect(() => {
        dispatch({ type: 'progress', value });
      }, []);
    },
    setWifiCredentials: (ssid: string, password?: string) => {
      dispatch({ type: 'wifi-creds', ssid, password: password ?? '' });
    },
    skipSetup: () => {
      setConfig({ doneOnboarding: true });
    },
    setSlimeSet,
    setUsage,
    setVrcOSC,
    setMocapPos,
    onboardingEnded,
    onboardingStarted,
  };
}

export const OnboardingContextC = createContext<OnboardingContext>(undefined as any);

export function useOnboarding() {
  const context = useContext<OnboardingContext>(OnboardingContextC);
  if (!context) {
    throw new Error('useOnboarding must be within a OnboardingContext Provider');
  }
  return context;
}
