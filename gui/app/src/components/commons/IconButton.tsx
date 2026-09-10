import { useLocalization } from '@fluent/react';
import { ComponentProps } from 'react';
import { Clickable } from './Clickable';

type DistributiveOmit<T, K extends PropertyKey> = T extends unknown
  ? Omit<T, K>
  : never;

/**
 * Icon-only button. `labelId` does double duty: it names the control for screen
 * readers and renders as the tooltip, so an icon never ships unlabelled.
 *
 * The tooltip is on by default here, since unlike `Clickable` there is no
 * visible text to fall back on. Pass `tooltip={false}` where it would be noise.
 *
 * Inherits `Clickable`'s link mode: pass `to` and it renders a `NavLink`.
 */
export function IconButton({
  labelId,
  tooltip = true,
  ...props
}: {
  /** Fluent id, used for both the accessible name and the tooltip text. */
  labelId: string;
  tooltip?: boolean;
} & DistributiveOmit<ComponentProps<typeof Clickable>, 'label' | 'tooltipId'>) {
  const { l10n } = useLocalization();

  return (
    <Clickable
      {...props}
      label={l10n.getString(labelId)}
      tooltipId={tooltip ? labelId : undefined}
    />
  );
}
