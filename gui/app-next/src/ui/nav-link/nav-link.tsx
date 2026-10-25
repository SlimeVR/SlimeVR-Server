import { A } from '@solidjs/router';
import type { JSX } from 'solid-js';
import { t, type MessageId } from '@/lib/l10n';
import { useMotionItem } from '@/ui/motion';

export interface NavLinkProps {
  to: string;
  label: MessageId;
  icon: JSX.Element;
  end?: boolean;
}

export function NavLink(props: NavLinkProps) {
  let el!: HTMLAnchorElement;
  useMotionItem(() => el);

  return (
    <A
      ref={el}
      href={props.to}
      end={props.end}
      class="
        group flex size-16 flex-col items-center justify-center gap-md rounded-t-md text-fg-muted
        transition-colors hover:bg-surface-hover aria-[current=page]:bg-accent-subtle
        aria-[current=page]:text-accent-strong shell:size-auto shell:rounded-md shell:py-md
      "
    >
      <span class="flex icon-lg">{props.icon}</span>
      <span
        class="
          sr-only text-center text-fg shell:not-sr-only group-aria-[current=page]:text-accent-strong
        "
      >
        {t(props.label)}
      </span>
    </A>
  );
}
