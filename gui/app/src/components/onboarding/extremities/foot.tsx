import { BodyPart } from 'solarxr-protocol';
import { ExtremityDescriptor } from '@/utils/extremities';
import {
  Digit,
  DigitColumn,
  DigitRow,
  ExtremityFrame,
  useExtremityLayout,
} from './ExtremityLayout';

export const FOOT_TOES = ['big', 'middle', 'little'] as const;
export type FootToe = (typeof FOOT_TOES)[number];

const toeLabelId = (part: BodyPart) =>
  `onboarding-assign_trackers-toe-${BodyPart[part].split('_').slice(1, -1).join('_').toLowerCase()}`;

const TOE_POSITIONS: Record<FootToe, [number, number][]> = {
  big: [[28.5, 299]],
  middle: [
    [53, 296.5],
    [70.5, 285],
    [86, 281],
  ],
  little: [[101, 274]],
};

const COLUMN = 'w-24 smol:w-32 xsAssign:w-44 [&>*]:w-full';
const BAND = 'justify-between pt-[32%] pb-[16%]';
const SOLO = 'justify-end pb-[16%]';

function FootLayout() {
  const { mirrored } = useExtremityLayout();
  const foot = <Digit name="root" />;

  return (
    <ExtremityFrame
      bottom={
        <DigitRow>
          <Digit name="middle" flow="columns" />
        </DigitRow>
      }
      near={
        <DigitColumn className={`${COLUMN} ${mirrored ? BAND : SOLO}`}>
          {mirrored && foot}
          <Digit name="big" />
        </DigitColumn>
      }
      far={
        <DigitColumn className={`${COLUMN} ${mirrored ? SOLO : BAND}`}>
          {!mirrored && foot}
          <Digit name="little" />
        </DigitColumn>
      }
    />
  );
}

export const FOOT_EXTREMITY: ExtremityDescriptor = {
  digits: FOOT_TOES,
  sides: {
    left: {
      digits: {
        big: [BodyPart.LEFT_BIG_TOE],
        middle: [
          BodyPart.LEFT_INDEX_TOE,
          BodyPart.LEFT_MIDDLE_TOE,
          BodyPart.LEFT_RING_TOE,
        ],
        little: [BodyPart.LEFT_LITTLE_TOE],
      },
      root: BodyPart.LEFT_FOOT,
    },
    right: {
      digits: {
        big: [BodyPart.RIGHT_BIG_TOE],
        middle: [
          BodyPart.RIGHT_INDEX_TOE,
          BodyPart.RIGHT_MIDDLE_TOE,
          BodyPart.RIGHT_RING_TOE,
        ],
        little: [BodyPart.RIGHT_LITTLE_TOE],
      },
      root: BodyPart.RIGHT_FOOT,
    },
  },
  figure: {
    image: '/images/foot-pose.webp',
    width: 120,
    height: 327,
    anchors: TOE_POSITIONS,
    rootAnchor: [63, 160],
  },
  digitLabelId: (toe) => `onboarding-assign_trackers-toes-${toe}`,
  partLabelId: toeLabelId,
  mainPart: (toes) => toes[Math.floor(toes.length / 2)],
  Layout: FootLayout,
};
