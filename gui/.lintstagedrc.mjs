export default {
  "**/*.{ts,tsx}": () => "tsc -p tsconfig.json --noEmit",
  "**/*.{js,jsx,ts,tsx}": "eslint --max-warnings=0 --cache --fix",
  "**/*.{js,jsx,ts,tsx,css,md,json}": "prettier --write"
};
