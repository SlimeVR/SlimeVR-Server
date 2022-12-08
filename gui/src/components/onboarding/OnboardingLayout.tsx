import { ReactNode } from 'react';
import { useLayout } from '../../hooks/layout';
import { useOnboarding } from '../../hooks/onboarding';
import { MainLayoutRoute } from '../MainLayout';
import { TopBar } from '../TopBar';

export function OnboardingLayout({ children }: { children: ReactNode }) {
  const { layoutHeight, ref } = useLayout<HTMLDivElement>();
  const { state } = useOnboarding();

  return !state.alonePage ? (
    <>
      <TopBar progress={state.progress}></TopBar>
      <div
        ref={ref}
        className="flex-grow pt-10 mx-4"
        style={{ height: layoutHeight }}
      >
        {children}
      </div>
    </>
  ) : (
    <MainLayoutRoute widgets={false}>
      <div className="flex-grow pt-10 mx-4">{children}</div>
    </MainLayoutRoute>
  );
}
