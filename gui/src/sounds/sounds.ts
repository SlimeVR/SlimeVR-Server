import { ResetType } from 'solarxr-protocol';
import Xylophone from './xylophone';

const tones = [
  ['C4', 'E4', 'G4'],
  ['E4', 'G4', 'C5'],
  ['G4', 'C5', 'E5'],
  ['C5', 'E5', 'G5'],
  ['E5', 'G5', 'C6'],
  ['G5', 'C6', 'E6'],
];

const xylophone = new Xylophone();

export async function playSoundOnResetEnded(resetType: ResetType, volume = 1) {
  switch (resetType) {
    case ResetType.Yaw: {
      xylophone.play({
        notes: ['C4'],
        offset: 0.15,
        type: 'square',
        volume,
      });
      break;
    }
    case ResetType.Full: {
      xylophone.play({
        notes: ['C4', 'E4'],
        offset: 0.15,
        type: 'square',
        volume,
      });
      break;
    }
    case ResetType.Mounting: {
      xylophone.play({
        notes: ['C4', 'E4', 'G4'],
        offset: 0.15,
        type: 'square',
        volume,
      });
      break;
    }
  }
}

export async function playSoundOnResetStarted(volume = 1) {
  await xylophone.play({
    notes: ['A4'],
    offset: 0.4,
    type: 'square',
    volume,
  });
}

let lastTap = 0;
export async function playTapSetupSound(volume = 1) {
  if (Math.floor(Math.random() * 12000) !== 0) {
    xylophone.play({
      notes: tones[lastTap],
      offset: 0.15,
      type: 'square',
      volume,
    });
  } else {
    xylophone.play({
      notes: ['D4', 'E4', 'G4', 'E4', 'B4', 'B4', 'A4'],
      offset: 0.15,
      length: 1,
      type: 'sawtooth',
      volume,
    });
  }
  lastTap++;
  if (lastTap >= tones.length) {
    lastTap = 0;
  }
}
