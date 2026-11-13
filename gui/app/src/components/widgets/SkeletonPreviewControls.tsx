import classNames from 'classnames';
import { Clickable } from '@/components/commons/Clickable';
import { Tooltip } from '@/components/commons/Tooltip';
import { Typography } from '@/components/commons/Typography';
import { EyeIcon } from '@/components/commons/icon/EyeIcon';
import { CubeIcon } from '@/components/commons/icon/CubeIcon';
import { LineIcon } from '@/components/commons/icon/LineIcon';
import { CameraLockIcon } from '@/components/commons/icon/CameraLockIcon';
import { useConfig } from '@/hooks/config';

const pillClass =
  'flex justify-center items-center w-10 h-10 rounded-full fill-background-10';
const pillActiveClass =
  'cursor-pointer bg-background-60 hover:bg-background-50';
const pillDimmedClass = 'cursor-not-allowed bg-background-60 opacity-40';

export function SkeletonPreviewControls({
  className,
  disabled = false,
  followLocked,
  onResetCamera,
  renderDisabled,
  onToggleRender,
}: {
  className?: string;
  disabled?: boolean;
  followLocked: boolean;
  onResetCamera: () => void;
  renderDisabled?: boolean;
  onToggleRender?: () => void;
}) {
  const { config, setConfig } = useConfig();

  return (
    <div className={classNames('flex gap-2', className)}>
      <Tooltip
        preferedDirection="bottom"
        content={<Typography id="preview-reset_camera" />}
      >
        <Clickable
          disabled={disabled || followLocked}
          aria-hidden={disabled}
          className={classNames(
            pillClass,
            followLocked ? pillDimmedClass : pillActiveClass
          )}
          onClick={() => onResetCamera()}
        >
          <CameraLockIcon size={23} locked={followLocked} />
        </Clickable>
      </Tooltip>
      <Tooltip
        preferedDirection="bottom"
        content={<Typography id="preview-render_mode" />}
      >
        <Clickable
          disabled={disabled}
          aria-hidden={disabled}
          className={classNames(pillClass, pillActiveClass)}
          onClick={() =>
            setConfig({
              skeletonPreviewStyle:
                config?.skeletonPreviewStyle == 'lines' ? 'mesh' : 'lines',
            })
          }
        >
          {config?.skeletonPreviewStyle == 'lines' && <CubeIcon width={18} />}
          {config?.skeletonPreviewStyle == 'mesh' && <LineIcon size={18} />}
        </Clickable>
      </Tooltip>
      {onToggleRender && (
        <Tooltip
          preferedDirection="bottom"
          content={<Typography id="preview-disable_render" />}
        >
          <Clickable
            disabled={disabled}
            aria-hidden={disabled}
            pressed={!renderDisabled}
            className={classNames(pillClass, pillActiveClass)}
            onClick={() => onToggleRender()}
          >
            <EyeIcon width={18} closed={!renderDisabled} />
          </Clickable>
        </Tooltip>
      )}
    </div>
  );
}
