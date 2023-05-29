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
import { exists, readTextFile, BaseDirectory } from '@tauri-apps/api/fs';

export const defaultNS = 'translation';
export const DEFAULT_LOCALE = 'en';
const OVERRIDE_FILENAME = 'override.ftl';

export const langs = [
  {
    name: '🇦🇪 عربى',
    key: 'ar',
  },
  {
    name: '🇨🇿 Čeština',
    key: 'cs',
  },
  {
    name: '🇩🇰 Dansk',
    key: 'da',
  },
  {
    name: '🇩🇪 Deutsch',
    key: 'de',
  },
  {
    name: '🇺🇸 English',
    key: 'en',
  },
  {
    name: '🌎 Español Latinoamericano',
    key: 'es-419',
  },
  {
    name: '🇪🇪 Eesti',
    key: 'et',
  },
  {
    name: '🇫🇮 Suomi',
    key: 'fi',
  },
  {
    name: '🇫🇷 Français',
    key: 'fr',
  },
  {
    name: '🇮🇹 Italiano',
    key: 'it',
  },
  {
    name: '🇯🇵 日本語',
    key: 'ja',
  },
  {
    name: '🇰🇷 한국어',
    key: 'ko',
  },
  {
    name: '🇳🇴  Norsk bokmål',
    key: 'nb-NO',
  },
  {
    name: '🇳🇱 Nederlands',
    key: 'nl',
  },
  {
    name: '🇵🇱 Polski',
    key: 'pl',
  },
  {
    name: '🇧🇷 Português Brasileiro',
    key: 'pt-BR',
  },
  {
    name: '🇷🇺 Русский',
    key: 'ru',
  },
  {
    name: '🇻🇳 Tiếng Việt',
    key: 'vi',
  },
  {
    name: '🇨🇳 简体中文',
    key: 'zh-Hans',
  },
  {
    name: '🧋 繁體中文',
    key: 'zh-Hant',
  },
  {
    name: '🥺 Engwish~ OwO',
    key: 'en-x-owo',
  },
];

// AppConfig path: https://docs.rs/tauri/1.2.4/tauri/api/path/fn.config_dir.html
// We doing this only once, don't want an override check to be done on runtime,
// only on launch :P
const overrideLangExists = exists(OVERRIDE_FILENAME, {
  dir: BaseDirectory.AppConfig,
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
    new Intl.Locale(locale);
    return locale;
  } catch (e) {
    console.error(e);
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
            dir: BaseDirectory.AppConfig,
          }),
        ]
      : await fetchMessages(currentLocale);

    const fetchedMessages = [
      currentLocaleFile,
      await fetchMessages(DEFAULT_LOCALE),
    ];

    const bundles = lazilyParsedBundles(fetchedMessages);
    localStorage.setItem('i18nextLng', currentLocale);
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
