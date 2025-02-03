import { fetchResourceUrl } from '@/utils/tauri';
import { ResetType } from 'solarxr-protocol';

const tapSetupSound1 = new Audio(await fetchResourceUrl('/sounds/first-tap.mp3'));
const tapSetupSound2 = new Audio(await fetchResourceUrl('/sounds/second-tap.mp3'));
const tapSetupSound3 = new Audio(await fetchResourceUrl('/sounds/third-tap.mp3'));
const tapSetupSound4 = new Audio(await fetchResourceUrl('/sounds/fourth-tap.mp3'));
const tapSetupSound5 = new Audio(await fetchResourceUrl('/sounds/fifth-tap.mp3'));
const tapSetupSoundEnd = new Audio(await fetchResourceUrl('/sounds/end-tap.mp3'));
const tapSetupExtraSound = new Audio(
  await fetchResourceUrl('/sounds/tapextrasetup.mp3')
);

function restartAndPlay(audio: HTMLAudioElement, volume: number) {
  audio.volume = Math.min(1, Math.pow(volume, Math.E) + 0.05);
  if (audio.paused) {
    audio.play();
  } else {
    audio.currentTime = 0;
  }
}

export function playSoundOnResetEnded(resetType: ResetType, volume = 1) {
  switch (resetType) {
    case ResetType.Yaw: {
      restartAndPlay(tapSetupSound2, volume);
      break;
    }
    case ResetType.Full: {
      restartAndPlay(tapSetupSound3, volume);
      break;
    }
    case ResetType.Mounting: {
      restartAndPlay(tapSetupSound4, volume);
      break;
    }
  }
}

export function playSoundOnResetStarted(volume = 1) {
  restartAndPlay(tapSetupSound1, volume);
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
