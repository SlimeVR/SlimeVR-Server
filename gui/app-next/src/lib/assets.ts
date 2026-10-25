// One AVIF per density, 1x first. Made by vite-imagetools from a `?img`
// import, see vite.config.ts. Sizes are pixels of each file.
export interface ImageVariant {
  src: string;
  width: number;
  height: number;
}
export type ImageAsset = [ImageVariant, ...ImageVariant[]];

// Frames laid out left to right, top to bottom, `columns` per row, each shown
// for `frameMs`. Holds are repeated frames.
export interface SpriteAsset {
  src: string;
  frames: number;
  columns: number;
  rows: number;
  frameMs: number;
}
