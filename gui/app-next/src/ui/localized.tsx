import type { JSX } from 'solid-js';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';

export type LocalizedElems = Partial<
  Record<string, (children: string) => JSX.Element>
>;

export interface LocalizedProps {
  id: MessageId;
  args?: MessageArgs;
  elems?: LocalizedElems;
}

interface Part {
  tag: string | null;
  text: string;
}

const MARKUP = /<([A-Za-z][\w-]*)>([\s\S]*?)<\/\1>/g;

export function splitMarkup(text: string): Part[] {
  const parts: Part[] = [];
  let last = 0;
  for (const m of text.matchAll(MARKUP)) {
    if (m.index > last)
      parts.push({ tag: null, text: text.slice(last, m.index) });
    parts.push({ tag: m[1]!, text: m[2]! });
    last = m.index + m[0].length;
  }
  if (last < text.length) parts.push({ tag: null, text: text.slice(last) });
  return parts;
}

function renderParts(parts: Part[], elems: LocalizedElems) {
  return parts.map((part) => {
    const render = part.tag === null ? undefined : elems[part.tag];
    return render ? render(part.text) : part.text;
  });
}

export function Localized(props: LocalizedProps) {
  return (
    <>{renderParts(splitMarkup(t(props.id, props.args)), props.elems ?? {})}</>
  );
}
