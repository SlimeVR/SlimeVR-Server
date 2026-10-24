import { negotiateLanguages } from '@fluent/langneg';
import { createSignal, untrack } from 'solid-js';
import * as v from 'valibot';
import {
  inContextMode,
  reloadInContext,
  startCrowdinInContext,
} from '@/lib/l10n/crowdin';
import { createBundle, setL10nBundles, t } from '@/lib/l10n/translate';
import { platform } from '@/lib/platform';
import { definePref } from '@/lib/storage';

export const DEFAULT_LOCALE = 'en';
// Crowdin pseudo-language, its strings are edit markers for the in-context tool
const IN_CONTEXT_LOCALE = 'ach-UG';

// Folders under public/i18n offered to users
export const availableLocales = [
  'ar',
  'cs',
  'da',
  'de',
  'en',
  'es-419',
  'es-ES',
  'et',
  'fi',
  'fr',
  'it',
  'ja',
  'ko',
  'nb-NO',
  'nl',
  'pl',
  'pt-BR',
  'ru',
  'sv-SE',
  'uk',
  'th',
  'vi',
  'zh-Hans',
  'zh-Hant',
  'en-x-owo',
] as const;

const langPref = definePref('lang', v.string(), DEFAULT_LOCALE);

const [locale, setLocale] = createSignal<string>(DEFAULT_LOCALE);
export { locale };

// Loads run one after another, so the last call is applied last
let queue: Promise<void> = Promise.resolve();
let initialized = false;

async function fetchMessages(locale: string) {
  const response = await fetch(`/i18n/${locale}/translation.ftl`);
  if (!response.ok)
    throw new Error(`fetching ${locale} translations: ${response.status}`);
  return response.text();
}

function validLocales(requested: readonly string[]) {
  return requested.filter((tag) => {
    try {
      Intl.getCanonicalLocales(tag);
      return true;
    } catch {
      return false;
    }
  });
}

function syncTrayTranslations() {
  if (inContextMode === 'active') return;
  platform.translations?.set({
    'tray_menu-show': t('tray_menu-show'),
    'tray_menu-hide': t('tray_menu-hide'),
    'tray_menu-quit': t('tray_menu-quit'),
  });
}

async function applyLocale(requested: readonly string[]) {
  const inContext = inContextMode === 'active';
  const [match = DEFAULT_LOCALE] = negotiateLanguages(
    validLocales(requested),
    availableLocales,
    {
      defaultLocale: DEFAULT_LOCALE,
      strategy: 'lookup',
    }
  );

  const override = inContext ? false : await platform.translations?.override();
  const [current, fallback] = await Promise.all([
    override || fetchMessages(inContext ? IN_CONTEXT_LOCALE : match),
    fetchMessages(DEFAULT_LOCALE),
  ]);

  setL10nBundles([
    createBundle(match, current),
    createBundle(DEFAULT_LOCALE, fallback),
  ]);
  setLocale(match);
  document.documentElement.lang = match;
  syncTrayTranslations();
}

export function loadLocale(requested: readonly string[]) {
  const run = queue.catch(() => {}).then(() => applyLocale(requested));
  queue = run;
  return run;
}

export function setLanguage(lang: string) {
  langPref.set(lang);
  return loadLocale([lang]);
}

export function savedLanguage() {
  return langPref();
}

export async function initL10n() {
  if (initialized) return;
  initialized = true;

  await loadLocale([langPref()]);

  if (inContextMode === 'active') {
    startCrowdinInContext();
    platform.translations?.onCrowdinPopupClosed(() => reloadInContext('active'));
  }
  // Reload bundles when a .ftl file changes in dev
  import.meta.hot?.on('locales-update', () => void loadLocale([untrack(locale)]));
}
