import { ReactNode, useState } from 'react';
import { useLayout } from '@/hooks/layout';
import { useOnboarding } from '@/hooks/onboarding';
import { MainLayoutRoute } from '@/components/MainLayout';
import { TopBar } from '@/components/TopBar';
import { useBreakpoint } from '@/hooks/breakpoint';
import { SkipSetupButton } from './SkipSetupButton';
import { SkipSetupWarningModal } from './SkipSetupWarningModal';

export function OnboardingLayout({ children }: { children: ReactNode }) {
  const { layoutHeight, ref } = useLayout<HTMLDivElement>();
  const { isMobile } = useBreakpoint('mobile');
  const { state, skipSetup } = useOnboarding();
  const [skipWarning, setSkipWarning] = useState(false);

  return !state.alonePage ? (
    <>
      <TopBar progress={state.progress}></TopBar>
      <div
        ref={ref}
        className="flex-grow relative"
        style={{ height: layoutHeight }}
      >
        <div className="absolute top-12 mobile:top-0 right-2 z-50">
          <SkipSetupButton
            visible={true}
            modalVisible={skipWarning}
            onClick={() => setSkipWarning(true)}
          ></SkipSetupButton>
        </div>
        {children}
        <SkipSetupWarningModal
          accept={skipSetup}
          onClose={() => setSkipWarning(false)}
          isOpen={skipWarning}
        ></SkipSetupWarningModal>
      </div>
    </>
  ) : (
    <MainLayoutRoute widgets={false} isMobile={isMobile}>
      <div className="flex-grow xs:pt-10 mobile:pt-2">{children}</div>
    </MainLayoutRoute>
  );
}
