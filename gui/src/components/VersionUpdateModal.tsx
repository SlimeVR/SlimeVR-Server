import { useLocalization } from '@fluent/react';
import { useContext, useState } from 'react';
import { BaseModal } from './commons/BaseModal';
import { Button } from './commons/Button';
import { Typography } from './commons/Typography';
import { open } from '@tauri-apps/plugin-shell';
import semver from 'semver';
import { GH_REPO, VersionContext } from '@/App';
import { error } from '@/utils/logging';

export function VersionUpdateModal() {
  const { l10n } = useLocalization();
  const newVersion = useContext(VersionContext);
  const [forceClose, setForceClose] = useState(false);
  const closeModal = () => {
    localStorage.setItem('lastVersionFound', newVersion);
    setForceClose(true);
  };
  let isVersionNew = false;
  try {
    if (newVersion) {
      isVersionNew = semver.gt(
        newVersion,
        localStorage.getItem('lastVersionFound') || 'v0.0.0'
      );
    }
  } catch {
    error('failed to parse new version');
  }

  return (
    <BaseModal
      isOpen={!forceClose && !!newVersion && isVersionNew}
      onRequestClose={closeModal}
    >
      <div className="flex flex-col gap-3">
        <>
          <div className="flex flex-col items-center gap-3 fill-accent-background-20">
            <div className="flex flex-col items-center gap-2">
              <Typography variant="main-title">
                {l10n.getString('version_update-title', {
                  version: newVersion,
                })}
              </Typography>
              <Typography variant="standard">
                {l10n.getString('version_update-description')}
              </Typography>
            </div>
          </div>

          <Button
            variant="primary"
            onClick={async () => {
              const url = document.body.classList.contains('windows_nt')
                ? 'https://slimevr.dev/download'
                : `https://github.com/${GH_REPO}/releases/latest`;
              await open(url).catch(() => window.open(url, '_blank'));
              closeModal();
            }}
          >
            {l10n.getString('version_update-update')}
          </Button>
          <Button variant="tertiary" onClick={closeModal}>
            {l10n.getString('version_update-close')}
          </Button>
        </>
      </div>
    </BaseModal>
  );
}

/**
 * A GitHub release.
 */
export interface Release {
  url: string;
  html_url: string;
  assets_url: string;
  upload_url: string;
  tarball_url: string | null;
  zipball_url: string | null;
  id: number;
  node_id: string;
  /**
   * The name of the tag.
   */
  tag_name: string;
  /**
   * Specifies the commitish value that determines where the Git tag is created from.
   */
  target_commitish: string;
  name: string | null;
  body?: string | null;
  /**
   * true to create a draft (unpublished) release, false to create a published one.
   */
  draft: boolean;
  /**
   * Whether to identify the release as a prerelease or a full release.
   */
  prerelease: boolean;
  created_at: string;
  published_at: string | null;
  author: SimpleUser;
  assets: ReleaseAsset[];
  body_html?: string;
  body_text?: string;
  mentions_count?: number;
  /**
   * The URL of the release discussion.
   */
  discussion_url?: string;
  reactions?: ReactionRollup;
  [k: string]: unknown;
}
/**
 * A GitHub user.
 */
export interface SimpleUser {
  name?: string | null;
  email?: string | null;
  login: string;
  id: number;
  node_id: string;
  avatar_url: string;
  gravatar_id: string | null;
  url: string;
  html_url: string;
  followers_url: string;
  following_url: string;
  gists_url: string;
  starred_url: string;
  subscriptions_url: string;
  organizations_url: string;
  repos_url: string;
  events_url: string;
  received_events_url: string;
  type: string;
  site_admin: boolean;
  starred_at?: string;
  [k: string]: unknown;
}
/**
 * Data related to a release.
 */
export interface ReleaseAsset {
  url: string;
  browser_download_url: string;
  id: number;
  node_id: string;
  /**
   * The file name of the asset.
   */
  name: string;
  label: string | null;
  /**
   * State of the release asset.
   */
  state: 'uploaded' | 'open';
  content_type: string;
  size: number;
  download_count: number;
  created_at: string;
  updated_at: string;
  uploader: null | SimpleUser1;
  [k: string]: unknown;
}
/**
 * A GitHub user.
 */
export interface SimpleUser1 {
  name?: string | null;
  email?: string | null;
  login: string;
  id: number;
  node_id: string;
  avatar_url: string;
  gravatar_id: string | null;
  url: string;
  html_url: string;
  followers_url: string;
  following_url: string;
  gists_url: string;
  starred_url: string;
  subscriptions_url: string;
  organizations_url: string;
  repos_url: string;
  events_url: string;
  received_events_url: string;
  type: string;
  site_admin: boolean;
  starred_at?: string;
  [k: string]: unknown;
}
export interface ReactionRollup {
  url: string;
  total_count: number;
  '+1': number;
  '-1': number;
  laugh: number;
  confused: number;
  heart: number;
  hooray: number;
  eyes: number;
  rocket: number;
  [k: string]: unknown;
}
