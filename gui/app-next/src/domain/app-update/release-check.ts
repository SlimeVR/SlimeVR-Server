import semver from 'semver';
import { createSignal } from 'solid-js';
import * as v from 'valibot';
import { ignoreError } from '@/domain/error-reporting';
import { RELEASES_API_URL } from '@/lib/links';
import { platform } from '@/lib/platform';

const releasesSchema = v.array(
  v.looseObject({ tag_name: v.string(), prerelease: v.boolean() })
);

const [update, setUpdate] = createSignal<string | null>(null);

// Tag of a stable server release newer than this build, null when none
export function serverUpdate() {
  return update();
}

async function newerRelease(signal: AbortSignal): Promise<string | null> {
  if (platform.kind === 'android') return null;
  if (await platform.steam?.isSteam()) return null;
  if (!semver.valid(__VERSION_TAG__)) {
    console.info('Non semver version, skipping the server update check');
    return null;
  }

  const response = await fetch(RELEASES_API_URL, { signal });
  const parsed = v.safeParse(releasesSchema, await response.json());
  if (!parsed.success) return null;

  const tag = parsed.output.find((release) => !release.prerelease)?.tag_name;
  const version = tag ? semver.coerce(tag) : null;
  return tag && version && semver.gt(version, __VERSION_TAG__) ? tag : null;
}

export function startReleaseCheck(): () => void {
  const abort = new AbortController();
  newerRelease(abort.signal)
    .then(setUpdate)
    .catch(ignoreError('release check offline or rate limited'));
  return () => abort.abort();
}
