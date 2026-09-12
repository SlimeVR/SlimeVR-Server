import classNames from 'classnames';
import { useEffect, useRef } from 'react';
import { BodyPart } from 'solarxr-protocol';
import { BaseModal } from '@/components/commons/BaseModal';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { CrossIcon } from '@/components/commons/icon/CrossIcon';
import {
  MirrorLegend,
  ShowAllPartsToggle,
} from '@/components/onboarding/BodyAssignment';
import { BodyPartAssignment } from '@/components/onboarding/BodyPartAssignment';
import {
  ExtremitySideToggle,
  PickerTabs,
} from '@/components/onboarding/pages/trackers-assign/BodyAssignmentPanel';
import { useLocalization } from '@fluent/react';
import { NeckWarningModal } from '@/components/onboarding/NeckWarningModal';
import { useChokerWarning } from '@/hooks/choker-warning';
import { useConfig } from '@/hooks/config';
import { useBreakpoint } from '@/hooks/breakpoint';
import {
  getPickerSelection,
  PICKER_TABS,
  PickerContext,
  providePicker,
} from '@/hooks/tracker-picker';

export function SingleTrackerBodyAssignmentMenu({
  isOpen,
  onClose,
  onRoleSelected,
  bodyPart,
}: {
  isOpen: boolean;
  onClose: () => void;
  onRoleSelected: (role: BodyPart) => void;
  bodyPart?: BodyPart;
}) {
  const { l10n } = useLocalization();
  const { config } = useConfig();
  const pickerShell = providePicker();
  const { isMobileAssign } = useBreakpoint('mobileAssign');
  const isMobile = isMobileAssign;
  const selectionRef = useRef<HTMLDivElement>(null);

  const { closeChokerWarning, tryOpenChokerWarning, shouldShowChokerWarn } =
    useChokerWarning({
      next: onRoleSelected,
    });

  const picker = {
    ...pickerShell,
    activePart: bodyPart ?? BodyPart.NONE,
    selectPart: tryOpenChokerWarning,
  };

  const activeParts =
    bodyPart != null && bodyPart !== BodyPart.NONE ? [bodyPart] : [];
  const dotClass = (part: BodyPart) =>
    part === bodyPart
      ? 'scale-150 ring-3 ring-accent-background-30'
      : undefined;

  useEffect(() => {
    if (!isOpen) return;
    const selection = getPickerSelection(bodyPart);
    pickerShell.setTab(selection.tab);
    pickerShell.setSide(selection.side);
  }, [isOpen, bodyPart, pickerShell.setTab, pickerShell.setSide]);

  const { view, dotSize } = PICKER_TABS[picker.tab];
  const sideControl =
    view.kind === 'extremity' ? (
      <ExtremitySideToggle
        compact
        descriptor={view.descriptor}
        side={picker.side}
        onChange={picker.setSide}
      />
    ) : (
      <MirrorLegend compact />
    );

  return (
    <>
      <BaseModal
        isOpen={isOpen}
        onRequestClose={onClose}
        aria={{ labelledby: 'single-tracker-assign-title' }}
        overlayClassName={classNames(
          'fixed inset-0 flex bg-background-90 bg-opacity-90 z-20'
        )}
        className={classNames(
          'focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent',
          'relative h-full w-full outline-none z-10 text-background-10',
          'overflow-hidden'
        )}
      >
        <div
          className={classNames(
            'grid min-w-0',
            isMobile
              ? 'h-full min-h-0 grid-cols-1 grid-rows-[auto_minmax(0,1fr)] gap-3 overflow-hidden px-1 pt-[calc(var(--topbar-h)+0.5rem)]'
              : 'h-full min-h-0 grid-rows-1 grid-cols-[minmax(15rem,0.7fr)_minmax(0,1.5fr)] gap-8 overflow-hidden p-7'
          )}
        >
          <div
            className={classNames(
              'flex min-h-0 min-w-0 flex-col',
              isMobile ? 'gap-2' : 'gap-5 overflow-y-auto pt-3'
            )}
          >
            <div className="flex min-w-0 flex-col gap-3">
              <div className="flex items-center gap-2">
                <div
                  id="single-tracker-assign-title"
                  className="min-w-0 flex-1 [overflow-wrap:anywhere]"
                >
                  <Typography
                    variant={isMobile ? 'section-title' : 'main-title'}
                    bold
                  >
                    {l10n.getString('body_assignment_menu')}
                  </Typography>
                </div>
                <button
                  type="button"
                  className={classNames(
                    'flex h-10 w-10 shrink-0 items-center justify-center rounded-md fill-background-10 hover:bg-background-50 focus-visible:ring-2 focus-visible:ring-background-20',
                    !isMobile &&
                      'absolute right-7 top-[calc(var(--topbar-h)+0.5rem)] z-10'
                  )}
                  onClick={onClose}
                  aria-label={l10n.getString('mounting_selection_menu-close')}
                  title={l10n.getString('mounting_selection_menu-close')}
                >
                  <CrossIcon size={24} />
                </button>
              </div>
              <div className="mobileAssign:hidden">
                <Typography>
                  {l10n.getString('body_assignment_menu-description')}
                </Typography>
              </div>
            </div>
            <div className="flex flex-wrap items-center gap-2">
              <Button
                variant="secondary"
                className="mobileAssign:px-3 mobileAssign:py-1.5 min-w-0 max-w-full"
                to="/onboarding/trackers-assign"
                state={{ alonePage: true }}
              >
                {l10n.getString('body_assignment_menu-manage_trackers')}
              </Button>
              <Button
                variant="secondary"
                className="mobileAssign:px-3 mobileAssign:py-1.5 min-w-0 max-w-full"
                onClick={() => onRoleSelected(BodyPart.NONE)}
                id="body_assignment_menu-unassign_tracker"
              />
            </div>
            <ShowAllPartsToggle compact={isMobile} />
          </div>
          <div
            ref={selectionRef}
            data-nav-entry
            className={classNames(
              'flex min-h-0 min-w-0 flex-col gap-3',
              isMobile ? 'h-full min-h-0' : 'h-full p-4'
            )}
          >
            {!isMobile && (
              <div className="grid grid-cols-[minmax(0,1fr)_auto_minmax(0,1fr)] items-center gap-2">
                <PickerContext.Provider value={picker}>
                  <PickerTabs compact />
                  {sideControl}
                </PickerContext.Provider>
              </div>
            )}
            <div className="flex-1 min-h-0 min-w-0">
              <PickerContext.Provider value={picker}>
                <div
                  className={classNames(
                    isMobile
                      ? 'h-full min-h-0 overflow-y-auto pb-[calc(7.5rem+env(safe-area-inset-bottom))]'
                      : view.kind === 'body'
                        ? 'h-full min-h-0 overflow-hidden'
                        : 'h-full min-h-0 overflow-y-auto'
                  )}
                >
                  <div className={classNames('h-full min-h-fit')}>
                    {view.kind === 'extremity' ? (
                      <BodyPartAssignment
                        view={view}
                        side={picker.side}
                        dotSize={dotSize.tap}
                        compact={isMobile}
                        fitContent={isMobile}
                        fillHeight
                        activeParts={activeParts}
                        dotClass={dotClass}
                        onRoleSelected={picker.selectPart}
                      />
                    ) : (
                      <BodyPartAssignment
                        view={view}
                        mirror={config?.mirrorView ?? false}
                        dotSize={dotSize.tap}
                        fillHeight
                        activeParts={activeParts}
                        dotClass={dotClass}
                        onRoleSelected={picker.selectPart}
                      />
                    )}
                  </div>
                </div>
              </PickerContext.Provider>
            </div>
          </div>
        </div>
        {isMobile && (
          <div className="pointer-events-none fixed inset-x-3 bottom-[calc(0.5rem+env(safe-area-inset-bottom))] z-20 mx-auto flex max-w-md flex-col items-center gap-2">
            <PickerContext.Provider
              value={{
                ...picker,
                setTab: (tab) => {
                  picker.setTab(tab);
                  const selection = selectionRef.current;
                  const modal = selection?.closest('[role="dialog"]');
                  if (
                    selection &&
                    modal &&
                    selection.getBoundingClientRect().top <
                      modal.getBoundingClientRect().top
                  ) {
                    selection.scrollIntoView({ block: 'start' });
                  }
                },
              }}
            >
              <div className="pointer-events-auto w-fit">{sideControl}</div>
              <PickerTabs
                compact
                className="pointer-events-auto w-full shadow-lg ring-1 ring-background-50 [&>div]:flex [&>div]:min-h-11 [&>div]:min-w-0 [&>div]:flex-1 [&>div]:items-center [&>div]:justify-center [&>div]:text-center"
              />
            </PickerContext.Provider>
          </div>
        )}
      </BaseModal>

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
