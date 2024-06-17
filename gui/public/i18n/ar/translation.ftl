# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = يتم التوصيل بالسيرفر
websocket-connection_lost = انقطع الاتصال بالسيرفر. يتم إعادة التوصيل...

## Update notification

version_update-title = نسخة جديدة متوفرة: { $version }
version_update-description = سيؤدي النقر على "{ version_update-update }" إلى تنزيل مثبت SlimeVR نيابة عنك.
version_update-update = تحديث
version_update-close = أغلق

## Tips

tips-find_tracker = لست متأكد من أجهزة التعقب؟ قم بتحريك الجهاز لتحديد العنصر المناسب.
tips-do_not_move_heels = يرجى عدم تحريك كاحليك أثناء التسجيل!
tips-file_select = اسحب الملفات وأفلتها لاستخدامها أو <u> تصفح </ u>
tips-tap_setup = يمكنك النقر ببطء مرتين على جهاز التعقب لاختياره بدلاً من تحديده من القائمة.
tips-turn_on_tracker = هل تستخدم أجهزة تعقب SlimeVR الرسمية؟ تذكر  <b><em> أن تشغل أجهزة التعقب </em></b> بعد توصيلها بالكمبيوتر!
tips-failed_webgl = فشل تهيئة WebGL.

## Body parts

body_part-NONE = غير محدد
body_part-HEAD = الرأس
body_part-NECK = العنق
body_part-RIGHT_SHOULDER = الكتف الأيمن
body_part-RIGHT_UPPER_ARM = العضد الأيمن
body_part-RIGHT_LOWER_ARM = الساعد الأيمن
body_part-RIGHT_HAND = اليد اليمنى
body_part-RIGHT_UPPER_LEG = الفخذ الأيمن
body_part-RIGHT_LOWER_LEG = الكاحل الأيمن
body_part-RIGHT_FOOT = القدم اليمنى
body_part-UPPER_CHEST = أعلى الصدر
body_part-CHEST = الصدر
body_part-WAIST = الخصر
body_part-HIP = الورك
body_part-LEFT_SHOULDER = الكتف الأيسر
body_part-LEFT_UPPER_ARM = العضد الأيسر
body_part-LEFT_LOWER_ARM = الساعد الأيسر
body_part-LEFT_HAND = اليد اليسرى
body_part-LEFT_UPPER_LEG = الفخذ الأيسر
body_part-LEFT_LOWER_LEG = الكاحل الأيسر
body_part-LEFT_FOOT = القدم اليسرى

## Proportions

skeleton_bone-NONE = غير محدد
skeleton_bone-HEAD = إمالة الرأس
skeleton_bone-NECK = طول العنق
skeleton_bone-torso_group = طول الجذع
skeleton_bone-UPPER_CHEST = طول أعلى الصدر
skeleton_bone-CHEST_OFFSET = درجة تشريد الصدر
skeleton_bone-CHEST = طول الصدر
skeleton_bone-WAIST = طول الخصر
skeleton_bone-HIP = طول الورك
skeleton_bone-HIP_OFFSET = درجة تشريد الورك
skeleton_bone-HIPS_WIDTH = عرض الورك
skeleton_bone-leg_group = طول الساق
skeleton_bone-UPPER_LEG = طول الفخذ
skeleton_bone-LOWER_LEG = طول الساق السفلي
skeleton_bone-FOOT_LENGTH = طول القدم
skeleton_bone-FOOT_SHIFT = إمالة القدم
skeleton_bone-SKELETON_OFFSET = درجة تشريد الهيكل العظمي
skeleton_bone-SHOULDERS_DISTANCE = مسافة الكتفين
skeleton_bone-SHOULDERS_WIDTH = عرض الكتفين
skeleton_bone-arm_group = طول الذراع
skeleton_bone-UPPER_ARM = طول العضد
skeleton_bone-LOWER_ARM = طول الساعد
skeleton_bone-HAND_Y = مسافة اليد Y
skeleton_bone-HAND_Z = مسافة اليد Z
skeleton_bone-ELBOW_OFFSET = درجة تشريد الكوع

## Tracker reset buttons

reset-reset_all = إعادة تعيين جميع النسب
reset-full = اعاده تعيين
reset-mounting = إعادة تعيين التركيب
reset-yaw = إعادة تعيين الانعراج

## Serial detection stuff

serial_detection-new_device-p0 = تم اكتشاف جهاز تسلسلي جديد!
serial_detection-new_device-p1 = أدخل بيانات اعتماد الواي فاي  الخاصة بك!
serial_detection-new_device-p2 = يرجى تحديد ما تريد القيام به
serial_detection-open_wifi = اتصل بشبكة الواي فاي
serial_detection-open_serial = افتح وحدة التحكم التسلسلية
serial_detection-submit = إرسال!
serial_detection-close = أغلق

## Navigation bar

navbar-home = الصفحة الرئيسية
navbar-body_proportions = نسب الجسم
navbar-trackers_assign = تعيين جهاز التعقب
navbar-mounting = معايرة التركيب
navbar-onboarding = معالج الإعداد
navbar-settings = الإعدادات

## Biovision hierarchy recording

bvh-start_recording = سجل بي في ايتش
bvh-recording = تسجيل...

## Tracking pause

tracking-unpaused = إيقاف التعقب مؤقتا
tracking-paused = إلغاء الإيقاف التعقب

## Widget: Overlay settings

widget-overlay = التراكب
widget-overlay-is_visible_label = إظهار التراكب في ستيم في ار
widget-overlay-is_mirrored_label = عكس تراكب الشاشة

## Widget: Drift compensation

widget-drift_compensation-clear = حذف تعويض الانجراف

## Widget: Clear Reset Mounting

widget-clear_mounting = مسح إعادة تعيين التركيب

## Widget: Developer settings

widget-developer_mode = وضع المطوّر
widget-developer_mode-high_contrast = تباين عالي
widget-developer_mode-precise_rotation = دوران دقيق
widget-developer_mode-fast_data_feed = تغذية البيانات السريعة
widget-developer_mode-filter_slimes_and_hmd = تصفية السليمس و ايتش أم دي
widget-developer_mode-sort_by_name = فرز بالاسم
widget-developer_mode-raw_slime_rotation = الدوران الصافي
widget-developer_mode-more_info = المزيد

## Widget: IMU Visualizer

widget-imu_visualizer = دوران
widget-imu_visualizer-rotation_raw = صافي
widget-imu_visualizer-rotation_preview = عرض مسبق
widget-imu_visualizer-rotation_hide = إخفاء

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = إظهار الهيكل العظمي
widget-skeleton_visualizer-hide = إخفاء

## Tracker status

tracker-status-none = لا توجد حالة
tracker-status-busy = مشغول
tracker-status-error = خطأ
tracker-status-disconnected = فقد الاتصال
tracker-status-occluded = محجوب
tracker-status-ok = حسنًا
tracker-status-timed_out = انتهت المهله

## Tracker status columns

tracker-table-column-name = الاسم
tracker-table-column-type = النوع
tracker-table-column-battery = البطارية
tracker-table-column-ping = بينج
tracker-table-column-tps = تي بي أس
tracker-table-column-temperature = درجة الحرارة درجة مئوية
tracker-table-column-linear-acceleration = تسارع X/Y/Z
tracker-table-column-rotation = دوران X / Y / Z
tracker-table-column-position = موضع X/Y/Z
tracker-table-column-url = عنوان URL

## Tracker rotation

tracker-rotation-front = المقدمة
tracker-rotation-front_left = أمامي-يسار
tracker-rotation-front_right = أمامي -يمين
tracker-rotation-left = اليسار
tracker-rotation-right = اليمين
tracker-rotation-back = الخلف
tracker-rotation-back_left = الخلف اليسار
tracker-rotation-back_right = الخلف الأيمن
tracker-rotation-custom = مخصص
tracker-rotation-overriden = (تم تجاوزه عن طريق إعادة الضبط المتصاعد)

## Tracker information

tracker-infos-manufacturer = المصنّع
tracker-infos-display_name = اسم العرض
tracker-infos-custom_name = اسم مخصص
tracker-infos-url = عنوان URL لجهاز التعقب
tracker-infos-version = إصدار البرنامج الثابت
tracker-infos-hardware_rev = مراجعة الأجهزة
tracker-infos-hardware_identifier = معرف الجهاز
tracker-infos-imu = مستشعر IMU
tracker-infos-board_type = اللوحة الرئيسية
tracker-infos-network_version = نسخة البروتوكول

## Tracker settings

tracker-settings-back = ارجع إلى قائمة أجهزة التعقب
tracker-settings-title = إعدادات جهاز التعقب
tracker-settings-assignment_section = التكليف
tracker-settings-assignment_section-description = أي جزء من الجسم تم تعيين جهاز التعقب له.
tracker-settings-assignment_section-edit = تعديل التكليف
tracker-settings-mounting_section = مكان التركيب
tracker-settings-mounting_section-description = أين تم تركيب جهاز التعقب؟
tracker-settings-mounting_section-edit = تعديل التركيب
tracker-settings-drift_compensation_section = السماح بتعويض الانجراف
tracker-settings-drift_compensation_section-description = هل يجب أن يعوض جهاز التعقب عن انحرافه عند تمكين تعويض الانجراف؟
tracker-settings-drift_compensation_section-edit = السماح بتعويض الانجراف
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = اسم جهاز التعقب
tracker-settings-name_section-description = أعطها لقب لطيف :)
tracker-settings-name_section-placeholder = ساق نايتي بيست اليسرى
tracker-settings-forget = انسي جهاز التعقب
tracker-settings-forget-description = يزيل جهاز التعقب من خادم SlimeVR ويمنعه من الاتصال به حتى يتم إعادة تشغيل الخادم. لن تضيع تكوين جهاز التعقب.
tracker-settings-forget-label = ننسى جهاز التعقب

## Tracker part card info

tracker-part_card-no_name = لا يوجد اسم
tracker-part_card-unassigned = غير محدد

## Body assignment menu

body_assignment_menu = أين تريد أن يكون هذا الجهاز التعقب؟
body_assignment_menu-description = اختر الموقع الذي تريد تعيين هذا جهاز التعقب. بدلاً من ذلك، يمكنك اختيار إدارة جميع أجهزة التعقب مرة واحدة بدلاً من إدارة كل جهاز تلو الآخر.
body_assignment_menu-show_advanced_locations = إظهار مواقع التعيين المتقدمة
body_assignment_menu-manage_trackers = إدارة جميع أجهزة التعقب
body_assignment_menu-unassign_tracker = إلغاء تعيين جهاز التعقب

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = أي جهاز تعقب لتعيينه إلى
tracker_selection_menu-NONE = ما هو جهاز تعقب تريد أن يكون غير معين؟
tracker_selection_menu-HEAD = { -tracker_selection-part } الرأس؟
tracker_selection_menu-NECK = { -tracker_selection-part } العنق؟
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } الكتف الأيمن؟
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } العضد الأيمن؟
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } الساعد الأيمن؟
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } اليد اليمنى؟
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } الفخذ الأيمن؟
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } الكاحل الأيمن؟
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } القدم اليمنى؟
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } وحدة التحكم اليمنى؟
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } أعلى الصدر؟
tracker_selection_menu-CHEST = { -tracker_selection-part } الصدر؟
tracker_selection_menu-WAIST = { -tracker_selection-part } الخصر؟
tracker_selection_menu-HIP = { -tracker_selection-part } الورك؟
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } الكتف الأيسر؟
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } العضد الأيسر؟
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } الساعد الأيسر؟
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } اليد اليسرى؟
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } الفخذ الأيسر؟
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } الكاحل الأيسر؟
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } القدم اليسرى؟
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } وحدة تحكم اليسار؟
tracker_selection_menu-unassigned = أجهزة تعقب غير معينة
tracker_selection_menu-assigned = أجهزة تعقب معينة
tracker_selection_menu-dont_assign = لا تعين
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>تحذير:</b> يمكن أن يكون جهاز تعقب الرقبة مميتا إذا تم شدها كثيراً،
    يمكن للحزام أن يقطع الدورة الدموية إلى رأسك!
tracker_selection_menu-neck_warning-done = أفهم المخاطر
tracker_selection_menu-neck_warning-cancel = إلغاء

## Mounting menu

mounting_selection_menu = أين تريد أن يكون جهاز التعقب؟
mounting_selection_menu-close = أغلق

## Sidebar settings

settings-sidebar-title = الإعدادات
settings-sidebar-general = الاعدادات العامة
settings-sidebar-tracker_mechanics = ميكانيكا جهاز التعقب
settings-sidebar-fk_settings = إعدادات التعقب
settings-sidebar-gesture_control = التحكم بالإيماءات
settings-sidebar-interface = واجهة المستخدم
settings-sidebar-osc_router = راوتر أوه أس سي
settings-sidebar-osc_trackers = أجهزة تعقب في ار تشات أوه أس سي
settings-sidebar-utils = الأدوات المساعدة
settings-sidebar-serial = وحدة التحكم التسلسلية
settings-sidebar-appearance = مظهر
settings-sidebar-notifications = إشعارات

## SteamVR settings

settings-general-steamvr = ستيم في ار
settings-general-steamvr-subtitle = أجهزة تعقب ستيم في ار
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    تمكين أو تعطيل أجهزة التعقب المحددة لستيم في ار.
    مفيد فقط للألعاب أو التطبيقات التي تدعم أجهزة تعقب معينة.
settings-general-steamvr-trackers-waist = الخصر
settings-general-steamvr-trackers-chest = الصدر
settings-general-steamvr-trackers-feet = القدمين
settings-general-steamvr-trackers-knees = الركبتين
settings-general-steamvr-trackers-elbows = الكوعين
settings-general-steamvr-trackers-hands = اليدين
settings-general-steamvr-trackers-tracker_toggling = تعيين جهاز التعقب تلقائي
settings-general-steamvr-trackers-tracker_toggling-description = يتعامل تلقائيا مع تبديل أجهزة تعقب SteamVR أو إيقاف تشغيلها اعتمادا على مهام التعقب الحالية
settings-general-steamvr-trackers-tracker_toggling-label = تعيين جهاز التعقب التلقائي
settings-general-steamvr-trackers-hands-warning =
    <b>تحذير:</b> ستتجاوز أجهزة تعقب اليد وحدات التحكم الخاصة بك.
    هل أنت متأكد؟
settings-general-steamvr-trackers-hands-warning-cancel = إلغاء
settings-general-steamvr-trackers-hands-warning-done = نعم

## Tracker mechanics

settings-general-tracker_mechanics = ميكانيكا جهاز التعقب
settings-general-tracker_mechanics-filtering = تصفية
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    اختر نوع التصفية لأجهزة التعقب الخاصة بك.
    يتنبأ التنبؤ بالحركة بينما يعمل على تنعيم الحركة.
settings-general-tracker_mechanics-filtering-type = نوع التصفية
settings-general-tracker_mechanics-filtering-type-none = بدون تصفية
settings-general-tracker_mechanics-filtering-type-none-description = استخدم التدوير كما هو. لن تفعل أي تصفية
settings-general-tracker_mechanics-filtering-type-smoothing = التنعيم
settings-general-tracker_mechanics-filtering-type-smoothing-description = ينعم الحركات لكنه يزيد من وقت الاستجابة.
settings-general-tracker_mechanics-filtering-type-prediction = التنبؤ
settings-general-tracker_mechanics-filtering-type-prediction-description = يقلل من وقت الإستجابة ويجعل الحركات أكثر سرعة ، ولكنه قد يزيد من التوتر.
settings-general-tracker_mechanics-filtering-amount = المبلغ
settings-general-tracker_mechanics-drift_compensation = تعويض الانجراف
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    يعوض عن انجراف انعراج وحدة IMU بتطبيق دوران عكسي.
    قم بتغيير كمية التعويض وعدد عمليات إعادة التعيين التي يتم أخذها في الاعتبار.
settings-general-tracker_mechanics-drift_compensation-enabled-label = تعويض الانجراف
settings-general-tracker_mechanics-drift_compensation-amount-label = مبلغ التعويض
settings-general-tracker_mechanics-drift_compensation-max_resets-label = استخدام ما يصل إلى x عمليات إعادة التعيين الأخيرة

## FK/Tracking settings

settings-general-fk_settings = إعدادات التعقب
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = فلور كليب
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = تصحيح التزحلق
settings-general-fk_settings-leg_tweak-toe_snap = انجذاب إلى أصابع القدم
settings-general-fk_settings-leg_tweak-foot_plant = تثبيت اصبع القدم
settings-general-fk_settings-leg_tweak-skating_correction-amount = قوة تصحيح التزحلق
settings-general-fk_settings-leg_tweak-skating_correction-description = تصحيح التزحلق يصحح التزحلق على الجليد، ولكن يمكن أن يقلل الدقة في أنماط حركة معينة. عند تمكين هذا، تأكد من إعادة الضبط وإعادة المعايرة بالكامل في اللعبة.
settings-general-fk_settings-leg_tweak-floor_clip-description = يمكن أن يقلل التثبيت الأرضية من الإجتياز الأرضية أو حتى يزيله. عند تمكين هذا، تأكد من إعادة الضبط وإعادة المعايرة بالكامل في اللعبة.
settings-general-fk_settings-leg_tweak-toe_snap-description = الانجذاب إلى أصابع القدم يحاول تخمين دوران قدميك إذا لم تكن أجهزة تعقب القدم قيد الاستخدام.
settings-general-fk_settings-leg_tweak-foot_plant-description = تثبيت اصبع القدم يحاول تخمين دوران قدميك إذا لم تكن أجهزة تعقب القدم قيد الاستخدام.
settings-general-fk_settings-leg_fk = تعقب الساق
settings-general-fk_settings-leg_fk-reset_mounting_feet-description = تمكين إعادة ضبط تركيب القدمين عن طريق المشي على رؤوس الأصابع.
settings-general-fk_settings-leg_fk-reset_mounting_feet = إعادة تعيين تركيب القدمين
settings-general-fk_settings-arm_fk = تعقب الذراع
settings-general-fk_settings-arm_fk-description = تغيير طريقة تعقب الذراعين.
settings-general-fk_settings-arm_fk-force_arms = إجبار الذراعين من ايتش أم دي
settings-general-fk_settings-arm_fk-reset_mode-description = قم بتغيير وضع الذراع المتوقع لإعادة ضبط المتصاعد.
settings-general-fk_settings-arm_fk-back = العودة
settings-general-fk_settings-arm_fk-back-description = الوضع الافتراضي، مع وضع الذراعين العلويين إلى الخلف والساعدين للأمام.
settings-general-fk_settings-arm_fk-tpose_up = تي بوز (أعلى)
settings-general-fk_settings-arm_fk-tpose_up-description = يتوقع أن تكون ذراعيك لأسفل على الجانبين أثناء إعادة الضبط الكامل ، و 90 درجة حتى الجانبين أثناء إعادة ضبط التركيب.
settings-general-fk_settings-arm_fk-tpose_down = تي بوز (لأسفل)
settings-general-fk_settings-arm_fk-tpose_down-description = يتوقع أن تكون ذراعيك 90 درجة لأعلى على الجانبين أثناء إعادة الضبط الكامل ، ولأسفل على الجانبين أثناء إعادة ضبط التركيب.
settings-general-fk_settings-arm_fk-forward = أمامي
settings-general-fk_settings-arm_fk-forward-description = يتوقع أن تكون ذراعيك 90 درجة للأمام. مفيد ل VTubing.
settings-general-fk_settings-skeleton_settings-toggles = تبديل الهيكل العظمي
settings-general-fk_settings-skeleton_settings-description = تبديل إعدادات الهيكل العظمي أو إيقافه. يوصى بتركها شغالة.
settings-general-fk_settings-skeleton_settings-extended_spine_model = نموذج العمود الفقري الممتد
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = نموذج الحوض الممتد
settings-general-fk_settings-skeleton_settings-extended_knees_model = نموذج الركبة الممتدة
settings-general-fk_settings-skeleton_settings-ratios = نسب الهيكل العظمي
settings-general-fk_settings-skeleton_settings-ratios-description = تغيير قيم إعدادات الهيكل العظمي. قد تحتاج إلى ضبط النسب الخاصة بك بعد تغييرها.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = خصص الخصر من الصدر إلى الورك
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = خصص الخصر من الصدر إلى الساقين
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = خصص الورك من الصدر إلى الساقين
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = خصص الورك من الخصر إلى الساقين
settings-general-fk_settings-skeleton_settings-interp_hip_legs = متوسط انعراج الفخذ وتدحرج مع الساقين'
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = متوسط الانحراف وتدحرج مع الكاحلين
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = متوسط انحراف الركبتين ولفة مع الكاحلين
settings-general-fk_settings-self_localization-title = وضع Mocap
settings-general-fk_settings-self_localization-description = يسمح وضع Mocap للهيكل العظمي بتعقب موضعه تقريبا بدون سماعة رأس أو أجهزة تعقب أخرى. لاحظ أن هذا يتطلب أجهزة تعقب القدمين والرأس للعمل ولا تزال تجريبية.
settings-general-fk_settings-vive_emulation-title = محاكاة فايف
settings-general-fk_settings-vive_emulation-description = محاكاة مشاكل تعقب الخصر التي تعاني منها أجهزة تعقب فايف. هذه مزحة وتجعل التتبع أسوأ.
settings-general-fk_settings-vive_emulation-label = تمكين محاكاة فايف

## Gesture control settings (tracker tapping)

settings-general-gesture_control = التحكم بالإيماءات
settings-general-gesture_control-subtitle = عمليات إعادة التعيين المستندة على النقر
settings-general-gesture_control-description = يسمح بتشغيل عملية إعادة التعيين من خلال النقر على جهاز التعقب. يتم استخدام جهاز التعقب الأعلى على جذعك لإعادة ضبط سريع ، ويتم استخدام جهاز التعقب الأعلى على ساقك اليسرى لإعادة ضبط ، ويتم استخدام جهاز التعقب الأعلى على ساقك اليمنى إعادة ضبط التركيب . تجدر الإشارة إلى أن النقرات يجب أن تتم خلال 0.6 ثانية لكي يتم تسجيلها.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [zero] { $amount } نقرات
        [one] نقرة واحدة
        [two] نقرتان
        [few] { $amount } نقرات
        [many] { $amount } نقرات
       *[other] { $amount } نقرات
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [zero] لا أجهزة تعقب
        [one] جهاز تعقب واحد
        [two] جهازي تعقب
        [few] { "" }
        [many] { "" }
       *[other] { $amount } أجهزة تعقب
    }
settings-general-gesture_control-yawResetEnabled = تمكين النقر لإعادة التعيين الانعراج
settings-general-gesture_control-yawResetDelay = إعادة تعيين التأخير الانعراج
settings-general-gesture_control-yawResetTaps = عدد النقرات لإعادة تعيين الانعراج
settings-general-gesture_control-fullResetEnabled = تمكين النقر لإعادة التعيين الكامل
settings-general-gesture_control-fullResetDelay = تأخير إعادة التعيين الكامل
settings-general-gesture_control-fullResetTaps = عدد النقرات لإعادة التعيين الكامل
settings-general-gesture_control-mountingResetEnabled = تمكين النقر لإعادة تعيين التركيب
settings-general-gesture_control-mountingResetDelay = تأخير إعادة تعيين التركيب
settings-general-gesture_control-mountingResetTaps = نقرات لإعادة تعيين التركيب
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = أجهزة تعقب فوق قيمة القطع
settings-general-gesture_control-numberTrackersOverThreshold-description = قم بزيادة هذه القيمة إذا كان اكتشاف النقر لا يعمل. لا تقم بزيادته فوق ما هو مطلوب لجعل اكتشاف النقر يعمل لأنه قد يتسبب في المزيد من الإيجابيات الخاطئة.

## Appearance settings

settings-interface-appearance = مظهر
settings-general-interface-dev_mode = وضع المطوّر
settings-general-interface-dev_mode-description = يمكن أن يكون هذا الوضع مفيدًا إذا كنت بحاجة إلى بيانات متعمقة أو للتفاعل مع أجهزة التعقب المتصلة على مستوى أكثر تقدمًا.
settings-general-interface-dev_mode-label = وضع المطوّر
settings-general-interface-theme = موضوع اللون
settings-general-interface-lang = اختر اللغة
settings-general-interface-lang-description = قم بتغيير اللغة الافتراضية التي تريد استخدامها.
settings-general-interface-lang-placeholder = اختر اللغة التي تريد استخدامها
# Keep the font name untranslated
settings-interface-appearance-font = خط واجهة المستخدم الرسومية
settings-interface-appearance-font-description = هذا يغير الخط المستخدم من قبل الواجهة.
settings-interface-appearance-font-placeholder = الخط الافتراضي
settings-interface-appearance-font-os_font = خط نظام التشغيل
settings-interface-appearance-font-slime_font = الخط الافتراضي
settings-interface-appearance-font_size = قياس الخط الأساسي
settings-interface-appearance-font_size-description = يؤثر هذا على حجم خط الواجهة بأكملها باستثناء لوحة الإعدادات هذه.

## Notification settings

settings-interface-notifications = إشعارات
settings-general-interface-serial_detection = الكشف عن جهاز تسلسلي
settings-general-interface-serial_detection-description = سيعرض هذا الخيار نافذة منبثقة في كل مرة تقوم فيها بتوصيل جهاز تسلسلي جديد يمكن أن يكون جهاز تعقب. يساعد في تحسين عملية إعداد جهاز التعقب.
settings-general-interface-serial_detection-label = الكشف عن جهاز تسلسلي
settings-general-interface-feedback_sound = صوت ردود الفعل
settings-general-interface-feedback_sound-description = سيصدر هذا الخيار صوتًا عند تشغيل إعادة الضبط
settings-general-interface-feedback_sound-label = صوت ردود الفعل
settings-general-interface-feedback_sound-volume = حجم صوت ردود الفعل
settings-general-interface-connected_trackers_warning = تحذير عن أجهزة التعقب المتصلة
settings-general-interface-connected_trackers_warning-description = سيعرض هذا الخيار نافذة كل مرة تحاول فيها الخروج من SlimeVR أثناء وجود جهاز أو أكثر من أجهزة التعقب المتصلة. سيذكرك بإيقاف تشغيل أجهزة التعقب عند الانتهاء للحفاظ على عمر البطارية.
settings-general-interface-connected_trackers_warning-label = تحذير عن أجهزة التعقب المتصلة عند الخروج
settings-general-interface-use_tray = تصغير إلى علبة النظام
settings-general-interface-use_tray-description = يتيح لك إغلاق النافذة دون إغلاق خادم SlimeVR حتى تتمكن من الاستمرار في استخدامه دون إزعاجك من واجهة المستخدم الرسومية.
settings-general-interface-use_tray-label = تصغير إلى علبة النظام

## Serial settings

settings-serial = وحدة التحكم التسلسلية
# This cares about multilines
settings-serial-description =
    هذا هو موجز معلومات مباشر للاتصال التسلسلي.
    قد يكون مفيدًا إذا كنت بحاجة إلى معرفة إن كان البرنامج الثابت به خلل.
settings-serial-connection_lost = تم فقد الاتصال بالمسلسل ، جاري إعادة الاتصال...
settings-serial-reboot = إعادة التشغيل
settings-serial-factory_reset = إعادة التعيين إلى إعدادات المصنع
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>تحذير:</b> سيؤدي هذا إلى إعادة تعيين المتعقب إلى إعدادات المصنع.
    مما يعني أن إعدادات واي فاي والمعايرة <b>ستفقد جميعا!</b>
settings-serial-factory_reset-warning-ok = أنا أعرف ماذا أفعل
settings-serial-factory_reset-warning-cancel = إلغاء
settings-serial-get_infos = احصل على معلومات
settings-serial-serial_select = اختر منفذ تسلسلي
settings-serial-auto_dropdown_item = تلقائي
settings-serial-get_wifi_scan = احصل على فحص WiFi

## OSC router settings

settings-osc-router = راوتر أوه أس سي
# This cares about multilines
settings-osc-router-description =
    إعادة توجيه رسائل أوه أس سي من برنامج آخر.
    مفيد لاستخدام برنامج أوه أس سي آخر مع في ار تشات على سبيل المثال.
settings-osc-router-enable = تمكين
settings-osc-router-enable-description = تبديل إعادة توجيه الرسائل.
settings-osc-router-enable-label = تمكين
settings-osc-router-network = منافذ الشبكة
# This cares about multilines
settings-osc-router-network-description =
    اضبط المنافذ للاستماع وإرسال البيانات.
        يمكن أن تكون هذه هي نفس المنافذ الأخرى المستخدمة في خادم سلايم في ار.
settings-osc-router-network-port_in =
    .label = منفذ الدخول
    .placeholder = منفذ الدخول (الإفتراضي: 9002)
settings-osc-router-network-port_out =
    .label = منفذ الخروج
    .placeholder = منفذ الخروج (الإفتراضي: 9000)
settings-osc-router-network-address = عنوان الشبكة
settings-osc-router-network-address-description = قم بتعيين العنوان لإرسال البيانات إليه.
settings-osc-router-network-address-placeholder = عنوان آي بي في 4

## OSC VRChat settings

settings-osc-vrchat = أجهزة تعقب "في ار تشات أوه أس سي"
# This cares about multilines
settings-osc-vrchat-description =
    قم بتغيير الإعدادات الخاصة ب في ار تشات لتلقي بيانات ايتش أم دي وإرسالها
    بيانات أجهزة تعقب لتعقب الجسم (يعمل على كوست مستقل).
settings-osc-vrchat-enable = تمكين
settings-osc-vrchat-enable-description = بتبديل إرسال واستقبال البيانات.
settings-osc-vrchat-enable-label = تمكين
settings-osc-vrchat-network = منافذ الشبكة
settings-osc-vrchat-network-description = قم بتعيين المنافذ للاستماع وإرسال البيانات إلى في ار تشات
settings-osc-vrchat-network-port_in =
    .label = منفذ الدخول
    .placeholder = منفذ الدخول (الإفتراضي: 9001)
settings-osc-vrchat-network-port_out =
    .label = منفذ الخروج
    .placeholder = منفذ الخروج (الإفتراضي: 9000)
settings-osc-vrchat-network-address = عنوان الشبكة
settings-osc-vrchat-network-address-description = اختر العنوان الذي تريد إرسال البيانات إلى في ار تشات (تحقق من إعدادات واي فاي على جهازك)
settings-osc-vrchat-network-address-placeholder = عنوان آي بي الخاص بفي ار تشات
settings-osc-vrchat-network-trackers = أجهزة التعقب
settings-osc-vrchat-network-trackers-description = تبديل إرسال أجهزة تتبع محددة عبر أوه أس سي.
settings-osc-vrchat-network-trackers-chest = الصدر
settings-osc-vrchat-network-trackers-hip = الورك
settings-osc-vrchat-network-trackers-knees = الركبتين
settings-osc-vrchat-network-trackers-feet = القدمين
settings-osc-vrchat-network-trackers-elbows = الكوعين

## VMC OSC settings

settings-osc-vmc = التقاط الحركة الافتراضية
# This cares about multilines
settings-osc-vmc-description =
    قم بتغيير الإعدادات الخاصة ببروتوكول التقاط الحركة الافتراضية
    لإرسال بيانات عظام سلايم في ار وتلقي بيانات العظام من تطبيقات أخرى.
settings-osc-vmc-enable = تمكين
settings-osc-vmc-enable-description = تبديل إرسال واستقبال البيانات.
settings-osc-vmc-enable-label = تمكين
settings-osc-vmc-network = منافذ الشبكة
settings-osc-vmc-network-description = قم بتعيين المنافذ للاستماع وإرسال البيانات إلى التقاط الحركة الافتراضية
settings-osc-vmc-network-port_in =
    .label = منفذ الدخول
    .placeholder = منفذ الدخول (الافتراضي: 39540)
settings-osc-vmc-network-port_out =
    .label = منفذ الخروج
    .placeholder = منفذ الخروج (الافتراضي: 39539)
settings-osc-vmc-network-address = عنوان الشبكة
settings-osc-vmc-network-address-description = قم بتعيين العنوان لإرسال البيانات إلى التقاط الحركة الافتراضية.
settings-osc-vmc-network-address-placeholder = عنوان آي بي في 4
settings-osc-vmc-vrm = نموذج في ار إم
settings-osc-vmc-vrm-description = قم بتحميل نموذج في ار إم للسماح بتركيز الرأس وتمكين توافق أعلى مع تطبيقات الأخرى
settings-osc-vmc-vrm-model_unloaded = لم يتم تحميل أي نموذج
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] تحميل النموذج: { $name }
       *[other] تم تحميل نموذج بدون عنوان
    }
settings-osc-vmc-vrm-file_select = اسحب نموذج وأفلته لاستخدامه أو <u> تصفح </ u>
settings-osc-vmc-anchor_hip = ثبت في الوركين
settings-osc-vmc-anchor_hip-description = ثبت التعقب في الوركين، هو مفيد إن كنت تيوبنغ جالسًا. في حالة التعطيل، قم بتحميل نموذج في ار إم.
settings-osc-vmc-anchor_hip-label = ثبت في الوركين

## Setup/onboarding menu

onboarding-skip = تخطى الإعداد
onboarding-continue = ‏‏متابعة
onboarding-wip = جاري العمل
onboarding-previous_step = الخطوة السابقة
onboarding-setup_warning =
    <b>تحذير:<b> الإعداد ضروري للتعقب الجيد،
    إنه مطلوب إذا كانت هذه هي المرة الأولى التي تستخدم سلايم في ار.
onboarding-setup_warning-skip = تخطى الإعداد
onboarding-setup_warning-cancel = متابعة الإعداد

## Wi-Fi setup

onboarding-wifi_creds-back = العودة إلى المقدمة
onboarding-wifi_creds = إدخل بيانات اعتماد واي فاي
# This cares about multilines
onboarding-wifi_creds-description =
    ستستخدم أجهزة التعقب بيانات الاعتماد هذه للاتصال لاسلكيًا.
    الرجاء استخدام بيانات الاعتماد التي تتصل بها حاليًا.
onboarding-wifi_creds-skip = تخطى إعدادات واي فاي
onboarding-wifi_creds-submit = إرسال!
onboarding-wifi_creds-ssid =
    .label = اسم الواي فاي
    .placeholder = أدخل اسم الواي فاي
onboarding-wifi_creds-password =
    .label = كلمة السر
    .placeholder = أدخل كلمة السر

## Mounting setup

onboarding-reset_tutorial-back = العودة إلى معايرة التركيب
onboarding-reset_tutorial = إعادة البرنامج التعليمي
onboarding-reset_tutorial-explanation = أثناء استخدام أجهزة التعقب، قد تخرج عن المحاذاة بسبب انحراف IMU ، أو لأنك ربما تكون قد نقلتها جسديا. لديك عدة طرق لإصلاح هذا.
onboarding-reset_tutorial-skip = تخطى الخطوة
# Cares about multiline
onboarding-reset_tutorial-0 =
    اضغط على جهاز التعقب  المحدد { $taps } مرات لتشغيل إعادة ضبط الانعراج.
    
    سيؤدي ذلك إلى جعل أجهزة التعقب تواجه نفس اتجاه HMD الخاص بك.
# Cares about multiline
onboarding-reset_tutorial-1 =
    اضغط على جهاز التعقب المحدد { $taps } مرات لتشغيل إعادة تعيين كاملة.
    
    يجب أن تكون واقفًا (i-pose). هناك تأخير لمدة 3 ثوان (قابل للتكوين) قبل إعادة التعيين بالكامل.
    هذا يعيد تعيين موضع ودوران جميع جهاز التعقب. يجب أن يحل معظم المشاكل.
# Cares about multiline
onboarding-reset_tutorial-2 =
    اضغط على المتتبع المحدد { $taps } مرات لتشغيل إعادة تعيين متصاعد.
    
    يساعد إعادة التعيين المتصاعد في تحديد كيفية وضع أجهزة التعقب عليك بالفعل. لذلك إذا قمت بنقلهم عن طريق الخطأ وغيرت كيفية توجيههم بمقدار كبير ، فسيساعد ذلك.
    
    يجب أن تكون في وضع تزلج كما هو موضح في معالج "التثبيت التلقائي" ولديك تأخير لمدة 3 ثوانٍ (قابل للتكوين) قبل أن يتم تشغيله.

## Setup start

onboarding-home = مرحبا بكم في سلايم في ار
onboarding-home-start = هيا نتجهز!

## Enter VR part of setup

onboarding-enter_vr-back = العودة إلى تعيين أجهزة التعقب
onboarding-enter_vr-title = حان وقت دخول في ار!
onboarding-enter_vr-description = ضع كل أجهزة التعقب ثم أدخل في ار!
onboarding-enter_vr-ready = أنا جاهز

## Setup done

onboarding-done-title = أنت جاهز تمامًا!
onboarding-done-description = استمتع بتجربة تتبع الجسم بالكامل!
onboarding-done-close = إغلاق الدليل

## Tracker connection setup

onboarding-connect_tracker-back = العودة إلى بيانات اعتماد الواي فاي
onboarding-connect_tracker-title = ربط أجهزة التعقب
onboarding-connect_tracker-description-p0-v1 = ننتقل الآن إلى الجزء الممتع ، ربط أجهزة التعقب!
onboarding-connect_tracker-description-p1-v1 = قم بتوصيل كل جهاز تعقب  واحدا تلو الآخر من خلال منفذ USB.
onboarding-connect_tracker-issue-serial = أواجه مشكلة في الاتصال!
onboarding-connect_tracker-usb = جهاز تعقب يو أس بي
onboarding-connect_tracker-connection_status-none = نبحث عن أجهزة التعقب
onboarding-connect_tracker-connection_status-serial_init = نتواصل بجهاز التسلسلي
onboarding-connect_tracker-connection_status-provisioning = نرسل بيانات اعتماد واي فاي
onboarding-connect_tracker-connection_status-connecting = جارٍ إرسال بيانات اعتماد الواي فاي
onboarding-connect_tracker-connection_status-looking_for_server = نبحث عن السرفر
onboarding-connect_tracker-connection_status-connection_error = غير قادر على الاتصال بشبكة الواي فاي
onboarding-connect_tracker-connection_status-could_not_find_server = تعذر العثور على السرفر
onboarding-connect_tracker-connection_status-done = متصل بالسيرفر
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] لا يوجد جهاز تعقب متصل
        [zero] لا يوجد جهاز تعقب متصل
        [one] جهاز تعقب واحد متصل
        [two] جهازا تعقب متصلان
        [few] { $amount } أجهزة تعقب متصلة
        [many] { $amount } أجهزة تعقب متصلة
       *[other] { $amount } أجهزة تعقب متصلة
    }
onboarding-connect_tracker-next = لقد قمت بتوصيل جميع أجهزة التعقب

## Tracker calibration tutorial

onboarding-calibration_tutorial = برنامج تعليم معايرة IMU
onboarding-calibration_tutorial-subtitle = سوف يساعد هذا في تقليل الانجراف التعقب!
onboarding-calibration_tutorial-description = كل مرة تقوم بتشغيل أجهزة التعقب، يجب أن تستريح للحظة على سطح مستوٍ للمعايرة. لنفعل الشيء نفسه بالنقر فوق الزر "{ onboarding-calibration_tutorial-calibrate }" ، <b>لا تحركها!</b>
onboarding-calibration_tutorial-calibrate = وضعت أجهزة التعقب على الطاولة
onboarding-calibration_tutorial-status-waiting = بانتظارك
onboarding-calibration_tutorial-status-calibrating = جاري المعايرة
onboarding-calibration_tutorial-status-success = رائع!
onboarding-calibration_tutorial-status-error = تم نقل جهاز التعقب

## Tracker assignment tutorial

onboarding-assignment_tutorial = كيفية تحضير جهاز تعقب Slime قبل وضعه
onboarding-assignment_tutorial-first_step = 1. ضع ملصق جزء الجسم (إذا كان لديك واحد) على جهاز التعقب وفقا لاختيارك
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = ملصق
onboarding-assignment_tutorial-second_step-v2 = 2. قم بتوصيل الشريط بجهاز التعقب، مع الحفاظ على جانب الفيلكرو من الشريط في نفس اتجاه وجه السلايم لجهاز التعقب:
onboarding-assignment_tutorial-second_step-continuation-v2 = يجب أن يكون جانب الفيلكرو للامتداد متجها للأعلى مثل الصورة التالية:
onboarding-assignment_tutorial-done = وضعت الملصقات والأشرطة!

## Tracker assignment setup

onboarding-assign_trackers-back = العودة إلى بيانات اعتماد الواي فاي
onboarding-assign_trackers-title = تعيين أجهزة التعقب
onboarding-assign_trackers-description = دعنا نختار موقع أجهزة التعقب. انقر فوق المكان الذي تريد وضع جهاز تعقب فيه
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [zero] { $assigned } من { $trackers } أجهزة تعقب عينت
        [one] جهاز واحد من { $trackers } أجهزة تعقب عينت
        [two] جهازان من { $trackers } أجهزة تعقب عينت
        [few] { $assigned } من { $trackers } أجهزة تعقب عينت
        [many] { $assigned } من { $trackers } أجهزة تعقب عينت
       *[other] { $assigned } من { $trackers } أجهزة تعقب عينت
    }
onboarding-assign_trackers-advanced = إظهار مواقع التعيين المتقدمة
onboarding-assign_trackers-next = لقد عينت جميع أجهزة التعقب
onboarding-assign_trackers-mirror_view = عرض المرآة

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] تم تحديد القدم اليسرى، ولكنك تحتاج أيضًا إلى تحديد الكاحل الأيسر والفخذ الأيسر وإما الصدر أو الورك أو الخصر!
        [1] تم تحديد القدم اليسرى، ولكنك تحتاج أيضًا إلى تحديد الفخذ الأيسر وإما الصدر أو الورك أو الخصر!
        [2] تم تحديد القدم اليسرى، ولكنك تحتاج أيضًا إلى تحديد الكاحل الأيسر وإما الصدر أو الورك أو الخصر!
        [3] تم تحديد القدم اليسرى، ولكنك تحتاج أيضًا إلى تحديد إما الصدر أو الورك أو الخصر!
        [4] تم تحديد القدم اليسرى، ولكنك تحتاج أيضًا إلى تحديد الكاحل الأيسر والفخذ الأيسر!
        [5] تم تحديد القدم اليسرى، ولكنك تحتاج أيضًا إلى تحديد الفخذ الأيسر!
        [6] تم تحديد القدم اليسرى، ولكنك تحتاج أيضًا إلى تحديد الكاحل الأيسر !
       *[unknown] تم تحديد القدم اليسرى، ولكنك تحتاج أيضًا إلى تحديد جزء جسم غير معروف غير مخصص !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] تم تحديد القدم اليمنى، ولكنك تحتاج أيضًا إلى تحديد الكاحل الأيمن والفخذ الأيمن وإما الصدر أو الورك أو الخصر!
        [1] تم تحديد القدم اليمنى، ولكنك تحتاج أيضًا إلى تحديد الفخذ الأيمن وإما الصدر أو الورك أو الخصر!
        [2] تم تحديد القدم اليمنى، ولكنك تحتاج أيضًا إلى تحديد الكاحل الأيمن وإما الصدر أو الورك أو الخصر!
        [3] تم تحديد القدم اليمنى، ولكنك تحتاج أيضًا إلى تحديد إما الصدر أو الورك أو الخصر!
        [4] تم تحديد القدم اليمنى، ولكنك تحتاج أيضًا إلى تحديد الكاحل الأيمن والفخذ الأيمن!
        [5] تم تحديد القدم اليمنى، ولكنك تحتاج أيضًا إلى تحديد الفخذ الأيمن!
        [6] تم تحديد القدم اليمنى، ولكنك تحتاج أيضًا إلى تحديد الكاحل الأيمن!
       *[unknown] تم تحديد القدم اليمنى، ولكنك تحتاج أيضًا إلى تحديد جزء جسم غير معروف غير مخصص!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] تم تحديد الكاحل الأيسر، ولكنك تحتاج أيضًا إلى تحديد الفخذ الأيسر وإما الصدر أو الورك أو الخصر!
        [1] تم تحديد الكاحل الأيسر، ولكنك تحتاج أيضًا إلى تحديد إما الصدر أو الورك أو الخصر!
        [2] تم تحديد الكاحل الأيسر، ولكنك تحتاج أيضًا إلى تحديد الفخذ الأيسر!
       *[unknown] تم تحديد الكاحل الأيسر، ولكنك تحتاج أيضًا إلى تحديد جزء جسم غير معروف غير مخصص!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] تم تحديد الكاحل الأيمن، ولكنك تحتاج أيضًا إلى تحديد الفخذ الأيمن وتحديد إما الصدر أو الورك أو الخصر!
        [1] تم تحديد الكاحل الأيمن، ولكنك تحتاج أيضًا إلى تحديد إما الصدر أو الورك أو الخصر!
        [2] تم تحديد الكاحل الأيمن، ولكنك تحتاج أيضًا إلى تحديد الفخذ الأيمن!
       *[unknown] تم تحديد الكاحل الأيمن، ولكنك تحتاج أيضًا إلى تحديد جزء جسم غير معروف غير مخصص!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] تم تحديد الفخذ الأيسر، ولكنك تحتاج أيضًا إلى تحديد الصدر أو الورك أو الخصر!
       *[unknown] تم تحديد الفخذ الأيسر، ولكنك تحتاج أيضًا إلى تحديد جزء جسم غير معروف غير مخصص !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] تم تحديد الفخذ الأيمن ولكنك تحتاج أيضًا إلى تحديد الصدر أو الورك أو الخصر!
       *[unknown] تم تحديد الفخذ الأيمن ولكنك تحتاج أيضًا إلى تحديد جزء جسم غير معروف غير مخصص !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] تم تحديد الورك، ولكنك تحتاج إلى تحديد الصدر أيضًا!
       *[unknown] تم تحديد الورك، ولكنك تحتاج إلى تحديد جزء جسم غير معروف غير مخصص أيضًا!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] تم تحديد الخصر، ولكنك تحتاج إلى تحديد الصدر أيضًا!
       *[unknown] تم تحديد الخصر، ولكنك تحتاج إلى تحديد جزء جسم غير معروف غير مخصص أيضًا!
    }

## Tracker mounting method choose

onboarding-choose_mounting = ما طريقة معايرة التركيب المستخدمة؟
# Multiline text
onboarding-choose_mounting-description = اتجاه التركيب يصحح وضع أجهزة التعقب على جسمك.
onboarding-choose_mounting-auto_mounting = التركيب التلقائي
# Italized text
onboarding-choose_mounting-auto_mounting-label = تجريبي
onboarding-choose_mounting-auto_mounting-description = سيكتشف هذا تلقائيًا اتجاهات التركيب لجميع أجهزة التعقب من وضعين
onboarding-choose_mounting-manual_mounting = التركيب اليدوي
# Italized text
onboarding-choose_mounting-manual_mounting-label = المستحسن
onboarding-choose_mounting-manual_mounting-description = سيسمح لك باختيار اتجاه التثبيت يدويًا لكل جهاز تعقب
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    هل أنت متأكد من أنك تريد 
    معايرة التركيب التلقائي؟
onboarding-choose_mounting-manual_modal-description = <b>يوصى بمعايرة التركيب اليدوي للمستخدمين الجدد</b> ، حيث قد يكون من الصعب الحصول على أوضاع معايرة التركيب التلقائي الصحيحة من اول مرة وقد تتطلب بعض التمرين.
onboarding-choose_mounting-manual_modal-confirm = أنا أعرف ماذا أفعل
onboarding-choose_mounting-manual_modal-cancel = إلغاء

## Tracker manual mounting setup

onboarding-manual_mounting-back = العودة إلى دخول في ار
onboarding-manual_mounting = التركيب اليدوي
onboarding-manual_mounting-description = انقر فوق كل جهاز تعقب وحدد طريقة تركيبها
onboarding-manual_mounting-auto_mounting = التركيب التلقائي
onboarding-manual_mounting-next = الخطوة التالية

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = العودة إلى دخول في ار
onboarding-automatic_mounting-title = معايرة التركيب
onboarding-automatic_mounting-description = لكي تعمل أجهزة تعقب سلايم في ار، نحتاج إلى تعيين دوران تركيب أجهزة التعقب لمواءمتها مع تركيب جهاز التعقب المادي.
onboarding-automatic_mounting-manual_mounting = ضبط التركيب يدويًا
onboarding-automatic_mounting-next = الخطوة التالية
onboarding-automatic_mounting-prev_step = الخطوة السابقة
onboarding-automatic_mounting-done-title = تم معايرة دوران التركيب
onboarding-automatic_mounting-done-description = اكتملت معايرة التركيب!
onboarding-automatic_mounting-done-restart = العودة إلى البداية
onboarding-automatic_mounting-mounting_reset-title = إعادة تعيين التركيب
onboarding-automatic_mounting-mounting_reset-step-0 = 1. قرفص في وضع "التزلج" مع ثني ساقيك ، وإمالة الجزء العلوي من جسمك إلى الأمام ، وثني ذراعيك.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. اضغط على زر "إعادة تعيين التركيب" وانتظر لمدة 3 ثوان قبل إعادة تعيين دوران تركيب أجهزة التعقب.
onboarding-automatic_mounting-preparation-title = التحضير
onboarding-automatic_mounting-preparation-step-0 = 1. قف بشكل مستقيم مع ذراعيك على جانبيك.
onboarding-automatic_mounting-preparation-step-1 = اضغط على زر "إعادة ضبط" و انتظر لمدة 3 ثوانٍ قبل إعادة تعيين أجهزة التعقب.
onboarding-automatic_mounting-put_trackers_on-title = ارتدي أجهزة التعقب
onboarding-automatic_mounting-put_trackers_on-description = لمعايرة دوران التركيب، سنستخدم أجهزة التعقب التي قمت بتعيينها. ارتدي جميع أجهزة التعقب، يمكنك معرفة أي منها في المستند على اليمين.
onboarding-automatic_mounting-put_trackers_on-next = ارتديت جميع أجهزة التعقب.

## Tracker proportions method choose

onboarding-choose_proportions = ما هي طريقة معايرة النسب التي يجب استخدامها؟
# Multiline string
onboarding-choose_proportions-description-v1 =
    تستخدم نسب الجسم لمعرفة قياسات جسمك. إنهم مطالبون لحساب مواقع أجهزة التعقب.
    عندما لا تتطابق نسب جسمك مع تلك المحفوظة ، ستكون دقة التعقب أسوأ وستلاحظ أشياء مثل التزلج أو الانزلاق ، أو أن جسمك لا يتطابق مع صورتك الرمزية جيدا.
    <b>ما عليك سوى قياس جسمك مرة واحدة!</b> إن لم تكن خاطئة أو تغير جسمك ، فلن تحتاج إلى القيام بها مرة أخرى.
onboarding-choose_proportions-auto_proportions = النسب التلقائية
# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = الموصى به
onboarding-choose_proportions-auto_proportions-descriptionv3 =
    سيؤدي ذلك إلى تخمين نسبك عن طريق تسجيل عينة من تحركاتك وتمريرها عبر خوارزمية.
    
    <b>يتطلب ذلك توصيل جهاز الواقع الافتراضي (HMD) ب SlimeVR و وضعها  على رأسك!</b>
onboarding-choose_proportions-manual_proportions = النسب اليدوية
# Italized text
onboarding-choose_proportions-manual_proportions-subtitle = للمسات الصغيرة
onboarding-choose_proportions-manual_proportions-description = سيسمح لك بتعديل النسب يدويًا عن طريق تعديلها مباشرة
onboarding-choose_proportions-export = تصدير النسب
onboarding-choose_proportions-import = استيراد النسب
onboarding-choose_proportions-import-success = تم استيراده
onboarding-choose_proportions-import-failed = فشل
onboarding-choose_proportions-file_type = ملف نسب الجسم

## Tracker manual proportions setup

onboarding-manual_proportions-back = العودة إلى برنامج تعليم إعادة التعيين
onboarding-manual_proportions-title = نسب الجسم اليدوية
onboarding-manual_proportions-precision = ضبط الدقة
onboarding-manual_proportions-auto = المعايرة التلقائية
onboarding-manual_proportions-ratio = اضبط حسب مجموعات النسب

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = العودة إلى برنامج تعليم إعادة التعيين
onboarding-automatic_proportions-title = قياس جسمك
onboarding-automatic_proportions-description = لكي تعمل أجهزة تعقب سلايم في ار, نحتاج إلى معرفة طول عظامك. هذه المعايرة القصيرة ستقيسها لك.
onboarding-automatic_proportions-manual = معايرة يدوية
onboarding-automatic_proportions-prev_step = الخطوة السابقة
onboarding-automatic_proportions-put_trackers_on-title = ارتدي أجهزة التعقب
onboarding-automatic_proportions-put_trackers_on-description = لمعايرة نسب جسمك ، سنستخدم أجهزة التعقب التي قمت بتعيينها. ضع جميع أجهزة التعقب، يمكنك معرفة أين تم تعيينه في المستند على اليمين.
onboarding-automatic_proportions-put_trackers_on-next = ارتديت جميع أجهزة التعقب.
onboarding-automatic_proportions-requirements-title = المتطلبات
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    لديك على الأقل ما يكفي من أجهزة التعقب لتتبع قدميك (بشكل عام 5 أجهزة تعقب).
    لديك أجهزة التعقب  وجهاز الواقع الافتراضي الخاص بك وترتديهم.
    أجهزة التعقب وجهاز الواقع الافتراضي متصلة بخادم SlimeVR وتعمل بشكل صحيح (مثلاً، لا يوجد تأتأة أو قطع اتصال ، إلخ).
    يقوم جهاز الواقع الافتراضي بالإبلاغ عن البيانات الموضعية إلى خادم SlimeVR (وهذا يعني عموما تشغيل SteamVR وتوصيله ب SlimeVR باستخدام برنامج تشغيل SteamVR الخاص ب SlimeVR).
    يعمل التتبع الخاص بك ويمثل تحركاتك بدقة (على سبيل المثال ، لقد أجريت إعادة تعيين كاملة وتتحرك في الاتجاه الصحيح عند الركل, الانحناء, الجلوس, إلخ).
onboarding-automatic_proportions-requirements-next = لقد قرأت المتطلبات
onboarding-automatic_proportions-check_height-title = تحقق من طولك
onboarding-automatic_proportions-check_height-description = نستخدم طولك كأساس لقياساتنا باستخدام ارتفاع HMD كتقريب لطولك الفعلي ، ولكن من الأفضل التحقق مما إذا كانت صحيحة بنفسك!
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning = يرجى الضغط على الزر أثناء الوقوف <u>في وضع مستقيم</u> لحساب طولك. لديك 3 ثوان بعد الضغط على الزر!
onboarding-automatic_proportions-check_height-guardian_tip =
    إذا كنت تستخدم سماعة رأس VR مستقلة ، فتأكد من تشغيل حدود الحارس /
    لكي يكون طولك صحيحا!
onboarding-automatic_proportions-check_height-fetch_height = أنا واقف!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = مجهول
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height1 = طولك من خلال HMD
# Shows an element below it
onboarding-automatic_proportions-check_height-height1 = لذا فإن طولك الفعلي هو
onboarding-automatic_proportions-check_height-next_step = انهم بخير
onboarding-automatic_proportions-start_recording-title = استعد للتحرك
onboarding-automatic_proportions-start_recording-description = سنقوم الآن بتسجيل بعض الوضعيات والحركات المحددة. ستتم مطالبتك بذلك في الشاشة التالية. كن مستعدا للبدء عند الضغط على الزر!
onboarding-automatic_proportions-start_recording-next = بدء التسجيل
onboarding-automatic_proportions-recording-title = تسجيل
onboarding-automatic_proportions-recording-description-p0 = جاري التسجيل...
onboarding-automatic_proportions-recording-description-p1 = قم بالحركات الموضحة أدناه:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    قف بشكل مستقيم، لف رأسك بشكل دائري.
    اثنِ ظهرك للأمام و قرفص. أثناء الجلوس ، انظر إلى يسارك ، ثم إلى يمينك.
    قم بتدوير الجزء العلوي من جسمك إلى اليسار (عكس اتجاه عقارب الساعة) ، ثم انزل نحو الأرض.
    قم بتدوير الجزء العلوي من جسمك إلى اليمين (في اتجاه عقارب الساعة) ، ثم انزل نحو الأرض.
    قم بتدوير وركيك في حركة دائرية كما لو كنت تستخدم طوق هولا هوب.
    إذا كان هناك وقت متبقي على التسجيل ، فيمكنك تكرار هذه الخطوات حتى تنتهي.
onboarding-automatic_proportions-recording-processing = معالجة النتيجة
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [zero] { $time } ثانية متبقية
        [one] { $time } ثانية متبقية
        [two] ثانيتين متبقية
        [few] { $time } ثوان متبقية
        [many] { $time } ثوان متبقية
       *[other] { $time } ثوان متبقية
    }
onboarding-automatic_proportions-verify_results-title = تحقق من النتائج
onboarding-automatic_proportions-verify_results-description = تحقق من النتائج أدناه ، هل تبدو صحيحة؟
onboarding-automatic_proportions-verify_results-results = تسجيل النتائج
onboarding-automatic_proportions-verify_results-processing = معالجة النتيجة
onboarding-automatic_proportions-verify_results-redo = إعادة التسجيل
onboarding-automatic_proportions-verify_results-confirm = تبدو صحيحة
onboarding-automatic_proportions-done-title = تم قياس الجسم و حفظه.
onboarding-automatic_proportions-done-description = اكتملت معايرة نسب جسمك!
onboarding-automatic_proportions-error_modal =
    <b>تحذير:</b> تم العثور على خطأ أثناء تقدير النسب!
    يرجى <docs>التحقق من المستندات</docs> أو الانضمام إلى <discord>Discord</discord> للحصول على المساعدة ^_^
onboarding-automatic_proportions-error_modal-confirm = مفهوم!

## Home

home-no_trackers = لم يتم الكشف أو تعيين عن أي جهاز تعقب

## Trackers Still On notification

trackers_still_on-modal-title = أجهزة التعقب لا تزال قيد التشغيل
trackers_still_on-modal-description =
    لا يزال واحد أو أكثر من أجهزة التعقب قيد التشغيل.
    هل مازلت تريد الخروج من SlimeVR؟
trackers_still_on-modal-confirm = الخروج من SlimeVR
trackers_still_on-modal-cancel = انتظر...

## Status system

status_system-StatusTrackerReset = يوصى بإجراء إعادة تعيين كاملة نظرًا لعدم تعديل واحد أو أكثر من أجهزة التعقب.
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] حاليًا غير متصل بتطبيق SlimeVR Feeder.
       *[other] حاليًا غير متصل بـ SteamVR عبر برنامج تشغيل SlimeVR.
    }
status_system-StatusTrackerError = يحتوي جهاز التعقب { $trackerName } على خطأ.

## Tray Menu

tray_menu-show = عرض
tray_menu-hide = إخفاء
tray_menu-quit = انهاء

## First exit modal

tray_or_exit_modal-title = ماذا يجب أن يفعل زر الإغلاق؟
# Multiline text
tray_or_exit_modal-description =
    يتيح لك ذلك اختيار ما إذا كنت تريد الخروج من الخادم أو تصغيره إلى علبة النظام عند الضغط على زر الإغلاق.
    
    يمكنك تغيير هذا لاحقا في إعدادات الواجهة!
tray_or_exit_modal-radio-exit = الخروج عند الإغلاق
tray_or_exit_modal-radio-tray = تصغير إلى علبة النظام
tray_or_exit_modal-submit = احفظ
tray_or_exit_modal-cancel = إلغاء

## Unknown device modal

unknown_device-modal-title = تم العثور على جهاز تعقب جديد!
unknown_device-modal-description =
    هناك جهاز تعقب جديد مع عنوان MAC <b>{ $deviceId }</b>.
    هل تريد توصيله ب SlimeVR؟
unknown_device-modal-confirm = أكيد
unknown_device-modal-forget = تجاهلها
