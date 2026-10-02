import { Localized } from '@fluent/react';
import { ErrorReportingConsent } from 'solarxr-protocol';
import { Typography } from './commons/Typography';
import { Button } from './commons/Button';
import { EmptyLayout } from './EmptyLayout';
import { useErrorReporting } from '@/hooks/error-reporting';

export function ErrorReportingConsentPage() {
  const { answer } = useErrorReporting();

  return (
    <EmptyLayout>
      <div className="flex items-center justify-center h-full flex-col gap-3 p-4">
        <div className="max-w-2xl flex flex-col gap-4">
          <div className="flex flex-col w-full gap-4">
            <Typography
              variant="main-title"
              id="error_collection_modal-title"
            />
            <Localized
              id="error_collection_modal-description_v2"
              elems={{
                b: <b />,
                h1: <span className="text-md font-bold" />,
              }}
            >
              <Typography variant="standard" whitespace="whitespace-pre-line" />
            </Localized>
          </div>
          <div className="flex flex-row gap-2 justify-between">
            <Button
              variant="tertiary"
              onClick={() => answer(ErrorReportingConsent.DENIED)}
              id="error_collection_modal-cancel"
            />
            <Button
              variant="primary"
              onClick={() => answer(ErrorReportingConsent.ALLOWED)}
              id="error_collection_modal-confirm"
            />
          </div>
        </div>
      </div>
    </EmptyLayout>
  );
}
