import { Localized } from '@fluent/react';
import { Button } from './commons/Button';
import { LoaderIcon, SlimeState } from './commons/icon/LoaderIcon';

export function CrashScreen({ eventId }: { eventId: string }) {
  return (
    <div className="flex items-center justify-center h-full w-full flex-col gap-4 p-4 bg-background-80 text-background-10">
      <LoaderIcon slimeState={SlimeState.SAD} size={200} />
      <div className="max-w-xl flex flex-col gap-3">
        <Localized id="crash_screen-title">
          <h1 className="text-main-title" />
        </Localized>
        <Localized id="crash_screen-description">
          <p className="text-standard" />
        </Localized>
        {eventId && (
          <Localized id="crash_screen-event_id" vars={{ id: eventId }}>
            <p className="text-standard font-mono" />
          </Localized>
        )}
        <div className="flex flex-row gap-2">
          {eventId && (
            <Button
              variant="secondary"
              id="crash_screen-copy"
              onClick={() => navigator.clipboard.writeText(eventId)}
            />
          )}
          <Button
            variant="primary"
            id="crash_screen-reload"
            onClick={() => window.location.reload()}
          />
        </div>
      </div>
    </div>
  );
}
