import { For, Show } from 'solid-js';
import WizardIcon from '~icons/material-symbols/auto-awesome-rounded';
import { t } from '@/lib/l10n';
import { TextListLink } from '@/ui/list-link';
import { MotionList } from '@/ui/motion';
import { QuickSearchField } from './quick-search/quick-search';
import { pageAvailable, settingsSections } from './settings-pages';

export interface SettingsLinks {
  base: string;
  wizard: string;
}

export function SettingsMenu(props: {
  links: SettingsLinks;
  chevron?: boolean;
}) {
  return (
    <div class="flex flex-col gap-lg">
      <QuickSearchField />
      <For each={settingsSections}>
        {(section) => (
          <section class="flex flex-col gap-sm">
            <h2 class="px-md text-section uppercase">{t(section.label)}</h2>
            <MotionList class="flex flex-col gap-xs">
              <For each={section.pages}>
                {(entry) => (
                  <Show when={pageAvailable(entry.page)}>
                    <TextListLink
                      to={`${props.links.base}/${entry.page.path}`}
                      label={entry.page.title}
                      icon={<entry.page.icon class="icon" />}
                      chevron={props.chevron}
                    />
                  </Show>
                )}
              </For>
            </MotionList>
          </section>
        )}
      </For>
      <section class="flex flex-col gap-sm">
        <h2 class="px-md text-section uppercase">
          {t('settings-sidebar-utils')}
        </h2>
        <MotionList class="flex flex-col gap-xs">
          <TextListLink
            to={props.links.wizard}
            label="navbar-onboarding"
            icon={<WizardIcon class="icon" />}
            chevron={props.chevron}
          />
        </MotionList>
      </section>
    </div>
  );
}
