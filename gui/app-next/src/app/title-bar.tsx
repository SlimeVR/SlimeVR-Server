import { useMatch } from '@solidjs/router';
import { createSignal, createUniqueId, Show } from 'solid-js';
import { ServerInfosRequestT, ServerInfosResponseT } from 'solarxr-protocol';
import DownloadIcon from '~icons/heroicons/arrow-down-tray-solid';
import QuestionIcon from '~icons/heroicons/question-mark-circle';
import CloseIcon from '~icons/material-symbols/close-rounded';
import MaximiseIcon from '~icons/material-symbols/crop-square-outline-rounded';
import MinimiseIcon from '~icons/material-symbols/minimize-rounded';
import GearIcon from '~icons/material-symbols/settings-rounded';
import SlimevrLogoIcon from '~icons/slime/slimevr-logo';
import { paths } from '@/app/routes';
import { serverUpdate } from '@/domain/app-update/release-check';
import {
  DOCS_SITE,
  LATEST_RELEASE_URL,
  RELEASES_URL,
  WINDOWS_DOWNLOAD_URL,
} from '@/lib/links';
import { platform, type WindowApi } from '@/lib/platform';
import { flushPrefs } from '@/lib/storage';
import { defineResource } from '@/solarxr/resource';
import { TextButton } from '@/ui/button';
import { MotionRegion } from '@/ui/motion';
import { PlainLink } from '@/ui/plain-link';
import { Tooltip } from '@/ui/tooltip';

const serverInfos = defineResource({
  request: () => new ServerInfosRequestT(),
  response: ServerInfosResponseT,
});

const VERSION =
  (__VERSION_TAG__ || __COMMIT_HASH__) + (__GIT_CLEAN__ ? '' : '-dirty');

function localIp() {
  const ip = serverInfos.value()?.localIp;
  return ip ? String(ip) : null;
}

function openUpdate() {
  platform.openUrl(
    platform.os === 'windows' ? WINDOWS_DOWNLOAD_URL : LATEST_RELEASE_URL
  );
}

function WindowControls(props: { api: WindowApi }) {
  async function close() {
    await flushPrefs();
    props.api.close();
  }

  return (
    <>
      <TextButton
        variant="ghost"
        size="small"
        iconOnly
        label="titlebar-minimize"
        icon={<MinimiseIcon class="icon icon-sm" />}
        onClick={() => props.api.minimize()}
      />
      <TextButton
        variant="ghost"
        size="small"
        iconOnly
        label="titlebar-maximize"
        icon={<MaximiseIcon class="icon icon-sm" />}
        onClick={() => props.api.toggleMaximize()}
      />
      <TextButton
        variant="ghost"
        size="small"
        iconOnly
        label="titlebar-close"
        icon={<CloseIcon class="icon icon-sm" />}
        onClick={close}
      />
    </>
  );
}

export function TitleBar() {
  const onSettings = useMatch(() => `${paths.settings}/*`);
  const [versionEl, setVersionEl] = createSignal<HTMLAnchorElement>();
  const commitTipId = createUniqueId();

  return (
    <MotionRegion
      orientation="horizontal"
      class="flex h-11 shrink-0 items-center gap-sm px-sm"
    >
      <div class="hidden shell:contents">
        <PlainLink
          to={paths.home}
          label="navbar-home"
          class="h-5.5 w-12.25 justify-center text-special"
        >
          <span class="flex translate-x-[2.17px]">
            <SlimevrLogoIcon
              aria-hidden="true"
              style={{ '--icon-size': '49.66px' }}
            />
          </span>
        </PlainLink>
        <span class="flex h-full items-center" data-electron-drag-region="true">
          SlimeVR
        </span>
      </div>
      <div class={onSettings() ? 'hidden shell:contents' : 'contents'}>
        <PlainLink
          ref={setVersionEl}
          href={RELEASES_URL}
          describedBy={commitTipId}
          class="
            h-5.5 shrink-0 rounded-lg bg-success/20 px-3 font-bold whitespace-nowrap text-success
            select-text
          "
        >
          {VERSION}
        </PlainLink>
        <Tooltip id={commitTipId} anchor={versionEl}>
          {__COMMIT_HASH__}
        </Tooltip>
      </div>
      <Show when={onSettings() && localIp()}>
        {(ip) => (
          <span class="rounded-md bg-special/20 px-sm font-bold text-special select-text">
            {ip()}
          </span>
        )}
      </Show>
      <Show when={platform.window && serverUpdate()}>
        <TextButton
          variant="ghost"
          size="small"
          iconOnly
          label="titlebar-update"
          icon={<DownloadIcon />}
          onClick={openUpdate}
        />
      </Show>
      <div class="h-full flex-1" data-electron-drag-region="true" />
      <TextButton
        to={paths.settings}
        variant="ghost"
        size="small"
        iconOnly
        label="titlebar-settings"
        icon={<GearIcon />}
      />
      <div class="hidden shell:contents">
        <TextButton
          variant="ghost"
          size="small"
          iconOnly
          label="titlebar-docs"
          icon={<QuestionIcon />}
          onClick={() => platform.openUrl(DOCS_SITE)}
        />
      </div>
      <Show when={platform.window}>
        {(api) => <WindowControls api={api()} />}
      </Show>
    </MotionRegion>
  );
}
