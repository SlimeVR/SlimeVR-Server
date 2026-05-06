import { Button } from '@/components/commons/Button';
import { WrenchIcon } from '@/components/commons/icon/WrenchIcons';
import { Typography } from '@/components/commons/Typography';
import {
  SettingsPageLayout,
  SettingsPagePaneLayout,
} from '@/components/settings/SettingsPageLayout';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { useLocalization } from '@fluent/react';
import { ComponentProps, useEffect, useState } from 'react';
import {
  RpcMessage,
  UpdatesRequest,
  UpdatesRequestT,
  UpdatesResponseT,
} from 'solarxr-protocol';
import Markdown, { MarkdownHooks } from 'react-markdown';
import classNames from 'classnames';
import { A } from '@/components/commons/A';
import remark from 'remark-gfm';
import { useElectron } from '@/hooks/electron';

export type updaterForm = {
  channels: {
    [channelName: string]: {
      versions: {
        [versionNumber: string]: {
          version: string;
          changelog: string;
        };
      };
    };
  };
};

export function UpdaterSettings() {
  const [mappedData, setMappedData] = useState<updaterForm | null>(null);
  const [selectedChannel, setSelectedChannel] = useState<string | null>(null);
  const [selectedVersion, setSelectedVersion] = useState<string | null>(null);

  const { l10n } = useLocalization();
  const { sendRPCPacket, useRPCPacket } = useWebsocketAPI();
    const electron = useElectron();
  function getVersionsList() {}

  function runUpdater() {}

  const onRunUpdater = () => {
    console.log("running updater")
    if (!electron.isElectron) throw "no electron";
    const updaterArgs = ["-r", "-i", selectedChannel, selectedVersion]
    electron.api.runUpdater(updaterArgs);
  }

  useEffect(() => {
    sendRPCPacket(RpcMessage.UpdatesRequest, new UpdatesRequestT());
  }, []);

  useRPCPacket(RpcMessage.UpdatesResponse, (response: UpdatesResponseT) => {
    if (!response.channels) return;

    const mappedData: updaterForm = {
      channels: response.channels.reduce(
        (allChannelsObject, currentChannel) => {
          const channelNameKey = String(currentChannel.channel);

          allChannelsObject[channelNameKey] = {
            versions: (currentChannel.versions || []).reduce(
              (allVersionsObject, currentVersion) => {
                const versionNumberKey = String(currentVersion.version);

                allVersionsObject[versionNumberKey] = {
                  version: versionNumberKey,
                  changelog: currentVersion.changeLog
                    ? String(currentVersion.changeLog)
                    : '',
                };

                return allVersionsObject;
              },
              {} as Record<string, { version: string; changelog: string }>
            ),
          };

          return allChannelsObject;
        },
        {} as Record<string, any>
      ),
    };

    console.log('Transformation Complete:', mappedData);
    setMappedData(mappedData);
  });

  const MarkdownLink = (props: ComponentProps<'a'>) => (
    <A href={props.href}>{props.children}</A>
  );

  const channelNames = mappedData?.channels
    ? Object.keys(mappedData.channels)
    : [];

  return (
    <SettingsPageLayout>
      <SettingsPagePaneLayout icon={<WrenchIcon />} id="steamvr">
        <Typography variant="main-title">Updater settings</Typography>

        <div className="w-full mt-4">
          {!mappedData ? (
            <div className="p-8 text-center bg-background-60 rounded-lg">
              <Typography>Loading update manifest...</Typography>
            </div>
          ) : (
            <table className="w-full border-separate border-spacing-2 table-fixed">
              <thead>
                <tr className="text-left text-sm opacity-70">
                  <th className="w-1/4">Channel</th>
                  <th className="w-1/4">Version</th>
                  <th className="w-1/2">Description</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td className="align-top">
                    <div className="flex flex-col gap-1 h-[400px] overflow-y-auto bg-background-60 rounded-lg p-2">
                      {channelNames.map((name) => (
                        <Button
                          variant="secondary"
                          key={name}
                          className={`text-left p-2 rounded transition ${
                            selectedChannel === name
                              ? 'bg-primary-60 text-white'
                              : 'hover:bg-white/10'
                          }`}
                          onClick={() => {
                            setSelectedChannel(name);
                            setSelectedVersion(null);
                          }}
                        >
                          {name}
                        </Button>
                      ))}
                    </div>
                  </td>

                  <td className="align-top">
                    <div className="flex flex-col gap-1 h-[400px] overflow-y-auto bg-background-60 rounded-lg p-2 text-center">
                      {selectedChannel &&
                      mappedData.channels[selectedChannel] ? (
                        Object.keys(
                          mappedData.channels[selectedChannel].versions
                        ).map((vNum) => (
                          <Button
                            variant="secondary"
                            key={vNum}
                            className={`text-left p-2 rounded transition ${
                              selectedVersion === vNum
                                ? 'bg-primary-60 text-white'
                                : 'hover:bg-white/10'
                            }`}
                            onClick={() => setSelectedVersion(vNum)}
                          >
                            {vNum}
                          </Button>
                        ))
                      ) : (
                        <div className="p-4 text-xs opacity-40">
                          Select a channel
                        </div>
                      )}
                    </div>
                  </td>

                  <td className="align-top">
                    <div className="h-[400px] overflow-y-auto bg-background-60 rounded-lg p-4 text-sm whitespace-pre-wrap">
                      {selectedChannel && selectedVersion ? (
                        <Markdown
                          remarkPlugins={[remark]}
                          components={{ a: MarkdownLink }}
                          className={classNames(
                            'w-full text-sm prose-xl prose text-background-10 prose-h1:text-background-10',
                            'prose-h2:text-background-10 prose-a:text-background-20 prose-strong:text-background-10',
                            'prose-code:text-background-20'
                          )}
                        >
                          {
                            mappedData.channels[selectedChannel].versions[
                              selectedVersion
                            ].changelog
                          }
                        </Markdown>
                      ) : (
                        <div className="opacity-40 italic">
                          Select a version to view details
                        </div>
                      )}
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          )}
        </div>
        <div className="flex flex-col w-max">
          <Button variant="primary"
          onClick={onRunUpdater}>Update</Button>
        </div>
      </SettingsPagePaneLayout>
    </SettingsPageLayout>
  );
}
