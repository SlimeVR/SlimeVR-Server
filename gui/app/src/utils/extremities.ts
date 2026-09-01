import { BodyPart } from 'solarxr-protocol';

export type ExtremitySide = 'left' | 'right';

export type ExtremityParts = {
  digits: Record<string, BodyPart[]>;
  root: BodyPart;
};

export type ExtremityFigureSpec = {
  image: string;
  width: number;
  height: number;
  anchors: Record<string, [number, number][]>;
  rootAnchor: [number, number];
};

export type ExtremityDescriptor = {
  digits: readonly string[];
  sides: Record<ExtremitySide, ExtremityParts>;
  figure: ExtremityFigureSpec;
  digitLabelId: (digit: string) => string;
  layout: {
    roomy: {
      nearSide: string[];
      farSide: string[];
      jointsBand: string;
    };
    compact: {
      top: string[];
      side: string[];
      figureHeight: number;
    };
  };
};

/** Name of a joint on its own, e.g. "Distal", taken from the tail of its part name */
export const jointLabelId = (part: BodyPart) =>
  `onboarding-assign_trackers-joint-${BodyPart[part].split('_').pop()?.toLowerCase()}`;

export const HAND_FINGERS = ['thumb', 'index', 'middle', 'ring', 'little'] as const;
export type HandFinger = (typeof HAND_FINGERS)[number];

/** Anchors for the right hand, palm toward the viewer */
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

export const HAND_EXTREMITY: ExtremityDescriptor = {
  digits: HAND_FINGERS,
  /** Joints of a finger, ordered from the tip down, so the index is the joint number */
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
  layout: {
    roomy: {
      nearSide: ['middle', 'index', 'thumb'],
      farSide: ['ring', 'little'],
      jointsBand: 'pt-[17%] pb-[42%]',
    },
    compact: {
      top: ['middle', 'ring', 'little'],
      side: ['index', 'thumb'],
      figureHeight: 300,
    },
  },
};
