import {
  createEffect,
  createSignal,
  on,
  onCleanup,
  onMount,
  Show,
} from 'solid-js';
import PlayIcon from '~icons/material-symbols/play-arrow-rounded';
import { cx } from '@/lib/cx';
import { TextButton } from '@/ui/button';

// Muted looping clip (`import src from '@/assets/videos/x.webm'`), sized by
// the caller. Plays while on screen and the page is shown. One element,
// changing `src` swaps it. When autoplay is refused (Android WebView wants a
// gesture) a play button covers the first frame.
export function Video(props: { src: string; class?: string }) {
  const [onScreen, setOnScreen] = createSignal(false);
  const [pageShown, setPageShown] = createSignal(!document.hidden);
  const [blocked, setBlocked] = createSignal(false);
  let video!: HTMLVideoElement;

  onMount(() => {
    const observer = new IntersectionObserver((entries) =>
      setOnScreen(entries.some((e) => e.isIntersecting))
    );
    observer.observe(video);
    const onVisibility = () => setPageShown(!document.hidden);
    document.addEventListener('visibilitychange', onVisibility);
    onCleanup(() => {
      observer.disconnect();
      document.removeEventListener('visibilitychange', onVisibility);
    });
  });

  function play() {
    video
      .play()
      .then(() => setBlocked(false))
      .catch((e: unknown) => {
        // AbortError is a src swap or pause racing the play, not a refusal
        if (e instanceof DOMException && e.name === 'NotAllowedError')
          setBlocked(true);
      });
  }

  createEffect(
    on([() => props.src, onScreen, pageShown], ([, visible, shown]) => {
      if (visible && shown) {
        play();
        return;
      }
      video.pause();
    })
  );

  return (
    <div class={cx('relative', props.class)}>
      <video
        ref={video}
        src={props.src}
        preload="metadata"
        muted
        loop
        playsinline
        disablepictureinpicture
        aria-hidden="true"
        class="block size-full bg-transparent object-contain"
      />
      <Show when={blocked()}>
        <div class="absolute inset-0 flex items-center justify-center">
          <TextButton
            label="video-play"
            icon={<PlayIcon />}
            iconOnly
            variant="primary"
            onClick={play}
          />
        </div>
      </Show>
    </div>
  );
}
