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
} from 'react';
import { exists, readTextFile, BaseDirectory } from '@tauri-apps/plugin-fs';
import { error } from '@/utils/logging';
import { invoke } from '@tauri-apps/api/core';
import { isTrayAvailable } from '@/utils/tauri';

export const defaultNS = 'translation';
export const DEFAULT_LOCALE = 'en';
const OVERRIDE_FILENAME = 'override.ftl';

export const langs = [
  {
    emoji: (await import('@twemoji/svg/1f1e6-1f1ea.svg')).default,
    name: 'عربى',
    key: 'ar',
  },
  {
    emoji: (await import('@twemoji/svg/1f1e8-1f1ff.svg')).default,
    name: 'Čeština',
    key: 'cs',
  },
  {
    emoji: (await import('@twemoji/svg/1f1e9-1f1f0.svg')).default,
    name: 'Dansk',
    key: 'da',
  },
  {
    emoji: (await import('@twemoji/svg/1f1e9-1f1ea.svg')).default,
    name: 'Deutsch',
    key: 'de',
  },
  {
    emoji: (await import('@twemoji/svg/1f1fa-1f1f8.svg')).default,
    name: 'English',
    key: 'en',
  },
  {
    emoji: (await import('@twemoji/svg/1f30e.svg')).default,
    name: 'Español Latinoamericano',
    key: 'es-419',
  },
  {
    emoji: (await import('@twemoji/svg/1f1ea-1f1f8.svg')).default,
    name: 'Español España',
    key: 'es-ES',
  },
  {
    emoji: (await import('@twemoji/svg/1f1ea-1f1ea.svg')).default,
    name: 'Eesti',
    key: 'et',
  },
  {
    emoji: (await import('@twemoji/svg/1f1eb-1f1ee.svg')).default,
    name: 'Suomi',
    key: 'fi',
  },
  {
    emoji: (await import('@twemoji/svg/1f1eb-1f1f7.svg')).default,
    name: 'Français',
    key: 'fr',
  },
  {
    emoji: (await import('@twemoji/svg/1f1ee-1f1f9.svg')).default,
    name: 'Italiano',
    key: 'it',
  },
  {
    emoji: (await import('@twemoji/svg/1f1ef-1f1f5.svg')).default,
    name: '日本語',
    key: 'ja',
  },
  {
    emoji: (await import('@twemoji/svg/1f1f0-1f1f7.svg')).default,
    name: '한국어',
    key: 'ko',
  },
  {
    emoji: (await import('@twemoji/svg/1f1f3-1f1f4.svg')).default,
    name: 'Norsk bokmål',
    key: 'nb-NO',
  },
  {
    emoji: (await import('@twemoji/svg/1f1f3-1f1f1.svg')).default,
    name: 'Nederlands',
    key: 'nl',
  },
  {
    emoji: (await import('@twemoji/svg/1f1f5-1f1f1.svg')).default,
    name: 'Polski',
    key: 'pl',
  },
  {
    emoji: (await import('@twemoji/svg/1f1e7-1f1f7.svg')).default,
    name: 'Português Brasileiro',
    key: 'pt-BR',
  },
  {
    emoji: (await import('@twemoji/svg/1f1f7-1f1fa.svg')).default,
    name: 'Русский',
    key: 'ru',
  },
  {
    emoji: (await import('@twemoji/svg/1f1fa-1f1e6.svg')).default,
    name: 'Українська',
    key: 'uk',
  },
  {
    emoji: (await import('@twemoji/svg/1f1fb-1f1f3.svg')).default,
    name: 'Tiếng Việt',
    key: 'vi',
  },
  {
    emoji: (await import('@twemoji/svg/1f1e8-1f1f3.svg')).default,
    name: '简体中文',
    key: 'zh-Hans',
  },
  {
    emoji: (await import('@twemoji/svg/1f9cb.svg')).default,
    name: '繁體中文',
    key: 'zh-Hant',
  },
  {
    emoji: (await import('@twemoji/svg/1f97a.svg')).default,
    name: 'Engwish~ OwO',
    key: 'en-x-owo',
  },
];

// AppConfig path: https://docs.rs/tauri/1.2.4/tauri/api/path/fn.config_dir.html
// We doing this only once, don't want an override check to be done on runtime,
// only on launch :P
const overrideLangExists = exists(OVERRIDE_FILENAME).catch(() => false);

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
interface i18n {
  currentLocales: string[];
  changeLocales: (userLocales: string[]) => Promise<void>;
}

const TRAY_MENU_KEYS = ['tray_menu-show', 'tray_menu-hide', 'tray_menu-quit'];

export const LangContext = createContext<i18n>(undefined as never);
export function AppLocalizationProvider(props: AppLocalizationProviderProps) {
  const [currentLocales, setCurrentLocales] = useState([DEFAULT_LOCALE]);
  const [l10n, setL10n] = useState<ReactLocalization | null>(null);

  async function changeLocales(userLocales: string[]) {
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
  }

  useEffect(() => {
    const lang = verifyLocale(localStorage.getItem('i18nextLng'));
    const array = [];
    if (lang) array.push(lang);
    changeLocales([...array, ...navigator.languages]);
    // detect hot reload translation file changes
    if (import.meta.hot) {
      import.meta.hot.on('locales-update', () => changeLocales(currentLocales));
    }
  }, []);

  useEffect(() => {
    if (l10n === null || !isTrayAvailable) return;

    const newI18n: Record<string, string> = {};
    TRAY_MENU_KEYS.forEach((key) => {
      newI18n[key] = l10n.getString(key);
    });
    const promise = invoke('update_translations', { newI18n });
    return () => {
      // eslint-disable-next-line @typescript-eslint/no-empty-function
      promise.then(() => {});
    };
  }, [l10n]);

  if (l10n === null) {
    return <></>;
  }

  return (
    <>
      <LocalizationProvider l10n={l10n}>
        <LangContext.Provider value={{ currentLocales, changeLocales }}>
          {Children.only(props.children)}
        </LangContext.Provider>
      </LocalizationProvider>
    </>
  );
}

export function useLocaleConfig() {
  const context = useContext<i18n>(LangContext);
  if (!context) {
    throw new Error(
      'useLocaleConfig must be within a AppLocalization Provider'
    );
  }
  return context;
}
