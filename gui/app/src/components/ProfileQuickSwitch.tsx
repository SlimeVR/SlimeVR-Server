import classNames from 'classnames';
import { ReactNode, useEffect, useRef } from 'react';
import { useLocalization } from '@fluent/react';
import { ProfileKind } from 'solarxr-protocol';
import { ArrowDownIcon } from './commons/icon/ArrowIcons';
import { CheckIcon } from './commons/icon/CheckIcon';
import { HumanIcon } from './commons/icon/HumanIcon';
import { TuneIcon } from './commons/icon/TuneIcon';
import { PopoverMenu, PopoverMenuItem } from './commons/PopoverMenu';
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
              <PopoverMenuItem current={active} onClick={() => onSelect(name)}>
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
              </PopoverMenuItem>
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
      <PopoverMenuItem to="/settings/profiles" onClick={onClose}>
        <Typography id="titlebar-profiles-manage" />
      </PopoverMenuItem>
    </div>
  );
}

export function ProfileQuickSwitch() {
  const { l10n } = useLocalization();
  const { isMobile } = useBreakpoint('mobile');
  const profiles = useProfiles();

  if (!profiles) return null;

  return (
    <PopoverMenu
      anchorClassName="min-w-0"
      drawerTitle={
        <Typography variant="section-title" id="titlebar-profiles" />
      }
      trigger={(triggerProps, open) => (
        <Tooltip
          preferedDirection="bottom"
          disabled={open}
          content={<Typography id="titlebar-profiles-tooltip" />}
        >
          <button
            type="button"
            {...triggerProps}
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
      )}
    >
      {({ open, close }) => (
        <ProfileMenu profiles={profiles} open={open} onClose={close} />
      )}
    </PopoverMenu>
  );
}
