import { BoardType, BodyPart } from 'solarxr-protocol';
import { type Component, type JSX, Show } from 'solid-js';
import { Dynamic } from 'solid-js/web';
import HeadMountedDeviceRoundedIcon from '~icons/material-symbols/head-mounted-device-rounded';
import AnkleIcon from '~icons/slime/ankle';
import ButterflyIcon from '~icons/slime/butterfly';
import ChestIcon from '~icons/slime/chest';
import ControllerIcon from '~icons/slime/controller';
import FingersIcon from '~icons/slime/fingers';
import FootIcon from '~icons/slime/foot';
import HipIcon from '~icons/slime/hip';
import LowerArmIcon from '~icons/slime/lower-arm';
import NeckIcon from '~icons/slime/neck';
import PawIcon from '~icons/slime/paw';
import ShoulderIcon from '~icons/slime/shoulder';
import SlimeExtensionIcon from '~icons/slime/slime-extension';
import SlimeNormalIcon from '~icons/slime/slime-normal';
import SlimevrIcon from '~icons/slime/slimevr';
import UpperArmIcon from '~icons/slime/upper-arm';
import UpperChestIcon from '~icons/slime/upper-chest';
import UpperLegIcon from '~icons/slime/upper-leg';
import WaistIcon from '~icons/slime/waist';
import { cx } from '@/lib/cx';
import { locale } from '@/lib/l10n';
import {
  trackerByKey,
  trackerDevice,
  trackerKeys,
  type TrackerKey,
} from './store';

type Icon = Component<JSX.SvgSVGAttributes<SVGSVGElement>>;

interface PartGlyph {
  icon: Icon;
  // Icons are drawn for the left side
  mirror?: boolean;
  // Paw in the owo locale
  foot?: boolean;
}

const FINGERS: PartGlyph = { icon: FingersIcon };
const LEFT_FOOT: PartGlyph = { icon: FootIcon, foot: true };
const RIGHT_FOOT: PartGlyph = { icon: FootIcon, mirror: true, foot: true };

const PART_GLYPHS = {
  [BodyPart.NONE]: { icon: SlimevrIcon },
  [BodyPart.HEAD]: { icon: HeadMountedDeviceRoundedIcon },
  [BodyPart.NECK]: { icon: NeckIcon },
  [BodyPart.UPPER_CHEST]: { icon: UpperChestIcon },
  [BodyPart.LOWER_CHEST]: { icon: ChestIcon },
  [BodyPart.UPPER_WAIST]: { icon: WaistIcon },
  [BodyPart.LOWER_WAIST]: { icon: WaistIcon },
  [BodyPart.HIP]: { icon: HipIcon },
  [BodyPart.LEFT_UPPER_LEG]: { icon: UpperLegIcon },
  [BodyPart.RIGHT_UPPER_LEG]: { icon: UpperLegIcon, mirror: true },
  [BodyPart.LEFT_LOWER_LEG]: { icon: AnkleIcon },
  [BodyPart.RIGHT_LOWER_LEG]: { icon: AnkleIcon, mirror: true },
  [BodyPart.LEFT_FOOT]: LEFT_FOOT,
  [BodyPart.RIGHT_FOOT]: RIGHT_FOOT,
  [BodyPart.LEFT_UPPER_ARM]: { icon: UpperArmIcon },
  [BodyPart.RIGHT_UPPER_ARM]: { icon: UpperArmIcon, mirror: true },
  [BodyPart.LEFT_LOWER_ARM]: { icon: LowerArmIcon },
  [BodyPart.RIGHT_LOWER_ARM]: { icon: LowerArmIcon, mirror: true },
  [BodyPart.LEFT_HAND]: { icon: ControllerIcon },
  [BodyPart.RIGHT_HAND]: { icon: ControllerIcon, mirror: true },
  // Old icons never mirrored the right shoulder
  [BodyPart.LEFT_SHOULDER]: { icon: ShoulderIcon },
  [BodyPart.RIGHT_SHOULDER]: { icon: ShoulderIcon },
  [BodyPart.LEFT_THUMB_METACARPAL]: FINGERS,
  [BodyPart.LEFT_THUMB_PROXIMAL]: FINGERS,
  [BodyPart.LEFT_THUMB_DISTAL]: FINGERS,
  [BodyPart.LEFT_INDEX_PROXIMAL]: FINGERS,
  [BodyPart.LEFT_INDEX_INTERMEDIATE]: FINGERS,
  [BodyPart.LEFT_INDEX_DISTAL]: FINGERS,
  [BodyPart.LEFT_MIDDLE_PROXIMAL]: FINGERS,
  [BodyPart.LEFT_MIDDLE_INTERMEDIATE]: FINGERS,
  [BodyPart.LEFT_MIDDLE_DISTAL]: FINGERS,
  [BodyPart.LEFT_RING_PROXIMAL]: FINGERS,
  [BodyPart.LEFT_RING_INTERMEDIATE]: FINGERS,
  [BodyPart.LEFT_RING_DISTAL]: FINGERS,
  [BodyPart.LEFT_LITTLE_PROXIMAL]: FINGERS,
  [BodyPart.LEFT_LITTLE_INTERMEDIATE]: FINGERS,
  [BodyPart.LEFT_LITTLE_DISTAL]: FINGERS,
  [BodyPart.RIGHT_THUMB_METACARPAL]: FINGERS,
  [BodyPart.RIGHT_THUMB_PROXIMAL]: FINGERS,
  [BodyPart.RIGHT_THUMB_DISTAL]: FINGERS,
  [BodyPart.RIGHT_INDEX_PROXIMAL]: FINGERS,
  [BodyPart.RIGHT_INDEX_INTERMEDIATE]: FINGERS,
  [BodyPart.RIGHT_INDEX_DISTAL]: FINGERS,
  [BodyPart.RIGHT_MIDDLE_PROXIMAL]: FINGERS,
  [BodyPart.RIGHT_MIDDLE_INTERMEDIATE]: FINGERS,
  [BodyPart.RIGHT_MIDDLE_DISTAL]: FINGERS,
  [BodyPart.RIGHT_RING_PROXIMAL]: FINGERS,
  [BodyPart.RIGHT_RING_INTERMEDIATE]: FINGERS,
  [BodyPart.RIGHT_RING_DISTAL]: FINGERS,
  [BodyPart.RIGHT_LITTLE_PROXIMAL]: FINGERS,
  [BodyPart.RIGHT_LITTLE_INTERMEDIATE]: FINGERS,
  [BodyPart.RIGHT_LITTLE_DISTAL]: FINGERS,
  [BodyPart.LEFT_BIG_TOE]: LEFT_FOOT,
  [BodyPart.LEFT_INDEX_TOE]: LEFT_FOOT,
  [BodyPart.LEFT_MIDDLE_TOE]: LEFT_FOOT,
  [BodyPart.LEFT_RING_TOE]: LEFT_FOOT,
  [BodyPart.LEFT_LITTLE_TOE]: LEFT_FOOT,
  [BodyPart.RIGHT_BIG_TOE]: RIGHT_FOOT,
  [BodyPart.RIGHT_INDEX_TOE]: RIGHT_FOOT,
  [BodyPart.RIGHT_MIDDLE_TOE]: RIGHT_FOOT,
  [BodyPart.RIGHT_RING_TOE]: RIGHT_FOOT,
  [BodyPart.RIGHT_LITTLE_TOE]: RIGHT_FOOT,
} satisfies Record<BodyPart, PartGlyph>;

const BUTTERFLY_BOARDS = new Set([
  BoardType.SLIMEVR_BUTTERFLY,
  BoardType.SLIMEVR_BUTTERFLY_DEV,
]);

const SLIME_BOARDS = new Set([
  BoardType.SLIMEVR,
  BoardType.SLIMEVR_V1_2,
  BoardType.SLIMEVR_DEV,
  BoardType.SLIMEVR_LEGACY,
]);

const OWO_LOCALE = 'en-x-owo';

function partGlyph(part: BodyPart): PartGlyph {
  const glyph: PartGlyph = PART_GLYPHS[part];
  if (glyph.foot && locale() === OWO_LOCALE) return { icon: PawIcon };
  return glyph;
}

// Unassigned trackers show their hardware: butterfly, slime (main or extension
// IMU) or the SlimeVR glyph
function UnassignedGlyph(props: { trackerKey: TrackerKey }) {
  const board = () =>
    trackerDevice(props.trackerKey)?.hardwareInfo?.officialBoardType;
  const extension = () => {
    const deviceId = trackerByKey(props.trackerKey)?.deviceId;
    const first = trackerKeys().find(
      (key) => trackerByKey(key)?.deviceId === deviceId
    );
    return first !== props.trackerKey;
  };
  const icon = (): { icon: Icon; small?: boolean } => {
    const type = board();
    if (type != null && BUTTERFLY_BOARDS.has(type)) {
      return { icon: ButterflyIcon };
    }
    if (type != null && SLIME_BOARDS.has(type)) {
      return {
        icon: extension() ? SlimeExtensionIcon : SlimeNormalIcon,
        small: true,
      };
    }
    return { icon: SlimevrIcon };
  };

  return (
    <span class={cx('flex', icon().small && 'scale-70')}>
      <Dynamic component={icon().icon} />
    </span>
  );
}

export interface BodyPartIconProps {
  // Reads the part and hardware from the tracker
  trackerKey?: TrackerKey;
  // Overrides the tracker's part
  part?: BodyPart;
}

// Rounded square sized by the container's `--icon-size` (`icon-xl` etc).
// Decorative, the name shows beside it.
export function BodyPartIcon(props: BodyPartIconProps) {
  const part = () =>
    props.part ??
    (props.trackerKey
      ? trackerByKey(props.trackerKey)?.info?.bodyPart
      : undefined) ??
    BodyPart.NONE;
  const unassignedKey = () =>
    part() === BodyPart.NONE &&
    props.trackerKey &&
    trackerDevice(props.trackerKey)
      ? props.trackerKey
      : undefined;
  const glyph = () => partGlyph(part());

  return (
    <span
      aria-hidden="true"
      class={cx(
        'flex size-(--icon-size) shrink-0 items-center justify-center rounded-xs text-fg',
        unassignedKey() ? 'bg-accent' : 'bg-accent-muted'
      )}
    >
      <Show
        when={unassignedKey()}
        fallback={
          <span class={cx('flex', glyph().mirror && '-scale-x-100')}>
            <Dynamic component={glyph().icon} />
          </span>
        }
      >
        {(key) => <UnassignedGlyph trackerKey={key()} />}
      </Show>
    </span>
  );
}
