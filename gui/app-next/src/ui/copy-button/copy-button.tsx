import { createSignal, onCleanup } from 'solid-js';
import CheckIcon from '~icons/material-symbols/check-rounded';
import CopyIcon from '~icons/material-symbols/content-copy-rounded';
import { t, type MessageArgs, type MessageId } from '@/lib/l10n';
import { Button, type ButtonSize, type ButtonVariant } from '@/ui/button';
import { showToast } from '@/ui/toast';

export const COPY_FEEDBACK_MS = 2000;

export interface CopyButtonProps {
  // Read at click time
  text: string | (() => string);
  label: MessageId;
  args?: MessageArgs;
  variant?: ButtonVariant;
  size?: ButtonSize;
  iconOnly?: boolean;
  disabled?: boolean;
  disabledReason?: MessageId;
  class?: string;
}

// The icon turns into a check for a moment and a status line announces it
export function CopyButton(props: CopyButtonProps) {
  const [copied, setCopied] = createSignal(false);
  let timer: ReturnType<typeof setTimeout> | undefined;
  onCleanup(() => clearTimeout(timer));

  async function copy() {
    const text = typeof props.text === 'function' ? props.text() : props.text;
    try {
      await navigator.clipboard.writeText(text);
    } catch {
      showToast({
        kind: 'error',
        message: 'input-copy-failed',
        key: 'copy-failed',
      });
      return;
    }
    clearTimeout(timer);
    setCopied(true);
    timer = setTimeout(() => setCopied(false), COPY_FEEDBACK_MS);
  }

  return (
    <>
      <Button
        variant={props.variant}
        size={props.size}
        label={props.label}
        args={props.args}
        iconOnly={props.iconOnly}
        disabled={props.disabled}
        disabledReason={props.disabledReason}
        class={props.class}
        icon={copied() ? <CheckIcon /> : <CopyIcon />}
        onClick={() => void copy()}
      />
      <span class="sr-only" role="status">
        {copied() ? t('input-copy-done') : ''}
      </span>
    </>
  );
}
