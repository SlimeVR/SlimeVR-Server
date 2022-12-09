import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './en/translation.json';
import fr from './fr/translation.json';
import vi from './vi/translation.json';
import owo from './owo/translation.json'


export const defaultNS = 'translations';

export const langs = [
  {
    name: 'English',
    key: 'en',
  },
  {
    name: 'Français',
    key: 'fr',
  },
  {
    name: 'Engwish~ (OwO)',
    key: 'owo',
  },
  {
    name: 'Tiếng Việt',
    key: 'vi',
  },
];

export const resources = {
  en: {
    translation: en,
  },
  fr: {
    translation: fr,
  },
  owo: {
    translation: owo,
  },
  vi: {
    translation: vi,
  },
};

i18next.use(initReactI18next).init({
  lng: 'en', // if you're using a language detector, do not define the lng option
  resources,
  fallbackLng: 'en',
  defaultNS,
});

export default i18next;
