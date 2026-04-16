import { useEffect, useMemo } from 'react';
import { useConfig } from './config';
import { useLocalization } from '@fluent/react';
import { connectedIMUTrackersAtom } from '@/store/app-store';
import { useAtomValue } from 'jotai';
import { useElectron } from './electron';

export function useDiscordPresence() {
  const { config } = useConfig();
  const { l10n } = useLocalization();
  const electron = useElectron();
  const imuTrackers = useAtomValue(connectedIMUTrackersAtom);
  const imuTrackersCount = useMemo(() => imuTrackers.length, [imuTrackers.length]);

  useEffect(() => {
    if (config?.discordPresence === false) return;

    if (!electron.isElectron) return;
    electron.api.setPresence({
      enable: true,
      activity: l10n.getString('settings-general-interface-discord_presence-message', {
        amount: imuTrackersCount,
      }),
    });

    return () => {
      if (!electron.isElectron) return;
      electron.api.setPresence({ enable: false });
    };
  }, [config?.discordPresence, imuTrackersCount]);
}
