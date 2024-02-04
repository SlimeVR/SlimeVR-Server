import { ReactNode, useEffect } from 'react';
import { useElemSize, useLayout } from '@/hooks/layout';
import { Navbar } from '@/components/Navbar';
import { TopBar } from '@/components/TopBar';
import { SettingsSidebar } from './SettingsSidebar';
import { useBreakpoint } from '@/hooks/breakpoint';
import { Dropdown } from '@/components/commons/Dropdown';
import { useForm } from 'react-hook-form';
import { useLocalization } from '@fluent/react';
import { useLocation, useNavigate } from 'react-router-dom';

export function SettingSelectorMobile() {
  const { l10n } = useLocalization();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const links: { label: string; value: { url: string; scrollTo?: string } }[] =
    [
      {
        label: l10n.getString('settings-sidebar-general'),
        value: { url: '/settings/trackers', scrollTo: 'steamvr' },
      },
      {
        label: l10n.getString('settings-sidebar-interface'),
        value: { url: '/settings/interface', scrollTo: 'appearance' },
      },
      {
        label: l10n.getString('settings-sidebar-osc_router'),
        value: { url: '/settings/osc/router', scrollTo: 'router' },
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
    ];

  const { control, watch, handleSubmit, setValue } = useForm<{
    link: string;
  }>({
    defaultValues: { link: links[0].value.url },
  });

  useEffect(() => {
    // This works because the component gets mounted/unmounted when switching beween desktop or mobile layout
    setValue('link', pathname, { shouldDirty: false, shouldTouch: false });
  }, []);

  useEffect(() => {
    const subscription = watch(() => handleSubmit(onSubmit)());
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = ({ link }: { link: string }) => {
    const item = links.find(({ value: { url } }) => url === link);

    if (!item) return;
    navigate(item.value.url, { state: { scrollTo: item.value.scrollTo } });
  };

  return (
    <div className="fixed top-12 z-50 px-4 w-full">
      <Dropdown
        control={control}
        display="block"
        items={links.map(({ label, value: { url: value } }) => ({
          label,
          value,
        }))}
        variant="tertiary"
        direction="down"
        // There is always an option selected placholder is not used
        placeholder=""
        name="link"
      ></Dropdown>
    </div>
  );
}

export function SettingsLayoutRoute({ children }: { children: ReactNode }) {
  const { layoutHeight, ref } = useLayout<HTMLDivElement>();
  const { height, ref: navRef } = useElemSize<HTMLDivElement>();
  const { isMobile } = useBreakpoint('mobile');
  return (
    <>
      <TopBar></TopBar>
      <div ref={ref} className="flex-grow" style={{ height: layoutHeight }}>
        <div className="flex h-full xs:pb-3">
          {!isMobile && <Navbar></Navbar>}
          <div className="h-full w-full gap-2 flex">
            {!isMobile && <SettingsSidebar></SettingsSidebar>}
            <div className="w-full flex flex-col">
              {isMobile && <SettingSelectorMobile></SettingSelectorMobile>}
              <div
                className="flex flex-col overflow-y-auto xs:pr-1 xs:mr-1 mobile:pt-7 pb-3"
                style={{ minHeight: layoutHeight - height }}
              >
                {children}
              </div>
              <div ref={navRef}>{isMobile && <Navbar></Navbar>}</div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
