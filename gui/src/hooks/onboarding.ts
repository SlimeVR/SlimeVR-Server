import { createContext, Reducer, useContext, useLayoutEffect, useReducer } from 'react';
import { useLocation } from 'react-router-dom';
import { useConfig } from './config';

type OnboardingAction =
  | { type: 'progress'; value: number }
  | { type: 'alone-page'; value: boolean }
  | { type: 'wifi-creds'; ssid: string; password: string };

interface OnboardingState {
  progress: number;
  wifi?: { ssid: string; password: string };
  alonePage: boolean;
}

export interface OnboardingContext {
  state: OnboardingState;
  applyProgress: (value: number) => void;
  setWifiCredentials: (ssid: string, password?: string) => void;
  skipSetup: () => void;
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

export function useProvideOnboarding(): OnboardingContext {
  const { setConfig } = useConfig();
  const [state, dispatch] = useReducer<Reducer<OnboardingState, OnboardingAction>>(
    reducer,
    {
      progress: 0,
      alonePage: false,
    }
  );

  const { state: locatioState } = useLocation();

  useLayoutEffect(() => {
    const { alonePage = false }: { alonePage?: boolean } = (locatioState as any) || {};

    if (alonePage !== state.alonePage)
      dispatch({ type: 'alone-page', value: alonePage });
  }, [locatioState, state]);

  return {
    state,
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
