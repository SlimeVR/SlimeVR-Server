import { render } from '@solidjs/testing-library';
import { describe, expect, it } from 'vitest';
import { createBundle, setL10nBundles } from '@/lib/l10n';
import { Localized, type LocalizedElems, splitMarkup } from '@/ui/localized';

function renderLocalized(id: string, elems: LocalizedElems) {
  return render(() => <Localized id={id} elems={elems} />);
}

describe('splitMarkup', () => {
  it('splits tags and plain text', () => {
    expect(splitMarkup('a <b>bold</b> c')).toEqual([
      { tag: null, text: 'a ' },
      { tag: 'b', text: 'bold' },
      { tag: null, text: ' c' },
    ]);
  });

  it('leaves unclosed tags as text', () => {
    expect(splitMarkup('a <b>bold')).toEqual([
      { tag: null, text: 'a <b>bold' },
    ]);
  });
});

describe('Localized', () => {
  it('maps whitelisted tags and renders others as text', () => {
    setL10nBundles([
      createBundle(
        'en',
        'msg = Click <b>here</b> or <script>alert(1)</script> or <img>x</img> now'
      ),
    ]);
    const { container } = renderLocalized('msg', {
      b: (c) => <strong>{c}</strong>,
    });
    expect(container.querySelector('strong')?.textContent).toBe('here');
    expect(container.querySelector('script')).toBeNull();
    expect(container.querySelector('img')).toBeNull();
    expect(container.textContent).toBe('Click here or alert(1) or x now');
  });

  it('follows the locale', () => {
    setL10nBundles([createBundle('en', 'msg = <b>One</b>')]);
    const { container } = renderLocalized('msg', { b: (c) => <em>{c}</em> });
    setL10nBundles([createBundle('fr', 'msg = <b>Un</b>')]);
    expect(container.querySelector('em')?.textContent).toBe('Un');
  });
});
