import eslint from '@eslint/js';
import boundaries from 'eslint-plugin-boundaries';
import solid from 'eslint-plugin-solid/configs/typescript';
import globals from 'globals';
import tseslint from 'typescript-eslint';
import l10n from './eslint/l10n.js';

// Each entry is allowed only in the files listed in `allowedIn`
const restricted = {
  toFixed: {
    allowedIn: ['src/lib/format.ts'],
    selector: "CallExpression[callee.property.name='toFixed']",
    message: 'Use fmt() from @/lib/format, it is locale aware.',
  },
  intl: {
    allowedIn: ['src/lib/format.ts'],
    selector: "NewExpression[callee.object.name='Intl']",
    message: 'Use fmt/fmtList/fmtRelative from @/lib/format, they cache per locale.',
  },
  storage: {
    allowedIn: ['src/lib/storage.ts', 'src/lib/storage.test.ts'],
    selector: "Identifier[name=/^(localStorage|sessionStorage)$/]",
    message: 'Use definePref/defineCache/defineSession from @/lib/storage.',
  },
  electronApi: {
    allowedIn: ['src/app/platform/**'],
    selector: "MemberExpression[property.name='electronAPI']",
    message: 'Use platform from @/lib/platform.',
  },
  android: {
    allowedIn: ['src/app/platform/**', 'src/env.d.ts'],
    selector: "Identifier[name='__ANDROID__']",
    message: 'Use platform from @/lib/platform.',
  },
};

function allow(from, to) {
  return { from: { element: { type: from } }, allow: { to: { element: { type: to } } } };
}

function restrictedSyntax(except = []) {
  return [
    'error',
    ...Object.entries(restricted)
      .filter(([name]) => !except.includes(name))
      .map(([, { selector, message }]) => ({ selector, message })),
  ];
}

function restrictedOverrides() {
  const byPattern = new Map();
  for (const [name, { allowedIn }] of Object.entries(restricted)) {
    for (const pattern of allowedIn) {
      byPattern.set(pattern, [...(byPattern.get(pattern) ?? []), name]);
    }
  }
  return [...byPattern].map(([pattern, names]) => ({
    files: [pattern],
    rules: { 'no-restricted-syntax': restrictedSyntax(names) },
  }));
}

export default tseslint.config(
  eslint.configs.recommended,
  ...tseslint.configs.recommended,
  {
    files: ['src/**/*.{ts,tsx}'],
    ...solid,
    languageOptions: {
      parser: tseslint.parser,
      globals: globals.browser,
    },
    plugins: {
      ...solid.plugins,
      boundaries,
      l10n,
    },
    settings: {
      'import/resolver': { typescript: { alwaysTryTypes: true, project: './tsconfig.json' } },
      'boundaries/include': ['src/**/*'],
      'boundaries/elements': [
        { type: 'app', pattern: 'src/app' },
        { type: 'ui', pattern: 'src/ui' },
        { type: 'lib', pattern: 'src/lib' },
        { type: 'solarxr', pattern: 'src/solarxr' },
        { type: 'domain', pattern: 'src/domain/*', capture: ['concept'] },
        { type: 'pages', pattern: 'src/pages/*', capture: ['page'] },
      ],
    },
    rules: {
      ...solid.rules,
      // External packages and imports inside one element (same page, same concept) are not checked
      'boundaries/dependencies': [
        'error',
        {
          default: 'disallow',
          policies: [
            allow('app', ['app', 'ui', 'lib', 'solarxr', 'domain', 'pages']),
            allow('pages', ['ui', 'lib', 'solarxr', 'domain']),
            allow('domain', ['ui', 'lib', 'solarxr', 'domain']),
            allow('solarxr', ['lib', 'solarxr']),
            allow('ui', ['ui', 'lib']),
          ],
        },
      ],
      'l10n/message-id': 'error',
      'no-throw-literal': 'error',
      'no-duplicate-imports': 'error',
      'no-restricted-syntax': restrictedSyntax(),
      '@typescript-eslint/no-unused-vars': [
        'warn',
        { argsIgnorePattern: '^_', varsIgnorePattern: '^_', ignoreRestSiblings: true },
      ],
    },
  },
  ...restrictedOverrides(),
  // Local rules run in node
  { files: ['eslint/**/*.js'], languageOptions: { globals: globals.node } },
  // Tests build their own bundles with made-up ids
  { files: ['src/**/*.test.{ts,tsx}'], rules: { 'l10n/message-id': 'off' } }
);
