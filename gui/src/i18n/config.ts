import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './en/translation.json';
import fr from './fr/translation.json';
import zh from './zh/translation.json';
import owo from './owo/translation.json';

export const defaultNS = 'tanslations';

export const langs = [
  {
    name: '🇺🇸 English',
    key: 'en',
  },
  {
    name: '🇫🇷 Français',
    key: 'fr',
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
    tanslations: en,
  },
  fr: {
    tanslations: fr,
  },
  zh: {
    tanslations: zh,
  },
  owo: {
    tanslations: owo,
  },
};

i18next.use(initReactI18next).init({
  lng: 'en', // if you're using a language detector, do not define the lng option
  resources,
  fallbackLng: 'en',
  defaultNS,
});

export default i18next;
