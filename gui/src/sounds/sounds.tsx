import { ResetType } from 'solarxr-protocol';

const singleBeep = new Audio('/sounds/single_beep.wav');
const doubleBeep = new Audio('/sounds/double_beep.wav');
const tripleBeep = new Audio('/sounds/triple_beep.wav');

export function playSoundForStarted(resetType: ResetType) {
  switch (resetType) {
    case ResetType.Quick: {
      singleBeep.play();
      break;
    }
    case ResetType.Full: {
      doubleBeep.play();
      break;
    }
    case ResetType.Mounting: {
      tripleBeep.play();
      break;
    }
  }
}
