import { A } from '@solidjs/router';
import { type JSX, Show } from 'solid-js';
import ChevronIcon from '~icons/material-symbols/chevron-right-rounded';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { useMotionItem } from '@/ui/motion';

export interface ListLinkProps {
  to: string;
  icon?: JSX.Element;
  // Drill-in row: chevron at the end
  chevron?: boolean;
  end?: boolean;
  children: JSX.Element;
}

// Full width menu row, the current route gets the accent
export function ListLink(props: ListLinkProps) {
  let el!: HTMLAnchorElement;
  useMotionItem(() => el);

  return (
    <A
      ref={el}
      href={props.to}
      end={props.end}
      class="
        flex min-h-control-sm items-center gap-md rounded-md px-md py-sm transition-colors
        hover:bg-surface-hover aria-[current=page]:bg-accent-subtle
        aria-[current=page]:text-accent-strong
      "
    >
      <Show when={props.icon}>
        <span class="flex shrink-0 icon-md">{props.icon}</span>
      </Show>
      <span class="min-w-0 grow">{props.children}</span>
      <Show when={props.chevron}>
        <ChevronIcon aria-hidden="true" class="icon shrink-0 icon-md" />
      </Show>
    </A>
  );
}

export interface TextListLinkProps extends Omit<ListLinkProps, 'children'> {
  label: MessageId;
  args?: MessageArgs;
}

export function TextListLink(props: TextListLinkProps) {
  return (
    <ListLink
      to={props.to}
      icon={props.icon}
      chevron={props.chevron}
      end={props.end}
    >
      {t(props.label, props.args)}
    </ListLink>
  );
}
