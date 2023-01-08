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
tips-find_tracker = 내 트래커가 어떤 트래커인지 모르시겠다구요? 트래커를 흔들면 해당 항목이 빛날 거에요.
tips-do_not_move_heels = 기록하는 동안 발뒤꿈치가 움직이지 않도록 조심하세요!

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
body_part-RIGHT_CONTROLLER = Right controller
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
body_part-LEFT_CONTROLLER = Left controller

## Skeleton stuff
skeleton_bone-NONE = None
skeleton_bone-HEAD = 머리 밀림
skeleton_bone-NECK = 목 길이
skeleton_bone-CHEST = Chest Length
skeleton_bone-CHEST_OFFSET = Chest Offset
skeleton_bone-WAIST = Waist Length
skeleton_bone-HIP = Hip Length
skeleton_bone-HIP_OFFSET = 골반 오프셋
skeleton_bone-HIPS_WIDTH = 골반 너비
skeleton_bone-UPPER_LEG = Upper Leg Length
skeleton_bone-LOWER_LEG = Lower Leg Length
skeleton_bone-FOOT_LENGTH = 발 크기
skeleton_bone-FOOT_SHIFT = 발 밀림
skeleton_bone-SKELETON_OFFSET = 골격 오프셋
skeleton_bone-SHOULDERS_DISTANCE = 어깨 거리
skeleton_bone-SHOULDERS_WIDTH = 어깨 너비
skeleton_bone-UPPER_ARM = 위팔 거리
skeleton_bone-LOWER_ARM = 전완 길이
skeleton_bone-CONTROLLER_Y = 컨트롤러 Y축 거리
skeleton_bone-CONTROLLER_Z = 컨트롤러 Z축 거리
skeleton_bone-ELBOW_OFFSET = 팔꿈치 오프셋

## Tracker reset buttons
reset-reset_all = 모든 신체 비율 리셋
reset-full = 리셋
reset-mounting = 착용 방향 리셋
reset-quick = 퀵 리셋

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

## Widget: Developer settings
widget-developer_mode = 개발자 모드
widget-developer_mode-high_contrast = High contrast
widget-developer_mode-precise_rotation = Precise rotation
widget-developer_mode-fast_data_feed = Fast data feed
widget-developer_mode-filter_slimes_and_hmd = Filter slimes and HMD
widget-developer_mode-sort_by_name = Sort by name
widget-developer_mode-raw_slime_rotation = Raw rotation
widget-developer_mode-more_info = More info

## Widget: IMU Visualizer
widget-imu_visualizer = Rotation
widget-imu_visualizer-rotation_raw = Raw
widget-imu_visualizer-rotation_preview = Preview

## Tracker status
tracker-status-none = No Status
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
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Accel. X/Y/Z
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

## Tracker settings
tracker-settings-back = 트래커 목록으로 돌아가기
tracker-settings-title = 트래커 설정
tracker-settings-assignment_section = 트래커 위치 지정
tracker-settings-assignment_section-description = 트래커가 위치한 신체 부위
tracker-settings-assignment_section-edit = 위치 수정
tracker-settings-mounting_section = 착용 방향
tracker-settings-mounting_section-description = 트래커는 어디에 착용하나요?
tracker-settings-mounting_section-edit = 방향 수정
tracker-settings-drift_compensation_section = Allow drift compensation
tracker-settings-drift_compensation_section-description = Should this tracker compensate for its drift when drift compensation is enabled?
tracker-settings-drift_compensation_section-edit = Allow drift compensation
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
-tracker_selection-part = Which tracker to assign to your
tracker_selection_menu-NONE = Which tracker do you want to be unassigned?
tracker_selection_menu-HEAD = { -tracker_selection-part } head?
tracker_selection_menu-NECK = { -tracker_selection-part } neck?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } right shoulder?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } right upper arm?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } right lower arm?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } right hand?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } right thigh?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } right ankle?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } right foot?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } right controller?
tracker_selection_menu-CHEST = { -tracker_selection-part } chest?
tracker_selection_menu-WAIST = { -tracker_selection-part } waist?
tracker_selection_menu-HIP = { -tracker_selection-part } hip?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } left shoulder?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } left upper arm?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } left lower arm?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } left hand?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } left thigh?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } left ankle?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } left foot?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } left controller?

tracker_selection_menu-unassigned = 할당되지 않은 트래커
tracker_selection_menu-assigned = 할당된 트래커
tracker_selection_menu-dont_assign = 할당하지 않기

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
settings-general-tracker_mechanics-drift_compensation = Drift compensation
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Compensates IMU yaw drift by applying an inverse rotation.
    Change amount of compensation and up to how many resets are taken into account.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Drift compensation
settings-general-tracker_mechanics-drift_compensation-amount-label = Compensation amount
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Use up to x last resets

## FK/Tracking settings
settings-general-fk_settings = FK 설정
settings-general-fk_settings-leg_tweak = 다리 보정
settings-general-fk_settings-leg_tweak-description = 플로어 클립은 바닥과의 클리핑을 줄이거나 제거할 수 있지만 무릎을 꿇을 때 문제를 일으킬 수 있어요. 스케이팅 보정은 아이스 스케이팅을 보정하지만, 특정 움직임 패턴에서 정확도를 저하시킬 수 있어요.
# Floor clip: 
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = 플로어 클립
# Skating correction: 
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = 스케이팅 보정
settings-general-fk_settings-leg_tweak-skating_correction-amount = 스케이팅 보정 강도
settings-general-fk_settings-arm_fk = 팔 운동학
settings-general-fk_settings-arm_fk-description = 팔이 추적되는 방식을 변경할 수 있어요.
settings-general-fk_settings-arm_fk-force_arms = 팔을 HMD에서만 받아오기
settings-general-fk_settings-skeleton_settings = 골격 설정
settings-general-fk_settings-skeleton_settings-description = 골격 설정을 설정하거나 해제해요. 이것들은 켜두는 게 좋아요.
settings-general-fk_settings-skeleton_settings-extended_spine = 척추 확장
settings-general-fk_settings-skeleton_settings-extended_pelvis = 골반 확장
settings-general-fk_settings-skeleton_settings-extended_knees = 무릎 확장
settings-general-fk_settings-vive_emulation-title = Vive emulation
settings-general-fk_settings-vive_emulation-description = Emulate the waist tracker problems that Vive trackers have. This is a joke and makes tracking worse.
settings-general-fk_settings-vive_emulation-label = Enable Vive emulation

## Gesture control settings (tracker tapping)
settings-general-gesture_control = 제스처 제어
settings-general-gesture_control-subtitle = 두 번 탭해서 퀵 리셋하기
settings-general-gesture_control-description = 활성화하면 가장 높이 있는 트래커의 아무 곳이나 두 번 탭해서 퀵 리셋을 활성화할 수 있어요. 두번 탭하는 간격은 딜레이로 조절할 수 있어요.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps = { $amount ->
    [one] 1 tap
    *[other] { $amount } taps
}
settings-general-gesture_control-quickResetEnabled = Enable tap to quick reset
settings-general-gesture_control-quickResetDelay = Quick reset delay
settings-general-gesture_control-quickResetTaps = Taps for quick reset
settings-general-gesture_control-resetEnabled = Enable tap to reset
settings-general-gesture_control-resetDelay = Reset delay
settings-general-gesture_control-resetTaps = Taps for reset
settings-general-gesture_control-mountingResetEnabled = Enable tap to reset mounting
settings-general-gesture_control-mountingResetDelay = Mounting reset delay
settings-general-gesture_control-mountingResetTaps = Taps for mounting reset

## Interface settings
settings-general-interface = 인터페이스
settings-general-interface-dev_mode = 개발자 모드
settings-general-interface-dev_mode-description = 이 모드는 더 많은 데이터가 필요하거나 고급 수준에서 연결된 트래커와 상호 작용하는 경우에 유용할 수 있어요.
settings-general-interface-dev_mode-label = 개발자 모드
settings-general-interface-serial_detection = 시리얼 디바이스 감지
settings-general-interface-serial_detection-description = 이 옵션은 트래커일 수도 있는 새로운 시리얼 디바이스를 연결할 때마다 팝업을 표시해요. 트래커 설정 프로세스를 개선하는 데 도움이 될 거에요.
settings-general-interface-serial_detection-label = 시리얼 디바이스 감지
settings-general-interface-lang = 언어 선택
settings-general-interface-lang-description = 사용하고 싶은 기본 언어를 선택하세요.
settings-general-interface-lang-placeholder = 사용할 언어를 선택하세요

## Serial settings
settings-serial = 시리얼 콘솔
# This cares about multilines
settings-serial-description =
    이 라이브 피드에서 시리얼 디바이스와 통신할 수 있어요.
    펌웨어가 제대로 작동하는지 알아야 할 때 유용할 거에요.
settings-serial-connection_lost = 시리얼 연결 끊김, 다시 연결 중...
settings-serial-reboot = 재부팅
settings-serial-factory_reset = 공장 초기화
settings-serial-get_infos = 정보 가져오기
settings-serial-serial_select = 시리얼 포트 선택
settings-serial-auto_dropdown_item = 자동

## OSC router settings
settings-osc-router = OSC 라우터
# This cares about multilines
settings-osc-router-description =
    다른 프로그램에서 오는 OSC 메시지를 전달해요.
    예를 들어 VRChat과 함께 다른 OSC 프로그램을 사용하는 데 유용할 거에요.
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
settings-osc-vrchat-enable-description = 활성화해서 데이터 송수신 전환
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
settings-osc-vrchat-network-trackers-description = 활성화해서 데이터 송수신 전환
settings-osc-vrchat-network-trackers-chest = Chest
settings-osc-vrchat-network-trackers-waist = Waist
settings-osc-vrchat-network-trackers-knees = Knees
settings-osc-vrchat-network-trackers-feet = Feet
settings-osc-vrchat-network-trackers-elbows = Elbows

## Setup/onboarding menu
onboarding-skip = 설정 건너뛰기
onboarding-continue = 계속하기
onboarding-wip = 아직 공사 중이에요

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
    .label = SSID
    .placeholder = Enter SSID
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup
onboarding-reset_tutorial-back = 착용 방향 정렬로 돌아가기
onboarding-reset_tutorial = 리셋 튜토리얼
onboarding-reset_tutorial-description = 이 기능은 아직 완성되지 않았어요, 지금은 일단 계속하기를 눌러주세요!

## Setup start
onboarding-home = SlimeVR에 어서오세요!
# This cares about multilines and it's centered!!
onboarding-home-description =
    Bringing full-body tracking
    to everyone
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
onboarding-connect_tracker-connection_status-connecting = Wi-Fi 자격증명 전송 중
onboarding-connect_tracker-connection_status-connected = Wi-Fi 연결됨
onboarding-connect_tracker-connection_status-error = Wi-Fi에 연결할 수 없음
onboarding-connect_tracker-connection_status-start_connecting = 트래커 찾는 중
onboarding-connect_tracker-connection_status-handshake = 서버에 연결됨
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers = { $amount ->
    [0] No trackers
    [one] 1 tracker
    *[other] { $amount } trackers
} connected
onboarding-connect_tracker-next = 모든 트래커를 잘 연결했어요

## Tracker assignment setup
onboarding-assign_trackers-back = Wi-Fi 자격 증명으로 돌아가기
onboarding-assign_trackers-title = 트래커 위치 지정
onboarding-assign_trackers-description = 이제, 어떤 트래커가 어디에 있는지 선택할 시간이에요. 트래커를 배치할 위치를 클릭해보세요
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned } of { $trackers ->
    [one] 1 tracker
    *[other] { $trackers } trackers
} assigned
onboarding-assign_trackers-advanced = 고급 할당 위치 보기
onboarding-assign_trackers-next = 모든 트래커를 배치했어요

## Tracker manual mounting setup
onboarding-manual_mounting-back = VR 입장 페이지로 돌아가기
onboarding-manual_mounting = 수동으로 착용 방향 설정
onboarding-manual_mounting-description = 트래커를 클릭한 다음, 트래커가 바라보는 방향을 선택해주세요
onboarding-manual_mounting-auto_mounting = 자동으로 착용 방향 설정
onboarding-manual_mounting-next = 다음 단계

## Tracker automatic mounting setup
onboarding-automatic_mounting-back = VR 입장 페이지로 돌아가기
onboarding-automatic_mounting-title = 착용 방향 정렬
onboarding-automatic_mounting-description = SlimeVR 트래커가 작동하려면, 착용 방향을 정해야 해요. 이 단계에서는 실제 트래커의 회전과 맞게 착용 방향을 정렬할 거에요.
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
onboarding-automatic_mounting-preparation-step-1 = 2. "리셋" 버튼을 누르고 트래커가 리셋될 때까지 3초 동안 기다려주세요
onboarding-automatic_mounting-put_trackers_on-title = 트래커를 착용해주세요
onboarding-automatic_mounting-put_trackers_on-description = 트래커의 착용 방향이 돌아가는 것을 보정하기 위해 방금 할당한 트래커를 사용할 거에요. 모든 트래커를 착용했다면 오른쪽 그림에서 어떤 트래커인지 확인할 수 있어요.
onboarding-automatic_mounting-put_trackers_on-next = 모든 트래커를 착용했어요

## Tracker manual proportions setup
onboarding-manual_proportions-back = 리셋 튜토리얼로 돌아가기
onboarding-manual_proportions-title = 수동 신체 비율 설정
onboarding-manual_proportions-precision = 자세히 조절하기
onboarding-manual_proportions-auto = 자동 신체 비율 설정

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = 리셋 튜토리얼로 돌아가기
onboarding-automatic_proportions-title = 신체 비율을 측정해보세요
onboarding-automatic_proportions-description = SlimeVR 트래커가 작동하려면 뼈의 길이를 알아야 하는데, 이 짧은 보정을 통해 측정할 수 있어요.
onboarding-automatic_proportions-manual = 수동 신체 비율 설정
onboarding-automatic_proportions-prev_step = 뒤로
onboarding-automatic_proportions-put_trackers_on-title = 트래커를 착용하세요
onboarding-automatic_proportions-put_trackers_on-description = 비율을 조정하기 위해 방금 할당한 트래커를 사용할 거에요. 모든 트래커를 착용하면 오른쪽 그림에서 어떤 것이 있는지 알 수 있어요.
onboarding-automatic_proportions-put_trackers_on-next = 트래커를 다 착용했어요
onboarding-automatic_proportions-preparation-title = 준비하기
onboarding-automatic_proportions-preparation-description = 여러분의 놀이 공간 안에 여러분의 바로 뒤에 의자를 놓으세요. 오토본 설정 중에 앉을 수 있도록 준비해주세요.
onboarding-automatic_proportions-preparation-next = 의자 앞에 서 있어요
onboarding-automatic_proportions-start_recording-title = 움직일 준비
onboarding-automatic_proportions-start_recording-description = 이제 몇 가지 특정 포즈와 동작을 기록할 거에요. 다음 화면에서 메시지가 표시되면 버튼을 눌러서 시작하세요!
onboarding-automatic_proportions-start_recording-next = 기록 시작하기
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = 기록하는 중...
onboarding-automatic_proportions-recording-description-p1 = 아래에 표시된 동작을 따라 하세요
onboarding-automatic_proportions-recording-steps-0 = 무릎을 몇 번 구부리세요
onboarding-automatic_proportions-recording-steps-1 = 의자에 앉았다가 일어서세요.
onboarding-automatic_proportions-recording-steps-2 = 상체를 왼쪽으로 비틀고 오른쪽으로 구부리세요.
onboarding-automatic_proportions-recording-steps-3 = 상체를 오른쪽으로 비틀고 왼쪽으로 구부리세요.
onboarding-automatic_proportions-recording-steps-4 = 타이머가 종료될 때까지 이리저리 움직여 보세요.
onboarding-automatic_proportions-recording-processing = 결과 처리 중
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 15)
onboarding-automatic_proportions-recording-timer = { $time ->
    [one] 1 second left
    *[other] { $time } seconds left
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
