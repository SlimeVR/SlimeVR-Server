import { ErrorReportingConsent } from 'solarxr-protocol';
import { Typography } from './commons/Typography';
import { Button } from './commons/Button';
import { EmptyLayout } from './EmptyLayout';
import { useErrorReporting } from '@/hooks/error-reporting';
import { ErrorReportingDetails } from './ErrorReportingDetails';
import { LoaderIcon, SlimeState } from './commons/icon/LoaderIcon';

export function ErrorReportingConsentPage() {
  const { answer } = useErrorReporting();

  return (
    <EmptyLayout>
      <div className="flex w-full h-full justify-center items-center overflow-y-auto p-4">
        <div className="flex flex-col items-center gap-4 w-full max-w-2xl">
          <LoaderIcon slimeState={SlimeState.CURIOUS} size={200} />
          <div className="flex flex-col items-center text-center">
            <Typography
              variant="main-title"
              id="error_collection_modal-title"
            />
            <Typography id="error_collection_modal-subtitle" />
          </div>
          <ErrorReportingDetails />
          <Typography id="error_collection_modal-change_later" />
          <div className="flex w-full flex-col-reverse xs:flex-row gap-3 xs:justify-between">
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
