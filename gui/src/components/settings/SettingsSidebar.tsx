import classNames from 'classnames';
import { useMemo } from 'react';
import { NavLink, useLocation, useMatch } from 'react-router-dom';
import { Typography } from '@/components/commons/Typography';
import { useVRCConfig } from '@/hooks/vrc-config';

export function SettingsLink({
  to,
  scrollTo,
  id,
}: {
  id: string;
  to: string;
  scrollTo?: string;
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
      <Typography id={id} />
    </NavLink>
  );
}

export function SettingsSidebar() {
  const { state: vrcConfigState } = useVRCConfig();

  return (
    <div className="flex flex-col px-5 py-5 gap-3 overflow-y-auto bg-background-70 rounded-lg h-full">
      <Typography variant="main-title" id="settings-sidebar-title" />
      <div className="flex flex-col gap-3">
        <Typography variant="section-title" id="settings-sidebar-general" />
        <div className="flex flex-col gap-2">
          <SettingsLink
            to="/settings/trackers"
            scrollTo="steamvr"
            id="settings-sidebar-steamvr"
          />
          <SettingsLink
            to="/settings/trackers"
            scrollTo="stayaligned"
            id="settings-sidebar-stay_aligned"
          />
          <SettingsLink
            to="/settings/trackers"
            scrollTo="mechanics"
            id="settings-sidebar-tracker_mechanics"
          />
          <SettingsLink
            to="/settings/trackers"
            scrollTo="fksettings"
            id="settings-sidebar-fk_settings"
          />
          <SettingsLink
            to="/settings/trackers"
            scrollTo="gestureControl"
            id="settings-sidebar-gesture_control"
          />
        </div>
      </div>
      <div className="flex flex-col gap-3">
        <Typography variant="section-title" id="settings-sidebar-interface" />
        <div className="flex flex-col gap-2">
          <SettingsLink
            to="/settings/interface"
            scrollTo="notifications"
            id="settings-sidebar-notifications"
          />
          <SettingsLink
            to="/settings/interface"
            scrollTo="behavior"
            id="settings-sidebar-behavior"
          />
          <SettingsLink
            to="/settings/interface"
            scrollTo="appearance"
            id="settings-sidebar-appearance"
          />
          <SettingsLink
            to="/settings/interface/home"
            scrollTo="home"
            id="settings-sidebar-home"
          />
          <SettingsLink
            to="/settings/interface/home"
            scrollTo="checklist"
            id="settings-sidebar-checklist"
          />
        </div>
        <div className="flex flex-col gap-3">
          <Typography variant="section-title">OSC</Typography>
          <div className="flex flex-col gap-2">
            <SettingsLink
              to="/settings/osc/router"
              scrollTo="router"
              id="settings-sidebar-osc_router"
            />
            <SettingsLink
              to="/settings/osc/vrchat"
              scrollTo="vrchat"
              id="settings-sidebar-osc_trackers"
            />
            <SettingsLink
              to="/settings/osc/vmc"
              scrollTo="vmc"
              id="settings-sidebar-osc_vmc"
            />
          </div>
        </div>
        <div className="flex flex-col gap-3">
          <Typography variant="section-title" id="settings-sidebar-utils" />
          <div className="flex flex-col gap-2">
            <SettingsLink to="/settings/serial" id="settings-sidebar-serial" />
            <SettingsLink
              to="/settings/firmware-tool"
              id="settings-sidebar-firmware-tool"
            />
            <SettingsLink to="/onboarding/home" id="navbar-onboarding" />
            {vrcConfigState?.isSupported && (
              <SettingsLink
                to="/vrc-warnings"
                id="settings-sidebar-vrc_warnings"
              />
            )}
            <SettingsLink
              to="/settings/advanced"
              id="settings-sidebar-advanced"
            />
          </div>
        </div>
      </div>
    </div>
  );
}
