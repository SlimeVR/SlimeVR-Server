import { ResetResponseT, ResetStatus, ResetType } from 'solarxr-protocol';
import Xylophone, { ValidNote } from './xylophone';
import { fetchResourceUrl } from '@/utils/tauri';

const tones: ValidNote[][] = [
  ['E3', 'G3', 'B3'],
  ['G3', 'B3', 'D4'],
  ['B3', 'D4', 'F#4'],
  ['D4', 'F#4', 'A4'],
  ['F#4', 'A4', 'C#5'],
];

const xylophone = new Xylophone();

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

async function createAudio(path: string): Promise<HTMLAudioElement> {
  const audio = new Audio(await fetchResourceUrl(path));
  audio.preload = 'auto';
  audio.load();
  return audio;
}

let resetSounds: Record<
  ResetType,
  {
    initial: HTMLAudioElement;
    tick: HTMLAudioElement[];
    end: HTMLAudioElement;
    mew: HTMLAudioElement;
  }
> | null = null;

export async function loadSounds() {
  const fullResetSounds = {
    initial: await createAudio('/sounds/full-reset/initial.mp3'),
    tick: [
      await createAudio('/sounds/full-reset/click_1.mp3'),
      await createAudio('/sounds/full-reset/click_2.mp3'),
      await createAudio('/sounds/full-reset/click_3.mp3'),
    ],
    end: await createAudio('/sounds/full-reset/end_chord.mp3'),
    mew: await createAudio('/sounds/full-reset/mew.mp3'),
  };
  resetSounds = {
    [ResetType.Full]: fullResetSounds,
    [ResetType.Yaw]: fullResetSounds,
    [ResetType.Mounting]: fullResetSounds,
  };
}

function restartAndPlay(audio: HTMLAudioElement, volume: number) {
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

  if (status === ResetStatus.STARTED) {
    if (progress === 0) restartAndPlay(sounds.initial, volume);

    const tickIndex = (progress / 1000) % sounds.tick.length;
    if (progress >= 1 && sounds.tick[tickIndex]) {
      restartAndPlay(sounds.tick[tickIndex], volume);
    }
  }

  if (status === ResetStatus.FINISHED) {
    restartAndPlay(sounds.end, volume);
    restartAndPlay(sounds.mew, volume);
  }
}
