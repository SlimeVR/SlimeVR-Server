import { match } from '@formatjs/intl-localematcher';
import { FluentBundle, FluentResource } from '@fluent/bundle';
import { LocalizationProvider, ReactLocalization } from '@fluent/react';
import {
  Children,
  ReactNode,
  useEffect,
  useState,
  createContext,
  useContext,
  useCallback,
  useMemo,
} from 'react';
import { exists, readTextFile, BaseDirectory } from '@tauri-apps/plugin-fs';
import { error } from '@/utils/logging';
import { invoke } from '@tauri-apps/api/core';
import { isTrayAvailable } from '@/utils/tauri';
import { langs } from './names';

export const defaultNS = 'translation';
export const DEFAULT_LOCALE = 'en';
const OVERRIDE_FILENAME = 'override.ftl';

// AppConfig path: https://docs.rs/tauri/1.2.4/tauri/api/path/fn.config_dir.html
// We doing this only once, don't want an override check to be done on runtime,
// only on launch :P
const overrideLangExists = exists(OVERRIDE_FILENAME, {
  baseDir: BaseDirectory.AppConfig,
}).catch(() => false);

// Fetch translation file
async function fetchMessages(locale: string): Promise<[string, string]> {
  const response = await fetch(`/i18n/${locale}/translation.ftl`);
  const messages = await response.text();
  return [locale, messages];
}

// Generator function for making FluentBundles from the translation file
function* lazilyParsedBundles(fetchedMessages: [string, string][]) {
  for (const [locale, messages] of fetchedMessages) {
    const resource = new FluentResource(messages);
    const bundle = new FluentBundle(locale);
    bundle.addResource(resource);
    yield bundle;
  }
}

function verifyLocale(locale: string | null): string | null {
  if (!locale) return null;
  try {
    // eslint-disable-next-line no-new
    new Intl.Locale(locale);
    return locale;
  } catch (e) {
    error(e);
    return null;
  }
}

interface AppLocalizationProviderProps {
  children: ReactNode;
}
interface I18n {
  currentLocales: string[];
  changeLocales: (userLocales: string[]) => Promise<void>;
}

const TRAY_MENU_KEYS = ['tray_menu-show', 'tray_menu-hide', 'tray_menu-quit'];

export const LangContext = createContext<I18n>(undefined as never);
export function AppLocalizationProvider({
  children,
}: AppLocalizationProviderProps) {
  const [currentLocales, setCurrentLocales] = useState([DEFAULT_LOCALE]);
  const [l10n, setL10n] = useState<ReactLocalization | null>(null);
  const changeLocales = useCallback(async (userLocales: string[]) => {
    const currentLocale = match(
      userLocales.filter((x) => verifyLocale(x) !== null),
      langs.map((x) => x.key),
      DEFAULT_LOCALE
    );
    setCurrentLocales([currentLocale]);

    const currentLocaleFile: [string, string] = (await overrideLangExists)
      ? [
          currentLocale,
          await readTextFile(OVERRIDE_FILENAME, {
            baseDir: BaseDirectory.AppConfig,
          }),
        ]
      : await fetchMessages(currentLocale);

    const fetchedMessages = [
      currentLocaleFile,
      await fetchMessages(DEFAULT_LOCALE),
    ];

    const bundles = lazilyParsedBundles(fetchedMessages);
    localStorage.setItem('i18nextLng', currentLocale);
    document.documentElement.lang = currentLocale;
    setL10n(new ReactLocalization(bundles));
  }, []);

  useEffect(() => {
    const lang = verifyLocale(localStorage.getItem('i18nextLng'));
    const array = [];
    if (lang) array.push(lang);
    changeLocales([...array, ...navigator.languages]);
    // detect hot reload translation file changes
    if (import.meta.hot) {
      import.meta.hot.on('locales-update', () => changeLocales(currentLocales));
    }
  }, [changeLocales, currentLocales]);

  useEffect(() => {
    if (l10n === null || !isTrayAvailable) return;

    const newI18n: Record<string, string> = {};
    TRAY_MENU_KEYS.forEach((key) => {
      newI18n[key] = l10n.getString(key);
    });
    const promise = invoke('update_translations', { newI18n });
    return () => {
      promise.then(() => {});
    };
  }, [l10n]);

  const values = useMemo(
    () => ({
      currentLocales,
      changeLocales,
    }),
    [changeLocales, currentLocales]
  );

  if (l10n === null) {
    return null;
  }

  return (
    <LocalizationProvider l10n={l10n}>
      <LangContext.Provider value={values}>
        {Children.only(children)}
      </LangContext.Provider>
    </LocalizationProvider>
  );
}

export function useLocaleConfig() {
  const context = useContext<I18n>(LangContext);
  if (!context) {
    throw new Error(
      'useLocaleConfig must be within a AppLocalization Provider'
    );
  }
  return context;
}
