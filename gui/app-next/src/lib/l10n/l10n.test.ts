import { describe, expect, it, vi } from 'vitest';
import * as l10n from '@/lib/l10n';
import { setPlatform, type TranslationsApi } from '@/lib/platform';
import { fakePlatform } from '@/lib/platform-fake';

const EN = `
greeting = Hello
only-en = English only
with-arg = Hi { $name }
field = Field
    .placeholder = Type here
    .label = Label
tray_menu-show = Show
tray_menu-hide = Hide
tray_menu-quit = Quit
`;
const FR = `
greeting = Bonjour
field = Champ
    .placeholder = Tapez ici
tray_menu-show = Afficher
`;

function setup({ override }: { override?: string } = {}) {
  const translations: TranslationsApi = {
    set: vi.fn(),
    override: async () => override ?? false,
    onCrowdinPopupClosed: () => () => {},
  };
  setPlatform(fakePlatform({ translations }));
  const fetchMock = vi.fn(async (url: string) => {
    const source = { '/i18n/en/translation.ftl': EN, '/i18n/fr/translation.ftl': FR }[
      url
    ];
    return new Response(source ?? '', { status: source === undefined ? 404 : 200 });
  });
  vi.stubGlobal('fetch', fetchMock);
  return { translations, fetchMock };
}

describe('t', () => {
  it('uses the current locale and falls back to en-US per message', async () => {
    setup();
    await l10n.loadLocale(['fr-FR']);
    expect(l10n.locale()).toBe('fr');
    expect(document.documentElement.lang).toBe('fr');
    expect(l10n.t('greeting')).toBe('Bonjour');
    expect(l10n.t('only-en')).toBe('English only');
  });

  it('returns the id when no bundle has the message', async () => {
    setup();
    await l10n.loadLocale(['en']);
    expect(l10n.t('nope')).toBe('nope');
  });

  it('formats args', async () => {
    setup();
    await l10n.loadLocale(['en']);
    expect(l10n.t('with-arg', { name: 'Ana' })).toContain('Ana');
  });

  it('falls back to en-US per attribute', async () => {
    setup();
    await l10n.loadLocale(['fr']);
    expect(l10n.t.attr('field', 'placeholder')).toBe('Tapez ici');
    expect(l10n.t.attr('field', 'label')).toBe('Label');
    expect(l10n.t.attr('field', 'missing')).toBe('field.missing');
  });
});

describe('loadLocale', () => {
  it('negotiates unknown locales down to en-US', async () => {
    setup();
    await l10n.loadLocale(['xx-YY', 'not a tag']);
    expect(l10n.locale()).toBe('en');
  });

  it('uses the platform override file instead of fetching the locale', async () => {
    const { fetchMock } = setup({ override: 'greeting = Overridden' });
    await l10n.loadLocale(['fr']);
    expect(l10n.t('greeting')).toBe('Overridden');
    expect(fetchMock.mock.calls.map(([url]) => url)).toEqual([
      '/i18n/en/translation.ftl',
    ]);
  });

  it('sends tray translations to the platform', async () => {
    const { translations } = setup();
    await l10n.loadLocale(['fr']);
    expect(translations.set).toHaveBeenLastCalledWith({
      'tray_menu-show': 'Afficher',
      'tray_menu-hide': 'Hide',
      'tray_menu-quit': 'Quit',
    });
  });

  it('applies overlapping loads in call order', async () => {
    setup();
    const first = l10n.loadLocale(['fr']);
    const second = l10n.loadLocale(['en']);
    await Promise.all([first, second]);
    expect(l10n.locale()).toBe('en');
    expect(l10n.t('greeting')).toBe('Hello');
  });

  it('setLanguage saves the choice', async () => {
    setup();
    await l10n.setLanguage('fr');
    expect(l10n.savedLanguage()).toBe('fr');
    expect(l10n.locale()).toBe('fr');
  });
});
