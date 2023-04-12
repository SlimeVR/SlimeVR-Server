import { ResetType } from 'solarxr-protocol';

const quickResetStartedSound = new Audio(
  '/sounds/quick-reset-started-sound.mp3'
);
const fullResetStartedSound = new Audio('/sounds/full-reset-started-sound.mp3');
const mountingResetStartedSound = new Audio(
  '/sounds/mounting-reset-started-sound.mp3'
);
const tapSetupSound = new Audio('/sounds/tapsetup.mp3');
const tapSetupExtraSound = new Audio('/sounds/tapextrasetup.mp3');

const sounds = [
  quickResetStartedSound,
  fullResetStartedSound,
  mountingResetStartedSound,
  tapSetupSound,
  tapSetupExtraSound,
];

sounds.forEach((s) => {
  s.play();
  setTimeout(() => s.pause(), 10);
});

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

let lastKnownVolume = 1;
tapSetupSound.onended = () => {
  if (Math.floor(Math.random() * 1000) !== 0) return;
  restartAndPlay(tapSetupExtraSound, lastKnownVolume);
};
export function playTapSetupSound(volume = 1) {
  lastKnownVolume = volume;
  restartAndPlay(tapSetupSound, volume);
}
