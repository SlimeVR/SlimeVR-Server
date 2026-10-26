import { paths } from '@/app/routes';
import type { SearchDestination } from '@/pages/settings/quick-search/search-entries';

// Pages outside the settings that quick search lists. Add a row when a page's route lands.
export const searchDestinations: SearchDestination[] = [
  { path: paths.home, title: 'navbar-home' },
  { path: paths.onboarding, title: 'navbar-onboarding' },
];
