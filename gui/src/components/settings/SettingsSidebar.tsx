import classNames from 'classnames';
import { ReactNode, useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { NavLink, useLocation, useMatch } from 'react-router-dom';
import { Typography } from '../commons/Typography';

export function SettingsLink({
  to,
  scrollTo,
  children,
}: {
  to: string;
  scrollTo?: string;
  children: ReactNode;
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
  const { t } = useTranslation();
  return (
    <div className="flex flex-col px-5 w-[280px] min-w-[280px] py-5 gap-3 overflow-y-auto bg-background-70 rounded-lg">
      <Typography variant="main-title">
        {t('settings.sidebar.title')}
      </Typography>
      <div className="flex flex-col gap-3">
        <Typography variant="section-title">
          {t('settings.sidebar.general')}
        </Typography>
        <div className="flex flex-col gap-2">
          <SettingsLink to="/settings/trackers" scrollTo="steamvr">
            SteamVR
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="mechanics">
            {t('settings.sidebar.tracker-mechanics')}
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="fksettings">
            {t('settings.sidebar.fk-settings')}
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="gestureControl">
            {t('settings.sidebar.gesture-control')}
          </SettingsLink>
          <SettingsLink to="/settings/trackers" scrollTo="interface">
            {t('settings.sidebar.interface')}
          </SettingsLink>
        </div>
      </div>
      <div className="flex flex-col gap-3">
        <Typography variant="section-title">OSC</Typography>
        <div className="flex flex-col gap-2">
          <SettingsLink to="/settings/osc/router" scrollTo="router">
            {t('settings.sidebar.osc-router')}
          </SettingsLink>
        </div>
        <div className="flex flex-col gap-2">
          <SettingsLink to="/settings/osc/vrchat" scrollTo="vrchat">
            VRChat
          </SettingsLink>
        </div>
      </div>
      <div className="flex flex-col gap-3">
        <Typography variant="section-title">
          {t('settings.sidebar.utils')}
        </Typography>
        <div className="flex flex-col gap-2">
          <SettingsLink to="/settings/serial">
            {t('settings.sidebar.serial')}
          </SettingsLink>
        </div>
      </div>
    </div>
  );
}
