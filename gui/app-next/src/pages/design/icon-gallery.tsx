import { For, type Component, type ComponentProps } from 'solid-js';
import { Dynamic } from 'solid-js/web';
import ArrowDownTrayIcon from '~icons/heroicons/arrow-down-tray-solid';
import ArrowsRightLeftIcon from '~icons/heroicons/arrows-right-left';
import BugAntIcon from '~icons/heroicons/bug-ant-solid';
import DocumentArrowUpIcon from '~icons/heroicons/document-arrow-up-solid';
import DocumentDuplicateIcon from '~icons/heroicons/document-duplicate-solid';
import DocumentIcon from '~icons/heroicons/document-solid';
import ExclamationTriangleIcon from '~icons/heroicons/exclamation-triangle-20-solid';
import EyeSlashIcon from '~icons/heroicons/eye-slash-solid';
import EyeIcon from '~icons/heroicons/eye-solid';
import FolderIcon from '~icons/heroicons/folder-solid';
import MagnifyingGlassIcon from '~icons/heroicons/magnifying-glass-20-solid';
import PencilIcon from '~icons/heroicons/pencil-solid';
import PlayCircleIcon from '~icons/heroicons/play-circle-solid';
import PlusIcon from '~icons/heroicons/plus-solid';
import QuestionMarkCircleIcon from '~icons/heroicons/question-mark-circle';
import TrashIcon from '~icons/heroicons/trash-solid';
import KeyboardArrowDownIcon from '~icons/ic/baseline-keyboard-arrow-down';
import KeyboardArrowUpIcon from '~icons/ic/baseline-keyboard-arrow-up';
import KeyboardIcon from '~icons/ic/baseline-keyboard';
import RotateCcwIcon from '~icons/lucide/rotate-ccw';
import AccessibilityNewIcon from '~icons/material-symbols/accessibility-new-rounded';
import AirlineSeatIcon from '~icons/material-symbols/airline-seat-recline-normal';
import ArrowBackIcon from '~icons/material-symbols/arrow-back-rounded';
import ArrowForwardIcon from '~icons/material-symbols/arrow-forward-rounded';
import BuildIcon from '~icons/material-symbols/build-rounded';
import BarChartIcon from '~icons/material-symbols/bar-chart-4-bars-rounded';
import CheckIcon from '~icons/material-symbols/check-rounded';
import ChecklistIcon from '~icons/material-symbols/checklist';
import CloseIcon from '~icons/material-symbols/close-rounded';
import CompareArrowsIcon from '~icons/material-symbols/compare-arrows';
import CropSquareIcon from '~icons/material-symbols/crop-square-outline-rounded';
import DeployedCodeIcon from '~icons/material-symbols/deployed-code-rounded';
import DiagonalLineIcon from '~icons/material-symbols/diagonal-line';
import DirectionsWalkIcon from '~icons/material-symbols/directions-walk';
import DownhillSkiingIcon from '~icons/material-symbols/downhill-skiing';
import HeadMountedDeviceIcon from '~icons/material-symbols/head-mounted-device-rounded';
import HomeIcon from '~icons/material-symbols/home';
import HorizontalAlignCenterIcon from '~icons/material-symbols/horizontal-align-center-rounded';
import LightbulbIcon from '~icons/material-symbols/lightbulb-rounded';
import MinimizeIcon from '~icons/material-symbols/minimize-rounded';
import PauseIcon from '~icons/material-symbols/pause-rounded';
import PercentIcon from '~icons/material-symbols/percent';
import PlayArrowIcon from '~icons/material-symbols/play-arrow-rounded';
import QuestionMarkIcon from '~icons/material-symbols/question-mark';
import RouterIcon from '~icons/material-symbols/router-rounded';
import SettingsIcon from '~icons/material-symbols/settings-rounded';
import SimCardDownloadIcon from '~icons/material-symbols/sim-card-download';
import SportsMartialArtsIcon from '~icons/material-symbols/sports-martial-arts-rounded';
import StraightenIcon from '~icons/material-symbols/straighten-rounded';
import TuneIcon from '~icons/material-symbols/tune';
import UsbIcon from '~icons/material-symbols/usb-rounded';
import AnkleIcon from '~icons/slime/ankle';
import ButterflyIcon from '~icons/slime/butterfly';
import CameraLockIcon from '~icons/slime/camera-lock';
import ChestIcon from '~icons/slime/chest';
import ControllerIcon from '~icons/slime/controller';
import FingersIcon from '~icons/slime/fingers';
import FootIcon from '~icons/slime/foot';
import FullResetIcon from '~icons/slime/full-reset';
import HipIcon from '~icons/slime/hip';
import LayoutIcon from '~icons/slime/layout';
import LowerArmIcon from '~icons/slime/lower-arm';
import MountingResetIcon from '~icons/slime/mounting-reset';
import NeckIcon from '~icons/slime/neck';
import PawIcon from '~icons/slime/paw';
import RecordIcon from '~icons/slime/record';
import ShoulderIcon from '~icons/slime/shoulder';
import SlimeExtensionIcon from '~icons/slime/slime-extension';
import SlimeNormalIcon from '~icons/slime/slime-normal';
import SlimeUpIcon from '~icons/slime/slime-up';
import SlimevrIcon from '~icons/slime/slimevr';
import SlimevrFoxIcon from '~icons/slime/slimevr-fox';
import SlimevrLogoIcon from '~icons/slime/slimevr-logo';
import SlimevrLogoFoxIcon from '~icons/slime/slimevr-logo-fox';
import SlimevrLogoSnepIcon from '~icons/slime/slimevr-logo-snep';
import SlimevrSnepIcon from '~icons/slime/slimevr-snep';
import SquaresIcon from '~icons/slime/squares';
import SteamIcon from '~icons/slime/steam';
import ToesIcon from '~icons/slime/toes';
import UploadFolderIcon from '~icons/slime/upload-folder';
import UpperArmIcon from '~icons/slime/upper-arm';
import UpperChestIcon from '~icons/slime/upper-chest';
import UpperLegIcon from '~icons/slime/upper-leg';
import VmcIcon from '~icons/slime/vmc';
import VrcIcon from '~icons/slime/vrc';
import WaistIcon from '~icons/slime/waist';
import YawResetIcon from '~icons/slime/yaw-reset';

type IconEntry = [path: string, icon: Component<ComponentProps<'svg'>>];

const ICONS: IconEntry[] = [
  ['material-symbols/accessibility-new-rounded', AccessibilityNewIcon],
  ['material-symbols/airline-seat-recline-normal', AirlineSeatIcon],
  ['slime/ankle', AnkleIcon],
  ['material-symbols/arrow-back-rounded', ArrowBackIcon],
  ['heroicons/arrow-down-tray-solid', ArrowDownTrayIcon],
  ['material-symbols/arrow-forward-rounded', ArrowForwardIcon],
  ['heroicons/arrows-right-left', ArrowsRightLeftIcon],
  ['material-symbols/bar-chart-4-bars-rounded', BarChartIcon],
  ['ic/baseline-keyboard', KeyboardIcon],
  ['ic/baseline-keyboard-arrow-down', KeyboardArrowDownIcon],
  ['ic/baseline-keyboard-arrow-up', KeyboardArrowUpIcon],
  ['heroicons/bug-ant-solid', BugAntIcon],
  ['material-symbols/build-rounded', BuildIcon],
  ['slime/butterfly', ButterflyIcon],
  ['slime/camera-lock', CameraLockIcon],
  ['material-symbols/check-rounded', CheckIcon],
  ['material-symbols/checklist', ChecklistIcon],
  ['slime/chest', ChestIcon],
  ['material-symbols/close-rounded', CloseIcon],
  ['material-symbols/compare-arrows', CompareArrowsIcon],
  ['slime/controller', ControllerIcon],
  ['material-symbols/crop-square-outline-rounded', CropSquareIcon],
  ['material-symbols/deployed-code-rounded', DeployedCodeIcon],
  ['material-symbols/diagonal-line', DiagonalLineIcon],
  ['material-symbols/directions-walk', DirectionsWalkIcon],
  ['heroicons/document-arrow-up-solid', DocumentArrowUpIcon],
  ['heroicons/document-duplicate-solid', DocumentDuplicateIcon],
  ['heroicons/document-solid', DocumentIcon],
  ['material-symbols/downhill-skiing', DownhillSkiingIcon],
  ['heroicons/exclamation-triangle-20-solid', ExclamationTriangleIcon],
  ['heroicons/eye-slash-solid', EyeSlashIcon],
  ['heroicons/eye-solid', EyeIcon],
  ['slime/fingers', FingersIcon],
  ['heroicons/folder-solid', FolderIcon],
  ['slime/foot', FootIcon],
  ['slime/full-reset', FullResetIcon],
  ['material-symbols/head-mounted-device-rounded', HeadMountedDeviceIcon],
  ['slime/hip', HipIcon],
  ['material-symbols/home', HomeIcon],
  [
    'material-symbols/horizontal-align-center-rounded',
    HorizontalAlignCenterIcon,
  ],
  ['slime/layout', LayoutIcon],
  ['material-symbols/lightbulb-rounded', LightbulbIcon],
  ['slime/lower-arm', LowerArmIcon],
  ['heroicons/magnifying-glass-20-solid', MagnifyingGlassIcon],
  ['material-symbols/minimize-rounded', MinimizeIcon],
  ['slime/mounting-reset', MountingResetIcon],
  ['slime/neck', NeckIcon],
  ['material-symbols/pause-rounded', PauseIcon],
  ['slime/paw', PawIcon],
  ['heroicons/pencil-solid', PencilIcon],
  ['material-symbols/percent', PercentIcon],
  ['material-symbols/play-arrow-rounded', PlayArrowIcon],
  ['heroicons/play-circle-solid', PlayCircleIcon],
  ['heroicons/plus-solid', PlusIcon],
  ['material-symbols/question-mark', QuestionMarkIcon],
  ['heroicons/question-mark-circle', QuestionMarkCircleIcon],
  ['slime/record', RecordIcon],
  ['lucide/rotate-ccw', RotateCcwIcon],
  ['material-symbols/router-rounded', RouterIcon],
  ['material-symbols/settings-rounded', SettingsIcon],
  ['slime/shoulder', ShoulderIcon],
  ['material-symbols/sim-card-download', SimCardDownloadIcon],
  ['slime/slime-extension', SlimeExtensionIcon],
  ['slime/slime-normal', SlimeNormalIcon],
  ['slime/slime-up', SlimeUpIcon],
  ['slime/slimevr', SlimevrIcon],
  ['slime/slimevr-fox', SlimevrFoxIcon],
  ['slime/slimevr-logo', SlimevrLogoIcon],
  ['slime/slimevr-logo-fox', SlimevrLogoFoxIcon],
  ['slime/slimevr-logo-snep', SlimevrLogoSnepIcon],
  ['slime/slimevr-snep', SlimevrSnepIcon],
  ['material-symbols/sports-martial-arts-rounded', SportsMartialArtsIcon],
  ['slime/squares', SquaresIcon],
  ['slime/steam', SteamIcon],
  ['material-symbols/straighten-rounded', StraightenIcon],
  ['slime/toes', ToesIcon],
  ['heroicons/trash-solid', TrashIcon],
  ['material-symbols/tune', TuneIcon],
  ['slime/upload-folder', UploadFolderIcon],
  ['slime/upper-arm', UpperArmIcon],
  ['slime/upper-chest', UpperChestIcon],
  ['slime/upper-leg', UpperLegIcon],
  ['material-symbols/usb-rounded', UsbIcon],
  ['slime/vmc', VmcIcon],
  ['slime/vrc', VrcIcon],
  ['slime/waist', WaistIcon],
  ['slime/yaw-reset', YawResetIcon],
];

const SIZES = ['xs', 'sm', 'md', 'lg', 'xl'] as const;

const SIZE_CLASS: Record<(typeof SIZES)[number], string> = {
  xs: 'icon-xs',
  sm: 'icon-sm',
  md: 'icon-md',
  lg: 'icon-lg',
  xl: 'icon-xl',
};

export function IconGallery() {
  return (
    <div class="surface-page flex flex-col gap-lg rounded-lg p-lg">
      <span class="text-title">icons</span>
      <div class="flex flex-wrap items-end gap-lg">
        <For each={SIZES}>
          {(size) => (
            <div
              class={`${SIZE_CLASS[size]} flex flex-col items-center gap-xs`}
            >
              <SettingsIcon />
              <span>icon-{size}</span>
            </div>
          )}
        </For>
        <div class="flex flex-col items-center gap-xs">
          <span class="flex gap-xs">
            <FootIcon class="text-assign-left" />
            <FootIcon class="text-assign-right -scale-x-100" />
          </span>
          <span>colour + mirror</span>
        </div>
      </div>

      <div class="icon-lg grid grid-cols-[repeat(auto-fill,minmax(8rem,1fr))] gap-sm">
        <For each={ICONS}>
          {([path, icon]) => (
            <div
              class="
                bg-surface-control hover:bg-surface-hover flex min-w-0 flex-col items-center gap-xs
                rounded-md p-sm text-center
              "
              title={path}
            >
              <Dynamic component={icon} />
              <span class="w-full truncate">
                {path.slice(path.indexOf('/') + 1)}
              </span>
            </div>
          )}
        </For>
      </div>
    </div>
  );
}
