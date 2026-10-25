import type { MessageId } from '@/lib/l10n';

// Contract of every input primitive
export interface Field<T> {
  value: T;
  onChange: (value: T) => void;
  // Inert skeleton with the geometry of the value area
  loading?: boolean;
  // aria-disabled, stays focusable so users can reach the reason
  disabled?: boolean;
  // The control draws the state, the row places the message
  error?: boolean | string;
}

// Wiring from the row or form field hosting the control
export interface FieldWiring {
  id?: string;
  labelledBy?: string;
  // The row's message element
  describedBy?: string;
  // Tooltip while disabled
  disabledReason?: MessageId;
}

export type FieldProps<T> = Field<T> & FieldWiring;

export type FieldState = 'loading' | 'disabled' | 'error' | 'ready';

export function fieldState(
  field: Pick<Field<unknown>, 'loading' | 'disabled' | 'error'>
): FieldState {
  if (field.loading) return 'loading';
  if (field.disabled) return 'disabled';
  if (field.error) return 'error';
  return 'ready';
}

export function fieldBlocked(state: FieldState): boolean {
  return state === 'loading' || state === 'disabled';
}

// Space separated ids for aria-labelledby/-describedby, undefined when empty
export function idList(
  ...ids: (string | false | null | undefined)[]
): string | undefined {
  const list = ids.filter(Boolean).join(' ');
  return list || undefined;
}
