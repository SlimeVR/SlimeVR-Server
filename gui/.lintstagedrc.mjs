export default {
  "**/*.{ts,tsx}": () => "tsc -p tsconfig.json --noEmit",
  "**/*.{js,jsx,ts,tsx}": "eslint --cache --fix",
  "**/*.{js,jsx,ts,tsx,css,md,json}": "prettier --write"
};
