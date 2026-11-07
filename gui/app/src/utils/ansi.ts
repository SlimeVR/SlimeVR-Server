import { CSSProperties } from 'react';
import {
  createColorPalette,
  ParseToken,
  parseAnsiSequences,
} from 'ansi-sequence-parser';

export interface AnsiSpan {
  text: string;
  style?: CSSProperties;
}

const palette = createColorPalette({
  black: 'rgb(var(--background-40))',
  red: 'rgb(var(--critical))',
  green: 'rgb(var(--success))',
  yellow: 'rgb(var(--warning))',
  blue: '#60a5fa',
  magenta: '#c084fc',
  cyan: '#22d3ee',
  white: 'rgb(var(--background-10))',
  brightBlack: 'rgb(var(--background-30))',
  brightRed: 'rgb(var(--critical))',
  brightGreen: 'rgb(var(--success))',
  brightYellow: 'rgb(var(--warning))',
  brightBlue: '#93c5fd',
  brightMagenta: '#d8b4fe',
  brightCyan: '#67e8f9',
  brightWhite: 'rgb(var(--background-10))',
});

// eslint-disable-next-line no-control-regex
const NON_SGR_CSI = /\x1b\[[0-?]*[ -/]*[@-ln-~]/g;
// eslint-disable-next-line no-control-regex
const STRAY_CONTROL = /[\x00-\x08\x0b-\x1a\x1c-\x1f\x7f]/g;
// eslint-disable-next-line no-control-regex
const LONE_ESC = /\x1b/g;
// eslint-disable-next-line no-control-regex
const BARE_RESET = /\x1b\[m/g;

function tokenCss(token: ParseToken): CSSProperties | undefined {
  const { foreground, background, decorations } = token;
  if (!foreground && !background && decorations.size === 0) return undefined;
  let color = foreground ? palette.value(foreground) : undefined;
  let backgroundColor = background ? palette.value(background) : undefined;
  if (decorations.has('reverse')) {
    [color, backgroundColor] = [
      backgroundColor ?? 'rgb(var(--background-80))',
      color ?? 'rgb(var(--background-10))',
    ];
  }
  const lines = [
    decorations.has('underline') && 'underline',
    decorations.has('strikethrough') && 'line-through',
    decorations.has('overline') && 'overline',
  ].filter(Boolean);
  return {
    color,
    backgroundColor,
    fontWeight: decorations.has('bold') ? 600 : undefined,
    opacity: decorations.has('dim') ? 0.65 : undefined,
    fontStyle: decorations.has('italic') ? 'italic' : undefined,
    textDecorationLine: lines.length > 0 ? lines.join(' ') : undefined,
    visibility: decorations.has('hidden') ? 'hidden' : undefined,
  };
}

export function parseAnsi(text: string): AnsiSpan[] {
  const clean = text
    .replace(NON_SGR_CSI, '')
    .replace(BARE_RESET, '\x1b[0m')
    .replace(STRAY_CONTROL, '');
  return parseAnsiSequences(clean).map((token) => ({
    text: token.value.replace(LONE_ESC, ''),
    style: tokenCss(token),
  }));
}

export function stripAnsi(text: string) {
  return parseAnsi(text)
    .map((span) => span.text)
    .join('');
}
