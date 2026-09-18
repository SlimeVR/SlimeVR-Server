# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Đang kết nối với máy chủ
websocket-connection_lost = Kết nối với máy chủ đã mất. Đang kết nối lại...
websocket-connection_lost-desc = Máy chủ SlimeVR bị dừng. Hãy kiểm tra nhật ký logs và khởi động lại chương trình.
websocket-timedout = Không thể tìm thấy máy chủ
websocket-timedout-desc = Có vẻ như máy chủ SlimeVR đã bị sập hoặc hết thời gian chờ. Vui lòng kiểm tra nhật ký logs và khởi động lại chương trình.
websocket-error-close = Thoát SlimeVR
websocket-error-logs = Mở thư mục nhật ký logs

## Update notification

version_update-title = New version available: { $version }
version_update-description = Nhấp vào "{ version_update-update }" sẽ tải xuống trình cài đặt SlimeVR cho bạn.
version_update-update = Cập nhật
version_update-close = Đóng

## Tips

tips-find_tracker = Không rõ tracker nào đang được chọn? Di chuyển nó và trong menu sẽ sáng lên tracker đó
tips-do_not_move_heels = Không di chuyển gót chân trong khi đo
tips-file_select = Kéo và thả tệp để sử dụng hoặc <u>duyệt</u>.
tips-tap_setup = Bạn có thể từ từ nhấn vào 2 lần trình theo dõi của mình để chọn nó thay vì chọn nó từ menu.
tips-turn_on_tracker = Sử dụng thiết bị SlimeVR chính thức? Hãy nhớ <b><em>bật trình theo dõi của bạn</em></b> sau khi kết nối thiết bị với máy tính!
tips-failed_webgl = Không thể khởi tạo WebGL.

## Units

unit-meter = Meter
unit-foot = Foot
unit-inch = Inch
unit-cm = cm

## Body parts

body_part-NONE = Chưa được gán
body_part-HEAD = Đầu
body_part-NECK = Cổ
body_part-RIGHT_SHOULDER = Vai phải
body_part-RIGHT_UPPER_ARM = Bắp tay phải
body_part-RIGHT_LOWER_ARM = Cẳng tay phải
body_part-RIGHT_HAND = Tay phải
body_part-RIGHT_UPPER_LEG = Bắp chân phải
body_part-RIGHT_LOWER_LEG = Cẳng chân phải
body_part-RIGHT_FOOT = Bàn chân phải
body_part-UPPER_CHEST = Ngực trên
body_part-CHEST = Ngực
body_part-WAIST = Eo
body_part-HIP = Hông
body_part-LEFT_SHOULDER = Vai trái
body_part-LEFT_UPPER_ARM = Bắp tay trái
body_part-LEFT_LOWER_ARM = Cẳng tay trái
body_part-LEFT_HAND = Tay trái
body_part-LEFT_UPPER_LEG = Bắp chân trái
body_part-LEFT_LOWER_LEG = Cẳng chân trái
body_part-LEFT_FOOT = Bàn chân trái
body_part-LEFT_THUMB_METACARPAL = Left thumb metacarpal
body_part-LEFT_THUMB_PROXIMAL = Left thumb proximal
body_part-LEFT_THUMB_DISTAL = Left thumb distal
body_part-LEFT_INDEX_PROXIMAL = Left index proximal
body_part-LEFT_INDEX_INTERMEDIATE = Left index intermediate
body_part-LEFT_INDEX_DISTAL = Left index distal
body_part-LEFT_MIDDLE_PROXIMAL = Left middle proximal
body_part-LEFT_MIDDLE_INTERMEDIATE = Left middle intermediate
body_part-LEFT_MIDDLE_DISTAL = Left middle distal
body_part-LEFT_RING_PROXIMAL = Left ring proximal
body_part-LEFT_RING_INTERMEDIATE = Left ring intermediate
body_part-LEFT_RING_DISTAL = Left ring distal
body_part-LEFT_LITTLE_PROXIMAL = Left little proximal
body_part-LEFT_LITTLE_INTERMEDIATE = Left little intermediate
body_part-LEFT_LITTLE_DISTAL = Left little distal
body_part-RIGHT_THUMB_METACARPAL = Right thumb metacarpal
body_part-RIGHT_THUMB_PROXIMAL = Right thumb proximal
body_part-RIGHT_THUMB_DISTAL = Right thumb distal
body_part-RIGHT_INDEX_PROXIMAL = Right index proximal
body_part-RIGHT_INDEX_INTERMEDIATE = Right index intermediate
body_part-RIGHT_INDEX_DISTAL = Right index distal
body_part-RIGHT_MIDDLE_PROXIMAL = Right middle proximal
body_part-RIGHT_MIDDLE_INTERMEDIATE = Right middle intermediate
body_part-RIGHT_MIDDLE_DISTAL = Right middle distal
body_part-RIGHT_RING_PROXIMAL = Right ring proximal
body_part-RIGHT_RING_INTERMEDIATE = Right ring intermediate
body_part-RIGHT_RING_DISTAL = Right ring distal
body_part-RIGHT_LITTLE_PROXIMAL = Right little proximal
body_part-RIGHT_LITTLE_INTERMEDIATE = Right little intermediate
body_part-RIGHT_LITTLE_DISTAL = Right little distal

## BoardType

board_type-UNKNOWN = Unknown
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Custom Board
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-SLIMEVR_DEV = SlimeVR Dev Board
board_type-SLIMEVR_V1_2 = SlimeVR v1.2
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
board_type-GESTURES = Gestures
board_type-ESP32S3_SUPERMINI = ESP32-S3 Supermini
board_type-GENERIC_NRF = Generic nRF
board_type-SLIMEVR_BUTTERFLY_DEV = SlimeVR Dev Butterfly
board_type-SLIMEVR_BUTTERFLY = SlimeVR Butterfly

## Proportions

skeleton_bone-NONE = Chưa được gán
skeleton_bone-HEAD = Sai số đầu
skeleton_bone-HEAD-desc =
    This is the distance from your headset to the middle of your head.
    To adjust it, shake your head left to right as if you're disagreeing and modify
    it until any movement in other trackers is negligible.
skeleton_bone-NECK = Chiều dài cổ
skeleton_bone-NECK-desc =
    This is the distance from the middle of your head to the base of your neck.
    To adjust it, move your head up and down as if you're nodding or tilt your head
    to the left and right and modify it until any movement in other trackers is negligible.
skeleton_bone-torso_group = Độ dài thân
skeleton_bone-torso_group-desc =
    This is the distance from the base of your neck to your hips.
    To adjust it, modify it standing up straight until your virtual hips line
    up with your real ones.
skeleton_bone-UPPER_CHEST = Độ dài ngực trên
skeleton_bone-UPPER_CHEST-desc =
    This is the distance from the base of your neck to the middle of your chest.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-CHEST_OFFSET = Sai số ngực
skeleton_bone-CHEST_OFFSET-desc =
    This can be adjusted to move your virtual chest tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be higher or lower.
skeleton_bone-CHEST = Khoảng cách ngực
skeleton_bone-CHEST-desc =
    This is the distance from the middle of your chest to the middle of your spine.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-WAIST = Khoảng cách eo
skeleton_bone-WAIST-desc =
    This is the distance from the middle of your spine to your belly button.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-HIP = Khoảng cách hông
skeleton_bone-HIP-desc =
    This is the distance from your belly button to your hips.
    To adjust it, set your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches your real one.
skeleton_bone-HIP_OFFSET = Sai số hông
skeleton_bone-HIP_OFFSET-desc =
    This can be adjusted to move your virtual hip tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be on your waist.
skeleton_bone-HIPS_WIDTH = Chiều rộng hông
skeleton_bone-HIPS_WIDTH-desc =
    This is the distance between the start of your legs.
    To adjust it, perform a full reset with your legs straight and modify it until
    your virtual legs match up with your real ones horizontally.
skeleton_bone-leg_group = Chiều dài chân
skeleton_bone-leg_group-desc =
    This is the distance from your hips to your feet.
    To adjust it, adjust your Torso Length properly and modify it
    until your virtual feet are at the same level as your real ones.
skeleton_bone-UPPER_LEG = Chiều dài bắp chân
skeleton_bone-UPPER_LEG-desc =
    This is the distance from your hips to your knees.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-LOWER_LEG = Chiều dài cẳng chân
skeleton_bone-LOWER_LEG-desc =
    This is the distance from your knees to your ankles.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-FOOT_LENGTH = Chiều dài bàn chân
skeleton_bone-FOOT_LENGTH-desc =
    This is the distance from your ankles to your toes.
    To adjust it, tiptoe and modify it until your virtual feet stay in place.
skeleton_bone-FOOT_SHIFT = Sai số bàn chân
skeleton_bone-FOOT_SHIFT-desc =
    This value is the horizontal distance from your knee to your ankle.
    It accounts for your lower legs going backwards when standing up straight.
    To adjust it, set Foot Length to 0, perform a full reset and modify it until your virtual
    feet line up with the middle of your ankles.
skeleton_bone-SKELETON_OFFSET = Sai số thân
skeleton_bone-SKELETON_OFFSET-desc =
    This can be adjusted to offset all your trackers forward or backward.
    It can be used to help with calibration in certain games or applications
    that may expect your trackers to be more forward.
skeleton_bone-SHOULDERS_DISTANCE = Khoảng cách vai
skeleton_bone-SHOULDERS_DISTANCE-desc =
    This is the vertical distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up vertically with your real shoulders.
skeleton_bone-SHOULDERS_WIDTH = Chiều rộng vai
skeleton_bone-SHOULDERS_WIDTH-desc =
    This is the horizontal distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up horizontally with your real shoulders.
skeleton_bone-arm_group = Chiều dài cánh tay
skeleton_bone-arm_group-desc =
    This is the distance from your shoulders to your wrists.
    To adjust it, adjust Shoulders Distance properly, set Hand Distance Y
    to 0 and modify it until your hand trackers line up with your wrists.
skeleton_bone-UPPER_ARM = Chiều dài bắp tay
skeleton_bone-UPPER_ARM-desc =
    This is the distance from your shoulders to your elbows.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-LOWER_ARM = Chiều dài cẳng tay
skeleton_bone-LOWER_ARM-desc =
    This is the distance from your elbows to your wrists.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-HAND_Y = Khoảng cách tay Y
skeleton_bone-HAND_Y-desc =
    This is the vertical distance from your wrists to the middle of your hand.
    To adjust it for motion capture, adjust Arm Length properly and modify it until your
    hand trackers line up vertically with the middle of your hands.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up vertically with your wrists.
skeleton_bone-HAND_Z = Khoảng cách tay Z
skeleton_bone-HAND_Z-desc =
    This is the horizontal distance from your wrists to the middle of your hand.
    To adjust it for motion capture, set it to 0.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up horizontally with your wrists.
skeleton_bone-ELBOW_OFFSET = Sai số khuỷu tay
skeleton_bone-ELBOW_OFFSET-desc =
    This can be adjusted to move your virtual elbow trackers up or down in order to aid
    with VRChat accidentally binding an elbow tracker to the chest.

## Tracker reset buttons

reset-reset_all = Đặt lại tất cả bộ phận
reset-reset_all_warning-v2 =
    <b>Warning:</b> Your proportions will be reset to defaults scaled to your configured height.
    Are you sure you want to do this?
reset-reset_all_warning-reset = Đặt lại tỷ lệ
reset-reset_all_warning-cancel = Hủy
reset-reset_all_warning_default-v2 =
    <b>Warning:</b> Your height has not been configured, your proportions will be reset to defaults with the default height.
    Are you sure you want to do this?
reset-full = Đặt lại
reset-mounting = Đặt lại hướng gắn tracker
reset-mounting-feet = Feet Calibration
reset-mounting-fingers = Fingers Calibration
reset-yaw = Đặt lại chiều quay lệch
reset-error-no_feet_tracker = No feet tracker assigned
reset-error-no_fingers_tracker = No finger tracker assigned
reset-error-mounting-need_full_reset = Need a full reset before mounting
reset-error-yaw-need_full_reset = Need a full reset before yaw reset

## Serial detection stuff

serial_detection-new_device-p0 = Tìm thấy thiết bị mới!
serial_detection-new_device-p1 = Nhập thông tin Wi-Fi
serial_detection-new_device-p2 = Chọn hành động cần thực hiện
serial_detection-open_wifi = Kết nối đến Wi-Fi
serial_detection-open_serial = Mở cổng Serial
serial_detection-submit = Gửi
serial_detection-close = Đóng

## Navigation bar

navbar-home = Trang chủ
navbar-body_proportions = Tỉ lệ cơ thể
navbar-trackers_assign = Phân bố tracker
navbar-mounting = Cân chỉnh hướng gắn tracker
navbar-onboarding = Trình thiết lập
navbar-settings = Cài đặt
navbar-connect_trackers = Connect Trackers

## Biovision hierarchy recording

bvh-start_recording = Ghi BVH
bvh-stop_recording = Save BVH recording
bvh-recording = Đang ghi...
bvh-save_title = Save BVH recording

## Tracking pause

tracking-unpaused = Tạm dừng tracking
tracking-paused = Bỏ dừng theo dõi

## Widget: Overlay settings

widget-overlay = Overlay
widget-overlay-is_visible_label = Xem overlay trên SteamVR
widget-overlay-is_mirrored_label = Xem overlay trong gương

## Widget: Drift compensation

widget-drift_compensation-clear = Xóa sai số

## Widget: Clear Mounting calibration

widget-clear_mounting = Đặt lại hướng gắn tracker

## Widget: Developer settings

widget-developer_mode = Chế độ nhà phát triển
widget-developer_mode-high_contrast = Chế độ tương phản cao
widget-developer_mode-precise_rotation = Hiển thị góc quay chính xác
widget-developer_mode-fast_data_feed = Tăng tốc độ gửi dữ liệu
widget-developer_mode-filter_slimes_and_hmd = Lọc dữ liệu tracker và kính
widget-developer_mode-sort_by_name = Sắp xếp theo tên
widget-developer_mode-raw_slime_rotation = Sử dụng giá trị góc quay thực cho tracker
widget-developer_mode-more_info = Thêm thông tin

## Widget: IMU Visualizer

widget-imu_visualizer = Góc quay
widget-imu_visualizer-preview = Xem trước
widget-imu_visualizer-hide = Ẩn
widget-imu_visualizer-rotation_raw = Gốc
widget-imu_visualizer-rotation_preview = Qua xử lí
widget-imu_visualizer-acceleration = Gia tốc
widget-imu_visualizer-position = Vị trí
widget-imu_visualizer-stay_aligned = Stay Aligned

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Xem trước khung xương
widget-skeleton_visualizer-hide = Ẩn

## Tracker status

tracker-status-none = Không có trạng thái
tracker-status-busy = Bận
tracker-status-error = Lỗi
tracker-status-disconnected = Đã ngắt kết nối
tracker-status-occluded = Nghẽn
tracker-status-ok = Đã kết nối
tracker-status-timed_out = Hết thời gian chờ

## Tracker status columns

tracker-table-column-name = Tên
tracker-table-column-type = Loại
tracker-table-column-battery = Pin
tracker-table-column-ping = Ping
tracker-table-column-packet_loss = Packet Loss
tracker-table-column-tps = TPS
tracker-table-column-temperature = Nhiệt độ (°C)
tracker-table-column-linear-acceleration = Tốc độ X/Y/Z
tracker-table-column-rotation = Góc quay X/Y/Z
tracker-table-column-position = Tọa độ X/Y/Z
tracker-table-column-stay_aligned = Stay Aligned
tracker-table-column-url = Đường dẫn

## Tracker rotation

tracker-rotation-front = Trước
tracker-rotation-front_left = Phía trước-Bên trái
tracker-rotation-front_right = Phía trước-Bên phải
tracker-rotation-left = Trái
tracker-rotation-right = Phải
tracker-rotation-back = Sau
tracker-rotation-back_left = Phía sau-Bên trái
tracker-rotation-back_right = Phía sau-Bên phải
tracker-rotation-custom = Tùy chọn
tracker-rotation-overriden = (được ghi đè bởi reset hướng gắn tracker)

## Tracker information

tracker-infos-manufacturer = Nhà sản xuất
tracker-infos-display_name = Tên hiển thị
tracker-infos-custom_name = Tên gọi
tracker-infos-url = Đường dẫn
tracker-infos-version = Phiên bản firmware
tracker-infos-hardware_rev = Revision phần cứng
tracker-infos-hardware_identifier = Hardware ID
tracker-infos-data_support = Data support
tracker-infos-imu = Cảm biến IMU (IMU Sensor)
tracker-infos-board_type = Bảng mạch chính
tracker-infos-network_version = Phiên bản giao thức
tracker-infos-magnetometer = Magnetometer
tracker-infos-magnetometer-status-v1 =
    { $status ->
       *[NOT_SUPPORTED] Not supported
        [DISABLED] Disabled
        [ENABLED] Enabled
    }
tracker-infos-packet_loss = Packet Loss
tracker-infos-packets_lost = Packets Lost
tracker-infos-packets_received = Packets Received

## Tracker settings

tracker-settings-back = Quay lại danh sách tracker
tracker-settings-title = Cài đặt
tracker-settings-assignment_section = Vị trí
tracker-settings-assignment_section-description = Vị trí của tracker trên cơ thể
tracker-settings-assignment_section-edit = Thay đổi vị trí
tracker-settings-mounting_section = Vị trí đặt
tracker-settings-mounting_section-description = Tracker được đặt ở đâu?
tracker-settings-mounting_section-edit = Thay đổi chỗ đặt
tracker-settings-drift_compensation_section = Cho phép bù trừ sai số
tracker-settings-drift_compensation_section-description = Tracker này được phép bù trừ cho sai số của nó không?
tracker-settings-drift_compensation_section-edit = Cho phép bù trừ sai số
tracker-settings-use_mag = Allow magnetometer on this tracker
# Multiline!
tracker-settings-use_mag-description =
    Should this tracker use magnetometer to reduce drift when magnetometer usage is allowed? <b>Please don't shutdown your tracker while toggling this!</b>
    
    You need to allow magnetometer usage first, <magSetting>click here to go to the setting</magSetting>.
tracker-settings-use_mag-label = Allow magnetometer
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tên tracker
tracker-settings-name_section-description = Đặt cho nó một cái tên đẹp :3
tracker-settings-name_section-placeholder = Chân trái của JINODK
tracker-settings-name_section-label = Tracker name
tracker-settings-forget = Quên thiết bị
tracker-settings-forget-description = Xóa thiết bị khỏi phần mềm SlimeVR và ngăn nó kết nối với nó cho đến khi máy chủ được khởi động lại. Cấu hình của trình theo dõi sẽ không bị mất.
tracker-settings-forget-label = Quên thiết bị
tracker-settings-update-unavailable-v2 = No releases found
tracker-settings-update-incompatible = Cannot update. Incompatible board or firmware version
tracker-settings-update-low-battery = Cannot update. Battery lower than 50%
tracker-settings-update-up_to_date = Up to date
tracker-settings-update-blocked = Update not available. No other releases available
tracker-settings-update = Update now
tracker-settings-update-title = Firmware version
tracker-settings-current-version = Current
tracker-settings-latest-version = Latest
tracker-settings-build-date = Build Date

## Tracker part card info

tracker-part_card-no_name = Không tên
tracker-part_card-unassigned = Chưa gán vị trí

## Body assignment menu

body_assignment_menu = Bạn muốn gán tracker này cho bộ phận nào?
body_assignment_menu-description = Chọn vị trí bạn muốn gán tracker, ngoài ra bạn cũng có thể quản lí vị trí tất cả các tracker cùng một lúc
body_assignment_menu-show_advanced_locations = Xem thêm vị trí đặt
body_assignment_menu-manage_trackers = Quản lí tất cả tracker
body_assignment_menu-unassign_tracker = Bỏ gán tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Tracker nào cho vị trí
tracker_selection_menu-NONE = Bạn muốn bỏ gán tracker nào?
tracker_selection_menu-HEAD = { -tracker_selection-part } đầu?
tracker_selection_menu-NECK = { -tracker_selection-part } cổ?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } vai phải?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } bắp tay phải?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } cẳng tay phải?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } tay phải?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } bắp chân phải?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } cẳng chân phải?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } bàn chân phải?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } tay cầm bên phải?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } ngực trên?
tracker_selection_menu-CHEST = { -tracker_selection-part } ngực?
tracker_selection_menu-WAIST = { -tracker_selection-part } eo?
tracker_selection_menu-HIP = { -tracker_selection-part } hông?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } vai trái?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } bắp tay trái?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } cẳng tay trái?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } tay trái?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } bắp chân trái?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } cẳng chân trái?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } bàn chân trái?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } tay cầm bên trái?
tracker_selection_menu-unassigned = Chưa gắn vị trí
tracker_selection_menu-assigned = Đã gán vị trí
tracker_selection_menu-dont_assign = Không gắn
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Cảnh báo:</b> Tracker ở cổ rất nguy hiểm và có thể gây tử vong nếu điều chỉnh quá chặt,
    Dây đeo có thể cắt lưu thông máu đến đầu của bạn!
tracker_selection_menu-neck_warning-done = Tôi hiểu những rủi ro
tracker_selection_menu-neck_warning-cancel = Hủy

## Mounting menu

mounting_selection_menu = Bạn muốn gắn tracker này cho bộ phận nào?
mounting_selection_menu-close = Đóng

## Sidebar settings

settings-sidebar-title = Cài đặt
settings-sidebar-general = Cài đặt chung
settings-sidebar-steamvr = SteamVR
settings-sidebar-tracker_mechanics = Cơ chế tracker
settings-sidebar-stay_aligned = Stay Aligned
settings-sidebar-fk_settings = Cài đặt tracker
settings-sidebar-gesture_control = Cử chỉ điều khiển
settings-sidebar-interface = Giao diện
settings-sidebar-osc_router = Router OSC
settings-sidebar-osc_trackers = Trình theo dõi VRChat OSC
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Công cụ
settings-sidebar-serial = Cổng Serial
settings-sidebar-appearance = Giao diện
settings-sidebar-home = Home Screen
settings-sidebar-checklist = Tracking checklist
settings-sidebar-notifications = Thông báo
settings-sidebar-behavior = Behavior
settings-sidebar-firmware-tool = DIY Firmware Tool
settings-sidebar-vrc_warnings = VRChat Config Warnings
settings-sidebar-advanced = Cài đặt mở rộng

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = Tracker SteamVR
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Bật hoặc tắt tracker SteamVR.
    Hữu dụng nếu game chỉ hỗ trợ số lượng tracker giới hạn.
settings-general-steamvr-trackers-waist = Eo
settings-general-steamvr-trackers-chest = Ngực
settings-general-steamvr-trackers-left_foot = Bàn chân trái
settings-general-steamvr-trackers-right_foot = Bàn chân phải
settings-general-steamvr-trackers-left_knee = Đầu gối trái
settings-general-steamvr-trackers-right_knee = Đầu gối phải
settings-general-steamvr-trackers-left_elbow = Khuỷu tay trái
settings-general-steamvr-trackers-right_elbow = Khuỷu tay phải
settings-general-steamvr-trackers-left_hand = Tay trái
settings-general-steamvr-trackers-right_hand = Tay phải
settings-general-steamvr-trackers-tracker_toggling = Tự động giao thiết bị
settings-general-steamvr-trackers-tracker_toggling-description = Tự động xử lý bật hoặc tắt thiết bị đo SteamVR chuyển đổi tùy thuộc vào thiết bị đã giao của bạn
settings-general-steamvr-trackers-tracker_toggling-label = Tự động giao thiết bị
settings-general-steamvr-trackers-hands-warning =
    <b>Lưu ý:</b> bộ theo dõi tay sẽ dùng đè tay cầm điều khiển của bạn.
    Bạn có chắc không?
settings-general-steamvr-trackers-hands-warning-cancel = Hủy
settings-general-steamvr-trackers-hands-warning-done = Có

## Tracker mechanics

settings-general-tracker_mechanics = Cơ chế tracker
settings-general-tracker_mechanics-filtering = Lọc nhiễu
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Chọn kiểu lọc nhiễu cho tracker
    "Dự đoán" sẽ dự đoán chuyển động trong khi "Khử nhiễu" sẽ làm chuyển động mềm mại hơn
settings-general-tracker_mechanics-filtering-type = Kiểu lọc
settings-general-tracker_mechanics-filtering-type-none = Không lọc
settings-general-tracker_mechanics-filtering-type-none-description = Sử dụng giá trị thực, không áp dụng bất kì bộ lọc nào
settings-general-tracker_mechanics-filtering-type-smoothing = Khử nhiễu
settings-general-tracker_mechanics-filtering-type-smoothing-description = Làm chuyển động mềm mại hơn nhưng có thể tăng độ trễ
settings-general-tracker_mechanics-filtering-type-prediction = Dự đoán
settings-general-tracker_mechanics-filtering-type-prediction-description = Giảm độ trễ và làm chuyển động chân thật hơn, có thể khiến chuyển động không mượt mà
settings-general-tracker_mechanics-filtering-amount = Mức độ lọc
settings-general-tracker_mechanics-yaw-reset-smooth-time = Thời gian thiết lập lại chiều quay lệch (0s tắt làm mượt)
settings-general-tracker_mechanics-drift_compensation = Bù trừ sai số
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Bù trừ sai lệch trục ngang của cảm biến bằng cách thêm một sai lệch chống lại nó
    Thay đổi mức độ bù trừ và số lần đặt lại được áp dụng bù trừ
settings-general-tracker_mechanics-drift_compensation-enabled-label = Bù trừ sai số
settings-general-tracker_mechanics-drift_compensation-prediction = Dự đoán bù trôi (drift compensation)
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    Predicts yaw drift compensation beyond previously measured range.
    Enable this if your trackers are continuously spinning on the yaw axis.
settings-general-tracker_mechanics-drift_compensation-prediction-label = Dự đoán bù trôi (drift compensation)
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>Warning:</b> Only use drift compensation if you need to reset
    very often (every ~5-10 minutes).
    
    Some IMUs prone to frequent resets include:
    Joy-Cons, owoTrack, and MPUs (without recent firmware).
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Hủy
settings-general-tracker_mechanics-drift_compensation_warning-done = Tôi hiểu
settings-general-tracker_mechanics-drift_compensation-amount-label = Mức độ bù trừ
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Số lần đặt lại được áp dụng bù trừ
settings-general-tracker_mechanics-save_mounting_reset = Lưu thiết bị đã giao tự động
settings-general-tracker_mechanics-save_mounting_reset-description =
    Lưu thiết bị đã giao tự động cho các thiết bị giữa các lần khởi động lại. Có ích
    khi mặc một bộ đồ SlimeVR mà trình theo dõi không di chuyển giữa các phiên. <b>Không được khuyến khích cho người dùng bình thường!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Đặt lại hướng gắn thiết bị
settings-general-tracker_mechanics-use_mag_on_all_trackers = Use magnetometer on all IMU trackers that support it
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Uses magnetometer on all trackers that have a compatible firmware for it, reducing drift in stable magnetic environments.
    Can be disabled per tracker in the tracker's settings. <b>Please don't shutdown any of the trackers while toggling this!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Use magnetometer on trackers
settings-general-tracker_mechanics-trackers_over_usb = Trackers over USB
settings-general-tracker_mechanics-trackers_over_usb-description = Enables receiving HID tracker data over USB. Make sure connected trackers have <b>connection over HID</b> enabled!
settings-general-tracker_mechanics-trackers_over_usb-enabled-label = Allow HID trackers to connect directly over USB
settings-stay_aligned = Stay Aligned
settings-stay_aligned-description = Stay Aligned reduces drift by gradually adjusting your trackers to match your relaxed poses.
settings-stay_aligned-setup-label = Setup Stay Aligned
settings-stay_aligned-setup-description = You must complete "Setup Stay Aligned" to enable Stay Aligned.
settings-stay_aligned-warnings-drift_compensation = ⚠ Please turn off Drift Compensation! Drift Compensation will conflict with Stay Aligned.
settings-stay_aligned-enabled-label = Adjust trackers
settings-stay_aligned-hide_yaw_correction-label = Hide adjustment (to compare with no Stay Aligned)
settings-stay_aligned-general-label = General
settings-stay_aligned-relaxed_poses-label = Relaxed Poses
settings-stay_aligned-relaxed_poses-description = Stay Aligned uses your relaxed poses to keep the trackers aligned. Use "Setup Stay Aligned" to update these poses.
settings-stay_aligned-relaxed_poses-standing = Adjust trackers while standing
settings-stay_aligned-relaxed_poses-sitting = Adjust trackers while sitting in a chair
settings-stay_aligned-relaxed_poses-flat = Adjust trackers while sitting on the floor, or lying on your back
settings-stay_aligned-relaxed_poses-save_pose = Save pose
settings-stay_aligned-relaxed_poses-reset_pose = Reset pose
settings-stay_aligned-relaxed_poses-close = Close
settings-stay_aligned-debug-label = Debugging
settings-stay_aligned-debug-description = Please include your settings when reporting problems about Stay Aligned.
settings-stay_aligned-debug-copy-label = Copy settings to clipboard

## FK/Tracking settings

settings-general-fk_settings = Cài đặt nâng cao
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Ngăn xuyên sàn
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Sửa trượt
settings-general-fk_settings-leg_tweak-toe_snap = Đoán hướng xoay chân
settings-general-fk_settings-leg_tweak-foot_plant = Cân bằng chân
settings-general-fk_settings-leg_tweak-skating_correction-amount = Mức độ sửa
settings-general-fk_settings-leg_tweak-skating_correction-description = Sửa trượt sẽ điều chỉnh nhằm giảm thiểu hiện tượng trượt băng nhưng có thể làm giảm độ chính xác trong một vài kiểu chuyển động nhất định. Khi bật tính năng này, vui lòng đảm bảo thực hiện thao tác đặt lại hoàn toàn và hiệu chỉnh lại trong trò chơi.
settings-general-fk_settings-leg_tweak-floor_clip-description = Ngăn xuyên sàn có thể giảm hoặc loại bỏ khả năng tracker của bạn đi xuyên sàn nhà. Khi bật tính năng này, vui lòng đảm bảo thực hiện thao tác đặt lại hoàn toàn và hiệu chỉnh lại trong trò chơi.
settings-general-fk_settings-leg_tweak-toe_snap-description = Đoán hướng xoay chân sẽ đoán hướng xoay của chân đồng thời khóa ngón chân của bạn vào mặt sàn bạn nếu bạn không sử dụng tracker cho chân.
settings-general-fk_settings-leg_tweak-foot_plant-description = Cân bằng chân sẽ xoay chân song song với mặt đất khi lại gần.
settings-general-fk_settings-leg_fk = Track chân
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Force feet mounting calibration during body mounting calibration.
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Force feet mounting calibration
settings-general-fk_settings-enforce_joint_constraints = Skeletal Limits
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Enforce constraints
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Prevents joints from rotating past their limit
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Correct with constraints
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Correct joint rotations when they push past their limit
settings-general-fk_settings-ik = Position data
settings-general-fk_settings-ik-use_position = Use Position data
settings-general-fk_settings-ik-use_position-description = Enables the use of position data from trackers that provide it. When enabling this make sure to full reset and recalibrate in game.
settings-general-fk_settings-velocity_settings = Velocity Settings
settings-general-fk_settings-velocity_settings-description = Send derived velocity data to SteamVR. Required for Natural Locomotion support. May cause jitter in FBT.
settings-general-fk_settings-velocity_settings-send_derived_velocity = Send derived velocity to driver
settings-general-fk_settings-arm_fk = Track cánh tay
settings-general-fk_settings-arm_fk-description = Thay đổi cách cánh tay được track
settings-general-fk_settings-arm_fk-force_arms = Lấy dữ liệu cánh tay từ kính
settings-general-fk_settings-reset_settings = Đặt lại cài đặt
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Đặt lại cao độ của HMD (xoay dọc) khi thiết lập lại toàn bộ. Hữu ích nếu đeo HMD trên trán cho VTubing hoặc mocap. Lưu ý không bật VR khi sử dụng.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Đặt lại cao độ HMD
settings-general-fk_settings-arm_fk-reset_mode-description = Thay đổi tư thế cánh tay để đặt lại hướng gắn tracker.
settings-general-fk_settings-arm_fk-back = Khuỷu tay ra sau
settings-general-fk_settings-arm_fk-back-description = Chế độ mặc định, với cánh tay trên trỏ về phía sau và cánh tay dưới hướng về phía trước.
settings-general-fk_settings-arm_fk-tpose_up = T-pose
settings-general-fk_settings-arm_fk-tpose_up-description = Hai tay của bạn sẽ hướng xuống ở hai bên khi đặt lại hoàn toàn, và đưa lên 90 độ sang hai bên khi đặt lại hướng gắn tracker.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (ngược lại)
settings-general-fk_settings-arm_fk-tpose_down-description = Hai tay của bạn sẽ đưa lên 90 độ sang hai bên khi đặt lại hoàn toàn, và hai tay hướng xuống hai bên khi đặt lại hướng gắn tracker.
settings-general-fk_settings-arm_fk-forward = Hai tay ra trước
settings-general-fk_settings-arm_fk-forward-description = Hai cánh tay của bạn nâng lên 90 độ về phía trước. Hữu dụng cho việc VTubing.
settings-general-fk_settings-skeleton_settings-toggles = Bật tắt bộ xương
settings-general-fk_settings-skeleton_settings-description = Bật hoặc tắt các cài đặt về khung cơ thể. Các lựa chọn này nên được giữ bật
settings-general-fk_settings-skeleton_settings-extended_spine_model = Mô hình cột sống mở rộng
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Mô hình xương chậu mở rộng
settings-general-fk_settings-skeleton_settings-extended_knees_model = Mô hình đầu gối mở rộng
settings-general-fk_settings-skeleton_settings-ratios = Tỷ lệ khung xương
settings-general-fk_settings-skeleton_settings-ratios-description = Thay đổi các giá trị của cài đặt bộ xương. Bạn có thể cần phải điều chỉnh tỷ lệ của bạn sau khi thay đổi những điều này.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Quy kết eo từ ngực đến hông
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Quy kết eo từ ngực đến hông
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Gán hông từ ngực đến chân
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Gán hông từ eo đến chân
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Tính trung bình số quay hông và lăn bằng chân.
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Tính trung bình của máy theo dõi đầu gối ngáp và lăn bằng mắt cá chân '
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Tính trung bình của chiều quay đầu gối ngáp và lăn bằng mắt cá chân '
settings-general-fk_settings-self_localization-title = Chế độ Mocap
settings-general-fk_settings-self_localization-description = Chế độ Mocap cho phép bộ xương theo dõi đại khái vị trí của chính nó mà không cần kính VR hoặc các thiết bị theo dõi khác. Lưu ý rằng điều này yêu cầu bộ theo dõi chân và đầu để hoạt động và chức năng này vẫn đang trong quá trình thử nghiệm.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Cử chỉ điều khiển
settings-general-gesture_control-subtitle = Chạm để đặt lại
settings-general-gesture_control-description = Cho phép chạm vào tracker để đặt lại vị trí, chạm hai lần vào tracker cao nhất ở thân để đặt lại nhanh, chạm hai lần vào tracker cao nhất ở chân trái để đặt lại, chạm hai lần vào tracker cao nhất ở chân phải để đặt lại vị trí gắn tracker. Cử chỉ chỉ được tiếp nhận khi thời gian giữa hai lần chạm ngắn hơn 0.6 giây.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tap
       *[other] { $amount } lần
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers = { $amount } trackers
settings-general-gesture_control-yawResetEnabled = Bật chạm để đặt lại chiều quay
settings-general-gesture_control-yawResetDelay = Thời gian delay trước khi đặt lại
settings-general-gesture_control-yawResetTaps = Số lần chạm để đặt lại chiều quay
settings-general-gesture_control-yawResetTracker = Yaw reset tracker
settings-general-gesture_control-fullResetEnabled = Bật chạm để đặt lại chiều quay
settings-general-gesture_control-fullResetDelay = Thời gian delay trước khi đặt lại full
settings-general-gesture_control-fullResetTaps = Số lần chạm để đặt lại full
settings-general-gesture_control-fullResetTracker = Full reset tracker
settings-general-gesture_control-mountingResetEnabled = Chạm để đặt lại hướng gắn tracker
settings-general-gesture_control-mountingResetDelay = Thời gian delay trước khi đặt lại hướng gắn tracker
settings-general-gesture_control-mountingResetTaps = Số lần chạm cho đặt lại hướng gắn tracker
settings-general-gesture_control-mountingResetTracker = Mounting reset tracker
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Số thiết bị có thể vượt phát hiện
settings-general-gesture_control-numberTrackersOverThreshold-description = Tăng số này nếu tính năng chạm thiết bị không hoạt động. Không tăng nó quá trên mức cần thiết để làm cho phát hiện chạm hoạt động vì nó sẽ gây ra nhiều kết quả sai hơn.

## Appearance settings

settings-interface-appearance = Giao diện
settings-general-interface-dev_mode = Chế độ nhà phát triển
settings-general-interface-dev_mode-description = Hữu dụng nếu cần thêm thông tin chi tiết của tracker hay can thiệp sâu hơn vào tracker
settings-general-interface-dev_mode-label = Chế độ nhà phát triển
settings-general-interface-theme = Màu giao diện
settings-general-interface-show-navbar-onboarding = Show "{ navbar-onboarding }" on navigation bar
settings-general-interface-show-navbar-onboarding-description = This changes whether the "{ navbar-onboarding }" button shows on the navigation bar.
settings-general-interface-show-navbar-onboarding-label = Show "{ navbar-onboarding }"
settings-general-interface-lang = Thay đổi ngôn ngữ (Change language)
settings-general-interface-lang-description = Đổi ngôn ngữ hiển thị (Change the default language you want to use)
settings-general-interface-lang-placeholder = Chọn ngôn ngữ để sử dụng (Select the language to use)
# Keep the font name untranslated
settings-interface-appearance-font = Phông chữ cho GUI
settings-interface-appearance-font-description = Cài đặt này thay đổi phông chữ được sử dụng bởi giao diện.
settings-interface-appearance-font-placeholder = Phông chữ mặc định
settings-interface-appearance-font-os_font = Phông chữ hệ điều hành
settings-interface-appearance-font-slime_font = Phông chữ mặc định
settings-interface-appearance-font_size = Tỷ lệ phông chữ cơ bản
settings-interface-appearance-font_size-description = Điều này ảnh hưởng đến kích thước phông chữ của toàn bộ giao diện ngoại trừ bảng cài đặt này.
settings-interface-appearance-decorations = Use the system native decorations
settings-interface-appearance-decorations-description = This will not render the top bar of the interface and will use the operating system's instead.
settings-interface-appearance-decorations-label = Use native decorations

## Notification settings

settings-interface-notifications = Thông báo
settings-general-interface-serial_detection = Nhận dạng thiết bị Serial mới
settings-general-interface-serial_detection-description = Hiển thị pop-up mỗi lần một thiết bị Serial mới được kết nối qua USB (có thể là tracker), giúp cải thiện quá trình thiết lập tracker
settings-general-interface-serial_detection-label = Nhận dạng thiết bị Serial mới
settings-general-interface-feedback_sound = Âm thanh phản hồi
settings-general-interface-feedback_sound-description = Tùy chọn này sẽ phát âm thanh khi thiết lập lại được kích hoạt.
settings-general-interface-feedback_sound-label = Âm thanh phản hồi
settings-general-interface-feedback_sound-volume = Âm lượng phản hồi
settings-general-interface-connected_trackers_warning = Cảnh báo với thiết bị đã kết nối
settings-general-interface-connected_trackers_warning-description = Tùy chọn này sẽ hiển thị cửa sổ bật lên mỗi khi bạn thử thoát khỏi SlimeVR trong khi có một hoặc nhiều thiết bị theo dõi được kết nối. Nó nhắc nhở bạn tắt trình theo dõi khi bạn hoàn tất để duy trì tuổi thọ pin.
settings-general-interface-connected_trackers_warning-label = Cảnh báo thiết bị đã kết nối khi thoát chương trình

## Behavior settings

settings-interface-behavior = Behavior
settings-general-interface-dev_mode = Developer Mode
settings-general-interface-dev_mode-description = This mode can be useful if you need in-depth data or need to interact with connected trackers on a more advanced level.
settings-general-interface-dev_mode-label = Developer Mode
settings-general-interface-use_tray = Thu nhỏ vào khay hệ thống
settings-general-interface-use_tray-description = Cho phép bạn đóng cửa sổ mà không cần đóng máy chủ SlimeVR để bạn có thể tiếp tục sử dụng nó mà không bị GUI làm phiền.
settings-general-interface-use_tray-label = Thu nhỏ vào khay hệ thống
settings-general-interface-discord_presence = Chia sẻ hoạt động trên Discord
settings-general-interface-discord_presence-description = Cho Discord của bạn biết rằng bạn đang sử dụng SlimeVR cùng với số lượng trình theo dõi IMU bạn đang sử dụng.
settings-general-interface-discord_presence-label = Chia sẻ hoạt động trên Discord
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Đang quay trên Slime
       *[other] Đang dùng { $amount } điểm full body
    }
settings-interface-behavior-error_tracking = Error collection via Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Do you consent to the collection of anonymized error data?</h1>
    
    <b>We do not collect personal information</b> such as your IP address or wireless credentials. SlimeVR values your privacy!
    
    To provide the best user experience, we collect anonymized error reports, performance metrics, and operating system information. This helps us detect bugs and issues with SlimeVR. These metrics are collected via Sentry.io.
settings-interface-behavior-error_tracking-label = Send errors to developers
settings-interface-behavior-bvh_directory = Directory to save BVH recordings
settings-interface-behavior-bvh_directory-description = Choose a directory to save your BVH recordings instead of having to choose where to save them each time.
settings-interface-behavior-bvh_directory-label = Directory for BVH recordings

## Serial settings

settings-serial = Cổng Serial
# This cares about multilines
settings-serial-description =
    Đây là cổng giao tiếp Serial trực tiếp với tracker
    Hữu dụng nếu cần kiểm tra tracker có hoạt động như mong muốn hay không
settings-serial-connection_lost = Kết nối đến Serial đã mất, đang kết nối lại...
settings-serial-reboot = Khởi động lại
settings-serial-factory_reset = Khôi phục cài đặt gốc
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Lưu ý:</b> Thao tác này sẽ đặt lại trình theo dõi về cài đặt gốc.
    Đặt lại bao gồm tất cả các cài đặt Wi-Fi và hiệu chuẩn (Calibrate) <b>sẽ bị mất!</b>
settings-serial-factory_reset-warning-ok = Tôi biết mình đang làm gì
settings-serial-factory_reset-warning-cancel = Hủy
settings-serial-serial_select = Chọn cổng Serial
settings-serial-auto_dropdown_item = Tự động
settings-serial-get_wifi_scan = Quét WiFi
settings-serial-enter_pairing = Enter Pairing
settings-serial-exit_pairing = Exit Pairing
settings-serial-calibrate = Calibrate
settings-serial-six_side_calibrate = 6-Side Calibrate
settings-serial-dfu = Enter DFU
settings-serial-meow = Meow!
settings-serial-file_type = Văn bản thô
settings-serial-save_logs = Lưu vào tệp
settings-serial-send_command = Send
settings-serial-send_command-placeholder = Command...
settings-serial-send_command-warning = <b>Warning:</b> Running serial commands can lead to data loss or brick the trackers.
settings-serial-send_command-warning-ok = I know what I'm doing
settings-serial-send_command-warning-cancel = Cancel

## OSC router settings

settings-osc-router = Router OSC
# This cares about multilines
settings-osc-router-description =
    Chuyển tiếp dữ liệu OSC từ phần mềm khác
    Hữu dụng cho việc sử dụng phần mềm OSC khác với VRChat, ...
settings-osc-router-enable = Chuyển tiếp dữ liệu OSC
settings-osc-router-enable-description = Cho phép chuyển tiếp dữ liệu OSC
settings-osc-router-enable-label = Chuyển tiếp dữ liệu OSC
settings-osc-router-network = Cổng mạng
# This cares about multilines
settings-osc-router-network-description =
    Đặt cổng nhận và gửi dữ liệu OSC
    Có thể dùng chung cổng với server SlimeVR
settings-osc-router-network-port_in = 
    .label = Cổng vào
    .placeholder = Cổng vào (Mặc định: 9002)
settings-osc-router-network-port_out = 
    .label = Cổng ra
    .placeholder = Cổng ra (Mặc định: 9000)
settings-osc-router-network-address = Địa chỉ mạng
settings-osc-router-network-address-description = Địa chỉ mạng mà SlimeVR sẽ gửi dữ liệu OSC đến
settings-osc-router-network-address-placeholder = Địa chỉ IPv4

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Thay đổi cài đặt cụ thể cho OSC Trackers được sử dụng để gửi
    dữ liệu đo đến các ứng dụng không có SteamVR (ví dụ: Quest Standalone).
    Hãy đảm bảo bật OSC trong VRChat thông qua Menu hành động trong OSC > Enabled.
    Để cho phép nhận kính thực tế ảo HMD và dữ liệu bộ điều khiển từ VRChat, hãy vào menu chính của bạn
    cài đặt trong Tracking & IK > Allow Sending Head and Wrist VR Tracking OSC Data.
settings-osc-vrchat-enable = Dữ liệu OSC
settings-osc-vrchat-enable-description = Cho phép nhận và gửi dữ liệu OSC
settings-osc-vrchat-enable-label = Giao tiếp dữ liệu OSC
settings-osc-vrchat-oscqueryEnabled = Enable OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery automatically detects running instances of VRChat and sends them data.
    It can also advertise itself to them in order to receive HMD and controller data.
    To allow receiving HMD and controller data from VRChat, go in your main menu's settings
    under "Tracking & IK" and enable "Allow Sending Head and Wrist VR Tracking OSC Data".
settings-osc-vrchat-oscqueryEnabled-label = Enable OSCQuery
settings-osc-vrchat-network = Cổng mạng
settings-osc-vrchat-network-description-v1 = Đặt các cổng để lấy và gửi dữ liệu. Có thể giữ nguyên cho VRChat.
settings-osc-vrchat-network-port_in = 
    .label = Cổng vào
    .placeholder = Cổng vào (Mặc định: 9001)
settings-osc-vrchat-network-port_out = 
    .label = Cổng ra
    .placeholder = Cổng ra (Mặc định: 9000)
settings-osc-vrchat-network-address = Địa chỉ mạng
settings-osc-vrchat-network-address-description-v1 = Chọn địa chỉ để gửi dữ liệu đến. Có thể giữ nguyên cho VRChat.
settings-osc-vrchat-network-address-placeholder = Địa chỉ IP của thiết bị chơi VRChat
settings-osc-vrchat-network-trackers = Cấu hình Tracker
settings-osc-vrchat-network-trackers-description = Chọn các tracker mà SlimeVR sẽ gửi dữ liệu OSC đến VRChat
settings-osc-vrchat-network-trackers-chest = Ngực
settings-osc-vrchat-network-trackers-hip = Hông
settings-osc-vrchat-network-trackers-knees = Đầu gối
settings-osc-vrchat-network-trackers-feet = Bàn chân
settings-osc-vrchat-network-trackers-elbows = Khuỷu tay

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Thay đổi cài đặt cụ thể cho giao thức VMC (Virtual Motion Capture)
        để gửi dữ liệu thân của SlimeVR và nhận dữ liệu thân từ các ứng dụng khác.
settings-osc-vmc-enable = Bật
settings-osc-vmc-enable-description = Cho phép nhận và gửi dữ liệu OSC
settings-osc-vmc-enable-label = Chuyển tiếp dữ liệu OSC
settings-osc-vmc-network = Cổng mạng (Network ports)
settings-osc-vmc-network-description = Đặt các cổng để nghe và gửi dữ liệu qua VMC.
settings-osc-vmc-network-port_in = 
    .label = Cổng vào (Port In)
    .placeholder = Port in (default: 39540)
settings-osc-vmc-network-port_out = 
    .label = Cổng ra (Port out)
    .placeholder = Port out (default: 39539)
settings-osc-vmc-network-address = Địa chỉ mạng (Network address)
settings-osc-vmc-network-address-description = Chọn địa chỉ để gửi dữ liệu qua VMC.
settings-osc-vmc-network-address-placeholder = Địa chỉ IPV4
settings-osc-vmc-vrm = Model VRM
settings-osc-vmc-vrm-description = Tải mô hình VRM để cho phép neo đầu và cho phép khả năng tương thích cao hơn với các ứng dụng khác.
settings-osc-vmc-vrm-untitled_model = Untitled model
settings-osc-vmc-vrm-file_select = Kéo và thả mô hình để sử dụng hoặc <u>duyệt file</u>
settings-osc-vmc-anchor_hip = Cố định ở hông
settings-osc-vmc-anchor_hip-description = Cố định theo dõi ở hông, hữu ích cho VTubing ngồi. Nếu tắt, hãy tải mô hình VRM.
settings-osc-vmc-anchor_hip-label = Cố định ở hông
settings-osc-vmc-mirror_tracking = Phản chiếu ngược theo dõi cơ thể
settings-osc-vmc-mirror_tracking-description = Phản chiếu theo dõi theo chiều ngang.
settings-osc-vmc-mirror_tracking-label = Phản chiếu ngược theo dõi cơ thể

## Common OSC settings

settings-osc-common-network-ports_match_error = The OSC Router in and out ports can't be the same!
settings-osc-common-network-port_banned_error = The port { $port } can't be used!

## Advanced settings

settings-utils-advanced = Cài đặt mở rộng
settings-utils-advanced-reset-gui = Đặt lại cài đặt GUI
settings-utils-advanced-reset-gui-description = Restore the default settings for the interface.
settings-utils-advanced-reset-gui-label = Reset GUI
settings-utils-advanced-reset-server = Reset tracking settings
settings-utils-advanced-reset-server-description = Restore the default settings for the tracking.
settings-utils-advanced-reset-server-label = Reset tracking
settings-utils-advanced-reset-all = Reset all settings
settings-utils-advanced-reset-all-description = Restore the default settings for both the interface and tracking.
settings-utils-advanced-reset-all-label = Đặt lại tất cả
settings-utils-advanced-reset_warning =
    <b>Warning:</b> This will reset { $type ->
        [gui] your GUI
        [server] your tracking
       *[all] all your
    } settings to the defaults.
    Are you sure you want to do this?
settings-utils-advanced-reset_warning-reset = Đặt lại cài đặt
settings-utils-advanced-reset_warning-cancel = Hủy
settings-utils-advanced-open_data-v1 = Config folder
settings-utils-advanced-open_data-description-v1 = Open SlimeVR's config folder in file explorer, containing the configuration
settings-utils-advanced-open_data-label = Mở thư mục
settings-utils-advanced-open_logs = Logs folder
settings-utils-advanced-open_logs-description = Open SlimeVR's logs folder in file explorer, containing the logs of the app
settings-utils-advanced-open_logs-label = Open folder

## Home Screen

settings-home-list-layout = Trackers list layout
settings-home-list-layout-desc = Select one of the possible layouts of the home screen
settings-home-list-layout-grid = Grid
settings-home-list-layout-table = Table

## Tracking Checlist

settings-tracking_checklist-active_steps = Active Steps
settings-tracking_checklist-active_steps-desc = List of all the steps in the tracking checklist. You can choose to disable specific steps.

## Setup/onboarding menu

onboarding-skip = Bỏ qua cài đặt
onboarding-continue = Tiếp tục
onboarding-wip = Chưa hoàn thiện
onboarding-previous_step = Quay lại
onboarding-setup_warning =
    <b>Lưu ý:</b> Trình thiết lập ban đầu là cần thiết để theo dõi tốt,
    bước này cần thiết nếu đây là lần đầu tiên bạn sử dụng SlimeVR.
onboarding-setup_warning-skip = Bỏ qua cài đặt
onboarding-setup_warning-cancel = Tiếp tục thiết lập

## Quiz

onboarding-quiz_continue = Continue
onboarding-quiz_back = Back
onboarding-quiz-more_sets_modal-title = Have you connected all of your trackers?
onboarding-quiz-more_sets_modal-desc = If you have sets of different models, we can connect them right now!
onboarding-quiz-more_sets_modal-confirm = I have connected all my trackers
onboarding-quiz-more_sets_modal-cancel = I want to connect more trackers
onboarding-quiz-slimeset-title = What type of trackers are you connecting?
onboarding-quiz-slimeset-description = If you have multiple sets, you will be asked again later in the process
onboarding-quiz-slimeset-official-sets = Official SlimeVR Trackers
onboarding-quiz-slimeset-thirdparty-sets = Third-party or DIY Trackers
onboarding-quiz-slimeset-answer-regular = SlimeVR V1.0 & V1.2
onboarding-quiz-slimeset-answer-butterfly = Butterfly
onboarding-quiz-slimeset-answer-wifi = WiFi-based Slime
onboarding-quiz-slimeset-answer-dongle = Dongle-based Slime
onboarding-quiz-usage-title = What are you using your trackers for?
onboarding-quiz-usage-description = If you plan on using SlimeVR for multiple purposes, you can change the affected settings later.
onboarding-quiz-usage-answer-VRC = VR Gaming (e.g. VRChat)
onboarding-quiz-usage-answer-mocap_vtubing = Mocap and VTubing
onboarding-quiz-runtime-title = Do you run games via SteamVR, or on the headset itself (standalone)?
onboarding-quiz-runtime-answer-steamvr = SteamVR
onboarding-quiz-runtime-answer-standalone = Standalone
onboarding-quiz-mocap_preferences-title = Mocap Preferences
onboarding-quiz-mocap_preferences-desc = Specify how you plan to use SlimeVR for mocap or VTubing
onboarding-quiz-mocap_preferences-playspace-title = What is your playspace?
onboarding-quiz-mocap_preferences-playspace-desc = If standing, SlimeVR will try to track walking movement instead of anchoring you in one spot.
onboarding-quiz-mocap_preferences-playspace-sitting = Sitting
onboarding-quiz-mocap_preferences-playspace-standing = Standing
onboarding-quiz-mocap_preferences-vrm_model-title = Do you have a VRM model? (Optional)
onboarding-quiz-mocap_preferences-vrm_model-desc = Loading a VRM model will improve tracking quality and compatibility with applications that use VMC.
onboarding-quiz-mocap_preferences-head_tracker-title = Are you wearing a tracker or VR headset on your head?
onboarding-quiz-mocap_preferences-head_tracker-yes = Yes
onboarding-quiz-mocap_preferences-head_tracker-no = No
onboarding-quiz-mocap_preferences-head_tracker_location-title = Where is your head tracker located?
onboarding-quiz-mocap_preferences-head_tracker_location-forehead = Forehead
onboarding-quiz-mocap_preferences-head_tracker_location-face = Face

## Wi-Fi setup

onboarding-wifi_creds-back-v2 = Go back
onboarding-wifi_creds-v2 = Trackers using Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description-v2 =
    Most trackers (such as official SlimeVR trackers) use Wi-Fi to connect to the server.
    Please use the credentials of the Wi-Fi network your device is currently connected to.
    
    Make sure to use a 2.4GHz Wi-Fi connection for your trackers!
onboarding-wifi_creds-skip = Bỏ qua cài đặt Wi-Fi
onboarding-wifi_creds-submit = Gửi
onboarding-wifi_creds-ssid = 
    .label = Tên hiển thị
    .placeholder = Nhập tên Wi-Fi
onboarding-wifi_creds-ssid-required = Wi-Fi name is required
onboarding-wifi_creds-password = 
    .label = Mật khẩu
    .placeholder = và mật khẩu
onboarding-wifi_creds-dongle-title = Trackers using a dongle
onboarding-wifi_creds-dongle-description = If your trackers came with a dongle, plug it into your device and you should be good to go!
onboarding-wifi_creds-dongle-wip = This section is a work in progress. A dedicated page to manage trackers that connect via a dongle will be made soon.
onboarding-wifi_creds-dongle-continue = Continue with a dongle

## Mounting setup

onboarding-reset_tutorial-back = Quay lại cân chỉnh vị trí gắn tracker
onboarding-reset_tutorial = Làm lại
onboarding-reset_tutorial-explanation = Trong khi bạn sử dụng trình theo dõi của mình, tracker có thể bị lệch khỏi căn chỉnh do IMU bị trượt, trôi dạt hoặc vì bạn có thể đã di chuyển chúng về mặt vật lý. Bạn có một số cách để khắc phục điều này.
onboarding-reset_tutorial-skip = Bỏ qua bước
# Cares about multiline
onboarding-reset_tutorial-0 =
    Nhấn { $taps } lần thiết bị được đánh dấu để kích hoạt đặt lại chiều quay.
    
    Điều này sẽ làm cho các trình theo dõi quay mặt về cùng hướng với kính thực thế ảo (HMD) của bạn.
# Cares about multiline
onboarding-reset_tutorial-1 =
    Nhấn vào { $taps } lần thiết bị được đánh dấu để kích hoạt đặt lại toàn bộ.
    
    Bạn cần phải đứng thẳng tay để làm việc này (i-pose). Có độ trễ 3 giây (có thể định cấu hình) trước khi nó thực sự xảy ra.
    Điều này đặt lại hoàn toàn vị trí và xoay của tất cả các trình theo dõi của bạn. Nó sẽ khắc phục hầu hết các vấn đề.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Nhấn vào { $taps } lần thiết bị được đánh dấu để kích hoạt đặt lại lắp.
    
    Thiết lập lại gắn kết giúp ích cho cách các trình theo dõi thực sự được đặt vào bạn, vì vậy nếu bạn vô tình di chuyển chúng và thay đổi cách chúng được định hướng với số lượng lớn, điều này sẽ hữu ích.
    
    Bạn cần phải ở trên một tư thế như bạn đang trượt tuyết như nó được hiển thị trên trình hướng dẫn gắn tự động và bạn có độ trễ 3 giây (có thể định cấu hình) trước khi nó được kích hoạt.

## Install info

install-info_udev-rules_modal_title = Hardware udev access rules not found
install-info_udev-rules_warning = Access rules via udev are required for serial console access & dongle connection. Paste the following command into your terminal to add the udev rules.
install-info_udev-rules_modal_button = Close
install-info_udev-rules_modal-dont-show-again_checkbox = Don't show again

## Setup start

onboarding-home = Chào mừng bạn đến với SlimeVR!
onboarding-home-start = Bắt đầu thiết lập!

## Setup done

onboarding-done-title = Hoàn thành!
onboarding-done-description = Bạn đã hoàn tất quá trình thiết lập cơ bản
onboarding-done-close = Đóng hướng dẫn

## Tracker connection setup

onboarding-connect_tracker-back = Quay lại cài đặt Wi-Fi
onboarding-connect_tracker-title = Kết nối tracker
onboarding-connect_tracker-description-p0-v1 = Bây giờ vào phần thú vị, kết nối thiết bị!
onboarding-connect_tracker-description-p1-v1 = Kết nối từng thiết bị một lần thông qua cổng USB.
onboarding-connect_tracker-issue-serial = Có vấn đề với việc kết nối? Kiểm tra thông tin qua cổng Serial
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Đang tìm tracker
onboarding-connect_tracker-connection_status-serial_init = Kết nối với thiết bị nối tiếp serial
onboarding-connect_tracker-connection_status-obtaining_mac_address = Obtaining the tracker mac address
onboarding-connect_tracker-connection_status-provisioning = Đang gửi thông tin Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Đang gửi thông tin Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Đang tìm máy chủ
onboarding-connect_tracker-connection_status-connection_error = Không thể kết nối đến Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Không thể tìm thấy máy chủ
onboarding-connect_tracker-connection_status-done = Đã kết nối đến máy chủ SlimeVR
onboarding-connect_tracker-connection_status-no_serial_log = Could not get logs from the tracker
onboarding-connect_tracker-connection_status-no_serial_device_found = Could not find a tracker from USB
onboarding-connect_serial-error-modal-no_serial_log = Is the tracker turned on?
onboarding-connect_serial-error-modal-no_serial_log-desc = Make sure the tracker is turned on and connected to your computer.
onboarding-connect_serial-error-modal-no_serial_device_found = No trackers detected
onboarding-connect_serial-error-modal-no_serial_device_found-desc =
    Please connect a tracker with the provided USB cable to your computer and turn the tracker on.
    If this does not work:
      - try using a different USB cable
      - try using a different USB port
      - try reinstalling the SlimeVR server and select "USB Drivers" in the components section
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Không có tracker đã giao
        [one] 1 tracker đã giao
       *[other] { $amount } tracker đã giao
    }
onboarding-connect_tracker-next = Đã kết nối với tất cả tracker

## Tracker calibration tutorial

onboarding-calibration_tutorial = Hướng dẫn hiệu chuẩn IMU
onboarding-calibration_tutorial-subtitle = Cái này sẽ giúp giảm trôi trượt theo dõi!
onboarding-calibration_tutorial-description-v1 = After turning on your trackers, place them on a stable surface for a moment to allow for calibration. Calibration can be performed at any time after the trackers are powered on—this page simply provides a tutorial. To begin, click the "{ onboarding-calibration_tutorial-calibrate }" button, then <b>do not move your trackers!</b>
onboarding-calibration_tutorial-calibrate = Tôi đã đặt thiết bị theo dõi của mình lên bàn
onboarding-calibration_tutorial-status-waiting = Đang chờ bạn hoàn thành
onboarding-calibration_tutorial-status-calibrating = Đang hiệu chuẩn
onboarding-calibration_tutorial-status-success = Nice!
onboarding-calibration_tutorial-status-error = Thiết bị đã di chuyển
onboarding-calibration_tutorial-skip = Bỏ qua hướng dẫn

## Tracker assignment tutorial

onboarding-assignment_tutorial = Làm thế nào để chuẩn bị một Slime Tracker trước khi đưa nó vào
onboarding-assignment_tutorial-first_step = 1. Đặt nhãn dán bộ phận cơ thể (nếu có) trên tracker theo lựa chọn của bạn
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Sticker
onboarding-assignment_tutorial-second_step-v2 = 2. Gắn dây đeo vào thiết bị theo dõi của bạn, giữ cho mặt Velcro của dây đeo hướng cùng hướng với hướng thiết bị của trình theo dõi của bạn:
onboarding-assignment_tutorial-second_step-continuation-v2 = Mặt Velcro cho extension phải hướng lên như hình ảnh sau:
onboarding-assignment_tutorial-done = Tôi đã dán nhãn dán và dây đai!

## Tracker assignment setup

onboarding-assign_trackers-back = Quay lại cài đặt Wi-Fi
onboarding-assign_trackers-title = Gán tracker
onboarding-assign_trackers-description = Chọn vị trí bạn muốn gán tracker bằng cách nhấn vào tên bộ phận muốn gán và chọn tracker
onboarding-assign_trackers-unassign_all = Unassign all trackers
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } trên 1 tracker đã giao
       *[other] { $assigned } trên { $trackers } tracker đã giao
    }
onboarding-assign_trackers-advanced = Xem thêm vị trí đặt
onboarding-assign_trackers-next = Hoàn thành
onboarding-assign_trackers-mirror_view = Xem hình phản chiếu
onboarding-assign_trackers-option-amount = x{ $trackersCount }
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] Tất cả thiết bị
        [core] { "" }
        [enhanced-core] { "" }
        [full-body] { "" }
       *[all] { "" }
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] Tối thiều để có full body tracking
        [core] + Thân eo
        [enhanced-core] + Quay bàn chân
        [full-body] + Khửu tay
       *[all] Tất cả thiết bị được giao
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [6] Bàn chân trái được xác định nhưng bạn cần thêm chân dưới kèm theo để hoạt động!
        [5] Bàn chân trái được xác định nhưng bạn cần thêm chân trên kèm theo để hoạt động!
        [4] Bàn chân trái được xác định nhưng bạn cần thêm chân trên và chân dưới kèm theo để hoạt động!
        [3] Bàn chân trái được xác định nhưng bạn cần thêm ngực, eo hoặc bụng kèm theo để hoạt động!
        [2] Bàn chân trái được xác định nhưng bạn cần thêm chân trên, ngực, eo hoặc bụng kèm theo để hoạt động!
        [1] Bàn chân trái được xác định nhưng bạn cần thêm chân dưới, ngực, eo hoặc bụng kèm theo để hoạt động!
        [0] Bàn chân trái được xác định nhưng bạn cần thêm chân trên, chân dưới, ngực, eo hoặc bụng kèm theo để hoạt động!
       *[unknown] Bàn chân trái được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] Bàn chân phải được xác định nhưng bạn cần thêm chân trên, chân dưới, ngực, eo hoặc bụng kèm theo để hoạt động!
        [1] Bàn chân phải được xác định nhưng bạn cần thêm chân dưới, ngực, eo hoặc bụng kèm theo để hoạt động!
        [2] Bàn chân phải được xác định nhưng bạn cần thêm chân trên, ngực, eo hoặc bụng kèm theo để hoạt động!
        [3] Bàn chân phải được xác định nhưng bạn cần thêm ngực, eo hoặc bụng kèm theo để hoạt động!
        [4] Bàn chân phải được xác định nhưng bạn cần thêm chân trên và chân dưới kèm theo để hoạt động!
        [5] Bàn chân phải được xác định nhưng bạn cần thêm chân trên kèm theo để hoạt động!
        [6] Bàn chân phải được xác định nhưng bạn cần thêm chân dưới kèm theo để hoạt động!
       *[unknown] Bàn chân phải được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] Chân trên trái được xác định nhưng cần thêm chân trên!
        [1] Chân trên trái được xác định nhưng cần thêm ngực, eo và bụng được giao!
        [2] Chân trên trái được xác định nhưng cần thêm chân trên!
       *[unknown] Chân trên trái được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] Chân trên phải được xác định nhưng cần thêm chân trên!
        [1] Chân trên phải được xác định nhưng cần thêm ngực, eo và bụng được giao!
        [2] Chân trên phải được xác định nhưng cần thêm chân trên!
       *[unknown] Chân trên phải được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] Chân trái trên được xác định nhưng cần thêm ngực, eo hoặc bụng!
       *[unknown] Chân trái trên được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] Chân phải trên được xác định nhưng cần thêm ngực, eo hoặc bụng!
       *[unknown] Chân phải trên được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] Eo được xác định được xác định nhưng cần thêm ngực!
       *[unknown] Eo được xác định được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] Bụng được xác định nhưng cần thêm ngực!
       *[unknown] Bụng được xác định được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }

## Tracker mounting method choose

onboarding-choose_mounting = Bạn muốn dùng phương pháp hiểu chuần nào?
# Multiline text
onboarding-choose_mounting-description = Hướng lắp đặt chính xác cho vị trí của thiết bị theo dõi trên cơ thể của bạn.
onboarding-choose_mounting-auto_mounting = Cân chỉnh tự động
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Khuyến khích dùng
onboarding-choose_mounting-auto_mounting-description = Điều này sẽ tự động phát hiện các hướng lắp đặt cho tất cả các thiết bị của bạn từ 2 tư thế
onboarding-choose_mounting-manual_mounting = Cân chỉnh thủ công
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Có thể không đủ chính xác
onboarding-choose_mounting-manual_mounting-description = Điều này sẽ cho phép bạn chọn hướng lắp theo cách thủ công cho từng thiết bị
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Bạn có chắc chắn muốn
    đo hướng quay tự động?
onboarding-choose_mounting-manual_modal-description = <b>Hiệu chuẩn lắp thủ công được khuyến nghị cho người dùng mới</b>, vì các tư thế của hiệu chuẩn lắp tự động có thể khó thực hiện ngay trước và có thể cần một số thực hành.
onboarding-choose_mounting-manual_modal-confirm = Tôi chắc chắn về những gì tôi đang làm
onboarding-choose_mounting-manual_modal-cancel = Hủy

## Tracker manual mounting setup

onboarding-manual_mounting-back = Quay lại chuẩn bị cân chỉnh
onboarding-manual_mounting = Cân chỉnh thủ công
onboarding-manual_mounting-description = Chọn từng tracker và chọn hướng nó được gắn
onboarding-manual_mounting-auto_mounting = Cân chỉnh tự động
onboarding-manual_mounting-next = Tiếp tục

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Quay lại chuẩn bị cân chỉnh
onboarding-automatic_mounting-title = Cân chỉnh hướng gắn
onboarding-automatic_mounting-description = SlimeVR cần biết hướng gắn thực của tracker để hoạt động đúng, làm theo hướng dẫn để cân chỉnh hướng gắn tự động
onboarding-automatic_mounting-manual_mounting = Cân chỉnh thủ công
onboarding-automatic_mounting-next = Tiếp tục
onboarding-automatic_mounting-prev_step = Quay lại
onboarding-automatic_mounting-done-title = Đã cân chỉnh hướng gắn
onboarding-automatic_mounting-done-description = Cài đặt vị trí đã hoàn thành!
onboarding-automatic_mounting-done-restart = Thử lại
onboarding-automatic_mounting-mounting_reset-title = Đặt lại hướng gắn
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Đứng khom người như tư thế trượt tuyết với đầu gối khom lại, thân trên hướng tới trước và hai tay co lại để giữ thăng bằng như hình bên
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Nhấn nút đặt lại và chờ 3 giây trước khi hệ thống cân chỉnh hướng gắn tracker
onboarding-automatic_mounting-mounting_reset-feet-step-0 = 1. Stand on your toes with both feet pointing forward. Alternatively you can do it sitting on a chair.
onboarding-automatic_mounting-mounting_reset-feet-step-1 = 2. Press the "Feet calibration" button and wait for 3 seconds before the trackers' mounting orientations will reset.
onboarding-automatic_mounting-preparation-title = Chuẩn bị tư thế
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Press the "Full Reset" button.
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Stand upright with your arms to your sides. Make sure to look forward.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Hold the position until the 3s timer ends.
onboarding-automatic_mounting-put_trackers_on-title = Đeo tracker lên người
onboarding-automatic_mounting-put_trackers_on-description = Để cân chỉnh hướng gắn của tracker, SlimeVR sẽ tiến hành đo góc nghiêng của tracker khi đang đeo để cân chỉnh hướng gắn, hãy đeo tracker theo đúng vị trí đã thiết lập
onboarding-automatic_mounting-put_trackers_on-next = Tiếp tục
onboarding-automatic_mounting-return-home = Done

## Tracker manual proportions setupa

onboarding-manual_proportions-back-scaled = Go back to Scaled Proportions
onboarding-manual_proportions-title = Đo kích thước cơ thể thủ công
onboarding-manual_proportions-fine_tuning_button = Automatically fine tune proportions
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Please connect a VR headset to use automatic fine tuning
onboarding-manual_proportions-export = Export proportions
onboarding-manual_proportions-import = Import proportions
onboarding-manual_proportions-file_type = Body proportions file
onboarding-manual_proportions-normal_increment = Normal increment
onboarding-manual_proportions-precise_increment = Precise increment
onboarding-manual_proportions-grouped_proportions = Grouped proportions
onboarding-manual_proportions-all_proportions = All proportions
onboarding-manual_proportions-estimated_height = Estimated user height

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Quay lại cân chỉnh hướng gắn
onboarding-automatic_proportions-title = Đo kích thước cơ thể
onboarding-automatic_proportions-description = Để tracker có vị trí chính xác, SlimeVR cần biết các chỉ số kích thước cơ thể, làm theo hướng dẫn để đo kích thước cơ thể tự động
onboarding-automatic_proportions-manual = Đo kích thước cơ thể thủ công
onboarding-automatic_proportions-prev_step = Quay lại
onboarding-automatic_proportions-put_trackers_on-title = Đeo tracker lên người
onboarding-automatic_proportions-put_trackers_on-description = Để đo kích thước cơ thể, SlimeVR sẽ sử dụng một thuật toán để dự đoán kích thước của các bộ phận, hãy đeo tracker theo đúng vị trí đã thiết lập như hình bên
onboarding-automatic_proportions-put_trackers_on-next = Tiếp tục
onboarding-automatic_proportions-requirements-title = Yêu cầu
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    Bạn có ít nhất đủ tracker để theo dõi bàn chân của mình (thường là 5 tracker).
    Bạn đã bật tracker và headset và đang đeo chúng.
    Tracker và headset của bạn được kết nối với máy chủ SlimeVR và đang hoạt động bình thường (ví dụ: không bị lag, ngắt kết nối, v.v.).
    Headset của bạn đang báo cáo dữ liệu vị trí cho máy chủ SlimeVR (điều này thường có nghĩa là SteamVR đang chạy và kết nối với SlimeVR bằng driver SteamVR của SlimeVR).
    Tracking của bạn đang hoạt động và thể hiện chính xác các chuyển động của bạn (ví dụ: bạn đã thực hiện thiết đặt lại hoàn toàn và chúng di chuyển đúng hướng khi đá, cúi xuống, ngồi, v.v.).
onboarding-automatic_proportions-requirements-next = Tôi đã đọc các yêu cầu
onboarding-automatic_proportions-check_height-title-v3 = Measure your headset height
onboarding-automatic_proportions-check_height-description-v2 = Your headset (HMD) height should be slightly less than your full height because headsets measure your eye height. This measurement will be used as a baseline for your body proportions.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = Start measuring while standing <u>upright</u> to measure your height. Be careful not to raise your hands higher than your headset, as they may affect the measurement!
onboarding-automatic_proportions-check_height-guardian_tip =
    Nếu bạn đang sử dụng Kính VR Standalone, hãy đảm bảo có guardian /
    Ranh giới được bật để chiều cao của bạn là chính xác!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Không rõ
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = Your headset height is:
onboarding-automatic_proportions-check_height-measure-start = Start measuring
onboarding-automatic_proportions-check_height-measure-stop = Stop measuring
onboarding-automatic_proportions-check_height-measure-reset = Retry measuring
onboarding-automatic_proportions-check_height-next_step = Những chỉ số này là đúng
onboarding-automatic_proportions-check_floor_height-title = Measure your floor height (optional)
onboarding-automatic_proportions-check_floor_height-description = In some cases, your floor height may not be set correctly by your headset, causing the headset height to be measured as higher than it should be. You can measure the "height" of your floor to correct your headset height.
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = Start measuring and put a controller against your floor to measure its height. If you are sure that your floor height is correct, you can skip this step.
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = Your floor height is:
onboarding-automatic_proportions-check_floor_height-full_height = Your estimated full height is:
onboarding-automatic_proportions-check_floor_height-measure-start = Start measuring
onboarding-automatic_proportions-check_floor_height-measure-stop = Stop measuring
onboarding-automatic_proportions-check_floor_height-measure-reset = Retry measuring
onboarding-automatic_proportions-check_floor_height-skip_step = Skip step and save
onboarding-automatic_proportions-check_floor_height-next_step = Use floor height and save
onboarding-automatic_proportions-start_recording-title = Chuẩn bị đo
onboarding-automatic_proportions-start_recording-description = Phần mềm sẽ đo một số chuyển động, cử chỉ cụ thể, hãy chuẩn bị cho việc di chuyển theo yêu cầu trong phần tiếp theo
onboarding-automatic_proportions-start_recording-next = Bắt đầu
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Đang ghi...
onboarding-automatic_proportions-recording-description-p1 = Thực hiện các thao tác sau:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Đứng thẳng, xoay đầu một vòng tròn.
    Cong lưng về phía trước và ngồi xổm. Trong khi ngồi xổm, nhìn sang trái, sau đó sang phải.
    Xoay phần thân trên của bạn sang trái (ngược chiều kim đồng hồ), sau đó đưa tay xuống đất.
    Xoay phần thân trên của bạn sang phải (theo chiều kim đồng hồ), sau đó với tay xuống đất.
    Xoay hông của bạn theo chuyển động tròn như thể bạn đang xoay vòng hula.
    Nếu còn thời gian, bạn có thể lặp lại các bước này cho đến khi kết thúc.
onboarding-automatic_proportions-recording-processing = Đang xử lí kết quả...
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 second
       *[other] còn lại { $time } giây
    }
onboarding-automatic_proportions-verify_results-title = Kiểm tra kết quả
onboarding-automatic_proportions-verify_results-description = So sánh kết quả với chỉ số thực, Lưu ý: kết quả chỉ mang tính tương đối
onboarding-automatic_proportions-verify_results-results = Kết quả đo
onboarding-automatic_proportions-verify_results-processing = Đang xử lí kết quả...
onboarding-automatic_proportions-verify_results-redo = Thử lại
onboarding-automatic_proportions-verify_results-confirm = Kết quả tương đối chính xác
onboarding-automatic_proportions-done-title = Đã lưu chỉ số đo
onboarding-automatic_proportions-done-description = Quá trình đo đã hoàn tất
onboarding-automatic_proportions-error_modal-v2 =
    <b>Warning:</b> There was an error while estimating proportions!
    This is likely a mounting calibration issue. Make sure your tracking works properly before trying again.
    Please <docs>check the docs</docs> or join our <discord>Discord</discord> for help ^_^
onboarding-automatic_proportions-error_modal-confirm = Đã hiểu!
onboarding-automatic_proportions-smol_warning =
    Your configured height of { $height } is smaller than the minimum accepted height of { $minHeight }.
    <b>Please redo the measurements and ensure they are correct.</b>
onboarding-automatic_proportions-smol_warning-cancel = Go back

## User height calibration

onboarding-user_height-title = What is your height?
onboarding-user_height-description = We need your height to calculate your body proportions and accurately represent your movements. You can either let SlimeVR calculate it, or input your height manually.
onboarding-user_height-need_head_tracker = A headset and controllers with positional tracking are required to perform the calibration.
onboarding-user_height-calculate = Calculate my height automatically
onboarding-user_height-next_step = Continue and save
onboarding-user_height-prev_step = Back
onboarding-user_height-manual-proportions = Manual Proportions
onboarding-user_height-calibration-title = Calibration Progress
onboarding-user_height-calibration-RECORDING_FLOOR = Touch the floor with the tip of your controller
onboarding-user_height-calibration-WAITING_FOR_RISE = Stand back up
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK = Stand back up and look forward
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-ok = Make sure your head is leveled
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-low = Do not look at the floor
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-high = Do not look too high up
onboarding-user_height-calibration-WAITING_FOR_CONTROLLER_PITCH = Make sure the controller is pointing down
onboarding-user_height-calibration-RECORDING_HEIGHT = Stand back up and stand still!
onboarding-user_height-calibration-DONE = Success!
onboarding-user_height-calibration-ERROR_TIMEOUT = Calibration timed out, try again.
onboarding-user_height-calibration-ERROR_TOO_HIGH = The detected user height is too high, try again.
onboarding-user_height-calibration-ERROR_TOO_SMALL = The detected user height is too small. Make sure to stand straight and look forward at the end of the calibration.
onboarding-user_height-calibration-error = Calibration Failed
onboarding-user_height-manual-tip = While adjusting your height, try different poses and see how the skeleton matches your body.
onboarding-user_height-reset-warning =
    <b>Warning:</b> This will reset your proportions to be based on your height.
    Are you sure you want to do this?

## Stay Aligned setup

onboarding-stay_aligned-title = Stay Aligned
onboarding-stay_aligned-description = Configure Stay Aligned to keep your trackers aligned.
onboarding-stay_aligned-put_trackers_on-title = Put on your trackers
onboarding-stay_aligned-put_trackers_on-description = To save your resting poses, we'll use the trackers you just assigned. Put on all your trackers, you can see which are which in the figure to the right.
onboarding-stay_aligned-put_trackers_on-trackers_warning = You have fewer than 5 trackers currently connected and assigned! This is the minimum amount of trackers required for Stay Aligned to function properly.
onboarding-stay_aligned-put_trackers_on-next = I have all my trackers on
onboarding-stay_aligned-verify_mounting-title = Mounting Calibration
onboarding-stay_aligned-verify_mounting-step-0 = Stay Aligned requires good mounting. Otherwise, you won't get a good experience with Stay Aligned.
onboarding-stay_aligned-verify_mounting-step-1 = 1. Move around while standing.
onboarding-stay_aligned-verify_mounting-step-2 = 2. Sit down and move your legs and feet.
onboarding-stay_aligned-verify_mounting-step-3 = 3. If your trackers aren't in the right place, press "Redo Mounting Calibration".
onboarding-stay_aligned-verify_mounting-redo_mounting = Redo Mounting calibration
onboarding-stay_aligned-preparation-title = Preparation
onboarding-stay_aligned-preparation-tip = Make sure to stand upright. Keep looking forward with your arms down at your sides.
onboarding-stay_aligned-relaxed_poses-standing-title = Relaxed Standing Pose
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Stand in a comfortable position. Relax!
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 2. Press the "Save pose" button.
onboarding-stay_aligned-relaxed_poses-sitting-title = Relaxed Sitting in Chair Pose
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. Sit in a comfortable position. Relax!
onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2 = 2. Press the "Save pose" button.
onboarding-stay_aligned-relaxed_poses-flat-title = Relaxed Sitting on Floor Pose
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. Sit on the floor with your legs in front. Relax!
onboarding-stay_aligned-relaxed_poses-flat-step-1-v2 = 2. Press the "Save pose" button.
onboarding-stay_aligned-relaxed_poses-skip_step = Skip
onboarding-stay_aligned-done-title = Stay Aligned enabled!
onboarding-stay_aligned-done-description = Your Stay Aligned setup is complete!
onboarding-stay_aligned-done-description-2 = Setup is complete! You may restart the process if you want to recalibrate the poses.
onboarding-stay_aligned-previous_step = Previous
onboarding-stay_aligned-next_step = Next
onboarding-stay_aligned-restart = Restart
onboarding-stay_aligned-done = Done
onboarding-stay_aligned-manual_mounting-done = Done

## Home

home-no_trackers = Chưa có thiết bị nào được phát hiện hoặc điều ra
home-settings = Home Page Settings
home-settings-close = Close

## Trackers Still On notification

trackers_still_on-modal-title = Tracker vẫn còn bật
trackers_still_on-modal-description =
    Vẫn còn một hoặc nhiều tracker vẫn đang bật.
    Bạn vẫn muốn thoát khỏi SlimeVR?
trackers_still_on-modal-confirm = Thoát SlimeVR
trackers_still_on-modal-cancel = Vui lòng đợi...

## Status system

status_system-StatusTrackerReset = Bạn nên thực hiện thiết lập lại toàn bộ vì một hoặc nhiều trình theo dõi không được điều chỉnh.
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] Chưa kết nối với SteamVR thông qua trình điều khiển SlimeVR Feeder App.
       *[other] Chưa kết nối với SteamVR thông qua driver SlimeVR.
    }
status_system-StatusTrackerError = Tracker { $trackerName } có lỗi.
status_system-StatusUnassignedHMD = Kính thực tế ảo VR này nên được giao là bộ theo dõi đầu.
status_system-StatusPublicNetwork =
    { $count ->
        [one] Your network profile is currently set to Public ({ $adapters }). This is not recommended for SlimeVR to function properly. <PublicFixLink>See how to fix it here.</PublicFixLink>
       *[many] Some of your network adapters are set to public: { $adapters }. This is not recommended for SlimeVR to function properly. <PublicFixLink>See how to fix it here.</PublicFixLink>
    }

## Firmware tool globals

firmware_tool-next_step = Next Step
firmware_tool-previous_step = Previous Step
firmware_tool-ok = Looks good
firmware_tool-retry = Retry
firmware_tool-loading = Loading...

## Firmware tool Steps

firmware_tool = DIY Firmware tool
firmware_tool-description = Allows you to configure and flash your DIY trackers
firmware_tool-not_available = Oops, the firmware tool is not available at the moment. Come back later!
firmware_tool-not_compatible = The firmware tool is not compatible with this version of the server. Please update your server!
firmware_tool-select_source = Select the firmware to flash
firmware_tool-select_source-description = Select the firmware you want to flash on your board
firmware_tool-select_source-error = Unable to load Sources
firmware_tool-select_source-board_type = Board Type
firmware_tool-select_source-firmware = Firmware Source
firmware_tool-select_source-version = Firmware Version
firmware_tool-select_source-official = Official
firmware_tool-select_source-dev = Dev
firmware_tool-select_source-not_selected = No source selected
firmware_tool-select_source-no_boards = No available boards for this source
firmware_tool-select_source-no_versions = No available versions for this source
firmware_tool-board_defaults = Configure your board
firmware_tool-board_defaults-description = Set the pins or settings relative to your hardware
firmware_tool-board_defaults-add = Add
firmware_tool-board_defaults-reset = Reset to Default
firmware_tool-board_defaults-error-required = Required field
firmware_tool-board_defaults-error-format = Invalid format
firmware_tool-board_defaults-error-format-number = Not a number
firmware_tool-flash_method_step = Flashing Method
firmware_tool-flash_method_step-description = Please select the flashing method you want to use
firmware_tool-flash_method_step-ota-v2 = 
    .label = Wi-Fi
    .description = Use the over-the-air method. Your tracker will use Wi-Fi to update its firmware. Only works on trackers that have been set up.
firmware_tool-flash_method_step-ota-info =
    We use your wifi credentials to flash the tracker and confirm that everything worked correctly.
    <b>We do not store your wifi credentials!</b>
firmware_tool-flash_method_step-serial-v2 = 
    .label = USB
    .description = Use a USB cable to update your tracker.
firmware_tool-flashbtn_step = Press the boot button
firmware_tool-flashbtn_step-description = Before going to the next step, there are a few things you need to do
firmware_tool-flashbtn_step-board_SLIMEVR = Turn off the tracker, remove the case (if any), connect the USB cable to your computer, then follow the appropriate steps for your SlimeVR board revision:
firmware_tool-flashbtn_step-board_SLIMEVR-r11-v2 = Turn on the tracker while shorting the second rectangular FLASH pad from the edge on the top side of the board to the metal shield of the microcontroller. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_SLIMEVR-r12-v2 = Turn on the tracker while shorting the circular FLASH pad on the top side of the board to the metal shield of the microcontroller. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_SLIMEVR-r14-v2 = Turn on the tracker while pushing in the FLASH button on the top side of the board. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_OTHER =
    Before flashing, you will probably need to put the tracker into bootloader mode.
    Most of the time, this means pressing the boot button on the board before the flashing process starts.
    If the flashing process times out at the start, it probably means that the tracker was not in bootloader mode.
    Refer to your board's flashing instructions to learn how to enter bootloader mode.
firmware_tool-flash_method_ota-title = Flashing over Wi-Fi
firmware_tool-flash_method_ota-devices = Detected OTA Devices:
firmware_tool-flash_method_ota-no_devices = There are no boards that can be updated using OTA, make sure you selected the correct board type
firmware_tool-flash_method_serial-title = Flashing over USB
firmware_tool-flash_method_serial-wifi = Wi-Fi Credentials:
firmware_tool-flash_method_serial-devices-label = Detected Serial Devices:
firmware_tool-flash_method_serial-devices-placeholder = Select a serial device
firmware_tool-flash_method_serial-no_devices = There are no compatible serial devices detected, make sure the tracker is plugged in
firmware_tool-build_step = Building
firmware_tool-build_step-description = The firmware is building, please wait
firmware_tool-flashing_step = Flashing
firmware_tool-flashing_step-description = Your trackers are flashing, please follow the instructions on the screen
firmware_tool-flashing_step-warning-v2 = Do not unplug or turn off the tracker during the upload process unless told to, it may make your board unusable
firmware_tool-flashing_step-flash_more = Flash more trackers
firmware_tool-flashing_step-exit = Exit
firmware_tool-flashing_step-onboarding_continue = Continue

## firmware tool build status

firmware_tool-build-QUEUED = Waiting to build....
firmware_tool-build-CREATING_BUILD_FOLDER = Creating the build folder
firmware_tool-build-DOWNLOADING_SOURCE = Downloading the source code
firmware_tool-build-EXTRACTING_SOURCE = Extracting the source code
firmware_tool-build-BUILDING = Building the firmware
firmware_tool-build-SAVING = Saving the build
firmware_tool-build-DONE = Build Complete
firmware_tool-build-ERROR = Unable to build the firmware

## Firmware update status

firmware_update-status-DOWNLOADING = Downloading the firmware
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Please turn your tracker off and on again
firmware_update-status-AUTHENTICATING = Authenticating with the mcu
firmware_update-status-UPLOADING = Uploading the firmware
firmware_update-status-SYNCING_WITH_MCU = Syncing with the mcu
firmware_update-status-REBOOTING = Applying the update
firmware_update-status-PROVISIONING = Setting Wi-Fi credentials
firmware_update-status-DONE = Update complete!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Could not find the device
firmware_update-status-ERROR_TIMEOUT = The update process timed out
firmware_update-status-ERROR_DOWNLOAD_FAILED = Could not download the firmware
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Could not authenticate with the mcu
firmware_update-status-ERROR_UPLOAD_FAILED = Could not upload the firmware
firmware_update-status-ERROR_PROVISIONING_FAILED = Could not set the Wi-Fi credentials
firmware_update-status-ERROR_UNSUPPORTED_METHOD = The update method is not supported
firmware_update-status-ERROR_UNKNOWN = Unknown error

## Dedicated Firmware Update Page

firmware_update-title = Firmware update
firmware_update-devices = Available Devices
firmware_update-devices-description = Please select the trackers you want to update to the latest version of SlimeVR firmware.
firmware_update-no_devices = Please make sure that the trackers you want to update are ON and connected to the Wi-Fi!
firmware_update-changelog-title = Updating to { $version }
firmware_update-looking_for_devices = Looking for devices to update...
firmware_update-retry = Retry
firmware_update-update = Update Selected Trackers
firmware_update-exit = Exit

## Tray Menu

tray_menu-show = Xem
tray_menu-hide = Ẩn
tray_menu-quit = Thoát

## First exit modal

tray_or_exit_modal-title = Nút đóng nên làm gì?
# Multiline text
tray_or_exit_modal-description =
    Điều này cho phép bạn chọn xem bạn muốn thoát khỏi chương trình hoặc thu nhỏ nó vào khay khi nhấn nút đóng.
    
    Bạn có thể thay đổi điều này sau trong cài đặt giao diện.
tray_or_exit_modal-radio-exit = Thoát khi đóng
tray_or_exit_modal-radio-tray = Thu nhỏ vào khay hệ thống
tray_or_exit_modal-submit = Lưu
tray_or_exit_modal-cancel = Hủy

## Unknown device modal

unknown_device-modal-title = Thiết bị mới đã được tìm thấy!
unknown_device-modal-description =
    Có thiết bị mới với địa chỉ MAC <b>{ $deviceId }</b>.
    Bạn có muốn kết nối nó với SlimeVR không?
unknown_device-modal-confirm = Chắc!
unknown_device-modal-forget = Bỏ qua
# VRChat config warnings
vrc_config-page-title = VRChat configuration warnings
vrc_config-page-desc = This page shows the state of your VRChat settings and shows what settings are incompatible with SlimeVR. It is highly recommended that you fix any warnings showing up here for the best user experience with SlimeVR.
vrc_config-page-help = Can't find the settings?
vrc_config-page-help-desc = Check out our <a>documentation on this topic!</a>
vrc_config-page-big_menu = Tracking & IK (Big Menu)
vrc_config-page-big_menu-desc = Settings related to IK in the big settings menu
vrc_config-page-wrist_menu = Tracking & IK (Wrist Menu)
vrc_config-page-wrist_menu-desc = Settings related to IK in small settings menu (wrist menu)
vrc_config-on = On
vrc_config-off = Off
vrc_config-invalid = You have misconfigured VRChat settings!
vrc_config-show_more = Show more
vrc_config-setting_name = VRChat Setting name
vrc_config-recommended_value = Recommended Value
vrc_config-current_value = Current Value
vrc_config-mute = Mute Warning
vrc_config-mute-btn = Mute
vrc_config-unmute-btn = Unmute
vrc_config-legacy_mode = Use Legacy IK Solving
vrc_config-disable_shoulder_tracking = Disable Shoulder Tracking
vrc_config-shoulder_width_compensation = Shoulder Width Compensation
vrc_config-spine_mode = FBT Spine Mode
vrc_config-tracker_model = FBT Tracker Model
vrc_config-avatar_measurement_type = Avatar Measurement
vrc_config-calibration_range = Calibration Range
vrc_config-calibration_visuals = Display Calibration Visuals
vrc_config-user_height = User Real Height
vrc_config-spine_mode-UNKNOWN = Unknown
vrc_config-spine_mode-LOCK_BOTH = Lock Both
vrc_config-spine_mode-LOCK_HEAD = Lock Head
vrc_config-spine_mode-LOCK_HIP = Lock Hip
vrc_config-tracker_model-UNKNOWN = Unknown
vrc_config-tracker_model-AXIS = Axis
vrc_config-tracker_model-BOX = Box
vrc_config-tracker_model-SPHERE = Sphere
vrc_config-tracker_model-SYSTEM = System
vrc_config-avatar_measurement_type-UNKNOWN = Unknown
vrc_config-avatar_measurement_type-HEIGHT = Height
vrc_config-avatar_measurement_type-ARM_SPAN = Arm Span

## Error collection consent modal

error_collection_modal-title = Can we collect errors?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    You can change this setting later in the Behavior section of the settings page.
error_collection_modal-confirm = I agree
error_collection_modal-cancel = I don't want to

## Tracking checklist section

tracking_checklist = Tracking Checklist
tracking_checklist-settings = Tracking Checklist Settings
tracking_checklist-settings-close = Close
tracking_checklist-status-incomplete = You are not prepared to use SlimeVR!
tracking_checklist-status-partial =
    { $count ->
        [one] You have 1 warning!
       *[many] You have { $count } warnings!
    }
tracking_checklist-status-complete = You are prepared to use SlimeVR!
tracking_checklist-MOUNTING_CALIBRATION = Perform a mounting calibration
tracking_checklist-FEET_MOUNTING_CALIBRATION = Perform a feet mounting calibration
tracking_checklist-FULL_RESET = Perform a full reset
tracking_checklist-FULL_RESET-desc = Some trackers need a reset to be performed.
tracking_checklist-STEAMVR_DISCONNECTED = SteamVR disconnected
tracking_checklist-STEAMVR_DISCONNECTED-desc = SteamVR is not running. Are you using it for VR?
tracking_checklist-STEAMVR_DISCONNECTED-driver_blocked-desc = The driver has been blocked by SteamVR due to a previous SteamVR crash.
tracking_checklist-STEAMVR_DISCONNECTED-driver_disabled-desc = The driver is disabled in SteamVR settings.
tracking_checklist-STEAMVR_DISCONNECTED-driver_not_installed-desc = The driver is not installed.
tracking_checklist-STEAMVR_DISCONNECTED-open = Launch SteamVR
tracking_checklist-STEAMVR_DISCONNECTED-enable = Enable driver
tracking_checklist-STEAMVR_HANDS_ENABLED = Hand trackers toggled on
tracking_checklist-STEAMVR_HANDS_ENABLED-desc = You have enabled the SteamVR virtual hand trackers. This will cause button inputs to not work in SteamVR and in games.
tracking_checklist-STEAMVR_HANDS_ENABLED-go = Disable them
tracking_checklist-STANDABLE_INSTALLED = Standable is installed
tracking_checklist-STANDABLE_INSTALLED-desc =
    Standable frequently causes tracking issues when used alongside SlimeVR. Standable should be fully uninstalled in Steam to ensure no issues arise.
    You must close SteamVR before uninstalling Standable in Steam.
tracking_checklist-TRACKERS_REST_CALIBRATION = Calibrate your trackers
tracking_checklist-TRACKERS_REST_CALIBRATION-desc = You didn't perform tracker calibration. Please let your trackers (highlighted in yellow) rest on a stable surface for a few seconds.
tracking_checklist-TRACKER_ERROR = Trackers with Errors
tracking_checklist-TRACKER_ERROR-desc = Some of your trackers have an error. Please restart the trackers highlighted in yellow.
tracking_checklist-VRCHAT_SETTINGS = Configure VRChat settings
tracking_checklist-VRCHAT_SETTINGS-desc = You have misconfigured VRChat settings! This can negatively impact your tracking.
tracking_checklist-VRCHAT_SETTINGS-open = Go to VRChat Warnings
tracking_checklist-UNASSIGNED_HMD = VR headset not assigned to Head
tracking_checklist-UNASSIGNED_HMD-desc = The VR headset should be assigned as a head tracker.
tracking_checklist-NETWORK_PROFILE_PUBLIC = Change your network profile
tracking_checklist-NETWORK_PROFILE_PUBLIC-desc =
    { $count ->
        [one]
            Your network profile is currently set to Public ({ $adapters }).
            This is not recommended for SlimeVR to function properly.
            <PublicFixLink>See how to fix it here.</PublicFixLink>
       *[many]
            Some of your network adapters are set to public:
            { $adapters }
            This is not recommended for SlimeVR to function properly.
            <PublicFixLink>See how to fix it here.</PublicFixLink>
    }
tracking_checklist-NETWORK_PROFILE_PUBLIC-open = Open Control Panel
tracking_checklist-STAY_ALIGNED_CONFIGURED = Configure Stay Aligned
tracking_checklist-STAY_ALIGNED_CONFIGURED-desc = Record the Stay Aligned poses to reduce drift
tracking_checklist-STAY_ALIGNED_CONFIGURED-open = Open Stay Aligned Wizard
tracking_checklist-ignore = Ignore
preview-mocap_mode_soon = Mocap Mode (Soon™)
preview-disable_render = Disable rendering
preview-disabled_render = Rendering disabled
toolbar-mounting_calibration = Mounting Calibration
toolbar-mounting_calibration-default = Body
toolbar-mounting_calibration-feet = Feet
toolbar-mounting_calibration-fingers = Fingers
toolbar-drift_reset = Drift Reset
toolbar-assigned_trackers = { $count } trackers assigned
toolbar-unassigned_trackers = { $count } trackers unassigned
