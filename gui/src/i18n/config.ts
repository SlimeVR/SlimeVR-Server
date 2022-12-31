import i18next from 'i18next';
import Fluent from 'i18next-fluent';
// @ts-expect-error - this package doesn't contain typings but we dont need it
import Backend from 'i18next-fluent-backend';
import LanguageDetector from 'i18next-browser-languagedetector';
import { initReactI18next } from 'react-i18next';

export const defaultNS = 'translation';

export const langs = [
  {
    name: '🇺🇸 English',
    key: 'en',
  },
  {
    name: '🌎 Español Latinoamericano',
    key: 'es-419'
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
    name: '🇵🇱 Polski',
    key: 'pl',
  },
  {
    name: '🇧🇷 Português Brasileiro',
    key: 'pt-BR',
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
    name: '🥺 Engwish~ OwO',
    key: 'en-OwO',
  },
];

i18next
  .use(Fluent)
  .use(Backend)
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    fallbackLng: 'en',
    defaultNS,
    backend: {
      loadPath: '/i18n/{{lng}}/{{ns}}.ftl',
    },
  });

if (import.meta.hot) {
  // detect hot reload translation file changes
  import.meta.hot.on('locales-update', async () => {
    await i18next.reloadResources();
    await i18next.changeLanguage(i18next.language);
  });
}

export default i18next;
