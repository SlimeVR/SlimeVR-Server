import { useEffect } from 'react';
import { useConfig } from './config';
import { useInterval } from './timeout';
import { invoke } from '@tauri-apps/api/core';
import { warn } from '@/utils/logging';
import { useLocalization } from '@fluent/react';
import { connectedIMUTrackersAtom } from '@/store/app-store';
import { getDefaultStore } from 'jotai';

export function useDiscordPresence() {
  const { config } = useConfig();
  const { l10n } = useLocalization();

  // Update presence every 6.9 seconds
  useInterval(
    () => {
      (async () => {
        try {
          // Better to do this instead of useAtomValue as we are doing polling with the interval
          // useAtomValue can trigger re render of the dom and this hook is top level, so this
          // would be really bad
          const imuTrackers = getDefaultStore().get(connectedIMUTrackersAtom);
          if (await checkDiscordClient()) {
            // If discord client exists, try updating presence
            await updateDiscordPresence({
              details: l10n.getString(
                'settings-general-interface-discord_presence-message',
                { amount: imuTrackers.length }
              ),
            });
          } else {
            // else, try creating a discord client
            await createDiscordClient();
          }
        } catch (e) {
          warn(`failed to update presence, error: ${e}`);
        }
      })();
    },
    config?.discordPresence ? 6900 : null
  );

  // Clear presence on config being disabled
  useEffect(() => {
    if (config?.discordPresence !== false) return;

    (async () => {
      if (!(await checkDiscordClient())) return;
      clearDiscordPresence().catch((e) =>
        warn(`failed to clear discord presence, error: ${e}`)
      );
    })();
  }, [config?.discordPresence]);
}

export function checkDiscordClient(): Promise<boolean> {
  return invoke('discord_client_exists');
}

export function createDiscordClient(): Promise<void> {
  return invoke('create_discord_client');
}

export function clearDiscordPresence(): Promise<void> {
  return invoke('clear_presence');
}

export function updateDiscordPresence(obj: {
  details: string;
  state?: string;
  small_icon?: [string, string];
  button?: { label: string; url: string };
}): Promise<void> {
  return invoke('update_presence', obj);
}
