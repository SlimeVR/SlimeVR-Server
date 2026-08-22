import { BaseModal } from '@/components/commons/BaseModal';
import { A } from '@/components/commons/A';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
import { LoaderIcon, SlimeState } from '@/components/commons/icon/LoaderIcon';

export const DOCS_TROUBLESHOOTING_URL =
  'https://docs.slimevr.dev/common-issues.html';

export function ErrorDetailModal({
  isOpen,
  onClose,
  titleId,
  descId,
  docsAnchor,
}: {
  isOpen: boolean;
  onClose: () => void;
  titleId: string;
  descId?: string;
  docsAnchor?: string;
}) {
  return (
    <BaseModal
      isOpen={isOpen}
      onRequestClose={onClose}
      className="max-w-md w-full p-6 flex flex-col gap-4 bg-background-70 rounded-xl border border-background-60"
    >
      <div className="flex flex-col items-center text-center gap-3">
        <LoaderIcon slimeState={SlimeState.SAD} size={56} />

        <div className="flex flex-col gap-1">
          <Typography
            bold
            variant="section-title"
            color="text-status-critical"
            id={titleId}
          />
          {descId && <Typography variant="standard" id={descId} />}
        </div>

        <div className="flex flex-row items-center justify-between w-full pt-2 gap-4">
          <A
            href={`${DOCS_TROUBLESHOOTING_URL}${docsAnchor || ''}`}
            className="text-sm font-medium text-accent-background-10 hover:underline shrink-0"
          >
            <Typography id="onboarding-connect_tracker-learn_more" />
          </A>

          <Button
            variant="secondary"
            onClick={onClose}
            className="shrink-0 px-6"
            id="onboarding-connect_tracker-close"
          />
        </div>
      </div>
    </BaseModal>
  );
}

export function NoSerialLogsModal({
  isOpen,
  onClose,
}: {
  isOpen: boolean;
  onClose: () => void;
}) {
  return (
    <BaseModal
      isOpen={isOpen}
      onRequestClose={onClose}
      className="max-w-md w-full p-6 flex flex-col gap-4 bg-background-70 rounded-xl border border-background-60"
    >
      <div className="flex flex-col items-center text-center gap-3">
        <LoaderIcon slimeState={SlimeState.SAD} size={56} />

        <div className="flex flex-col gap-1">
          <Typography
            bold
            variant="section-title"
            color="text-status-critical"
            id="onboarding-connect_tracker-connection_status-no_serial_log"
          />
          <Typography
            variant="standard"
            id="onboarding-connect_serial-error-modal-no_serial_log-desc"
          />
        </div>

        <div className="w-full aspect-video rounded-lg overflow-hidden my-1 bg-background-90">
          <video
            src="/videos/troubleshoot.mp4"
            autoPlay
            loop
            muted
            playsInline
            className="w-full h-full object-cover"
          />
        </div>

        <div className="flex flex-row items-center justify-between w-full pt-2 gap-4">
          <A
            href={`${DOCS_TROUBLESHOOTING_URL}#no-serial-device-appears--looking-for-trackers--connection-to-serial-lost-reconnecting`}
            className="text-sm font-medium text-accent-background-10 hover:underline shrink-0"
          >
            <Typography id="onboarding-connect_tracker-learn_more" />
          </A>

          <Button
            variant="secondary"
            onClick={onClose}
            className="shrink-0 px-6"
            id="onboarding-connect_tracker-close"
          />
        </div>
      </div>
    </BaseModal>
  );
}
