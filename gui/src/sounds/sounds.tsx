import { ResetType } from 'solarxr-protocol';

export function playSoundForTriggered(resetType: ResetType) {
  switch (resetType) {
    case ResetType.Quick: {
      new Audio('/sounds/single_beep.wav').play();
      break;
    }
    case ResetType.Full: {
      new Audio('/sounds/double_beep.wav').play();
      break;
    }
    case ResetType.Mounting: {
      new Audio('/sounds/triple_beep.wav').play();
      break;
    }
  }
}
