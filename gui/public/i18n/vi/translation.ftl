### SlimeVR complete GUI translations
# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropiate 
# features like variables and selectors in each appropiate case!
# And also comment the string if it's something not easy to translate so you help
# translators on what it means

## Websocket (server) status
websocket-connecting = Đang kết nối với máy chủ
websocket-connection_lost = Kết nối với máy chủ đã mất. Đang kết nối lại...

## Tips
tips-find_tracker = Không rõ tracker nào? Lắc tracker và trong menu sẽ sáng lên.
tips-do_not_move_heels = Hãy đảm bảo gót chân không cử động trong khi đo ghi!

## Body parts
body_part-NONE = Chưa liên kết
body_part-HEAD = Đầu
body_part-NECK = Cổ
body_part-RIGHT_SHOULDER = Vai phải
body_part-RIGHT_UPPER_ARM = Tay phải trên
body_part-RIGHT_LOWER_ARM = Tay phải dưới
body_part-RIGHT_HAND = Tay phải
body_part-RIGHT_UPPER_LEG = Chân phải trên
body_part-RIGHT_LOWER_LEG = chân phải dưới
body_part-RIGHT_FOOT = Bàn chân phải
body_part-RIGHT_CONTROLLER = Right controller
body_part-CHEST = Ngực
body_part-WAIST = Eo
body_part-HIP = Hông
body_part-LEFT_SHOULDER = Vai trái
body_part-LEFT_UPPER_ARM = Tay trái trên
body_part-LEFT_LOWER_ARM = tay trái dưới
body_part-LEFT_HAND = Tay trái
body_part-LEFT_UPPER_LEG = Chân trái trên
body_part-LEFT_LOWER_LEG = Chân trái dưới
body_part-LEFT_FOOT = Bàn chân trái
body_part-LEFT_CONTROLLER = Left controller

## Skeleton stuff
skeleton_bone-NONE = Chưa liên kết
skeleton_bone-HEAD = Đầu ca
skeleton_bone-NECK = Cổ dài
skeleton_bone-TORSO = Chiều dài thân
skeleton_bone-CHEST = Khoảng cách ngực
skeleton_bone-WAIST = Khoảng cách eo
skeleton_bone-HIP_OFFSET = Lệc đo hông
skeleton_bone-HIPS_WIDTH = Chiều rộng hông
skeleton_bone-LEGS_LENGTH = Chiều dài chân
skeleton_bone-KNEE_HEIGHT = Chiều cao đầu gối
skeleton_bone-FOOT_LENGTH = CHiều dài bàn chân
skeleton_bone-FOOT_SHIFT = Lệch đo bàn chân
skeleton_bone-SKELETON_OFFSET = Lệch đo thân
skeleton_bone-CONTROLLER_DISTANCE_Z = Khoảng cách tay cầm Z
skeleton_bone-CONTROLLER_DISTANCE_Y = Khoảng cách tay cầm Y
skeleton_bone-FOREARM_LENGTH = khoảng cách cánh tay
skeleton_bone-SHOULDERS_DISTANCE = Khoảng cách vai
skeleton_bone-SHOULDERS_WIDTH = Chiều rộng vai
skeleton_bone-UPPER_ARM_LENGTH = Chiều dài tay trên
skeleton_bone-ELBOW_OFFSET = Lệch đo khuỷu tay

## Tracker reset buttons
reset-reset_all = Reset tất cả bộ phận
reset-full = Reset
reset-mounting = Reset vị trí cài
reset-quick = Reset nhanh

## Serial detection stuff
serial_detection-new_device-p0 = New serial device detected!
serial_detection-new_device-p1 = Vui lòng nhập thông tin Wi-Fi!
serial_detection-new_device-p2 = Vui lòng chọn những gì làm với nó
serial_detection-open_wifi = Kết nối Wi-Fi
serial_detection-open_serial = Mở Serial Console
serial_detection-submit = Đăng lên!
serial_detection-close = Đóng

## Navigation bar
navbar-home = Giao diện chính
navbar-body_proportions = Bộ phận cơ thể
navbar-trackers_assign = Giao bộ phận tracker
navbar-mounting = Đo vị trí đặt
navbar-onboarding = Trình hướng dẫn cài đặt
navbar-settings = Cài đặt

## Bounding volume hierarchy recording
bvh-start_recording = Ghi BVH
bvh-recording = Đang ghi...

## Overlay settings
overlay-is_visible_label = Xem overlay trên SteamVR
overlay-is_mirrored_label = Xem overlay trong gương

## Tracker status
tracker-status-none = Không có tình trạng
tracker-status-busy = Bận
tracker-status-error = Lỗi
tracker-status-disconnected = Đã ngắt kết nối
tracker-status-occluded = Bị tắc
tracker-status-ok = Đã kết nối

## Tracker status columns
tracker-table-column-name = Tên
tracker-table-column-type = Loại
tracker-table-column-battery = Pin
tracker-table-column-ping = Ping
tracker-table-column-rotation = Chiều chuyển X/Y/Z
tracker-table-column-position = Vị trí X/Y/Z
tracker-table-column-url = URL

## Tracker rotation
tracker-rotation-front = Đằng trước
tracker-rotation-left = Bên trái
tracker-rotation-right = Bên phải
tracker-rotation-back = Đằng sau

## Tracker information
tracker-infos-manufacturer = Nhà sản xuất
tracker-infos-display_name = Tên gọi
tracker-infos-custom_name = Tên tự chọn
tracker-infos-url =  URL

## Tracker settings
tracker-settings-back = Quay lại danh sách tracker
tracker-settings-title = Cài đặt tracker
tracker-settings-assignment_section = Giao bộ phận
tracker-settings-assignment_section-description = Thiết bị này được giao cho bộ phận nào.
tracker-settings-assignment_section-edit = Chỉnh lại giao bộ phận
tracker-settings-mounting_section = Vị trí đặt bộ phận
tracker-settings-mounting_section-description = Tracker đặt ở đâu?
tracker-settings-mounting_section-edit = Chỉnh lại chỗ đặt
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tên tracker
tracker-settings-name_section-placeholder = Menaced's left leg
tracker-settings-name_section-description = Hãy cho một tên đẹp :)

## Tracker part card info
tracker-part_card-no_name = Không tên
tracker-part_card-unassigned = Chưa giao vị trí

## Body assignment menu
body_assignment_menu = Bạn muốn giao tracker này cho bộ phận nào?
body_assignment_menu-description = Chọn vị trí bạn muốn giao tracker cho. Ngoài ra, bạn có thể quản lí tất cả tracker cùng một lúc thay vì từng cái một.
body_assignment_menu-show_advanced_locations = Xem thêm vị trí đặt
body_assignment_menu-manage_trackers = Quản lí tất cả tracker
body_assignment_menu-unassign_tracker = Tracker chưa giao vị trí

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

tracker_selection_menu-unassigned = Chưa giao vị trí
tracker_selection_menu-assigned = Đã giao vị trí
tracker_selection_menu-dont_assign = Thoát và không giao

## Mounting menu
mounting_selection_menu = Bạn muốn giao tracker này ở đâu?
mounting_selection_menu-close = Đóng

## Sidebar settings
settings-sidebar-title = Cài đặt
settings-sidebar-general = Cài đặt chung
settings-sidebar-tracker_mechanics = Cơ khí tracker
settings-sidebar-fk_settings = Cài đặt FK
settings-sidebar-gesture_control = Gesture control
settings-sidebar-interface = Giao diện
settings-sidebar-osc_router = OSC router
settings-sidebar-utils = Hữu dụng
settings-sidebar-serial = Bảng điều khiển Serial

## SteamVR settings
settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR trackers
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Bặt hoặc tắt tracker SteamVR.
    Hữu dụng nếu bạn muốn điều khiển cách SlimeVR hoạt động.
settings-general-steamvr-trackers-waist = Eo
settings-general-steamvr-trackers-chest = Ngực
settings-general-steamvr-trackers-feet = Bàn chân
settings-general-steamvr-trackers-knees = Đầu gối (Chân)
settings-general-steamvr-trackers-elbows = Khuỷu tay
settings-general-steamvr-trackers-hands = Hands

## Tracker mechanics
settings-general-tracker_mechanics = Cơ khí tracker
settings-general-tracker_mechanics-subtitle = Lọc rung
# This also cares about multilines
settings-general-tracker_mechanics-description =
    Chọn kiểu lọc rung cho tracker.
    Dự đoán đoán cử động trong khi lọc rung cử động.
settings-general-tracker_mechanics-filtering_type = Kiểu lọc
settings-general-tracker_mechanics-filtering_type-none = Không lọc
settings-general-tracker_mechanics-filtering_type-none-description = Để đo chiều chuyển y nguyên. Không sử dụlọclọc.
settings-general-tracker_mechanics-filtering_type-smoothing = Làm mượt
settings-general-tracker_mechanics-filtering_type-smoothing-description = Lọc cử động mượt nhưng tạo ít chậm rễ.
settings-general-tracker_mechanics-filtering_type-prediction = Dự đoán
settings-general-tracker_mechanics-filtering_type-prediction-description = Giảm chậm trê và để cử động gọn hơn, nhưng có thể tăng độ rung.
settings-general-tracker_mechanics-amount = Số luọng

## FK/Tracking settings
settings-general-fk_settings = Cài đặt FK
settings-general-fk_settings-leg_tweak = Chỉnh chân
settings-general-fk_settings-leg_tweak-description = Chỉnh chân có thể giảm hoặc loại bỏ chân đi xuyên sàn nhà nhưng có thể ảnh hưởng đầu gối. Sửa trượt sửa khi lướt, nhưng giảm độ chính xác ở một số cử động.
# Floor clip: 
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Xuyên sàn
# Skating correction: 
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Sửa trượt
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating correction strength
settings-general-fk_settings-arm_fk = Tay FK
settings-general-fk_settings-arm_fk-description = Chỉnh cách tay được đo.
settings-general-fk_settings-arm_fk-force_arms = Bắt tay từ kính thực tế ảo
settings-general-fk_settings-skeleton_settings = Cài đặt cơ thể
settings-general-fk_settings-skeleton_settings-description = Bật hoặc tắt hình cơ thể. Khuyên nên luôn để bật lựa chọn này.
settings-general-fk_settings-skeleton_settings-extended_spine = Xương sống mở rộng
settings-general-fk_settings-skeleton_settings-extended_pelvis = Xuong chậu mở rộng
settings-general-fk_settings-skeleton_settings-extended_knees = Đầu gối mở rộng
settings-general-fk_settings-vive_emulation-title = Vive emulation
settings-general-fk_settings-vive_emulation-description = Emulate the waist tracker problems that Vive trackers have. This is a joke and makes tracking worse.
settings-general-fk_settings-vive_emulation-label = Enable Vive emulation

## Gesture control settings (tracker tapping)
settings-general-gesture_control = Điều khiển cử chỉ
settings-general-gesture_control-subtitle = Bấm hai lần để reset nhanh
settings-general-gesture_control-description = Bật hoặc tắt reset nhanh. Khi bật bấm hai lần bất cứ đâu trên điểm cao nhất ở thân sẽ kích hoạt reset nhanh. Delay is the time between registering a tap and resetting.
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
settings-general-interface = Giao diện
settings-general-interface-dev_mode = Chế độ nhà phát triển
settings-general-interface-dev_mode-description = Chế độ này có thể hữu dụng nếu như bạn cần thông tin sâu hơn hoặc giao tiếp với tracker nâng cao
settings-general-interface-dev_mode-label = Chế độ nhà phát triển
settings-general-interface-serial_detection = Phát hiện thiết bị Serial
settings-general-interface-serial_detection-description = Lựa chọn này sẽ hiển thị thông báo mỗi lần thiết bị Serial mà có thể là tracker.Lựa chọn này có thể giúp quá trình cài đạt tracker
settings-general-interface-serial_detection-label = Phát hiện thiết bị Serial
settings-general-interface-lang = Chọn ngôn ngữ (Change language)
settings-general-interface-lang-description = Đổi ngôn ngữ cố định (Change the default language you want to use)
settings-general-interface-lang-placeholder = Chọn ngôn ngữ để sử dụng (Select the language to use)

## Serial settings
settings-serial = Bảng điều khiển serial
# This cares about multilines
settings-serial-description =
    Đâu là thông tin trực tiếp về giao tiếp với Serial.
    Hữu dụng nếu muốn biết phần mềm firmware có vấn đề không.
settings-serial-connection_lost = Kết nối đến Serial đã mất, đang kết nối lại...
settings-serial-reboot = Khởi động lại
settings-serial-factory_reset = Khôi phục cài đặt gối
settings-serial-get_infos = Lấy thông tin
settings-serial-serial_select = Chọn cổng Serial
settings-serial-auto_dropdown_item = Tự động

## OSC router settings
settings-osc-router = OSC router
# This cares about multilines
settings-osc-router-description =
    Forward OSC messages from another program.
    Useful for using another OSC program with VRChat for example.
settings-osc-router-enable = Enable
settings-osc-router-enable-description = Toggle the forwarding of messages.
settings-osc-router-enable-label = Enable
settings-osc-router-network = Network ports
# This cares about multilines
settings-osc-router-network-description =
    Set the ports for listening and sending data.
    These can be the same as other ports used in the SlimeVR server.
settings-osc-router-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9002)
settings-osc-router-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-router-network-address = Network address
settings-osc-router-network-address-description = Set the address to send out data at.
settings-osc-router-network-address-placeholder = IPV4 address

## OSC VRChat settings
settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description =
    Đổi cài đặt dành riêng cho VRChat để nhận thông tin từ kính thực tế ảo và gửi
    thông tin tracker để track toàn thân (Hoạt động trên Quest một mình standalone).
settings-osc-vrchat-enable = Bật
settings-osc-vrchat-enable-description = Chình gửi và nhận thông tin
settings-osc-vrchat-enable-label = Bật
settings-osc-vrchat-network = Cổng mạng
settings-osc-vrchat-network-description = Chọn cổng mạng để nghe và gửi thông tin lên VRChat
settings-osc-vrchat-network-port_in =
    .label = Cổng vào
    .placeholder = Cổng vào (Cố định: 9001)
settings-osc-vrchat-network-port_out =
    .label = Cổng ra
    .placeholder = Cổng ra (Cố định: 9000)
settings-osc-vrchat-network-address = Địa chỉ mạng
settings-osc-vrchat-network-address-description = Chọn địa chỉ mạng nào để gứi lên VRChat (Hãy kiểm tra cài đặt Wi-Fi trên thiết bị để tìm cái này)
settings-osc-vrchat-network-address-placeholder = Địa chỉ IP cho VRChat
settings-osc-vrchat-network-trackers = Trackers
settings-osc-vrchat-network-trackers-description = Chỉnh gửi và nhận thông tin
settings-osc-vrchat-network-trackers-chest = Ngực
settings-osc-vrchat-network-trackers-waist = Eo
settings-osc-vrchat-network-trackers-knees = Đầu gối
settings-osc-vrchat-network-trackers-feet = Bàn chân
settings-osc-vrchat-network-trackers-elbows = Khuỷu tay

## Setup/onboarding menu
onboarding-skip = Bỏ qua cài đặt
onboarding-continue = Tiếp tục
onboarding-wip = Đang làm dở (vui lòng quay lại sau update)

## WiFi setup
onboarding-wifi_creds-back = Quay lại đoạn giới thiệuthiệu
onboarding-wifi_creds = Bỏ thông tin Wi-Fi ở đây
# This cares about multilines
onboarding-wifi_creds-description =
    Tracker sẽ sử dụng thông tin sau để kết nối không dây
    vui lòng bỏ thông tin bạn muốn kết nối
onboarding-wifi_creds-skip = Bỏ qua cài đặt Wi-Fi
onboarding-wifi_creds-submit = Thiết lập!
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = Enter SSID
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup
onboarding-reset_tutorial-back = Quay lại đo đạt vị trí
onboarding-reset_tutorial = Làm lại hướng dẫn
onboarding-reset_tutorial-description = Tính năng này chưa hoàn thiện, vui lòng bấm tiếp tục và quay lại sau update

## Setup start
onboarding-home = Chào mừng đến với SlimeVR!
# This cares about multilines and it's centered!!
onboarding-home-description =
    Đem đến đo toàn thân
    tới tất cả
onboarding-home-start = Hãy bắt đầu cài đặt!

## Enter VR part of setup
onboarding-enter_vr-back = Quay lại giao bộ phận tracker
onboarding-enter_vr-title = Đến giờ lên VR!
onboarding-enter_vr-description = Đeo tất cả tracker và lên VR!
onboarding-enter_vr-ready = Sẵn sàng!

## Setup done
onboarding-done-title = Bạn đã hoàn thành cài đặt!
onboarding-done-description = Hãy tận hưởng đo toàn thân
onboarding-done-close = Đóng hướng dẫn

## Tracker connection setup
onboarding-connect_tracker-back = Quay lại tra thông tin Wi-Fi
onboarding-connect_tracker-title = Kết nối tracker
onboarding-connect_tracker-description-p0 = Đến giờ đoạn hay nhất, kết nối tất cả tracker!
onboarding-connect_tracker-description-p1 = Đơn thuần kết nối tất cả tracker chưa kết nối, qua cổng USB
onboarding-connect_tracker-issue-serial = Mình có vấn đề kết nối!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-connecting = Đang gửi thông tin Wi-Fi
onboarding-connect_tracker-connection_status-connected = Đã kết nối Wi-Fi
onboarding-connect_tracker-connection_status-error = Không thể kết nối Wi-Fi
onboarding-connect_tracker-connection_status-start_connecting = Đang tìm tracker
onboarding-connect_tracker-connection_status-handshake = Đã kết nối với máy chủ
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
onboarding-connect_tracker-next = Tôi đã kết nối tất cả tracker

## Tracker assignment setup
onboarding-assign_trackers-back = Quay lại tra thông tin Wi-Fi
onboarding-assign_trackers-title = Giao tracker
onboarding-assign_trackers-description = Hãy chọn tracker nào nằm ở đâu. Chọn vị trí bạn muốn giao tracker cho
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned } of { $trackers ->
    [one] 1 tracker
    *[other] { $trackers } trackers
} assigned
onboarding-assign_trackers-advanced = Xem vị trí mở rộng
onboarding-assign_trackers-next = Hoàn thành

## Tracker manual mounting setup
onboarding-manual_mounting-back = Quay lại đến vào VR
onboarding-manual_mounting = Đặt thủ công
onboarding-manual_mounting-description = Bấm từng tracker một và chọn xem nó được gắn theo chiều nào
onboarding-manual_mounting-auto_mounting = Gắn tự động
onboarding-manual_mounting-next = Bước tiếp theo

## Tracker automatic mounting setup
onboarding-automatic_mounting-back = Quay lại đến vào VR
onboarding-automatic_mounting-title = Đo vị trí đặt
onboarding-automatic_mounting-description = Để SlimeVR hoạt động, bạn cần đạt chiều hướng quay của tracker để khớp với điểm đặt tracker trên cơ thể
onboarding-automatic_mounting-manual_mounting = Đặt vị trí thủ công
onboarding-automatic_mounting-next = Bước tiếp theo
onboarding-automatic_mounting-prev_step = Bước trước
onboarding-automatic_mounting-done-title = Hướng quay đã được đo.
onboarding-automatic_mounting-done-description = Cài đặt vị trí đã hoàn thành!
onboarding-automatic_mounting-done-restart = Quay lại bắt đầu
onboarding-automatic_mounting-mounting_reset-title = Reset vị trí
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Ngồi xổm trong tư thế "skiing" với hai chân cong, thân trên nghiêng về phía trước và hai cánh tay cong.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Nhấn nút "Reset Mounting" và đợi trong 3 giây trước khi các vòng quay giá đỡ của thiết bị theo dõi sẽ được đặt lại.
onboarding-automatic_mounting-preparation-title = Chuẩn bị
onboarding-automatic_mounting-preparation-step-0 = 1. Đứng thẳng với hai cánh tay sang hai bên.
onboarding-automatic_mounting-preparation-step-1 = 2. Nhấn nút "Reset" và đợi trong 3 giây trước khi trình theo dõi sẽ đặt lại.
onboarding-automatic_mounting-put_trackers_on-title = Hãy đeo tracker lên người
onboarding-automatic_mounting-put_trackers_on-description = Để đo chiều quay của tracker, phần mềm sẽ sử dụng tracker mà bạn đã giao cho. Hãy đeo lên tất cả tracker, và xem cái nào là cái nào trên hình bên phải
onboarding-automatic_mounting-put_trackers_on-next = Hoàn thành (Gắn xong)

## Tracker manual proportions setup
onboarding-manual_proportions-back = Quay lại hướng dẫn reset
onboarding-manual_proportions-title = Cài bộ phận cơ thể thủ công
onboarding-manual_proportions-precision = Chỉnh độ chính xác
onboarding-manual_proportions-auto = Đo cơ thể tự động

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = Quay lại hướng dẫn reset
onboarding-automatic_proportions-title = Đo cơ thể
onboarding-automatic_proportions-description = Để SlimeVR hoạt động, phần mềm cần biết chiều dài của thân. Bước đo tự động này sẽ giúp bạn.
onboarding-automatic_proportions-manual = Đo thủ công
onboarding-automatic_proportions-prev_step = Bước trước
onboarding-automatic_proportions-put_trackers_on-title = Đeo tất cả
onboarding-automatic_proportions-put_trackers_on-description = Để đo bộ phận, phần mềm sẽ sử dụng tracker mà bạn đã giao cho. Hãy đeo lên tất cả tracker, và xem cái nào là cái nào trên hình bên phải.
onboarding-automatic_proportions-put_trackers_on-next = Hoàn thành (Gắn xong)
onboarding-automatic_proportions-preparation-title = Chuẩn bị
onboarding-automatic_proportions-preparation-description = Đặt ghế đằng sau bạn thẳng trong khu vực chơi VR. Hãy chuẩn bị ngồi trong khi cài đặt đo thân tự động.
onboarding-automatic_proportions-preparation-next = Tôi đang đứng trước ghế ngồi
onboarding-automatic_proportions-start_recording-title = Chuẩn bị cử động
onboarding-automatic_proportions-start_recording-description = Phần mềm sẽ ghi một số củ động cử chỉ. Bước này sẽ bắt đầu ở màn hình tiếp theo. Hãy chuẩn bị khi bấm bắt đầu
onboarding-automatic_proportions-start_recording-next = Bắt đầu quay
onboarding-automatic_proportions-recording-title = Quay
onboarding-automatic_proportions-recording-description-p0 = Đang quay...
onboarding-automatic_proportions-recording-description-p1 = Hãy cử động theo hướng dẫn sau:
onboarding-automatic_proportions-recording-steps-0 = Cong đầu gối vài lần.
onboarding-automatic_proportions-recording-steps-1 = Ngồi lên ghế rồi đứng lên.
onboarding-automatic_proportions-recording-steps-2 = Vặn thân trên sang trái, sau đó uốn cong sang phải.
onboarding-automatic_proportions-recording-steps-3 = Vặn thân trên sang phải, sau đó uốn cong sang trái.
onboarding-automatic_proportions-recording-steps-4 = Đảo xung quanh cho đến khi bộ đếm thời gian kết thúc.
onboarding-automatic_proportions-recording-processing = Đang xử lí kết quả
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 15)
onboarding-automatic_proportions-recording-timer = { $time ->
    [one] 1 second left
    *[other] { $time } seconds left
}
onboarding-automatic_proportions-verify_results-title = Xác nhận kết quả
onboarding-automatic_proportions-verify_results-description = Kiểm tra kết quả dưới đây, nó có chính xác không?
onboarding-automatic_proportions-verify_results-results = Đang ghi kết quả
onboarding-automatic_proportions-verify_results-processing = Đang xử lí kết quả
onboarding-automatic_proportions-verify_results-redo = Ghi lại
onboarding-automatic_proportions-verify_results-confirm = Kết quả này chính xác!
onboarding-automatic_proportions-done-title = Thân đã được đo và lưu.
onboarding-automatic_proportions-done-description = Quá trình đo thân đã hoàn thành!

## Home
home-no_trackers = Không tracker nào được phát hiện hoặc giao vị trí
