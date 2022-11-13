import { ReactChild } from 'react';
import {
  OnboardingContextC,
  useProvideOnboarding,
} from '../../hooks/onboarding';

export function OnboardingContextProvider({
  children,
}: {
  children: ReactChild;
}) {
  const context = useProvideOnboarding();

  return (
    <OnboardingContextC.Provider value={context}>
      {children}
    </OnboardingContextC.Provider>
  );
}
