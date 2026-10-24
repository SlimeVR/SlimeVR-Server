export type RedactKind = 'known' | 'ip' | 'mac' | 'path' | 'ssid' | 'password';

export interface Segment {
  text: string;
  kind?: RedactKind;
}

interface Match {
  start: number;
  end: number;
  kind: RedactKind;
}

interface Rule {
  regex: RegExp;
  groups: { group: number; kind: RedactKind }[];
  kind?: RedactKind;
  keep?: (text: string) => boolean;
}

const OCTET = '(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)';
const HEX = '[0-9A-Fa-f]';

// Firmware lines, checked against SlimeVR-Tracker-ESP
const FIRMWARE_RULES: Rule[] = [
  // wifihandler.cpp: Loaded credentials for SSID '%s' and pass length %d
  // wifihandler.cpp: Connected successfully to SSID '%s', IP address %s
  {
    regex: /SSID '(.*?)'(?= and pass length|, IP address)/dg,
    groups: [{ group: 1, kind: 'ssid' }],
  },
  // serialcommands.cpp: [WSCAN] %d:\t%02d\t'%s'\t(%d dBm)\t%s
  {
    regex: /\[WSCAN\] \d+:\t\d+\t'(.*?)'\t\(/dg,
    groups: [{ group: 1, kind: 'ssid' }],
  },
  {
    regex: /SET WIFI "(.*?)" "(.*?)"/dg,
    groups: [
      { group: 1, kind: 'ssid' },
      { group: 2, kind: 'password' },
    ],
  },
  {
    regex: /SET BWIFI (\S+) (\S+)/dg,
    groups: [
      { group: 1, kind: 'ssid' },
      { group: 2, kind: 'password' },
    ],
  },
];

const PATTERN_RULES: Rule[] = [
  {
    regex: /(?:\/var)?\/home\/[^/\\\s"']+|\/Users\/[^/\\\s"']+|[A-Za-z]:[\\/]Users[\\/][^/\\\s"']+/gi,
    groups: [],
    kind: 'path',
  },
  {
    regex: new RegExp(`(?<![\\w:-])${HEX}{2}([:-])${HEX}{2}(?:\\1${HEX}{2}){4}(?![\\w:-])`, 'g'),
    groups: [],
    kind: 'mac',
  },
  {
    regex: new RegExp(`(?<![\\w.])${OCTET}(?:\\.${OCTET}){3}(?![\\w.])`, 'g'),
    groups: [],
    kind: 'ip',
    keep: (ip) => ip.startsWith('127.') || ip === '0.0.0.0' || ip === '255.255.255.255',
  },
  {
    regex: new RegExp(
      `(?<![\\w:])(?:(?:${HEX}{1,4}:){7}${HEX}{1,4}|(?:${HEX}{1,4}:)*${HEX}{0,4}::(?:${HEX}{1,4}:)*${HEX}{0,4})(?![\\w:])`,
      'g'
    ),
    groups: [],
    kind: 'ip',
    keep: (ip) => ip === '::1' || !/[0-9A-Fa-f]/.test(ip),
  },
];

const MIN_KNOWN_LENGTH = 2;

function ruleMatches(line: string, rule: Rule): Match[] {
  const found: Match[] = [];
  for (const m of line.matchAll(rule.regex)) {
    if (rule.keep?.(m[0])) continue;
    if (rule.groups.length === 0) {
      found.push({ start: m.index, end: m.index + m[0].length, kind: rule.kind ?? 'known' });
      continue;
    }
    for (const { group, kind } of rule.groups) {
      const range = m.indices?.[group];
      if (!range || range[0] === range[1]) continue;
      found.push({ start: range[0], end: range[1], kind });
    }
  }
  return found;
}

function knownMatches(line: string, known: readonly string[]): Match[] {
  const found: Match[] = [];
  for (const value of known) {
    if (value.length < MIN_KNOWN_LENGTH) continue;
    let at = line.indexOf(value);
    while (at !== -1) {
      found.push({ start: at, end: at + value.length, kind: 'known' });
      at = line.indexOf(value, at + value.length);
    }
  }
  return found;
}

function compute(line: string, known: readonly string[]): Segment[] {
  const matches = [
    ...knownMatches(line, known),
    ...FIRMWARE_RULES.flatMap((rule) => ruleMatches(line, rule)),
    ...PATTERN_RULES.flatMap((rule) => ruleMatches(line, rule)),
  ].sort((a, b) => a.start - b.start || b.end - a.end);

  const segments: Segment[] = [];
  let cursor = 0;
  for (const match of matches) {
    // Overlaps keep the earliest, then longest match
    if (match.start < cursor) continue;
    if (match.start > cursor) segments.push({ text: line.slice(cursor, match.start) });
    segments.push({ text: line.slice(match.start, match.end), kind: match.kind });
    cursor = match.end;
  }
  if (cursor < line.length) segments.push({ text: line.slice(cursor) });
  return segments;
}

const CACHE_LIMIT = 2000;
const cache = new WeakMap<readonly string[], Map<string, Segment[]>>();

// Memoized per `known` array identity
export function redact(line: string, known: readonly string[] = []): Segment[] {
  let lines = cache.get(known);
  if (!lines) cache.set(known, (lines = new Map()));
  let segments = lines.get(line);
  if (!segments) {
    if (lines.size >= CACHE_LIMIT) lines.clear();
    segments = compute(line, known);
    lines.set(line, segments);
  }
  return segments;
}

function replacement({ text, kind }: Segment) {
  switch (kind) {
    case undefined:
      return text;
    case 'path':
      return '~';
    case 'known':
      return '[redacted]';
    default:
      return `[${kind}]`;
  }
}

export function redactText(text: string, known: readonly string[] = []) {
  return text
    .split('\n')
    .map((line) => redact(line, known).map(replacement).join(''))
    .join('\n');
}
