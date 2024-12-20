export default {
  '**/*.{ts,tsx}': () => 'tsc -p tsconfig.json --noEmit',
  'src/**/*.{js,jsx,ts,tsx}': 'eslint --max-warnings=0 --no-warn-ignored --cache --fix',
  '**/*.{js,jsx,ts,tsx,css,md,json}': 'prettier --write',
};
