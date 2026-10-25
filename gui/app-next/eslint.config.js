import eslint from '@eslint/js';
import betterTailwind from 'eslint-plugin-better-tailwindcss';
import boundaries from 'eslint-plugin-boundaries';
import jsxA11y from 'eslint-plugin-jsx-a11y';
import solid from 'eslint-plugin-solid/configs/typescript';
import globals from 'globals';
import tseslint from 'typescript-eslint';
import l10n from './eslint/l10n.js';
import layers from './eslint/layers.js';

const UI_DEEP_IMPORT = {
  group: ['@/ui/*/*'],
  message: 'Import a ui folder through its index, e.g. @/ui/select.',
};

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
    selector: 'Identifier[name=/^(localStorage|sessionStorage)$/]',
    message: 'Use definePref/defineCache/defineSession from @/lib/storage.',
  },
  electronApi: {
    allowedIn: ['src/app/platform/**'],
    selector: "MemberExpression[property.name='electronAPI']",
    message: 'Use platform from @/lib/platform.',
  },
  keydown: {
    allowedIn: ['src/lib/keyboard.ts', 'src/ui/motion/**'],
    selector:
      "CallExpression[callee.property.name='addEventListener'][arguments.0.value=/^key(down|up)$/]",
    message:
      'Use useShortcut from @/lib/keyboard, Escape goes to the back stack (useBack).',
  },
  android: {
    allowedIn: ['src/app/platform/**', 'src/env.d.ts'],
    selector: "Identifier[name='__ANDROID__']",
    message: 'Use platform from @/lib/platform.',
  },
};

function allow(from, to) {
  return {
    from: { element: { type: from } },
    allow: { to: { element: { type: to } } },
  };
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
      parserOptions: { projectService: true, tsconfigRootDir: import.meta.dirname },
      globals: globals.browser,
    },
    plugins: {
      ...solid.plugins,
      boundaries,
      l10n,
    },
    settings: {
      'import/resolver': {
        typescript: { alwaysTryTypes: true, project: './tsconfig.json' },
      },
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
  {
    files: ['src/lib/**/*.{ts,tsx}'],
    plugins: { layers },
    rules: { 'layers/no-jsx': 'error' },
  },
  // Peer range stops at ESLint 9 but the rules only read JSX attributes
  { files: ['src/**/*.tsx'], ...jsxA11y.flatConfigs.recommended },
  // Long class strings wrap into a multiline template literal, fixed by lint:fix
  {
    files: ['src/**/*.tsx'],
    plugins: { 'better-tailwindcss': betterTailwind },
    settings: { 'better-tailwindcss': { entryPoint: 'src/styles.css' } },
    rules: {
      'better-tailwindcss/enforce-consistent-line-wrapping': [
        'error',
        // Packed lines, no line per variant group. Wider than prettier so
        // only really long strings wrap at JSX indentation depth.
        {
          printWidth: 100,
          group: 'never',
          preferSingleLine: true,
          strictness: 'loose',
        },
      ],
      // The type scale is the text tokens in styles.css, nothing else
      'better-tailwindcss/no-restricted-classes': [
        'error',
        {
          restrict: [
            {
              pattern: '^(?:.*:)?!?text-[\\[(].*$',
              message: 'No arbitrary text values, use text-standard, text-section or text-title.',
            },
            {
              pattern: '^(?:.*:)?!?text-(?:xs|sm|base|lg|xl|\\dxl)$',
              message: 'Tailwind sizes are removed, use text-standard, text-section or text-title.',
            },
          ],
        },
      ],
    },
  },
  // Solid compiles `ref={x}` into an assignment the rule cannot see
  { files: ['src/**/*.tsx'], rules: { 'no-unassigned-vars': 'off' } },
  // Motion hooks belong to ui primitives, pages only use the containers
  {
    files: ['src/**/*.{ts,tsx}'],
    ignores: ['src/ui/**'],
    rules: {
      'no-restricted-imports': [
        'error',
        {
          paths: [
            {
              name: '@/ui/motion',
              importNames: ['useMotionItem'],
              message: 'Only ui primitives register motion items.',
            },
          ],
          patterns: [UI_DEEP_IMPORT],
        },
      ],
    },
  },
  // Each ui folder is entered through its index; files inside it import each other relatively
  {
    files: ['src/ui/**/*.{ts,tsx}'],
    rules: { 'no-restricted-imports': ['error', { patterns: [UI_DEEP_IMPORT] }] },
  },
  // Local rules run in node
  { files: ['eslint/**/*.js'], languageOptions: { globals: globals.node } },
  // Tests build their own bundles with made-up ids
  { files: ['src/**/*.test.{ts,tsx}'], rules: { 'l10n/message-id': 'off' } }
);
