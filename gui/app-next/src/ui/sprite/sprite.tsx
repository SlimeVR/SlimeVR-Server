import type { SpriteAsset } from '@/lib/assets';
import { cx } from '@/lib/cx';

// Decorative looping sprite sheet (e.g. `slimes`), `size` CSS px square.
// Frame 0 under reduced motion. `lowPriority` also freezes it while the app
// is not active (.freeze-animations).
export function Sprite(props: {
  asset: SpriteAsset;
  size: number;
  lowPriority?: boolean;
  class?: string;
}) {
  return (
    <div
      aria-hidden="true"
      class={cx(
        'animate-sprite shrink-0 bg-no-repeat motion-reduce:animate-none',
        props.lowPriority && 'animate-low-priority',
        props.class
      )}
      style={{
        width: `${props.size}px`,
        height: `${props.size}px`,
        'background-image': `url(${props.asset.src})`,
        'background-size': `${props.asset.columns * 100}% ${props.asset.rows * 100}%`,
        '--sprite-frames': String(props.asset.frames),
        '--sprite-frame-ms': String(props.asset.frameMs),
        '--sprite-columns': String(props.asset.columns),
        '--sprite-rows': String(props.asset.rows),
      }}
    />
  );
}
