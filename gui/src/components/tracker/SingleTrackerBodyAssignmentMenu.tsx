import classNames from 'classnames';
import ReactModal from 'react-modal';
import { BodyPart } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { BodyAssignment } from '@/components/onboarding/BodyAssignment';
import { useLocalization } from '@fluent/react';
import { NeckWarningModal } from '@/components/onboarding/NeckWarningModal';
import { useChokerWarning } from '@/hooks/choker-warning';
import { useBreakpoint } from '@/hooks/breakpoint';
import { defaultConfig, useConfig } from '@/hooks/config';
import { TrackerAssignOptions } from '@/components/onboarding/pages/trackers-assign/TrackerAssignOptions';

export function SingleTrackerBodyAssignmentMenu({
  isOpen,
  onClose,
  onRoleSelected,
}: {
  isOpen: boolean;
  onClose: () => void;
  onRoleSelected: (role: BodyPart) => void;
}) {
  const { isMobile } = useBreakpoint('mobile');
  const { l10n } = useLocalization();
  const { config } = useConfig();

  const { closeChokerWarning, tryOpenChokerWarning, shouldShowChokerWarn } =
    useChokerWarning({
      next: onRoleSelected,
    });

  return (
    <>
      <ReactModal
        isOpen={isOpen}
        shouldCloseOnOverlayClick
        shouldCloseOnEsc
        onRequestClose={onClose}
        overlayClassName={classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full justify-center bg-background-90 bg-opacity-90 z-20'
        )}
        className={classNames(
          'focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent outline-none mt-12 z-10 overflow-y-auto'
        )}
      >
        <div className="flex w-full h-full flex-col gap-10 px-3">
          <div className="flex xs:flex-row h-full xs:gap-8 mobile:flex-col  xs:justify-center items-center">
            <div className="flex flex-col xs:max-w-sm gap-3">
              <Typography variant="mobile-title" bold>
                {l10n.getString('body_assignment_menu')}
              </Typography>
              <Typography color="secondary">
                {l10n.getString('body_assignment_menu-description')}
              </Typography>
              <div className="flex">
                <Button
                  variant="secondary"
                  to="/onboarding/trackers-assign"
                  state={{ alonePage: true }}
                >
                  {l10n.getString('body_assignment_menu-manage_trackers')}
                </Button>
              </div>
              <TrackerAssignOptions variant={isMobile ? 'dropdown' : 'radio'} />
            </div>
            <div className="flex flex-col xs:flex-grow gap-3 rounded-xl fill-background-50 py-2">
              <BodyAssignment
                mirror={config?.mirrorView ?? defaultConfig.mirrorView}
                width={isMobile ? 160 : undefined}
                onlyAssigned={false}
                assignMode={config?.assignMode ?? defaultConfig.assignMode}
                onRoleSelected={tryOpenChokerWarning}
              ></BodyAssignment>
              <div className="flex justify-center">
                <Button
                  variant="secondary"
                  onClick={() => onRoleSelected(BodyPart.NONE)}
                >
                  {l10n.getString('body_assignment_menu-unassign_tracker')}
                </Button>
              </div>
            </div>
          </div>
        </div>
      </ReactModal>

      <NeckWarningModal
        isOpen={shouldShowChokerWarn}
        overlayClassName={classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full justify-center bg-background-90 bg-opacity-90 z-20'
        )}
        onClose={() => closeChokerWarning(true)}
        accept={() => closeChokerWarning(false)}
      ></NeckWarningModal>
    </>
  );
}
