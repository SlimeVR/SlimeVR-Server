import type { SpriteAsset } from '@/lib/assets';
import curious from './sprites/curious-slime.webp?url';
import happy from './sprites/happy-slime.webp?url';
import jumping from './sprites/jumping-slime.webp?url';
import sad from './sprites/sad-slime.webp?url';

// Lossless sheets of the original slime GIFs (6286c50ef^), 300px frames.
// Rebuild one with ffmpeg: `fps=1000/<frameMs>` on the GIF repeats frames
// onto the tick, then `tile=<columns>x<rows>:color=0x00000000` and
// `-c:v libwebp -lossless 1`. 13 columns keeps sheets under 4096px.
export const slimes = {
  happy: { src: happy, frames: 56, columns: 13, rows: 5, frameMs: 80 },
  sad: { src: sad, frames: 48, columns: 13, rows: 4, frameMs: 80 },
  jumping: { src: jumping, frames: 14, columns: 13, rows: 2, frameMs: 80 },
  curious: { src: curious, frames: 33, columns: 13, rows: 3, frameMs: 90 },
} as const satisfies Record<string, SpriteAsset>;
