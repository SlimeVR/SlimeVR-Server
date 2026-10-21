import classNames from 'classnames';
import { NavLink } from 'react-router-dom';
import { Typography } from '@/components/commons/Typography';
import { useVRCConfig } from '@/hooks/vrc-config';
import { QuickSearchField } from './QuickSearch';

export function SettingsLink({ to, id }: { id: string; to: string }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        classNames('pl-5 py-1.5 hover:bg-background-60 rounded-lg', {
          'bg-background-60': isActive,
        })
      }
    >
      <Typography id={id} />
    </NavLink>
  );
}

export function SettingsSidebar() {
  const { state: vrcConfigState } = useVRCConfig();

  return (
    <div className="flex flex-col px-5 py-4 gap-4 overflow-y-auto bg-background-70 rounded-lg h-full">
      <QuickSearchField />
      <div className="flex flex-col gap-2">
        <Typography variant="section-title" id="settings-sidebar-general" />
        <div className="flex flex-col gap-1">
          <SettingsLink
            to="/settings/tracking"
            id="settings-sidebar-tracking"
          />
          <SettingsLink to="/settings/resets" id="settings-sidebar-resets" />
          <SettingsLink
            to="/settings/stay-aligned"
            id="settings-sidebar-stay_aligned"
          />
          <SettingsLink
            to="/settings/trackers"
            id="settings-sidebar-trackers"
          />
          <SettingsLink
            to="/settings/keybinds"
            id="settings-sidebar-keybinds"
          />
          <SettingsLink
            to="/settings/profiles"
            id="settings-sidebar-profiles"
          />
        </div>
      </div>
      <div className="flex flex-col gap-2">
        <Typography variant="section-title" id="settings-sidebar-interface" />
        <div className="flex flex-col gap-1">
          <SettingsLink
            to="/settings/appearance"
            id="settings-sidebar-appearance"
          />
          <SettingsLink
            to="/settings/behavior"
            id="settings-sidebar-behavior"
          />
          <SettingsLink to="/settings/home" id="settings-sidebar-home" />
          <SettingsLink
            to="/settings/checklist"
            id="settings-sidebar-checklist"
          />
        </div>
      </div>
      <div className="flex flex-col gap-2">
        <Typography variant="section-title" id="settings-sidebar-outputs" />
        <div className="flex flex-col gap-1">
          <SettingsLink to="/settings/routing" id="settings-sidebar-routing" />
          <SettingsLink to="/settings/driver" id="settings-sidebar-driver" />
          <SettingsLink
            to="/settings/osc/vrchat"
            id="settings-sidebar-vrchat_osc"
          />
          <SettingsLink to="/settings/osc/vmc" id="settings-sidebar-osc_vmc" />
        </div>
      </div>
      <div className="flex flex-col gap-2">
        <Typography variant="section-title" id="settings-sidebar-utils" />
        <div className="flex flex-col gap-1">
          <SettingsLink to="/onboarding/home" id="navbar-onboarding" />
          <SettingsLink to="/settings/serial" id="settings-sidebar-serial" />
          <SettingsLink to="/settings/logs" id="settings-sidebar-logs" />
          <SettingsLink
            to="/settings/firmware-tool"
            id="settings-sidebar-firmware-tool"
          />
          {vrcConfigState?.isSupported && (
            <SettingsLink
              to="/settings/vrc-warnings"
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
  );
}
