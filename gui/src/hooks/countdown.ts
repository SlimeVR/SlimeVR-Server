import { useState } from 'react';

export function useCountdown({
  duration = 3,
  onCountdownEnd = () => {
    return;
  },
}: {
  duration?: number;
  onCountdownEnd: () => void;
}) {
  const [isCounting, setIsCounting] = useState(false);
  const [timer, setDisplayTimer] = useState(0);

  const startCountdown = () => {
    setIsCounting(true);
    setDisplayTimer(duration);
    for (let i = 1; i < duration; i++) {
      setTimeout(() => setDisplayTimer(duration - i), i * 1000);
    }
    setTimeout(resetEnd, duration * 1000);
  };

  const resetEnd = () => {
    setIsCounting(false);
    onCountdownEnd();
  };

  return {
    timer,
    isCounting,
    startCountdown,
  };
}
