import classNames from 'classnames';
import ReactModal from 'react-modal';
import { BodyPart } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import {
  BodyAssignment,
  MirrorLegend,
  ShowAllPartsToggle,
} from '@/components/onboarding/BodyAssignment';
import { useLocalization } from '@fluent/react';
import { NeckWarningModal } from '@/components/onboarding/NeckWarningModal';
import { useChokerWarning } from '@/hooks/choker-warning';
import { defaultConfig, useConfig } from '@/hooks/config';

export function SingleTrackerBodyAssignmentMenu({
  isOpen,
  onClose,
  onRoleSelected,
}: {
  isOpen: boolean;
  onClose: () => void;
  onRoleSelected: (role: BodyPart) => void;
}) {
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
          <div className="flex flex-col h-full items-center xsAssign:flex-row xsAssign:gap-8 xsAssign:justify-center">
            <div className="flex flex-col gap-4 xsAssign:max-w-sm">
              <Typography variant="mobile-title" bold>
                {l10n.getString('body_assignment_menu')}
              </Typography>
              <Typography>
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
              <ShowAllPartsToggle />
            </div>
            <div className="flex flex-col gap-4 rounded-xl fill-background-50 py-2 xsAssign:flex-grow">
              <div className="flex justify-center">
                <MirrorLegend />
              </div>
              <BodyAssignment
                mirror={config?.mirrorView ?? defaultConfig.mirrorView}
                onRoleSelected={tryOpenChokerWarning}
              />
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
      />
    </>
  );
}
