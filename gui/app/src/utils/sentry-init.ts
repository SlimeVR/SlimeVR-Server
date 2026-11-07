import { getDefaultStore } from 'jotai';
import { sessionConsentAtom } from '@/hooks/error-reporting';
import { initSentry } from './sentry';

const isSteam = window.electronAPI ? await window.electronAPI.isSteam() : false;
initSentry(isSteam, () => getDefaultStore().get(sessionConsentAtom));
