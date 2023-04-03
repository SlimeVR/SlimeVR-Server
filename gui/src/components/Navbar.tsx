import { useLocalization } from '@fluent/react';
import classnames from 'classnames';
import { ReactNode } from 'react';
import { NavLink, useMatch } from 'react-router-dom';
import { useBodyProportions } from '../hooks/body-proportions';
import { CubeIcon } from './commons/icon/CubeIcon';
import { GearIcon } from './commons/icon/GearIcon';

export function NavButton({
  to,
  children,
  match,
  state = {},
  icon,
}: {
  to: string;
  children: ReactNode;
  match?: string;
  state?: any;
  icon: ReactNode;
}) {
  const doesMatch = useMatch({
    path: match || to,
  });

  return (
    <NavLink
      to={to}
      state={state}
      className={classnames(
        'flex flex-col justify-center gap-4 w-[85px] h-[85px] rounded-md group select-text',
        {
          'bg-accent-background-50 fill-accent-background-20': doesMatch,
          'hover:bg-background-70': !doesMatch,
        }
      )}
    >
      <div className="flex justify-around">
        <div
          className={classnames('scale-150', {
            'fill-accent-lighter': doesMatch,
            'fill-background-50': !doesMatch,
          })}
        >
          {icon}
        </div>
      </div>
      <div
        className={classnames('text-center', {
          'text-accent-background-10': doesMatch,
          'text-background-10': !doesMatch,
        })}
      >
        {children}
      </div>
    </NavLink>
  );
}

export function Navbar() {
  const { l10n } = useLocalization();
  const { lastUsedPage } = useBodyProportions();

  return (
    <div className="flex flex-col px-2 pt-2">
      <div className="flex flex-col flex-grow gap-2">
        <NavButton to="/" icon={<CubeIcon></CubeIcon>}>
          {l10n.getString('navbar-home')}
        </NavButton>
        <NavButton
          to={lastUsedPage}
          match="/onboarding/body-proportions/*"
          state={{ alonePage: true }}
          icon={<GearIcon></GearIcon>}
        >
          {l10n.getString('navbar-body_proportions')}
        </NavButton>
        <NavButton
          to="/onboarding/trackers-assign"
          state={{ alonePage: true }}
          icon={<GearIcon></GearIcon>}
        >
          {l10n.getString('navbar-trackers_assign')}
        </NavButton>
        <NavButton
          to="/onboarding/mounting/choose"
          match="/onboarding/mounting/*"
          state={{ alonePage: true }}
          icon={<GearIcon></GearIcon>}
        >
          {l10n.getString('navbar-mounting')}
        </NavButton>
        <NavButton to="/onboarding/home" icon={<GearIcon></GearIcon>}>
          {l10n.getString('navbar-onboarding')}
        </NavButton>
      </div>
      <NavButton
        to="/settings/trackers"
        match="/settings/*"
        state={{ scrollTo: 'steamvr' }}
        icon={<GearIcon></GearIcon>}
      >
        {l10n.getString('navbar-settings')}
      </NavButton>
    </div>
  );
}
