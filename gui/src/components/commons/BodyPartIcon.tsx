import { BodyPart } from 'solarxr-protocol';
import { useLocaleConfig } from '@/i18n/config';
import { AnkleIcon } from './icon/AnkleIcon';
import { ChestIcon } from './icon/ChestIcon';
import { ControllerIcon } from './icon/ControllerIcon';
import { FootIcon } from './icon/FootIcon';
import { HeadsetIcon } from './icon/HeadsetIcon';
import { HipIcon } from './icon/HipIcon';
import { LowerArmIcon } from './icon/LowerArmIcon';
import { NeckIcon } from './icon/NeckIcon';
import { PawIcon } from './icon/PawIcon';
import { ShoulderIcon } from './icon/ShoulderIcon';
import { SlimeVRIcon } from './icon/SlimeVRIcon';
import { UpperArmIcon } from './icon/UpperArmIcon';
import { UpperLegIcon } from './icon/UpperLegIcon';
import { WaistIcon } from './icon/WaistIcon';
import { UpperChestIcon } from './icon/UpperChestIcon';
import { FingersIcon } from './icon/FingersIcon';

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
  [BodyPart.UPPER_CHEST]: ({ width }) => <UpperChestIcon width={width} />,
  [BodyPart.CHEST]: ({ width }) => <ChestIcon width={width} />,
  [BodyPart.HEAD]: ({ width }) => <HeadsetIcon width={width} />,
  [BodyPart.HIP]: ({ width }) => <HipIcon width={width} />,
  [BodyPart.LEFT_HIP]: ({ width }) => <HipIcon width={width} />, // Unused
  [BodyPart.RIGHT_HIP]: ({ width }) => <HipIcon width={width} />, // Unused
  [BodyPart.LEFT_FOOT]: ({ width, currentLocales }) =>
    currentLocales.includes('en-x-owo') ? (
      <PawIcon />
    ) : (
      <FootIcon width={width} />
    ),
  [BodyPart.LEFT_HAND]: ({ width }) => <ControllerIcon width={width} />,
  [BodyPart.LEFT_LOWER_ARM]: ({ width }) => <LowerArmIcon width={width} />,
  [BodyPart.LEFT_LOWER_LEG]: ({ width }) => <AnkleIcon width={width} />,
  [BodyPart.LEFT_SHOULDER]: ({ width }) => <ShoulderIcon width={width} />,
  [BodyPart.LEFT_UPPER_ARM]: ({ width }) => <UpperArmIcon width={width} />,
  [BodyPart.LEFT_UPPER_LEG]: ({ width }) => <UpperLegIcon width={width} />,
  [BodyPart.NECK]: ({ width }) => <NeckIcon width={width} />,
  [BodyPart.NONE]: ({ width }) => <SlimeVRIcon width={width} />,
  [BodyPart.RIGHT_FOOT]: ({ width, currentLocales }) =>
    currentLocales.includes('en-x-owo') ? (
      <PawIcon />
    ) : (
      <FootIcon width={width} flipped />
    ),
  [BodyPart.RIGHT_HAND]: ({ width }) => (
    <ControllerIcon width={width} flipped />
  ),
  [BodyPart.RIGHT_LOWER_ARM]: ({ width }) => (
    <LowerArmIcon width={width} flipped />
  ),
  [BodyPart.RIGHT_LOWER_LEG]: ({ width }) => (
    <AnkleIcon width={width} flipped />
  ),
  [BodyPart.RIGHT_SHOULDER]: ({ width }) => <ShoulderIcon width={width} />,
  [BodyPart.RIGHT_UPPER_ARM]: ({ width }) => (
    <UpperArmIcon width={width} flipped />
  ),
  [BodyPart.RIGHT_UPPER_LEG]: ({ width }) => (
    <UpperLegIcon width={width} flipped />
  ),
  [BodyPart.WAIST]: ({ width }) => <WaistIcon width={width} />,
  [BodyPart.LEFT_THUMB_METACARPAL]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.LEFT_THUMB_PROXIMAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.LEFT_THUMB_DISTAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.LEFT_INDEX_PROXIMAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.LEFT_INDEX_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.LEFT_INDEX_DISTAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.LEFT_MIDDLE_PROXIMAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.LEFT_MIDDLE_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.LEFT_MIDDLE_DISTAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.LEFT_RING_PROXIMAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.LEFT_RING_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.LEFT_RING_DISTAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.LEFT_LITTLE_PROXIMAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.LEFT_LITTLE_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.LEFT_LITTLE_DISTAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.RIGHT_THUMB_METACARPAL]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.RIGHT_THUMB_PROXIMAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.RIGHT_THUMB_DISTAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.RIGHT_INDEX_PROXIMAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.RIGHT_INDEX_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.RIGHT_INDEX_DISTAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.RIGHT_MIDDLE_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.RIGHT_MIDDLE_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.RIGHT_MIDDLE_DISTAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.RIGHT_RING_PROXIMAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.RIGHT_RING_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.RIGHT_RING_DISTAL]: ({ width }) => <FingersIcon width={width} />,
  [BodyPart.RIGHT_LITTLE_PROXIMAL]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.RIGHT_LITTLE_INTERMEDIATE]: ({ width }) => (
    <FingersIcon width={width} />
  ),
  [BodyPart.RIGHT_LITTLE_DISTAL]: ({ width }) => <FingersIcon width={width} />,
};

export function BodyPartIcon({
  bodyPart = BodyPart.NONE,
  width = 24,
}: {
  bodyPart?: BodyPart;
  width?: number;
}) {
  const { currentLocales } = useLocaleConfig();
  return (
    <svg width={width} height={width}>
      <rect width={width} height={width} rx="2" fill="#56407B" />
      {mapPart[bodyPart]({ width, currentLocales })}
    </svg>
  );
}
