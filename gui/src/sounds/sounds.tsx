import { ResetType } from 'solarxr-protocol';

const quickResetStartedSound = new Audio(
  '/sounds/quick-reset-started-sound.mp3'
);
const fullResetStartedSound = new Audio('/sounds/full-reset-started-sound.mp3');
const mountingResetStartedSound = new Audio(
  '/sounds/mounting-reset-started-sound.mp3'
);

function restartAndPlay(audio: HTMLAudioElement, volume: number) {
  audio.volume = Math.min(1, Math.pow(volume, Math.E) + 0.05);
  if (audio.paused) {
    audio.play();
  } else {
    audio.currentTime = 0;
  }
}

export function playSoundOnResetStarted(resetType: ResetType, volume = 1) {
  switch (resetType) {
    case ResetType.Yaw: {
      restartAndPlay(quickResetStartedSound, volume);
      break;
    }
    case ResetType.Full: {
      restartAndPlay(fullResetStartedSound, volume);
      break;
    }
    case ResetType.Mounting: {
      restartAndPlay(mountingResetStartedSound, volume);
      break;
    }
  }
}
