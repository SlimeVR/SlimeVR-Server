import globals from 'globals';
import tseslint from 'typescript-eslint';
import js from '@eslint/js';
import importAlias from '@dword-design/eslint-plugin-import-alias';
import { configs, plugins } from 'eslint-config-airbnb-extended';
import { rules as prettierConfigRules } from 'eslint-config-prettier';
import prettierPlugin from 'eslint-plugin-prettier';

const jsConfig = [
  // ESLint Recommended Rules
  {
    name: 'js/config',
    ...js.configs.recommended,
  },
  // Stylistic Plugin
  plugins.stylistic,
  // Import X Plugin
  plugins.importX,
  // Airbnb Base Recommended Config
  ...configs.base.recommended,
];

const reactConfig = [
  // React Plugin
  plugins.react,
  // React Hooks Plugin
  plugins.reactHooks,
  // React JSX A11y Plugin
  plugins.reactA11y,
  // Airbnb React Recommended Config
  ...configs.react.recommended,
];

const typescriptConfig = [
  // TypeScript ESLint Plugin
  plugins.typescriptEslint,
  // Airbnb Base TypeScript Config
  ...configs.base.typescript,
  // Airbnb React TypeScript Config
  ...configs.react.typescript,
];

const prettierConfig = [
  // Prettier Plugin
  {
    name: 'prettier/plugin/config',
    plugins: {
      prettier: prettierPlugin,
    },
  },
  // Prettier Config
  {
    name: 'prettier/config',
    rules: {
      ...prettierConfigRules,
      // remove errors with prettier, use prettier for that pls
      // 'prettier/prettier': 'error',
    },
  },
];

export const gui = [
  // Javascript Config
  ...jsConfig,
  // React Config
  ...reactConfig,
  // TypeScript Config
  ...typescriptConfig,
  // Prettier Config
  ...prettierConfig,
  importAlias.configs.recommended,
  {
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      parser: tseslint.parser,
      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },
      },
      globals: {
        ...globals.browser,
        ...globals.jest,
      },
    },
    files: ['src/**/*.{js,jsx,ts,tsx,json}'],
    rules: {
      // fixes for airbnb
      'max-classes-per-file': 'off',
      'no-underscore-dangle': 'off',
      'no-plusplus': 'off',
      'consistent-return': 'off',
      'no-bitwise': 'off',
      eqeqeq: ['error', 'smart'],
      'no-restricted-syntax': 'off',
      'no-param-reassign': 'off',
      'no-return-assign': ['error', 'except-parens'],
      'default-case': 'off',
      'no-continue': 'off',
      'react/forbid-prop-types': 'off',
      'no-script-url': 'off',
      // stuff that should be enabled again later
      eqeqeq: 'off',
      'no-nested-ternary': 'off',
      '@typescript-eslint/no-use-before-define': 'off',
      '@typescript-eslint/no-shadow': 'off',
      'import-x/prefer-default-export': 'off',
      'import-x/no-cycle': 'off',
      'import-x/no-rename-default': 'off',
      'jsx-a11y/heading-has-content': 'off',
      'jsx-a11y/alt-text': 'off',
      'jsx-a11y/media-has-caption': 'off',
      'jsx-a11y/no-static-element-interactions': 'off',
      'jsx-a11y/click-events-have-key-events': 'off',
      'jsx-a11y/label-has-associated-control': 'off',
      'jsx-a11y/no-noninteractive-element-interactions': 'off',
      'jsx-a11y/no-noninteractive-element-to-interactive-role': 'off',
      'jsx-a11y/anchor-is-valid': 'off',
      'react/require-default-props': 'off',
      'react/no-unstable-nested-components': 'off',
      'react/no-unused-prop-types': 'off',
      'react-hooks/rules-of-hooks': 'off',
      'react-hooks/exhaustive-deps': 'off',
      // end
      '@typescript-eslint/switch-exhaustiveness-check': [
        'error',
        {
          considerDefaultExhaustiveForUnions: true,
        },
      ],
      'react/react-in-jsx-scope': 'off',
      'react/prop-types': 'off',
      'spaced-comment': 'error',
      quotes: ['error', 'single'],
      'no-duplicate-imports': 'error',
      'no-inline-styles': 'off',
      '@typescript-eslint/no-explicit-any': 'off',
      'react/no-unescaped-entities': 'off',
      camelcase: 'error',
      '@typescript-eslint/no-unused-vars': [
        'warn',
        {
          argsIgnorePattern: '^_',
          varsIgnorePattern: '^_',
          ignoreRestSiblings: true,
        },
      ],
      '@dword-design/import-alias/prefer-alias': [
        'error',
        {
          alias: {
            '@': './src/',
          },
        },
      ],
    },
  },
  // Global ignore
  {
    ignores: ['**/firmware-tool-api/'],
  },
];

export default gui;
