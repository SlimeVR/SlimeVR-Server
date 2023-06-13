import classNames from 'classnames';
import { MouseEventHandler } from 'react';
import ReactModal from 'react-modal';
import { useElemSize, useLayout } from '../../../../hooks/layout';
import { Button } from '../../../commons/Button';
import { AnkleIcon } from '../../../commons/icon/AnkleIcon';
import { Typography } from '../../../commons/Typography';
import { rotationToQuatMap } from '../../../tracker/TrackerSettings';
import { useLocalization } from '@fluent/react';

function MoutingOrientationCard({
  orientation,
  onClick,
}: {
  orientation: string;
  onClick?: MouseEventHandler<HTMLDivElement>;
}) {
  // FIXME: Dont use AnkleIcon for this please
  return (
    <div
      onClick={onClick}
      className="xs:h-32 mobile:h-20 bg-background-60 rounded-md flex justify-between p-4 hover:bg-background-50"
    >
      <div className="flex flex-col justify-center">
        <Typography variant="main-title">{orientation}</Typography>
      </div>
      <div className="flex flex-col justify-center fill-white">
        <AnkleIcon width={58}></AnkleIcon>
      </div>
    </div>
  );
}

export function MountingSelectionMenu({
  isOpen = true,
  onClose,
  onDirectionSelected,
}: {
  isOpen: boolean;
  onClose: () => void;
  onDirectionSelected: (direction: number) => void;
}) {
  const { l10n } = useLocalization();
  const { ref: refTrackers, layoutHeight: trackersHeight } =
    useLayout<HTMLDivElement>();
  const { ref: refOptions, height: optionsHeight } =
    useElemSize<HTMLDivElement>();

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
        'focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent outline-none mt-20 z-10 px-2'
      )}
    >
      <div className="flex w-full h-full flex-col ">
        <Typography variant="main-title" bold>
          {l10n.getString('mounting_selection_menu')}
        </Typography>
        <div
          className="flex w-full flex-col flex-grow items-center gap-3 xs:justify-center"
          ref={refTrackers}
          style={{ height: trackersHeight - optionsHeight }}
        >
          <div className="grid xs:grid-cols-2 xs:grid-rows-2 mobile:grid-cols-1 gap-6 w-full">
            <MoutingOrientationCard
              orientation={l10n.getString('tracker-rotation-left')}
              onClick={() => onDirectionSelected(rotationToQuatMap.LEFT)}
            />
            <MoutingOrientationCard
              orientation={l10n.getString('tracker-rotation-right')}
              onClick={() => onDirectionSelected(rotationToQuatMap.RIGHT)}
            />
            <MoutingOrientationCard
              orientation={l10n.getString('tracker-rotation-front')}
              onClick={() => onDirectionSelected(rotationToQuatMap.FRONT)}
            />
            <MoutingOrientationCard
              orientation={l10n.getString('tracker-rotation-back')}
              onClick={() => onDirectionSelected(rotationToQuatMap.BACK)}
            />
          </div>
        </div>
      </div>
      <div
        className="flex w-full justify-between absolute bottom-0 left-0 p-10 z-0"
        onClick={onClose}
        ref={refOptions}
      >
        <div className="flex flex-col justify-end pointer-events-auto">
          <Button variant="primary" onClick={onClose}>
            {l10n.getString('mounting_selection_menu-close')}
          </Button>
        </div>
      </div>
    </ReactModal>
  );
}
