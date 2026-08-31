import classNames from 'classnames';
import { useLocalization } from '@fluent/react';
import { useEffect, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { useParams } from 'react-router-dom';
import { useAtomValue } from 'jotai';
import {
  ChangeDongleSettingsRequestT,
  DongleStatus,
  RpcMessage,
} from 'solarxr-protocol';
import { useDebouncedEffect } from '@/hooks/timeout';
import { useWebsocketAPI } from '@/hooks/websocket-api';
import { ArrowLink } from '@/components/commons/ArrowLink';
import { Button } from '@/components/commons/Button';
import { Input } from '@/components/commons/Input';
import { Typography } from '@/components/commons/Typography';
import { BodyPartIcon } from '@/components/commons/BodyPartIcon';
import { DongleTelemetry } from './DongleTelemetry';
import { TrackerStatus } from './TrackerStatus';
import { ConnectionGroupIcon } from './TrackerConnectionGroup';
import {
  donglesAtom,
  flatTrackersAtom,
  groupTrackersByConnection,
} from '@/store/app-store';

function DongleStatusLabel({ status }: { status: DongleStatus }) {
  const connected = status === DongleStatus.CONNECTED;

  return (
    <div className="flex text-default gap-2">
      <div className="flex flex-col justify-center">
        <div
          className={classNames(
            'w-2 h-2 rounded-full',
            connected ? 'bg-status-success' : 'bg-background-30'
          )}
        />
      </div>
      <Typography
        whitespace="whitespace-nowrap"
        id={
          connected ? 'dongle-status-connected' : 'dongle-status-disconnected'
        }
      />
    </div>
  );
}

export function DongleSettingsPage() {
  const { l10n } = useLocalization();
  const { sendRPCPacket } = useWebsocketAPI();
  const { dongleid } = useParams<{ dongleid: string }>();
  const dongleId = dongleid ? +dongleid : undefined;

  const { control, watch, reset } = useForm<{ dongleName: string }>({
    defaultValues: { dongleName: '' },
    reValidateMode: 'onSubmit',
  });
  const { dongleName } = watch();

  const dongles = useAtomValue(donglesAtom);
  const dongle = useMemo(
    () => dongles.find((d) => d.id === dongleId),
    [dongles, dongleId]
  );

  const flatTrackers = useAtomValue(flatTrackersAtom);
  const pairedTrackers = useMemo(() => {
    const group = groupTrackersByConnection(flatTrackers, dongles).find(
      (g) => g.kind === 'dongle' && g.dongleId === dongleId
    );
    return group ? [...group.assigned, ...group.unassigned] : [];
  }, [flatTrackers, dongles, dongleId]);

  const displayName = dongle?.displayName?.toString();
  const customName = dongle?.customName?.toString();
  const manufacturer = dongle?.manufacturer?.toString();
  const model = dongle?.model?.toString();
  const boardType = dongle?.boardType?.toString();
  const hardwareRevision = dongle?.hardwareRevision?.toString();
  const firmwareVersion = dongle?.firmwareVersion?.toString();
  const firmwareDate = dongle?.firmwareDate?.toString();

  const updateDongleSettings = () => {
    if (dongle?.id == null) return;
    if (dongleName === (customName ?? '')) return;

    const req = new ChangeDongleSettingsRequestT(dongle.id, dongleName || null);
    sendRPCPacket(RpcMessage.ChangeDongleSettingsRequest, req);
  };

  useDebouncedEffect(() => updateDongleSettings(), [dongleName], 1000);

  useEffect(() => {
    reset({ dongleName: customName ?? '' });
  }, []);

  return (
    <div className="h-full overflow-y-auto">
      <div className="flex gap-2 max-md:flex-wrap md:flex-row xs:flex-col mobile:flex-col min-h-full">
        <div className="flex flex-col w-full md:max-w-xs gap-2">
          <div className="flex flex-col items-center justify-center rounded-md py-10 px-6 w-full gap-2 box-border bg-background-70">
            <ConnectionGroupIcon
              kind="dongle"
              disconnected={dongle?.status === DongleStatus.DISCONNECTED}
              size={48}
            />
            <Typography bold truncate>
              {customName || displayName}
            </Typography>
            {dongle && <DongleStatusLabel status={dongle.status} />}
          </div>

          <div className="flex flex-col bg-background-70 p-3 rounded-lg gap-2">
            <Typography
              variant="section-title"
              id="dongle-settings-update-title"
            />
            <div className="flex gap-2 flex-col">
              <div className="flex justify-between gap-2">
                <Typography id="tracker-settings-build-date" />
                <Typography
                  whitespace="whitespace-pre-wrap"
                  textAlign="text-end"
                >
                  {firmwareDate || '--'}
                </Typography>
              </div>
              <div className="flex justify-between gap-2">
                <Typography id="tracker-settings-current-version" />
                <Typography
                  whitespace="whitespace-pre-wrap"
                  textAlign="text-end"
                >
                  {firmwareVersion ? `v${firmwareVersion}` : '--'}
                </Typography>
              </div>
              <div className="flex justify-between gap-2">
                <Typography id="tracker-settings-latest-version" />
                <Typography textAlign="text-end">--</Typography>
              </div>
            </div>
            <Button variant="secondary" disabled id="dongle-settings-update" />
          </div>

          <div className="flex flex-col bg-background-70 p-3 rounded-lg gap-2 overflow-x-auto">
            <div className="flex justify-between">
              <Typography id="tracker-infos-manufacturer" />
              <Typography>{manufacturer || '--'}</Typography>
            </div>
            <div className="flex justify-between">
              <Typography id="tracker-infos-display_name" />
              <Typography>{displayName || '--'}</Typography>
            </div>
            <div className="flex justify-between">
              <Typography id="tracker-infos-custom_name" />
              <Typography sentryMask>{customName || '--'}</Typography>
            </div>
            <div className="flex justify-between">
              <Typography id="dongle-infos-model" />
              <Typography>{model || '--'}</Typography>
            </div>
            <div className="flex justify-between">
              <Typography id="tracker-infos-hardware_identifier" />
              <Typography>
                {dongle?.hardwareAddress
                  ? dongle.hardwareAddress.addr.toString(16).toUpperCase()
                  : '--'}
              </Typography>
            </div>
            <div className="flex justify-between">
              <Typography id="tracker-infos-board_type" />
              <Typography>{boardType || '--'}</Typography>
            </div>
            <div className="flex justify-between">
              <Typography id="dongle-infos-hardware_revision" />
              <Typography>{hardwareRevision || '--'}</Typography>
            </div>
          </div>
        </div>

        <div className="flex flex-col gap-2 flex-grow">
          <div className="flex flex-col bg-background-70 rounded-lg p-5 gap-3">
            <ArrowLink to="/">
              {l10n.getString('dongle-settings-back')}
            </ArrowLink>
            <Typography variant="main-title" id="dongle-settings-title" />

            <div className="flex flex-col gap-2 w-full sentry-mask">
              <Typography
                variant="section-title"
                id="dongle-settings-name_section"
              />
              <Typography id="dongle-settings-name_section-description" />
              <Input
                placeholder={l10n.getString(
                  'dongle-settings-name_section-placeholder'
                )}
                type="text"
                name="dongleName"
                control={control}
                autocomplete="off"
                rules={undefined}
              />
            </div>
          </div>
          <div className="flex flex-col flex-grow bg-background-70 rounded-lg p-5 gap-3">
            <div className="flex flex-col gap-2 w-full">
              <div className="flex justify-between items-center">
                <Typography
                  variant="section-title"
                  id="dongle-settings-paired_trackers"
                />
                <Button variant="primary" disabled id="dongle-settings-pair" />
              </div>
              <div className="flex flex-col gap-2">
                {pairedTrackers.map(({ tracker, device }, index) => (
                  <div
                    key={index}
                    className="flex justify-between items-center bg-background-80 w-full p-3 rounded-lg"
                  >
                    <div className="flex gap-3 items-center fill-background-10">
                      <BodyPartIcon
                        bodyPart={tracker.info?.bodyPart}
                        device={device}
                        trackerId={tracker.trackerId}
                        width={32}
                      />
                      <div className="flex flex-col">
                        <Typography bold>
                          {tracker.info?.customName?.toString() ||
                            tracker.info?.displayName?.toString()}
                        </Typography>
                        <TrackerStatus status={tracker.status} />
                      </div>
                    </div>
                    <Button
                      variant="secondary"
                      disabled
                      id="dongle-settings-forget_tracker"
                    />
                  </div>
                ))}
                {pairedTrackers.length === 0 && (
                  <Typography
                    color="secondary"
                    id="dongle-settings-paired_trackers-empty"
                  />
                )}
              </div>
            </div>
          </div>
          <DongleTelemetry trackers={pairedTrackers} />
        </div>
      </div>
    </div>
  );
}
