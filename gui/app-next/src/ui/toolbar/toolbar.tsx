import type { JSX } from 'solid-js';
import { cx } from '@/lib/cx';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { MotionToolbar } from '@/ui/motion';

export interface ToolbarProps {
  label?: MessageId;
  args?: MessageArgs;
  class?: string;
  children: JSX.Element;
}

export function Toolbar(props: ToolbarProps) {
  return (
    <MotionToolbar
      aria-label={props.label && t(props.label, props.args)}
      class={cx('flex flex-wrap items-center gap-sm', props.class)}
    >
      {props.children}
    </MotionToolbar>
  );
}
