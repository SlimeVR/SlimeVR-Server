import { match } from '@formatjs/intl-localematcher';
import { FluentBundle, FluentResource, FluentVariable } from '@fluent/bundle';
import {
  LocalizationProvider,
  ReactLocalization,
  useLocalization,
} from '@fluent/react';
import {
  Children,
  ReactNode,
  useEffect,
  useState,
  createContext,
  useContext,
} from 'react';
import { error } from '@/utils/logging';
import { langs } from './names';
import { useElectron } from '@/hooks/electron';
import {
  inContextMode,
  reloadInContext,
  startCrowdinInContext,
} from './crowdin';

export const defaultNS = 'translation';
export const DEFAULT_LOCALE = 'en';
const IN_CONTEXT_LOCALE = 'ach-UG';

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
  inContext: boolean;
  inContextFailed: boolean;
  setInContext: (enabled: boolean) => void;
}

const TRAY_MENU_KEYS = ['tray_menu-show', 'tray_menu-hide', 'tray_menu-quit'];

export const LangContext = createContext<i18n>(undefined as never);
export function AppLocalizationProvider(props: AppLocalizationProviderProps) {
  const electron = useElectron();
  const [currentLocales, setCurrentLocales] = useState([DEFAULT_LOCALE]);
  const [l10n, setL10n] = useState<ReactLocalization | null>(null);
  const inContext = inContextMode === 'active';

  async function changeLocales(userLocales: string[]) {
    const currentLocale = match(
      userLocales.filter((x) => verifyLocale(x) !== null),
      langs.map((x) => x.key),
      DEFAULT_LOCALE
    );
    setCurrentLocales([currentLocale]);

    const overrideFile =
      !inContext && electron.isElectron && (await electron.api.i18nOverride());

    const currentLocaleFile: [string, string] = overrideFile
      ? [currentLocale, overrideFile]
      : await fetchMessages(inContext ? IN_CONTEXT_LOCALE : currentLocale);

    const fetchedMessages = [
      currentLocaleFile,
      await fetchMessages(DEFAULT_LOCALE),
    ];

    const bundles = lazilyParsedBundles(fetchedMessages);
    document.documentElement.lang = currentLocale;
    setL10n(new ReactLocalization(bundles));
  }

  // Children render once this loads, then useProvideAppContext applies config.lang.
  useEffect(() => {
    changeLocales([...navigator.languages]).then(() => {
      if (inContext) startCrowdinInContext();
    });
    // detect hot reload translation file changes
    if (import.meta.hot) {
      import.meta.hot.on('locales-update', () => changeLocales(currentLocales));
    }
  }, []);

  useEffect(() => {
    if (!electron.isElectron || !inContext) return;
    return electron.api.onCrowdinPopupClosed(() => reloadInContext('active'));
  }, []);

  useEffect(() => {
    if (l10n === null || !electron.isElectron || inContext) return;

    const newI18n: Record<string, string> = {};
    TRAY_MENU_KEYS.forEach((key) => {
      newI18n[key] = l10n.getString(key);
    });
    electron.api.setTranslations(newI18n);
  }, [l10n]);

  if (l10n === null) {
    return <></>;
  }

  return (
    <>
      <LocalizationProvider l10n={l10n}>
        <LangContext.Provider
          value={{
            currentLocales,
            changeLocales,
            inContext,
            inContextFailed: inContextMode === 'failed',
            setInContext: (enabled) =>
              reloadInContext(enabled ? 'active' : 'off'),
          }}
        >
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

export function useSafeLocalization() {
  const l = useLocalization();

  return {
    ...l,
    getStringOrNull: (
      id: string,
      vars?: Record<string, FluentVariable> | null
    ): string | null => {
      const bundle = l.l10n.getBundle(id);
      if (bundle) {
        const msg = bundle.getMessage(id);
        if (msg && msg.value) {
          const errors: Array<Error> = [];
          const value = bundle.formatPattern(msg.value, vars, errors);
          for (const error of errors) {
            l.l10n.reportError(error);
          }
          return value;
        }
      }
      return null;
    },
  };
}
