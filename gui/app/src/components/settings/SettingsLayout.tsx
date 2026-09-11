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

  const links: { label: string; value: { url: string; scrollTo?: string } }[] =
    [
      {
        label: l10n.getString('settings-sidebar-general'),
        value: { url: '/settings/trackers', scrollTo: 'stayAligned' },
      },
      {
        label: l10n.getString('settings-sidebar-interface'),
        value: { url: '/settings/interface', scrollTo: 'notifications' },
      },
      {
        label: l10n.getString('settings-sidebar-keybinds'),
        value: { url: '/settings/keybinds' },
      },
      {
        label: l10n.getString('settings-sidebar-routing'),
        value: { url: '/settings/routing', scrollTo: 'routing' },
      },
      {
        label: l10n.getString('settings-sidebar-driver'),
        value: { url: '/settings/driver', scrollTo: 'driver' },
      },
      {
        label: l10n.getString('settings-sidebar-osc_trackers'),
        value: { url: '/settings/osc/vrchat', scrollTo: 'vrchat' },
      },
      {
        label: 'VMC',
        value: { url: '/settings/osc/vmc', scrollTo: 'vmc' },
      },
      {
        label: l10n.getString('settings-sidebar-serial'),
        value: { url: '/settings/serial' },
      },
      {
        label: l10n.getString('settings-sidebar-firmware-tool'),
        value: { url: '/settings/firmware-tool' },
      },
      ...(vrcConfigState?.isSupported
        ? [
            {
              label: l10n.getString('settings-sidebar-vrc_warnings'),
              value: { url: '/settings/vrc-warnings' },
            },
          ]
        : []),
      {
        label: l10n.getString('settings-sidebar-advanced'),
        value: { url: '/settings/advanced' },
      },
      {
        label: l10n.getString('navbar-onboarding'),
        value: { url: '/onboarding/home' },
      },
    ];

  const onSubmit = ({ link }: { link: string }) => {
    const item = links.find(({ value: { url } }) => url === link);

    if (!item) return;
    navigate(item.value.url, { state: { scrollTo: item.value.scrollTo } });
  };

  return (
    <div className="fixed top-12 z-50 px-4 w-full">
      <DropdownInside
        onChange={(value) => onSubmit({ link: value })}
        display="block"
        items={links.map(({ label, value: { url: value } }) => ({
          label,
          value,
        }))}
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
          className="xs:pl-2 xs:pb-2 xs:mt-2 mobile:mt-7 overflow-y-auto"
        >
          {isMobile && <SettingSelectorMobile />}
          {children}
        </div>
      </div>
    </>
  );
}
