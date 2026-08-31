import classNames from 'classnames';
import ReactModal from 'react-modal';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { BodyPart } from 'solarxr-protocol';
import { Button } from '@/components/commons/Button';
import { CheckBox } from '@/components/commons/Checkbox';
import { Typography } from '@/components/commons/Typography';
import { BodyAssignment } from '@/components/onboarding/BodyAssignment';
import { useLocalization } from '@fluent/react';
import { NeckWarningModal } from '@/components/onboarding/NeckWarningModal';
import { useChokerWarning } from '@/hooks/choker-warning';
import { AssignMode, defaultConfig, useConfig } from '@/hooks/config';
import { useBreakpoint } from '@/hooks/breakpoint';

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
  const { config, setConfig } = useConfig();
  const { isMobile } = useBreakpoint('mobile');

  const { control, watch } = useForm<{ showAllBodyParts: boolean }>({
    defaultValues: {
      showAllBodyParts: config?.assignShowAllBodyParts ?? false,
    },
  });
  const { showAllBodyParts } = watch();

  useEffect(() => {
    setConfig({ assignShowAllBodyParts: showAllBodyParts });
  }, [showAllBodyParts]);

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
              <CheckBox
                control={control}
                label={l10n.getString('onboarding-assign_trackers-show_all')}
                name="showAllBodyParts"
                variant="toggle"
              />
            </div>
            <div className="flex flex-col xs:flex-grow gap-3 rounded-xl fill-background-50 py-2">
              <BodyAssignment
                mirror={config?.mirrorView ?? defaultConfig.mirrorView}
                width={isMobile ? 160 : undefined}
                onlyAssigned={false}
                /* FIXME: need to use the right stuff */
                assignMode={AssignMode.All}
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
