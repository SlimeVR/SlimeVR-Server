### SlimeVR complete GUI translations


# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = 서버에 연결하는 중...
websocket-connection_lost = 서버와의 연결이 끊어졌어요. 다시 연결하는 중...

## Tips

tips-find_tracker = 내 트래커가 어떤 트래커인지 모르시겠다구요? 트래커를 흔들면 해당 항목이 빛날 거예요.
tips-do_not_move_heels = 기록하는 동안 발뒤꿈치가 움직이지 않도록 조심하세요!
tips-file_select = 파일을 <u>열거나,</u> 여기에 드래그&드롭하세요.

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

## Proportions

skeleton_bone-NONE = 없음
skeleton_bone-HEAD = 머리 밀림
skeleton_bone-NECK = 목 길이
skeleton_bone-torso_group = 몸통 길이
skeleton_bone-CHEST = 가슴 길이
skeleton_bone-CHEST_OFFSET = 가슴 오프셋
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
reset-full = 전체 정렬
reset-mounting = 착용 방향 정렬
reset-yaw = Yaw 정렬

## Serial detection stuff

serial_detection-new_device-p0 = 새로운 시리얼 디바이스를 찾았어요!
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

## Bounding volume hierarchy recording

bvh-start_recording = BVH 기록
bvh-recording = 기록중...

## Widget: Overlay settings

widget-overlay = 오버레이
widget-overlay-is_visible_label = SteamVR에서 오버레이 표시
widget-overlay-is_mirrored_label = 오버레이 반전

## Widget: Drift compensation

widget-drift_compensation-clear = 틀어짐 보정 초기화

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
widget-imu_visualizer-rotation_raw = Raw
widget-imu_visualizer-rotation_preview = 미리보기

## Tracker status

tracker-status-none = 알 수 없음
tracker-status-busy = 바쁨
tracker-status-error = 오류
tracker-status-disconnected = 연결되지 않음
tracker-status-occluded = 사용할 수 없음
tracker-status-ok = 연결됨

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
tracker-rotation-left = 왼쪽
tracker-rotation-right = 오른쪽
tracker-rotation-back = 뒤쪽

## Tracker information

tracker-infos-manufacturer = 제조사
tracker-infos-display_name = 표시되는 이름
tracker-infos-custom_name = 사용자 정의 이름
tracker-infos-url = 트래커 URL
tracker-infos-version = 펌웨어 버전
tracker-infos-hardware_rev = 하드웨어 리비전

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
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 트래커 이름
tracker-settings-name_section-description = 귀여운 이름을 지어주세요! >_<
tracker-settings-name_section-placeholder = NightyBeast's left leg

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
settings-sidebar-fk_settings = FK 설정
settings-sidebar-gesture_control = 제스처 제어
settings-sidebar-interface = 인터페이스
settings-sidebar-osc_router = OSC 라우터
settings-sidebar-utils = 유틸리티
settings-sidebar-serial = 시리얼 콘솔

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
settings-general-steamvr-trackers-waist = Waist
settings-general-steamvr-trackers-chest = Chest
settings-general-steamvr-trackers-feet = Feet
settings-general-steamvr-trackers-knees = Knees
settings-general-steamvr-trackers-elbows = Elbows
settings-general-steamvr-trackers-hands = Hands

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
settings-general-tracker_mechanics-drift_compensation = 틀어짐 보정
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    틀어지는 방향의 반대 방향으로 회전해서 IMU Yaw 드리프트를 보정할 수 있어요.
    보정하는 강도와 감지할 최근 정렬 횟수를 설정할 수 있어요.
settings-general-tracker_mechanics-drift_compensation-enabled-label = 틀어짐 보정
settings-general-tracker_mechanics-drift_compensation-amount-label = 보정 강도
settings-general-tracker_mechanics-drift_compensation-max_resets-label = 보정에 사용할 최근 정렬 횟수

## FK/Tracking settings

settings-general-fk_settings = FK 설정
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
settings-general-fk_settings-arm_fk = 팔 운동학
settings-general-fk_settings-arm_fk-description = 팔이 추적되는 방식을 변경할 수 있어요.
settings-general-fk_settings-arm_fk-force_arms = 팔을 HMD에서만 받아오기
settings-general-fk_settings-skeleton_settings = 골격 설정
settings-general-fk_settings-skeleton_settings-description = 골격 설정을 설정하거나 해제해요. 이것들은 켜두는 게 좋아요.
settings-general-fk_settings-skeleton_settings-extended_spine = 척추 확장
settings-general-fk_settings-skeleton_settings-extended_pelvis = 골반 확장
settings-general-fk_settings-skeleton_settings-extended_knees = 무릎 확장
settings-general-fk_settings-vive_emulation-title = VIVE 에뮬레이션
settings-general-fk_settings-vive_emulation-description = 바이브 트래커가 가지고 있는 허리 트래커 문제를 따라해보세요! 사실 이건 장난이고 추적을 더 악화시켜요.
settings-general-fk_settings-vive_emulation-label = VIVE 에뮬레이션 활성화

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
settings-general-gesture_control-yawResetEnabled = 탭해서 Yaw 정렬 활성화
settings-general-gesture_control-yawResetDelay = Yaw 정렬 딜레이
settings-general-gesture_control-yawResetTaps = Yaw 정렬 탭 횟수
settings-general-gesture_control-fullResetEnabled = 탭해서 전체 정렬 활성화
settings-general-gesture_control-fullResetDelay = 전체 정렬 딜레이
settings-general-gesture_control-fullResetTaps = 탭해서 전체 정렬
settings-general-gesture_control-mountingResetEnabled = 탭해서 착용 방향 정렬 활성화
settings-general-gesture_control-mountingResetDelay = 착용 방향 정렬 딜레이
settings-general-gesture_control-mountingResetTaps = 탭해서 착용 방향 정렬

## Interface settings

settings-general-interface = 인터페이스
settings-general-interface-dev_mode = 개발자 모드
settings-general-interface-dev_mode-description = 이 모드는 더 많은 데이터가 필요하거나 고급 수준에서 연결된 트래커와 상호 작용하는 경우에 유용할 수 있어요.
settings-general-interface-dev_mode-label = 개발자 모드
settings-general-interface-serial_detection = 시리얼 디바이스 감지
settings-general-interface-serial_detection-description = 이 옵션은 트래커일 수도 있는 새로운 시리얼 디바이스를 연결할 때마다 팝업을 표시해요. 트래커 설정 프로세스를 개선하는 데 도움이 될 거예요.
settings-general-interface-serial_detection-label = 시리얼 디바이스 감지
settings-general-interface-feedback_sound = 피드백 사운드
settings-general-interface-feedback_sound-description = 이 옵션을 켜면 트래커를 정렬할 때 효과음을 재생해요
settings-general-interface-feedback_sound-label = 피드백 사운드
settings-general-interface-feedback_sound-volume = 피드백 사운드 음량
settings-general-interface-theme = 컬러 테마
settings-general-interface-lang = 언어 선택
settings-general-interface-lang-description = 사용하고 싶은 기본 언어를 선택하세요.
settings-general-interface-lang-placeholder = 사용할 언어를 선택하세요

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
settings-serial-get_infos = 정보 가져오기
settings-serial-serial_select = 시리얼 포트 선택
settings-serial-auto_dropdown_item = 자동

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
    이들은 SlimeVR 서버에서 사용되는 다른 포트와 동일할 수 있어요.
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
settings-osc-vrchat-description =
    HMD 데이터 수신 및 전송을 위한 VRChat 관련 설정 변경
    FBT용 트래커 데이터(Quest 단독에서 작동)
settings-osc-vrchat-enable = 활성화
settings-osc-vrchat-enable-description = 활성화해서 데이터 송수신
settings-osc-vrchat-enable-label = 활성화
settings-osc-vrchat-network = 네트워크 포트
settings-osc-vrchat-network-description = VRChat과 데이터를 주고받는 포트 설정
settings-osc-vrchat-network-port_in =
    .label = 들어오는 포트
    .placeholder = Port in (기본값: 9001)
settings-osc-vrchat-network-port_out =
    .label = 나가는 포트
    .placeholder = Port out (기본값: 9000)
settings-osc-vrchat-network-address = 네트워크 주소
settings-osc-vrchat-network-address-description = VRChat으로 데이터를 보낼 주소를 선택하세요(장치의 Wi-Fi 설정 확인).
settings-osc-vrchat-network-address-placeholder = VRChat IP 주소
settings-osc-vrchat-network-trackers = 트래커
settings-osc-vrchat-network-trackers-description = 활성화해서 데이터 송수신
settings-osc-vrchat-network-trackers-chest = Chest
settings-osc-vrchat-network-trackers-waist = Waist
settings-osc-vrchat-network-trackers-knees = Knees
settings-osc-vrchat-network-trackers-feet = Feet
settings-osc-vrchat-network-trackers-elbows = Elbows

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    SlimeVR의 본 데이터를 보내고 다른 앱에서 본 데이터를 수신하려면
    VMC(Virtual Motion Capture) 프로토콜 설정을 변경하세요.
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
settings-osc-vmc-vrm-description = VRM 모델을 로드할 때 헤드 앵커를 허용하고 다른 애플리케이션과 더 높은 호환성을 가능하게 해요.
settings-osc-vmc-vrm-model_unloaded = 로드된 모델이 없어요
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] 로드된 모델: { $name }
       *[other] 제목이 없는 모델 로드됨
    }
settings-osc-vmc-vrm-file_select = 모델을 <u>열거나,</u> 여기에 드래그&드롭하세요.
settings-osc-vmc-anchor_hip = 골반에 앵커 설정
settings-osc-vmc-anchor_hip-description = 추적을 엉덩이에 고정해요. 앉은 자세로 VTubing할 때 유용해요. 비활성화하는 경우 VRM 모델에서 가져와요.
settings-osc-vmc-anchor_hip-label = 골반에 앵커 설정

## Setup/onboarding menu

onboarding-skip = 설정 건너뛰기
onboarding-continue = 계속하기
onboarding-wip = 아직 공사중이에요!
onboarding-previous_step = 이전 단계
onboarding-setup_warning =
    <b>주의:</b> SlimeVR을 처음 설정하고 계시다면...
    트래커가 올바르게 움직이기 위해 이 초기 설정이 필요해요
onboarding-setup_warning-skip = 설정 건너뛰기
onboarding-setup_warning-cancel = 설정 계속하기

## Wi-Fi setup

onboarding-wifi_creds-back = 처음으로 돌아가기
onboarding-wifi_creds = Wi-Fi 자격 증명을 입력하세요
# This cares about multilines
onboarding-wifi_creds-description =
    트래커는 이 자격 증명을 사용하여 무선으로 연결해요
    지금 연결되어 있는 자격 증명을 사용해주세요
onboarding-wifi_creds-skip = Wi-Fi 설정 건너뛰기
onboarding-wifi_creds-submit = 저장!
onboarding-wifi_creds-ssid =
    .label = Wi-Fi 이름
    .placeholder = Wi-Fi 이름을 입력하세요
onboarding-wifi_creds-password =
    .label = 암호
    .placeholder = 암호를 입력하세요

## Mounting setup

onboarding-reset_tutorial-back = 착용 방향 정렬로 돌아가기
onboarding-reset_tutorial = 정렬 튜토리얼
onboarding-reset_tutorial-description = 이 기능은 아직 완성되지 않았어요, 지금은 일단 계속하기를 눌러주세요!

## Setup start

onboarding-home = SlimeVR에 어서오세요!
onboarding-home-start = 설정하러 가보죠!

## Enter VR part of setup

onboarding-enter_vr-back = 트래커 위치 지정으로 돌아가기
onboarding-enter_vr-title = VR에 들어갈 시간이에요!
onboarding-enter_vr-description = 모든 트래커를 착용하고 VR에 입장하세요!
onboarding-enter_vr-ready = 준비됐어요!

## Setup done

onboarding-done-title = 모든 설정을 마쳤어요!
onboarding-done-description = 풀바디 트래킹을 즐기세요!
onboarding-done-close = 마법사 닫기

## Tracker connection setup

onboarding-connect_tracker-back = Wi-Fi 자격 증명으로 돌아가기
onboarding-connect_tracker-title = 트래커 연결
onboarding-connect_tracker-description-p0 = 이제 모든 트래커를 연결하는 재미있는 부분으로 가봐요!
onboarding-connect_tracker-description-p1 = 그냥 모든 트래커를 USB 포트에 연결하기만 하면 돼요
onboarding-connect_tracker-issue-serial = 연결하는 데 문제가 생겼어요!
onboarding-connect_tracker-usb = USB 트래커
onboarding-connect_tracker-connection_status-none = 트래커 찾는 중
onboarding-connect_tracker-connection_status-serial_init = 시리얼 디바이스에 연결 중
onboarding-connect_tracker-connection_status-provisioning = Wi-Fi 자격 증명 전송 중
onboarding-connect_tracker-connection_status-connecting = Wi-Fi 자격증명 전송 중
onboarding-connect_tracker-connection_status-looking_for_server = 서버 찾는 중
onboarding-connect_tracker-connection_status-connection_error = Wi-Fi에 연결할 수 없음
onboarding-connect_tracker-connection_status-could_not_find_server = 서버를 찾을 수 없어요
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
onboarding-connect_tracker-next = 모든 트래커를 잘 연결했어요

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

onboarding-choose_mounting = 착용 방향을 정렬하기 위해 어떤 방법을 사용할래요?
onboarding-choose_mounting-auto_mounting = 자동으로 방향 설정
# Italized text
onboarding-choose_mounting-auto_mounting-subtitle = 권장
onboarding-choose_mounting-auto_mounting-description = 이렇게 하면 두 가지 자세로 모든 트래커의 착용 방향을 자동으로 설정할 수 있어요
onboarding-choose_mounting-manual_mounting = 수동으로 방향 설정
# Italized text
onboarding-choose_mounting-manual_mounting-subtitle = 무엇을 하려는 지 알고 있다면요
onboarding-choose_mounting-manual_mounting-description = 이렇게 하면 각 트래커의 착용 방향을 직접 고를 수 있어요

## Tracker manual mounting setup

onboarding-manual_mounting-back = VR 입장 페이지로 돌아가기
onboarding-manual_mounting = 수동으로 착용 방향 설정
onboarding-manual_mounting-description = 트래커를 클릭한 다음, 트래커가 바라보는 방향을 선택해주세요
onboarding-manual_mounting-auto_mounting = 자동으로 착용 방향 설정
onboarding-manual_mounting-next = 다음 단계

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = VR 입장 페이지로 돌아가기
onboarding-automatic_mounting-title = 착용 방향 정렬
onboarding-automatic_mounting-description = SlimeVR 트래커가 작동하려면, 착용 방향을 정해야 해요. 이 단계에서는 실제 트래커의 회전과 맞게 착용 방향을 정렬할 거예요.
onboarding-automatic_mounting-manual_mounting = 수동으로 착용 방향 설정
onboarding-automatic_mounting-next = 다음 단계
onboarding-automatic_mounting-prev_step = 이전 단계
onboarding-automatic_mounting-done-title = 착용 방향이 정렬되었어요
onboarding-automatic_mounting-done-description = 트래커의 착용 방향이 잘 설정되었어요!
onboarding-automatic_mounting-done-restart = 다시 처음으로 돌아가기
onboarding-automatic_mounting-mounting_reset-title = 착용 방향 정렬
onboarding-automatic_mounting-mounting_reset-step-0 = 1. 팔, 다리를 구부린 다음 상체를 앞으로 기울여서 마치 스키를 타는 것처럼 쪼그리고 앉으세요.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. "착용 방향 재설정" 버튼을 누르고 착용 방향이 재설정될 때 까지 3초간 기다려주세요.
onboarding-automatic_mounting-preparation-title = 준비
onboarding-automatic_mounting-preparation-step-0 = 1. 팔을 몸에 붙이고 똑바로 서 주세요
onboarding-automatic_mounting-preparation-step-1 = 2. "전체 정렬" 버튼을 누르고 트래커가 정렬될 때까지 3초간 기다려주세요.
onboarding-automatic_mounting-put_trackers_on-title = 트래커를 착용해주세요
onboarding-automatic_mounting-put_trackers_on-description = 트래커의 착용 방향이 돌아가는 것을 보정하기 위해 방금 할당한 트래커를 사용할 거예요. 모든 트래커를 착용했다면 오른쪽 그림에서 어떤 트래커인지 확인할 수 있어요.
onboarding-automatic_mounting-put_trackers_on-next = 모든 트래커를 착용했어요

## Tracker proportions method choose

onboarding-choose_proportions = 신체 비율을 설정하 위해 어떤 방법을 사용할래요?
onboarding-choose_proportions-auto_proportions = 자동으로 비율 설정
# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = 권장
onboarding-choose_proportions-auto_proportions-description = 이렇게 하면 움직임 샘플을 기록하고 알고리즘을 통과해서 자동으로 신체 비율을 설정할 수 있어요
onboarding-choose_proportions-manual_proportions = 수동으로 비율 설정
# Italized text
onboarding-choose_proportions-manual_proportions-subtitle = 정밀하게 설정하고 싶다면
onboarding-choose_proportions-manual_proportions-description = 이렇게 하면 신체 비율을 직접 수정하여 수동으로 조절할 수 있어요

## Tracker manual proportions setup

onboarding-manual_proportions-back = 정렬 튜토리얼로 돌아가기
onboarding-manual_proportions-title = 수동 신체 비율 설정
onboarding-manual_proportions-precision = 자세히 조절하기
onboarding-manual_proportions-auto = 자동 신체 비율 설정
onboarding-manual_proportions-ratio = 비율 그룹으로 조절하기

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = 정렬 튜토리얼로 돌아가기
onboarding-automatic_proportions-title = 신체 비율을 측정해보세요
onboarding-automatic_proportions-description = SlimeVR 트래커가 작동하려면 뼈의 길이를 알아야 하는데, 이 짧은 보정을 통해 측정할 수 있어요.
onboarding-automatic_proportions-manual = 수동 신체 비율 설정
onboarding-automatic_proportions-prev_step = 뒤로
onboarding-automatic_proportions-put_trackers_on-title = 트래커를 착용하세요
onboarding-automatic_proportions-put_trackers_on-description = 비율을 조정하기 위해 방금 할당한 트래커를 사용할 거예요. 모든 트래커를 착용하면 오른쪽 그림에서 어떤 것이 있는지 알 수 있어요.
onboarding-automatic_proportions-put_trackers_on-next = 트래커를 다 착용했어요
onboarding-automatic_proportions-requirements-title = 요구사항
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-description =
    발까지 추적할 수 있는 적어도 5개의 트래커가 있어야 해요.
    전원이 켜진 트래커와 헤드셋이 필요해요.
    트래커와 헤드셋을 착용하고 있어야 해요.
    트래커와 헤드셋이 SlimeVR 서버와 연결되어 있어야 해요.
    트래커와 헤드셋이 SlimeVR 서버에서 제대로 작동하고 있어야 해요.
    헤드셋이 SlimeVR 서버에 위치 데이터를 보고하고 있어야 해요. (SteamVR이 실행 중이고 SlimeVR의 SteamVR 드라이버를 사용하여 SlimeVR에 연결되어 있어야 해요).
onboarding-automatic_proportions-requirements-next = 요구사항을 모두 읽었어요
onboarding-automatic_proportions-start_recording-title = 움직일 준비
onboarding-automatic_proportions-start_recording-description = 이제 몇 가지 특정 포즈와 동작을 기록할 거예요. 다음 화면에서 메시지가 표시되면 버튼을 눌러서 시작하세요!
onboarding-automatic_proportions-start_recording-next = 기록 시작하기
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = 기록하는 중...
onboarding-automatic_proportions-recording-description-p1 = 아래에 표시된 동작을 따라 하세요
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    자리에서 똑바로 일어나서, 머리를 원을 그리듯이 움직이세요. 
    등을 앞으로 구부리고 쪼그리고 앉으세요. 그대로 왼쪽을 바라본 다음 오른쪽을 바라보세요.
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

## Home

home-no_trackers = 감지되거나 할당된 트래커가 없어요.
