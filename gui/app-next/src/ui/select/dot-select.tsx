import { For, Show } from 'solid-js';
import { t, type MessageId } from '@/lib/l10n';
import {
  MultiSelect,
  type MultiSelectProps,
  optionText,
  Select,
  type SelectOption,
  type SelectProps,
} from './select';

export type DotOption<T> = SelectOption<T> & { color: string; detail?: string };

export function ColorDot(props: { color: string }) {
  return (
    <span
      class="size-2 shrink-0 rounded-full"
      style={{ 'background-color': props.color }}
    />
  );
}

function DotLabel<T>(props: { option: DotOption<T> }) {
  return (
    <span class="flex min-w-0 items-center gap-sm">
      <ColorDot color={props.option.color} />
      <span class="truncate">{optionText(props.option)}</span>
    </span>
  );
}

function withRows<T>(options: readonly DotOption<T>[]): DotOption<T>[] {
  return options.map((option) => ({
    ...option,
    render: () => (
      <>
        <ColorDot color={option.color} />
        <span class="min-w-0 grow">{optionText(option)}</span>
        <Show when={option.detail}>
          {(detail) => <span class="font-mono text-fg-muted">{detail()}</span>}
        </Show>
      </>
    ),
  }));
}

export interface DotSelectProps<T> extends Omit<
  SelectProps<T>,
  'options' | 'renderValue'
> {
  options: readonly DotOption<T>[];
}

export function DotSelect<T>(props: DotSelectProps<T>) {
  return (
    <Select
      {...props}
      options={withRows(props.options)}
      renderValue={(option) => <DotLabel option={option as DotOption<T>} />}
    />
  );
}

export interface DotMultiSelectProps<T> extends Omit<
  MultiSelectProps<T>,
  'options' | 'renderValue'
> {
  options: readonly DotOption<T>[];
  allLabel?: MessageId;
}

export function DotMultiSelect<T>(props: DotMultiSelectProps<T>) {
  return (
    <MultiSelect
      {...props}
      options={withRows(props.options)}
      renderValue={(selected) => {
        const list = selected as DotOption<T>[];
        if (list.length === 1) return <DotLabel option={list[0]!} />;
        if (props.allLabel && list.length === props.options.length)
          return t(props.allLabel);
        return (
          <span class="flex items-center gap-0.75">
            <For each={list}>
              {(option) => <ColorDot color={option.color} />}
            </For>
            <span class="sr-only">
              {t('input-select-count', { count: list.length })}
            </span>
          </span>
        );
      }}
    />
  );
}
