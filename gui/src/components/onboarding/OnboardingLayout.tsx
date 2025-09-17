import { ReactNode, useState } from 'react';
import { useOnboarding } from '@/hooks/onboarding';
import { MainLayout } from '@/components/MainLayout';
import { TopBar } from '@/components/TopBar';
import { useBreakpoint } from '@/hooks/breakpoint';
import { SkipSetupButton } from './SkipSetupButton';
import { SkipSetupWarningModal } from './SkipSetupWarningModal';
import './OnboardingLayout.scss';
import classNames from 'classnames';

export function OnboardingLayout({ children }: { children: ReactNode }) {
  const { isMobile } = useBreakpoint('mobile');
  const { state, skipSetup } = useOnboarding();
  const [showWarning, setShowWarning] = useState(false);

  return !state.alonePage ? (
    <div className="onboarding-layout h-full">
      <div
        style={{ gridArea: 't' }}
        className={classNames(window.__IOS__ && 'mobile:mt-10')}
      >
        <TopBar progress={state.progress}></TopBar>
      </div>
      <div
        style={{ gridArea: 'c' }}
        className={classNames(
          'relative mt-2',
          window.__IOS__ && 'mobile:mt-12'
        )}
      >
        <div className="absolute top-12 mobile:top-0 right-2 z-50">
          <SkipSetupButton
            visible={true}
            modalVisible={showWarning}
            onClick={() => setShowWarning(true)}
          ></SkipSetupButton>
        </div>
        <div className="h-full w-full overflow-y-auto">{children}</div>
        <SkipSetupWarningModal
          accept={skipSetup}
          onClose={() => setShowWarning(false)}
          isOpen={showWarning}
        ></SkipSetupWarningModal>
      </div>
    </div>
  ) : (
    <MainLayout widgets={false} isMobile={isMobile}>
      {children}
    </MainLayout>
  );
}
