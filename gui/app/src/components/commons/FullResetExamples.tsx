import classNames from 'classnames';
import { CheckIcon } from './icon/CheckIcon';
import { CrossIcon } from './icon/CrossIcon';
import { Typography } from './Typography';

const EXAMPLES = [
  {
    id: 'front',
    src: '/images/reset/FullResetPose.webp',
    alt: 'Reset position',
    ok: true,
  },
  {
    id: 'side',
    src: '/images/reset/FullResetPoseSide.webp',
    alt: 'Reset position side',
    ok: true,
  },
  {
    id: 'wrong',
    src: '/images/reset/FullResetPoseWrong.webp',
    alt: 'Reset position wrong',
    ok: false,
  },
];

export function FullResetExamples({
  captions = false,
  className,
  tileClassName = 'bg-background-70',
}: {
  captions?: boolean;
  className?: string;
  tileClassName?: string;
}) {
  return (
    <div className={classNames('grid grid-cols-3 gap-2', className)}>
      {EXAMPLES.map(({ id, src, alt, ok }) => (
        <div
          key={id}
          className={classNames(
            'flex flex-col min-h-0 rounded-md relative',
            tileClassName
          )}
        >
          {ok ? (
            <CheckIcon className="md:w-10 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-success" />
          ) : (
            <CrossIcon className="md:w-10 sm:w-8 w-6 h-auto absolute top-2 right-2 fill-status-critical" />
          )}
          <img
            src={src}
            className="min-h-0 flex-1 w-full object-contain p-2"
            alt={alt}
          />
          {captions && (
            <div className="px-3 pb-3">
              <Typography
                textAlign="text-center"
                id={`onboarding-automatic_mounting-preparation-example-${id}`}
              />
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
