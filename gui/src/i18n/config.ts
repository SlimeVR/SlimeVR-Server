import i18next from 'i18next';
import {initReactI18next} from 'react-i18next';
import en from './en/translation.json';
import fr from './fr/translation.json';
import de from './de/translation.json';
import pt-BR from './pt-BR/translation.json';
import zh from './zh/translation.json';
import pl from './pl/translation.json';
import vi from './vi/translation.json';
import owo from './owo/translation.json';


export const defaultNS = 'translation';

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
    name: '🇩🇪 Deutsch',
    key: 'de',
  },
  {
    name: '🇧🇷 Português Brasileiro',
    key: 'pt-BR',
  },
  {
    name: '🇨🇳 简体中文',
    key: 'zh',
  },
  {
    name: 'Polski',
    key: 'pl',
  },
  {
    name: 'Tiếng Việt',
    key: 'vi',
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
  fr: {
    translation: fr,
  },
  vi: {
    translation: vi,
  },
  pl: {
    tanslations: pl,
  },
  zh: {
    tanslations: zh,
  },
  pt-BR: {
    tanslations: pt-BR,
  },
  de: {
    tanslations: de,
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
