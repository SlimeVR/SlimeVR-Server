import { ReactNode } from 'react';
import { OnboardingContextC, useProvideOnboarding } from '@/hooks/onboarding';

export function OnboardingContextProvider({
  children,
}: {
  children: ReactNode;
}) {
  const context = useProvideOnboarding();

  return (
    <OnboardingContextC.Provider value={context}>
      {children}
    </OnboardingContextC.Provider>
  );
}
