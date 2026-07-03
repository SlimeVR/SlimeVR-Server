export default {
  'app/**/*.{js,jsx,ts,tsx}':
    'pnpm --filter @slimevr/gui-app exec eslint --max-warnings=0 --no-warn-ignored --cache --fix',
  '**/*.{js,jsx,ts,tsx,css,scss,md,json}': 'prettier --write',
};
