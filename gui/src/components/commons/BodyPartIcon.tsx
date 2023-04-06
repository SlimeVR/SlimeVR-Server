import { BodyPart } from 'solarxr-protocol';
import { AnkleIcon } from './icon/AnkleIcon';
import { ChestIcon } from './icon/ChestIcon';
import { ControllerIcon } from './icon/ControllerIcon';
import { FootIcon } from './icon/FootIcon';
import { HeadsetIcon } from './icon/HeadsetIcon';
import { HipIcon } from './icon/HipIcon';
import { LowerArmIcon } from './icon/LowerArmIcon';
import { NeckIcon } from './icon/NeckIcon';
import { ShoulderIcon } from './icon/ShoulderIcon';
import { SlimeVRIcon } from './icon/SlimeVRIcon';
import { UpperArmIcon } from './icon/UpperArmIcon';
import { UpperLegIcon } from './icon/UpperLegIcon';
import { WaistIcon } from './icon/WaistIcon';

// All body parts that are right or left, are by default left!
export const mapPart: Record<
  BodyPart,
  ({ width }: { width?: number }) => JSX.Element
> = {
  [BodyPart.CHEST]: ({ width }) => <ChestIcon width={width}></ChestIcon>,
  [BodyPart.HEAD]: ({ width }) => <HeadsetIcon width={width}></HeadsetIcon>,
  [BodyPart.HIP]: ({ width }) => <HipIcon width={width}></HipIcon>,
  [BodyPart.LEFT_FOOT]: ({ width }) => <FootIcon width={width}></FootIcon>,
  [BodyPart.LEFT_HAND]: ({ width }) => (
    <ControllerIcon width={width}></ControllerIcon>
  ),
  [BodyPart.LEFT_LOWER_ARM]: ({ width }) => (
    <LowerArmIcon width={width}></LowerArmIcon>
  ),
  [BodyPart.LEFT_LOWER_LEG]: ({ width }) => (
    <AnkleIcon width={width}></AnkleIcon>
  ),
  [BodyPart.LEFT_SHOULDER]: ({ width }) => (
    <ShoulderIcon width={width}></ShoulderIcon>
  ),
  [BodyPart.LEFT_UPPER_ARM]: ({ width }) => (
    <UpperArmIcon width={width}></UpperArmIcon>
  ),
  [BodyPart.LEFT_UPPER_LEG]: ({ width }) => (
    <UpperLegIcon width={width}></UpperLegIcon>
  ),
  [BodyPart.NECK]: ({ width }) => <NeckIcon width={width}></NeckIcon>,
  [BodyPart.NONE]: ({ width }) => <SlimeVRIcon width={width}></SlimeVRIcon>,
  [BodyPart.RIGHT_FOOT]: ({ width }) => (
    <FootIcon width={width} flipped></FootIcon>
  ),
  [BodyPart.RIGHT_HAND]: ({ width }) => (
    <ControllerIcon width={width} flipped></ControllerIcon>
  ),
  [BodyPart.RIGHT_LOWER_ARM]: ({ width }) => (
    <LowerArmIcon width={width} flipped></LowerArmIcon>
  ),
  [BodyPart.RIGHT_LOWER_LEG]: ({ width }) => (
    <AnkleIcon width={width} flipped></AnkleIcon>
  ),
  [BodyPart.RIGHT_SHOULDER]: ({ width }) => (
    <ShoulderIcon width={width}></ShoulderIcon>
  ),
  [BodyPart.RIGHT_UPPER_ARM]: ({ width }) => (
    <UpperArmIcon width={width} flipped></UpperArmIcon>
  ),
  [BodyPart.RIGHT_UPPER_LEG]: ({ width }) => (
    <UpperLegIcon width={width} flipped></UpperLegIcon>
  ),
  [BodyPart.WAIST]: ({ width }) => <WaistIcon width={width}></WaistIcon>,
};

export function BodyPartIcon({
  bodyPart = BodyPart.NONE,
  width = 24,
}: {
  bodyPart?: BodyPart;
  width?: number;
}) {
  return (
    <svg width={width} height={width}>
      <rect width={width} height={width} rx="2" fill="#56407B" />
      {mapPart[bodyPart]({ width })}
    </svg>
  );
}
