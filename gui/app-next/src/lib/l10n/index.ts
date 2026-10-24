export {
  t,
  createBundle,
  setL10nBundles,
  type MessageArgs,
  type MessageId,
} from '@/lib/l10n/translate';
export {
  DEFAULT_LOCALE,
  availableLocales,
  initL10n,
  loadLocale,
  locale,
  savedLanguage,
  setLanguage,
} from '@/lib/l10n/locale';
export { inContextMode, setInContext, type InContextMode } from '@/lib/l10n/crowdin';
