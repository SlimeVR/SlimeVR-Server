import type { MessageId } from '@/lib/l10n';

type NumericEnum = Record<string, string | number>;
type EnumValue<E extends NumericEnum> = Extract<E[keyof E], number>;

export interface EnumOption<V> {
  value: V;
  label: MessageId;
}

// Numeric members in declaration order
export function enumValues<E extends NumericEnum>(e: E): EnumValue<E>[] {
  return Object.values(e).filter((x): x is EnumValue<E> => typeof x === 'number');
}

// Members without a label are skipped, `only` narrows further and keeps enum order
export function enumOptions<E extends NumericEnum>(
  e: E,
  labels: Partial<Record<EnumValue<E>, MessageId>>,
  { only }: { only?: readonly EnumValue<E>[] } = {}
): EnumOption<EnumValue<E>>[] {
  const options: EnumOption<EnumValue<E>>[] = [];
  for (const value of enumValues(e)) {
    const label = labels[value];
    if (label === undefined || (only && !only.includes(value))) continue;
    options.push({ value, label });
  }
  return options;
}
