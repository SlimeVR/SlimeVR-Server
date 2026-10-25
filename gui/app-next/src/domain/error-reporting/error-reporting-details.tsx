import { For } from 'solid-js';
import CheckIcon from '~icons/material-symbols/check-rounded';
import { t, type MessageId } from '@/lib/l10n';
import { Localized } from '@/ui/localized';

const COLLECTED: MessageId[] = [
  'settings-interface-behavior-error_tracking-collects-errors',
  'settings-interface-behavior-error_tracking-collects-system',
  'settings-interface-behavior-error_tracking-collects-recordings',
  'settings-interface-behavior-error_tracking-collects-usage',
  'settings-interface-behavior-error_tracking-collects-proportions',
];

export function ErrorReportingDetails() {
  return (
    <div class="flex flex-col gap-md rounded-lg bg-surface p-lg surface-section">
      <p>
        <Localized
          id="settings-interface-behavior-error_tracking-no_personal_data"
          elems={{ b: (text) => <b>{text}</b> }}
        />
      </p>
      <p class="font-bold">
        {t('settings-interface-behavior-error_tracking-collects')}
      </p>
      <ul class="flex flex-col gap-sm">
        <For each={COLLECTED}>
          {(item) => (
            <li class="flex items-start gap-sm">
              <span class="flex text-accent icon-sm">
                <CheckIcon />
              </span>
              {t(item)}
            </li>
          )}
        </For>
      </ul>
      <p>{t('settings-interface-behavior-error_tracking-footer')}</p>
    </div>
  );
}
