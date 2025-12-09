# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = 서버에 연결하는 중...
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
tips-tap_setup = 목록에서 트래커를 선택하는 대신 트래커를 천천히 2번 탭해서 선택할 수 있어요.
tips-turn_on_tracker = 공식 SlimeVR 트래커를 사용 중이신가요? 트래커를 <b><em>PC에 연결</em></b>하고 <b><em>전원을 키셔야</em></b> 해요.
tips-failed_webgl = WebGL 초기화에 실패했습니다.

## Units


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
body_part-CHEST = 가슴
body_part-WAIST = 허리
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
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = 커스텀 보드
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-LOLIN_C3_MINI = Lolin C3 Mini
board_type-BEETLE32C3 = Beetle ESP32-C3
board_type-ESP32C3DEVKITM1 = Espressif ESP32-C3 DevKitM-1
board_type-OWOTRACK = owoTrack
board_type-WRANGLER = Wrangler Joycons
board_type-MOCOPI = Sony Mocopi
board_type-WEMOSWROOM02 = Wemos Wroom-02 D1 Mini
board_type-XIAO_ESP32C3 = Seeed Studio XIAO ESP32C3
board_type-HARITORA = Haritora
board_type-ESP32C6DEVKITC1 = Espressif ESP32-C6 DevKitC-1
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev IMU Glove

## Proportions

skeleton_bone-NONE = 없음
skeleton_bone-HEAD = 머리 밀림
skeleton_bone-NECK = 목 길이
skeleton_bone-torso_group = 몸통 길이
skeleton_bone-UPPER_CHEST = 가슴 위쪽 길이
skeleton_bone-CHEST_OFFSET = 가슴 오프셋
skeleton_bone-CHEST = 가슴 길이
skeleton_bone-WAIST = 허리 길이
skeleton_bone-HIP = 골반 길이
skeleton_bone-HIP_OFFSET = 골반 오프셋
skeleton_bone-HIPS_WIDTH = 골반 너비
skeleton_bone-leg_group = 다리 길이
skeleton_bone-UPPER_LEG = 위쪽 다리 길이
skeleton_bone-LOWER_LEG = 아래쪽 다리 길이
skeleton_bone-FOOT_LENGTH = 발 크기
skeleton_bone-FOOT_SHIFT = 발 밀림
skeleton_bone-SKELETON_OFFSET = 골격 오프셋
skeleton_bone-SHOULDERS_DISTANCE = 어깨 거리
skeleton_bone-SHOULDERS_WIDTH = 어깨 너비
skeleton_bone-arm_group = 팔 길이
skeleton_bone-UPPER_ARM = 위쪽 팔 거리
skeleton_bone-LOWER_ARM = 아래쪽 팔 길이
skeleton_bone-HAND_Y = 손 길이 Y
skeleton_bone-HAND_Z = 손 길이 Z
skeleton_bone-ELBOW_OFFSET = 팔꿈치 오프셋

## Tracker reset buttons

reset-reset_all = 모든 신체 비율 초기화
reset-reset_all_warning-v2 =
    <b>경고:</b> 기존에 설정한 신체 비율이 키에 비례하여 재설정됩니다.
    그래도 진행하시겠습니까?
reset-reset_all_warning-reset = 신체 비율 초기화
reset-reset_all_warning-cancel = 취소
reset-reset_all_warning_default-v2 =
    <b>경고:</b> 사용자의 키가 설정되지 않았으므로 키를 비롯한 신체 비율이 기본값으로 재설정됩니다.
    그래도 진행하시겠습니까?
reset-full = 전체 정렬
reset-mounting = 착용 방향 정렬
reset-yaw = Yaw 정렬

## Serial detection stuff

serial_detection-new_device-p0 = 새로운 시리얼 디바이스 감지됨!
serial_detection-new_device-p1 = Wi-Fi 자격 증명을 입력해주세요!
serial_detection-new_device-p2 = 원하는 작업을 선택하세요
serial_detection-open_wifi = Wi-Fi 연결
serial_detection-open_serial = 시리얼 콘솔 열기
serial_detection-submit = 저장!
serial_detection-close = 닫기

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

## Widget: Overlay settings

widget-overlay = 오버레이
widget-overlay-is_visible_label = SteamVR에서 오버레이 표시
widget-overlay-is_mirrored_label = 오버레이 반전

## Widget: Drift compensation

widget-drift_compensation-clear = 틀어짐 보정 초기화

## Widget: Clear Mounting calibration

widget-clear_mounting = 착용 방향 정렬 초기화

## Widget: Developer settings

widget-developer_mode = 개발자 모드
widget-developer_mode-high_contrast = 고대비
widget-developer_mode-precise_rotation = 회전 자세히 보기
widget-developer_mode-fast_data_feed = 빠른 데이터 피드
widget-developer_mode-filter_slimes_and_hmd = 트래커와 VR 헤드셋만 보기
widget-developer_mode-sort_by_name = 이름순으로 정렬
widget-developer_mode-raw_slime_rotation = 원시 회전값 보기
widget-developer_mode-more_info = 더 많은 정보 보기

## Widget: IMU Visualizer

widget-imu_visualizer = 회전
widget-imu_visualizer-preview = 미리보기
widget-imu_visualizer-hide = 숨기기
widget-imu_visualizer-rotation_raw = Raw
widget-imu_visualizer-rotation_preview = 미리보기
widget-imu_visualizer-acceleration = 가속도
widget-imu_visualizer-position = 위치

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = 골격 비율 미리보기
widget-skeleton_visualizer-hide = 숨기기

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
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = 온도 °C
tracker-table-column-linear-acceleration = X/Y/Z 가속도
tracker-table-column-rotation = X/Y/Z 회전
tracker-table-column-position = X/Y/Z 위치
tracker-table-column-url = URL

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
tracker-rotation-overriden = (착용 방향 보정으로 재정의됨)

## Tracker information

tracker-infos-manufacturer = 제조사
tracker-infos-display_name = 표시되는 이름
tracker-infos-custom_name = 사용자 정의 이름
tracker-infos-url = 트래커 URL
tracker-infos-version = 펌웨어 버전
tracker-infos-hardware_rev = 하드웨어 리비전
tracker-infos-hardware_identifier = 하드웨어 ID
tracker-infos-data_support = 데이터 지원
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
tracker-settings-drift_compensation_section = 틀어짐 보정 사용
tracker-settings-drift_compensation_section-description = 틀어짐 보정이 켜져 있을 때 이 트래커의 틀어짐을 보정할까요?
tracker-settings-drift_compensation_section-edit = 틀어짐 보정 사용
tracker-settings-use_mag = 이 트래커에서 자력계 활성화하기
# Multiline!
tracker-settings-use_mag-description =
    이 트래커는 자력계 사용이 허용될 때 드리프트를 줄이기 위해 자력계를 사용해야 합니까? <b>이것을 토글하는 동안 트래커를 종료하지 마십시오!</b>
    
    먼저 자력계를 사용하도록 설정한 다음, <magSetting>여기를 클릭하여 설정으로 이동하세요</magSetting>.
tracker-settings-use_mag-label = 자력계 활성화
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 트래커 이름
tracker-settings-name_section-description = 귀여운 이름을 지어주세요! >_<
tracker-settings-name_section-placeholder = NightyBeast's left leg
tracker-settings-name_section-label = 트래커 이름
tracker-settings-forget = 트래커 삭제
tracker-settings-forget-description = SlimeVR 서버에서 트래커를 제거하고 서버를 다시 시작할 때까지 자동으로 연결하지 않아요. 트래커의 설정은 지워지지 않아요.
tracker-settings-forget-label = 트래커 삭제
tracker-settings-update-up_to_date = 최신 버전
tracker-settings-update = 지금 업데이트
tracker-settings-update-title = 펌웨어 버전

## Tracker part card info

tracker-part_card-no_name = 이름 없음
tracker-part_card-unassigned = 할당되지 않음

## Body assignment menu

body_assignment_menu = 트래커가 어디에 있나요?
body_assignment_menu-description = 이 트래커를 할당할 위치를 선택하세요. 또는, 모든 트래커를 한 번에 설정할 수도 있어요.
body_assignment_menu-show_advanced_locations = 고급 할당 위치 표시
body_assignment_menu-manage_trackers = 모든 트래커 설정
body_assignment_menu-unassign_tracker = 할당하지 않기

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = 에 어떤 트래커를 할당할까요?
tracker_selection_menu-NONE = 어떤 트래커를 할당 취소할까요?
tracker_selection_menu-HEAD = 머리{ -tracker_selection-part }
tracker_selection_menu-NECK = 목{ -tracker_selection-part }
tracker_selection_menu-RIGHT_SHOULDER = 오른쪽 어께{ -tracker_selection-part }
tracker_selection_menu-RIGHT_UPPER_ARM = 오른쪽 팔 위{ -tracker_selection-part }
tracker_selection_menu-RIGHT_LOWER_ARM = 오른쪽 팔 아래{ -tracker_selection-part }
tracker_selection_menu-RIGHT_HAND = 오른손{ -tracker_selection-part }
tracker_selection_menu-RIGHT_UPPER_LEG = 오른쪽 다리 위{ -tracker_selection-part }
tracker_selection_menu-RIGHT_LOWER_LEG = 오른쪽 다리 아래{ -tracker_selection-part }
tracker_selection_menu-RIGHT_FOOT = 오른쪽 발{ -tracker_selection-part }
tracker_selection_menu-RIGHT_CONTROLLER = 오른쪽 컨트롤러{ -tracker_selection-part }
tracker_selection_menu-UPPER_CHEST = 가슴 위{ -tracker_selection-part }
tracker_selection_menu-CHEST = 가슴{ -tracker_selection-part }
tracker_selection_menu-WAIST = 허리{ -tracker_selection-part }
tracker_selection_menu-HIP = 골반{ -tracker_selection-part }
tracker_selection_menu-LEFT_SHOULDER = 왼쪽 어께{ -tracker_selection-part }
tracker_selection_menu-LEFT_UPPER_ARM = 왼쪽 팔 위{ -tracker_selection-part }
tracker_selection_menu-LEFT_LOWER_ARM = 왼쪽 팔 아래{ -tracker_selection-part }
tracker_selection_menu-LEFT_HAND = 왼손{ -tracker_selection-part }
tracker_selection_menu-LEFT_UPPER_LEG = 왼쪽 다리 위{ -tracker_selection-part }
tracker_selection_menu-LEFT_LOWER_LEG = 왼쪽 다리 아래{ -tracker_selection-part }
tracker_selection_menu-LEFT_FOOT = 왼쪽 발{ -tracker_selection-part }
tracker_selection_menu-LEFT_CONTROLLER = 왼쪽 컨트롤러{ -tracker_selection-part }
tracker_selection_menu-unassigned = 할당되지 않은 트래커
tracker_selection_menu-assigned = 할당된 트래커
tracker_selection_menu-dont_assign = 할당하지 않기
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>경고:</b> 목 트래커는 너무 세게 조정하면 위험해요.
    스트랩이 머리의 혈액 순환을 방해할 수 있어요!
tracker_selection_menu-neck_warning-done = 위험성을 이해했어요.
tracker_selection_menu-neck_warning-cancel = 취소

## Mounting menu

mounting_selection_menu = 트래커가 어디에 있나요?
mounting_selection_menu-close = 닫기

## Sidebar settings

settings-sidebar-title = 설정
settings-sidebar-general = 일반
settings-sidebar-tracker_mechanics = 트래커 역학
settings-sidebar-fk_settings = 트래킹 설정
settings-sidebar-gesture_control = 제스처 제어
settings-sidebar-interface = 인터페이스
settings-sidebar-osc_router = OSC 라우터
settings-sidebar-osc_trackers = VRChat OSC 트래커
settings-sidebar-utils = 유틸리티
settings-sidebar-serial = 시리얼 콘솔
settings-sidebar-appearance = 모양
settings-sidebar-notifications = 알림
settings-sidebar-firmware-tool = DIY 펌웨어 도구
settings-sidebar-advanced = 고급

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR 트래커
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    SteamVR 트래커를 켜거나 끄세요
    특정 트래커 구성만 허용하는 게임 또는 앱에서 유용해요.
settings-general-steamvr-trackers-waist = 허리
settings-general-steamvr-trackers-chest = 가슴
settings-general-steamvr-trackers-left_foot = 왼발
settings-general-steamvr-trackers-right_foot = 오른발
settings-general-steamvr-trackers-left_knee = 왼쪽 무릎
settings-general-steamvr-trackers-right_knee = 오른쪽 무릎
settings-general-steamvr-trackers-left_elbow = 왼쪽 팔꿈치
settings-general-steamvr-trackers-right_elbow = 오른쪽 팔꿈치
settings-general-steamvr-trackers-left_hand = 왼손
settings-general-steamvr-trackers-right_hand = 오른손
settings-general-steamvr-trackers-tracker_toggling = 자동 트래커 할당
settings-general-steamvr-trackers-tracker_toggling-description = 지정한 트래커 할당 상태에 따라 SteamVR 트래커를 자동으로 켜고 끄기
settings-general-steamvr-trackers-tracker_toggling-label = 자동 트래커 할당
settings-general-steamvr-trackers-hands-warning =
    <b>경고:</b> 핸드 트래커를 사용하면 VR 컨트롤러가 작동하지 않아요.
    그래도 사용할까요?
settings-general-steamvr-trackers-hands-warning-cancel = 취소
settings-general-steamvr-trackers-hands-warning-done = 확인

## Tracker mechanics

settings-general-tracker_mechanics = 트래커 역학
settings-general-tracker_mechanics-filtering = 필터링
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    트래커의 필터링 유형을 선택하세요.
    Prediction은 움직임을 예측하고 Smoothing은 움직임을 부드럽게 해요.
settings-general-tracker_mechanics-filtering-type = 필터링 타입
settings-general-tracker_mechanics-filtering-type-none = 필터링 없음
settings-general-tracker_mechanics-filtering-type-none-description = 어떠한 필터링도 사용하지 않아요. 있는 그대로의 회전 값을 사용해요.
settings-general-tracker_mechanics-filtering-type-smoothing = Smoothing
settings-general-tracker_mechanics-filtering-type-smoothing-description = 움직임을 부드럽게 하지만 약간의 대기 시간이 추가돼요.
settings-general-tracker_mechanics-filtering-type-prediction = Prediction
settings-general-tracker_mechanics-filtering-type-prediction-description = 대기 시간이 줄어들고 움직임이 더 빨라지지만 지터가 증가할 수 있어요.
settings-general-tracker_mechanics-filtering-amount = 강도
settings-general-tracker_mechanics-yaw-reset-smooth-time = Yaw 정렬할 때 부드럽게 움직이는 시간 (비활성화: 0초)
settings-general-tracker_mechanics-drift_compensation = 틀어짐 보정
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    틀어지는 방향의 반대 방향으로 회전해서 IMU Yaw 드리프트를 보정할 수 있어요.
    보정하는 강도와 감지할 최근 정렬 횟수를 설정할 수 있어요.
settings-general-tracker_mechanics-drift_compensation-enabled-label = 틀어짐 보정
settings-general-tracker_mechanics-drift_compensation-prediction = 틀어짐 보정 예측
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    점점 심하게 틀어지는 트래커의 틀어짐 방향을 예측해요.
    틀어짐 보정을 사용해도 트래커가 Yaw 축에서 계속 틀어지면 이 옵션을 켜세요.
settings-general-tracker_mechanics-drift_compensation-prediction-label = 예측해서 틀어짐 보정하기
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>경고:</b> 트래커를 지나치게 자주 정렬해야 하는 경우에만 틀어짐 보정을 사용하세요 (5~10분마다).
    
    Joy-Con, owoTrack 및 MPU 시리즈 IMU(최신 펌웨어 제외)
    등과 같은 트래커들이 해당합니다.
settings-general-tracker_mechanics-drift_compensation_warning-cancel = 취소
settings-general-tracker_mechanics-drift_compensation_warning-done = 이해했어요
settings-general-tracker_mechanics-drift_compensation-amount-label = 보정 강도
settings-general-tracker_mechanics-drift_compensation-max_resets-label = 보정에 사용할 최근 정렬 횟수
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

## FK/Tracking settings

settings-general-fk_settings = 트래킹 설정
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
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = 상수를 사용해 보정
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = 관절이 최대 회전 각도를 넘어 회전 시 관절의 회전 각도를 보정합니다
settings-general-fk_settings-arm_fk = 팔 트래킹
settings-general-fk_settings-arm_fk-description = 손 컨트롤러 위치 데이터를 사용할 수 없는 경우에도 VR 헤드셋(HMD)으로부터 팔을 추적하도록 할 수 있어요.
settings-general-fk_settings-arm_fk-force_arms = 팔을 HMD에서만 받아오기
settings-general-fk_settings-reset_settings = 정렬 설정
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = 전체 정렬을 수행하면 HMD의 피치(앞뒤 회전각)도 재설정해요. VTubing 또는 모션 캡처에서 이마에 HMD를 걸쳐두거나 할 때 유용해요. VR에서는 사용하지 마세요.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = HMD 피치 정렬
settings-general-fk_settings-arm_fk-reset_mode-description = 착용 방향 정렬에 사용되는 팔 자세 설정
settings-general-fk_settings-arm_fk-back = 뒤쪽
settings-general-fk_settings-arm_fk-back-description = 기본값. 위쪽 팔은 뒤를 향하고 아래쪽 팔은 앞을 향하게 하는 자세.
settings-general-fk_settings-arm_fk-tpose_up = T-포즈(올리기)
settings-general-fk_settings-arm_fk-tpose_up-description = 전체 정렬을 수행할 때에는 팔을 차렷 자세로 내리고, 착용 방향 정렬 중에는 팔을 좌우로 나란히 동작으로 펼치는 자세.
settings-general-fk_settings-arm_fk-tpose_down = T-포즈(내리기)
settings-general-fk_settings-arm_fk-tpose_down-description = 전체 정렬에서는 좌우로 나란히 자세, 착용 방향 정렬에서는 팔을 차렷 동작으로 내리는 자세
settings-general-fk_settings-arm_fk-forward = 앞쪽
settings-general-fk_settings-arm_fk-forward-description = 앞으로 나란히 자세. 앉아있거나 버튜버 활동 등에서 유용해요.
settings-general-fk_settings-skeleton_settings-toggles = 골격 설정 제어
settings-general-fk_settings-skeleton_settings-description = 골격 설정을 설정하거나 해제해요. 이것들은 켜두는 게 좋아요.
settings-general-fk_settings-skeleton_settings-extended_spine_model = 확장된 척추 모델
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = 확장된 골반 모델
settings-general-fk_settings-skeleton_settings-extended_knees_model = 확장된 무릎 모델
settings-general-fk_settings-skeleton_settings-ratios = 골격 비율
settings-general-fk_settings-skeleton_settings-ratios-description = 골격 비율을 변경하면 신체 비율 설정을 다시 조절해야 할 수 있어요.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = 가슴, 골반으로부터 추측한 허리 데이터
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = 가슴, 다리로부터 추측한 허리 데이터
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = 다리로부터 추측한 골반 데이터
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = 허리, 다리로부터 추측한 골반 데이터
settings-general-fk_settings-skeleton_settings-interp_hip_legs = 골반 각도를 계산할 때 다리 각도를 합산
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = 무릎 트래커 각도를 계산할 때 발목 각도를 합산
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = 무릎 각도를 계산할 때 발목 각도를 합산
settings-general-fk_settings-self_localization-title = Mocap 모드
settings-general-fk_settings-self_localization-description = Mocap 모드에서는 헤드셋이나 다른 트래커 없이 골격이 자신의 위치를 대략적으로 추적할 수 있어요. 발과 머리 트래커가 필요하고 아직 실험적이에요.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = 제스처 제어
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
settings-general-interface-theme = 컬러 테마
settings-general-interface-show-navbar-onboarding = 내비게이션 바에 "{ navbar-onboarding }" 표시
settings-general-interface-show-navbar-onboarding-description = 이 설정은 내비게이션 바에 "{ navbar-onboarding }" 버튼을 표시할 지 결정해요.
settings-general-interface-show-navbar-onboarding-label = "{ navbar-onboarding }" 보이기
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
settings-interface-appearance-decorations = 인터페이스
settings-interface-appearance-decorations-description = 상단바 인터페이스를 자체적으로 랜더링하는 대신 시스템의 기본 인터페이스를 유지하기
settings-interface-appearance-decorations-label = 시스템 기본 인터페이스 사용

## Notification settings

settings-interface-notifications = 알림
settings-general-interface-serial_detection = 시리얼 디바이스 감지
settings-general-interface-serial_detection-description = 이 옵션은 트래커일 수도 있는 새로운 시리얼 디바이스를 연결할 때마다 팝업을 표시해요. 트래커 설정 프로세스를 개선하는 데 도움이 될 거예요.
settings-general-interface-serial_detection-label = 시리얼 디바이스 감지
settings-general-interface-feedback_sound = 피드백 사운드
settings-general-interface-feedback_sound-description = 이 옵션을 켜면 트래커를 정렬할 때 효과음을 재생해요
settings-general-interface-feedback_sound-label = 피드백 사운드
settings-general-interface-feedback_sound-volume = 피드백 사운드 음량
settings-general-interface-connected_trackers_warning = 작동 중인 트래커 경고
settings-general-interface-connected_trackers_warning-description = 이 옵션은 트래커가 연결되어 있는 채로 SlimeVR을 종료하려고 할 때 팝업을 표시합니다. 그렇게 하면 실수로 트래커를 끄지 않아 배터리가 방전되는 일을 예방할 수 있어요.
settings-general-interface-connected_trackers_warning-label = 종료 시 작동 중인 트래커 경고 활성화

## Behavior settings

settings-general-interface-use_tray = 작업 표시줄로 최소화
settings-general-interface-use_tray-description = SlimeVR 서버를 닫지 않고 창만 닫을 수 있게 하여 사용 시 항상 GUI를 띄워 놓을 필요가 없게 해요.
settings-general-interface-use_tray-label = 작업 표시줄로 최소화
settings-general-interface-discord_presence = Discord에서 활동 공유
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

settings-serial = 시리얼 콘솔
# This cares about multilines
settings-serial-description =
    이 라이브 피드에서 시리얼 디바이스와 통신할 수 있어요.
    펌웨어가 제대로 작동하는지 알아야 할 때 유용할 거예요.
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
settings-serial-get_wifi_scan = WiFi 검색
settings-serial-file_type = 텍스트 문서
settings-serial-save_logs = 파일에 저장

## OSC router settings

settings-osc-router = OSC 라우터
# This cares about multilines
settings-osc-router-description =
    다른 프로그램에서 오는 OSC 메시지를 전달해요.
    예를 들어 VRChat과 함께 다른 OSC 프로그램을 사용하는 데 유용할 거예요.
settings-osc-router-enable = 활성화
settings-osc-router-enable-description = 활성화해서 메세지 전달 켜기
settings-osc-router-enable-label = 활성화
settings-osc-router-network = 네트워크 포트
# This cares about multilines
settings-osc-router-network-description =
    데이터 수신 및 전송을 위한 포트 설정
    이들은 SlimeVR 서버에서 사용되는 다른 포트와 동일해도 돼요.
settings-osc-router-network-port_in =
    .label = 들어오는 포트
    .placeholder = Port in (기본값: 9002)
settings-osc-router-network-port_out =
    .label = 나가는 포트
    .placeholder = Port out (기본값: 9000)
settings-osc-router-network-address = 네트워크 주소
settings-osc-router-network-address-description = 데이터를 보낼 주소를 설정하세요.
settings-osc-router-network-address-placeholder = IPV4 주소

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description-v1 =
    SteamVR이 없는 애플리케이션(예: Quest 단독 실행)에 추적 데이터를 전송하는 데 사용되는 OSC 트래커 표준에 관한 설정.
    VRChat에서 OSC를 사용하려면 액션 메뉴에서 '옵션' > 'OSC' > '활성화됨' 토글 스위치를 켜 주세요.
    그리고 HMD 및 컨트롤러 데이터를 수신하려면 '트래킹 및 IK' > 'OSC를 통한 머리와 손목 트래킹 데이터 전송'을 활성화 해주세요.
settings-osc-vrchat-enable = 활성화
settings-osc-vrchat-enable-description = 데이터 송/수신 활성화
settings-osc-vrchat-enable-label = 활성화
settings-osc-vrchat-oscqueryEnabled = OSCQuery 활성화
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery는 실행 중인 VRChat 인스턴스를 자동으로 탐지한 후 데이터를 전송합니다.
    또한 HMD와 컨트롤러 정보를 받아오기 위해 신호를 발송할 수도 있습니다.
    VRChat으로부터 HMD와 컨트롤러 정보를 받아오려면, 메인 메뉴 설정에서
    "Tracking & IK" 메뉴 아래 "Allow Sending Head and Wrist VR Tracking OSC Data" 옵션을 활성화해 주세요.
settings-osc-vrchat-oscqueryEnabled-label = OSCQuery 활성화
settings-osc-vrchat-network = 네트워크 포트
settings-osc-vrchat-network-description-v1 = 들어오는 포트와 나가는 포트 설정하기, VRChat에서 사용하려면 그냥 두세요.
settings-osc-vrchat-network-port_in =
    .label = 들어오는 포트
    .placeholder = Port in (기본값: 9001)
settings-osc-vrchat-network-port_out =
    .label = 나가는 포트
    .placeholder = Port out (기본값: 9000)
settings-osc-vrchat-network-address = 네트워크 주소
settings-osc-vrchat-network-address-description-v1 = OSC 데이터를 보낼 주소, VRChat에서 사용하려면 그냥 두세요.
settings-osc-vrchat-network-address-placeholder = VRChat IP 주소
settings-osc-vrchat-network-trackers = 트래커
settings-osc-vrchat-network-trackers-description = OSC를 통한 특정 트래커의 전송 여부 설정
settings-osc-vrchat-network-trackers-chest = Chest
settings-osc-vrchat-network-trackers-hip = 골반
settings-osc-vrchat-network-trackers-knees = Knees
settings-osc-vrchat-network-trackers-feet = Feet
settings-osc-vrchat-network-trackers-elbows = Elbows

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    SlimeVR의 골격 데이터를 보내고 다른 앱에서 이 데이터를 수신하기 위해
    VMC(Virtual Motion Capture) 프로토콜 설정을 변경합니다.
settings-osc-vmc-enable = 활성화
settings-osc-vmc-enable-description = 활성화해서 데이터 송수신
settings-osc-vmc-enable-label = 활성화
settings-osc-vmc-network = 네트워크 포트
settings-osc-vmc-network-description = VMC와 데이터를 송수신할 포트 설정
settings-osc-vmc-network-port_in =
    .label = 들어오는 포트
    .placeholder = 들어오는 포트 (기본: 39540)
settings-osc-vmc-network-port_out =
    .label = 나가는 포트
    .placeholder = 나가는 포트 (기본: 39539)
settings-osc-vmc-network-address = 네트워크 주소
settings-osc-vmc-network-address-description = 데이터를 받을 VMC 클라이언트의 주소
settings-osc-vmc-network-address-placeholder = IPV4 주소
settings-osc-vmc-vrm = VRM 모델
settings-osc-vmc-vrm-description = VRM 모델을 로드할 때 헤드 앵커를 허용하고 다른 애플리케이션과의 호환성을 높여줘요.
settings-osc-vmc-vrm-untitled_model = 이름 없는 모델
settings-osc-vmc-vrm-file_select = 모델을 <u>열거나,</u> 여기에 드래그&드롭하세요.
settings-osc-vmc-anchor_hip = 골반에 앵커 설정
settings-osc-vmc-anchor_hip-description = 추적을 엉덩이에 고정해요. 앉은 자세로 VTubing할 때 유용해요. 비활성화하는 경우 VRM 모델에서 가져와요.
settings-osc-vmc-anchor_hip-label = 골반에 앵커 설정
settings-osc-vmc-mirror_tracking = 움직임 좌우 반전
settings-osc-vmc-mirror_tracking-description = 움직임을 수평 방향으로 반전시킵니다.
settings-osc-vmc-mirror_tracking-label = 움직임 좌우 반전

## Common OSC settings


## Advanced settings

settings-utils-advanced = 고급
settings-utils-advanced-reset-gui = GUI 설정 초기화하기
settings-utils-advanced-reset-gui-description = 인터페이스 관련 설정을 원래대로 되돌려요.
settings-utils-advanced-reset-gui-label = GUI 설정 초기화
settings-utils-advanced-reset-server = 트래킹 설정 초기화하기
settings-utils-advanced-reset-server-description = 트래킹 관련 설정을 원래대로 되돌려요.
settings-utils-advanced-reset-server-label = 트래킹 설정 초기화
settings-utils-advanced-reset-all = 모든 설정 초기화하기
settings-utils-advanced-reset-all-description = 인터페이스와 트래킹 설정을 모두 원래대로 되돌려요.
settings-utils-advanced-reset-all-label = 모든 설정 초기화
settings-utils-advanced-reset_warning =
    { $type ->
        [gui] <b>경고:</b> 이렇게 하면 화면 표시와 관련된 모든 설정이 초기화돼요. 계속하시겠어요?
        [server] <b>경고:</b> 이렇게 하면 화면 트래커의 움직임에 관한 모든 설정이 초기화돼요. 계속하시겠어요?
       *[all] <b>경고:</b> 이렇게 하면 지금까지 변경한 모든 설정이 초기화돼요. 계속하시겠어요?
    }
settings-utils-advanced-reset_warning-reset = 설정 초기화
settings-utils-advanced-reset_warning-cancel = 취소
settings-utils-advanced-open_data-v1 = 설정 폴더
settings-utils-advanced-open_data-label = 폴더 열기
settings-utils-advanced-open_logs = 로그 폴더
settings-utils-advanced-open_logs-label = 폴더 열기

## Home Screen


## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = 설정 건너뛰기
onboarding-continue = 계속하기
onboarding-wip = 아직 공사중이에요!
onboarding-previous_step = 이전 단계
onboarding-setup_warning =
    <b>주의:</b> 더 좋은 추적을 위해서는 초기 설정이 필요해요.
    SlimeVR을 처음 사용하는 경우에도 필요합니다.
onboarding-setup_warning-skip = 설정 건너뛰기
onboarding-setup_warning-cancel = 설정 계속하기

## Wi-Fi setup

onboarding-wifi_creds-back = 처음으로 돌아가기
onboarding-wifi_creds-skip = Wi-Fi 설정 건너뛰기
onboarding-wifi_creds-submit = 저장!
onboarding-wifi_creds-ssid =
    .label = Wi-Fi 이름
    .placeholder = Wi-Fi 이름을 입력하세요
onboarding-wifi_creds-ssid-required = Wi-Fi 이름을 입력하세요
onboarding-wifi_creds-password =
    .label = 암호
    .placeholder = 암호를 입력하세요

## Mounting setup

onboarding-reset_tutorial-back = 착용 방향 정렬로 돌아가기
onboarding-reset_tutorial = 정렬 튜토리얼
onboarding-reset_tutorial-explanation = 트래커를 사용하다 보면 IMU의 yaw 드리프트 또는 트래커의 위치가 달라져서 틀어짐이 발생할 수 있어요. 이러한 문제들을 해결하는 몇 가지 방법을 알려 드릴게요.
onboarding-reset_tutorial-skip = 단계 건너뛰기
# Cares about multiline
onboarding-reset_tutorial-0 =
    Yaw 정렬을 시도하려면 강조된 트래커를 { $taps }번 탭하세요.
    
    그러면 트래커는 HMD가 바라보는 면과 같은 방향으로 정렬될 거예요.
# Cares about multiline
onboarding-reset_tutorial-1 =
    전체 정렬을 시도하려면 강조된 트래커를 { $taps }번 탭하세요.
    
    탭한 다음 3초 뒤에(설정에서 변경 가능) 실제 보정이 이뤄지기 때문에 그 사이에 일어나서 차렷 자세로 보정을 기다리면 돼요.
    전체 정렬은 모든 트래커의 위치와 각도를 원래대로 되돌리기 때문에 대부분의 틀어짐 문제를 해결할 수 있어요.
# Cares about multiline
onboarding-reset_tutorial-2 =
    착용 방향 정렬을 시도하려면 강조된 트래커를 { $taps }번 탭하세요.
    
    착용 방향 정렬은 실제로 몸에 있는 트래커의 위치를 감지할 수 있어요. 트래커를 정확한 방향으로 착용하지 않거나 실수로 움직여서 트래커가 미끄러져도 착용 방향 정렬을 통해 해결할 수 있어요.
    
    자동 착용 방향 설정 마법사에서 봤던 것처럼 스키를 타는 듯한 자세로 몸을 구부리고 있으세요. 탭한 다음 3초 뒤에(설정에서 변경 가능) 보정이 시작될 거예요.

## Setup start

onboarding-home = SlimeVR에 어서오세요!
onboarding-home-start = 설정하러 가보죠!

## Setup done

onboarding-done-title = 모든 설정을 마쳤어요!
onboarding-done-description = 풀바디 트래킹을 즐기세요!
onboarding-done-close = 마법사 닫기

## Tracker connection setup

onboarding-connect_tracker-back = Wi-Fi 자격 증명으로 돌아가기
onboarding-connect_tracker-title = 트래커 연결
onboarding-connect_tracker-description-p0-v1 = 이제 트래커를 연결하는 재미있는 부분으로 가봐요!
onboarding-connect_tracker-description-p1-v1 = USB 포트를 통해 트래커들을 한 개씩 컴퓨터에 연결해 주세요.
onboarding-connect_tracker-issue-serial = 연결하는 데 문제가 생겼어요!
onboarding-connect_tracker-usb = USB 트래커
onboarding-connect_tracker-connection_status-none = 트래커 찾는 중
onboarding-connect_tracker-connection_status-serial_init = 시리얼 디바이스에 연결 중
onboarding-connect_tracker-connection_status-obtaining_mac_address = 트래커 MAC 주소를 가져오는 중
onboarding-connect_tracker-connection_status-provisioning = Wi-Fi 자격 증명 전송 중
onboarding-connect_tracker-connection_status-connecting = Wi-Fi 연결 시도 중
onboarding-connect_tracker-connection_status-looking_for_server = 서버 찾는 중
onboarding-connect_tracker-connection_status-connection_error = Wi-Fi에 연결할 수 없음
onboarding-connect_tracker-connection_status-could_not_find_server = 서버를 찾을 수 없음
onboarding-connect_tracker-connection_status-done = 서버에 연결됨
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] 연결된 트래커가 없어요.
       *[other] 트래커 { $amount }개가 연결되었어요.
    }
onboarding-connect_tracker-next = 트래커를 모두 연결했어요

## Tracker calibration tutorial

onboarding-calibration_tutorial = IMU 보정 튜토리얼
onboarding-calibration_tutorial-subtitle = 트래커 틀어짐을 줄이는 데 도움이 될 거예요!
onboarding-calibration_tutorial-calibrate = 트래커들을 모두 올려뒀어요
onboarding-calibration_tutorial-status-waiting = 대기 중
onboarding-calibration_tutorial-status-calibrating = 보정 중
onboarding-calibration_tutorial-status-success = 좋아요!
onboarding-calibration_tutorial-status-error = 트래커가 움직였습니다
onboarding-calibration_tutorial-skip = 튜토리얼 건너뛰기

## Tracker assignment tutorial

onboarding-assignment_tutorial = Slime 트래커를 착용하기 전에 준비하는 방법
onboarding-assignment_tutorial-first_step = 1. 신체 부위가 적힌 스티커를 가지고 있다면 트래커에 붙여보세요
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = 스티커
onboarding-assignment_tutorial-second_step-v2 = 2. 스트랩을 트래커에 장착하되, 벨크로 접착면이 트래커의 Slime 얼굴과 같은 방향을 바라보도록 해 주세요:
onboarding-assignment_tutorial-second_step-continuation-v2 = 확장 모듈의 벨크로 접착면이 다음 이미지처럼 위를 향하게 해 주세요:
onboarding-assignment_tutorial-done = 스트랩과 스티커를 트래커에 잘 부착했어요!

## Tracker assignment setup

onboarding-assign_trackers-back = Wi-Fi 자격 증명으로 돌아가기
onboarding-assign_trackers-title = 트래커 위치 지정
onboarding-assign_trackers-description = 이제, 어떤 트래커가 어디에 있는지 선택할 시간이에요. 트래커를 배치할 위치를 클릭해보세요
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = 트래커 { $trackers }개 중 { $assigned }개 연결됨
onboarding-assign_trackers-advanced = 고급 할당 위치 보기
onboarding-assign_trackers-next = 모든 트래커를 배치했어요
onboarding-assign_trackers-mirror_view = 좌우 반전
onboarding-assign_trackers-option-amount = x{ $trackersCount }
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] 하반신 세트
        [core] 코어 세트
        [enhanced-core] 향상된 코어 세트
        [full-body] 풀 바디 세트
       *[all] 전부 다
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] VR 신체 추적에 필요한 최소 할당
        [core] + 골반 추적 향상
        [enhanced-core] + 발 회전 감지
        [full-body] + 팔꿈치 추적
       *[all] 가능한 모든 트래커 할당
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [6] 왼발 트래커를 할당했다면 왼쪽 다리 아래 트래커도 할당해야 해요.
        [5] 왼발 트래커를 할당했다면 왼쪽 다리 위 트래커도 할당해야 해요.
        [4] 왼발 트래커를 할당했다면 왼쪽 다리 아래, 왼쪽 다리 위 트래커도 할당해야 해요.
        [3] 왼발 트래커를 할당했다면 골반, 허리 또는 가슴 트래커도 할당해야 해요.
        [2] 왼발 트래커를 할당했다면 왼쪽 다리 아래 트래커와 골반, 허리 또는 가슴 트래커도 할당해야 해요.
        [1] 왼발 트래커를 할당했다면 왼쪽 다리 위 트래커와 골반, 허리 또는 가슴 트래커도 할당해야 해요.
        [0] 왼발 트래커를 할당했다면 왼쪽 다리 아래, 왼쪽 다리 위 트래커와 허리, 골반 또는 가슴 트래커도 할당해야 해요.
       *[other] 왼발 트래커를 할당했다면 다른 몸통 트래커도 할당해야 해요.
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] 오른발 트래커를 할당했다면 오른쪽 다리 아래, 오른쪽 다리 위 트래커와 허리, 골반 또는 가슴 트래커도 할당해야 해요.
        [1] 오른발 트래커를 할당했다면 오른쪽 다리 위 트래커와 골반, 허리 또는 가슴 트래커도 할당해야 해요.
        [2] 오른발 트래커를 할당했다면 오른쪽 다리 아래 트래커와 골반, 허리 또는 가슴 트래커도 할당해야 해요.
        [3] 오른발 트래커를 할당했다면 골반, 허리 또는 가슴 트래커도 할당해야 해요.
        [4] 오른발 트래커를 할당했다면 오른쪽 다리 아래, 오른쪽 다리 위 트래커도 할당해야 해요.
        [5] 오른발 트래커를 할당했다면 오른쪽 다리 위 트래커도 할당해야 해요.
        [6] 오른발 트래커를 할당했다면 오른쪽 다리 아래 트래커도 할당해야 해요.
       *[other] 오른발 트래커를 할당했다면 다른 몸통 트래커도 할당해야 해요.
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] 왼쪽 다리 아래 트래커를 할당했다면 왼쪽 다리 위 트래커도 할당해야 해요.
        [1] 왼쪽 다리 아래 트래커를 할당했다면 허리, 골반 또는 가슴 트래커도 할당해야 해요.
        [2] 왼쪽 다리 아래 트래커를 할당했다면 왼쪽 다리 위 트래커와 허리, 골반 또는 가슴 트래커도 할당해야 해요.
       *[other] 왼쪽 다리 아래 트래커를 할당했다면 다른 몸통 트래커도 할당해야 해요.
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] 오른쪽 다리 아래 트래커를 할당했다면 오른쪽 다리 위 트래커도 할당해야 해요.
        [1] 오른쪽 다리 아래 트래커를 할당했다면 허리, 골반 또는 가슴 트래커도 할당해야 해요.
        [2] 오른쪽 다리 아래 트래커를 할당했다면 오른쪽 다리 위 트래커와 허리, 골반 또는 가슴 트래커도 할당해야 해요.
       *[other] 오른쪽 다리 아래 트래커를 할당했다면 다른 몸통 트래커도 할당해야 해요.
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] 왼쪽 다리 위 트래커를 할당했다면 허리, 골반 또는 가슴 트래커도 할당해야 해요.
       *[other] 왼쪽 다리 위 트래커를 할당했다면 다른 몸통 트래커도 할당해야 해요.
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] 오른쪽 다리 위 트래커를 할당했다면 허리, 골반 또는 가슴 트래커도 할당해야 해요.
       *[other] 오른쪽 다리 위 트래커를 할당했다면 다른 몸통 트래커도 할당해야 해요.
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] 골반 트래커를 할당했다면 가슴 트래커도 할당해야 해요.
       *[other] 골반 트래커를 할당했다면 다른 몸통 트래커도 할당해야 해요.
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] 허리 트래커를 할당했다면 가슴 트래커도 할당해야 해요.
       *[other] 허리 트래커를 할당했다면 다른 몸통 트래커도 할당해야 해요.
    }

## Tracker mounting method choose

onboarding-choose_mounting = 착용 방향 보정을 위해 어떤 방법을 사용할까요?
# Multiline text
onboarding-choose_mounting-description = 착용 방향 정렬은 트래커가 몸에 착용된 방향을 찾아 수정하도록 도와줘요.
onboarding-choose_mounting-auto_mounting = 자동으로 방향 설정
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = 권장됨
onboarding-choose_mounting-auto_mounting-description = 이렇게 하면 2가지 자세로 모든 트래커의 착용 방향을 자동으로 설정할 수 있어요
onboarding-choose_mounting-manual_mounting = 수동으로 방향 설정
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = 정확하지 않을 수도 있어요
onboarding-choose_mounting-manual_mounting-description = 이렇게 하면 각 트래커의 착용 방향을 직접 고를 수 있어요
# Multiline text
onboarding-choose_mounting-manual_modal-title = 자동으로 착용 방향을 설정하시겠어요?
onboarding-choose_mounting-manual_modal-description = 자동 착용 방향 정렬은 복잡한 자세와 올바르게 설정된 트래커가 필요하기 때문에 처음 사용하시는 분에게는 조금 어려울 수 있어요. <b>처음 이용하시는 분들께는 수동 착용 방향 정렬을 추천드려요!</b>
onboarding-choose_mounting-manual_modal-confirm = 네, 알고 있어요!
onboarding-choose_mounting-manual_modal-cancel = 취소

## Tracker manual mounting setup

onboarding-manual_mounting-back = VR 입장 페이지로 돌아가기
onboarding-manual_mounting = 수동으로 착용 방향 설정
onboarding-manual_mounting-description = 트래커를 클릭한 다음, 트래커가 바라보는 방향을 선택해주세요
onboarding-manual_mounting-auto_mounting = 자동으로 착용 방향 설정
onboarding-manual_mounting-next = 다음 단계

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = VR 입장 페이지로 돌아가기
onboarding-automatic_mounting-title = 착용 방향 정렬
onboarding-automatic_mounting-description = SlimeVR 트래커가 작동하려면, 실제 트래커의 장착 위치와 맞게 트래커의 착용 방향을 설정해야 해요.
onboarding-automatic_mounting-manual_mounting = 수동으로 착용 방향 설정
onboarding-automatic_mounting-next = 다음 단계
onboarding-automatic_mounting-prev_step = 이전 단계
onboarding-automatic_mounting-done-title = 착용 방향이 정렬되었어요
onboarding-automatic_mounting-done-description = 트래커의 착용 방향이 잘 설정되었어요!
onboarding-automatic_mounting-done-restart = 다시 처음으로 돌아가기
onboarding-automatic_mounting-mounting_reset-title = 착용 방향 정렬
onboarding-automatic_mounting-mounting_reset-step-0 = 1. 팔, 다리를 구부린 다음 상체를 앞으로 기울여서 마치 스키를 타는 것처럼 몸을 굽혀 낮추세요.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. "착용 방향 재설정" 버튼을 누르고 착용 방향이 재설정될 때 까지 3초간 기다려주세요.
onboarding-automatic_mounting-preparation-title = 준비
onboarding-automatic_mounting-put_trackers_on-title = 트래커를 착용해주세요
onboarding-automatic_mounting-put_trackers_on-description = 트래커의 착용 방향을 보정하기 위해 방금 할당한 트래커들을 사용할 거예요. 모든 트래커를 착용했다면 오른쪽 그림에서 각각의 트래커가 어떤 위치에 있는지 확인할 수 있어요.
onboarding-automatic_mounting-put_trackers_on-next = 모든 트래커를 착용했어요

## Tracker manual proportions setupa

onboarding-manual_proportions-title = 수동 신체 비율 설정
onboarding-manual_proportions-fine_tuning_button = 신체 비율을 자동으로 조정
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = 신체 비율 자동 조정 기능을 이용하려면 VR 헤드셋을 연결해 주세요
onboarding-manual_proportions-export = 신체 비율 내보내기
onboarding-manual_proportions-import = 신체 비율 가져오기
onboarding-manual_proportions-file_type = 신체 비율 파일

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = 정렬 튜토리얼로 돌아가기
onboarding-automatic_proportions-title = 신체 비율을 측정해보세요
onboarding-automatic_proportions-description = SlimeVR 트래커가 작동하려면 뼈의 길이를 알아야 하는데, 이 짧은 보정을 통해 측정할 수 있어요.
onboarding-automatic_proportions-manual = 수동 신체 비율 설정
onboarding-automatic_proportions-prev_step = 뒤로
onboarding-automatic_proportions-put_trackers_on-title = 트래커를 착용하세요
onboarding-automatic_proportions-put_trackers_on-description = 비율을 조정하기 위해 방금 할당한 트래커를 사용할 거예요. 모든 트래커를 착용하면 오른쪽 그림에서 어떤 것이 있는지 알 수 있어요.
onboarding-automatic_proportions-put_trackers_on-next = 트래커를 모두 착용했어요
onboarding-automatic_proportions-requirements-title = 요구사항
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    다리를 추적하기 위해 최소 5개 이상의 트래커를 착용하세요.
    VR 헤드셋을 착용하세요.
    VR 헤드셋과 컨트롤러의 위치 정보가 SlimeVR 서버에 실시간으로 표시되는지 확인하세요.
    SlimeVR 서버에 연결된 트래커와 VR 헤드셋이 끊김이나 렉 없이 제대로 표시되는지 확인하세요. (이것은 SteamVR이 실행 중이며 SlimeVR의 SteamVR 드라이버를 사용하여 SlimeVR에 올바르게 연결되어 있다는 것을 의미합니다)
    트래커가 제대로 작동하고 나의 움직임을 올바르게 반영중인지 확인하세요. (예: 전체 정렬을 수행했으며 발차기, 허리 숙이기, 앉기 등 올바른 방향으로 움직임)
onboarding-automatic_proportions-requirements-next = 요구사항을 모두 읽었어요
onboarding-automatic_proportions-check_height-title-v3 = 헤드셋 높이 측정
onboarding-automatic_proportions-check_height-description-v2 = 헤드셋(HMD)은 눈 높이에 위치해 있으므로 감지된 높이는 실제 키보다 살짝 작을 것입니다. 이 측정치는 사용자의 신체 비율을 추산하기 위한 기준값으로 이용됩니다.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = 키를 측정하기 위해 <u>똑바로 선 채</u>로 측정을 시작히세요. 측정에 영향을 줄 수 있으므로, 손을 헤드셋보다 높이 올리지 않게 주의하세요!
onboarding-automatic_proportions-check_height-guardian_tip =
    독립형 VR 헤드셋을 사용하는 경우, 꼭 보호자와 함께하셔야 하고
    높이가 정확하도록 플레이 영역을 설정하는 것을 잊지 말아주세요!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = 알 수 없음
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = 측정된 헤드셋 높이:
onboarding-automatic_proportions-check_height-measure-start = 측정 시작
onboarding-automatic_proportions-check_height-measure-stop = 측정 중지
onboarding-automatic_proportions-check_height-measure-reset = 측정 재시도
onboarding-automatic_proportions-check_height-next_step = 괜찮아 보여요
onboarding-automatic_proportions-check_floor_height-title = 바닥 높이 측정 (선택)
onboarding-automatic_proportions-check_floor_height-description = 경우에 따라 헤드셋이 바닥 높이를 올바르게 감지하지 못하여 실제보다 헤드셋 높이가 더 크게 측정될 수 있습니다. 헤드셋을 이용해 바닥의 "높이"를 측정하여 이에 따른 오차를 보정할 수 있습니다.
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = 측정을 시작하고 바닥의 높이를 측정하기 위해 컨트롤러 하나를 바닥에 놓아주세요. 바닥 높이가 정확하다면 건너뛰셔도 좋습니다.
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = 측정된 바닥 높이:
onboarding-automatic_proportions-check_floor_height-full_height = 추산된 키:
onboarding-automatic_proportions-check_floor_height-measure-start = 측정 시작
onboarding-automatic_proportions-check_floor_height-measure-stop = 측정 중지
onboarding-automatic_proportions-check_floor_height-measure-reset = 측정 재시도
onboarding-automatic_proportions-check_floor_height-skip_step = 건너뛰고 저장하기
onboarding-automatic_proportions-check_floor_height-next_step = 바닥 높이를 사용하고 저장하기
onboarding-automatic_proportions-start_recording-title = 움직일 준비를 해요
onboarding-automatic_proportions-start_recording-description = 이제 몇 가지 특정 포즈와 동작을 기록할 거예요. 다음 화면에서 메시지가 표시되면 버튼을 눌러서 시작하세요!
onboarding-automatic_proportions-start_recording-next = 기록 시작하기
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = 기록하는 중...
onboarding-automatic_proportions-recording-description-p1 = 아래에 표시된 동작을 따라 하세요
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    자리에서 똑바로 일어나서, 머리를 원을 그리듯이 움직이세요.
    등을 앞으로 구부리고 스쿼트를 하듯이 몸을 낮추세요. 그대로 왼쪽을 바라본 다음 오른쪽을 바라보세요.
    상체를 왼쪽(시계 반대 방향)으로 비틀어서 바닥을 향해 손을 뻗으세요.
    상체를 오른쪽(시계 방향)으로 비틀어서 바닥을 향해 손을 뻗으세요.
    훌라후프를 사용하는 것처럼 골반을 원을 그리며 굴리세요.
    아직 기록이 끝나기 전까지 시간이 남아 있다면, 위처럼 여러 가지로 움직이세요.
onboarding-automatic_proportions-recording-processing = 결과 처리 중
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
       *[other] { $time } 초 남음
    }
onboarding-automatic_proportions-verify_results-title = 결과를 확인하세요
onboarding-automatic_proportions-verify_results-description = 아래 결과를 한번 보세요, 어때요?
onboarding-automatic_proportions-verify_results-results = 기록 결과
onboarding-automatic_proportions-verify_results-processing = 결과 처리중
onboarding-automatic_proportions-verify_results-redo = 다시 기록하기
onboarding-automatic_proportions-verify_results-confirm = 정확해요!
onboarding-automatic_proportions-done-title = 몸을 측정하고 저장했어요
onboarding-automatic_proportions-done-description = 신체 비율 보정이 완료되었어요!
onboarding-automatic_proportions-error_modal-v2 =
    <b>경고:</b> 신체 비율을 추정하는 동안 오류가 발생했어요..!
    이는 착용 방향 정렬 문제일 수 있어요. 다시 시도하기 전에 추적이 제대로 작동하는지 확인해보세요.
     <docs>설명서</docs>를 읽어보거나 <discord>Discord 서버</discord>에 가입해서 도와달라고 해 보세요! ^_^
onboarding-automatic_proportions-error_modal-confirm = 이해했어요!
onboarding-automatic_proportions-smol_warning =
    당신의 설정된 키({ $height })는 프로그램이 허용하는 최소 키({ $minHeight })보다 작습니다.
    <b>측정을 다시 수행하고 측정값들이 올바른지 확인해 주세요.</b>
onboarding-automatic_proportions-smol_warning-cancel = 돌아가기

## User height calibration


## Stay Aligned setup


## Home

home-no_trackers = 감지되거나 할당된 트래커가 없어요.

## Trackers Still On notification

trackers_still_on-modal-title = 트래커가 아직 켜져 있어요!
trackers_still_on-modal-description = 하나 이상의 트래커가 여전히 켜져 있는 것으로 보여요. SlimeVR을 종료할까요?
trackers_still_on-modal-confirm = SlimeVR 종료
trackers_still_on-modal-cancel = 돌아가기

## Status system

status_system-StatusTrackerReset = 전체 정렬을 수행해 아직 정렬되지 않은 트래커를 정렬해주세요.
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] SlimeVR 피더와 연결되지 않음
       *[other] SlimeVR 드라이버가 SteamVR과 연결되지 않음
    }
status_system-StatusTrackerError = { $trackerName } 트래커에 문제가 발생했어요.
status_system-StatusUnassignedHMD = VR 헤드셋은 머리 트래커로 할당되어야 해요.

## Firmware tool globals

firmware_tool-next_step = 다음 단계
firmware_tool-previous_step = 이전 단계
firmware_tool-ok = 좋아요
firmware_tool-retry = 재시도
firmware_tool-loading = 로딩 중...

## Firmware tool Steps

firmware_tool = DIY 펌웨어 도구
firmware_tool-description = DIY 트래커를 설정하고 펌웨어를 쓸 수 있습니다
firmware_tool-not_available = 앗, 지금은 펌웨어 툴을 사용할 수 없어요. 나중에 다시 오세요!
firmware_tool-not_compatible = 이 서버 버전은 펌웨어 도구를 지원하지 않습니다. 서버를 업데이트해 주세요!
firmware_tool-flash_method_step = 펌웨어 플래시 방식
firmware_tool-flash_method_step-description = 펌웨어를 트래커에 플래시할 방법을 선택해 주세요
firmware_tool-flashbtn_step = BOOT 버튼 누르기
firmware_tool-flashbtn_step-description = 다음 단계로 진행하기 전 몇 가지 작업을 해야 해요
firmware_tool-flashbtn_step-board_SLIMEVR = 트래커를 끄고, 케이스를 제거하고 (만약 있다면), 이 컴퓨터에 USB 케이블을 연결한 후 SlimeVR 보드 버전에 따라 해당하는 작업을 수행해 주세요:
firmware_tool-flashbtn_step-board_OTHER =
    펌웨어를 쓰기 전에 트래커를 부트로더 모드에 진입시켜야 해요.
    대부분의 경우 이는 펌웨어 쓰기 작업이 시작되기 전 보드에 있는 BOOT 버튼을 누르면 가능합니다.
    펌웨어 쓰기 작업 초반에 시간 초과가 발생한다면 트래커가 부트로더 모드에 진입하지 않아서일 확률이 높아요.
    트래커를 부트로더 모드로 진입시키는 방법은 가지고 계신 보드의 펌웨어 쓰기 도움말을 참고해 주세요
firmware_tool-flash_method_ota-devices = 감지된 OTA 장치:
firmware_tool-flash_method_ota-no_devices = OTA를 이용하여 업데이트할 수 있는 보드가 없습니다. 올바른 보드 버전을 선택했는지 확인해 주세요
firmware_tool-flash_method_serial-wifi = Wi-Fi 이름과 비밀번호:
firmware_tool-flash_method_serial-devices-label = 감지된 Serial 장치:
firmware_tool-flash_method_serial-devices-placeholder = Serial 장치 선택
firmware_tool-flash_method_serial-no_devices = 호환되는 Serial 장치가 감지되지 않았습니다. 트래커와 컴퓨터가 연결되어 있는지 확인해 주세요
firmware_tool-build_step = 빌드 중
firmware_tool-build_step-description = 펌웨어를 빌드하는 중입니다. 잠시만 기다려 주세요
firmware_tool-flashing_step = 펌웨어 쓰는 중
firmware_tool-flashing_step-description = 트래커에 펌웨어를 쓰는 중입니다. 화면의 지시를 따라 주세요
firmware_tool-flashing_step-flash_more = 더 많은 트래커에 펌웨어 쓰기
firmware_tool-flashing_step-exit = 나가기

## firmware tool build status

firmware_tool-build-CREATING_BUILD_FOLDER = 빌드 폴더 만드는 중
firmware_tool-build-BUILDING = 펌웨어 빌드 중
firmware_tool-build-SAVING = 빌드 저장 중
firmware_tool-build-DONE = 빌드 완료
firmware_tool-build-ERROR = 펌웨어를 빌드할 수 없음

## Firmware update status

firmware_update-status-DOWNLOADING = 펌웨어 다운로드 중
firmware_update-status-AUTHENTICATING = MCU와 연결 시도 중
firmware_update-status-UPLOADING = 펌웨어 업로드 중
firmware_update-status-SYNCING_WITH_MCU = MCU와 동기화 중
firmware_update-status-REBOOTING = 업데이트 적용 중
firmware_update-status-PROVISIONING = Wi-Fi 자격 증명 설정 중
firmware_update-status-DONE = 업데이트 완료!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = 장치를 찾을 수 없음
firmware_update-status-ERROR_TIMEOUT = 업데이트 시간 초과
firmware_update-status-ERROR_DOWNLOAD_FAILED = 펌웨어를 다운로드할 수 없음
firmware_update-status-ERROR_AUTHENTICATION_FAILED = MCU와 연결할 수 없음
firmware_update-status-ERROR_UPLOAD_FAILED = 펌웨어를 업로드할 수 없음
firmware_update-status-ERROR_PROVISIONING_FAILED = Wi-Fi 자격 증명을 설정할 수 없음
firmware_update-status-ERROR_UNSUPPORTED_METHOD = 업데이트 방법이 지원되지 않음
firmware_update-status-ERROR_UNKNOWN = 알 수 없는 오류

## Dedicated Firmware Update Page

firmware_update-title = 펌웨어 업데이트
firmware_update-devices = 사용 가능한 장치
firmware_update-devices-description = 최신 버전의 SlimeVR 펌웨어로 업데이트하려는 트래커들을 선택하십시오.
firmware_update-no_devices = 업데이트하고자 하는 트래커들의 전원이 켜져 있고, Wi-Fi에 연결되어 있는지 확인해 주세요!
firmware_update-changelog-title = { $version }으로 업데이트 중
firmware_update-looking_for_devices = 업데이트할 장치들 찾는 중
firmware_update-retry = 재시도
firmware_update-update = 선택된 트래커들 업데이트
firmware_update-exit = 나가기

## Tray Menu

tray_menu-show = 열기
tray_menu-hide = 숨기기
tray_menu-quit = 종료

## First exit modal

tray_or_exit_modal-title = 닫기 버튼이 무엇을 하도록 할까요?
# Multiline text
tray_or_exit_modal-description =
    이 옵션은 닫기 버튼을 누를 때 서버를 종료할지 또는 시스템 트레이로 최소화할지를 결정합니다.
    
    언제든지 설정의 인터페이스 탭에서 변경하실 수 있어요!
tray_or_exit_modal-radio-exit = SlimeVR 종료하기
tray_or_exit_modal-radio-tray = 작업 표시줄로 최소화
tray_or_exit_modal-submit = 저장
tray_or_exit_modal-cancel = 취소

## Unknown device modal

unknown_device-modal-title = 새로운 트래커를 찾았어요!
unknown_device-modal-description =
    <b>{ $deviceID }</b>의 MAC 주소를 가진 새로운 트래커를 발견했어요.
    SlimeVR에 연결할까요?
unknown_device-modal-confirm = 당연하죠!
unknown_device-modal-forget = 무시할게요

## Error collection consent modal

error_collection_modal-title = 오류를 수집해도 될까요?
error_collection_modal-confirm = 동의해요

## Tracking checklist section

