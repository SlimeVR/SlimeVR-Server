import classNames from 'classnames';
import ReactModal from 'react-modal';
import { BodyPart } from 'solarxr-protocol';
import { FlatDeviceTracker } from '@/hooks/app';
import { useTrackers } from '@/hooks/tracker';
import { Button } from '@/components/commons/Button';
import { TipBox } from '@/components/commons/TipBox';
import { Typography } from '@/components/commons/Typography';
import { TrackerCard } from '@/components/tracker/TrackerCard';
import { useLocalization } from '@fluent/react';

export function TrackerSelectionMenu({
  isOpen = true,
  onClose,
  onTrackerSelected,
  bodyPart,
}: {
  isOpen: boolean;
  bodyPart: BodyPart;
  onClose: () => void;
  onTrackerSelected: (tracker: FlatDeviceTracker | null) => void;
}) {
  const { l10n } = useLocalization();
  const { useAssignedTrackers, useUnassignedTrackers } = useTrackers();

  const unassignedTrackers = useUnassignedTrackers();
  const assignedTrackers = useAssignedTrackers();

  return (
    <>
      <ReactModal
        isOpen={isOpen}
        shouldCloseOnOverlayClick
        shouldCloseOnEsc
        onRequestClose={onClose}
        overlayClassName={classNames(
          'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full bg-background-90 bg-opacity-90 z-20'
        )}
        className={classNames(
          'focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent outline-none z-10 h-full pt-10'
        )}
      >
        <div className="flex w-full h-full flex-col px-4">
          <div className="flex w-full flex-col flex-grow h-0 items-center gap-3 py-4">
            <Typography variant="main-title" bold>
              {l10n.getString('tracker_selection_menu-' + BodyPart[bodyPart])}
            </Typography>
            <div className="w-full max-w-sm">
              <TipBox>{l10n.getString('tips-tap_setup')}</TipBox>
            </div>
            <div className="w-full p-2 flex flex-col flex-grow h-0 gap-4 overflow-y-auto">
              {unassignedTrackers.length && (
                <>
                  <Typography>
                    {l10n.getString('tracker_selection_menu-unassigned')}
                  </Typography>
                  <div className="grid xs:grid-cols-2 mobile:grid-cols-1 gap-3">
                    {unassignedTrackers.map((fd, index) => (
                      <TrackerCard
                        key={index}
                        tracker={fd.tracker}
                        device={fd.device}
                        onClick={() => onTrackerSelected(fd)}
                        smol
                        interactable
                        outlined={
                          bodyPart ===
                          (fd.tracker.info?.bodyPart || BodyPart.NONE)
                        }
                      ></TrackerCard>
                    ))}
                  </div>
                </>
              )}
              <Typography>
                {l10n.getString('tracker_selection_menu-assigned')}
              </Typography>
              <div className=" grid xs:grid-cols-2 mobile:grid-cols-1 gap-3">
                {assignedTrackers.map((fd, index) => (
                  <TrackerCard
                    key={index}
                    tracker={fd.tracker}
                    device={fd.device}
                    onClick={() => onTrackerSelected(fd)}
                    smol
                    interactable
                    outlined={
                      bodyPart === (fd.tracker.info?.bodyPart || BodyPart.NONE)
                    }
                  ></TrackerCard>
                ))}
              </div>
            </div>
            <div className="flex w-full justify-end" onClick={onClose}>
              <div className="flex flex-col justify-end pointer-events-auto">
                <Button
                  variant="primary"
                  onClick={() => onTrackerSelected(null)}
                >
                  {l10n.getString('tracker_selection_menu-dont_assign')}
                </Button>
              </div>
            </div>
          </div>
        </div>
      </ReactModal>
    </>
  );
}
