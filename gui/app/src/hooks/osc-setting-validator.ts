import { useLocalization } from '@fluent/react';
import { number, object, string } from 'yup';

export type OSCPortsAddress = {
  portIn: number;
  portOut: number;
  address: string;
};

export function useOscPortsAddressValidator() {
  const bannedPorts = [6969, 21110];
  const { l10n } = useLocalization();

  const portValidator = number()
    .typeError(' ')
    .required()
    .test(
      'ports-dont-match',
      l10n.getString('settings-osc-common-network-ports_match_error-v2'),
      (port, context) => context.parent.portIn != context.parent.portOut
    )
    .notOneOf(bannedPorts, (context) =>
      l10n.getString('settings-osc-common-network-port_banned_error', {
        port: context.originalValue,
      })
    );

  const oscValidator = object({
    portIn: portValidator,
    portOut: portValidator,
    address: string()
      .required(' ')
      .matches(/^(?!0)(?!.*\.$)((1?\d?\d|25[0-5]|2[0-4]\d)(\.|$)){4}$/i, {
        message: ' ',
      }),
  });

  return { oscValidator };
}
