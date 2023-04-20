import { ResetType } from 'solarxr-protocol';

const quickResetStartedSound = new Audio(
  '/sounds/quick-reset-started-sound.mp3'
);
const fullResetStartedSound = new Audio('/sounds/full-reset-started-sound.mp3');
const mountingResetStartedSound = new Audio(
  '/sounds/mounting-reset-started-sound.mp3'
);
const tapSetupSound1 = new Audio('/sounds/first-tap.mp3');
const tapSetupSound2 = new Audio('/sounds/second-tap.mp3');
const tapSetupSound3 = new Audio('/sounds/third-tap.mp3');
const tapSetupSound4 = new Audio('/sounds/fourth-tap.mp3');
const tapSetupSound5 = new Audio('/sounds/fifth-tap.mp3');
const tapSetupSoundEnd = new Audio('/sounds/end-tap.mp3');
const tapSetupExtraSound = new Audio('/sounds/tapextrasetup.mp3');

const sounds = [
  quickResetStartedSound,
  fullResetStartedSound,
  mountingResetStartedSound,
  tapSetupSound1,
  tapSetupSound2,
  tapSetupSound3,
  tapSetupSound4,
  tapSetupSound5,
  tapSetupSoundEnd,
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
/* Easter egg */
tapSetupSoundEnd.onended = () => {
  if (Math.floor(Math.random() * 12000) !== 0) return;
  restartAndPlay(tapSetupExtraSound, lastKnownVolume);
};

const order = [
  tapSetupSound1,
  tapSetupSound2,
  tapSetupSound3,
  tapSetupSound4,
  tapSetupSound5,
  tapSetupSoundEnd,
  tapSetupSoundEnd,
  tapSetupSoundEnd,
];
let lastTap = 0;
export function playTapSetupSound(volume = 1) {
  lastKnownVolume = volume;
  restartAndPlay(order[lastTap++], volume);
  if (lastTap >= order.length) {
    lastTap = 0;
  }
}
