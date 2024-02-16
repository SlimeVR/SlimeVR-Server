import { Helmet } from 'react-helmet';
export function Preload() {
  return (
    <Helmet>
      <link rel="preload" href="/images/front-standing-pose.webp" as="image" />
      <link rel="preload" href="/images/slime-girl.webp" as="image" />
      <link rel="preload" href="/images/mounting-reset-pose.webp" as="image" />
      <link rel="preload" href="/images/reset-pose.webp" as="image" />
      <link rel="preload" href="/images/slimes.webp" as="image" />

      <link rel="preload" href="/videos/autobone.webm" as="video" />

      <link
        rel="preload"
        href="/sounds/quick-reset-started-sound.mp3"
        as="audio"
      />
      <link
        rel="preload"
        href="/sounds/full-reset-started-sound.mp3"
        as="audio"
      />
      <link
        rel="preload"
        href="/sounds/mounting-reset-started-sound.mp3"
        as="audio"
      />
      <link rel="preload" href="/sounds/first-tap.mp3" as="audio" />
      <link rel="preload" href="/sounds/second-tap.mp3" as="audio" />
      <link rel="preload" href="/sounds/third-tap.mp3" as="audio" />
      <link rel="preload" href="/sounds/fourth-tap.mp3" as="audio" />
      <link rel="preload" href="/sounds/fifth-tap.mp3" as="audio" />
      <link rel="preload" href="/sounds/end-tap.mp3" as="audio" />
      <link rel="preload" href="/sounds/tapextrasetup.mp3" as="audio" />

      <link
        rel="preload"
        href="/models/tracker.gltf"
        as="fetch"
        crossOrigin="anonymous"
      />
      <link
        rel="preload"
        href="/models/extension.gltf"
        as="fetch"
        crossOrigin="anonymous"
      />
    </Helmet>
  );
}
