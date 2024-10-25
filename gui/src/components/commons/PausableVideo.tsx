import { useRef, useState } from 'react';
import { PlayCircleIcon } from './icon/PlayIcon';
import { useDebouncedEffect } from '@/hooks/timeout';

export function PausableVideo({
  src,
  poster,
}: {
  src?: string;
  poster?: string;
}) {
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const [paused, setPaused] = useState(true);

  function toggleVideo() {
    if (!videoRef.current) return;
    if (videoRef.current.paused) {
      videoRef.current.play();
    } else {
      videoRef.current.pause();
      videoRef.current.currentTime = 0;
    }
    setPaused(videoRef.current.paused);
  }

  useDebouncedEffect(
    () => {
      if (paused) videoRef.current?.pause();
    },
    [paused],
    250
  );

  return (
    <button className="relative appearance-none h-fit" onClick={toggleVideo}>
      <div
        className="absolute w-[100px] h-[100px] top-0 bottom-0 left-0 right-0 m-auto fill-background-20"
        hidden={!paused}
      >
        <PlayCircleIcon width={100}></PlayCircleIcon>
      </div>

      <video
        preload="auto"
        ref={videoRef}
        src={src}
        poster={poster}
        className="min-w-[12rem] w-[30rem]"
        muted
        loop
        playsInline
        controls={false}
      ></video>
    </button>
  );
}
