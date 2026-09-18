# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = 로딩 중...
websocket-connection_lost = 서버와의 연결이 끊어졌어요. 다시 연결하는 중...
websocket-connection_lost-desc = SlimeVR 서버에 오류가 발생한 것 같습니다. 로그를 확인하고 프로그램을 재시작해 주세요
websocket-timedout = 서버에 연결할 수 없습니다
websocket-timedout-desc = SlimeVR 서버에 오류가 발생했거나 연결 시간이 초과된 것 같습니다. 로그를 확인하고 프로그램을 재시작해 주세요
websocket-error-close = SlimeVR 종료
websocket-error-logs = 로그 폴더 열기

## Update notification

version_update-title = 새로운 버전 발견: { $version }
version_update-description = "{ version_update-update }"를 눌러 설치 프로그램을 다운로드하세요.
version_update-update = 업데이트
version_update-close = 닫기

## Tips

tips-find_tracker = 어떤 트래커가 어디에 대응되는지 모르겠나요? 트래커를 흔들면 해당 트래커에 해당되는 항목이 강조 표시돼요.
tips-do_not_move_heels = 기록하는 동안 발뒤꿈치가 움직이지 않도록 조심하세요!
tips-file_select = 파일을 <u>열거나</u>, 여기에 드래그&드롭하세요.
tips-failed_webgl = WebGL 초기화에 실패했습니다.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = 닫기

## Body parts

body_part-NONE = 할당되지 않음
body_part-HEAD = 머리
body_part-NECK = 목
body_part-RIGHT_SHOULDER = 오른쪽 어깨
body_part-RIGHT_UPPER_ARM = 오른쪽 팔 위
body_part-RIGHT_LOWER_ARM = 오른쪽 팔 아래
body_part-RIGHT_HAND = 오른손
body_part-RIGHT_UPPER_LEG = 오른쪽 다리 위
body_part-RIGHT_LOWER_LEG = 오른쪽 다리 아래
body_part-RIGHT_FOOT = 오른발
body_part-UPPER_CHEST = 가슴 위
body_part-HIP = 골반
body_part-LEFT_SHOULDER = 오른쪽 어깨
body_part-LEFT_UPPER_ARM = 왼쪽 팔 위
body_part-LEFT_LOWER_ARM = 왼쪽 팔 아래
body_part-LEFT_HAND = 왼손
body_part-LEFT_UPPER_LEG = 왼쪽 다리 위
body_part-LEFT_LOWER_LEG = 왼쪽 다리 아래
body_part-LEFT_FOOT = 왼발

## BoardType

board_type-UNKNOWN = 알 수 없음
board_type-CUSTOM = 커스텀 보드

## Proportions

skeleton_bone-NONE = 없음
skeleton_bone-HEAD = 머리 밀림
skeleton_bone-NECK = 목 길이
skeleton_bone-torso_group = 몸통 길이
skeleton_bone-UPPER_CHEST = 가슴 위쪽 길이
skeleton_bone-HIP = 골반 길이
skeleton_bone-HIPS_WIDTH = 골반 너비
skeleton_bone-leg_group = 다리 길이
skeleton_bone-UPPER_LEG = 위쪽 다리 길이
skeleton_bone-LOWER_LEG = 아래쪽 다리 길이
skeleton_bone-FOOT_LENGTH = 발 크기
skeleton_bone-FOOT_SHIFT = 발 밀림
skeleton_bone-SHOULDERS_DISTANCE = 어깨 거리
skeleton_bone-SHOULDERS_WIDTH = 어깨 너비
skeleton_bone-arm_group = 팔 길이
skeleton_bone-UPPER_ARM = 위쪽 팔 거리
skeleton_bone-LOWER_ARM = 아래쪽 팔 길이
skeleton_bone-HAND_Y = 손 길이 Y
skeleton_bone-HAND_Z = 손 길이 Z

## Tracker reset buttons

reset-reset_all = 모든 신체 비율 초기화
reset-reset_all_warning-reset = 신체 비율 초기화
reset-reset_all_warning-cancel = 취소
reset-full = 전체 정렬
reset-mounting = 착용 방향 정렬
reset-yaw = Yaw 정렬

## Navigation bar

navbar-home = 홈
navbar-body_proportions = 신체 비율
navbar-trackers_assign = 트래커 위치
navbar-mounting = 착용 방향 정렬
navbar-onboarding = 설정 마법사
navbar-settings = 설정

## Biovision hierarchy recording

bvh-start_recording = BVH 기록
bvh-recording = 기록중...

## Tracking pause

tracking-unpaused = 트래킹 일시 중지
tracking-paused = 트래킹 재개

## Widget: Developer settings

widget-developer_mode = 개발자 모드
widget-developer_mode-high_contrast = 고대비
widget-developer_mode-precise_rotation = 회전 자세히 보기
widget-developer_mode-fast_data_feed = 빠른 데이터 피드
widget-developer_mode-raw_slime_rotation = Raw

## Widget: IMU Visualizer

widget-imu_visualizer = 회전
widget-imu_visualizer-preview = 미리보기
widget-imu_visualizer-hide = 숨기기
widget-imu_visualizer-rotation_raw = Raw
widget-imu_visualizer-rotation_preview = 미리보기
widget-imu_visualizer-acceleration = 가속도
widget-imu_visualizer-position = 위치

## Tracker status

tracker-status-none = 알 수 없음
tracker-status-busy = 바쁨
tracker-status-error = 오류
tracker-status-disconnected = 연결되지 않음
tracker-status-occluded = 사용할 수 없음
tracker-status-ok = 연결됨
tracker-status-timed_out = 시간 초과

## Tracker status columns

tracker-table-column-name = 이름
tracker-table-column-type = 타입
tracker-table-column-battery = 배터리
tracker-table-column-temperature = 온도 °C
tracker-table-column-linear-acceleration = X/Y/Z 가속도
tracker-table-column-rotation = X/Y/Z 회전
tracker-table-column-position = X/Y/Z 위치

## Tracker rotation

tracker-rotation-front = 앞쪽
tracker-rotation-front_left = 왼쪽 앞
tracker-rotation-front_right = 오른쪽 앞
tracker-rotation-left = 왼쪽
tracker-rotation-right = 오른쪽
tracker-rotation-back = 뒤쪽
tracker-rotation-back_left = 왼쪽 뒤
tracker-rotation-back_right = 오른쪽 뒤
tracker-rotation-custom = 사용자 지정

## Tracker information

tracker-infos-manufacturer = 제조사
tracker-infos-display_name = 표시되는 이름
tracker-infos-custom_name = 사용자 정의 이름
tracker-infos-url = 트래커 URL
tracker-infos-hardware_identifier = 하드웨어 ID
tracker-infos-imu = IMU 센서
tracker-infos-board_type = 메인보드
tracker-infos-network_version = 프로토콜 버전
tracker-infos-magnetometer = 자력계
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] 비활성화됨
        [ENABLED] 활성화됨
       *[NOT_SUPPORTED] 지원되지 않음
    }

## Tracker settings

tracker-settings-back = 트래커 목록으로 돌아가기
tracker-settings-title = 트래커 설정
tracker-settings-assignment_section = 트래커 위치 지정
tracker-settings-assignment_section-description = 트래커가 위치한 신체 부위
tracker-settings-assignment_section-edit = 위치 수정
tracker-settings-mounting_section = 착용 방향
tracker-settings-mounting_section-description = 트래커는 어디에 착용하나요?
tracker-settings-mounting_section-edit = 방향 수정
tracker-settings-use_mag = 이 트래커에서 자력계 활성화하기
# Multiline!
tracker-settings-use_mag-description =
    이 트래커는 자력계 사용이 허용될 때 드리프트를 줄이기 위해 자력계를 사용해야 합니까? <b>이것을 토글하는 동안 트래커를 종료하지 마십시오!</b>
    
    먼저 자력계를 사용하도록 설정한 다음, <magSetting>여기를 클릭하여 설정으로 이동하세요</magSetting>.
tracker-settings-use_mag-label = 자력계 활성화
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 트래커 이름
tracker-settings-name_section-label = 트래커 이름
tracker-settings-forget = 트래커 삭제
tracker-settings-forget-description = SlimeVR 서버에서 트래커를 제거하고 서버를 다시 시작할 때까지 자동으로 연결하지 않아요. 트래커의 설정은 지워지지 않아요.
tracker-settings-forget-label = 트래커 삭제
tracker-settings-update-up_to_date = 최신 버전
tracker-settings-update = 지금 업데이트
tracker-settings-update-title = 펌웨어 버전

## Dongle settings

dongle-infos-hardware_revision = 하드웨어 리비전
dongle-status-disconnected = 연결되지 않음
dongle-settings-back = 트래커 목록으로 돌아가기
dongle-settings-update = 지금 업데이트
dongle-settings-update-title = 펌웨어 버전

## Tracker part card info

tracker-part_card-unassigned = 할당되지 않음

## Body assignment menu

body_assignment_menu = 트래커가 어디에 있나요?
body_assignment_menu-description = 이 트래커를 할당할 위치를 선택하세요. 또는, 모든 트래커를 한 번에 설정할 수도 있어요.
body_assignment_menu-manage_trackers = 모든 트래커 설정
body_assignment_menu-unassign_tracker = 할당하지 않기

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>경고:</b> 목 트래커는 너무 세게 조정하면 위험해요.
    스트랩이 머리의 혈액 순환을 방해할 수 있어요!
tracker_selection_menu-neck_warning-done = 위험성을 이해했어요.
tracker_selection_menu-neck_warning-cancel = 취소

## Mounting menu

mounting_selection_menu-close = 닫기

## Sidebar settings

settings-sidebar-title = 설정
settings-sidebar-general = 일반
settings-sidebar-trackers = 트래커
settings-sidebar-interface = 인터페이스
settings-sidebar-utils = 유틸리티
settings-sidebar-appearance = 모양
settings-sidebar-notifications = 알림
settings-sidebar-firmware-tool = DIY 펌웨어 도구
settings-sidebar-advanced = 고급

## Bone routing settings

settings-routing-hands-warning-cancel = 취소

## SteamVR / Monado output settings

settings-driver-enable = 활성화

## Tracker mechanics

settings-general-tracker_mechanics-filtering = 필터링
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    트래커의 필터링 유형을 선택하세요.
    Prediction은 움직임을 예측하고 Smoothing은 움직임을 부드럽게 해요.
settings-general-tracker_mechanics-filtering-type-none = 필터링 없음
settings-general-tracker_mechanics-filtering-type-none-description = 어떠한 필터링도 사용하지 않아요. 있는 그대로의 회전 값을 사용해요.
settings-general-tracker_mechanics-filtering-type-smoothing-description = 움직임을 부드럽게 하지만 약간의 대기 시간이 추가돼요.
settings-general-tracker_mechanics-filtering-type-prediction-description = 대기 시간이 줄어들고 움직임이 더 빨라지지만 지터가 증가할 수 있어요.
settings-general-tracker_mechanics-save_mounting_reset = 자동 착용 방향 정렬 보정값 저장
settings-general-tracker_mechanics-save_mounting_reset-description =
    트래커의 착용 방향 정렬 보정값을 저장합니다. 트래커들의 위치가 고정된
    모션 캡처 슈트 같은 것을 사용할 때 유용해요. <b>일반 사용자들에게는 권장되지 않아요!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = 착용 방향 정렬 저장
settings-general-tracker_mechanics-use_mag_on_all_trackers = 자력계를 지원하는 모든 IMU 트래커에서 자력계 활성화
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    호환 가능한 펌웨어가 있는 모든 트래커에서 자력계를 사용하여 안정적인 자기 환경에서 틀어짐을 줄일 수 있어요.
    트래커의 설정에서 트래커별로 비활성화할 수 있어요. <b>이 기능을 토글하는 동안 트래커를 종료하지 마세요!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = 트래커에서 자력계 사용하기
settings-stay_aligned-general-label = 일반
settings-stay_aligned-relaxed_poses-close = 닫기

## Keybinds Page

settings-keybinds_full-reset = 전체 정렬
settings-keybinds_yaw-reset = Yaw 정렬
settings-keybinds_reset-all-button = 모든 설정 초기화
settings-keybinds-recorder-modal-cancel-button = 취소

## FK/Tracking settings

# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = 플로어 클립
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = 스케이팅 보정
settings-general-fk_settings-leg_tweak-toe_snap = 토 스냅
settings-general-fk_settings-leg_tweak-foot_plant = 풋 플랜트
settings-general-fk_settings-leg_tweak-skating_correction-amount = 스케이팅 보정 강도
settings-general-fk_settings-leg_tweak-skating_correction-description = 스케이트 보정은 바닥에서 미끄러지는 문제를 보정해주지만 특정 움직임 패턴에서 정확도가 낮아질 수도 있어요. 이 기능을 활성화한다면 게임에서 다시 전체 정렬 및 캘리브레이션을 해야 해요.
settings-general-fk_settings-leg_tweak-floor_clip-description = 플로어 클립은 바닥을 통한 클리핑을 줄이거나 없앨 수 있어요.  이 기능을 활성화한다면 게임에서 다시 전체 정렬 및 캘리브레이션을 해야 해요.
settings-general-fk_settings-leg_tweak-toe_snap-description = 토 스냅은 발 트래커가 없을 때, 발 트래커가 있는 것처럼 예측해서 움직여주는 기능이에요.
settings-general-fk_settings-leg_tweak-foot_plant-description = 풋 플랜트는 발이 바닥에 닿았을 때 바닥과 평평하게 회전시켜 줘요.
settings-general-fk_settings-leg_fk = 발 트래킹
settings-general-fk_settings-enforce_joint_constraints = 골격 한계
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = 상수 강제 적용
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = 관절의 회전 각도를 제한합니다
settings-general-fk_settings-arm_fk-back = 뒤쪽
settings-general-fk_settings-arm_fk-back-description = 기본값. 위쪽 팔은 뒤를 향하고 아래쪽 팔은 앞을 향하게 하는 자세.
settings-general-fk_settings-arm_fk-tpose_up = T-포즈(올리기)
settings-general-fk_settings-arm_fk-tpose_up-description = 전체 정렬을 수행할 때에는 팔을 차렷 자세로 내리고, 착용 방향 정렬 중에는 팔을 좌우로 나란히 동작으로 펼치는 자세.
settings-general-fk_settings-arm_fk-tpose_down = T-포즈(내리기)
settings-general-fk_settings-arm_fk-tpose_down-description = 전체 정렬에서는 좌우로 나란히 자세, 착용 방향 정렬에서는 팔을 차렷 동작으로 내리는 자세
settings-general-fk_settings-arm_fk-forward = 앞쪽
settings-general-fk_settings-arm_fk-forward-description = 앞으로 나란히 자세. 앉아있거나 버튜버 활동 등에서 유용해요.
settings-general-fk_settings-skeleton_settings-ratios = 골격 비율
settings-general-fk_settings-skeleton_settings-ratios-description = 골격 비율을 변경하면 신체 비율 설정을 다시 조절해야 할 수 있어요.
settings-general-fk_settings-self_localization-title = Mocap 모드

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = 탭해서 정렬하기
settings-general-gesture_control-description = 트래커를 탭하면 쉽게 트래커를 정렬할 수 있어요. 몸통에서 가장 높은 트래커는 Yaw 정렬에 사용되고 왼쪽 다리에서 가장 높은 트래커는 전체 정렬에 사용되며, 오른쪽 다리에서 가장 높은 트래커는 착용 방향 정렬에 사용돼요. 탭할 때의 간격은 0.6초 이내여야 해요.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
       *[other] { $amount } 탭
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers = { $amount } 트래커
settings-general-gesture_control-yawResetEnabled = 탭해서 Yaw 정렬 활성화
settings-general-gesture_control-yawResetDelay = Yaw 정렬 딜레이
settings-general-gesture_control-yawResetTaps = Yaw 정렬 탭 횟수
settings-general-gesture_control-fullResetEnabled = 탭해서 전체 정렬 활성화
settings-general-gesture_control-fullResetDelay = 전체 정렬 딜레이
settings-general-gesture_control-fullResetTaps = 탭해서 전체 정렬
settings-general-gesture_control-mountingResetEnabled = 탭해서 착용 방향 정렬 활성화
settings-general-gesture_control-mountingResetDelay = 착용 방향 정렬 딜레이
settings-general-gesture_control-mountingResetTaps = 탭해서 착용 방향 정렬
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = 트래커 감지 한계
settings-general-gesture_control-numberTrackersOverThreshold-description = 몸을 움직이면서 제스처 제어를 하고 싶은데 잘 작동하지 않는다면 이 값을 늘리세요. 탭 탐지가 작동하는 데 필요한 값 이상으로 늘리지 마세요. 더 많은 오작동이 발생할 수 있어요.

## Appearance settings

settings-interface-appearance = 모양
settings-general-interface-dev_mode = 개발자 모드
settings-general-interface-dev_mode-description = 이 모드는 더 많은 데이터가 필요하거나 고급 수준에서 연결된 트래커와 상호 작용하는 경우에 유용할 수 있어요.
settings-general-interface-dev_mode-label = 개발자 모드
settings-general-interface-lang = 언어 선택
settings-general-interface-lang-description = 사용하고 싶은 기본 언어를 선택하세요.
settings-general-interface-lang-placeholder = 사용할 언어를 선택하세요
# Keep the font name untranslated
settings-interface-appearance-font = GUI 글꼴
settings-interface-appearance-font-description = 이렇게 하면 인터페이스에서 사용하는 글꼴이 변경돼요.
settings-interface-appearance-font-placeholder = 기본 글꼴
settings-interface-appearance-font-os_font = OS 글꼴
settings-interface-appearance-font-slime_font = 기본 글꼴
settings-interface-appearance-font_size = 글꼴 크기 조정
settings-interface-appearance-font_size-description = 변경하면 이 설정 패널을 제외하고 모든 인터페이스의 글꼴 크기가 달라져요.

## Notification settings

settings-interface-notifications = 알림
settings-general-interface-feedback_sound = 피드백 사운드
settings-general-interface-feedback_sound-description = 이 옵션을 켜면 트래커를 정렬할 때 효과음을 재생해요
settings-general-interface-feedback_sound-label = 피드백 사운드
settings-general-interface-feedback_sound-volume = 피드백 사운드 음량
settings-general-interface-connected_trackers_warning = 작동 중인 트래커 경고
settings-general-interface-connected_trackers_warning-description = 이 옵션은 트래커가 연결되어 있는 채로 SlimeVR을 종료하려고 할 때 팝업을 표시합니다. 그렇게 하면 실수로 트래커를 끄지 않아 배터리가 방전되는 일을 예방할 수 있어요.
settings-general-interface-connected_trackers_warning-label = 종료 시 작동 중인 트래커 경고 활성화

## Behavior settings

settings-general-interface-dev_mode = 개발자 모드
settings-general-interface-dev_mode-label = 개발자 모드
settings-general-interface-use_tray = 작업 표시줄로 최소화
settings-general-interface-use_tray-description = SlimeVR 서버를 닫지 않고 창만 닫을 수 있게 하여 사용 시 항상 GUI를 띄워 놓을 필요가 없게 해요.
settings-general-interface-use_tray-label = 작업 표시줄로 최소화
settings-general-interface-discord_presence = DIscord에서 활동 공유
settings-general-interface-discord_presence-description = Discord 활동 상태에 SlimeVR을 사용 중이라는 것과 사용 중인 트래커의 개수를 같이 표시합니다.
settings-general-interface-discord_presence-label = DIscord에서 활동 공유
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] 슬라임 갖고 노는 중
       *[other] 트래커 { $amount } 개 사용 중
    }
settings-interface-behavior-error_tracking = Sentry.io 를 통한 오류 수집
settings-interface-behavior-error_tracking-label = 개발자에게 오류 보내기

## Serial settings

settings-serial-connection_lost = 시리얼 연결 끊김, 다시 연결 중...
settings-serial-reboot = 재부팅
settings-serial-factory_reset = 공장 초기화
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>주의:</b> 트래커의 모든 설정이 초기화돼요.
    계속하면 Wi-Fi와 캘리브레이션 정보도 <b>모두 삭제됩니다!</b>
settings-serial-factory_reset-warning-ok = 네! 알고 있어요.
settings-serial-factory_reset-warning-cancel = 취소
settings-serial-serial_select = 시리얼 포트 선택
settings-serial-auto_dropdown_item = 자동
settings-serial-save_logs = 파일에 저장
settings-serial-send_command-warning-ok = 네! 알고 있어요.
settings-serial-send_command-warning-cancel = 취소

## OSC VRChat settings

settings-osc-vrchat-enable = 활성화
settings-osc-vrchat-enable-description = 활성화해서 데이터 송수신
settings-osc-vrchat-enable-label = 활성화
settings-osc-vrchat-network = 네트워크 포트
settings-osc-vrchat-network-port_in =
    .label = 들어오는 포트
    .placeholder = Port in (기본값: 9001)
settings-osc-vrchat-network-port_out =
    .label = 나가는 포트
    .placeholder = Port out (기본값: 9000)
settings-osc-vrchat-network-address = 네트워크 주소
settings-osc-vrchat-network-address-description-v1 = OSC 데이터를 보낼 주소, VRChat에서 사용하려면 그냥 두세요.
settings-osc-vrchat-network-address-placeholder = VRChat IP 주소

## VRChat OSC status

settings-osc-vrchat-status-tracking = 회전
settings-osc-vrchat-status-badge-error = 오류
settings-osc-vrchat-status-badge-unknown = 알 수 없음

## VMC OSC settings

# This cares about multilines
settings-osc-vmc-description =
    SlimeVR의 골격 데이터를 보내고 다른 앱에서 이 데이터를 수신하기 위해
    VMC(Virtual Motion Capture) 프로토콜 설정을 변경합니다.
settings-osc-vmc-enable = 활성화
settings-osc-vmc-enable-description = 활성화해서 데이터 송수신
settings-osc-vmc-enable-label = 활성화
settings-osc-vmc-network = 네트워크 포트
settings-osc-vmc-network-description = VMC와 데이터를 송수신할 포트 설정

## Common OSC settings


## Advanced settings


## Home Screen


## Tracking Checklist


## Setup/onboarding menu


## Quiz


## Wi-Fi setup


## Install info


## Setup start


## Tracker connection setup


## Tracker assignment setup


## Tracker assignment warnings


## Tracker mounting method choose


## Tracker manual mounting setup


## Tracker automatic mounting setup


## Tracker manual proportions setupa


## Tracker automatic proportions setup


## User height calibration


## Stay Aligned setup


## Home


## Trackers Still On notification


## Firmware tool globals


## Firmware tool Steps


## firmware tool build status


## Firmware update status


## Dedicated Firmware Update Page


## Tray Menu


## First exit modal


## Unknown device modal


## Error collection consent modal


## Tracking checklist section

