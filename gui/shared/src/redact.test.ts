import { redact, redactText } from './redact';
import vectors from './redaction-vectors.json';
import { describe, expect, it } from 'vitest';

describe('redactText', () => {
  for (const { name, line, known, expected } of vectors) {
    it(name, () => {
      expect(redactText(line, known)).toBe(expected);
    });
  }
});

describe('redact', () => {
  it('splits a line into segments and marks identifying ones', () => {
    expect(redact('server is 192.168.1.10:6969')).toEqual([
      { text: 'server is ' },
      { text: '192.168.1.10', kind: 'ip' },
      { text: ':6969' },
    ]);
  });

  it('memoizes per line for the same known array', () => {
    const known = ['Bob'];
    expect(redact('hi Bob', known)).toBe(redact('hi Bob', known));
    expect(redact('hi Bob', known)).not.toBe(redact('hi Bob', ['Bob']));
  });

  it('keeps the earliest, longest match on overlap', () => {
    const segments = redact("SSID 'net 10.0.0.1', IP address 10.0.0.2", ['net']);
    expect(segments.filter((s) => s.kind).map((s) => [s.text, s.kind])).toEqual([
      ['net 10.0.0.1', 'ssid'],
      ['10.0.0.2', 'ip'],
    ]);
  });
});
