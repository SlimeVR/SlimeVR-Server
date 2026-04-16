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
  ChangeSettingsRequestT,
  ModelSettingsT,
  ModelTogglesT,
  OSCSettingsT,
  ResetsSettingsT,
  RpcMessage,
  SettingsRequestT,
  SettingsResponseT,
  SkeletonBone,
  VRCOSCSettingsT,
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

  const { state: locatioState, pathname } = useLocation();
  const [previousPath, setPreviousPath] = useState(pathname);
  const [settings, setSettings] = useState<SettingsResponseT>();

  useLayoutEffect(() => {
    const { alonePage = false }: { alonePage?: boolean } = (locatioState as any) || {};

    if (alonePage !== state.alonePage)
      dispatch({ type: 'alone-page', value: alonePage });
  }, [locatioState, state]);

  useEffect(() => {
    sendRPCPacket(RpcMessage.SettingsRequest, new SettingsRequestT());
  }, []);

  useRPCPacket(RpcMessage.SettingsResponse, (settings: SettingsResponseT) => {
    setSettings(settings);
  });

  const { changeBoneValue } = useManualProportions({
    type: 'linear',
  });

  const onboardingEnded = () => {
    if (!settings?.modelSettings || !settings?.vrcOsc) throw 'settings should be set';
    const req = new ChangeSettingsRequestT();
    const modelSettings = new ModelSettingsT();
    const oscSettings = new VRCOSCSettingsT();

    const mocap = usage === 'mocap' || usage === 'vtubing';

    const toggles = Object.assign(new ModelTogglesT(), settings.modelSettings.toggles);
    toggles.selfLocalization = mocap && playspace === 'standing';
    modelSettings.toggles = toggles;
    req.modelSettings = modelSettings;

    const resets = Object.assign(new ResetsSettingsT(), settings.resetsSettings);
    resets.resetHmdPitch = mocapPos === 'forehead';
    req.resetsSettings = resets;

    const osc = Object.assign(new OSCSettingsT(), settings.vrcOsc.oscSettings);
    osc.enabled = vrcOsc ?? false;
    oscSettings.oscSettings = osc;
    req.vrcOsc = oscSettings;

    sendRPCPacket(RpcMessage.ChangeSettingsRequest, req);

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

  useEffect(() => {
    setPreviousPath(pathname);

    if (!pathname.startsWith('/onboarding') && previousPath.startsWith('/onboarding')) {
      onboardingEnded();
    }
    if (pathname.startsWith('/onboarding') && !previousPath.startsWith('/onboarding')) {
      onboardingStarted();
    }
  }, [pathname]);

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
