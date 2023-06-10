import { ReactNode } from 'react';
import { useLayout } from '../../hooks/layout';
import { useOnboarding } from '../../hooks/onboarding';
import { MainLayoutRoute } from '../MainLayout';
import { TopBar } from '../TopBar';
import { useBreakpoint } from '../../hooks/breakpoint';

export function OnboardingLayout({ children }: { children: ReactNode }) {
  const { layoutHeight, ref } = useLayout<HTMLDivElement>();
  const { isMobile } = useBreakpoint('mobile');
  const { state } = useOnboarding();

  return !state.alonePage ? (
    <>
      <TopBar progress={state.progress}></TopBar>
      <div
        ref={ref}
        className="flex-grow xs:pt-10 mobile:pt-2"
        style={{ height: layoutHeight }}
      >
        {children}
      </div>
    </>
  ) : (
    <MainLayoutRoute widgets={false} isMobile={isMobile}>
      <div className="flex-grow xs:pt-10 mobile:pt-2">{children}</div>
    </MainLayoutRoute>
  );
}
