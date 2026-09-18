import classNames from 'classnames';
import { KeyboardEvent, useEffect, useState } from 'react';
import { Button } from '@/components/commons/Button';
import { BaseModal } from '@/components/commons/BaseModal';
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
import { a11yClick, stepKeys } from '@/utils/a11y';
import { useNavAim } from '@/hooks/controller-nav';

const FINGERS = new Set(FINGER_BODY_PARTS);
const LEFT_TOES = new Set(
  TOE_BODY_PARTS.filter((part) => BodyPart[part].startsWith('LEFT_'))
);
const RIGHT_TOES = new Set(
  TOE_BODY_PARTS.filter((part) => BodyPart[part].startsWith('RIGHT_'))
);

type Wedge = {
  id: string;
  direction: Quaternion;
  d: string;
  angle: number;
  noText: boolean;
  trackerTransform: string;
  trackerWidth: number;
};

const WEDGES: Wedge[] = [
  {
    id: 'tracker-rotation-left',
    direction: rotationToQuatMap.LEFT,
    d: 'M0 0-89 44A99 99 0 0 1-89-44Z',
    angle: 270,
    noText: false,
    trackerTransform: 'translate(75, 0) scale(-1, 1)',
    trackerWidth: 10,
  },
  {
    id: 'tracker-rotation-front_left',
    direction: rotationToQuatMap.FRONT_LEFT,
    d: 'M0 0-89-44A99 99 0 0 1-44-89Z',
    angle: 315,
    noText: true,
    trackerTransform: 'translate(-2, 175) rotate(-135)',
    trackerWidth: 7,
  },
  {
    id: 'tracker-rotation-front',
    direction: rotationToQuatMap.FRONT,
    d: 'M0 0-44-89A99 99 0 0 1 44-89Z',
    angle: 0,
    noText: false,
    trackerTransform: 'translate(0, 75) rotate(-90)',
    trackerWidth: 10,
  },
  {
    id: 'tracker-rotation-front_right',
    direction: rotationToQuatMap.FRONT_RIGHT,
    d: 'M0 0 44-89A99 99 0 0 1 89-44Z',
    angle: 45,
    noText: true,
    trackerTransform: 'translate(73, 0) rotate(-45)',
    trackerWidth: 7,
  },
  {
    id: 'tracker-rotation-right',
    direction: rotationToQuatMap.RIGHT,
    d: 'M0 0 89-44A99 99 0 0 1 89 44Z',
    angle: 90,
    noText: false,
    trackerTransform: 'translate(175,0)',
    trackerWidth: 10,
  },
  {
    id: 'tracker-rotation-back_right',
    direction: rotationToQuatMap.BACK_RIGHT,
    d: 'M0 0 89 44A99 99 0 0 1 44 89Z',
    angle: 135,
    noText: true,
    trackerTransform: 'translate(252, 75) rotate(45)',
    trackerWidth: 7,
  },
  {
    id: 'tracker-rotation-back',
    direction: rotationToQuatMap.BACK,
    d: 'M0 0 44 89A99 99 0 0 1-44 89Z',
    angle: 180,
    noText: false,
    trackerTransform: 'translate(250, 175) rotate(90)',
    trackerWidth: 10,
  },
  {
    id: 'tracker-rotation-back_left',
    direction: rotationToQuatMap.BACK_LEFT,
    d: 'M0 0-44 89A99 99 0 0 1-89 44Z',
    angle: 225,
    noText: true,
    trackerTransform: 'translate(177, 250) rotate(135)',
    trackerWidth: 7,
  },
];

const FRONT_INDEX = WEDGES.findIndex((w) => w.angle === 0);

const wedgeOptionId = (i: number) => `mounting-wedge-${i}`;

function angleGap(a: number, b: number): number {
  const diff = Math.abs(a - b) % 360;
  return diff > 180 ? 360 - diff : diff;
}

function nearestWedge(bearing: number): number {
  let best = 0;
  let bestGap = Infinity;
  WEDGES.forEach((w, i) => {
    const gap = angleGap(bearing, w.angle);
    if (gap < bestGap) {
      bestGap = gap;
      best = i;
    }
  });
  return best;
}

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
  wedge,
  optionId,
  onDirectionSelected,
  selected,
  focused,
}: {
  wedge: Wedge;
  optionId: string;
  onDirectionSelected: (direction: Quaternion) => void;
  selected: boolean;
  focused: boolean;
}) {
  const { l10n } = useLocalization();
  const { id, d, direction, noText, trackerTransform, trackerWidth } = wedge;

  return (
    <g
      id={optionId}
      role="option"
      aria-selected={selected}
      aria-label={l10n.getString(id)}
      onClick={() => onDirectionSelected(direction)}
      className={classNames('group fill-background-10 stroke-background-10')}
    >
      <path
        d={d}
        className={classNames(
          'opacity-50 stroke-background-90 group-hover:fill-background-30',
          'group-active:fill-background-20',
          focused ? 'fill-background-30' : 'fill-background-40'
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
          focused
            ? 'fill-accent-background-20'
            : selected
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
  const [cursor, setCursor] = useState(FRONT_INDEX);

  const appliedIndex = currRotation
    ? WEDGES.findIndex((w) => similarQuaternions(currRotation, w.direction))
    : -1;

  useEffect(() => {
    if (isOpen) setCursor(appliedIndex >= 0 ? appliedIndex : FRONT_INDEX);
  }, [isOpen]);

  useNavAim(isOpen, (bearing) => setCursor(nearestWedge(bearing)));

  const step = stepKeys({
    value: cursor,
    min: 0,
    max: WEDGES.length - 1,
    step: (v, add) => (v + (add ? 1 : WEDGES.length - 1)) % WEDGES.length,
    bigStep: (v, add) => (v + (add ? 2 : WEDGES.length - 2)) % WEDGES.length,
    onChange: setCursor,
  });

  const onKeyDown = (e: KeyboardEvent) => {
    if (a11yClick(e)) {
      e.preventDefault();
      onDirectionSelected(WEDGES[cursor].direction);
      return;
    }
    step(e);
  };

  return (
    <BaseModal
      isOpen={isOpen}
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
          <div
            role="listbox"
            tabIndex={-1}
            data-nav-entry
            data-nav-arrows
            data-nav-aim
            aria-label={l10n.getString('mounting_selection_menu')}
            aria-activedescendant={wedgeOptionId(cursor)}
            onKeyDown={onKeyDown}
            className="rounded-lg outline-none"
          >
            <svg
              width="400"
              viewBox="0 0 250 250"
              className="fill-background-40"
            >
              <g transform="translate(80, 0)" className="fill-background-10">
                <MountingBodyPartIcon width={100} bodyPart={bodyPart} />
              </g>
              <g strokeWidth="4" className="stroke-background-90">
                {WEDGES.map((wedge, i) => (
                  <PieSliceOfFeet
                    key={wedge.id}
                    wedge={wedge}
                    optionId={wedgeOptionId(i)}
                    onDirectionSelected={onDirectionSelected}
                    selected={i === appliedIndex}
                    focused={i === cursor}
                  />
                ))}
              </g>
            </svg>
          </div>
        </div>
      </div>
      <div className="flex w-full justify-between absolute bottom-0 left-0 p-10 z-0">
        <div className="flex flex-col justify-end pointer-events-auto">
          <Button variant="primary" onClick={onClose}>
            {l10n.getString('mounting_selection_menu-close')}
          </Button>
        </div>
      </div>
    </BaseModal>
  );
}
