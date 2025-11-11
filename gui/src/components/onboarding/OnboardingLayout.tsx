import { ReactNode, useState } from 'react';
import { useOnboarding } from '@/hooks/onboarding';
import { MainLayout } from '@/components/MainLayout';
import { TopBar } from '@/components/TopBar';
import { useBreakpoint } from '@/hooks/breakpoint';
import { SkipSetupButton } from './SkipSetupButton';
import { SkipSetupWarningModal } from './SkipSetupWarningModal';
import './OnboardingLayout.scss';

export function OnboardingLayout({ children }: { children: ReactNode }) {
  const { isMobile } = useBreakpoint('mobile');
  const { state, skipSetup } = useOnboarding();
  const [showWarning, setShowWarning] = useState(false);

  return !state.alonePage ? (
    <div className="onboarding-layout h-full">
      <div style={{ gridArea: 't' }}>
        <TopBar progress={state.progress} />
      </div>
      <div style={{ gridArea: 'c' }} className="mt-2 relative">
        <div className="absolute top-12 mobile:top-0 right-2 z-50">
          <SkipSetupButton
            visible={true}
            modalVisible={showWarning}
            onClick={() => setShowWarning(true)}
          />
        </div>
        <div className="h-full w-full overflow-y-auto">{children}</div>
        <SkipSetupWarningModal
          accept={skipSetup}
          onClose={() => setShowWarning(false)}
          isOpen={showWarning}
        />
      </div>
    </div>
  ) : (
    <MainLayout isMobile={isMobile}>{children}</MainLayout>
  );
}
