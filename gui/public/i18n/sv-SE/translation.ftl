# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Ansluter till server
websocket-connection_lost = Anslutning förlorad till server. Försöker återansluta
websocket-connection_lost-desc = Det verkar som att SlimeVR servern kraschade. Kolla loggen och starta om programmet.
websocket-timedout = Kunde ej koppla till servern.
websocket-timedout-desc = Det verkar som att SlimeVR servern kraschade eller löpte ur tidsgränsen. Kolla loggen och starta om programmet.
websocket-error-close = Stäng SlimeVR
websocket-error-logs = Öppna logg-mappen

## Update notification

version_update-title = Ny version tillgänglig: { $version }
version_update-description = Att trycka på "{ version_update-update }" kommer ladda ner SlimeVR installatorn för dig
version_update-update = Updatera
version_update-close = Stäng

## Tips

tips-find_tracker = Osäker vilken spårare är vilken? Dela en spårare och den kommer markera motsvarande spårare.
tips-do_not_move_heels = Se till att dina hälar inte rör sig under inspelningen!
tips-file_select = Dra och släpp filer som du vill använda, eller <u>bläddra</u>.
tips-tap_setup = Du kan långsamt trycka 2 gånger på din spårare för att välja den istället att välja den på menun.
tips-turn_on_tracker = Använder du officiella SlimeVR spårare? Glöm inte att <b> <em>sätta på din spårare</em></b> efter du ansluter den till datorn!
tips-failed_webgl = Misslyckades att initiera WebGL.

## Units


## Body parts

body_part-NONE = Ej tilldelad
body_part-HEAD = Huvud
body_part-NECK = Nacke
body_part-RIGHT_SHOULDER = Höger axel
body_part-RIGHT_UPPER_ARM = Höger överarm
body_part-RIGHT_LOWER_ARM = Höger underarm
body_part-RIGHT_HAND = Höger hand
body_part-RIGHT_UPPER_LEG = Höger lår
body_part-RIGHT_LOWER_LEG = Höger vrist
body_part-RIGHT_FOOT = Höger fot
body_part-UPPER_CHEST = Över bröst
body_part-CHEST = Bröst
body_part-WAIST = Midja
body_part-HIP = Höft
body_part-LEFT_SHOULDER = Vänster axel
body_part-LEFT_UPPER_ARM = Vänster överarm
body_part-LEFT_LOWER_ARM = Väster underarm
body_part-LEFT_HAND = Vänster hand
body_part-LEFT_UPPER_LEG = Vänster lår
body_part-LEFT_LOWER_LEG = Vänster vrist
body_part-LEFT_FOOT = Vänster fot
body_part-LEFT_THUMB_METACARPAL = Vänster tumme metacarpalben
body_part-LEFT_THUMB_PROXIMAL = Vänster tumme proximal falang
body_part-LEFT_THUMB_DISTAL = Vänster tumme distal falang
body_part-LEFT_INDEX_PROXIMAL = Vänster pekfinger proximal falang
body_part-LEFT_INDEX_INTERMEDIATE = Vänster pekfinger mellanfalang
body_part-LEFT_INDEX_DISTAL = Vänster pekfinger distal falang
body_part-LEFT_MIDDLE_PROXIMAL = Vänster långfinger proximal falang
body_part-LEFT_MIDDLE_INTERMEDIATE = Vänster långfinger mellanfalang
body_part-LEFT_MIDDLE_DISTAL = Vänster långfinger distal falang
body_part-LEFT_RING_PROXIMAL = Vänster ringfinger proximal falang
body_part-LEFT_RING_INTERMEDIATE = Vänster ringfinger mellanfalang
body_part-LEFT_RING_DISTAL = Vänster ringfinger distal falang
body_part-LEFT_LITTLE_PROXIMAL = Vänster ringfinger proximal falang
body_part-LEFT_LITTLE_INTERMEDIATE = Vänster lillfinger mellanfalang
body_part-LEFT_LITTLE_DISTAL = Vänster lillfinger distal falang
body_part-RIGHT_THUMB_METACARPAL = Höger tumme metacarpalben
body_part-RIGHT_THUMB_PROXIMAL = Höger tumme proximal falang
body_part-RIGHT_THUMB_DISTAL = Höger tumme distal falang
body_part-RIGHT_INDEX_PROXIMAL = Höger pekfinger proximal falang
body_part-RIGHT_INDEX_INTERMEDIATE = Höger pekfinger mellanfalang
body_part-RIGHT_INDEX_DISTAL = Höger pekfinger distal falang
body_part-RIGHT_MIDDLE_PROXIMAL = Höger långfinger proximal falang
body_part-RIGHT_MIDDLE_INTERMEDIATE = Höger långfinger mellanfalang
body_part-RIGHT_MIDDLE_DISTAL = Höger långfinger distal falang
body_part-RIGHT_RING_PROXIMAL = Höger ringfinger proximal falang
body_part-RIGHT_RING_INTERMEDIATE = Höger ringfinger mellanfalang
body_part-RIGHT_RING_DISTAL = Höger ringfinger distal falang
body_part-RIGHT_LITTLE_PROXIMAL = Höger ringfinger proximal falang
body_part-RIGHT_LITTLE_INTERMEDIATE = Höger lillfinger mellanfalang
body_part-RIGHT_LITTLE_DISTAL = Höger lillfinger distal falang

## BoardType

board_type-UNKNOWN = Okänd
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Anpassat kretskort
board_type-WROOM32 = WROOM32

## Proportions

skeleton_bone-NONE = Ingen
skeleton_bone-HEAD = Huvudskift
skeleton_bone-HEAD-desc =
    Detta är distansen från ditt headset till mittpunkten av ditt huvud.
    För att justera det, skaka ditt huvud höger eller vänster, som om att du säger nej och modifiera
    det tills någon rörelse i andra rörelsesensorer är obetydlig.
skeleton_bone-NECK = Halsens längd
skeleton_bone-NECK-desc =
    Detta är distansen från mittpunkten av ditt huvud till din nackes bas.
    För att justera det, skaka ditt huvud upp och ner, som om att du säger ja eller luta ditt
    huvud höger eller vänster och modifiera det tills någon rörelse i andra rörelsesensorer är obetydlig.
skeleton_bone-torso_group = Halsens längd
skeleton_bone-torso_group-desc =
    Detta är distansen från din nackes bas till dina höfter.
    För att justera det, modifiera det stående rakt upp tills dina virtuella höfter
    matchar dina riktiga.
skeleton_bone-UPPER_CHEST = Övre bröstkorgens längd
skeleton_bone-UPPER_CHEST-desc =
    Detta är distansen från din nackes bas till mitten av din bröstkorg.
    För att justera det, justera din torso-längd ordentligt och modifiera den i olika olika positioner.
    (sittande, böjd, liggande, osv.) Tills din virtuella ryggrad matchar med din riktiga.
skeleton_bone-CHEST_OFFSET = Bröstkorgens förskjutning
skeleton_bone-CHEST_OFFSET-desc =
    Detta kan justeras för att röra din virtuella bröstkorgs-sensor upp eller ner för att assistera
    med kalibrering i vissa spel eller applikationer som förväntar sig att den ska vara högre eller lägre.
skeleton_bone-CHEST = Bröstkorgens längd
skeleton_bone-CHEST-desc =
    Detta är distansen från mitten av din bröstkorg till mitten av din ryggrad.
    För att justera det, justera din torso-längd ordentligt och modifiera den i olika olika positioner.
    (sittande, böjd, liggande, osv.) Tills din virtuella ryggrad matchar med din riktiga.
skeleton_bone-WAIST = Midja Längd
skeleton_bone-WAIST-desc =
    Detta är distansen från mitten av din ryggrad till din navel.
    För att justera det, justera din torso-längd ordentligt och modifiera den i olika olika positioner.
    (sittande, böjd, liggande, osv.) Tills din virtuella ryggrad matchar med din riktiga.
skeleton_bone-HIP = Höftlängd
skeleton_bone-HIP-desc =
    Detta är distansen från din navel till dina höfter.
    För att justera det, justera din torso-längd ordentligt och modifiera den i olika olika positioner.
    (sittande, böjd, liggande, osv.) Tills din virtuella ryggrad matchar med din riktiga.
skeleton_bone-HIP_OFFSET = Höft förskjutning
skeleton_bone-HIP_OFFSET-desc =
    Detta kan justeras för att röra din virtuella höft-sensor upp eller ner för att assistera
    med kalibrering i vissa spel eller applikationer som förväntar sig att den ska vara på din midja
skeleton_bone-HIPS_WIDTH = Höftbredd
skeleton_bone-HIPS_WIDTH-desc =
    Detta är distansen mellan början på dina ben.
    För att justera det, utför en full återställning med dina ben raka och modifiera den
    tills dina virtuella ben matchar med dina riktiga horisontellt.
skeleton_bone-leg_group = Benlängd
skeleton_bone-leg_group-desc =
    Detta är distansen från dina höfter till dina fötter.
    För att justera det, justera din Torso-längd ordentligt och modifiera
    den tills dina virtuella fötter är på samma nivå som dina riktiga.
skeleton_bone-UPPER_LEG = Längd på övre delen av benet
skeleton_bone-UPPER_LEG-desc =
    Detta är distansen från dina fötter till dina knän.
    För att justera det, justera din ben-längd ordentligt och modifiera
    den tills dina virtuella knän är på samma nivå som dina riktiga.
skeleton_bone-LOWER_LEG = Längd på underben
skeleton_bone-LOWER_LEG-desc =
    Detta är avståndet från dina knän till dina fotleder.
    För att justera det, justera din ben-längd ordentligt och modifiera
    den tills dina virtuella knän är på samma nivå som dina riktiga.
skeleton_bone-FOOT_LENGTH = Fot Längd
skeleton_bone-FOOT_LENGTH-desc =
    Detta är distansen från dina höftleder till dina tår.
    För att justera det, gå på tå och modifiera det tills dina virtuella fötter stannar på plats.
skeleton_bone-FOOT_SHIFT = Fotförskjutning
skeleton_bone-FOOT_SHIFT-desc =
    Detta värde är den horisontella distansen från dina ditt knä till din fotled.
    den tar hänsyn till att dina underben går baklänges när du står rakt upp.
    För att justera det, ställ fotens längd till 0, utför en full återställning och modifiera den tills
    dina virtuella fötter matchar mitten av dina fotleder.
skeleton_bone-SKELETON_OFFSET = Skelettets förskjutning
skeleton_bone-SKELETON_OFFSET-desc =
    Detta kan justeras för att förskjuta alla dina rörelse-sensorer framåt eller bakåt.
    Det kan användas för att assistera med kalibrering i vissa spel eller applikationer
    som kan förvänta att dina sensorer är längre fram.
skeleton_bone-SHOULDERS_DISTANCE = Avstånd mellan axlar
skeleton_bone-SHOULDERS_DISTANCE-desc =
    Detta är den vertikala distansen från din nackes bas till dina axlar.
    För att justera det, Längd på överarm till 0 och modifiera det tills dina virituella
    axel-sensorer är i linje med dina riktiga axlar.
skeleton_bone-SHOULDERS_WIDTH = Axlarnas bredd
skeleton_bone-SHOULDERS_WIDTH-desc =
    Detta är den horisontella distansen från din nackes bas till dina axlar.
    För att justera det, ändra längd på överarm till 0 och modifiera det tills dina virtuella
    axel-sensorer är i linje med dina riktiga axlar.
skeleton_bone-arm_group = Armlängd
skeleton_bone-arm_group-desc =
    Detta är avståndet från dina axlar till dina handleder.
    För att justera det, justera Avståndet mellan axlar ordentligt, ändra Handavstånd Y
    till 0 och modifiera tills dina hand-sensorer är i linje med dina handleder.
skeleton_bone-UPPER_ARM = Längd på överarm
skeleton_bone-UPPER_ARM-desc =
    Detta är distansen från dina axlar till dina armbågar.
    För att justera det, justera Armlängd ordentligt och modifiera det
    tills dina armbågs-sensorer är i linje med dina riktiga armbågar.
skeleton_bone-LOWER_ARM = Längd på underarm
skeleton_bone-LOWER_ARM-desc =
    Detta är avståndet från dina armbågar till dina handleder.
    För att justera det, justera Armlängd ordentligt och modifiera det
    tills dina armbågs-spårare matchar med dina riktiga armbågar.
skeleton_bone-HAND_Y = Handavstånd Y
skeleton_bone-HAND_Y-desc =
    Detta är den vertikala distansen från dina handleder till mitten av din hand.
    Föra att justera den till Motion Capture, justera Armlängd ordentligt och modifiera
    den tills dina hand-sensorer är i linje vertikalt med mitten av dina händer.
    För att justera den till Armbågs-spårning från dina dina kontroller, sätt Armlängd till 0 och
    modifiera tills dina armbågs-sensorer är i linje vertikalt med dina handleder.
skeleton_bone-HAND_Z = Handavstånd Z
skeleton_bone-HAND_Z-desc =
    Detta är den horisontella distansen från dina handleder till mitten av din hand.
    För att justera det för Motion Capture, sätt det till 0.
    För att justera det för armbågs-spårning från dina kontroller, sätt Armlängd till 0 och
    modifiera tills dina armbågs-spårare är i linje horisontellt med dina handleder.
skeleton_bone-ELBOW_OFFSET = Förskjutning av armbåge
skeleton_bone-ELBOW_OFFSET-desc =
    Detta kan justeras för att flytta dina virtuella armbågs-sensorer upp eller ner för att assistera
    med att VRChat av misstag kopplar en armbågs-sensor till bröstkorgen.

## Tracker reset buttons

reset-reset_all = Återställ alla proportioner
reset-reset_all_warning-v2 =
    Varning, dina proportioner kommer att återställas till standards skalade till din konfigurerade längd.
    Är du säker på att du vill göra detta?
reset-reset_all_warning-reset = Återställ proportioner
reset-reset_all_warning-cancel = Avbryt
reset-reset_all_warning_default-v2 =
    Varning: Din längd har ej konfigurerats, dina proportioner kommer att återställas till standards med standardlängden.
    Är du säker på att du vill göra detta?
reset-full = Fullständig återställning
reset-mounting = Återställning av montering
reset-yaw = Återställning av gir

## Serial detection stuff

serial_detection-new_device-p0 = Ny seriell enhet upptäckt!
serial_detection-new_device-p1 = Ange dina Wi-Fi-uppgifter!
serial_detection-new_device-p2 = Vänligen välj vad du vill göra med den
serial_detection-open_wifi = Anslut till Wi-Fi
serial_detection-open_serial = Öppna seriell konsol
serial_detection-submit = Lämna in!
serial_detection-close = Stäng

## Navigation bar

navbar-home = Hem
navbar-body_proportions = Kroppsproportioner
navbar-trackers_assign = Tracker Uppgift
navbar-mounting = Kalibrering av montering
navbar-onboarding = Inställningsguide
navbar-settings = Inställningar

## Biovision hierarchy recording

bvh-start_recording = Spela in BVH-rekord
bvh-recording = Inspelning...

## Tracking pause

tracking-unpaused = Pausa spårning
tracking-paused = Avbryt spårning

## Widget: Overlay settings

widget-overlay = Överlägg
widget-overlay-is_visible_label = Visa överlägg i SteamVR
widget-overlay-is_mirrored_label = Visa överlägg som spegel

## Widget: Drift compensation

widget-drift_compensation-clear = Kompensation för clear drift

## Widget: Clear Mounting calibration

widget-clear_mounting = Montage med tydlig återställning

## Widget: Developer settings

widget-developer_mode = Utvecklarläge
widget-developer_mode-high_contrast = Hög kontrast
widget-developer_mode-precise_rotation = Exakt rotation
widget-developer_mode-fast_data_feed = Snabb dataflöde
widget-developer_mode-filter_slimes_and_hmd = Filtrera slem och HMD
widget-developer_mode-sort_by_name = Sortera efter namn
widget-developer_mode-raw_slime_rotation = Rå rotation
widget-developer_mode-more_info = Mer information

## Widget: IMU Visualizer

widget-imu_visualizer = Rotation
widget-imu_visualizer-preview = Förhandsvisa
widget-imu_visualizer-hide = Göm
widget-imu_visualizer-rotation_raw = Rå rotation
widget-imu_visualizer-rotation_preview = Förhandsgranska rotation
widget-imu_visualizer-acceleration = Acceleration
widget-imu_visualizer-position = Position
widget-imu_visualizer-stay_aligned = Behåll inriktning

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Förhandstitt på skelett
widget-skeleton_visualizer-hide = Göm

## Tracker status

tracker-status-none = Ingen status
tracker-status-busy = Upptagen
tracker-status-error = Fel
tracker-status-disconnected = Frånkopplad
tracker-status-occluded = Ocklusivt
tracker-status-ok = Okej
tracker-status-timed_out = Tid ute

## Tracker status columns

tracker-table-column-name = Namn
tracker-table-column-type = Typ
tracker-table-column-battery = Batteri
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temperatur i Celsius:
tracker-table-column-linear-acceleration = Accel. X/Y/Z
tracker-table-column-rotation = Rotation X/Y/Z
tracker-table-column-position = Position X/Y/Z
tracker-table-column-stay_aligned = Behåll inriktning
tracker-table-column-url = WEBBADRESS

## Tracker rotation

tracker-rotation-front = Fram
tracker-rotation-front_left = Fram-vänster
tracker-rotation-front_right = Fram-höger
tracker-rotation-left = Vänster
tracker-rotation-right = Höger
tracker-rotation-back = Bak
tracker-rotation-back_left = Bak-väster
tracker-rotation-back_right = Bak-höger
tracker-rotation-custom = Egenanpassad
tracker-rotation-overriden = (åsidosätts genom återställning av montering)

## Tracker information

tracker-infos-manufacturer = Tillverkare
tracker-infos-display_name = Visa namn
tracker-infos-custom_name = Anpassat namn
tracker-infos-url = URL för spårare
tracker-infos-version = Firmware-version
tracker-infos-hardware_rev = Revision av hårdvara
tracker-infos-hardware_identifier = Hårdvaru-ID
tracker-infos-data_support = Datastöd
tracker-infos-imu = IMU-sensor
tracker-infos-board_type = Huvudkrets
tracker-infos-network_version = Protokollsversion
tracker-infos-magnetometer = Magnetometer
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Avaktiverad
        [ENABLED] Aktiverad
       *[NOT_SUPPORTED] Ej stödd
    }

## Tracker settings

tracker-settings-back = Gå tillbaka till trackerslistan
tracker-settings-title = Inställningar för spårare
tracker-settings-assignment_section = Uppdrag
tracker-settings-assignment_section-description = Vilken del av kroppen som spåraren är kopplad till.
tracker-settings-assignment_section-edit = Redigera uppdrag
tracker-settings-mounting_section = Monteringsriktning
tracker-settings-mounting_section-description = Var är spårningsenheten monterad?
tracker-settings-mounting_section-edit = Redigera montering
tracker-settings-drift_compensation_section = Tillåt driftkompensation
tracker-settings-drift_compensation_section-description = Bör denna tracker kompensera för sin drift när driftkompensationen är aktiverad?
tracker-settings-drift_compensation_section-edit = Tillåt driftkompensation
tracker-settings-use_mag = Aktivera magnetometer på denna sensorn.
# Multiline!
tracker-settings-use_mag-description =
    Ska denna sensorn använda magnetometern för att minska drift när magnetometer-användning är tillåten? <b> Var vänlig och stäng inte av sensorn när du växlar av och på denna inställningen! <b>
    
    Du behöver tillåta magnetometer-användning först <magSetting> klicka här för att gå till inställningen </magSetting>.
tracker-settings-use_mag-label = Tillåt magnetometer
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Sensorns namn
tracker-settings-name_section-description = Ge den ett gulligt smeknamn :)
tracker-settings-name_section-placeholder = Bokstensmannens vänstra ben
tracker-settings-name_section-label = Sensorns namn
tracker-settings-forget = Glöm spårning
tracker-settings-forget-description = Tar bort trackern från SlimeVR-servern och förhindrar den från att ansluta till den tills servern startas om. Konfigurationen av trackern kommer inte att gå förlorad.
tracker-settings-forget-label = Glöm spårning
tracker-settings-update-low-battery = Kan ej uppdatera. Batteriet är under 50%
tracker-settings-update-up_to_date = Uppdaterad
tracker-settings-update = Uppdatera nu
tracker-settings-update-title = Mjukvaroversion

## Tracker part card info

tracker-part_card-no_name = Inget namn
tracker-part_card-unassigned = Ej tilldelad

## Body assignment menu

body_assignment_menu = Var vill du att den här trackern ska vara?
body_assignment_menu-description = Välj en plats där du vill att denna tracker ska tilldelas. Alternativt kan du välja att hantera alla trackers på en gång istället för en och en.
body_assignment_menu-show_advanced_locations = Visa avancerade tilldelade platser
body_assignment_menu-manage_trackers = Hantera alla spårare
body_assignment_menu-unassign_tracker = Ta bort tilldelning av spårare

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Vilken tracker ska du tilldela din
tracker_selection_menu-NONE = Vilken tracker vill du att ska avaktiveras?
tracker_selection_menu-HEAD = { -tracker_selection-part } huvud?
tracker_selection_menu-NECK = { -tracker_selection-part } hals?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } högra axeln?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } höger överarm?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } höger underarm?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } höger hand?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } höger lår?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } höger fotled?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } höger fot?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } rätt styrenhet?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } övre bröst?
tracker_selection_menu-CHEST = { -tracker_selection-part } bröst?
tracker_selection_menu-WAIST = { -tracker_selection-part } midja?
tracker_selection_menu-HIP = { -tracker_selection-part } höft?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } vänster axel?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } vänster underarm
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } vänster underarm?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } vänster hand?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } vänster lår?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } vänstra vristen?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } vänster fot?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } vänster kontroll?
tracker_selection_menu-unassigned = Ej tilldelade trackers
tracker_selection_menu-assigned = Tilldelade trackers
tracker_selection_menu-dont_assign = Ej tilldelad
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Varning:</b> En halsboja kan vara livsfarlig om den sitter för hårt,
    då kan remmen skära av blodcirkulationen till huvudet!
tracker_selection_menu-neck_warning-done = Jag förstår riskerna
tracker_selection_menu-neck_warning-cancel = Avbryt

## Mounting menu

mounting_selection_menu = Var vill du att den här trackern ska vara?
mounting_selection_menu-close = Stäng

## Sidebar settings

settings-sidebar-title = Inställningar
settings-sidebar-general = Allmänt
settings-sidebar-tracker_mechanics = Mekanik för spårning
settings-sidebar-stay_aligned = Behåll inriktning
settings-sidebar-fk_settings = Inställningar för spårning
settings-sidebar-gesture_control = Geststyrning
settings-sidebar-interface = Gränssnitt
settings-sidebar-osc_router = OSC router
settings-sidebar-osc_trackers = VRChat OSC spårare
settings-sidebar-utils = Verktyg
settings-sidebar-serial = Seriell konsol
settings-sidebar-appearance = Utseende
settings-sidebar-notifications = Meddelanden
settings-sidebar-behavior = Beteende
settings-sidebar-firmware-tool = DIY Mjukvaroverktyg
settings-sidebar-vrc_warnings = VRChat Config varningar
settings-sidebar-advanced = Avancerat

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR spårare
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Aktivera eller inaktivera specifika SteamVR-trackers.
    Användbart för spel eller appar som bara stöder vissa spårare.
settings-general-steamvr-trackers-waist = Midja
settings-general-steamvr-trackers-chest = Bröstkorg
settings-general-steamvr-trackers-left_foot = Vänster fot
settings-general-steamvr-trackers-right_foot = Höger fot
settings-general-steamvr-trackers-left_knee = Vänster knä
settings-general-steamvr-trackers-right_knee = Höger knä
settings-general-steamvr-trackers-left_elbow = Vänster armbåge
settings-general-steamvr-trackers-right_elbow = Höger armbåge
settings-general-steamvr-trackers-left_hand = Vänster hand
settings-general-steamvr-trackers-right_hand = Höger hand
settings-general-steamvr-trackers-tracker_toggling = Automatisk tilldelning av spårare
settings-general-steamvr-trackers-tracker_toggling-description = Hanterar automatiskt att växla SteamVR-trackers på eller av beroende på dina aktuella tracker-tilldelningar
settings-general-steamvr-trackers-tracker_toggling-label = Automatisk tilldelning av spårare
settings-general-steamvr-trackers-hands-warning = <b>Varning:</b> Handspårare kommer att åsidosätta dina styrenheter. Är du säker på det?
settings-general-steamvr-trackers-hands-warning-cancel = Avbryt
settings-general-steamvr-trackers-hands-warning-done = Ja

## Tracker mechanics

settings-general-tracker_mechanics = Mekanik för spårning
settings-general-tracker_mechanics-filtering = Filtrering
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Välj filtreringstyp för dina trackers.
    Prediction förutspår rörelser medan smoothing jämnar ut rörelser.
settings-general-tracker_mechanics-filtering-type = Typ av filtrering
settings-general-tracker_mechanics-filtering-type-none = Ingen filtrering
settings-general-tracker_mechanics-filtering-type-none-description = Använd rotationer som de är. Kommer inte att göra någon filtrering.
settings-general-tracker_mechanics-filtering-type-smoothing = Utjämning
settings-general-tracker_mechanics-filtering-type-smoothing-description = Utjämnar rörelser men ger viss fördröjning.
settings-general-tracker_mechanics-filtering-type-prediction = Förutsägelse
settings-general-tracker_mechanics-filtering-type-prediction-description = Minskar latensen och gör rörelserna mer snabba, men kan öka jittern.
settings-general-tracker_mechanics-filtering-amount = Belopp
settings-general-tracker_mechanics-yaw-reset-smooth-time = Tid för utjämning av Yaw reset (0s avaktiverar utjämning)
settings-general-tracker_mechanics-drift_compensation = Driftkompensation
settings-general-tracker_mechanics-drift_compensation-enabled-label = Driftkompensation
settings-general-tracker_mechanics-drift_compensation-prediction = Drift kompensations-förutsägelse.
settings-general-tracker_mechanics-drift_compensation-prediction-label = Drift kompensations-förutsägelse.
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Avbryt
settings-general-tracker_mechanics-drift_compensation_warning-done = Jag förstår
settings-general-tracker_mechanics-drift_compensation-amount-label = Ersättningsbelopp
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Använd upp till x sista återställningar
settings-general-tracker_mechanics-save_mounting_reset = Spara automatisk montering återställning kalibrering
settings-general-tracker_mechanics-save_mounting_reset-description = Sparar de automatiska kalibreringarna för återställning av montering för trackers mellan omstarter. Användbart när du bär en dräkt där trackers inte flyttas mellan sessionerna. <b>Rekommenderas inte för normala användare!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Spara återställning av montering
settings-general-tracker_mechanics-use_mag_on_all_trackers = Använd magnetometern på alla IMU-sensorer som stödjer det
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Använder magnetometers på alla sensorer som har en kompatibel mjukvara till det, minskar drift i magnetiskt stabila miljöer.
    Kan stängas av för individuella spårare i dess inställningar. <b> Var vänlig och stäng inte av någon av sensorerna när du växlar av och på denna funktionen.
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = använd magnetometer på sensorer.
settings-stay_aligned = Behåll inriktning
settings-stay_aligned-description = Behåll inriktning minskar drift genom att gradvis justera dina sensorer för att matcha dina avslappnade positioner.
settings-stay_aligned-setup-label = Ställ in Behåll inriktning
settings-stay_aligned-setup-description = Du måste kompletera "Ställ in Behåll inriktnining" för att kunna aktivera Behåll inriktning.
settings-stay_aligned-warnings-drift_compensation = ⚠ Var vänlig stäng av Drift-kompensation! Drift-kompensation kommer att vara i konflikt med Behåll inriktning.
settings-stay_aligned-enabled-label = Justera sensorer
settings-stay_aligned-hide_yaw_correction-label = Göm justeringar (För att jämföra med ingen Behåll inriktning)
settings-stay_aligned-general-label = Allmän
settings-stay_aligned-relaxed_poses-label = Avslappnade positioner
settings-stay_aligned-relaxed_poses-description = Behåll inriktning använder dina avslappnade positioner till att hålla dina sensorer inriktade. Använd "ställ in Behåll inriktning" för att uppdatera dessa positionerna.
settings-stay_aligned-relaxed_poses-standing = Justera sensorer medans du står
settings-stay_aligned-relaxed_poses-sitting = Justera sensorer medans du sitter i en stol
settings-stay_aligned-relaxed_poses-flat = Justera sensorer medans du sitter på golvet, eller ligger på ryggen.
settings-stay_aligned-relaxed_poses-save_pose = Spara position
settings-stay_aligned-relaxed_poses-reset_pose = Återställ position
settings-stay_aligned-relaxed_poses-close = Stäng
settings-stay_aligned-debug-label = Avbuggning
settings-stay_aligned-debug-description = Var vänlig inkludera dina inställningar när du rapporterar problem om Behåll inriktning.
settings-stay_aligned-debug-copy-label = Kopiera inställningar till urklipp

## FK/Tracking settings

settings-general-fk_settings = Inställningar för spårning
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Golvklämma
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Korrigering av skridskoåkning
settings-general-fk_settings-leg_tweak-toe_snap = Snäppning av tå
settings-general-fk_settings-leg_tweak-foot_plant = Fotväxt
settings-general-fk_settings-leg_tweak-skating_correction-amount = Styrka vid korrigering av skridskoåkning
settings-general-fk_settings-leg_tweak-skating_correction-description = Skating-correction korrigerar för skridskoåkning men kan försämra precisionen i vissa rörelsemönster. När du aktiverar detta, se till att återställa och kalibrera om i spelet.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor-clip kan minska eller till och med eliminera klippning genom golvet. När du aktiverar detta, se till att återställa och kalibrera om i spelet.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe-snap försöker gissa rotationen på dina fötter om fotspårare inte används.
settings-general-fk_settings-leg_tweak-foot_plant-description = Fotplatta roterar fötterna så att de är parallella med marken vid kontakt.
settings-general-fk_settings-leg_fk = Spårning av ben
settings-general-fk_settings-enforce_joint_constraints = Skelett-gränser
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Upprätthåll begränsningar
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Förhindra leder från att rotera förbi dess gränser
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Rätta med begränsningar
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Rätta leds-rotationer när de går förbi dess gränser
settings-general-fk_settings-arm_fk = Spårning av arm
settings-general-fk_settings-arm_fk-description = Tvinga armarna att spåras från headsetet (HMD) även om positionsdata för handen finns tillgänglig.
settings-general-fk_settings-arm_fk-force_arms = Tvångsarmar från HMD
settings-general-fk_settings-reset_settings = Återställ inställningar
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Återställ HMD:ns pitch (vertikal rotation) när du gör en fullständig återställning. Användbart om du bär en HMD på pannan för VTubing eller mocap. Aktivera inte för VR.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Återställ HMD:s tonhöjd
settings-general-fk_settings-arm_fk-reset_mode-description = Ändra vilken armställning som förväntas för återställning av montering.
settings-general-fk_settings-arm_fk-back = Tillbaka
settings-general-fk_settings-arm_fk-back-description = Standardläget, där överarmarna går bakåt och underarmarna framåt.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (upp)
settings-general-fk_settings-arm_fk-tpose_up-description = Förväntar sig att armarna ska vara nere på sidorna under Full Reset och 90 grader upp på sidorna under Mounting Reset.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (nedåt)
settings-general-fk_settings-arm_fk-tpose_down-description = Förväntar sig att armarna ska vara 90 grader upp åt sidorna under Full Reset och nedåt på sidorna under Mounting Reset.
settings-general-fk_settings-arm_fk-forward = Framåt
settings-general-fk_settings-arm_fk-forward-description = Förväntar sig att dina armar är upp 90 grader framåt. Användbart för VTubing.
settings-general-fk_settings-skeleton_settings-toggles = Skelett-växlar
settings-general-fk_settings-skeleton_settings-description = Slå på eller av skelettinställningar. Vi rekommenderar att du låter dessa vara på.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Modell med förlängd ryggrad
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Förlängd bäckenmodell
settings-general-fk_settings-skeleton_settings-extended_knees_model = Förlängd knämodell
settings-general-fk_settings-skeleton_settings-ratios = Skelettets proportioner
settings-general-fk_settings-skeleton_settings-ratios-description = Ändra värdena för skelettinställningarna. Du kan behöva justera dina proportioner efter att du har ändrat dessa.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Imputera midja från bröst till höft
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Imputera midja från bröst till ben
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Imputera höft från bröst till ben
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Imputera höft från midja till ben
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Genomsnittlig höftrörelse och rullning med benen
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Genomsnittlig girning och rullning för knäspårarna med vristerna
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Knäens genomsnittliga gungning och rullning med vristerna
settings-general-fk_settings-self_localization-title = Mocap-läge
settings-general-fk_settings-self_localization-description = Mocap Mode gör att skelettet i stort sett kan följa sin egen position utan headset eller andra spårare. Observera att detta kräver fot- och huvudspårare för att fungera och att det fortfarande är experimentellt.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Geststyrning
settings-general-gesture_control-subtitle = Tap-baserade återställningar
settings-general-gesture_control-description = Gör att återställningar kan utlösas genom att trycka på en tracker. Den tracker som sitter högst upp på din bål används för Yaw Reset, den tracker som sitter högst upp på ditt vänstra ben används för Full Reset och den tracker som sitter högst upp på ditt högra ben används för Mounting Reset. Taps måste ske inom tidsgränsen 0,3 sekunder gånger antalet taps som ska erkännas.
settings-general-gesture_control-yawResetEnabled = Aktivera återställning av tap till yaw
settings-general-gesture_control-yawResetDelay = Fördröjning av återställning av yaw
settings-general-gesture_control-yawResetTaps = Tryck för återställning av yaw
settings-general-gesture_control-fullResetEnabled = Aktivera tryck för fullständig återställning
settings-general-gesture_control-fullResetDelay = Fördröjning vid fullständig återställning
settings-general-gesture_control-fullResetTaps = Kranar för fullständig återställning
settings-general-gesture_control-mountingResetEnabled = Aktivera tryck för att återställa montering
settings-general-gesture_control-mountingResetDelay = Fördröjning av återställning av montering
settings-general-gesture_control-mountingResetTaps = Gängtappar för montering av återställning
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackers över tröskelvärdet
settings-general-gesture_control-numberTrackersOverThreshold-description = Öka detta värde om avkänningen inte fungerar. Öka inte värdet mer än vad som krävs för att detekteringen ska fungera, eftersom det kan leda till fler falska positiva resultat.

## Appearance settings

settings-interface-appearance = Utseende
settings-general-interface-dev_mode = Utvecklarläge
settings-general-interface-dev_mode-description = Det här läget kan vara användbart om du behöver djupgående data eller vill interagera med anslutna trackers på en mer avancerad nivå.
settings-general-interface-dev_mode-label = Utvecklarläge
settings-general-interface-theme = Färg tema
settings-general-interface-show-navbar-onboarding = Visa { navbar-onboarding } på navigeringsfältet
settings-general-interface-show-navbar-onboarding-description = Detta förändras om { navbar-onboarding } knappen visas på navigeringsfältet
settings-general-interface-show-navbar-onboarding-label = Visa { navbar-onboarding }
settings-general-interface-lang = Välj språk
settings-general-interface-lang-description = Ändra det standardspråk som du vill använda.
settings-general-interface-lang-placeholder = Välj det språk du vill använda
# Keep the font name untranslated
settings-interface-appearance-font = GUI-teckensnitt
settings-interface-appearance-font-description = Detta ändrar det teckensnitt som används av gränssnittet.
settings-interface-appearance-font-placeholder = Standardteckensnitt
settings-interface-appearance-font-os_font = OS-teckensnitt
settings-interface-appearance-font-slime_font = Standardteckensnitt
settings-interface-appearance-font_size = Skalning av basteckensnitt
settings-interface-appearance-font_size-description = Detta påverkar teckenstorleken i hela gränssnittet utom i denna inställningspanel.
settings-interface-appearance-decorations = Använd systemets inbyggda dekorationer.
settings-interface-appearance-decorations-description = Detta kommer ej visualisera topfältet av gränssnittet och kommer att använda operativsystemets istället
settings-interface-appearance-decorations-label = Använd inbyggda dekorationer

## Notification settings

settings-interface-notifications = Meddelanden
settings-general-interface-serial_detection = Detektering av seriell enhet
settings-general-interface-serial_detection-description = Det här alternativet visar ett popup-fönster varje gång du ansluter en ny seriell enhet som kan vara en spårare. Det hjälper till att förbättra installationsprocessen för en tracker.
settings-general-interface-serial_detection-label = Detektering av seriell enhet
settings-general-interface-feedback_sound = Feedback-ljud
settings-general-interface-feedback_sound-description = Detta alternativ spelar upp ett ljud när en återställning utlöses.
settings-general-interface-feedback_sound-label = Feedback-ljud
settings-general-interface-feedback_sound-volume = Volym för återkopplingsljud
settings-general-interface-connected_trackers_warning = Varning för uppkopplade spårare
settings-general-interface-connected_trackers_warning-description = Detta alternativ kommer att visa ett popup-fönster varje gång du försöker lämna SlimeVR medan du har en eller flera anslutna trackers. Det påminner dig om att stänga av dina trackers när du är klar för att spara batteritid.
settings-general-interface-connected_trackers_warning-label = Varning för uppkopplade trackers vid utresa

## Behavior settings

settings-interface-behavior = Beteende
settings-general-interface-use_tray = Minimera till systemfältet
settings-general-interface-use_tray-description = Låter dig stänga fönstret utan att stänga SlimeVR-servern så att du kan fortsätta använda den utan att GUI stör dig.
settings-general-interface-use_tray-label = Minimera till systemfältet
settings-general-interface-discord_presence = Dela aktivitet på Discord
settings-general-interface-discord_presence-description = Berättar för din Discord-klient att du använder SlimeVR tillsammans med antalet IMU-trackers du använder.
settings-general-interface-discord_presence-label = Dela aktivitet på Discord
settings-interface-behavior-error_tracking = Error samling via Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Tillåter du samlingen av anonym error-data?</h1>
    
    <b>Vi samlar inte personlig information</b> så som din IP adress eller trådlösa referenser. Slimevr värdesätter din integritet!
    
    För att tillhandhålla den bästa användarupplevelsen, så samlar vi anonyma error-raporter, prestandamått och operativsystems-info. Detta hjälper oss upptäcka buggar och problem med Slimevr. Dessa rapporterna samlas via Sentry.io.
settings-interface-behavior-error_tracking-label = Skicka errors till utväcklare

## Serial settings

settings-serial = Seriell konsol
# This cares about multilines
settings-serial-description = Detta är en live informationsflöde för seriell kommunikation. Kan vara användbart för att felsöka problem med firmware eller maskinvara.
settings-serial-connection_lost = Anslutning till seriell enhet förlorad, återanslutning...
settings-serial-reboot = Omstart
settings-serial-factory_reset = Fabriksåterställning
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Warning:</b> Detta återställer trackern till fabriksinställningarna.<b>
    Det innebär att Wi-Fi- och kalibreringsinställningar <b>kommer att gå förlorade!</b>
settings-serial-factory_reset-warning-ok = Jag vet vad jag gör
settings-serial-factory_reset-warning-cancel = Avbryt
settings-serial-serial_select = Välj en serieport
settings-serial-auto_dropdown_item = Automatiskt
settings-serial-get_wifi_scan = Hämta WiFi-skanning
settings-serial-file_type = Vanlig text
settings-serial-save_logs = Spara till fil

## OSC router settings

settings-osc-router = OSC router
# This cares about multilines
settings-osc-router-description = Vidarebefordra OSC-meddelanden från ett annat program. Användbart för att använda ett annat OSC-program med VRChat till exempel.
settings-osc-router-enable = Aktivera
settings-osc-router-enable-description = Växla vidarebefordran av meddelanden.
settings-osc-router-enable-label = Aktivera
settings-osc-router-network = Nätverksportar
# This cares about multilines
settings-osc-router-network-description = Ställ in portarna för att lyssna och skicka data. Dessa kan vara desamma som andra portar som används i SlimeVR-servern.
settings-osc-router-network-address = Nätverksadress
settings-osc-router-network-address-description = Ange den adress som data ska skickas till.
settings-osc-router-network-address-placeholder = IPV4-adress

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC spårare
settings-osc-vrchat-enable = Aktivera
settings-osc-vrchat-enable-description = Växla mellan sändning och mottagning av data.
settings-osc-vrchat-enable-label = Aktivera
settings-osc-vrchat-oscqueryEnabled = Aktivera OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery känner automatiskt av körande instanser av VRChat och skickar data till OSCQuery.
    De kan även annonsera sig själva till VRChat för att få HMD och kontrollerdata.
    För att tillåta samling av HMD och kontrollerdata från VRChat, gå till din huvudmenys inställningar
    under "Tracking & IK" och tillåt "Allow Sending Head and Wrist VR Tracking OSC Data"
settings-osc-vrchat-oscqueryEnabled-label = Aktivera OSCQuery
settings-osc-vrchat-network = Nätverksportar
settings-osc-vrchat-network-description-v1 = Ställ in portarna för att lyssna och skicka data. Kan lämnas orörd för VRChat.
settings-osc-vrchat-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port Ut
    .placeholder = Port ut (default: 9000)
settings-osc-vrchat-network-address = Nätverksadress
settings-osc-vrchat-network-address-description-v1 = Välj vilken adress du vill skicka data till. Kan lämnas orörd för VRChat.
settings-osc-vrchat-network-address-placeholder = VRChat ip-adress
settings-osc-vrchat-network-trackers = Spårare
settings-osc-vrchat-network-trackers-description = Växla sändning av specifika trackers via OSC.
settings-osc-vrchat-network-trackers-chest = Bröstkorg
settings-osc-vrchat-network-trackers-hip = Höft
settings-osc-vrchat-network-trackers-knees = Knän
settings-osc-vrchat-network-trackers-feet = Fötter
settings-osc-vrchat-network-trackers-elbows = Armbågar

## VMC OSC settings

settings-osc-vmc = Virtuell rörelseinspelning
settings-osc-vmc-enable = Aktivera
settings-osc-vmc-enable-description = Växla mellan sändning och mottagning av data.
settings-osc-vmc-enable-label = Aktivera
settings-osc-vmc-network = Nätverksportar
settings-osc-vmc-network-description = Ställ in portarna för att lyssna på och skicka data via VMC.
settings-osc-vmc-network-port_in =
    .label = Port in
    .placeholder = Port in (standard: 39540)
settings-osc-vmc-network-port_out =
    .label = Port ut
    .placeholder = Port ut (standard: 39539)
settings-osc-vmc-network-address = Nätverksadress
settings-osc-vmc-network-address-description = Välj vilken adress du vill skicka ut data till via VMC.
settings-osc-vmc-network-address-placeholder = IPV4-adress
settings-osc-vmc-vrm = VRM-modell
settings-osc-vmc-vrm-description = Ladda en VRM-modell för att tillåta huvudförankring och möjliggöra högre kompatibilitet med andra applikationer.
settings-osc-vmc-vrm-untitled_model = Namnlös modell
settings-osc-vmc-vrm-file_select = Dra och släpp en modell att använda, eller <u>bläddra</u>
settings-osc-vmc-anchor_hip = Förankring vid höfterna
settings-osc-vmc-anchor_hip-description = Förankra följningen i höfterna, användbart för sittande VTubing. Om du inaktiverar, ladda en VRM-modell.
settings-osc-vmc-anchor_hip-label = Förankring vid höfterna
settings-osc-vmc-mirror_tracking = Spegla spårning
settings-osc-vmc-mirror_tracking-description = Spegla spårning horisontellt.
settings-osc-vmc-mirror_tracking-label = Spegla spårning

## Common OSC settings


## Advanced settings

settings-utils-advanced = Avancerad
settings-utils-advanced-reset-gui = Återställ GUI-inställningar
settings-utils-advanced-reset-gui-description = Återställ standardinställningarna för gränssnittet
settings-utils-advanced-reset-gui-label = Återställ GUI
settings-utils-advanced-reset-server = Återställ spårnings-inställningarna
settings-utils-advanced-reset-server-description = Återställ standardinställningarna för spårningen.
settings-utils-advanced-reset-server-label = Återställ spårning
settings-utils-advanced-reset-all = Återställ alla inställningar
settings-utils-advanced-reset-all-description = Återställ standardinställningarna för både gränssnittet och spårningen.
settings-utils-advanced-reset-all-label = Återställ allt
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>Varning;</b> Detta kommer att återställa dina GUI-inställningar till Standard.
            Är du säker på att du vill göra detta?
        [server]
            <b>Varning;</b> Detta kommer att återställa dina spårnings-inställningar till Standard.
            Är du säker på att du vill göra detta?
       *[all]
            <b>Varning;</b> Detta kommer att återställa alla dina inställningar till Standard.
            Är du säker på att du vill göra detta?
    }
settings-utils-advanced-reset_warning-reset = Återställ inställningar
settings-utils-advanced-reset_warning-cancel = Avbryt
settings-utils-advanced-open_data-v1 = Config mapp
settings-utils-advanced-open_data-description-v1 = Öppna SlimeVR's config mapp i filutforskaren, som innehåller konfigurationen.
settings-utils-advanced-open_data-label = Öppna mapp
settings-utils-advanced-open_logs = Logg-mapp
settings-utils-advanced-open_logs-description = Öppna SlimeVR's config mapp i filutforskaren, som innehåller appens loggar
settings-utils-advanced-open_logs-label = Öppna mapp

## Home Screen


## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = Hoppa över inställning
onboarding-continue = Fortsätt
onboarding-wip = Pågående arbete
onboarding-previous_step = Föregående steget
onboarding-setup_warning =
    <b>Varning:</b> Den inledande installationen krävs för bra spårning,
    den behövs om det är första gången du använder SlimeVR.
onboarding-setup_warning-skip = Hoppa över inställning
onboarding-setup_warning-cancel = Fortsätt inställning

## Wi-Fi setup

onboarding-wifi_creds-back = Gå tillbaka till introduktion
onboarding-wifi_creds-skip = Hoppa över Wi-Fi inställningar.
onboarding-wifi_creds-submit = Överlämna!
onboarding-wifi_creds-ssid =
    .label = Wi-Fi namn
    .placeholder = Fyll i Wi-Fi namn
onboarding-wifi_creds-ssid-required = Wi-Fi namn är nödvändigt
onboarding-wifi_creds-password =
    .label = Lösenord
    .placeholder = Ange lösenord

## Mounting setup

onboarding-reset_tutorial-back = Gå tillbaka till monteringskalibrering
onboarding-reset_tutorial = Börja om introduktionen
onboarding-reset_tutorial-explanation = När du använder dina trackers kan de hamna ur linje på grund av IMU:s girdrift eller för att du har flyttat dem fysiskt. Du har flera sätt att åtgärda detta.
onboarding-reset_tutorial-skip = Hoppa över steg
# Cares about multiline
onboarding-reset_tutorial-0 =
    Tryck { $taps } gånger på den markerade trackern för att utlösa yaw reset.
    
    Detta gör att spårarna vänds i samma riktning som ditt headset (HMD).

## Setup start

onboarding-home = Välkommen till SlimeVR
onboarding-home-start = Låt oss komma igång!

## Setup done

onboarding-done-title = Du är klar!
onboarding-done-description = Njut av en helkroppsupplevelse
onboarding-done-close = Stäng inställningen

## Tracker connection setup

onboarding-connect_tracker-back = Gå tillbaka till Wi-Fi-legitimation
onboarding-connect_tracker-title = Ansluta spårare
onboarding-connect_tracker-description-p0-v1 = Nu till den roliga delen, att ansluta trackers!
onboarding-connect_tracker-description-p1-v1 = Anslut varje tracker en i taget via en USB-port.
onboarding-connect_tracker-issue-serial = Jag har problem med att ansluta!
onboarding-connect_tracker-usb = USB spårare
onboarding-connect_tracker-connection_status-none = Letar efter spårare
onboarding-connect_tracker-connection_status-serial_init = Anslutning till seriell enhet
onboarding-connect_tracker-connection_status-obtaining_mac_address = Får sensorns mac adress
onboarding-connect_tracker-connection_status-provisioning = Skicka Wi-Fi-autentiseringsuppgifter
onboarding-connect_tracker-connection_status-connecting = Försöker ansluta till Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Söker server
onboarding-connect_tracker-connection_status-connection_error = Det går inte att ansluta till Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Kunde inte hitta servern
onboarding-connect_tracker-connection_status-done = Ansluten till servern
onboarding-connect_tracker-connection_status-no_serial_log = Kunde inte få en logg från trackern.
onboarding-connect_tracker-connection_status-no_serial_device_found = Kunde inte hitta en tracker från usb
onboarding-connect_serial-error-modal-no_serial_log = Är trackern påslagen?
onboarding-connect_serial-error-modal-no_serial_log-desc = Säkerställ att trackern är på och är kopplad till din dator.
onboarding-connect_serial-error-modal-no_serial_device_found = Inga trackers upptäckta
onboarding-connect_serial-error-modal-no_serial_device_found-desc =
    Var vänlig och koppla en tracker med inkluderade USB kabeln till din dator och slå på trackern.
    Om detta inte fungerar:
    -Testa med an annan USB sladd
    -Testa med en annan USB-port
    -Testa installera om SlimeVR servern och välj "USB Drivers" i komponentsektionen.
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Inga spårare anslutna
        [one] En spårare ansluten
       *[other] { $amount } spårare anslutna
    }
onboarding-connect_tracker-next = Jag har anslutit alla mina spårare

## Tracker calibration tutorial

onboarding-calibration_tutorial = Handledning för IMU-kalibrering
onboarding-calibration_tutorial-subtitle = Detta kommer att bidra till att minska spårarens drift!
onboarding-calibration_tutorial-calibrate = Jag placerade mina trackers på bordet
onboarding-calibration_tutorial-status-waiting = Väntar på dig
onboarding-calibration_tutorial-status-calibrating = Kalibrering
onboarding-calibration_tutorial-status-success = Snyggt!
onboarding-calibration_tutorial-status-error = Spåraren flyttades
onboarding-calibration_tutorial-skip = Hoppa över introduktion

## Tracker assignment tutorial

onboarding-assignment_tutorial = Hur man förbereder en Slime Tracker innan den sätts på
onboarding-assignment_tutorial-first_step = 1. Placera ett klistermärke för en kroppsdel (om du har ett sådant) på spåraren enligt ditt val
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Klistermärken
onboarding-assignment_tutorial-second_step-v2 = 2. Fäst remmen på din tracker och håll kardborresidan av remmen vänd åt samma håll som slime-ansiktet på din tracker:
onboarding-assignment_tutorial-second_step-continuation-v2 = Kardborresidan för förlängningen ska vara vänd uppåt som på följande bild:
onboarding-assignment_tutorial-done = Jag sätter på klistermärken och remmar!

## Tracker assignment setup

onboarding-assign_trackers-back = Gå tillbaka till Wi-Fi uppgifter
onboarding-assign_trackers-title = Utse trackers
onboarding-assign_trackers-description = Låt oss välja vilken tracker som ska sitta var. Klicka på en plats där du vill placera en tracker
onboarding-assign_trackers-advanced = Visa avancerade tilldelar-positioner
onboarding-assign_trackers-next = Jag har tilldelat alla trackers
onboarding-assign_trackers-mirror_view = Spegla vy
onboarding-assign_trackers-option-amount =
    { $trackersCount ->
        [one] x{ $trackersCount }
       *[other] x{ $trackersCount }
    }
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] Lägre-kropps kit
        [core] Kärna kit
        [enhanced-core] Förbättrad kärna kit
        [full-body] Full kropps kit
       *[all] Alla kit
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] Minimum för full kropps-spårning
        [core] +Förbättrad ryggrads-spårning
        [enhanced-core] +Fötters-rotation
        [full-body] +Armbågs-spårning
       *[all] Alla tillgängliga tracker-val
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] Vänster fot är tilldelad men du behöver även tilldela vänster fotled, vänster lår och antingen bröstkorgen, höften eller midjan!
        [1] Vänster fot är tilldelad men du behöver även tilldela vänster lår och antingen bröstkorgen, höften eller midjan!
        [2] Vänster fot är tilldelad men du behöver även tilldela vänster fotled och antingen bröstkorgen, höften eller midjan!
        [3] Vänster fot är tilldelad men du behöver även tilldela antingen bröstkorgen, höften eller midjan!
        [4] Vänster fot är tilldelad men vänster fotled och vänster lår behövs även tilldelas!
        [5] Vänster fot är tilldelad men vänster lår behövs även tilldelas!
        [6] Vänster fot är tilldelad men vänster fotled behövs även tilldelas!
       *[unknown] Vänster fot är tilldelad men du behöver även okänd icke tilldelad kroppsdel tilldelad!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [6] Höger fot är tilldelad men du behöver även tilldela höger fotled, höger och antingen bröstkorgen, höften eller midjan!
        [5] Vänster fot är tilldelad men du behöver även tilldela vänster lår och antingen bröstkorgen, höften eller midjan!
        [4] Vänster fot är tilldelad men du behöver även tilldela vänster fotled och antingen bröstkorgen, höften eller midjan!
        [3] Vänster fot är tilldelad men du behöver även tilldela antingen bröstkorgen, höften eller midjan!
        [2] Vänster fot är tilldelad men vänster fotled och vänster lår behövs även tilldelas!
        [1] Vänster fot är tilldelad men vänster lår behövs även tilldelas!
        [0] Vänster fot är tilldelad men vänster fotled behövs även tilldelas!
       *[unknown] Höger fot är tilldelad men du behöver även tilldela okänd icke tilldelad kroppsdel!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] Vänster fotled är ej tilldelad men du behöver även tilldela vänster lår och antingen bröstkorgen, höfter eller midjan!
        [1] Vänster fotled är tilldelad men du behöver även tilldela antingen bröstkorgen, höfter eller midjan!
        [2] Vänster fotled är tilldelad men du behöver även tilldela vänster lår!
       *[unknown] Vänster fotled är tilldelad men du behöver även tilldela okänd icke tilldelad kroppsdel!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [2] Höger fotled är tilldelad men du behöver även tilldela höger lår!
        [1] Höger fotled är tilldelad men du behöver tilldela antingen bröstkorgen, höften eller midjan!
        [0] Höger fotled är tilldelad men du behöver tilldela vänster lår och antingen bröstkorgen, höften eller midjan!
       *[unknown] Höger fotled är tilldelad men du behöver även tilldela okänd icke tilldelad kroppsdel!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] Vänster lår är tilldelad men du behöver även tilldela antingen bröstkorgen, höften eller midjan!
       *[unknown] Vänster lår är tilldelad men du behöver även tilldela okänd ej tilldelad kroppsdel!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] Höger lår är tilldelad, men du behöver även tilldela aningen bröstkorgen, höften eller midjan!
       *[unknown] Höger lår är tilldelad men du behöver även tilldela okänd ej tilldelad kroppsdel!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] Höft är tilldelad men du behöver även tilldela bröstkorg!
       *[unknown] Höft är tilldelad men du behöver även tilldela okänd ej tilldelad kroppsdel!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] Midja är tilldelad men du behöver även tilldela bröstkorg!
       *[unknown] Midja är tilldelad men du behöver även tilldela okänd ej tilldelad kroppsdel!
    }

## Tracker mounting method choose

onboarding-choose_mounting = Vilken kalibrerings-metod ska man använda?
# Multiline text
onboarding-choose_mounting-description = Monteringsriktningen korrigerar för placeringen av trackers på din kropp.
onboarding-choose_mounting-auto_mounting = Automatisk montering.
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Rekomenderad
onboarding-choose_mounting-auto_mounting-description = Detta kommer automatiskt känna av monteringsriktningen av alla dina trackers från 2 positioner
onboarding-choose_mounting-manual_mounting = Manuell montering
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Detta är kanske inte tillräckligt exakt
onboarding-choose_mounting-manual_mounting-description = Detta kommer låta dig välja monteringsriktningen manuellt för varje tracker
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Är du säker på att du vill göra
    den automatiska monterings-kalibreringen?
onboarding-choose_mounting-manual_modal-description =
    <b>Den manuella monterings-kalibreringen är rekommenderad för nya användare</b>, eftersom den automatiska monterings-kalibreringen kan vara svår att få rätt första gången
    och kan behöva lite träning för att få rätt.
onboarding-choose_mounting-manual_modal-confirm = Jag är säker på vad jag gör
onboarding-choose_mounting-manual_modal-cancel = Avbryt

## Tracker manual mounting setup

onboarding-manual_mounting-back = Gå tillbaka för att gå in i VR
onboarding-manual_mounting = Manuell montering
onboarding-manual_mounting-description = Klicka på varje tracker och välj vilket håll de är monterade på
onboarding-manual_mounting-auto_mounting = Automatisk montering
onboarding-manual_mounting-next = Nästa steg

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Gå tillbaka för att gå in i VR
onboarding-automatic_mounting-title = Monterings-Kalibrering
onboarding-automatic_mounting-description = För att SlimeVR trackers ska fungera, so måste vi tilldela en monterings-riktning för dina trackers för att linjera de med din fysiska tracker-montering.
onboarding-automatic_mounting-manual_mounting = Manuell montering
onboarding-automatic_mounting-next = Nästa steg
onboarding-automatic_mounting-prev_step = Föregående steg
onboarding-automatic_mounting-done-title = Monterings orientering kalibrerad.
onboarding-automatic_mounting-done-description = Din monterings kalibrering är klar!
onboarding-automatic_mounting-done-restart = Försök igen
onboarding-automatic_mounting-mounting_reset-title = Monterings-återställning
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Huka i en "Skidåknings" position med böjda ben, framåtlutad överkropp, och böjda armar.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Tryck på "Återställ montering" knappen och vänta 3 sekunder innan trackerns montering kommer att återställas.
onboarding-automatic_mounting-preparation-title = Förberedning
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Tryck på "Full återställning" knappen.
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Stå rakt upp med dina armar vid sidan av dig. Kom ihåg att kolla framåt.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Håll positioner tills 3s timern går ut.
onboarding-automatic_mounting-put_trackers_on-title = Sätt på dig dina trackers
onboarding-automatic_mounting-put_trackers_on-description = För att kalibrera monterings-riktningen, så kommer i att använda trackersen du precis tilldelade. Sätt på alla dina trackers, du kan se vilka som är vilka i figuren till höger.
onboarding-automatic_mounting-put_trackers_on-next = Jag har på mig alla trackers

## Tracker manual proportions setupa

onboarding-manual_proportions-title = Manuella kropps-dimensioner
onboarding-manual_proportions-fine_tuning_button = Finjustera automatiskt proportioner
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Var vänlig anslut ett VR-headset för att använda automatisk finjustering
onboarding-manual_proportions-export = Exportera proportioner
onboarding-manual_proportions-import = Importera proportioner
onboarding-manual_proportions-file_type = Kropps-proportions fil
onboarding-manual_proportions-normal_increment = Normal ökning
onboarding-manual_proportions-precise_increment = Detaljerad ökning
onboarding-manual_proportions-grouped_proportions = Grupperad ökning
onboarding-manual_proportions-all_proportions = Alla proportioner
onboarding-manual_proportions-estimated_height = Uppskattad användarlängd

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Gå tillbaka till manuella proportioner
onboarding-automatic_proportions-title = Mät din kropp
onboarding-automatic_proportions-description = För att SlimeVR-trackers ska fungera behöver vi veta längden på dina ben. Denna korta kalibrering kommer att mäta den åt dig.
onboarding-automatic_proportions-manual = Manuella proportionern
onboarding-automatic_proportions-prev_step = Föregående steg
onboarding-automatic_proportions-put_trackers_on-title = Sätt på alla dina trackers
onboarding-automatic_proportions-put_trackers_on-description = För att kalibrera proportionerna ska vi använda de trackers du just tilldelade. Sätt på dig alla dina trackers, du kan se vilka som är vilka i figuren till höger.
onboarding-automatic_proportions-check_height-guardian_tip =
    Om du använder ett fristående VR-headset, se till att ha din guardian /
    boundary aktiverad så att din höjd är korrekt!
onboarding-automatic_proportions-start_recording-description = Vi kommer nu att spela in några specifika poser och rörelser. Dessa kommer att visas på nästa skärm. Var redo att starta när du trycker på knappen!

## User height calibration


## Stay Aligned setup


## Home


## Trackers Still On notification


## Status system


## Firmware tool globals


## Firmware tool Steps

firmware_tool-flash_method_ota-devices = Upptäckta OTA enheter:
firmware_tool-flash_method_ota-no_devices = Det finns inga kort som kan uppdateras med OTA, se till att du valde rätt kort-typ
firmware_tool-flash_method_serial-wifi = Wi-Fi information:
firmware_tool-flash_method_serial-devices-label = Upptäckta seriell enheter:
firmware_tool-flash_method_serial-devices-placeholder = Välj en seriell enhet.
firmware_tool-flash_method_serial-no_devices = Det finns inga kompatibla upptäckta seriella enheter, se till att trackern är inkopplad
firmware_tool-build_step = Bygger
firmware_tool-build_step-description = Mjukvaran byggs, var vänlig vänta
firmware_tool-flashing_step = Flashar nu
firmware_tool-flashing_step-description = Dina trackers flashar, var vänlig och följ instruktionerna på skärmen.
firmware_tool-flashing_step-warning-v2 = Koppla inte ur eller stäng av trackern under processen, om du inte uppmanats att göra det. Det kan göra ditt kort ej användbart.
firmware_tool-flashing_step-flash_more = Flasha fler trackers
firmware_tool-flashing_step-exit = Stäng

## firmware tool build status

firmware_tool-build-CREATING_BUILD_FOLDER = Skapar bygges-filen
firmware_tool-build-BUILDING = Bygger mjukvaran.
firmware_tool-build-SAVING = Sparar bygget.
firmware_tool-build-DONE = Byggning färdig
firmware_tool-build-ERROR = Kunde ej bygga mjukvaran

## Firmware update status

firmware_update-status-DOWNLOADING = Laddar ner mjukvaran
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Var vänlig och stäng av och på trackern
firmware_update-status-AUTHENTICATING = Autentiserar med MCUn
firmware_update-status-UPLOADING = Laddar upp mjukvaran
firmware_update-status-SYNCING_WITH_MCU = Synkar med MCUn
firmware_update-status-REBOOTING = Tillämpar uppdateringen
firmware_update-status-PROVISIONING = Ställer in Wi-Fi informationen
firmware_update-status-DONE = Uppdatering klar!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Kunde inte hitta enheten
firmware_update-status-ERROR_TIMEOUT = Uppdateringsprocessen är i timeout
firmware_update-status-ERROR_DOWNLOAD_FAILED = Kunde inte ladda ner mjukvaran
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Kunde inte autentisera med MCUn
firmware_update-status-ERROR_UPLOAD_FAILED = Kunde inte ladda upp mjukvaran
firmware_update-status-ERROR_PROVISIONING_FAILED = Kunde inte ställa in Wi-Fi informationen
firmware_update-status-ERROR_UNSUPPORTED_METHOD = Uppdaterings-metoden är inte stödd
firmware_update-status-ERROR_UNKNOWN = Okänd errror

## Dedicated Firmware Update Page

firmware_update-title = Mjukvaro-uppdatering
firmware_update-devices = Tillgängliga enheter

## Tray Menu


## First exit modal

# Multiline text
tray_or_exit_modal-description = Detta gör att du kan välja om du vill avsluta servern eller minimera den till facket när du trycker på stäng-knappen. Du kan ändra detta senare i gränssnittsinställningarna!

## Unknown device modal

unknown_device-modal-description =
    Det finns en ny tracker med MAC-adress <b>{ $deviceId }</b>.
    Vill du ansluta den till SlimeVR?
vrc_config-page-help = Hittar inte inställningen?
vrc_config-page-help-desc = Kolla vår <a>dokumentation på detta området!</a>
vrc_config-page-big_menu = Tracking & IK (stora menyn)
vrc_config-page-big_menu-desc = Inställningar relaterade till IK i stora inställnings-menyn
vrc_config-page-wrist_menu = Tracking & IK (handleds-meny)
vrc_config-page-wrist_menu-desc = Inställningar relaterade till IK i lilla inställnings-menyn (handleds-meny)
vrc_config-on = På
vrc_config-off = Av
vrc_config-invalid = Du har felkonfigurerade VRChat inställningar!
vrc_config-show_more = Visa mer
vrc_config-setting_name = VRChat inställningsnamn
vrc_config-recommended_value = Rekommenderat värde
vrc_config-current_value = Nuvarande värde
vrc_config-mute = Tysta Varning
vrc_config-mute-btn = Stäng av ljud
vrc_config-unmute-btn = Sätt på ljud
vrc_config-legacy_mode = Använd gammal IK Solving
vrc_config-disable_shoulder_tracking = Avaktivera axel-spårning
vrc_config-shoulder_width_compensation = Axelbredds-kompensering
vrc_config-spine_mode = FBT ryggrads-läge
vrc_config-tracker_model = FBT Spårnings-modell
vrc_config-avatar_measurement_type = Avatar-mätning
vrc_config-calibration_range = Kalibrerings-räckvidd
vrc_config-calibration_visuals = Visa visuella kalibrerings-element
vrc_config-user_height = Användarens riktiga längd
vrc_config-spine_mode-UNKNOWN = Okänd
vrc_config-spine_mode-LOCK_BOTH = Lås båda
vrc_config-spine_mode-LOCK_HEAD = Lås huvud
vrc_config-spine_mode-LOCK_HIP = Lås höft
vrc_config-tracker_model-UNKNOWN = Okänd
vrc_config-tracker_model-AXIS = Vridaxel
vrc_config-tracker_model-BOX = Låda
vrc_config-tracker_model-SPHERE = Sfär
vrc_config-tracker_model-SYSTEM = System
vrc_config-avatar_measurement_type-UNKNOWN = Okänd
vrc_config-avatar_measurement_type-HEIGHT = Längd
vrc_config-avatar_measurement_type-ARM_SPAN = Armspann

## Error collection consent modal

error_collection_modal-title = Kan vi samla errors?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    Du kan ändra denna inställningen senare i beteende-sektionen av inställnings-sidan
error_collection_modal-confirm = Jag tillåter.
error_collection_modal-cancel = Jag vill inte

## Tracking checklist section

