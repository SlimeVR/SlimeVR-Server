import { BodyPart } from 'solarxr-protocol';
import { ExtremityDescriptor } from '@/utils/extremities';
import {
  Digit,
  DigitColumn,
  DigitRow,
  ExtremityFrame,
  useExtremityLayout,
} from './ExtremityLayout';

const jointLabelId = (part: BodyPart) =>
  `onboarding-assign_trackers-joint-${BodyPart[part].split('_').pop()?.toLowerCase()}`;

export const HAND_FINGERS = [
  'thumb',
  'index',
  'middle',
  'ring',
  'little',
] as const;
export type HandFinger = (typeof HAND_FINGERS)[number];

const JOINT_POSITIONS: Record<HandFinger, [number, number][]> = {
  thumb: [
    [40.152, 182.392],
    [54.786, 214.794],
    [67.33, 240.93],
  ],
  index: [
    [48.515, 95.633],
    [64.194, 132.218],
    [83.009, 169.847],
  ],
  middle: [
    [101.825, 72.636],
    [107.051, 120.72],
    [112.278, 164.62],
  ],
  ring: [
    [155.134, 83.09],
    [149.907, 125.944],
    [144.681, 168.802],
  ],
  little: [
    [205.318, 126.999],
    [191.729, 153.13],
    [180.231, 181.354],
  ],
};

function HandLayout() {
  const { compact } = useExtremityLayout();

  if (compact)
    return (
      <ExtremityFrame
        figureHeight={300}
        top={
          <DigitRow>
            <Digit name="middle" />
            <Digit name="ring" />
            <Digit name="little" />
          </DigitRow>
        }
        near={
          <DigitColumn className="justify-end">
            <Digit name="index" />
            <Digit name="thumb" />
            <Digit name="root" />
          </DigitColumn>
        }
      />
    );

  return (
    <ExtremityFrame
      near={
        <DigitColumn className="justify-between pt-[17%] pb-[42%]">
          <Digit name="middle" />
          <Digit name="index" />
          <Digit name="thumb" />
        </DigitColumn>
      }
      far={
        <DigitColumn className="justify-between pt-[17%] pb-[42%]">
          <Digit name="ring" />
          <Digit name="little" />
          <Digit name="root" />
        </DigitColumn>
      }
    />
  );
}

export const HAND_EXTREMITY: ExtremityDescriptor = {
  digits: HAND_FINGERS,
  sides: {
    left: {
      digits: {
        thumb: [
          BodyPart.LEFT_THUMB_DISTAL,
          BodyPart.LEFT_THUMB_PROXIMAL,
          BodyPart.LEFT_THUMB_METACARPAL,
        ],
        index: [
          BodyPart.LEFT_INDEX_DISTAL,
          BodyPart.LEFT_INDEX_INTERMEDIATE,
          BodyPart.LEFT_INDEX_PROXIMAL,
        ],
        middle: [
          BodyPart.LEFT_MIDDLE_DISTAL,
          BodyPart.LEFT_MIDDLE_INTERMEDIATE,
          BodyPart.LEFT_MIDDLE_PROXIMAL,
        ],
        ring: [
          BodyPart.LEFT_RING_DISTAL,
          BodyPart.LEFT_RING_INTERMEDIATE,
          BodyPart.LEFT_RING_PROXIMAL,
        ],
        little: [
          BodyPart.LEFT_LITTLE_DISTAL,
          BodyPart.LEFT_LITTLE_INTERMEDIATE,
          BodyPart.LEFT_LITTLE_PROXIMAL,
        ],
      },
      root: BodyPart.LEFT_HAND,
    },
    right: {
      digits: {
        thumb: [
          BodyPart.RIGHT_THUMB_DISTAL,
          BodyPart.RIGHT_THUMB_PROXIMAL,
          BodyPart.RIGHT_THUMB_METACARPAL,
        ],
        index: [
          BodyPart.RIGHT_INDEX_DISTAL,
          BodyPart.RIGHT_INDEX_INTERMEDIATE,
          BodyPart.RIGHT_INDEX_PROXIMAL,
        ],
        middle: [
          BodyPart.RIGHT_MIDDLE_DISTAL,
          BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
          BodyPart.RIGHT_MIDDLE_PROXIMAL,
        ],
        ring: [
          BodyPart.RIGHT_RING_DISTAL,
          BodyPart.RIGHT_RING_INTERMEDIATE,
          BodyPart.RIGHT_RING_PROXIMAL,
        ],
        little: [
          BodyPart.RIGHT_LITTLE_DISTAL,
          BodyPart.RIGHT_LITTLE_INTERMEDIATE,
          BodyPart.RIGHT_LITTLE_PROXIMAL,
        ],
      },
      root: BodyPart.RIGHT_HAND,
    },
  },
  figure: {
    image: '/images/hand-pose.webp',
    width: 262,
    height: 428,
    anchors: JOINT_POSITIONS,
    rootAnchor: [132.138, 248.248],
  },
  digitLabelId: (finger) => `onboarding-assign_trackers-finger-${finger}`,
  partLabelId: jointLabelId,
  mainPart: (joints) => joints[0],
  Layout: HandLayout,
};
