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

version_update-description = Nhấp vào "{ version_update-update }" sẽ tải xuống trình cài đặt SlimeVR cho bạn.
version_update-update = Cập nhật
version_update-close = Đóng

## Tips

tips-find_tracker = Không rõ tracker nào đang được chọn? Di chuyển nó và trong menu sẽ sáng lên tracker đó
tips-do_not_move_heels = Không di chuyển gót chân trong khi đo
tips-file_select = Kéo và thả tệp để sử dụng hoặc <u>duyệt</u>.
tips-failed_webgl = Không thể khởi tạo WebGL.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Đóng

## Body parts

body_part-NONE = Chưa gán vị trí
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
body_part-HIP = Hông
body_part-LEFT_SHOULDER = Vai trái
body_part-LEFT_UPPER_ARM = Bắp tay trái
body_part-LEFT_LOWER_ARM = Cẳng tay trái
body_part-LEFT_HAND = Tay trái
body_part-LEFT_UPPER_LEG = Bắp chân trái
body_part-LEFT_LOWER_LEG = Cẳng chân trái
body_part-LEFT_FOOT = Bàn chân trái

## BoardType

board_type-UNKNOWN = Không rõ

## Proportions

skeleton_bone-NONE = Chưa được gán
skeleton_bone-HEAD = Sai số đầu
skeleton_bone-NECK = Chiều dài cổ
skeleton_bone-torso_group = Độ dài thân
skeleton_bone-UPPER_CHEST = Độ dài ngực trên
skeleton_bone-HIP = Khoảng cách hông
skeleton_bone-HIPS_WIDTH = Chiều rộng hông
skeleton_bone-leg_group = Chiều dài chân
skeleton_bone-UPPER_LEG = Chiều dài bắp chân
skeleton_bone-LOWER_LEG = Chiều dài cẳng chân
skeleton_bone-FOOT_LENGTH = Chiều dài bàn chân
skeleton_bone-FOOT_SHIFT = Sai số bàn chân
skeleton_bone-SHOULDERS_DISTANCE = Khoảng cách vai
skeleton_bone-SHOULDERS_WIDTH = Chiều rộng vai
skeleton_bone-arm_group = Chiều dài cánh tay
skeleton_bone-UPPER_ARM = Chiều dài bắp tay
skeleton_bone-LOWER_ARM = Chiều dài cẳng tay
skeleton_bone-HAND_Y = Khoảng cách tay Y
skeleton_bone-HAND_Z = Khoảng cách tay Z

## Tracker reset buttons

reset-reset_all = Đặt lại tất cả bộ phận
reset-reset_all_warning-reset = Đặt lại tỷ lệ
reset-reset_all_warning-cancel = Hủy
reset-full = Đặt lại
reset-mounting = Đặt lại hướng gắn
reset-yaw = Đặt lại chiều quay lệch

## Navigation bar

navbar-home = Trang chủ
navbar-body_proportions = Tỉ lệ cơ thể
navbar-trackers_assign = Phân bố tracker
navbar-mounting = Đặt lại hướng gắn
navbar-onboarding = Trình thiết lập
navbar-settings = Cài đặt

## Biovision hierarchy recording

bvh-start_recording = Ghi BVH
bvh-recording = Đang ghi...

## Tracking pause

tracking-unpaused = Tạm dừng tracking
tracking-paused = Bỏ dừng theo dõi

## Widget: Developer settings

widget-developer_mode = Chế độ nhà phát triển
widget-developer_mode-high_contrast = Chế độ tương phản cao
widget-developer_mode-precise_rotation = Hiển thị góc quay chính xác
widget-developer_mode-fast_data_feed = Tăng tốc độ gửi dữ liệu
widget-developer_mode-raw_slime_rotation = Gốc

## Widget: IMU Visualizer

widget-imu_visualizer = Góc quay
widget-imu_visualizer-preview = Xem trước
widget-imu_visualizer-hide = Ẩn
widget-imu_visualizer-rotation_raw = Gốc
widget-imu_visualizer-rotation_preview = Qua xử lí
widget-imu_visualizer-acceleration = Gia tốc
widget-imu_visualizer-position = Vị trí

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
tracker-table-column-temperature = Nhiệt độ (°C)
tracker-table-column-linear-acceleration = Tốc độ X/Y/Z
tracker-table-column-rotation = Góc quay X/Y/Z
tracker-table-column-position = Tọa độ X/Y/Z
tracker-table-column-url = Đường dẫn

## Tracker rotation

tracker-rotation-front = Trước
tracker-rotation-front_left = Phía trước-Bên trái
tracker-rotation-front_right = Phía trước-Bên phải
tracker-rotation-left = Trái
tracker-rotation-right = Phải
tracker-rotation-back = Khuỷu tay ra sau
tracker-rotation-back_left = Phía sau-Bên trái
tracker-rotation-back_right = Phía sau-Bên phải
tracker-rotation-custom = Tùy chọn

## Tracker information

tracker-infos-manufacturer = Nhà sản xuất
tracker-infos-display_name = Tên hiển thị
tracker-infos-custom_name = Tên gọi
tracker-infos-url = Đường dẫn
tracker-infos-imu = Cảm biến IMU (IMU Sensor)
tracker-infos-board_type = Bảng mạch chính
tracker-infos-network_version = Phiên bản giao thức

## Tracker settings

tracker-settings-back = Quay lại danh sách tracker
tracker-settings-title = Cài đặt
tracker-settings-assignment_section = Vị trí
tracker-settings-assignment_section-description = Vị trí của tracker trên cơ thể
tracker-settings-assignment_section-edit = Thay đổi vị trí
tracker-settings-mounting_section = Vị trí đặt
tracker-settings-mounting_section-description = Tracker được đặt ở đâu?
tracker-settings-mounting_section-edit = Thay đổi chỗ đặt
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tên tracker
tracker-settings-name_section-placeholder = Chân trái của JINODK
tracker-settings-name_section-label = Tên tracker
tracker-settings-forget = Quên thiết bị
tracker-settings-forget-description = Xóa thiết bị khỏi phần mềm SlimeVR và ngăn nó kết nối với nó cho đến khi máy chủ được khởi động lại. Cấu hình của trình theo dõi sẽ không bị mất.
tracker-settings-forget-label = Quên thiết bị

## Dongle settings

dongle-infos-hardware_revision = Revision phần cứng
dongle-status-disconnected = Đã ngắt kết nối
dongle-settings-back = Quay lại danh sách tracker

## Tracker part card info

tracker-part_card-unassigned = Chưa gán vị trí

## Body assignment menu

body_assignment_menu = Bạn muốn gắn tracker này cho bộ phận nào?
body_assignment_menu-description = Chọn vị trí bạn muốn gán tracker, ngoài ra bạn cũng có thể quản lí vị trí tất cả các tracker cùng một lúc
body_assignment_menu-manage_trackers = Quản lí tất cả tracker
body_assignment_menu-unassign_tracker = Bỏ gán tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Cảnh báo:</b> Tracker ở cổ rất nguy hiểm và có thể gây tử vong nếu điều chỉnh quá chặt,
    Dây đeo có thể cắt lưu thông máu đến đầu của bạn!
tracker_selection_menu-neck_warning-done = Tôi hiểu những rủi ro
tracker_selection_menu-neck_warning-cancel = Hủy

## Mounting menu

mounting_selection_menu-close = Đóng

## Sidebar settings

settings-sidebar-title = Cài đặt
settings-sidebar-general = Cài đặt chung
settings-sidebar-trackers = Cấu hình Tracker
settings-sidebar-interface = Giao diện
settings-sidebar-utils = Công cụ
settings-sidebar-appearance = Giao diện
settings-sidebar-notifications = Thông báo
settings-sidebar-advanced = Cài đặt mở rộng

## Bone routing settings

settings-routing-hands-warning-cancel = Hủy

## SteamVR / Monado output settings

settings-driver-enable = Bật

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Lọc nhiễu
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Chọn kiểu lọc nhiễu cho tracker
    "Dự đoán" sẽ dự đoán chuyển động trong khi "Khử nhiễu" sẽ làm chuyển động mềm mại hơn
settings-general-tracker_mechanics-filtering-type-none = Không lọc
settings-general-tracker_mechanics-filtering-type-none-description = Sử dụng giá trị thực, không áp dụng bất kì bộ lọc nào
settings-general-tracker_mechanics-filtering-type-smoothing = Khử nhiễu
settings-general-tracker_mechanics-filtering-type-smoothing-description = Làm chuyển động mềm mại hơn nhưng có thể tăng độ trễ
settings-general-tracker_mechanics-filtering-type-prediction = Dự đoán
settings-general-tracker_mechanics-filtering-type-prediction-description = Giảm độ trễ và làm chuyển động chân thật hơn, có thể khiến chuyển động không mượt mà
settings-general-tracker_mechanics-save_mounting_reset = Lưu thiết bị đã giao tự động
settings-general-tracker_mechanics-save_mounting_reset-description =
    Lưu thiết bị đã giao tự động cho các thiết bị giữa các lần khởi động lại. Có ích
    khi mặc một bộ đồ SlimeVR mà trình theo dõi không di chuyển giữa các phiên. <b>Không được khuyến khích cho người dùng bình thường!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Đặt lại hướng gắn thiết bị
settings-stay_aligned-general-label = Cài đặt chung
settings-stay_aligned-relaxed_poses-close = Đóng

## Keybinds Page

settings-keybinds_full-reset = Đặt lại
settings-keybinds_yaw-reset = Đặt lại chiều quay lệch
settings-keybinds_reset-all-button = Đặt lại tất cả
settings-keybinds-recorder-modal-cancel-button = Hủy

## FK/Tracking settings

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
settings-general-fk_settings-arm_fk-back = Khuỷu tay ra sau
settings-general-fk_settings-arm_fk-back-description = Chế độ mặc định, với cánh tay trên trỏ về phía sau và cánh tay dưới hướng về phía trước.
settings-general-fk_settings-arm_fk-tpose_up = T-pose
settings-general-fk_settings-arm_fk-tpose_up-description = Hai tay của bạn sẽ hướng xuống ở hai bên khi đặt lại hoàn toàn, và đưa lên 90 độ sang hai bên khi đặt lại hướng gắn tracker.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (ngược lại)
settings-general-fk_settings-arm_fk-tpose_down-description = Hai tay của bạn sẽ đưa lên 90 độ sang hai bên khi đặt lại hoàn toàn, và hai tay hướng xuống hai bên khi đặt lại hướng gắn tracker.
settings-general-fk_settings-arm_fk-forward = Hai tay ra trước
settings-general-fk_settings-arm_fk-forward-description = Hai cánh tay của bạn nâng lên 90 độ về phía trước. Hữu dụng cho việc VTubing.
settings-general-fk_settings-skeleton_settings-ratios = Tỷ lệ khung xương
settings-general-fk_settings-skeleton_settings-ratios-description = Thay đổi các giá trị của cài đặt bộ xương. Bạn có thể cần phải điều chỉnh tỷ lệ của bạn sau khi thay đổi những điều này.
settings-general-fk_settings-self_localization-title = Chế độ Mocap

## Gesture control settings (tracker tapping)

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
settings-general-gesture_control-fullResetEnabled = Bật chạm để đặt lại chiều quay
settings-general-gesture_control-fullResetDelay = Thời gian delay trước khi đặt lại full
settings-general-gesture_control-fullResetTaps = Số lần chạm để đặt lại full
settings-general-gesture_control-mountingResetEnabled = Chạm để đặt lại hướng gắn tracker
settings-general-gesture_control-mountingResetDelay = Thời gian delay trước khi đặt lại hướng gắn tracker
settings-general-gesture_control-mountingResetTaps = Số lần chạm cho đặt lại hướng gắn tracker
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Số thiết bị có thể vượt phát hiện
settings-general-gesture_control-numberTrackersOverThreshold-description = Tăng số này nếu tính năng chạm thiết bị không hoạt động. Không tăng nó quá trên mức cần thiết để làm cho phát hiện chạm hoạt động vì nó sẽ gây ra nhiều kết quả sai hơn.

## Appearance settings

settings-interface-appearance = Giao diện
settings-general-interface-dev_mode = Chế độ nhà phát triển
settings-general-interface-dev_mode-description = Hữu dụng nếu cần thêm thông tin chi tiết của tracker hay can thiệp sâu hơn vào tracker
settings-general-interface-dev_mode-label = Chế độ nhà phát triển
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

## Notification settings

settings-interface-notifications = Thông báo
settings-general-interface-feedback_sound = Âm thanh phản hồi
settings-general-interface-feedback_sound-description = Tùy chọn này sẽ phát âm thanh khi thiết lập lại được kích hoạt.
settings-general-interface-feedback_sound-label = Âm thanh phản hồi
settings-general-interface-feedback_sound-volume = Âm lượng phản hồi
settings-general-interface-connected_trackers_warning = Cảnh báo với thiết bị đã kết nối
settings-general-interface-connected_trackers_warning-description = Tùy chọn này sẽ hiển thị cửa sổ bật lên mỗi khi bạn thử thoát khỏi SlimeVR trong khi có một hoặc nhiều thiết bị theo dõi được kết nối. Nó nhắc nhở bạn tắt trình theo dõi khi bạn hoàn tất để duy trì tuổi thọ pin.
settings-general-interface-connected_trackers_warning-label = Cảnh báo thiết bị đã kết nối khi thoát chương trình

## Behavior settings

settings-general-interface-dev_mode = Chế độ nhà phát triển
settings-general-interface-dev_mode-label = Chế độ nhà phát triển
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

## Serial settings

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
settings-serial-save_logs = Lưu vào tệp
settings-serial-send_command-warning-ok = Tôi biết mình đang làm gì
settings-serial-send_command-warning-cancel = Hủy

## OSC VRChat settings

settings-osc-vrchat-enable = Bật
settings-osc-vrchat-enable-description = Cho phép nhận và gửi dữ liệu OSC
settings-osc-vrchat-enable-label = Bật
settings-osc-vrchat-network = Cổng mạng (Network ports)
settings-osc-vrchat-network-port_in =
    .label = Cổng vào (Port In)
    .placeholder = Cổng vào (Mặc định: 9001)
settings-osc-vrchat-network-port_out =
    .label = Cổng ra (Port out)
    .placeholder = Cổng ra (Mặc định: 9000)
settings-osc-vrchat-network-address = Địa chỉ mạng (Network address)
settings-osc-vrchat-network-address-description-v1 = Chọn địa chỉ để gửi dữ liệu đến. Có thể giữ nguyên cho VRChat.
settings-osc-vrchat-network-address-placeholder = Địa chỉ IP của thiết bị chơi VRChat

## VRChat OSC status

settings-osc-vrchat-status-tracking = Góc quay
settings-osc-vrchat-status-badge-error = Lỗi
settings-osc-vrchat-status-badge-unknown = Không rõ

## VMC OSC settings

# This cares about multilines
settings-osc-vmc-description =
    Thay đổi cài đặt cụ thể cho giao thức VMC (Virtual Motion Capture)
        để gửi dữ liệu thân của SlimeVR và nhận dữ liệu thân từ các ứng dụng khác.
settings-osc-vmc-enable = Bật
settings-osc-vmc-enable-description = Cho phép nhận và gửi dữ liệu OSC
settings-osc-vmc-enable-label = Bật
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
settings-osc-vmc-vrm-file_select = Kéo và thả mô hình để sử dụng hoặc <u>duyệt file</u>
settings-osc-vmc-anchor_hip = Cố định ở hông
settings-osc-vmc-anchor_hip-label = Cố định ở hông
settings-osc-vmc-mirror_tracking = Phản chiếu ngược theo dõi cơ thể
settings-osc-vmc-mirror_tracking-description = Phản chiếu theo dõi theo chiều ngang.
settings-osc-vmc-mirror_tracking-label = Phản chiếu ngược theo dõi cơ thể
settings-osc-vmc-status-badge-error = Lỗi

## Common OSC settings


## Advanced settings

settings-utils-advanced = Cài đặt mở rộng
settings-utils-advanced-reset-gui = Đặt lại cài đặt GUI
settings-utils-advanced-reset-all-label = Đặt lại tất cả
settings-utils-advanced-reset_warning-reset = Đặt lại cài đặt
settings-utils-advanced-reset_warning-cancel = Hủy
settings-utils-advanced-open_data-label = Mở thư mục

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

