import classNames from 'classnames';
import { MouseEventHandler } from 'react';
import ReactModal from 'react-modal';
import { useElemSize, useLayout } from '../../../../hooks/layout';
import { Button } from '../../../commons/Button';
import { Typography } from '../../../commons/Typography';
import { useLocalization } from '@fluent/react';
import { FootIcon } from '../../../commons/icon/FootIcon';
import { rotationToQuatMap } from '../../../../maths/quaternion';
import { Quaternion } from 'three';

function PieSliceOfFeet({
  onClick,
  id,
  d,
  noText = false,
}: {
  onClick?: MouseEventHandler<SVGGElement>;
  id: string;
  d: string;
  noText?: boolean;
}) {
  const { l10n } = useLocalization();

  return (
    <g
      onClick={onClick}
      className={classNames('fill-background-10 stroke-background-10')}
    >
      <path
        d={d}
        className={classNames(
          'fill-background-40 opacity-50 stroke-background-90',
          'hover:fill-background-30 active:fill-background-20'
        )}
        transform="translate(125 125)"
        id={id}
      ></path>
      <text strokeWidth="1">
        <textPath xlinkHref={`#${id}`} startOffset="50%" textAnchor="middle">
          {!noText ? l10n.getString(id) : ''}
        </textPath>
      </text>
    </g>
  );
}

export function MountingSelectionMenu({
  isOpen = true,
  onClose,
  onDirectionSelected,
}: {
  isOpen: boolean;
  onClose: () => void;
  onDirectionSelected: (direction: Quaternion) => void;
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
          <div className="flex justify-center items-center gap-6 w-full">
            <svg
              width="400"
              viewBox="0 0 250 250"
              className="fill-background-40"
            >
              <g transform="translate(80, 0)" className="fill-background-10">
                <FootIcon width={100} />
              </g>
              <g strokeWidth="4" className="stroke-background-90">
                <PieSliceOfFeet
                  d="M0 0-89 44A99 99 0 0 1-89-44Z"
                  onClick={() => onDirectionSelected(rotationToQuatMap.LEFT)}
                  id="tracker-rotation-left"
                ></PieSliceOfFeet>
                <PieSliceOfFeet
                  d="M0 0-89-44A99 99 0 0 1-44-89Z"
                  onClick={() =>
                    onDirectionSelected(rotationToQuatMap.LEFT_FRONT)
                  }
                  id="tracker-rotation_left_front"
                  noText={true}
                ></PieSliceOfFeet>
                <PieSliceOfFeet
                  onClick={() => onDirectionSelected(rotationToQuatMap.FRONT)}
                  d="M0 0-44-89A99 99 0 0 1 44-89Z"
                  id="tracker-rotation-front"
                ></PieSliceOfFeet>
                <PieSliceOfFeet
                  d="M0 0 44-89A99 99 0 0 1 89-44Z"
                  onClick={() =>
                    onDirectionSelected(rotationToQuatMap.RIGHT_FRONT)
                  }
                  id="tracker-rotation-front_right"
                  noText={true}
                ></PieSliceOfFeet>
                <PieSliceOfFeet
                  d="M0 0 89-44A99 99 0 0 1 89 44Z"
                  onClick={() => onDirectionSelected(rotationToQuatMap.RIGHT)}
                  id="tracker-rotation-right"
                ></PieSliceOfFeet>
                <PieSliceOfFeet
                  d="M0 0 89 44A99 99 0 0 1 44 89Z"
                  onClick={() =>
                    onDirectionSelected(rotationToQuatMap.RIGHT_BACK)
                  }
                  id="tracker-rotation-back_right"
                  noText={true}
                ></PieSliceOfFeet>
                <PieSliceOfFeet
                  d="M0 0 44 89A99 99 0 0 1-44 89Z"
                  onClick={() => onDirectionSelected(rotationToQuatMap.BACK)}
                  id="tracker-rotation-back"
                ></PieSliceOfFeet>
                <PieSliceOfFeet
                  d="M0 0-44 89A99 99 0 0 1-89 44Z"
                  onClick={() =>
                    onDirectionSelected(rotationToQuatMap.RIGHT_BACK)
                  }
                  id="tracker-rotation-back_right"
                  noText={true}
                ></PieSliceOfFeet>
              </g>
            </svg>
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
