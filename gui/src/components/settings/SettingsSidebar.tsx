import classNames from 'classnames';
import { ReactChild, useMemo } from 'react';
import { NavLink, useLocation, useMatch } from 'react-router-dom';
import { Typography } from '../commons/Typography';

export function SettingsLink({
  to,
  scrollTo,
  children,
}: {
  to: string;
  scrollTo?: string;
  children: ReactChild;
}) {
  const { state } = useLocation();
  const doesMatch = useMatch({
    path: to,
  });

  const isActive = useMemo(() => {
    const typedState: { scrollTo?: string } = state as any;
    return (
      (doesMatch && !scrollTo && !typedState?.scrollTo) ||
      (doesMatch && typedState?.scrollTo == scrollTo)
    );
  }, [state, doesMatch]);

  return (
    <NavLink
      to={to}
      state={{ scrollTo }}
      className={classNames('pl-5 py-2 hover:bg-background-60 rounded-lg', {
        'bg-background-60': isActive,
      })}
    >
      {children}
    </NavLink>
  );
}

export function SettingsSidebar() {
  return (
    <div className="flex flex-col px-5 w-[280px] min-w-[280px] py-5 gap-3 overflow-y-auto bg-background-70 rounded-lg">
      <Typography variant="main-title">Settings</Typography>
      <div className="flex flex-col gap-3">
        <Typography variant="section-title">General</Typography>
        <div className="flex flex-col gap-2">
          <SettingsLink to="/settings/trackers" scrollTo="steamvr">
            SteamVR
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="mechanics">
            Tracker mechanics
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="fksettings">
            FK settings
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="interface">
            Interface
          </SettingsLink>
        </div>
      </div>
      <div className="flex flex-col gap-3">
        <Typography variant="section-title">OSC</Typography>
        <div className="flex flex-col gap-2">
          <SettingsLink to="/settings/osc/vrchat" scrollTo="vrchat">
            VRChat
          </SettingsLink>
        </div>
      </div>
      <div className="flex flex-col gap-3">
        <Typography variant="section-title">Utilities</Typography>
        <div className="flex flex-col gap-2">
          <SettingsLink to="/settings/serial">Serial Console</SettingsLink>
        </div>
      </div>
    </div>
  );
}
