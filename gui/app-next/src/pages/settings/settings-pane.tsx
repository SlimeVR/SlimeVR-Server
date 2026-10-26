import { createContext, type JSX, useContext } from 'solid-js';
import ArrowBackIcon from '~icons/material-symbols/arrow-back-rounded';
import type { SettingsPageDef } from '@/lib/settings-def';
import { TextButton } from '@/ui/button';
import { Page } from '@/ui/page';

export const SettingsBase = createContext<string>();

// Page frame of a settings page, title and icon from its declaration. On
// phones a back button to the page list takes the icon's place
export function SettingsPane(props: {
  page: SettingsPageDef;
  children: JSX.Element;
}) {
  const base = useContext(SettingsBase);

  return (
    <Page
      title={props.page.title}
      icon={<props.page.icon class="icon" />}
      back={
        base && (
          <TextButton
            to={base}
            size="small"
            round
            iconOnly
            label="settings-back"
            icon={<ArrowBackIcon class="icon" />}
          />
        )
      }
      gap="2xl"
    >
      {props.children}
    </Page>
  );
}
