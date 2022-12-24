import classNames from 'classnames';
import { useTranslation } from 'react-i18next';
import ReactModal from 'react-modal';
import { BodyPart } from 'solarxr-protocol';
import { FlatDeviceTracker } from '../../../../hooks/app';
import { useElemSize, useLayout } from '../../../../hooks/layout';
import { useTrackers } from '../../../../hooks/tracker';
import { Button } from '../../../commons/Button';
import { TipBox } from '../../../commons/TipBox';
import { Typography } from '../../../commons/Typography';
import { TrackerCard } from '../../../tracker/TrackerCard';

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
  const { t } = useTranslation();
  const { ref: refTrackers, layoutHeight: trackersHeight } =
    useLayout<HTMLDivElement>();
  const { ref: refOptions, height: optionsHeight } =
    useElemSize<HTMLDivElement>();

  const { useAssignedTrackers, useUnassignedTrackers } = useTrackers();

  const unassignedTrackers = useUnassignedTrackers();
  const assignedTrackers = useAssignedTrackers();

  return (
    <ReactModal
      isOpen={isOpen}
      shouldCloseOnOverlayClick
      shouldCloseOnEsc
      onRequestClose={onClose}
      overlayClassName={classNames(
        'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full bg-black bg-opacity-90 z-20'
      )}
      className={classNames(
        'focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent outline-none mt-20 z-10'
      )}
    >
      <div className="flex w-full h-full flex-col ">
        <div className="flex w-full flex-col flex-grow items-center gap-3">
          <Typography variant="main-title" bold>
            {t('tracker_selection_menu' + BodyPart[bodyPart])}
          </Typography>
          <div className="relative">
            <div
              className="w-full h-full min-w-[700px] overflow-y-auto p-2 flex flex-col gap-6"
              ref={refTrackers}
              style={{ height: trackersHeight - optionsHeight }}
            >
              <div className="flex flex-col gap-3">
                {unassignedTrackers.length && (
                  <div className="flex flex-col gap-3">
                    <Typography>
                      {t('tracker_selection_menu-unassigned')}
                    </Typography>
                    <div className="grid grid-cols-2 gap-3">
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
                  </div>
                )}
                <Typography>{t('tracker_selection_menu-assigned')}</Typography>
                <div className=" grid grid-cols-2 gap-3">
                  {assignedTrackers.map((fd, index) => (
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
              </div>
            </div>
            <div className="absolute px-2 pr-4 bottom-0 h-10 w-full border-b-[1px] border-background-40">
              <div className="w-full h-full bg-gradient-to-b from-transparent to-black opacity-50"></div>
            </div>
          </div>
        </div>
      </div>
      <div
        className="flex w-full justify-between absolute bottom-0 left-0 p-10 z-0"
        onClick={onClose}
        ref={refOptions}
      >
        <div className="w-full max-w-sm">
          <TipBox>{t('tips-find_tracker')}</TipBox>
        </div>
        <div className="flex flex-col justify-end pointer-events-auto">
          <Button variant="primary" onClick={() => onTrackerSelected(null)}>
            {t('tracker_selection_menu-dont_assign')}
          </Button>
        </div>
      </div>
    </ReactModal>
  );
}
