import { ResetResponseT, ResetStatus, ResetType } from 'solarxr-protocol';
import Xylophone, { ValidNote } from './xylophone';

const tones: ValidNote[][] = [
  ['E3', 'G3', 'B3'],
  ['G3', 'B3', 'D4'],
  ['B3', 'D4', 'F#4'],
  ['D4', 'F#4', 'A4'],
  ['F#4', 'A4', 'C#5'],
];

const xylophone = new Xylophone();
const mew = createAudio('/sounds/mew.ogg');
export const scaledProportionsClick = createAudio(
  '/sounds/full-reset/full-click-1.ogg'
);
const resetSounds: Record<
  ResetType,
  {
    initial: HTMLAudioElement | null;
    tick: HTMLAudioElement[] | null;
    end: HTMLAudioElement;
    mew: HTMLAudioElement | null;
  }
> = {
  [ResetType.Full]: {
    initial: createAudio('/sounds/full-reset/init-full-reset-with-tail.ogg'),
    tick: [
      createAudio('/sounds/full-reset/full-click-1.ogg'),
      createAudio('/sounds/full-reset/full-click-2.ogg'),
      createAudio('/sounds/full-reset/full-click-3.ogg'),
    ],
    end: createAudio('/sounds/full-reset/end-full-reset-with-tail.ogg'),
    mew,
  },
  [ResetType.Yaw]: {
    initial: null,
    tick: null,
    end: createAudio('/sounds/yaw-reset/yaw-reset.ogg'),
    mew: null,
  },
  [ResetType.Mounting]: {
    initial: createAudio('/sounds/mounting-reset/init-mounting-reset-with-tail.ogg'),
    tick: [
      createAudio('/sounds/mounting-reset/mount-click-1.ogg'),
      createAudio('/sounds/mounting-reset/mount-click-2.ogg'),
      createAudio('/sounds/mounting-reset/mount-click-3.ogg'),
    ],
    end: createAudio('/sounds/mounting-reset/end-mounting-reset-with-tail.ogg'),
    mew,
  },
};

export const trackingPauseSound = createAudio('/sounds/tracking/pause.ogg');
export const trackingPlaySound = createAudio('/sounds/tracking/play.ogg');

let lastTap = 0;
export async function playTapSetupSound(volume = 1) {
  if (Math.floor(Math.random() * 12000) !== 0) {
    xylophone.play({
      notes: tones[lastTap],
      offset: 0.15,
      type: 'custom',
      volume,
    });
  } else {
    xylophone.play([
      {
        notes: ['G#3', 'A#3', 'C#4', 'A#3'],
        offset: 0.15,
        length: 1,
        type: 'custom',
        volume,
      },
      {
        notes: ['F4', 'F4', 'D#4'],
        offset: 0.45,
        length: 2,
        type: 'custom',
        volume,
      },
    ]);
  }
  lastTap++;
  if (lastTap >= tones.length) {
    lastTap = 0;
  }
}

function createAudio(path: string): HTMLAudioElement {
  const audio = new Audio(path);
  audio.preload = 'auto';
  audio.load();
  return audio;
}

export function restartAndPlay(audio: HTMLAudioElement | null, volume: number) {
  if (!audio) return;
  try {
    audio.load(); // LINUX: Solves wierd bug where webkit would unload sounds wierdly and make the sounds not play anymore

    audio.volume = Math.min(1, Math.pow(volume, Math.E) + 0.05);
    audio.currentTime = 0;
    const playPromise = audio.play();
    if (playPromise !== undefined) {
      playPromise.catch((error) => {
        console.error('Audio playback failed:', error);
      });
    }
  } catch (error) {
    console.error('Audio error:', error);
  }
}

export function handleResetSounds(
  volume: number,
  { progress, status, resetType }: ResetResponseT
) {
  if (!resetSounds) throw 'sounds not loaded';
  const sounds = resetSounds[resetType];
  if (!sounds) throw 'reset type does not have a reset sound: ' + resetType;

  if (status === ResetStatus.STARTED) {
    if (progress === 0) {
      restartAndPlay(sounds.initial, volume);
    }

    if (sounds.tick) {
      const arrayLength = sounds.tick.length;

      const cycleLength = arrayLength * 2 - 2;
      const positionInCycle = Math.floor(progress / 1000) % cycleLength;

      let tickIndex;

      if (positionInCycle < arrayLength) {
        tickIndex = positionInCycle;
      } else {
        tickIndex = cycleLength - positionInCycle;
      }

      if (progress >= 1000 && sounds.tick[tickIndex]) {
        restartAndPlay(sounds.tick[tickIndex], volume);
      }
    }
  }

  if (status === ResetStatus.FINISHED) {
    restartAndPlay(sounds.end, volume);
    restartAndPlay(sounds.mew, volume);
  }
}
