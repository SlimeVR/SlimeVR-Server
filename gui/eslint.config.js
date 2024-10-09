import { FlatCompat } from '@eslint/eslintrc';
import eslint from '@eslint/js';
import globals from 'globals';
import tseslint from 'typescript-eslint';

const compat = new FlatCompat();

export const gui = [
  eslint.configs.recommended,
  ...tseslint.configs.recommended,
  ...compat.extends('plugin:@dword-design/import-alias/recommended'),
  ...compat.plugins('eslint-plugin-react-hooks'),
  // Add import-alias rule inside compat because plugin doesn't like flat configs
  ...compat.config({
    rules: {
      '@dword-design/import-alias/prefer-alias': [
        'error',
        {
          alias: {
            '@': './src/',
          },
        },
      ],
    },
  }),
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
    plugins: {
      '@typescript-eslint': tseslint.plugin,
    },
    rules: {
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
    },
    settings: {
      'import/resolver': {
        typescript: {},
      },
      react: {
        version: 'detect',
      },
    },
  },
  // Global ignore
  {
    ignores: ['**/firmware-tool-api/'],
  },
];

export default gui;
