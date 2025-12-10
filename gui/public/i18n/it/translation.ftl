# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Connessione al server in corso
websocket-connection_lost = Connessione con il server persa. Tentativo di riconnessione in corso...
websocket-connection_lost-desc = Sembra che il server SlimeVR si sia crashato. Controlla i log e riavvia il programma
websocket-timedout = Impossibile connettersi al server
websocket-timedout-desc = Sembra che il server SlimeVR si sia crashato o bloccato. Controlla i log e riavvia il programma
websocket-error-close = Chiudi SlimeVR
websocket-error-logs = Apri la cartella dei log

## Update notification

version_update-title = Nuova versione disponibile: { $version }
version_update-description = Cliccando su "{ version_update-update }" si scaricherà il programma di installazione di SlimeVR.
version_update-update = Aggiorna
version_update-close = Chiudi

## Tips

tips-find_tracker = Non sei sicurə quale tracker è quale? Agitalo e l'oggetto corrispondente sarà evidenziato.
tips-do_not_move_heels = Assicurati di non muovere i tuoi talloni durante la registrazione!
tips-file_select = Trascina qui i file da usare, o <u>sfoglia</u>.
tips-tap_setup = Puoi toccare lentamente 2 volte il tracker per sceglierlo invece di selezionarlo dal menu.
tips-turn_on_tracker = Stai utilizzando i tracker ufficiali di SlimeVR? Ricordati di <b><em>accendere il tuo tracker</em></b> dopo averlo collegato al PC!
tips-failed_webgl = Inizializzazione WebGL fallita.

## Units


## Body parts

body_part-NONE = Non assegnato
body_part-HEAD = Testa
body_part-NECK = Collo
body_part-RIGHT_SHOULDER = Spalla destra
body_part-RIGHT_UPPER_ARM = Parte superiore del braccio destro
body_part-RIGHT_LOWER_ARM = Avambraccio destro
body_part-RIGHT_HAND = Mano destra
body_part-RIGHT_UPPER_LEG = Coscia destra
body_part-RIGHT_LOWER_LEG = Caviglia destra
body_part-RIGHT_FOOT = Piede destro
body_part-UPPER_CHEST = Parte superiore del petto
body_part-CHEST = Petto
body_part-WAIST = Girovita
body_part-HIP = Bacino
body_part-LEFT_SHOULDER = Spalla sinistra
body_part-LEFT_UPPER_ARM = Parte superiore del braccio sinistro
body_part-LEFT_LOWER_ARM = Avambraccio sinistro
body_part-LEFT_HAND = Mano sinistra
body_part-LEFT_UPPER_LEG = Coscia sinistra
body_part-LEFT_LOWER_LEG = Caviglia sinistra
body_part-LEFT_FOOT = Piede sinistro
body_part-LEFT_THUMB_METACARPAL = Metacarpo del pollice sinistro
body_part-LEFT_THUMB_PROXIMAL = Falange prossimale del pollice sinistro
body_part-LEFT_THUMB_DISTAL = Falange distale del pollice sinistro
body_part-LEFT_INDEX_PROXIMAL = Falange prossimale dell'indice sinistro
body_part-LEFT_INDEX_INTERMEDIATE = Falange intermedia dell'indice sinistro
body_part-LEFT_INDEX_DISTAL = Falange distale dell'indice sinistro
body_part-LEFT_MIDDLE_PROXIMAL = Falange prossimale del medio sinistro
body_part-LEFT_MIDDLE_INTERMEDIATE = Falange intermedia del medio sinistro
body_part-LEFT_MIDDLE_DISTAL = Falange distale del medio sinistro
body_part-LEFT_RING_PROXIMAL = Falange prossimale dell'anulare sinistro
body_part-LEFT_RING_INTERMEDIATE = Falange intermedia dell'anulare sinistro
body_part-LEFT_RING_DISTAL = Falange distale dell'anulare sinistro
body_part-LEFT_LITTLE_PROXIMAL = Falange prossimale del mignolo sinistro
body_part-LEFT_LITTLE_INTERMEDIATE = Falange intermedia del mignolo sinistro
body_part-LEFT_LITTLE_DISTAL = Falange distale del mignolo sinistro
body_part-RIGHT_THUMB_METACARPAL = Metacarpo del pollice destro
body_part-RIGHT_THUMB_PROXIMAL = Falange prossimale del pollice destro
body_part-RIGHT_THUMB_DISTAL = Falange distale del pollice destro
body_part-RIGHT_INDEX_PROXIMAL = Falange prossimale dell'indice destro
body_part-RIGHT_INDEX_INTERMEDIATE = Falange intermedia dell'indice destro
body_part-RIGHT_INDEX_DISTAL = Falange distale dell'indice destro
body_part-RIGHT_MIDDLE_PROXIMAL = Falange prossimale del medio destro
body_part-RIGHT_MIDDLE_INTERMEDIATE = Falange intermedia del medio destro
body_part-RIGHT_MIDDLE_DISTAL = Falange distale del medio destro
body_part-RIGHT_RING_PROXIMAL = Falange prossimale dell'anulare destro
body_part-RIGHT_RING_INTERMEDIATE = Falange intermedia dell'anulare destro
body_part-RIGHT_RING_DISTAL = Falange distale dell'anulare destro
body_part-RIGHT_LITTLE_PROXIMAL = Falange prossimale del mignolo destro
body_part-RIGHT_LITTLE_INTERMEDIATE = Falange intermedia del mignolo destro
body_part-RIGHT_LITTLE_DISTAL = Falange distale del mignolo destro

## BoardType

board_type-UNKNOWN = Sconosciuto
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Scheda Personalizzata
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
board_type-GLOVE_IMU_SLIMEVR_DEV = Guanto SlimeVR Dev IMU

## Proportions

skeleton_bone-NONE = Nessuna selezione
skeleton_bone-HEAD = Correzione per la testa
skeleton_bone-HEAD-desc =
    La distanza tra il visore e il centro della testa.
    Scuoti la testa da sinistra a destra come se si fosse in disaccordo
    e modificala fino a quando il movimento degli altri tracker è trascurabile.
skeleton_bone-NECK = Lunghezza del collo
skeleton_bone-NECK-desc =
    La distanza tra il centro della testa e la base del collo.
    Muovere la testa verso l'alto e verso il basso come se si stesse annuendo o inclinare la testa a sinistra e a destra, modificala fino a quando il movimento degli altri tracker è trascurabile.
skeleton_bone-torso_group = Lunghezza del torso
skeleton_bone-torso_group-desc =
    La distanza dalla base del collo ai fianchi.
    Modificala stando in piedi finché i fianchi virtuali non si allineano a quelli reali.
skeleton_bone-UPPER_CHEST = Lunghezza della parte superiore del petto
skeleton_bone-UPPER_CHEST-desc =
    La distanza tra la base del collo e il centro del torace.
    Regola correttamente la lunghezza del busto e modificala in varie posizioni
    (seduti, piegati, sdraiati, ecc.) finché la colonna vertebrale virtuale non corrisponde a quella reale.
skeleton_bone-CHEST_OFFSET = Correzione per il petto
skeleton_bone-CHEST_OFFSET-desc = Questo parametro può essere regolato per spostare il tracker virtuale del torace verso l'alto o verso il basso, per aiutare a calibrare alcuni giochi o applicazioni che prevedono tracker più alto o più basso.
skeleton_bone-CHEST = Lunghezza del torace
skeleton_bone-CHEST-desc =
    La distanza tra il centro del petto e il centro della colonna vertebrale.
    Regola correttamente la lunghezza del busto e modificala in varie posizioni
    (seduti, piegati, sdraiati, ecc.) finché la colonna vertebrale virtuale non corrisponde a quella reale.
skeleton_bone-WAIST = Lunghezza del girovita
skeleton_bone-WAIST-desc =
    La distanza tra il centro della colonna vertebrale e l'ombelico.
    Regola correttamente la lunghezza del busto e modificala in varie posizioni
    (seduti, piegati, sdraiati, ecc.) finché la colonna vertebrale virtuale non corrisponde a quella reale.
skeleton_bone-HIP = Lunghezza del bacino
skeleton_bone-HIP-desc =
    La distanza tra l'ombelico e i fianchi
    Regola correttamente la lunghezza del busto e modificala in varie posizioni
    (seduti, piegati, sdraiati, ecc.) finché la colonna vertebrale virtuale non corrisponde a quella reale.
skeleton_bone-HIP_OFFSET = Correzione per il bacino
skeleton_bone-HIP_OFFSET-desc = Questo può parametro essere regolato per spostare il tracker virtuale dell'anca verso l'alto o verso il basso, in modo da aiutare a calibrare alcuni giochi o applicazioni che prevedono di posizionarlo sulla vita.
skeleton_bone-HIPS_WIDTH = Larghezza del bacino
skeleton_bone-HIPS_WIDTH-desc =
    La distanza tra l'inizio delle gambe.
    Esegui un reset completo con le gambe dritte e modificala finché
    le gambe virtuali non coincidono con quelle reali in orizzontale.
skeleton_bone-leg_group = Lunghezza della gamba
skeleton_bone-leg_group-desc =
    La distanza tra i fianchi e i piedi.
    Regola correttamente la lunghezza del busto e modificala
    finché i piedi virtuali non si trovano allo stesso livello di quelli reali.
skeleton_bone-UPPER_LEG = Lunghezza della parte superiore della gamba
skeleton_bone-UPPER_LEG-desc =
    La distanza tra i fianchi e le ginocchia.
    Regola adeguatamente la lunghezza delle gambe e modificala
    finché le ginocchia virtuali non sono allo stesso livello di quelle reali.
skeleton_bone-LOWER_LEG = Lunghezza della parte inferiore della gamba
skeleton_bone-LOWER_LEG-desc =
    La distanza tra le ginocchia e le caviglie.
    Regola correttamente la lunghezza delle gambe e modificala
    fino a quando le ginocchia virtuali sono allo stesso livello di quelle reali.
skeleton_bone-FOOT_LENGTH = Lunghezza dei piedi
skeleton_bone-FOOT_LENGTH-desc =
    La distanza tra le caviglie e le dita dei piedi.
    Cammina in punta di piedi e modificala finché i piedi virtuali non rimangono in posizione.
skeleton_bone-FOOT_SHIFT = Correzione per i piedi
skeleton_bone-FOOT_SHIFT-desc =
    Questo valore è la distanza orizzontale dal ginocchio alla caviglia.
    Tiene conto del fatto che la parte inferiore delle gambe va all'indietro quando si sta in piedi.
    Per regolarla, impostare la lunghezza dei piedi su 0, eseguire un reset completo e modificarla
    finché i piedi virtuali di non si allineano al centro delle caviglie.
skeleton_bone-SKELETON_OFFSET = Correzione per lo scheletro
skeleton_bone-SKELETON_OFFSET-desc =
    Questo parametro può essere regolato per spostare tutti i tracker in avanti o indietro.
    Può essere utilizzato per facilitare la calibrazione in alcuni giochi o applicazioni
    che potrebbero richiedere che i tracker siano più in avanti.
skeleton_bone-SHOULDERS_DISTANCE = Distanza delle spalle
skeleton_bone-SHOULDERS_DISTANCE-desc =
    La distanza verticale dalla base del collo alle spalle.
    Imposta Lunghezza braccio superiore su 0 e modificala finché i tracker dei gomiti virtuali
    non si allineano verticalmente con le spalle reali.
skeleton_bone-SHOULDERS_WIDTH = Larghezza delle spalle
skeleton_bone-SHOULDERS_WIDTH-desc =
    La distanza orizzontale dalla base del collo alle spalle.
    Imposta Lunghezza braccio superiore su 0 e modificala finché i tracker dei gomiti virtuali
    non si allineano orizzontalmente con le spalle reali.
skeleton_bone-arm_group = Lunghezza delle braccia
skeleton_bone-arm_group-desc =
    La distanza tra le spalle e i polsi.
    Regola correttamente la distanza delle spalle, imposta la distanza delle mani Y
    su 0 e modificala finché i tracker delle mani non si allineano ai polsi.
skeleton_bone-UPPER_ARM = Lunghezza braccio superiore
skeleton_bone-UPPER_ARM-desc =
    La distanza tra le spalle e i gomiti.
    Regola correttamente la Lunghezza delle braccia e modificala finché
    i tracker dei gomiti non si allineano con i gomiti reali.
skeleton_bone-LOWER_ARM = Lunghezza degli avambracci
skeleton_bone-LOWER_ARM-desc =
    La distanza tra i gomiti e i polsi.
    Regola correttamente la Lunghezza del braccio e modificala finché
    i tracker dei gomiti non si allineano con i gomiti reali.
skeleton_bone-HAND_Y = Distanza della mano sull'asse Y
skeleton_bone-HAND_Y-desc =
    La distanza verticale tra i polsi e il centro della mano.
    Per regolarla per il Motion Capture, regolare correttamente la lunghezza del braccio e modificarla finché i tracker delle mani di non si allineano verticalmente con il centro delle mani.
    Per regolarla per il tracciamento dei gomiti dai controller, impostare Lunghezza delle Braccia su 0 e
    modificarla finché i tracker dei gomiti non si allineano verticalmente con i polsi.
skeleton_bone-HAND_Z = Distanza della mano sull'asse Z
skeleton_bone-HAND_Z-desc =
    La distanza orizzontale tra i polsi e il centro della mano.
    Per regolarla per il Motion Capture, impostatela su 0.
    Per regolarla per il tracciamento dei gomiti dai controller, impostate Lunghezza delle braccia su 0 e
    modificarla finché i tracker dei gomiti non si allineano orizzontalmente con i polsi.
skeleton_bone-ELBOW_OFFSET = Correzione per il gomito
skeleton_bone-ELBOW_OFFSET-desc =
    Questo può essere regolato per spostare i tracker virtuali del gomito verso l'alto o verso il basso
    per evitare che VRChat a leghi accidentalmente un tracker del gomito al petto.

## Tracker reset buttons

reset-reset_all = Ripristina tutte le proporzioni
reset-reset_all_warning-v2 =
    <b>Attenzione:</b>Le proporzioni verranno ripristinate ai valori predefiniti in base all'altezza configurata.
    Sei sicurə di volerlo fare?
reset-reset_all_warning-reset = Ripristino delle proporzioni
reset-reset_all_warning-cancel = Annulla
reset-reset_all_warning_default-v2 =
    <b>Attenzione:</b>Non hai configurato la tua alteza, le proporzioni verranno ripristinate ai valori predefiniti in base all'altezza predefinita.
    Sei sicurə di volerlo fare?
reset-full = Ripristino completo
reset-mounting = Ripristino del posizionamento
reset-yaw = Ripristino dell'orientamento

## Serial detection stuff

serial_detection-new_device-p0 = Nuovo dispositivo seriale rilevato!
serial_detection-new_device-p1 = Inserisci le tue credenziali Wi-Fi!
serial_detection-new_device-p2 = Per favore, seleziona come utilizzare il tracker
serial_detection-open_wifi = Connetti al Wi-Fi
serial_detection-open_serial = Apri la Console Seriale
serial_detection-submit = Conferma!
serial_detection-close = Chiudi

## Navigation bar

navbar-home = Home
navbar-body_proportions = Proporzioni del corpo
navbar-trackers_assign = Assegnazione dei tracker
navbar-mounting = Calibrazione del posizionamento
navbar-onboarding = Installazione guidata
navbar-settings = Impostazioni

## Biovision hierarchy recording

bvh-start_recording = Registra BVH
bvh-recording = Registrazione in corso...

## Tracking pause

tracking-unpaused = Pausa il Tracciamento
tracking-paused = Riprendi il tracciamento

## Widget: Overlay settings

widget-overlay = Overlay
widget-overlay-is_visible_label = Mostra Overlay in SteamVR
widget-overlay-is_mirrored_label = Mostra Overlay come specchio

## Widget: Drift compensation

widget-drift_compensation-clear = Rimuovi compensazione per il drift

## Widget: Clear Mounting calibration

widget-clear_mounting = Cancella tutti i ripristini del posizionamento

## Widget: Developer settings

widget-developer_mode = Modalità sviluppatore
widget-developer_mode-high_contrast = Contrasto alto
widget-developer_mode-precise_rotation = Rotazione precisa
widget-developer_mode-fast_data_feed = Trasmissione veloce dei dati
widget-developer_mode-filter_slimes_and_hmd = Filtra gli Slime e il visore
widget-developer_mode-sort_by_name = Ordina per nome
widget-developer_mode-raw_slime_rotation = Rotazione non processata
widget-developer_mode-more_info = Ulteriori informazioni

## Widget: IMU Visualizer

widget-imu_visualizer = Dati di tracciamento
widget-imu_visualizer-preview = Anteprima
widget-imu_visualizer-hide = Nascondi
widget-imu_visualizer-rotation_raw = Non processato
widget-imu_visualizer-rotation_preview = Anteprima
widget-imu_visualizer-acceleration = Accelerazione
widget-imu_visualizer-position = Posizione
widget-imu_visualizer-stay_aligned = Rimani Allineato

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Anteprima dello scheletro
widget-skeleton_visualizer-hide = Nascondi

## Tracker status

tracker-status-none = Nessuno stato
tracker-status-busy = Occupato
tracker-status-error = Errore
tracker-status-disconnected = Disconnesso
tracker-status-occluded = Ostruito
tracker-status-ok = OK
tracker-status-timed_out = Tempo scaduto

## Tracker status columns

tracker-table-column-name = Nome
tracker-table-column-type = Tipologia
tracker-table-column-battery = Batteria
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temperatura °C
tracker-table-column-linear-acceleration = Accellerazione X/Y/Z
tracker-table-column-rotation = Rotazione X/Y/Z
tracker-table-column-position = Rotazione X/Y/Z
tracker-table-column-stay_aligned = Rimani Allineato
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Davanti
tracker-rotation-front_left = Anteriore sinistra
tracker-rotation-front_right = Anteriore destra
tracker-rotation-left = Sinistra
tracker-rotation-right = Destra
tracker-rotation-back = Dietro
tracker-rotation-back_left = Posteriore sinistra
tracker-rotation-back_right = Posteriore destra
tracker-rotation-custom = Personalizzata
tracker-rotation-overriden = (sovrascritto dal ripristino del posizionamento)

## Tracker information

tracker-infos-manufacturer = Produttore
tracker-infos-display_name = Nome da visualizzare
tracker-infos-custom_name = Nome personalizzato
tracker-infos-url = URL del tracker
tracker-infos-version = Versione firmware
tracker-infos-hardware_rev = Versione hardware
tracker-infos-hardware_identifier = Hardware ID
tracker-infos-data_support = Supporto dati
tracker-infos-imu = Sensore IMU
tracker-infos-board_type = Scheda principale
tracker-infos-network_version = Versione del protocollo
tracker-infos-magnetometer = Magnetometro
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Disattivato
        [ENABLED] Attivato
       *[NOT_SUPPORTED] Non supportato
    }

## Tracker settings

tracker-settings-back = Torna alla lista dei tracker
tracker-settings-title = Impostazioni del tracker
tracker-settings-assignment_section = Assegnazione
tracker-settings-assignment_section-description = Definisce a che parte del corpo è assegnato il tracker.
tracker-settings-assignment_section-edit = Modifica assegnazione
tracker-settings-mounting_section = Orientamento del posizionamento
tracker-settings-mounting_section-description = Dove è posizionato il tracker?
tracker-settings-mounting_section-edit = Modifica posizionamento
tracker-settings-drift_compensation_section = Consenti compensazione per il drift
tracker-settings-drift_compensation_section-description = Questo tracker dovrebbe compensare per il drift quando la compensazione per il drift è abilitata?
tracker-settings-drift_compensation_section-edit = Consenti compensazione per il drift
tracker-settings-use_mag = Consenti il magnetometro su questo tracker
# Multiline!
tracker-settings-use_mag-description =
    Vuoi consentire al tracker l'utilizzo del magnetometro per ridurre il drift quando l'uso del magnetometro è consentito? <b>Per favore non spegnere il tracker durante l'attivazione!</b>
    
    È necessario prima consentire l'utilizzo del magnetometro, <magSetting>fare clic qui per accedere alle impostazioni</magSetting>.
tracker-settings-use_mag-label = Consenti magnetometro
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nome del tracker
tracker-settings-name_section-description = Scegli un soprannome carino :)
tracker-settings-name_section-placeholder = Gamba destra di NightyQueer
tracker-settings-name_section-label = Nome del tracker
tracker-settings-forget = Dimentica il tracker
tracker-settings-forget-description = Rimuove il tracker dal SlimeVR server e impedisce che si riconnetta ad fino al riavvio del server. Le impostazioni del tracker non andranno perse.
tracker-settings-forget-label = Dimentica il tracker
tracker-settings-update-low-battery = Non è possibile aggiornare. Batteria inferiore al 50%
tracker-settings-update-up_to_date = Aggiornata
tracker-settings-update = Aggiorna
tracker-settings-update-title = Versione firmware

## Tracker part card info

tracker-part_card-no_name = Nessun nome
tracker-part_card-unassigned = Non assegnato

## Body assignment menu

body_assignment_menu = Dove vuoi posizionare il tracker?
body_assignment_menu-description = Scegli una parte del corpo a cui assegnare questo tracker. Alternativamente puoi scegliere di gestire tutti i tracker in una schermata unica invece che singolarmente.
body_assignment_menu-show_advanced_locations = Mostra impostazioni avanzate di assegnazione
body_assignment_menu-manage_trackers = Gestisci tutti i tracker
body_assignment_menu-unassign_tracker = Rimuovi assegnazione del tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Quale tracker vuoi assegnare al vostro
tracker_selection_menu-NONE = Quale tracker vuoi che non sia assegnato?
tracker_selection_menu-HEAD = { -tracker_selection-part } testa?
tracker_selection_menu-NECK = { -tracker_selection-part } collo?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } spalla destra?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } parte superiore del braccio destro?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } avambraccio destro?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } mano destra?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } coscia destra?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } caviglia destra?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } piede destro?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } controller destro?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } parte superiore del petto?
tracker_selection_menu-CHEST = { -tracker_selection-part } petto?
tracker_selection_menu-WAIST = { -tracker_selection-part } girovita?
tracker_selection_menu-HIP = { -tracker_selection-part } bacino?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } spalla sinistra?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } parte superiore del braccio sinistro?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } avambraccio sinistro?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } mano sinistra?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } coscia sinistra?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } caviglia sinistra?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } piede sinistro?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } controller sinistro?
tracker_selection_menu-unassigned = Tracker non assegnati
tracker_selection_menu-assigned = Tracker assegnati
tracker_selection_menu-dont_assign = Non assegnare
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Attenzione:</b> Un tracker per il collo può essere mortale se indossato troppo stretto,
    Il cinturino potrebbe bloccare la circolazione alla tua testa!
tracker_selection_menu-neck_warning-done = Comprendo i rischi
tracker_selection_menu-neck_warning-cancel = Annulla

## Mounting menu

mounting_selection_menu = Dove vuoi che sia posizionato questo tracker?
mounting_selection_menu-close = Chiudi

## Sidebar settings

settings-sidebar-title = Impostazioni
settings-sidebar-general = Generali
settings-sidebar-tracker_mechanics = Comportamento del tracker
settings-sidebar-stay_aligned = Rimani Allineato
settings-sidebar-fk_settings = Impostazioni del tracciamento
settings-sidebar-gesture_control = Controllo con gesti
settings-sidebar-interface = Interfaccia
settings-sidebar-osc_router = Router OSC
settings-sidebar-osc_trackers = Tracker OSC per VRChat
settings-sidebar-utils = Strumenti
settings-sidebar-serial = Console seriale
settings-sidebar-appearance = Aspetto
settings-sidebar-notifications = Notifiche
settings-sidebar-behavior = Comportamento
settings-sidebar-firmware-tool = Strumento firmware fai-da-te
settings-sidebar-vrc_warnings = Avvertimenti per le impostazioni di VRChat
settings-sidebar-advanced = Avanzate

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = Tracker di SteamVR
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Abilita o disabilita specifici tracker di SteamVR.
    Utile per alcuni giochi che utilizzano solo alcuni tracker di SteamVR.
settings-general-steamvr-trackers-waist = Girovita
settings-general-steamvr-trackers-chest = Petto
settings-general-steamvr-trackers-left_foot = Piede sinistro
settings-general-steamvr-trackers-right_foot = Piede destro
settings-general-steamvr-trackers-left_knee = Ginocchio sinistro
settings-general-steamvr-trackers-right_knee = Ginocchio destro
settings-general-steamvr-trackers-left_elbow = Gomito sinistro
settings-general-steamvr-trackers-right_elbow = Gomito destro
settings-general-steamvr-trackers-left_hand = Mano sinistra
settings-general-steamvr-trackers-right_hand = Mano destra
settings-general-steamvr-trackers-tracker_toggling = Assegnazione tracker automatica
settings-general-steamvr-trackers-tracker_toggling-description = Gestisce automaticamente l'attivazione o la disattivazione dei tracker SteamVR a seconda delle assegnazioni correnti dei tracker
settings-general-steamvr-trackers-tracker_toggling-label = Assegnazione tracker automatica
settings-general-steamvr-trackers-hands-warning =
    <b>Attenzione:</b> i tracker delle mani sostituiranno i controller.
    Sei sicurə?
settings-general-steamvr-trackers-hands-warning-cancel = Annulla
settings-general-steamvr-trackers-hands-warning-done = Sì

## Tracker mechanics

settings-general-tracker_mechanics = Comportamento del tracker
settings-general-tracker_mechanics-filtering = Filtro movimenti
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Scegli la tipologia di filtro per il tuo tracker.
    Predizione anticipa i movimenti, mentre Attenuazione attenua movimenti eccessivi.
settings-general-tracker_mechanics-filtering-type = Tipologia di filtro
settings-general-tracker_mechanics-filtering-type-none = Non usare alcun filtro
settings-general-tracker_mechanics-filtering-type-none-description = Usa le rotazioni così come sono. Non eseguirà alcun filtro.
settings-general-tracker_mechanics-filtering-type-smoothing = Attenuazione
settings-general-tracker_mechanics-filtering-type-smoothing-description = Attenua movimenti eccessivi ma aggiunge ritardo.
settings-general-tracker_mechanics-filtering-type-prediction = Predizione
settings-general-tracker_mechanics-filtering-type-prediction-description = Riduce ritardo e rende movimenti più istantanei, ma può introdurre tremolio.
settings-general-tracker_mechanics-filtering-amount = Quantità
settings-general-tracker_mechanics-yaw-reset-smooth-time = Tempo di attenuazione del ripristino dell'orientamento (0 secondi disabilita l'attenuazione)
settings-general-tracker_mechanics-drift_compensation = Compensazione per il drift
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description = Compensa il drift di orientamento degli IMU applicando una rotazione inversa. Modifica la forza della compensazione e il massimo numero di ripristini che sono presi in considerazione.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Compensazione per il drift
settings-general-tracker_mechanics-drift_compensation-prediction = Compensazione del drift predittiva
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    Predice la compensazione del drift oltre la quantità misurata in precedenza.
    Abilita questa opzione se i tuoi tracker perdono continuano l'orientamento.
settings-general-tracker_mechanics-drift_compensation-prediction-label = Compensazione del drift predittiva
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>Attenzione:</b> Utilizzare la compensazione del drift solo se è necessario il ripristino
    molto spesso (ogni ~5-10 minuti).
    
    Alcune IMU che sono soggetti a frequenti ripristini includono:
    Joy-Con, owoTrack e MPU (senza firmware recente).
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Annulla
settings-general-tracker_mechanics-drift_compensation_warning-done = Capisco
settings-general-tracker_mechanics-drift_compensation-amount-label = Grado di compensazione
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Utilizza fino a x ultimi ripristini
settings-general-tracker_mechanics-save_mounting_reset = Salva automaticamente la calibrazione del ripristino del posizionamento
settings-general-tracker_mechanics-save_mounting_reset-description =
    Salva in automatico la calibrazione del ripristino del posizionamento per i tracker tra un riavvio e l'altro. Utile
    quando si indossa una tuta in cui la posizione e orientamento dei tracker non cambia tra una sessione e l'altra. <b>Non consigliato per gli utenti normali!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Salva il ripristino del posizionamento
settings-general-tracker_mechanics-use_mag_on_all_trackers = Utilizza il magnetometro su tutti i tracker IMU che lo supportano
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Utilizza il magnetometro su tutti i tracker che dispongono di un firmware compatibile, riducendo il drift in ambienti magnetici stabili.
    Può essere disabilitato per ogni tracker nelle impostazioni del tracker. <b>Per favore non spegnere nessuno dei tracker durante l'attivazione!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Usa il magnetometro sui tracker
settings-stay_aligned = Rimani Allineato
settings-stay_aligned-description = Rimani Allineato riduce la deriva regolando gradualmente i tracker per allinearsi alle tue pose rilassate.
settings-stay_aligned-setup-label = Configura Rimani Allineato
settings-stay_aligned-setup-description = Devi completare "Configura Rimani Allineato" per attivare Rimani Allineato.
settings-stay_aligned-warnings-drift_compensation = ⚠ Si prega di disattivare la compensazione del drift! La compensazione del drift andrà in conflitto con l'opzione Rimani allineato.
settings-stay_aligned-enabled-label = Regola i tracker
settings-stay_aligned-hide_yaw_correction-label = Nascondi regolazione (per confrontare con l'opzione Rimani allineato)
settings-stay_aligned-general-label = Generale
settings-stay_aligned-relaxed_poses-label = Pose Rilassate
settings-stay_aligned-relaxed_poses-description = Rimani Allineato utilizza le tue pose rilassate per mantenere allineati i tracker. Usa "Configura Rimani Allineato" per aggiornare queste pose.
settings-stay_aligned-relaxed_poses-standing = Regola i tracker stando in piedi
settings-stay_aligned-relaxed_poses-sitting = Regola i tracker mentre sei seduto su una sedia
settings-stay_aligned-relaxed_poses-flat = Regola i tracker mentre sei seduto a terra o sdraiato sulla schiena
settings-stay_aligned-relaxed_poses-save_pose = Salva posa
settings-stay_aligned-relaxed_poses-reset_pose = Ripristina posa
settings-stay_aligned-relaxed_poses-close = Chiudi
settings-stay_aligned-debug-label = Debug
settings-stay_aligned-debug-description = Includi le tue impostazioni quando segnali problemi relativi a Rimani Allineato.
settings-stay_aligned-debug-copy-label = Copia le impostazioni negli appunti

## FK/Tracking settings

settings-general-fk_settings = Impostazioni del traciamento
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Compenetrazione pavimento
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Correzione slittamento
settings-general-fk_settings-leg_tweak-toe_snap = Dita dei piedi piantate
settings-general-fk_settings-leg_tweak-foot_plant = Piedi piantati
settings-general-fk_settings-leg_tweak-skating_correction-amount = Forza fattore di correzione slittamento
settings-general-fk_settings-leg_tweak-skating_correction-description = "Correzione slittamento" corregge l'effetto che fa sembrare che pattini sul ghiaccio, ma può peggiorare la precisione di certi movimenti. Quando abiliti questa funzione é necessario eseguire un ripristino completo e ri-calibrazione in gioco per il corretto funzionamento.
settings-general-fk_settings-leg_tweak-floor_clip-description = "Compenetrazione pavimento" può ridurre o anche eliminare completamente la compenetrazione con il pavimento. Quando abiliti questa funzione é necessario eseguire un ripristino completo e ri-calibrazione in gioco per il corretto funzionamento.
settings-general-fk_settings-leg_tweak-toe_snap-description = "Dita dei piedi piantate" prova ad indovinare la rotazione dei tuoi piedi quando non stai usando dei tracker per i piedi.
settings-general-fk_settings-leg_tweak-foot_plant-description = "Piedi piantati" ruota i piedi in modo tale che siano paralleli al terreno quando in contatto con esso.
settings-general-fk_settings-leg_fk = Tracciamento delle gambe
settings-general-fk_settings-enforce_joint_constraints = Limiti dello scheletro
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Rispetta i vincoli
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Impedisci ai legamenti di ruotare oltre il loro limite
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Correggi con i vincoli
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Correggi le rotazioni dei legamenti quando si spingono oltre il loro limite
settings-general-fk_settings-arm_fk = Tracciamento delle braccia
settings-general-fk_settings-arm_fk-description = Forza il calcolo della posizione delle braccia a utilizzare il visore anche se la posizione delle mani é disponibile.
settings-general-fk_settings-arm_fk-force_arms = Forza il calcolo delle braccia dal visore
settings-general-fk_settings-reset_settings = Ripristino delle impostazioni
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Ripristina l'inclinazione dell'HMD (rotazione verticale) dopo un reset completo. Utile se si indossa un HMD sulla fronte per VTubing o mocap. Non abilitare per VR.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Ripristina l'inclinazione dell'HMD
settings-general-fk_settings-arm_fk-reset_mode-description = Cambia la posa delle braccia usata per il ripristino del posizionamento.
settings-general-fk_settings-arm_fk-back = Indietro
settings-general-fk_settings-arm_fk-back-description = La modalità predefinita, con la parte superiori delle braccia che vanno indietro e le parte inferiori delle braccia che vanno avanti.
settings-general-fk_settings-arm_fk-tpose_up = Posa a T (in alto)
settings-general-fk_settings-arm_fk-tpose_up-description = Si aspetta che le braccia siano abbassate sui lati durante il Ripristino Completo e a 90 gradi con il busto ai lati per il Ripristino Posizionamento.
settings-general-fk_settings-arm_fk-tpose_down = Posa a T (in basso)
settings-general-fk_settings-arm_fk-tpose_down-description = Si aspetta che le braccia siano a 90 gradi con il busto ai lati durante il Ripristino Completo e abbassate sui lati per il Ripristino Posizionamento.
settings-general-fk_settings-arm_fk-forward = Avanti
settings-general-fk_settings-arm_fk-forward-description = Si aspetta che le tue braccia siano alzate di 90 gradi in avanti. Utile per VTubing.
settings-general-fk_settings-skeleton_settings-toggles = Interruttori per lo scheletro
settings-general-fk_settings-skeleton_settings-description = Abilita o disabilita le impostazioni dello scheletro. É consigliato lasciare queste impostazioni attive.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Modello di colonna vertebrale estesa
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = modello di bacino esteso
settings-general-fk_settings-skeleton_settings-extended_knees_model = Modello di ginocchio esteso
settings-general-fk_settings-skeleton_settings-ratios = Proporzioni dello scheletro
settings-general-fk_settings-skeleton_settings-ratios-description = Modifica i valori delle impostazioni dello scheletro. Potrebbe essere necessario regolare le proporzioni dopo aver modificato queste impostazioni.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Imputazione dei valori del girovita dalla combinazione del petto e bacino
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Imputazione dei valori del girovita dalla combinazione del petto e gambe
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Imputazione dei valori del bacino dalla combinazione del petto e gambe
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Imputazione dei valori del bacino dalla combinazione del girovita e gambe
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Media dell'orientamento del bacino e la rotazione delle gambe
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Media dell'orientamento del ginocchio e la rotazione delle caviglie
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Media dell'orientamento delle ginocchia e la rotazione delle caviglie
settings-general-fk_settings-self_localization-title = Modalità Mocap
settings-general-fk_settings-self_localization-description = La modalità Mocap consente allo scheletro di tracciare approssimativamente la propria posizione senza visore o altri tracker. Si noti che questo richiede trakers per piedi e la testa per funzionare ed è ancora in fase sperimentale.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Controllo con gesti
settings-general-gesture_control-subtitle = Ripristino toccando un tracker
settings-general-gesture_control-description = Abilita o disabilita il ripristino toccando un tracker. Il tracker più in alto sul torso viene utilizzato per il ripristino dell'orientamento, il tracker più in alto sulla gamba sinistra viene utilizzato per il ripristino completo e il tracker più in alto sulla gamba destra viene utilizzato per il ripristino del posizionamento. Si deve tener presente che i tocchi devono avvenire entro 0,6 secondi per essere registrati.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tocco
       *[other] { $amount } tocchi
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 tracker
        [many] { $amount } tracker
       *[other] { $amount } tracker
    }
settings-general-gesture_control-yawResetEnabled = Abilita tocco per il ripristino dell'orientamento
settings-general-gesture_control-yawResetDelay = Ritardo per il ripristino dell'orientamento
settings-general-gesture_control-yawResetTaps = Numero di tocchi per il ripristino dell'orientamento
settings-general-gesture_control-fullResetEnabled = Abilita tocco per il ripristino completo
settings-general-gesture_control-fullResetDelay = Ritardo per il ripristino completo
settings-general-gesture_control-fullResetTaps = Numero di tocchi per il ripristino completo
settings-general-gesture_control-mountingResetEnabled = Abilita tocco per ripristino del posizionamento
settings-general-gesture_control-mountingResetDelay = Ritardo per il ripristino del posizionamento
settings-general-gesture_control-mountingResetTaps = Numero di tocchi per il ripristino del posizionamento
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Tracker sopra il limite
settings-general-gesture_control-numberTrackersOverThreshold-description = Aumenta questo valore se il rilevamento del tocco non funziona. Non aumentarlo al di sopra di quanto necessario per far funzionare il rilevamento del tocco in quanto causerebbe più falsi positivi.

## Appearance settings

settings-interface-appearance = Aspetto
settings-general-interface-dev_mode = Modalità sviluppatore
settings-general-interface-dev_mode-description = Questa modalità è utile se hai bisogno di dati approfonditi o devi interagire in maniera più avanzata con i tracker connessi.
settings-general-interface-dev_mode-label = Modalità sviluppatore
settings-general-interface-theme = Colore di tema
settings-general-interface-show-navbar-onboarding = Mostra "{ navbar-onboarding }" sulla barra di navigazione
settings-general-interface-show-navbar-onboarding-description = Cambia se il pulsante "{ navbar-onboarding }" viene visualizzato nella barra di navigazione.
settings-general-interface-show-navbar-onboarding-label = Mostra "{ navbar-onboarding }"
settings-general-interface-lang = Seleziona la lingua
settings-general-interface-lang-description = Seleziona la lingua che vuoi utilizzare
settings-general-interface-lang-placeholder = Seleziona la lingua da utilizzare
# Keep the font name untranslated
settings-interface-appearance-font = Font della GUI
settings-interface-appearance-font-description = Questo cambia il font utilizzato dall'interfaccia.
settings-interface-appearance-font-placeholder = Font predefinito
settings-interface-appearance-font-os_font = Font di sistema
settings-interface-appearance-font-slime_font = Font predefinito
settings-interface-appearance-font_size = Ridimensionamento del font di base
settings-interface-appearance-font_size-description = Questo influisce sulla dimensione del font dell'intera interfaccia ad eccezione di questo pannello delle impostazioni.
settings-interface-appearance-decorations = Usa le decorazioni native del sistema
settings-interface-appearance-decorations-description = L'applicazione non eseguirà il rendering della barra superiore dell'interfaccia e utilizzerà invece quella del sistema operativo.
settings-interface-appearance-decorations-label = Usa decorazioni native

## Notification settings

settings-interface-notifications = Notifiche
settings-general-interface-serial_detection = Rilevazione nuovi dispositivi seriali
settings-general-interface-serial_detection-description = Questa opzione mostrerà un pop-up ogni volta che colleghi un nuovo dispositivo seriale che potrebbe essere un tracker. Aiuta a facilitare la configurazione iniziale di un tracker
settings-general-interface-serial_detection-label = Rilevazione nuovi dispositivi seriali
settings-general-interface-feedback_sound = Suono di feedback
settings-general-interface-feedback_sound-description = Questa opzione riprodurrà un suono quando viene effettuato un ripristino
settings-general-interface-feedback_sound-label = Suono di feedback
settings-general-interface-feedback_sound-volume = Volume del suono di feedback
settings-general-interface-connected_trackers_warning = Avviso di tracker connessi
settings-general-interface-connected_trackers_warning-description = Questa opzione mostrerà un pop-up ogni volta che proverai ad uscire da SmileVR mentre uno o più tracker sono connessi. Ció ti permetterà di ricordarti di spegnere i tuoi tracker per preservarne la durata delle batterie.
settings-general-interface-connected_trackers_warning-label = Avviso di tracker connessi alla chiusura dell'applicazione

## Behavior settings

settings-interface-behavior = Comportamento
settings-general-interface-use_tray = Riduci a icona nella barra delle applicazioni
settings-general-interface-use_tray-description = Ti consente di chiudere la finestra senza chiudere il server SlimeVR in modo da poter continuare a usarlo senza che la GUI ti infastidisca.
settings-general-interface-use_tray-label = Riduci a icona nella barra delle applicazioni
settings-general-interface-discord_presence = Condividi attività su Discord
settings-general-interface-discord_presence-description = Dice al tuo client di Discord che stai utilizzando SlimeVR insieme al numero di tracker IMU che stai utilizzando.
settings-general-interface-discord_presence-label = Condividi attività su Discord
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Slimeggiando qua e là
        [one] Sta usando 1 tracker
       *[other] Sta usando { $amount } tracker
    }
settings-interface-behavior-error_tracking = Raccolta degli errori tramite Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Acconsenti alla raccolta di dati di errore anonimizzati?</h1>
    
    <b>Non raccogliamo informazioni personali</b> come l'indirizzo IP o le credenziali wireless. SlimeVR ha a cuore la tua privacy!
    
    Per offrire la migliore esperienza utente, raccogliamo segnalazioni di errori anonime, metriche delle prestazioni e informazioni sul sistema operativo. Questo ci aiuta a rilevare bug e problemi con SlimeVR. Queste metriche vengono raccolte tramite Sentry.io.
settings-interface-behavior-error_tracking-label = Invia errori agli sviluppatori

## Serial settings

settings-serial = Console Seriale
# This cares about multilines
settings-serial-description =
    Questo è un feed di informazioni in tempo reale per la comunicazione seriale.
    Può essere utile se ti serve capire se il firmware sta avendo problemi.
settings-serial-connection_lost = Connessione seriale persa. Riconnessione in corso...
settings-serial-reboot = Riavvia
settings-serial-factory_reset = Ripristino delle impostazioni di fabbrica
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Attenzione:</b> Questa azione ripristinerà il tracker alle impostazioni di fabbrica.
    Ciò significa che le impostazioni Wi-Fi e di calibrazione <b>andranno tutte perse!</b>
settings-serial-factory_reset-warning-ok = Capisco cosa sto facendo
settings-serial-factory_reset-warning-cancel = Annulla
settings-serial-serial_select = Seleziona una porta seriale
settings-serial-auto_dropdown_item = Automatico
settings-serial-get_wifi_scan = Elenca WiFi Network
settings-serial-file_type = Testo normale
settings-serial-save_logs = Salva su file

## OSC router settings

settings-osc-router = Router OSC
# This cares about multilines
settings-osc-router-description =
    Inoltra messaggi OSC da un altro programma.
    Utile per utilizzare un altro programma OSC con VRChat, per esempio.
settings-osc-router-enable = Attiva
settings-osc-router-enable-description = Attiva o disattiva l'inoltro dei messaggi
settings-osc-router-enable-label = Attiva
settings-osc-router-network = Porte di rete
# This cares about multilines
settings-osc-router-network-description =
    Impostare le porte di rete per l'ascolto e l'invio dei dati
    Queste possono essere le stesse porte di rete di altre porte utilizzate nel server di SlimeVR
settings-osc-router-network-port_in =
    .label = Porta in ingresso
    .placeholder = Porta in ingresso (predefinito: 9002)
settings-osc-router-network-port_out =
    .label = Porta in uscita
    .placeholder = Porta in uscita (predefinito: 9000)
settings-osc-router-network-address = Indirizzo di rete
settings-osc-router-network-address-description = Impostare l'indirizzo di rete a cui inviare i dati
settings-osc-router-network-address-placeholder = Indirizzo IPV4

## OSC VRChat settings

settings-osc-vrchat = Tracker OSC per VRChat
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Modifica impostazioni specifiche dello standard dei Tracker OSC utilizzato per l'invio
    di dati di tracciamento alle applicazioni senza l'utlizzo di SteamVR (es. Quest standalone).
    Assicurarsi di abilitare l'OSC in VRChat tramite il menu Azione alla voce OSC > Abilitato.
    Per consentire la ricezione dei dati dell'HMD e del controller da VRChat, vai nella sezione impostazioni del menu principale
    in Tracciamento e IK > Consenti invio dati OSC tracciamento VR testa e polso.
settings-osc-vrchat-enable = Attiva
settings-osc-vrchat-enable-description = Attiva o disattiva l'invio e la ricezione dei dati
settings-osc-vrchat-enable-label = Attiva
settings-osc-vrchat-oscqueryEnabled = Abilita OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery rileva automaticamente le istanze in esecuzione di VRChat e invia loro dati.
    Può anche rendersi visibile ad essi per ricevere i dati del HMD e dei controller.
    Per consentire la ricezione dei dati del HMD e controller da VRChat, vai nelle impostazioni dal menu principale
    in "Tracking e IK" e abilita "Consenti l'Invio di Dati di Tracciamento OSC di Testa e Polso".
settings-osc-vrchat-oscqueryEnabled-label = Abilita OSCQuery
settings-osc-vrchat-network = Porte di rete
settings-osc-vrchat-network-description-v1 = Imposta le porte per l'ascolto e l'invio dei dati. Può essere lasciato come predefinito per utilizzo con VRChat.
settings-osc-vrchat-network-port_in =
    .label = Porta in ingresso
    .placeholder = Porta in ingresso (predefinito: 9002)
settings-osc-vrchat-network-port_out =
    .label = Porta in uscita
    .placeholder = Porta in uscita (predefinito: 9000)
settings-osc-vrchat-network-address = Indirizzo di rete
settings-osc-vrchat-network-address-description-v1 = Scegli a quale indirizzo inviare i dati. Può essere lasciato come predefinito per utilizzo con VRChat.
settings-osc-vrchat-network-address-placeholder = Indirizzo IP di VRChat
settings-osc-vrchat-network-trackers = Tracker
settings-osc-vrchat-network-trackers-description = Attiva o disattiva l'invio e la ricezione dei dati
settings-osc-vrchat-network-trackers-chest = Petto
settings-osc-vrchat-network-trackers-hip = Bacino
settings-osc-vrchat-network-trackers-knees = Ginocchia
settings-osc-vrchat-network-trackers-feet = Piedi
settings-osc-vrchat-network-trackers-elbows = Gomiti

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Cambia impostazioni legate al protocollo VMC (Virtual Motion Capture)
     per inviare dati dello scheletro di SlimeVR e ricevere dati dello scheletro da altre applicazioni.
settings-osc-vmc-enable = Attiva
settings-osc-vmc-enable-description = Attiva o disattiva l'invio e la ricezione dei dati
settings-osc-vmc-enable-label = Attiva
settings-osc-vmc-network = Porte di rete
settings-osc-vmc-network-description = Impostare le porte di rete per ascoltare e inviare dati a VMC
settings-osc-vmc-network-port_in =
    .label = Porta in ingresso
    .placeholder = Porta in ingresso (predefinita: 39540)
settings-osc-vmc-network-port_out =
    .label = Porta in uscita
    .placeholder = Porta in uscita (predefinita: 39539)
settings-osc-vmc-network-address = Indirizzo di rete
settings-osc-vmc-network-address-description = Scegli l'indirizzo di rete a cui inviare i dati
settings-osc-vmc-network-address-placeholder = Indirizzo IPV4
settings-osc-vmc-vrm = Modello VRM
settings-osc-vmc-vrm-description = Carica un modello VRM per consentite l'ancoraggio della testa e migliorare la compatibilitá con altre applicazioni
settings-osc-vmc-vrm-untitled_model = Modello senza titolo
settings-osc-vmc-vrm-file_select = Trascina qui un modello da usare, o <u>sfoglia</u>
settings-osc-vmc-anchor_hip = Ancoraggio sul bacino
settings-osc-vmc-anchor_hip-description = Ancora la posizione del tracciamento del bacino; utile per VTubing da seduti. Se disabilitato, carica un modello VRM.
settings-osc-vmc-anchor_hip-label = Ancoraggio sul bacino
settings-osc-vmc-mirror_tracking = Tracciamento speculare
settings-osc-vmc-mirror_tracking-description = Specchia il tracciamento orizzontalmente.
settings-osc-vmc-mirror_tracking-label = Tracciamento speculare

## Common OSC settings


## Advanced settings

settings-utils-advanced = Avanzato
settings-utils-advanced-reset-gui = Ripristina impostazioni della GUI
settings-utils-advanced-reset-gui-description = Ripristina le impostazioni predefinite per l'interfaccia.
settings-utils-advanced-reset-gui-label = Ripristina GUI
settings-utils-advanced-reset-server = Reimpostare le impostazioni di tracciamento
settings-utils-advanced-reset-server-description = Ripristina le impostazioni predefinite per il tracciamento.
settings-utils-advanced-reset-server-label = Ripristina il tracciamento
settings-utils-advanced-reset-all = Ripristina tutte le impostazioni
settings-utils-advanced-reset-all-description = Ripristina le impostazioni predefinite sia per l'interfaccia che per il tracciamento.
settings-utils-advanced-reset-all-label = Ripristina tutto
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>Attenzione:</b> Tutte le impostazioni della GUI verranno ripristinate ai valori predefiniti.
            Sei sicuro di volerlo fare?
        [server]
            <b>Attenzione:</b> Tutte le impostazioni di tracciento verranno ripristinate ai valori predefiniti.
            Sei sicuro di volerlo fare?
       *[all]
            <b>Attenzione:</b> Tutte le impostazioni verranno ripristinate ai valori predefiniti.
            Sei sicuro di volerlo fare?
    }
settings-utils-advanced-reset_warning-reset = Ripristina impostazioni
settings-utils-advanced-reset_warning-cancel = Annulla
settings-utils-advanced-open_data-v1 = Cartella di configurazione
settings-utils-advanced-open_data-description-v1 = Apri la cartella di configurazione di SlimeVR in Esplora Risorse, contenente la configurazione
settings-utils-advanced-open_data-label = Apri cartella
settings-utils-advanced-open_logs = Cartella dei Log
settings-utils-advanced-open_logs-description = Apri la cartella dei log di SlimeVR in Esplora Risorse, contenente i log dell'app
settings-utils-advanced-open_logs-label = Apri cartella

## Home Screen


## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = Salta la configurazione
onboarding-continue = Continua
onboarding-wip = Lavori in corso
onboarding-previous_step = Passaggio precedente
onboarding-setup_warning =
    <b>Attenzione:</b> La configurazione è necessaria per un buon tracciamento,
    è richiesta se è la prima volta che usi SlimeVR.
onboarding-setup_warning-skip = Salta la configurazione
onboarding-setup_warning-cancel = Continua la configurazione

## Wi-Fi setup

onboarding-wifi_creds-back = Torna all'introduzione
onboarding-wifi_creds-skip = Salta impostazioni Wi-Fi
onboarding-wifi_creds-submit = Conferma!
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = Enter SSID
onboarding-wifi_creds-ssid-required = È richiesto il nome Wi-Fi
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Inserisci password

## Mounting setup

onboarding-reset_tutorial-back = Torna alla calibrazione posizionamento
onboarding-reset_tutorial = Tutorial di ripristino
onboarding-reset_tutorial-explanation = Mentre usi i tuoi tracker, potrebbero perdere l'allineamento a causa del drift dell'orientamento dell'IMU, o perché potresti averli spostati fisicamente. Hai diversi modi per risolvere questo problema.
onboarding-reset_tutorial-skip = Salta passaggio
# Cares about multiline
onboarding-reset_tutorial-0 =
    Tocca { $taps } volte il tracker evidenziato per eseguire il ripristino dell'orientamento.
    
    Ciò farà sì che i tracker siano rivolti nella stessa direzione del tuo HMD.
# Cares about multiline
onboarding-reset_tutorial-1 =
    Tocca { $taps } volte il tracker evidenziato per eseguire il ripristino completo.
    
    Devi stare in piedi drittə per questo (I-pose). C'è un ritardo di 3 secondi (configurabile) prima che accada effettivamente.
    Questo ripristinerà  completamente la posizione e la rotazione di tutti i tuoi tracker. Dovrebbe risolvere la maggior parte dei problemi.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Tocca { $taps } volte il tracker evidenziato per eseguire il ripristino del posizionamento.
    
    Il ripristino del posizionamento aiuta a determinare come i tracker vengono effettivamente posizionati su di te, quindi se li hai spostati accidentalmente e hai cambiato il modo in cui sono orientati di una grande quantità, questo aiuterà.
    
    Devi essere in posa come se stessi sciando come mostrato nella procedura guidata di posizionamento automatico e ha un ritardo di 3 secondi (configurabile) prima che venga attivato.

## Setup start

onboarding-home = Benvenuti a SlimeVR
onboarding-home-start = Prepariamoci!

## Setup done

onboarding-done-title = È tutto pronto!
onboarding-done-description = Goditi la tua esperienza di full-body tracking
onboarding-done-close = Chiudi la configurazione

## Tracker connection setup

onboarding-connect_tracker-back = Torna alle credenziali Wi-Fi
onboarding-connect_tracker-title = Connetti i tracker
onboarding-connect_tracker-description-p0-v1 = Ora passiamo alla parte divertente, colleghiamo i tracker!
onboarding-connect_tracker-description-p1-v1 = Collega ogni tracker uno alla volta tramite una porta USB.
onboarding-connect_tracker-issue-serial = Sto avendo problemi nel connettermi!
onboarding-connect_tracker-usb = Tracker USB
onboarding-connect_tracker-connection_status-none = Ricerca dei tracker in corso
onboarding-connect_tracker-connection_status-serial_init = Connessione al dispositivo seriale in corso
onboarding-connect_tracker-connection_status-obtaining_mac_address = Aquisizione in corso dell'indirizzo MAC del tracker
onboarding-connect_tracker-connection_status-provisioning = Invio credenziali Wi-Fi in corso
onboarding-connect_tracker-connection_status-connecting = Tentativo di connessione al Wi-Fi in corso
onboarding-connect_tracker-connection_status-looking_for_server = Ricerca del server in corso
onboarding-connect_tracker-connection_status-connection_error = Impossibile connettersi al Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Impossibile trovare il server
onboarding-connect_tracker-connection_status-done = Connesso al server
onboarding-connect_tracker-connection_status-no_serial_log = Impossibile ottenere log dal tracker
onboarding-connect_tracker-connection_status-no_serial_device_found = Impossibile trovare un tracker tramite USB
onboarding-connect_serial-error-modal-no_serial_log = Il tracker è acceso?
onboarding-connect_serial-error-modal-no_serial_log-desc = Assicurati che il tracker sia acceso e connesso al computer
onboarding-connect_serial-error-modal-no_serial_device_found = Nessun tracker rilevato
onboarding-connect_serial-error-modal-no_serial_device_found-desc =
    Con il cavo USB in dotazione collega un tracker al computer e accendilo.
    Se questo non funziona:
      - Prova con un altro cavo USB
      - Prova con un'altra porta USB
      - Prova a reinstallare il server SlimeVR e seleziona "Driver USB" nella sezione componenti
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Nessun Tracker connesso
        [one] 1 tracker connesso
       *[other] { $amount } tracker connessi
    }
onboarding-connect_tracker-next = Ho collegato tutti i miei tracker

## Tracker calibration tutorial

onboarding-calibration_tutorial = Tutorial di calibrazione IMU
onboarding-calibration_tutorial-subtitle = Ciò aiuterà a ridurre il drift del tracker!
onboarding-calibration_tutorial-calibrate = Ho messo i miei tracker sul tavolo
onboarding-calibration_tutorial-status-waiting = Ti aspettiamo
onboarding-calibration_tutorial-status-calibrating = Calibrazione in corso
onboarding-calibration_tutorial-status-success = Bene!
onboarding-calibration_tutorial-status-error = Il tracker è stato mosso
onboarding-calibration_tutorial-skip = Salta il tutorial

## Tracker assignment tutorial

onboarding-assignment_tutorial = Come preparare uno Slime Tracker prima di indossarlo
onboarding-assignment_tutorial-first_step = 1. Posiziona un adesivo di una parte del corpo sul tracker in base alla tua scelta (se ne hai uno)
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Adesivo
onboarding-assignment_tutorial-second_step-v2 = 2. Fissare il cinturino al tracker, mantenendo il lato in velcro del cinturino rivolto nella stessa direzione del lato frontale del tuo slime tracker:
onboarding-assignment_tutorial-second_step-continuation-v2 = Il lato del velcro per l'estensione dovrebbe essere rivolto verso l'alto come nell'immagine seguente:
onboarding-assignment_tutorial-done = Ho messo gli adesivi e i cinturini!

## Tracker assignment setup

onboarding-assign_trackers-back = Torna alle credenziali Wi-Fi
onboarding-assign_trackers-title = Assegna i tracker
onboarding-assign_trackers-description = Scegliamo quale tracker va dove. Fare clic su una parte del corpo in cui si desidera assegnare un tracker
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } di 1 tracker assegnato
       *[other] { $assigned } di { $trackers } tracker assegnati
    }
onboarding-assign_trackers-advanced = Mostra impostazioni avanzate di assegnazione
onboarding-assign_trackers-next = Ho assegnato tutti i miei tracker
onboarding-assign_trackers-mirror_view = Inverti interfaccia
onboarding-assign_trackers-option-amount =
    { $trackersCount ->
        [one] x{ $trackersCount }
       *[other] x{ $trackersCount }
    }
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] Pacchetto "Lower-Body"
        [core] Pacchetto "Core"
        [enhanced-core] Pacchetto "Enhanced Core"
        [full-body] Pacchetto "Full-Body"
       *[all] Tutti i tracker
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] Il minimo per full-body tracking in VR
        [core] + Miglior tracciamento della spina dorsale
        [enhanced-core] + Rotazione dei piedi
        [full-body] + Tracciamento dei gomiti
       *[all] Tutte le assegnazioni di tracker disponibili
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] Il piede sinistro è assegnato, ma anche la coscia sinistra e la caviglia sinistra e almeno uno tra il petto o il bacino o il girovita devono avere un tracker assegnato!
        [1] Il piede sinistro è assegnato, ma anche la coscia sinistra e almeno uno tra il petto o il bacino o il girovita devono avere un tracker assegnato!
        [2] Il piede sinistro è assegnato, ma anche la caviglia sinistra e almeno uno tra il petto o il bacino o il girovita devono avere un tracker assegnato!
        [3] Il piede sinistro è assegnato, ma anche il petto o il bacino o il girovita devono avere un tracker assegnato!
        [4] Il piede sinistro è assegnato, ma anche la coscia sinistra e la caviglia destra devono avere un tracker assegnato!
        [5] Il piede sinistro è assegnato, ma anche la coscia sinistra deve avere un tracker assegnato!
        [6] Il piede sinistro è assegnato, ma anche la caviglia sinistra deve avere un tracker assegnato!
       *[unknown] Il piede sinistro è assegnato, ma una parte del corpo sconosciuta non ha un tracker assegnato!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] Il piede destro è assegnato, ma anche la coscia destra e la caviglia destra e almeno uno tra il petto o il bacino o il girovita devono avere un tracker assegnato!
        [1] Il piede destro è assegnato, ma anche la coscia destra e almeno uno tra il petto o il bacino o il girovita devono avere un tracker assegnato!
        [2] Il piede destro è assegnato, ma anche la caviglia destra e almeno uno tra il petto o il bacino o il girovita devono avere un tracker assegnato!
        [3] Il piede destro è assegnato, ma anche il petto o il bacino o il girovita devono avere un tracker assegnato!
        [4] Il piede destro è assegnato, ma anche la coscia destra e la caviglia destra devono avere un tracker assegnato!
        [5] Il piede destro è assegnato, ma anche la coscia destra deve avere un tracker assegnato!
        [6] Il piede destro è assegnato, ma anche la caviglia destra deve avere un tracker assegnato!
       *[unknown] Il piede destro è assegnato, ma una parte del corpo sconosciuta non ha un tracker assegnato!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] La caviglia sinistra è assegnata, ma anche la coscia sinistra e almeno uno tra il petto o il bacino o il girovita devono avere un tracker assegnato!
        [1] La caviglia sinistra è assegnata, ma anche il petto o il bacino o il girovita devono avere un tracker assegnato!
        [2] La caviglia sinistra è assegnata, ma anche la coscia sinistra deve avere un tracker assegnato!
       *[unknown] La caviglia sinistra è assegnata, ma una parte del corpo sconosciuta non ha un tracker assegnato!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] La caviglia destra è assegnata, ma anche la coscia destra e almeno uno tra il petto o il bacino o il girovita devono avere un tracker assegnato!
        [1] La caviglia destra è assegnata, ma anche il petto o il bacino o il girovita devono avere un tracker assegnato!
        [2] La caviglia destra è assegnata, ma anche la coscia destra deve avere un tracker assegnato!
       *[unknown] La caviglia destra è assegnata, ma una parte del corpo sconosciuta non ha un tracker assegnato!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] La coscia sinistra è assegnata, ma anche il petto o il bacino o il girovita devono avere un tracker assegnato!
       *[unknown] La coscia sinistra è assegnata, ma una parte del corpo sconosciuta non ha un tracker assegnato!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] La coscia destra è assegnata, ma anche il petto o il bacino o il girovita  devono avere un tracker assegnato!
       *[unknown] La coscia destra è assegnata, ma una parte del corpo sconosciuta non ha un tracker assegnato!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] Il bacino è assegnato, ma anche il petto deve avere un tracker assegnato!
       *[unknown] Il bacino è assegnato, ma una parte del corpo sconosciuta non ha un tracker assegnato!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] Il girovita è assegnato, ma anche il petto deve avere un tracker assegnato!
       *[unknown] Il girovita è assegnato, ma una parte del corpo sconosciuta non ha un tracker assegnato!
    }

## Tracker mounting method choose

onboarding-choose_mounting = Quale metodo di calibrazione del posizionamento vuoi usare?
# Multiline text
onboarding-choose_mounting-description = L'orientamento di posizionamento corregge la posizione dei tracker sul corpo.
onboarding-choose_mounting-auto_mounting = Posizionamento automatico
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Raccomandato
onboarding-choose_mounting-auto_mounting-description = Questo processo identificherá automaticamente le direzioni per la posizione di montaggio di tutti i traker facendo 2 pose
onboarding-choose_mounting-manual_mounting = Posizionamento manuale
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Potrebbe non essere abbastanza preciso
onboarding-choose_mounting-manual_mounting-description = Questo processo ti lascerá scegliere manualmente le direzioni per la posizione di montaggio di tutti i tracker
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Sei sicuro di voler fare
    la calibrazione automatica di posizionamento?
onboarding-choose_mounting-manual_modal-description = <b>La calibrazione manuale è raccomandata per i nuovi utenti</b>, considerando che le pose necessarie per la calibrazione automatica di posizionamento possono risultare complicate al primo tentativo e potrebbero richiedere un po' di pratica.
onboarding-choose_mounting-manual_modal-confirm = Sono sicurə di ciò che sto facendo.
onboarding-choose_mounting-manual_modal-cancel = Annulla

## Tracker manual mounting setup

onboarding-manual_mounting-back = Torna indietro per entrare in VR
onboarding-manual_mounting = Posizionamento manuale
onboarding-manual_mounting-description = Fare clic su ogni tracker e selezionare in che direzione sono montati
onboarding-manual_mounting-auto_mounting = Posizionamento automatico
onboarding-manual_mounting-next = Passaggio successivo

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Torna indietro per entrare in VR
onboarding-automatic_mounting-title = Calibrazione posizionamento
onboarding-automatic_mounting-description = Affinché i tracker di SlimeVR funzionino, dobbiamo configurare una rotazione di posizione di montaggio ai tuoi tracker per allinearli con la posizione di montaggio del tuo tracker fisico.
onboarding-automatic_mounting-manual_mounting = Posizionamento manuale
onboarding-automatic_mounting-next = Passaggio successivo
onboarding-automatic_mounting-prev_step = Passaggio precedente
onboarding-automatic_mounting-done-title = Rotazione delle posizioni di montaggio calibrate.
onboarding-automatic_mounting-done-description = La calibrazione della posizione é completa!
onboarding-automatic_mounting-done-restart = Torna all'inizio
onboarding-automatic_mounting-mounting_reset-title = Ripristino del posizionamento
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Piegati come per sciare: con le gambe leggermente piegate e unite, la parte superiore del corpo inclinata in avanti e le braccia piegate.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Premere il pulsante "Ripristino del posizionamento" e attendere 3 secondi prima che le rotazioni delle posizioni di montaggio dei tracker vengano ripristinate.
onboarding-automatic_mounting-preparation-title = Preparazione
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Premi il pulsante "Ripristino completo".
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Stai in piedi con le braccia lungo i fianchi. Assicurati di guardare in avanti.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Mantieni la posizione fino allo scadere dei 3 secondi.
onboarding-automatic_mounting-put_trackers_on-title = Indossa i tuoi tracker
onboarding-automatic_mounting-put_trackers_on-description = Per calibrare le rotazioni delle posizioni di montaggio useremo i tracker che hai appena assegnato. Indossa tutti i tuoi tracker, puoi vedere quali sono quali nella figura a destra.
onboarding-automatic_mounting-put_trackers_on-next = Sto indossando tutti i miei tracker

## Tracker manual proportions setupa

onboarding-manual_proportions-title = Impostazione manuale delle proporzioni del corpo
onboarding-manual_proportions-fine_tuning_button = Regola automaticamente le proporzioni
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Per piacer collega un visore VR per utilizzare la regolazione automatica
onboarding-manual_proportions-export = Esporta le proporzioni del corpo
onboarding-manual_proportions-import = Importa le proporzioni del corpo
onboarding-manual_proportions-file_type = File delle proporzioni del corpo
onboarding-manual_proportions-normal_increment = Incremento normale
onboarding-manual_proportions-precise_increment = Incremento preciso
onboarding-manual_proportions-grouped_proportions = Proporzioni raggruppate
onboarding-manual_proportions-all_proportions = Tutte le proporzioni
onboarding-manual_proportions-estimated_height = Altezza utente stimata

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Torna al tutorial di ripristino
onboarding-automatic_proportions-title = Misura il tuo corpo
onboarding-automatic_proportions-description = Affinché i tracker di SlimeVR funzionino dobbiamo conoscere la lunghezza dei tuoi arti. Questa breve calibrazione le misurerà per te.
onboarding-automatic_proportions-manual = Proporzioni manuali
onboarding-automatic_proportions-prev_step = Passaggio precedente
onboarding-automatic_proportions-put_trackers_on-title = Indossa i tuoi tracker
onboarding-automatic_proportions-put_trackers_on-description = Per calibrare le tue proporzioni useremo i tracker che hai appena assegnato. Indossa tutti i tuoi tracker, puoi vedere quali sono quali nella figura a destra.
onboarding-automatic_proportions-put_trackers_on-next = Sto indossando tutti i miei tracker
onboarding-automatic_proportions-requirements-title = Requisiti
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    Hai almeno abbastanza tracker per tracciare i tuoi piedi (generalmente 5 tracker).
    Hai i tuoi tracker e il visore accessi e li stai indossando.
    I tuoi tracker e il visore sono connessi al server di SlimeVR e stanno funzionando correttamente (e.g. non si bloccano, disconnettono, etc.)
    Il tuo visore sta riportando dati di posizione al server di SlimeVR (ciò significa generalmente avere SteamVR in esecuzione e connesso a SlimeVR usando il driver di SlimeVR per SteamVR).
    Il tuo visore sta riportando dati di posizione al server di SlimeVR (ciò significa generalmente avere SteamVR in esecuzione e connesso a SlimeVR usando il driver di SlimeVR per SteamVR).
    Il tuo tracciamento funziona e rappresenta accuratamente i tuoi movimenti (e.g. hai eseguito un ripristino completo e i traker si muovono nella giusta direzione quando calci, ti pieghi, ti siedi, ecc.).
onboarding-automatic_proportions-requirements-next = Ho letto i requisiti.
onboarding-automatic_proportions-check_height-title-v3 = Misura l'altezza del visore
onboarding-automatic_proportions-check_height-description-v2 = L'altezza del visore (HMD) dovrebbe essere leggermente inferiore all'altezza completa, poiché il visore misura l'altezza degli occhi. Questa misurazione verrà utilizzata come base per le proporzioni del tuo corpo.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = Inizia a misurare stando in piedi <u>dritti</u> per misurare la tua altezza. Fai attenzione a non alzare le mani più in alto del visore, poiché potrebbero influire sulla misurazione!
onboarding-automatic_proportions-check_height-guardian_tip =
    Se stai utilizzando un visore VR standalone, assicurati di avere il tuo guardiano/
    limiti attivato in modo che la tua altezza sia corretta!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Sconosciuto
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = L'altezza del visore è:
onboarding-automatic_proportions-check_height-measure-start = Inizia la misurazione
onboarding-automatic_proportions-check_height-measure-stop = Interrompi la misurazione
onboarding-automatic_proportions-check_height-measure-reset = Riprova la misurazione
onboarding-automatic_proportions-check_height-next_step = Sono corretti
onboarding-automatic_proportions-check_floor_height-title = Misura l'altezza del pavimento (opzionale)
onboarding-automatic_proportions-check_floor_height-description = In alcuni casi, l'altezza del pavimento potrebbe non essere impostata correttamente dal visore, causando la misurazione dell'altezza del visore a risultare superiore a quella che dovrebbe essere. Puoi misurare l'"altezza" del pavimento per correggere l'altezza del visore.
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = Inizia a misurare e posiziona un controller contro il pavimento per misurarne l'altezza. Se sei sicurə che l'altezza del pavimento sia corretta, puoi saltare questo passaggio.
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = L'altezza del pavimento è:
onboarding-automatic_proportions-check_floor_height-full_height = La tua altezza totale stimata è:
onboarding-automatic_proportions-check_floor_height-measure-start = Inizia la misurazione
onboarding-automatic_proportions-check_floor_height-measure-stop = Interrompi la misurazione
onboarding-automatic_proportions-check_floor_height-measure-reset = Riprova la misurazione
onboarding-automatic_proportions-check_floor_height-skip_step = Salta il passaggio e salva
onboarding-automatic_proportions-check_floor_height-next_step = Usa l'altezza del pavimento e salva
onboarding-automatic_proportions-start_recording-title = Preparati a muoverti
onboarding-automatic_proportions-start_recording-description = Ora registreremo alcune pose e movimenti specifici. Questi verranno descritte nelle schermate successive. Preparati a iniziare quando premi il pulsante!
onboarding-automatic_proportions-start_recording-next = Inizia registrazione
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Registrazione in corso...
onboarding-automatic_proportions-recording-description-p1 = Fai i movimenti mostrati di seguito:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Stando dritti, ruota la testa con un movimento circolare.
    Piega la schiena in avanti e accovacciati. Mentre sei accovacciatə, guarda a sinistra e poi a destra.
    Ruota la parte superiore del tuo corpo verso sinistra (in senso antiorario), poi raggiungi il suolo con la mano.
    Rupta la parte superiore del tuo corpo verso destra (in senso orario), poi raggiungi il suolo con la mano.
    Ruota i tuoi fianchi in un movimento circolare come se stessi usando un hula hoop.
    Se c'è tempo rimasto nella registrazione, puoi ripetere questi passaggi fino alla fine.
onboarding-automatic_proportions-recording-processing = Elaborazione del risultato
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 secondo rimasto
       *[other] { $time } secondi rimasti
    }
onboarding-automatic_proportions-verify_results-title = Verifica i risultati
onboarding-automatic_proportions-verify_results-description = Controlla i risultati qui sotto, sembrano corretti?
onboarding-automatic_proportions-verify_results-results = Salvataggio dei risultati
onboarding-automatic_proportions-verify_results-processing = Elaborazione del risultato
onboarding-automatic_proportions-verify_results-redo = Rifai registrazione
onboarding-automatic_proportions-verify_results-confirm = Sono corretti
onboarding-automatic_proportions-done-title = Corpo misurato e salvato.
onboarding-automatic_proportions-done-description = La calibrazione delle proporzioni del tuo corpo è completa!
onboarding-automatic_proportions-error_modal-v2 =
    <b>Attenzione:</b> C'è stato un errore durante la stima delle proporzioni!
    Si tratta probabilmente di un problema di calibrazione del montaggio. Assicurati che il tracciamento funzioni correttamente prima di riprovare.
    Controlla <docs>i documenti</docs> o entra nel nostro server <discord>Discord</discord> per assistenza ^_^
onboarding-automatic_proportions-error_modal-confirm = Ho capito!
onboarding-automatic_proportions-smol_warning =
    L'altezza configurata di { $height } è inferiore all'altezza minima accettata di { $minHeight }.
    <b>Si prega di ripetere le misurazioni e assicurarsi che siano corrette.</b>
onboarding-automatic_proportions-smol_warning-cancel = Indietro

## User height calibration


## Stay Aligned setup

onboarding-stay_aligned-title = Rimani Allineato
onboarding-stay_aligned-description = Configura Rimani Allineato per mantenere allineati i tuoi tracker.
onboarding-stay_aligned-put_trackers_on-title = Indossa i tuoi tracker
onboarding-stay_aligned-put_trackers_on-description = Per salvare la tua posa useremo i tracker che hai appena assegnato. Indossa tutti i tuoi tracker, puoi vedere quali sono quali nella figura a destra.
onboarding-stay_aligned-put_trackers_on-trackers_warning = Hai meno di 5 tracker attualmente collegati e assegnati! Questa è la quantità minima di tracker necessaria per il corretto funzionamento di Stay Aligned.
onboarding-stay_aligned-put_trackers_on-next = Sto indossando tutti i miei tracker
onboarding-stay_aligned-verify_mounting-title = Controlla il posizionamento dei tuoi tracker
onboarding-stay_aligned-verify_mounting-step-0 = Rimani Allineato richiede un buon posizionamento dei tracker. In caso contrario, non otterrai una buona esperienza con Rimani Allineato.
onboarding-stay_aligned-verify_mounting-step-1 = 1. Muoviti stando in piedi.
onboarding-stay_aligned-verify_mounting-step-2 = 2. Siediti e muovi gambe e piedi.
onboarding-stay_aligned-verify_mounting-step-3 = 3. Se i tuoi tracker non sono nel posto giusto, riavvia il processo.
onboarding-stay_aligned-verify_mounting-redo_mounting = Rifai la calibrazione del posizionamento
onboarding-stay_aligned-preparation-title = Preparazione
onboarding-stay_aligned-preparation-tip = Assicurati di stare in piedi. Devi guardare in avanti e le tue braccia devono essere abbassate lungo i fianchi.
onboarding-stay_aligned-relaxed_poses-standing-title = Posa Rilassata in piedi
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Stai in una posizione comoda. Rilassati!
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 2. Premi il pulsante "Salva posa".
onboarding-stay_aligned-relaxed_poses-sitting-title = Seduto rilassato nella posa della sedia
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. Siediti in una posizione comoda. Rilassati!
onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2 = 2. Premi il pulsante "Salva posa".
onboarding-stay_aligned-relaxed_poses-flat-title = Posa rilassata seduta a terra
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. Siediti a terra con le gambe in avanti. Rilassati!
onboarding-stay_aligned-relaxed_poses-flat-step-1-v2 = 2. Premi il pulsante "Salva posa".
onboarding-stay_aligned-relaxed_poses-skip_step = Salta
onboarding-stay_aligned-done-title = Rimani Allineato è abilitato!
onboarding-stay_aligned-done-description = La configurazione di Rimani Allineato è completa!
onboarding-stay_aligned-done-description-2 = L'installazione è completa! È possibile riavviare il processo se si desidera ricalibrare le pose
onboarding-stay_aligned-previous_step = Precedente
onboarding-stay_aligned-next_step = Successivo
onboarding-stay_aligned-restart = Riavvia
onboarding-stay_aligned-done = Fatto

## Home

home-no_trackers = Nessun tracker rilevato o assegnato

## Trackers Still On notification

trackers_still_on-modal-title = I tracker sono ancora accesi
trackers_still_on-modal-description =
    Uno o più tracker ancora accesi.
    Vuoi uscire comunque da SmileVR?
trackers_still_on-modal-confirm = Chiudi SlimeVR
trackers_still_on-modal-cancel = Attendi un momento...

## Status system

status_system-StatusTrackerReset = É consigliato eseguire un ripristino completo poiché uno o più tracker non sono regolati.
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] Attualmente non è connesso a SlimeVR Feeder App.
       *[other] Attualmente non è connesso a SteamVR tramite il driver SlimeVR.
    }
status_system-StatusTrackerError = Il tracker { $trackerName } ha un errore.
status_system-StatusUnassignedHMD = Il visore deve essere assegnato come tracker della testa.
status_system-StatusPublicNetwork = Il profilo di rete è attualmente impostato su Pubblico. Questo non è consigliato per il corretto funzionamento di SlimeVR. <PublicFixLink>Scopri come risolverlo qui.</PublicFixLink>

## Firmware tool globals

firmware_tool-next_step = Passaggio successivo
firmware_tool-previous_step = Passaggio precedente
firmware_tool-ok = Tutto apposto
firmware_tool-retry = Riprova
firmware_tool-loading = Caricamento...

## Firmware tool Steps

firmware_tool = Strumento firmware fai-da-te
firmware_tool-description = Ti consente di configurare e flashare i tuoi tracker fai-da-te
firmware_tool-not_available = Oops, lo strumento firmware non è disponibile al momento. Torna più tardi!
firmware_tool-not_compatible = Lo strumento firmware non è compatibile con questa versione del server. Aggiorna il tuo server!
firmware_tool-flash_method_step = Metodo di flashing
firmware_tool-flash_method_step-description = Seleziona il metodo di flashing che desideri utilizzare
firmware_tool-flashbtn_step = Premi il pulsante di avvio
firmware_tool-flashbtn_step-description = Prima di passare al passaggio successivo, ci sono alcune cose che devi fare
firmware_tool-flashbtn_step-board_SLIMEVR = Spegni il tracker, rimuovi la custodia (se presente), collega un cavo USB a questo computer, quindi esegui uno dei seguenti passaggi in base alla revisione della tua scheda SlimeVR:
firmware_tool-flashbtn_step-board_OTHER =
    Prima di eseguire il flashing, sarà probabilmente necessario mettere il tracker in modalità bootloader.
    La maggior parte delle volte significa premere il pulsante di avvio sulla scheda prima che inizi il processo di flashing.
    Se il processo di flashing fa timeout all'inizio del flashing, probabilmente significa che il tracker non era in modalità bootloader
    Si prega di fare riferimento alle istruzioni di flashing della scheda per sapere come attivare la modalità boatloader
firmware_tool-flash_method_ota-devices = Dispositivi OTA rilevati:
firmware_tool-flash_method_ota-no_devices = Non ci sono schede che possono essere aggiornate utilizzando OTA, assicurati di aver selezionato il tipo di scheda corretto
firmware_tool-flash_method_serial-wifi = Credenziali Wi-Fi:
firmware_tool-flash_method_serial-devices-label = Dispositivi seriali rilevati:
firmware_tool-flash_method_serial-devices-placeholder = Seleziona un dispositivo seriale
firmware_tool-flash_method_serial-no_devices = Non sono stati rilevati dispositivi seriali compatibili, assicurarsi che il tracker sia collegato
firmware_tool-build_step = Compilazione
firmware_tool-build_step-description = Il firmware è in fase di compilazione, attendere
firmware_tool-flashing_step = Flashing
firmware_tool-flashing_step-description = Stiamo flashando I tuoi tracker, per piacere segui le istruzioni sullo schermo
firmware_tool-flashing_step-warning-v2 = Non scollegare o spegnere il tracker durante il processo di caricamento a meno che non venga richiesto, potrebbe rendere la scheda inutilizzabile
firmware_tool-flashing_step-flash_more = Esegui il flashing di altri tracker
firmware_tool-flashing_step-exit = Esci

## firmware tool build status

firmware_tool-build-CREATING_BUILD_FOLDER = Creazione della cartella di compilazione
firmware_tool-build-BUILDING = Compilazione del firmware
firmware_tool-build-SAVING = Salvataggio del codice compilato
firmware_tool-build-DONE = Compilazione completata
firmware_tool-build-ERROR = Impossibile compilare il firmware

## Firmware update status

firmware_update-status-DOWNLOADING = Scaricamento del firmware
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Spegnere e riaccendere il tracker
firmware_update-status-AUTHENTICATING = Autenticazione con l'MCU
firmware_update-status-UPLOADING = Caricamento del firmware
firmware_update-status-SYNCING_WITH_MCU = Sincronizzazione con l'MCU
firmware_update-status-REBOOTING = Riavvio del tracker
firmware_update-status-PROVISIONING = Impostazione delle credenziali Wi-Fi
firmware_update-status-DONE = Aggiornamento completato!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Impossibile trovare il dispositivo
firmware_update-status-ERROR_TIMEOUT = Timeout del processo di aggiornamento
firmware_update-status-ERROR_DOWNLOAD_FAILED = Impossibile scaricare il firmware
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Impossibile eseguire l'autenticazione con l'MCU
firmware_update-status-ERROR_UPLOAD_FAILED = Impossibile caricare il firmware
firmware_update-status-ERROR_PROVISIONING_FAILED = Impossibile impostare le credenziali Wi-Fi
firmware_update-status-ERROR_UNSUPPORTED_METHOD = Il metodo di aggiornamento non è supportato
firmware_update-status-ERROR_UNKNOWN = Errore sconosciuto

## Dedicated Firmware Update Page

firmware_update-title = Aggiornamento del firmware
firmware_update-devices = Dispositivi disponibili
firmware_update-devices-description = Seleziona i tracker che desideri aggiornare all'ultima versione del firmware di SlimeVR
firmware_update-no_devices = Assicurati che i tracker che desideri aggiornare siano accesi e connessi al Wi-Fi!
firmware_update-changelog-title = Aggiornamento a { $version }
firmware_update-looking_for_devices = Ricerca in corso di dispositivi da aggiornare...
firmware_update-retry = Riprova
firmware_update-update = Aggiorna i tracker selezionati
firmware_update-exit = Esci

## Tray Menu

tray_menu-show = Mostra
tray_menu-hide = Nascondi
tray_menu-quit = Esci

## First exit modal

tray_or_exit_modal-title = Cosa dovrebbe fare il pulsante di chiusura?
# Multiline text
tray_or_exit_modal-description =
    Questa opzione ti permette di scegliere se si desidera uscire dal server o ridurlo a icona nella barra delle applicationi quando si preme il pulsante di chiusura.
    
    Puoi cambiare la tua scelta in seguito nelle impostazioni dell'interfaccia!
tray_or_exit_modal-radio-exit = Termina alla chiusura
tray_or_exit_modal-radio-tray = Riduci a icona nella barra delle applicazioni
tray_or_exit_modal-submit = Salva
tray_or_exit_modal-cancel = Annulla

## Unknown device modal

unknown_device-modal-title = È stato trovato un nuovo tracker!
unknown_device-modal-description =
    C'è un nuovo tracker con indirizzo MAC <b>{ $deviceId }</b>.
    Vuoi collegarlo a SlimeVR?
unknown_device-modal-confirm = Certo!
unknown_device-modal-forget = Ignoralo
# VRChat config warnings
vrc_config-page-title = Avvertimenti per le impostazioni di VRChat
vrc_config-page-desc = Questa pagina mostra lo stato delle impostazioni di VRChat e quali impostazioni sono incompatibili con SlimeVR. Si consiglia vivamente di correggere eventuali avvisi visualizzati qui per una migliore esperienza utente con SlimeVR.
vrc_config-page-help = Non riesci a trovare le impostazioni?
vrc_config-page-help-desc = Dai un'occhiata alla nostra <a>documentazione su questo argomento!</a>
vrc_config-page-big_menu = Tracking e IK (Menu Grande)
vrc_config-page-big_menu-desc = Impostazioni relative a IK nel menu delle impostazioni grande
vrc_config-page-wrist_menu = Tracking & IK (Menu al polso)
vrc_config-page-wrist_menu-desc = Impostazioni relative a IK nel menu delle impostazioni piccolo (menu al polso)
vrc_config-on = Acceso
vrc_config-off = Spento
vrc_config-invalid = Hai configurato male le impostazioni di VRChat!
vrc_config-show_more = Mostra di più
vrc_config-setting_name = Nome dell'impostazione in VRChat
vrc_config-recommended_value = Valore consigliato
vrc_config-current_value = Valore corrente
vrc_config-mute = Avviso: microfono disattivato
vrc_config-mute-btn = Muto
vrc_config-unmute-btn = Riattiva il microfono
vrc_config-legacy_mode = Usa la risoluzione IK versione precedente
vrc_config-disable_shoulder_tracking = Disabilita il tracciamento della spalla
vrc_config-shoulder_width_compensation = Compensazione della larghezza delle spalle
vrc_config-spine_mode = Modalità spina dorsale FBT
vrc_config-tracker_model = Modello Tracker FBT
vrc_config-avatar_measurement_type = Misurazione dell'avatar
vrc_config-calibration_range = Raggio di Calibrazione
vrc_config-calibration_visuals = Mostra Aiuto Visivo Calibrazione
vrc_config-user_height = Altezza reale dell'utente
vrc_config-spine_mode-UNKNOWN = Sconosciuto
vrc_config-spine_mode-LOCK_BOTH = Blocca entrambi
vrc_config-spine_mode-LOCK_HEAD = Blocca Testa
vrc_config-spine_mode-LOCK_HIP = Blocca anca
vrc_config-tracker_model-UNKNOWN = Sconosciuto
vrc_config-tracker_model-AXIS = Assi
vrc_config-tracker_model-BOX = Cubo
vrc_config-tracker_model-SPHERE = Sfera
vrc_config-tracker_model-SYSTEM = Sistema
vrc_config-avatar_measurement_type-UNKNOWN = Sconosciuto
vrc_config-avatar_measurement_type-HEIGHT = Altezza
vrc_config-avatar_measurement_type-ARM_SPAN = Apertura del braccio

## Error collection consent modal

error_collection_modal-title = Possiamo raccogliere gli errori?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    Puoi modificare questa impostazione in un secondo momento nella sezione Comportamento delle impostazioni.
error_collection_modal-confirm = Acconsento
error_collection_modal-cancel = Non acconsento

## Tracking checklist section

