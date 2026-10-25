import { createSignal, For } from 'solid-js';
import boardReset from '@/assets/images/R14_board_reset_sw.webp?img';
import fullResetPose from '@/assets/images/reset/FullResetPose.webp?img';
import fullResetPoseSide from '@/assets/images/reset/FullResetPoseSide.webp?img';
import fullResetPoseWrong from '@/assets/images/reset/FullResetPoseWrong.webp?img';
import mountingFeets from '@/assets/images/mounting/MountingFeets.webp?img';
import stayAlignedStanding from '@/assets/images/stay-aligned/StayAlignedStanding.webp?img';
import butterflySlime from '@/assets/images/trackers/butterfly_slime.webp?size=256&img';
import controllerOk from '@/assets/images/user-height/controller-ok.webp?size=360&img';
import { slimes } from '@/assets/slimes';
import mountingBack from '@/assets/videos/mounting-back.webm';
import mountingForward from '@/assets/videos/mounting-forward.webm';
import mountingTposeUp from '@/assets/videos/mounting-tpose-up.webm';
import stepMounting from '@/assets/videos/step-mounting.webm';
import turnOnTracker from '@/assets/videos/turn-on-tracker.webm';
import { Image } from '@/ui/image';
import { Segmented } from '@/ui/radio-group';
import { Sprite } from '@/ui/sprite';
import { Video } from '@/ui/video';

const IMAGES = {
  'reset/FullResetPose': fullResetPose,
  'reset/FullResetPoseSide': fullResetPoseSide,
  'reset/FullResetPoseWrong': fullResetPoseWrong,
  'mounting/MountingFeets': mountingFeets,
  'stay-aligned/StayAlignedStanding': stayAlignedStanding,
  'user-height/controller-ok (size=360)': controllerOk,
  'trackers/butterfly_slime (size=256)': butterflySlime,
  R14_board_reset_sw: boardReset,
};

const CLIPS = {
  'mounting-forward': mountingForward,
  'mounting-back': mountingBack,
  'mounting-tpose-up': mountingTposeUp,
  'step-mounting': stepMounting,
  'turn-on-tracker': turnOnTracker,
};

type Clip = keyof typeof CLIPS;

export function AssetsDemo() {
  const [clip, setClip] = createSignal<Clip>('mounting-forward');

  return (
    <div class="surface-page flex flex-col gap-lg rounded-lg p-lg">
      <span class="text-title">Assets</span>

      <span class="text-section">Image</span>
      <div class="flex flex-wrap items-end gap-lg">
        <For each={Object.entries(IMAGES)}>
          {([name, asset]) => (
            <figure class="flex flex-col items-center gap-xs">
              <Image asset={asset} alt={name} class="h-auto w-40" />
              <figcaption class="text-fg-muted">
                {name} ({asset.map((v) => `${v.width}x${v.height}`).join(', ')})
              </figcaption>
            </figure>
          )}
        </For>
      </div>

      <span class="text-section">Video (one element, src swaps)</span>
      <Segmented
        value={clip()}
        onChange={setClip}
        options={(Object.keys(CLIPS) as Clip[]).map((value) => ({
          value,
          label: value,
        }))}
      />
      <Video
        src={CLIPS[clip()]}
        class="surface-card h-80 w-full max-w-md rounded-md"
      />

      <span class="text-section">Sprite (frame 0 under reduced motion)</span>
      <div class="flex flex-wrap items-end gap-lg">
        <For each={Object.entries(slimes)}>
          {([name, asset]) => (
            <figure class="flex flex-col items-center gap-xs">
              <Sprite asset={asset} size={128} />
              <figcaption class="text-fg-muted">
                {name} ({asset.frames} frames)
              </figcaption>
            </figure>
          )}
        </For>
        <figure class="flex flex-col items-center gap-xs">
          <Sprite asset={slimes.jumping} size={32} lowPriority />
          <figcaption class="text-fg-muted">32px, lowPriority</figcaption>
        </figure>
      </div>
    </div>
  );
}
