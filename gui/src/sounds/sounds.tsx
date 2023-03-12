import { ResetType } from 'solarxr-protocol';

const quickResetStartedSound = new Audio(
  '/sounds/quick-reset-started-sound.mp3'
);
const fullResetStartedSound = new Audio('/sounds/full-reset-started-sound.mp3');
const mountingResetStartedSound = new Audio(
  '/sounds/mounting-reset-started-sound.mp3'
);

function restartAndPlay(audio: HTMLAudioElement) {
  if (audio.paused) {
    audio.play();
  } else {
    audio.currentTime = 0;
  }
}

export function playSoundForStarted(resetType: ResetType) {
  switch (resetType) {
    case ResetType.Yaw: {
      restartAndPlay(quickResetStartedSound);
      break;
    }
    case ResetType.Full: {
      restartAndPlay(fullResetStartedSound);
      break;
    }
    case ResetType.Mounting: {
      restartAndPlay(mountingResetStartedSound);
      break;
    }
  }
}
