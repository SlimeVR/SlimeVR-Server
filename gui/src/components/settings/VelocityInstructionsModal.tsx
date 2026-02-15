import { useLocalization } from '@fluent/react';
import { BaseModal } from '@/components/commons/BaseModal';
import { Button } from '@/components/commons/Button';
import { Typography } from '@/components/commons/Typography';
interface VelocityInstructionsModalProps {
  isOpen: boolean;
  onClose: () => void;
}
export function VelocityInstructionsModal({
  isOpen,
  onClose,
}: VelocityInstructionsModalProps) {
  const { l10n } = useLocalization();
  return (
    <BaseModal
      isOpen={isOpen}
      onRequestClose={onClose}
      className="w-full max-w-2xl min-w-[450px] h-[80vh] min-h-[600px] bg-background-60 p-6 rounded-lg text-background-10 focus:outline-none"
      shouldCloseOnOverlayClick
      shouldCloseOnEsc
    >
      <div className="flex flex-col h-full gap-2">
        {/* Title section - 5% */}
        <div className="h-[5%] min-h-[40px]">
          <Typography variant="main-title">
            {l10n.getString('settings-general-tracker_velocity-instructions-title')}
          </Typography>
        </div>

        {/* Content section - 85% */}
        <div className="h-[85%] overflow-y-auto relative scrollbar-thin scrollbar-thumb-background-60 scrollbar-track-transparent">
          {/* Top shadow gradient */}
          <div className="sticky top-0 h-6 bg-gradient-to-b from-background-60 via-background-60/80 to-transparent pointer-events-none z-10" />

          <div className="px-2 pb-12 space-y-6">
            {/* Section 1: Before you start */}
            <div>
              <Typography variant="section-title" className="pb-2">
                {l10n.getString('settings-general-tracker_velocity-instructions-section1-title')}
              </Typography>
              <div className="space-y-2">
                {l10n.getString('settings-general-tracker_velocity-instructions-section1-content')
                  .split('\n')
                  .filter(line => line.trim())
                  .map((line, index) => (
                    <Typography
                      key={index}
                      color="secondary"
                      className="leading-relaxed"
                    >
                      {line.trim()}
                    </Typography>
                  ))}
              </div>
            </div>

            {/* Section 2: Recommended settings */}
            <div>
              <Typography variant="section-title" className="pb-2">
                {l10n.getString('settings-general-tracker_velocity-instructions-section2-title')}
              </Typography>
              <div className="space-y-2">
                {l10n.getString('settings-general-tracker_velocity-instructions-section2-content')
                  .split('\n')
                  .filter(line => line.trim())
                  .map((line, index) => {
                    const trimmedLine = line.trim();
                    const isKeyPoint = !trimmedLine.startsWith('•') && !trimmedLine.startsWith('Note:');
                    return (
                      <Typography
                        key={index}
                        color={isKeyPoint ? "primary" : "secondary"}
                        className={`leading-relaxed ${isKeyPoint ? "text-base font-medium" : ""}`}
                      >
                        {trimmedLine}
                      </Typography>
                    );
                  })}
              </div>
            </div>

            {/* Section 3: Common Issues */}
            <div>
              <Typography variant="section-title" className="pb-2">
                {l10n.getString('settings-general-tracker_velocity-instructions-section3-title')}
              </Typography>
              <div className="space-y-2">
                {l10n.getString('settings-general-tracker_velocity-instructions-section3-content')
                  .split('\n')
                  .filter(line => line.trim())
                  .map((line, index) => {
                    const trimmedLine = line.trim();
                    const isKeyPoint = !trimmedLine.startsWith('•') &&
                                     !trimmedLine.startsWith('Note:') &&
                                     !trimmedLine.startsWith('Check:') &&
                                     !trimmedLine.startsWith('Solution:');
                    return (
                      <Typography
                        key={index}
                        color={isKeyPoint ? "primary" : "secondary"}
                        className={`leading-relaxed ${isKeyPoint ? "text-base font-medium" : ""}`}
                      >
                        {trimmedLine}
                      </Typography>
                    );
                  })}
              </div>
            </div>
          </div>

          {/* Bottom shadow gradient */}
          <div className="sticky bottom-0 h-6 bg-gradient-to-t from-background-60 via-background-60/80 to-transparent pointer-events-none z-10 -mt-6" />
        </div>

        {/* Button section - 10% */}
        <div className="h-[10%] min-h-[60px] flex items-center justify-center border-t border-background-50 pt-2">
          <Button variant="primary" onClick={onClose}>
            {l10n.getString('settings-general-tracker_velocity-instructions-close')}
          </Button>
        </div>
      </div>
    </BaseModal>
  );
}
