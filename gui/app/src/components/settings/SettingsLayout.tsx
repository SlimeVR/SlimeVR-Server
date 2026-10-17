import { ReactNode } from 'react';
import { Navbar } from '@/components/Navbar';
import { TopBar } from '@/components/TopBar';
import { SettingsSidebar } from './SettingsSidebar';
import { useBreakpoint } from '@/hooks/breakpoint';
import { DropdownInside } from '@/components/commons/Dropdown';
import { useLocalization } from '@fluent/react';
import { useLocation, useNavigate } from 'react-router-dom';
import './SettingsLayout.scss';
import { useVRCConfig } from '@/hooks/vrc-config';

export function SettingSelectorMobile() {
  const { l10n } = useLocalization();
  const { state: vrcConfigState } = useVRCConfig();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const links: { label: string; url: string }[] = [
    {
      label: l10n.getString('settings-sidebar-tracking'),
      url: '/settings/tracking',
    },
    {
      label: l10n.getString('settings-sidebar-trackers'),
      url: '/settings/trackers',
    },
    {
      label: l10n.getString('settings-sidebar-resets'),
      url: '/settings/resets',
    },
    {
      label: l10n.getString('settings-sidebar-stay_aligned'),
      url: '/settings/stay-aligned',
    },
    {
      label: l10n.getString('settings-sidebar-profiles'),
      url: '/settings/profiles',
    },
    {
      label: l10n.getString('settings-sidebar-appearance'),
      url: '/settings/appearance',
    },
    {
      label: l10n.getString('settings-sidebar-behavior'),
      url: '/settings/behavior',
    },
    {
      label: l10n.getString('settings-sidebar-home'),
      url: '/settings/home',
    },
    {
      label: l10n.getString('settings-sidebar-checklist'),
      url: '/settings/checklist',
    },
    {
      label: l10n.getString('settings-sidebar-keybinds'),
      url: '/settings/keybinds',
    },
    {
      label: l10n.getString('settings-sidebar-routing'),
      url: '/settings/routing',
    },
    {
      label: l10n.getString('settings-sidebar-driver'),
      url: '/settings/driver',
    },
    {
      label: l10n.getString('settings-sidebar-vrchat_osc'),
      url: '/settings/osc/vrchat',
    },
    {
      label: l10n.getString('settings-sidebar-osc_vmc'),
      url: '/settings/osc/vmc',
    },
    {
      label: l10n.getString('settings-sidebar-serial'),
      url: '/settings/serial',
    },
    {
      label: l10n.getString('settings-sidebar-logs'),
      url: '/settings/logs',
    },
    {
      label: l10n.getString('settings-sidebar-firmware-tool'),
      url: '/settings/firmware-tool',
    },
    ...(vrcConfigState?.isSupported
      ? [
          {
            label: l10n.getString('settings-sidebar-vrc_warnings'),
            url: '/settings/vrc-warnings',
          },
        ]
      : []),
    {
      label: l10n.getString('settings-sidebar-advanced'),
      url: '/settings/advanced',
    },
    {
      label: l10n.getString('navbar-onboarding'),
      url: '/onboarding/home',
    },
  ];

  return (
    <div className="fixed top-12 z-50 px-4 w-full">
      <DropdownInside
        onChange={(value) => navigate(value)}
        display="block"
        items={links.map(({ label, url }) => ({ label, value: url }))}
        variant="tertiary"
        direction="down"
        // There is always an option selected; placeholder is not used.
        placeholder=""
        name="link"
        value={pathname}
      />
    </div>
  );
}

export function SettingsLayout({ children }: { children: ReactNode }) {
  const { isMobile } = useBreakpoint('mobile');
  return (
    <>
      <div className="settings-layout h-full">
        <div
          data-nav-region="shell"
          data-nav-area="topbar"
          style={{ gridArea: 't' }}
        >
          <TopBar />
        </div>
        <div
          data-nav-region="shell"
          data-nav-area="navbar"
          style={{ gridArea: 'n' }}
        >
          <Navbar />
        </div>
        <div
          data-nav-region="shell"
          data-nav-area="settings-nav"
          style={{ gridArea: 's' }}
          className="my-2 mobile:hidden"
        >
          <SettingsSidebar />
        </div>
        <div
          data-nav-region="page"
          style={{ gridArea: 'c' }}
          className="xs:pl-2 xs:pr-2 xs:pb-2 xs:mt-2 mobile:mt-7 overflow-y-auto"
        >
          {isMobile && <SettingSelectorMobile />}
          {children}
        </div>
      </div>
    </>
  );
}
