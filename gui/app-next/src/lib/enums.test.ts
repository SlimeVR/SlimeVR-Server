import { describe, expect, it } from 'vitest';
import { enumOptions, enumValues } from '@/lib/enums';

enum Fruit {
  APPLE = 0,
  PEAR = 1,
  PLUM = 2,
}

const labels = {
  [Fruit.APPLE]: 'fruit-APPLE',
  [Fruit.PEAR]: 'fruit-PEAR',
};

describe('enumValues', () => {
  it('returns numeric members in order', () => {
    expect(enumValues(Fruit)).toEqual([Fruit.APPLE, Fruit.PEAR, Fruit.PLUM]);
  });
});

describe('enumOptions', () => {
  it('skips members without a label', () => {
    expect(enumOptions(Fruit, labels)).toEqual([
      { value: Fruit.APPLE, label: 'fruit-APPLE' },
      { value: Fruit.PEAR, label: 'fruit-PEAR' },
    ]);
  });

  it('keeps enum order with only', () => {
    expect(enumOptions(Fruit, labels, { only: [Fruit.PEAR, Fruit.APPLE] })).toEqual([
      { value: Fruit.APPLE, label: 'fruit-APPLE' },
      { value: Fruit.PEAR, label: 'fruit-PEAR' },
    ]);
  });
});
