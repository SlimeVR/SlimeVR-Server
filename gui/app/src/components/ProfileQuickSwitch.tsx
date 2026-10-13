import classNames from 'classnames';
import {
  CSSProperties,
  ReactNode,
  useEffect,
  useId,
  useRef,
  useState,
} from 'react';
import { useLocalization } from '@fluent/react';
import { ProfileKind } from 'solarxr-protocol';
import { ArrowDownIcon } from './commons/icon/ArrowIcons';
import { CheckIcon } from './commons/icon/CheckIcon';
import { HumanIcon } from './commons/icon/HumanIcon';
import { TuneIcon } from './commons/icon/TuneIcon';
import { Clickable } from './commons/Clickable';
import { Drawer } from './commons/Drawer';
import { Tooltip } from './commons/Tooltip';
import { Typography } from './commons/Typography';
import { useBreakpoint } from '@/hooks/breakpoint';
import {
  ProfileNames,
  Profiles,
  useProfileOperations,
  useProfiles,
} from '@/hooks/profiles';

function ProfileSection({
  titleId,
  icon,
  profiles,
  open,
  onSelect,
}: {
  titleId: string;
  icon: ReactNode;
  profiles: ProfileNames;
  open: boolean;
  onSelect: (name: string) => void;
}) {
  const listRef = useRef<HTMLUListElement>(null);

  // Long lists open scrolled to the active profile
  useEffect(() => {
    const list = listRef.current;
    if (!open || !list) return;
    const active = list.querySelector<HTMLElement>('[aria-current]');
    if (!active) return;
    list.scrollTop =
      active.offsetTop - list.clientHeight / 2 + active.offsetHeight / 2;
  }, [open]);

  return (
    <div className="flex flex-col min-w-0 py-1">
      <div className="flex items-center gap-2 px-4 py-2 fill-background-10">
        {icon}
        <Typography bold id={titleId} />
      </div>
      <ul
        ref={listRef}
        className="relative flex flex-col min-w-0 max-h-[30vh] overflow-y-auto overscroll-contain dropdown-scroll"
      >
        {profiles.names.map((name) => {
          const active = name === profiles.active;
          return (
            <li key={name}>
              <Clickable
                current={active || undefined}
                onClick={() => onSelect(name)}
                className={classNames(
                  'flex w-full items-center gap-2 px-4 py-2 mobile:py-3 hover:bg-background-50',
                  active && 'bg-background-50/50'
                )}
              >
                <span className="w-3 flex justify-center fill-accent-background-10 shrink-0">
                  {active && <CheckIcon size={10} />}
                </span>
                <span className="min-w-0 truncate">
                  <Typography
                    tag="span"
                    whitespace="whitespace-nowrap"
                    bold={active}
                  >
                    {name}
                  </Typography>
                </span>
              </Clickable>
            </li>
          );
        })}
      </ul>
    </div>
  );
}

function ProfileMenu({
  profiles,
  open,
  onClose,
}: {
  profiles: Profiles;
  open: boolean;
  onClose: () => void;
}) {
  const { switchProfile } = useProfileOperations();

  const select = (kind: ProfileKind, active: string, name: string) => {
    onClose();
    if (name !== active) switchProfile(kind, name);
  };

  return (
    <div className="flex flex-col w-full min-w-0 divide-y divide-background-50">
      <ProfileSection
        titleId="titlebar-profiles-settings"
        icon={<TuneIcon size={14} />}
        profiles={profiles.settings}
        open={open}
        onSelect={(name) =>
          select(ProfileKind.SETTINGS, profiles.settings.active, name)
        }
      />
      <ProfileSection
        titleId="titlebar-profiles-user"
        icon={<HumanIcon width={12} />}
        profiles={profiles.user}
        open={open}
        onSelect={(name) =>
          select(ProfileKind.USER, profiles.user.active, name)
        }
      />
      <Clickable
        to="/settings/profiles"
        onClick={onClose}
        className="flex items-center px-4 py-2 mobile:py-3 hover:bg-background-50"
      >
        <Typography id="titlebar-profiles-manage" />
      </Clickable>
    </div>
  );
}

export function ProfileQuickSwitch() {
  const { l10n } = useLocalization();
  const { isMobile } = useBreakpoint('mobile');
  const profiles = useProfiles();
  const [open, setOpen] = useState(false);
  const popoverRef = useRef<HTMLDivElement>(null);
  const id = `profile-switch-${useId().replace(/:/g, '')}`;
  const anchorName = `--${id}`;

  useEffect(() => {
    const el = popoverRef.current;
    if (!el) return;
    el.setAttribute('popover', 'auto');
    const onToggle = (e: Event) =>
      setOpen((e as ToggleEvent).newState === 'open');
    el.addEventListener('toggle', onToggle);
    return () => el.removeEventListener('toggle', onToggle);
  }, [profiles !== null, isMobile]);

  if (!profiles) return null;

  const close = () => {
    popoverRef.current?.hidePopover();
    setOpen(false);
  };

  return (
    <>
      <div className="flex min-w-0" style={{ anchorName } as CSSProperties}>
        <Tooltip
          preferedDirection="bottom"
          disabled={open}
          content={<Typography id="titlebar-profiles-tooltip" />}
        >
          <button
            type="button"
            {...(isMobile
              ? { onClick: () => setOpen(true) }
              : { popovertarget: id })}
            aria-expanded={open}
            aria-controls={id}
            aria-label={l10n.getString('titlebar-profiles')}
            className={classNames(
              'flex items-center gap-2 h-7 pl-2.5 pr-2 rounded-lg min-w-0 shrink',
              'text-background-10 fill-background-10 hover:bg-background-50',
              open ? 'bg-background-50' : 'bg-background-60'
            )}
          >
            <TuneIcon size={14} />
            {!isMobile && (
              <>
                <span className="min-w-0 max-w-[140px] truncate">
                  <Typography tag="span" whitespace="whitespace-nowrap">
                    {profiles.settings.active}
                  </Typography>
                </span>
                <span className="w-px h-3.5 bg-background-40 shrink-0" />
                <span className="flex shrink-0">
                  <HumanIcon width={12} />
                </span>
                <span className="min-w-0 max-w-[140px] truncate">
                  <Typography tag="span" whitespace="whitespace-nowrap">
                    {profiles.user.active}
                  </Typography>
                </span>
              </>
            )}
            <span
              className={classNames(
                'shrink-0 transition-transform',
                open && 'rotate-180'
              )}
            >
              <ArrowDownIcon size={12} />
            </span>
          </button>
        </Tooltip>
      </div>
      {isMobile ? (
        <Drawer
          isOpen={open}
          onClose={close}
          title={<Typography variant="section-title" id="titlebar-profiles" />}
        >
          <div id={id}>
            <ProfileMenu profiles={profiles} open={open} onClose={close} />
          </div>
        </Drawer>
      ) : (
        <div
          ref={popoverRef}
          id={id}
          className="dropdown-popover fixed inset-auto m-0 mt-2 p-0 w-[260px] overflow-hidden rounded-lg border border-background-50 bg-background-60 shadow-lg"
          style={
            {
              positionAnchor: anchorName,
              positionArea: 'bottom span-left',
              positionTryFallbacks: 'flip-inline',
            } as CSSProperties
          }
        >
          <ProfileMenu profiles={profiles} open={open} onClose={close} />
        </div>
      )}
    </>
  );
}
