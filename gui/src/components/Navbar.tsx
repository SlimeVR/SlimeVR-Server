import { useLocalization } from '@fluent/react';
import classnames from 'classnames';
import { ReactNode } from 'react';
import { NavLink, useMatch } from 'react-router-dom';
import { CubeIcon } from './commons/icon/CubeIcon';
import { GearIcon } from './commons/icon/GearIcon';
import { HumanIcon } from './commons/icon/HumanIcon';
import { RulerIcon } from './commons/icon/RulerIcon';
import { SparkleIcon } from './commons/icon/SparkleIcon';
import { WrenchIcon } from './commons/icon/WrenchIcons';
import { useBreakpoint } from '@/hooks/breakpoint';

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
        'flex flex-col justify-center xs:gap-4 mobile:gap-2',
        'xs:w-[85px] mobile:w-[80px] mobile:h-[80px]',
        'xs:py-3 mobile:py-4 rounded-md mobile:rounded-b-none group select-text',
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

export function MainLinks() {
  const { l10n } = useLocalization();

  return (
    <>
      <NavButton to="/" icon={<CubeIcon></CubeIcon>}>
        {l10n.getString('navbar-home')}
      </NavButton>
      <NavButton
        to="/onboarding/trackers-assign"
        state={{ alonePage: true }}
        icon={<HumanIcon></HumanIcon>}
      >
        {l10n.getString('navbar-trackers_assign')}
      </NavButton>
      <NavButton
        to="/onboarding/mounting/choose"
        match="/onboarding/mounting/*"
        state={{ alonePage: true }}
        icon={<WrenchIcon></WrenchIcon>}
      >
        {l10n.getString('navbar-mounting')}
      </NavButton>
      <NavButton
        to="/onboarding/body-proportions/choose"
        match="/onboarding/body-proportions/*"
        state={{ alonePage: true }}
        icon={<RulerIcon></RulerIcon>}
      >
        {l10n.getString('navbar-body_proportions')}
      </NavButton>
      <NavButton to="/onboarding/home" icon={<SparkleIcon></SparkleIcon>}>
        {l10n.getString('navbar-onboarding')}
      </NavButton>
    </>
  );
}

export function Navbar() {
  const { isMobile } = useBreakpoint('mobile');
  const { l10n } = useLocalization();

  return isMobile ? (
    <div className="flex flex-row justify-around px-2 pt-2 bg-background-80 gap-2">
      <MainLinks></MainLinks>
    </div>
  ) : (
    <div className="flex flex-col px-2 pt-2">
      <div className="flex flex-col flex-grow gap-2">
        <MainLinks></MainLinks>
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
