import classNames from 'classnames';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { FootIcon } from '@/components/commons/icon/FootIcon';
import { rotationToQuatMap, similarQuaternions } from '@/maths/quaternion';
import { Quaternion } from 'three';
import { SlimeUpIcon } from '@/components/commons/icon/SlimeUpIcon';
import { BodyPart } from 'solarxr-protocol';
import { useLocaleConfig } from '@/i18n/config';
import { FingersIcon } from '@/components/commons/icon/FingersIcon';
import {
  renderFootLeft,
  renderFootRight,
} from '@/components/commons/BodyPartIcon';
import { FINGER_BODY_PARTS, TOE_BODY_PARTS } from '@/hooks/body-parts';
import ReactModal from 'react-modal';

const FINGERS = new Set(FINGER_BODY_PARTS);
const LEFT_TOES = new Set(
  TOE_BODY_PARTS.filter((part) => BodyPart[part].startsWith('LEFT_'))
);
const RIGHT_TOES = new Set(
  TOE_BODY_PARTS.filter((part) => BodyPart[part].startsWith('RIGHT_'))
);

export function MountingBodyPartIcon({
  bodyPart = BodyPart.NONE,
  width = 24,
}: {
  bodyPart?: BodyPart;
  width?: number;
}) {
  const { currentLocales } = useLocaleConfig();

  if (FINGERS.has(bodyPart)) return <FingersIcon width={width} />;
  if (LEFT_TOES.has(bodyPart)) return renderFootLeft({ width, currentLocales });
  if (RIGHT_TOES.has(bodyPart))
    return renderFootRight({ width, currentLocales });
  return <FootIcon width={width} />;
}

function PieSliceOfFeet({
  direction,
  onDirectionSelected,
  currRotation,
  id,
  d,
  noText = false,
  trackerTransform,
  trackerWidth = 10,
}: {
  direction: Quaternion;
  onDirectionSelected: (direction: Quaternion) => void;
  currRotation?: Quaternion;
  id: string;
  d: string;
  noText?: boolean;
  trackerTransform: string;
  trackerWidth?: number;
}) {
  const { l10n } = useLocalization();

  return (
    <g
      onClick={() => onDirectionSelected(direction)}
      className={classNames('group fill-background-10 stroke-background-10')}
    >
      <path
        d={d}
        className={classNames(
          'fill-background-40 opacity-50 stroke-background-90',
          'group-hover:fill-background-30 group-active:fill-background-20'
        )}
        transform="translate(125 125)"
        id={id}
      />
      <text dy="-5" strokeWidth="1">
        <textPath xlinkHref={`#${id}`} startOffset="50%" textAnchor="middle">
          {!noText ? l10n.getString(id) : ''}
        </textPath>
      </text>
      <g
        transform={trackerTransform}
        className={classNames(
          'stroke-none group-hover:fill-accent-background-20',
          currRotation && similarQuaternions(currRotation, direction)
            ? 'fill-background-90'
            : 'fill-none'
        )}
      >
        <SlimeUpIcon width={trackerWidth} />
      </g>
    </g>
  );
}

export function MountingSelectionMenu({
  isOpen = true,
  onClose,
  onDirectionSelected,
  bodyPart,
  currRotation,
}: {
  isOpen: boolean;
  onClose: () => void;
  onDirectionSelected: (direction: Quaternion) => void;
  bodyPart?: BodyPart;
  currRotation?: Quaternion;
}) {
  const { l10n } = useLocalization();

  return (
    <ReactModal
      isOpen={isOpen}
      shouldCloseOnOverlayClick
      shouldCloseOnEsc
      onRequestClose={onClose}
      overlayClassName={classNames(
        'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full bg-background-90 bg-opacity-90 z-50'
      )}
      className={classNames(
        'focus:ring-transparent focus:ring-offset-transparent focus:outline-transparent outline-none mt-20 z-10'
      )}
    >
      <div className="flex flex-col p-4">
        <Typography variant="main-title" bold textAlign="text-center">
          {l10n.getString('mounting_selection_menu')}
        </Typography>
        <div className="flex w-full flex-col flex-grow items-center gap-3 justify-center">
          <svg width="400" viewBox="0 0 250 250" className="fill-background-40">
            <g transform="translate(80, 0)" className="fill-background-10">
              <MountingBodyPartIcon width={100} bodyPart={bodyPart} />
            </g>
            <g strokeWidth="4" className="stroke-background-90">
              <PieSliceOfFeet
                d="M0 0-89 44A99 99 0 0 1-89-44Z"
                direction={rotationToQuatMap.LEFT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-left"
                trackerTransform="translate(75, 0) scale(-1, 1)"
              />
              <PieSliceOfFeet
                d="M0 0-89-44A99 99 0 0 1-44-89Z"
                direction={rotationToQuatMap.FRONT_LEFT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation_left_front"
                noText={true}
                trackerTransform="translate(-2, 175) rotate(-135)"
                trackerWidth={7}
              />
              <PieSliceOfFeet
                d="M0 0-44-89A99 99 0 0 1 44-89Z"
                direction={rotationToQuatMap.FRONT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-front"
                trackerTransform="translate(0, 75) rotate(-90)"
              />
              <PieSliceOfFeet
                d="M0 0 44-89A99 99 0 0 1 89-44Z"
                direction={rotationToQuatMap.FRONT_RIGHT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-front_right"
                noText={true}
                trackerTransform="translate(73, 0) rotate(-45)"
                trackerWidth={7}
              />
              <PieSliceOfFeet
                d="M0 0 89-44A99 99 0 0 1 89 44Z"
                direction={rotationToQuatMap.RIGHT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-right"
                trackerTransform="translate(175,0)"
              />
              <PieSliceOfFeet
                d="M0 0 89 44A99 99 0 0 1 44 89Z"
                direction={rotationToQuatMap.BACK_RIGHT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-back_right"
                noText={true}
                trackerTransform="translate(252, 75) rotate(45)"
                trackerWidth={7}
              />
              <PieSliceOfFeet
                d="M0 0 44 89A99 99 0 0 1-44 89Z"
                direction={rotationToQuatMap.BACK}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-back"
                trackerTransform="translate(250, 175) rotate(90)"
              />
              <PieSliceOfFeet
                d="M0 0-44 89A99 99 0 0 1-89 44Z"
                direction={rotationToQuatMap.BACK_LEFT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-back_left"
                noText={true}
                trackerTransform="translate(177, 250) rotate(135)"
                trackerWidth={7}
              />
            </g>
          </svg>
        </div>
      </div>
      <div
        className="flex w-full justify-between absolute bottom-0 left-0 p-10 z-0"
        onClick={onClose}
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
