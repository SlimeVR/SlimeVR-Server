import { ResetType } from 'solarxr-protocol';
import Xylophone from './xylophone';

const tones = [
  ['E3', 'G3', 'B3'],
  ['G3', 'B3', 'D4'],
  ['B3', 'D4', 'F#4'],
  ['D4', 'F#4', 'A4'],
  ['F#4', 'A4', 'C#5'],
];

const xylophone = new Xylophone();

export async function playSoundOnResetEnded(resetType: ResetType, volume = 1) {
  switch (resetType) {
    case ResetType.Yaw: {
      xylophone.play({
        notes: ['B3', 'D4'],
        offset: 0.15,
        type: 'custom',
        volume,
      });
      break;
    }
    case ResetType.Full: {
      xylophone.play({
        notes: ['E3', 'G3'],
        offset: 0.15,
        type: 'custom',
        volume,
      });
      break;
    }
    case ResetType.Mounting: {
      xylophone.play({
        notes: ['G3', 'B3', 'D4'],
        offset: 0.15,
        type: 'custom',
        volume,
      });
      break;
    }
  }
}

export async function playSoundOnResetStarted(resetType: ResetType, volume = 1) {
  switch (resetType) {
    case ResetType.Full: {
      await xylophone.play({
        notes: ['D4', 'F#4'],
        offset: 0.15,
        type: 'custom',
        volume,
      });
      break;
    }
    case ResetType.Mounting: {
      await xylophone.play({
        notes: ['F#4', 'A4'],
        offset: 0.15,
        type: 'custom',
        volume,
      });
      break;
    }
  }
}

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
