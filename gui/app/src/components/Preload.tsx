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
