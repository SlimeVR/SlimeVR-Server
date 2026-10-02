import { FlatCompat } from '@eslint/eslintrc';
import eslint from '@eslint/js';
import globals from 'globals';
import tseslint from 'typescript-eslint';
import react from 'eslint-plugin-react';
import eslintPluginPrettierRecommended from 'eslint-plugin-prettier/recommended';

const compat = new FlatCompat();

export const gui = [
  eslint.configs.recommended,
  ...tseslint.configs.recommended,
  ...compat.extends('plugin:@dword-design/import-alias/recommended'),
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
    files: ['{electron,src}/**/*.{js,jsx,ts,tsx}'],
    plugins: {
      '@typescript-eslint': tseslint.plugin,
      react,
    },
    rules: {
      'react/react-in-jsx-scope': 'off',
      'react/prop-types': 'off',
      'react/no-unescaped-entities': 'off',
      // effect on every render (a common infinite-loop / perf footgun).
      'no-restricted-syntax': [
        'error',
        {
          selector:
            'CallExpression[callee.name=/^use(Effect|LayoutEffect|InsertionEffect)$/][arguments.length<2]',
          message:
            'Pass a dependency array to this hook (use [] to run it once). Omitting it re-runs on every render.',
        },
      ],
      'spaced-comment': 'error',
      'no-duplicate-imports': 'error',
      '@typescript-eslint/no-explicit-any': 'off',
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
  eslintPluginPrettierRecommended,
  // Global ignore
  {
    ignores: ['**/firmware-tool-api/'],
  },
];

export default gui;
