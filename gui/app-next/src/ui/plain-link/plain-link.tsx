import { A } from '@solidjs/router';
import { type JSX, Show } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageId } from '@/lib/l10n';
import { platform } from '@/lib/platform';
import { useMotionItem } from '@/ui/motion';

type PlainLinkBase = {
  label?: MessageId;
  // Id of a tooltip or text describing the link
  describedBy?: string;
  class?: string;
  ref?: (el: HTMLAnchorElement) => void;
  children: JSX.Element;
};

export type PlainLinkProps = PlainLinkBase &
  ({ to: string; href?: undefined } | { href: string; to?: undefined });

export function PlainLink(props: PlainLinkProps) {
  let el!: HTMLAnchorElement;
  useMotionItem(() => el);

  function setRef(node: HTMLAnchorElement) {
    el = node;
    props.ref?.(node);
  }

  const className = () => cx('flex items-center', props.class);
  const label = () => (props.label ? t(props.label) : undefined);

  function openExternal(e: MouseEvent) {
    e.preventDefault();
    platform.openUrl(props.href!);
  }

  return (
    <Show
      when={props.to}
      fallback={
        <a
          ref={(e) => setRef(e)}
          href={props.href}
          aria-label={label()}
          aria-describedby={props.describedBy}
          class={className()}
          onClick={openExternal}
        >
          {props.children}
        </a>
      }
    >
      {(to) => (
        <A
          ref={setRef}
          href={to()}
          aria-label={label()}
          aria-describedby={props.describedBy}
          class={className()}
        >
          {props.children}
        </A>
      )}
    </Show>
  );
}
