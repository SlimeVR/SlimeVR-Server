import classNames from 'classnames';
import ReactModal from 'react-modal';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { useLocalization } from '@fluent/react';
import { FootIcon } from '@/components/commons/icon/FootIcon';
import { rotationToQuatMap, similarQuaternions } from '@/maths/quaternion';
import { Quaternion } from 'three';
import { SlimeUpIcon } from '@/components/commons/icon/SlimeUpIcon';
import { BodyPart } from 'solarxr-protocol';
import { PawIcon } from '@/components/commons/icon/PawIcon';
import { useLocaleConfig } from '@/i18n/config';
import { FingersIcon } from '@/components/commons/icon/FingersIcon';

// All body parts that are right or left, are by default left!
export const mapPart: Record<
  BodyPart,
  ({
    width,
    currentLocales,
  }: {
    width?: number;
    currentLocales: string[];
  }) => JSX.Element
> = {
  [BodyPart.UPPER_CHEST]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.CHEST]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.HEAD]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.HIP]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.LEFT_HIP]: ({ width }) => <FootIcon width={width}></FootIcon>, // Unused
  [BodyPart.RIGHT_HIP]: ({ width }) => <FootIcon width={width}></FootIcon>, // Unused
  [BodyPart.LEFT_FOOT]: ({ width, currentLocales }) =>
    currentLocales.includes('en-x-owo') ? (
      <PawIcon
        width={width ? width * 0.75 : undefined}
        transform="translate(40, -50)"
      ></PawIcon>
    ) : (
      <FootIcon width={width}></FootIcon>
    ),
  [BodyPart.LEFT_HAND]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.LEFT_LOWER_ARM]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.LEFT_LOWER_LEG]: ({ width, currentLocales }) =>
    currentLocales.includes('en-x-owo') ? (
      <PawIcon
        width={width ? width * 0.75 : undefined}
        transform="translate(40, -50)"
      ></PawIcon>
    ) : (
      <FootIcon width={width}></FootIcon>
    ),
  [BodyPart.LEFT_SHOULDER]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.LEFT_UPPER_ARM]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.LEFT_UPPER_LEG]: ({ width, currentLocales }) =>
    currentLocales.includes('en-x-owo') ? (
      <PawIcon
        width={width ? width * 0.75 : undefined}
        transform="translate(40, -50)"
      ></PawIcon>
    ) : (
      <FootIcon width={width}></FootIcon>
    ),
  [BodyPart.NECK]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.NONE]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.RIGHT_FOOT]: ({ width, currentLocales }) =>
    currentLocales.includes('en-x-owo') ? (
      <PawIcon
        width={width ? width * 0.75 : undefined}
        transform="translate(40, -50)"
      ></PawIcon>
    ) : (
      <FootIcon width={width} flipped></FootIcon>
    ),
  [BodyPart.RIGHT_HAND]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.RIGHT_LOWER_ARM]: ({ width }) => (
    <FootIcon width={width}></FootIcon>
  ),
  [BodyPart.RIGHT_LOWER_LEG]: ({ width, currentLocales }) =>
    currentLocales.includes('en-x-owo') ? (
      <PawIcon
        width={width ? width * 0.75 : undefined}
        transform="translate(40, -50)"
      ></PawIcon>
    ) : (
      <FootIcon width={width} flipped></FootIcon>
    ),
  [BodyPart.RIGHT_SHOULDER]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.RIGHT_UPPER_ARM]: ({ width }) => (
    <FootIcon width={width}></FootIcon>
  ),
  [BodyPart.RIGHT_UPPER_LEG]: ({ width, currentLocales }) =>
    currentLocales.includes('en-x-owo') ? (
      <PawIcon
        width={width ? width * 0.75 : undefined}
        transform="translate(40, -50)"
      ></PawIcon>
    ) : (
      <FootIcon width={width} flipped></FootIcon>
    ),
  [BodyPart.WAIST]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.LEFT_THUMB_METACARPAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_THUMB_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_THUMB_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_INDEX_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_INDEX_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_INDEX_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_MIDDLE_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_MIDDLE_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_MIDDLE_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_RING_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_RING_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_RING_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_LITTLE_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_LITTLE_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.LEFT_LITTLE_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_THUMB_METACARPAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_THUMB_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_THUMB_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_INDEX_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_INDEX_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_INDEX_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_MIDDLE_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_MIDDLE_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_MIDDLE_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_RING_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_RING_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_RING_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_LITTLE_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_LITTLE_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
  [BodyPart.RIGHT_LITTLE_DISTAL]: ({ width }) => (
    <FingersIcon width={width}></FingersIcon>
  ),
};

export function MountingBodyPartIcon({
  bodyPart = BodyPart.NONE,
  width = 24,
}: {
  bodyPart?: BodyPart;
  width?: number;
}) {
  const { currentLocales } = useLocaleConfig();
  return mapPart[bodyPart]({ width, currentLocales });
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
      ></path>
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
        <SlimeUpIcon width={trackerWidth}></SlimeUpIcon>
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
        'fixed top-0 right-0 left-0 bottom-0 flex flex-col items-center w-full h-full bg-background-90 bg-opacity-90 z-20'
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
              ></PieSliceOfFeet>
              <PieSliceOfFeet
                d="M0 0-89-44A99 99 0 0 1-44-89Z"
                direction={rotationToQuatMap.FRONT_LEFT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation_left_front"
                noText={true}
                trackerTransform="translate(-2, 175) rotate(-135)"
                trackerWidth={7}
              ></PieSliceOfFeet>
              <PieSliceOfFeet
                d="M0 0-44-89A99 99 0 0 1 44-89Z"
                direction={rotationToQuatMap.FRONT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-front"
                trackerTransform="translate(0, 75) rotate(-90)"
              ></PieSliceOfFeet>
              <PieSliceOfFeet
                d="M0 0 44-89A99 99 0 0 1 89-44Z"
                direction={rotationToQuatMap.FRONT_RIGHT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-front_right"
                noText={true}
                trackerTransform="translate(73, 0) rotate(-45)"
                trackerWidth={7}
              ></PieSliceOfFeet>
              <PieSliceOfFeet
                d="M0 0 89-44A99 99 0 0 1 89 44Z"
                direction={rotationToQuatMap.RIGHT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-right"
                trackerTransform="translate(175,0)"
              ></PieSliceOfFeet>
              <PieSliceOfFeet
                d="M0 0 89 44A99 99 0 0 1 44 89Z"
                direction={rotationToQuatMap.BACK_RIGHT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-back_right"
                noText={true}
                trackerTransform="translate(252, 75) rotate(45)"
                trackerWidth={7}
              ></PieSliceOfFeet>
              <PieSliceOfFeet
                d="M0 0 44 89A99 99 0 0 1-44 89Z"
                direction={rotationToQuatMap.BACK}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-back"
                trackerTransform="translate(250, 175) rotate(90)"
              ></PieSliceOfFeet>
              <PieSliceOfFeet
                d="M0 0-44 89A99 99 0 0 1-89 44Z"
                direction={rotationToQuatMap.BACK_LEFT}
                onDirectionSelected={onDirectionSelected}
                currRotation={currRotation}
                id="tracker-rotation-back_left"
                noText={true}
                trackerTransform="translate(177, 250) rotate(135)"
                trackerWidth={7}
              ></PieSliceOfFeet>
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
