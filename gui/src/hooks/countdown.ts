import { useRef, useState } from 'react';

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
  const countdownTimer = useRef<NodeJS.Timeout>();
  const counter = useRef(0);

  const startCountdown = () => {
    setIsCounting(true);
    setDisplayTimer(duration);
    counter.current = 0;
    countdownTimer.current = setInterval(
      () => {
        counter.current++;
        setDisplayTimer(duration - counter.current);
        if (counter.current >= duration) {
          clearInterval(countdownTimer.current);
          resetEnd();
        }
      },
      duration > 1 ? 1000 : 500
    );
  };

  const resetEnd = () => {
    setIsCounting(false);
    clearInterval(countdownTimer.current);
    onCountdownEnd();
  };

  const abortCountdown = () => {
    setIsCounting(false);
    clearInterval(countdownTimer.current);
  };

  return {
    timer,
    isCounting,
    startCountdown,
    abortCountdown,
  };
}
