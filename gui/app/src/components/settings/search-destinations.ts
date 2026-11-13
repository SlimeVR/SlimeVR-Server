import { SearchAvailability } from './settings-definitions';

// Where a result lives, shown as the colored start of its breadcrumb
export type SearchSection = 'settings' | 'page';

export interface SearchDestination {
  section: SearchSection;
  path: string;
  title: string;
  state?: object;
  available?: (ctx: SearchAvailability) => boolean;
}

export const searchDestinations: SearchDestination[] = [
  { section: 'page', path: '/', title: 'navbar-home' },
  {
    section: 'page',
    path: '/onboarding/trackers-assign',
    title: 'navbar-trackers_assign',
    state: { alonePage: true },
  },
  {
    section: 'page',
    path: '/onboarding/mounting/choose',
    title: 'navbar-mounting',
    state: { alonePage: true },
  },
  {
    section: 'page',
    path: '/onboarding/body-proportions/scaled',
    title: 'navbar-body_proportions',
    state: { alonePage: true },
  },
  {
    section: 'page',
    path: '/onboarding/body-proportions/manual',
    title: 'settings-search-manual_proportions',
    state: { alonePage: true },
  },
  {
    section: 'page',
    path: '/onboarding/add-trackers',
    title: 'navbar-connect_trackers',
    state: { alonePage: true },
  },
  { section: 'page', path: '/onboarding/home', title: 'navbar-onboarding' },
];
