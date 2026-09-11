import { useEffect, useMemo } from 'react';
import { useConfig } from './config';
import { useLocalization } from '@fluent/react';
import { connectedIMUTrackersAtom } from '@/store/app-store';
import { useAtomValue } from 'jotai';
import { useElectron } from './electron';

export function useDiscordPresence() {
  const electron = useElectron();
  if (!electron.isElectron) return;

  const { config } = useConfig();
  const { l10n } = useLocalization();
  const imuTrackers = useAtomValue(connectedIMUTrackersAtom);
  const imuTrackersCount = useMemo(() => imuTrackers.length, [imuTrackers.length]);

  useEffect(() => {
    if (config?.discordPresence === false) {
      electron.api.setPresence({ enable: false });
      return;
    }

    electron.api.setPresence({
      enable: true,
      activity: l10n.getString('settings-general-interface-discord_presence-message', {
        amount: imuTrackersCount,
      }),
      iconText: (__VERSION_TAG__ || __COMMIT_HASH__) + (__GIT_CLEAN__ ? '' : '-dirty'),
    });
  }, [config?.discordPresence, imuTrackersCount]);
}
