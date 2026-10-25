import { createSignal } from 'solid-js';
import type { ImageAsset } from '@/lib/assets';
import { cx } from '@/lib/cx';

export function Image(props: {
  asset: ImageAsset;
  alt: string;
  class?: string;
}) {
  const [loaded, setLoaded] = createSignal(false);

  return (
    <img
      src={props.asset[0].src}
      srcset={srcset(props.asset)}
      width={props.asset[0].width}
      height={props.asset[0].height}
      alt={props.alt}
      decoding="async"
      draggable={false}
      onLoad={() => setLoaded(true)}
      onError={() => setLoaded(true)}
      class={cx(!loaded() && 'animate-pulse rounded-sm bg-line', props.class)}
    />
  );
}

// Fetches and decodes an image ahead of time, e.g. the next step of a flow
export function warmImage(asset: ImageAsset): Promise<void> {
  const img = document.createElement('img');
  img.srcset = srcset(asset);
  img.src = asset[0].src;
  return img.decode().catch(() => {});
}

function srcset(asset: ImageAsset) {
  return asset.map((v, i) => `${v.src} ${i + 1}x`).join(', ');
}
