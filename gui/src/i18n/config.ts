import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './en/translation.json';
import es419 from './es-419/translation.json';
import fr from './fr/translation.json';
import ko from './ko/translation.json';
import pl from './pl/translation.json';
import ptBR from './pt-BR/translation.json';
import vi from './vi/translation.json';
import zh from './zh/translation.json';
import owo from './owo/translation.json';

export const defaultNS = 'translation';

export const langs = [
  {
    name: '🇺🇸 English',
    key: 'en',
  },
  {
    name: '🐰 Español Latino',
    key: 'es419',
  },
  {
    name: '🇫🇷 Français',
    key: 'fr',
  },
  {
    name: '🇰🇷 한국어',
    key: 'ko',
  },
  {
    name: 'Polski',
    key: 'pl',
  },
  {
    name: '🇧🇷 Português Brasileiro',
    key: 'ptBR',
  },
  {
    name: 'Tiếng Việt',
    key: 'vi',
  },
  {
    name: '🇨🇳 简体中文',
    key: 'zh',
  },
  {
    name: '🥺 Engwish~ OwO',
    key: 'owo',
  },
];

export const resources = {
  en: {
    translation: en,
  },
  es419: {
    tanslations: es419,
  },
  fr: {
    translation: fr,
  },
  ko: {
    translation: ko,
  },
  pl: {
    translation: pl,
  },
  ptBR: {
    translation: ptBR,
  },
  vi: {
    translation: vi,
  },
  zh: {
    translation: zh,
  },
  owo: {
    translation: owo,
  },
};

i18next.use(initReactI18next).init({
  lng: 'en', // if you're using a language detector, do not define the lng option
  resources,
  fallbackLng: 'en',
  defaultNS,
});

export default i18next;
