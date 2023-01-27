### SlimeVR complete GUI translations


# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Connessione al server in corso
websocket-connection_lost = Connessione con il server persa. Tentativo di riconnessione in corso...

## Tips

tips-find_tracker = Non sei sicurə quale tracker è quale? Agitalo e l'oggetto corrispondente sarà evidenziato.
tips-do_not_move_heels = Assicurati di non muovere i tuoi talloni durante la registrazione!

## Body parts

body_part-NONE = Non assegnato
body_part-HEAD = Testa
body_part-NECK = Collo
body_part-RIGHT_SHOULDER = Spalla destra
body_part-RIGHT_UPPER_ARM = Braccio destro
body_part-RIGHT_LOWER_ARM = Avambraccio destro
body_part-RIGHT_HAND = Mano destra
body_part-RIGHT_UPPER_LEG = Coscia destra
body_part-RIGHT_LOWER_LEG = Caviglia destra
body_part-RIGHT_FOOT = Piede destro
body_part-RIGHT_CONTROLLER = Controller destro
body_part-CHEST = Petto
body_part-WAIST = Girovita
body_part-HIP = Fianchi
body_part-LEFT_SHOULDER = Spalla sinistra
body_part-LEFT_UPPER_ARM = Braccio sinistro
body_part-LEFT_LOWER_ARM = Avambraccio sinistro
body_part-LEFT_HAND = Mano sinistra
body_part-LEFT_UPPER_LEG = Coscia sinistra
body_part-LEFT_LOWER_LEG = Caviglia sinistra
body_part-LEFT_FOOT = Piede sinistro
body_part-LEFT_CONTROLLER = Controller sinistro

## Proportions

skeleton_bone-NONE = Non assegnato
skeleton_bone-HEAD = Correzione Testa
skeleton_bone-NECK = Lunghezza Collo
skeleton_bone-CHEST = Lunghezza del torace
skeleton_bone-CHEST_OFFSET = Correzione Petto
skeleton_bone-WAIST = Giro vita
skeleton_bone-HIP = Lunghezza fianchi
skeleton_bone-HIP_OFFSET = Correzione Fianchi
skeleton_bone-HIPS_WIDTH = Larghezza Fianchi
skeleton_bone-UPPER_LEG = Lunghezza della parte superiore della gamba
skeleton_bone-LOWER_LEG = Lunghezza della parte inferiore della gamba
skeleton_bone-FOOT_LENGTH = Lunghezza Piedi
skeleton_bone-FOOT_SHIFT = Correzione Piedi
skeleton_bone-SKELETON_OFFSET = Compensazione scheletro
skeleton_bone-SHOULDERS_DISTANCE = Distanza Spalle
skeleton_bone-SHOULDERS_WIDTH = Larghezza Spalle
skeleton_bone-UPPER_ARM = Lunghezza Braccia
skeleton_bone-LOWER_ARM = Distanza Avambracci
skeleton_bone-CONTROLLER_Y = Distanza Y Controller
skeleton_bone-CONTROLLER_Z = Distanza Z Controller
skeleton_bone-ELBOW_OFFSET = Correzione Gomito

## Tracker reset buttons

reset-reset_all = Ripristina tutte le proporzioni
reset-full = Ripristina
reset-mounting = Ripristina posizionamento
reset-quick = Reset veloce

## Serial detection stuff

serial_detection-new_device-p0 = Nuovo dispositivo seriale rilevato!
serial_detection-new_device-p1 = Inserisci le tue credenziali Wi-Fi!
serial_detection-new_device-p2 = Seleziona come utilizzare il tracker, per piacere
serial_detection-open_wifi = Connetti al Wi-Fi
serial_detection-open_serial = Apri la Serial Console
serial_detection-submit = Conferma!
serial_detection-close = Chiudi

## Navigation bar

navbar-home = Home
navbar-body_proportions = Proporzioni del corpo
navbar-trackers_assign = Assegnazione dei tracker
navbar-mounting = Calibrazione della posizionamento
navbar-onboarding = Installazione guidata
navbar-settings = Impostazioni

## Bounding volume hierarchy recording

bvh-start_recording = Registra BVH
bvh-recording = Registrazione in corso...

## Widget: Overlay settings

widget-overlay = Overlay
widget-overlay-is_visible_label = Mostra Overlay in SteamVR
widget-overlay-is_mirrored_label = Includi uno specchio nel Overlay

## Widget: Developer settings

widget-developer_mode = Modalità sviluppatore
widget-developer_mode-high_contrast = Alto contrasto
widget-developer_mode-precise_rotation = Rotazione precisa
widget-developer_mode-fast_data_feed = Alimentazione veloce dei dati
widget-developer_mode-filter_slimes_and_hmd = Filtra slimes e HMD
widget-developer_mode-sort_by_name = Ordina per nome
widget-developer_mode-raw_slime_rotation = Rotazione non processata
widget-developer_mode-more_info = Ulteriori informazioni

## Widget: IMU Visualizer

widget-imu_visualizer = Rotazione
widget-imu_visualizer-rotation_raw = Non processato
widget-imu_visualizer-rotation_preview = Anteprima

## Tracker status

tracker-status-none = Nessuno Stato
tracker-status-busy = Occupato
tracker-status-error = Errore
tracker-status-disconnected = Disconnesso
tracker-status-occluded = Ostruito
tracker-status-ok = Connesso

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
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Davanti
tracker-rotation-left = Sinistra
tracker-rotation-right = Destra
tracker-rotation-back = Dietro

## Tracker information

tracker-infos-manufacturer = Produttore
tracker-infos-display_name = Nome da visualizzare
tracker-infos-custom_name = Nome Personalizzato
tracker-infos-url = URL del tracker

## Tracker settings

tracker-settings-back = Torna alla lista dei tracker
tracker-settings-title = Impostazioni del tracker
tracker-settings-assignment_section = Assegnazione
tracker-settings-assignment_section-description = Definisce a che parte del corpo è assegnato il tracker.
tracker-settings-assignment_section-edit = Cambia assegnazione
tracker-settings-mounting_section = posizionamento di montaggio
tracker-settings-mounting_section-description = Come è posizionato il tracker?
tracker-settings-mounting_section-edit = Cambia posizionamento
tracker-settings-drift_compensation_section = Consenti compensazione deriva
tracker-settings-drift_compensation_section-description = Questo tracker dovrebbe compensare per il drift quando la compensazione del drift è abilitata?
tracker-settings-drift_compensation_section-edit = Consenti compensazione del drift
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nome del tracker
tracker-settings-name_section-description = Scegli un nomignolo carino :)
tracker-settings-name_section-placeholder = Gamba destra di NightyQueer

## Tracker part card info

tracker-part_card-no_name = Nessun nome
tracker-part_card-unassigned = Non assegnato

## Body assignment menu

body_assignment_menu = Con che parte del corpo vuoi utilizzare il tracker?
body_assignment_menu-description = Scegli una parte del corpo a cui assegnare questo tracker. Alternativamente puoi scegliere di gestire tutti i tracker in una schermata unica invece che singolarmente.
body_assignment_menu-show_advanced_locations = Mostra impostazioni avanzate di Assegnazione
body_assignment_menu-manage_trackers = Gestisci tutti i tracker
body_assignment_menu-unassign_tracker = Rimuovi assegnazione del tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Which tracker to assign to your
tracker_selection_menu-NONE = Quale tracker vuoi che non sia assegnato?
tracker_selection_menu-HEAD = { -tracker_selection-part } testa?
tracker_selection_menu-NECK = { -tracker_selection-part } collo?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } spalla destra?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } braccio superiore destro?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } braccio inferiore destro?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } mano destra?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } coscia destra?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } caviglia destra?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } piede destro?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } controller destro?
tracker_selection_menu-CHEST = { -tracker_selection-part } petto?
tracker_selection_menu-WAIST = { -tracker_selection-part } vita?
tracker_selection_menu-HIP = { -tracker_selection-part } anca?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } spalla sinistra?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } braccio superiore sinistro?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } braccio inferiore sinistro?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } mano sinistra?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } coscia sinistra?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } caviglia sinistra?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } piede sinistro?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } controller sinistro?
tracker_selection_menu-unassigned = Tracker non assegnato
tracker_selection_menu-assigned = Dispositivo assegnato
tracker_selection_menu-dont_assign = Non assegnare

## Mounting menu

mounting_selection_menu = Come è posizionato il tracker?
mounting_selection_menu-close = Chiudi

## Sidebar settings

settings-sidebar-title = Impostazioni
settings-sidebar-general = Generali
settings-sidebar-tracker_mechanics = Comportamento del tracker
settings-sidebar-fk_settings = Impostazioni FK
settings-sidebar-gesture_control = Controllo dei gesti
settings-sidebar-interface = Interfaccia
settings-sidebar-osc_router = OSC router
settings-sidebar-utils = Strumenti
settings-sidebar-serial = Console seriale

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
settings-general-steamvr-trackers-feet = Piedi
settings-general-steamvr-trackers-knees = Ginocchia
settings-general-steamvr-trackers-elbows = Gomiti
settings-general-steamvr-trackers-hands = Mani

## Tracker mechanics

settings-general-tracker_mechanics = Comportamento del tracker
settings-general-tracker_mechanics-filtering = Filtro movimenti
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Scegli la tipologia di filtraggio movimenti per il tuo tracker.
    Predizione anticipa i movimenti, mentre Attenuazione attenua movimenti eccessivi.
settings-general-tracker_mechanics-filtering-type = Tipologia di filtraggio
settings-general-tracker_mechanics-filtering-type-none = Non usare alcun filtraggio
settings-general-tracker_mechanics-filtering-type-none-description = Usa le rotazioni così come sono. Non eseguirà alcun filtro.
settings-general-tracker_mechanics-filtering-type-smoothing = Attenuazione
settings-general-tracker_mechanics-filtering-type-smoothing-description = Attenua movimenti eccessivi ma aggiunge ritardo.
settings-general-tracker_mechanics-filtering-type-prediction = Predizione
settings-general-tracker_mechanics-filtering-type-prediction-description = Riduce ritardo e rende movimenti più istantanei, ma può introdurre tremolio.
settings-general-tracker_mechanics-filtering-amount = Quantità
settings-general-tracker_mechanics-drift_compensation = Compensazione del drift
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Compensates IMU yaw drift by applying an inverse rotation.
    Change amount of compensation and up to how many resets are taken into account.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Compensazione del drift
settings-general-tracker_mechanics-drift_compensation-amount-label = Grado di compensazione
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Utilizza fino a x ultimi ripristini

## FK/Tracking settings

settings-general-fk_settings = Impostazioni Tracking
settings-general-fk_settings-leg_tweak = Impostazioni Gambe
settings-general-fk_settings-leg_tweak-description = "Compenetrazione pavimento" può ridurre o anche eliminare completamente la compenetrazione con il pavimento, ma può causare problemi quando in ginocchio. "Correzione pattinaggio" corregge l'effetto che fa sembrare che pattini sul ghiaccio, ma può peggiorare la precisione di certi movimenti.
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Compenetrazione pavimento
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Correzione pattinaggio
settings-general-fk_settings-leg_tweak-skating_correction-amount = Forza fattore di correzione pattinaggio
settings-general-fk_settings-arm_fk = FK Braccia
settings-general-fk_settings-arm_fk-description = Cambia la modalità di tracciamento delle braccia.
settings-general-fk_settings-arm_fk-force_arms = Forza il calcolo della posizione delle braccia a utilizzare il HMD
settings-general-fk_settings-skeleton_settings = Impostazioni scheletro
settings-general-fk_settings-skeleton_settings-description = Abilita o disabilita le impostazioni dello scheletro. É raccomandato lasciare queste impostazioni attive.
settings-general-fk_settings-skeleton_settings-extended_spine = Estensione colonna vertebrale
settings-general-fk_settings-skeleton_settings-extended_pelvis = Estensione bacino
settings-general-fk_settings-skeleton_settings-extended_knees = Estensione ginocchia
settings-general-fk_settings-vive_emulation-title = Emulazione Vive
settings-general-fk_settings-vive_emulation-description = Emula i problemi del tracker in vita che hanno i tracker Vive. Questo è uno scherzo e peggiora il tracciamento.
settings-general-fk_settings-vive_emulation-label = Abilita l'emulazione Vive

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Controllo dei gesti
settings-general-gesture_control-subtitle = Reset veloce con il doppio tocco
settings-general-gesture_control-description = Abilita o disabilita il reset veloce con il doppio tocco. Quando attivato, un doppio tocco su una qualsiasi parte del tracker posizionato più in altro lungo il torso abiliterà il reset veloce. "Ritardo" è il ritardo dal momento in cui il gesto è eseguito e il Reset.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tocco
       *[other] { $amount } tocchi
    }
settings-general-gesture_control-quickResetEnabled = Abilita il tocco per il ripristino rapido
settings-general-gesture_control-quickResetDelay = Ritardo reset rapido
settings-general-gesture_control-quickResetTaps = Tocchi per ripristino rapido
settings-general-gesture_control-resetEnabled = Abilita tocca per ripristinare
settings-general-gesture_control-resetDelay = Ritardo reset
settings-general-gesture_control-resetTaps = Tocchi per resettare
settings-general-gesture_control-mountingResetEnabled = Abilita tocco per ripristinare il montaggio
settings-general-gesture_control-mountingResetDelay = Ritardo ripristino montaggio
settings-general-gesture_control-mountingResetTaps = Tocchi per il ripristino rapido

## Interface settings

settings-general-interface = Interfaccia
settings-general-interface-dev_mode = Modalità sviluppatore
settings-general-interface-dev_mode-description = Questa modalità è utile se hai bisogno di dati approfonditi o devi interagire in maniera più avanzata con i tracker connessi.
settings-general-interface-dev_mode-label = Modalità sviluppatore
settings-general-interface-serial_detection = Rilevazione nuovi dispositivi seriali
settings-general-interface-serial_detection-description = Questa opzione mostrerà un pop-up ogni volta che colleghi un nuovo dispositivo seriale che potrebbe essere un tracker. Aiuta a facilitare la configurazione iniziale di un tracker
settings-general-interface-serial_detection-label = Rilevazione nuovi dispositivi seriali
settings-general-interface-lang = Seleziona la lingua
settings-general-interface-lang-description = Seleziona la lingua che vuoi utilizzare
settings-general-interface-lang-placeholder = Seleziona la lingua da utilizzare

## Serial settings

settings-serial = Serial Console
# This cares about multilines
settings-serial-description =
    Questo è un feed di informazioni in tempo reale per la comunicazione seriale.
    Può essere utile se ti serve capire se il firmware sta avendo problemi.
settings-serial-connection_lost = Connessione seriale persa. Riconnessione in corso...
settings-serial-reboot = Riavvia
settings-serial-factory_reset = Ripristino delle impostazioni di fabbrica
settings-serial-get_infos = Ottieni informazioni
settings-serial-serial_select = Seleziona una porta seriale
settings-serial-auto_dropdown_item = Automatico

## OSC router settings

settings-osc-router = OSC router
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

settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description =
    Modifica le impostazioni specifiche a VRChat per ricevere e inviare dati del HMD
    Dati dei tracker per FBT (funziona su Quest standalone).
settings-osc-vrchat-enable = Attiva
settings-osc-vrchat-enable-description = Attiva o disattiva l'invio e la ricezione dei dati
settings-osc-vrchat-enable-label = Attiva
settings-osc-vrchat-network = Porte di rete
settings-osc-vrchat-network-description = Impostare le porte di rete per ascoltare e inviare dati a VRChat
settings-osc-vrchat-network-port_in =
    .label = Porta in ingresso
    .placeholder = Porta in ingresso (predefinito: 9002)
settings-osc-vrchat-network-port_out =
    .label = Porta in uscita
    .placeholder = Porta in uscita (predefinito: 9000)
settings-osc-vrchat-network-address = Indirizzo di rete
settings-osc-vrchat-network-address-description = Scegli a quale indirizzo di rete inviare i dati di VRChat (controlla le impostazioni Wi-Fi sul tuo dispositivo)
settings-osc-vrchat-network-address-placeholder = Indirizzo IP di VRChat
settings-osc-vrchat-network-trackers = Tracker
settings-osc-vrchat-network-trackers-description = Attiva o disattiva l'invio e la ricezione dei dati
settings-osc-vrchat-network-trackers-chest = Petto
settings-osc-vrchat-network-trackers-waist = Girovita
settings-osc-vrchat-network-trackers-knees = Ginocchia
settings-osc-vrchat-network-trackers-feet = Piedi
settings-osc-vrchat-network-trackers-elbows = Gomiti

## Setup/onboarding menu

onboarding-skip = Salta la configurazione
onboarding-continue = Continua
onboarding-wip = Lavori in corso

## Wi-Fi setup

onboarding-wifi_creds-back = Torna all'introduzione
onboarding-wifi_creds = Inserisci credenziali Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description =
    I tracker utilizzeranno queste credenziali per connettersi in modalità wireless
    Si prega di utilizzare le stesse credenziali con cui si è attualmente connessi
onboarding-wifi_creds-skip = Salta impostazioni Wi-Fi
onboarding-wifi_creds-submit = Conferma!
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = Enter SSID
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup

onboarding-reset_tutorial-back = Torna alla calibrazione posizionamento
onboarding-reset_tutorial = Tutorial di ripristino
onboarding-reset_tutorial-description = Questa funzionalità non è completa, premi continua

## Setup start

onboarding-home = Benvenuti a SlimeVR
# This cares about multilines and it's centered!!
onboarding-home-description =
    Portiamo full-body tracking
    a tuttə
onboarding-home-start = Prepariamoci!

## Enter VR part of setup

onboarding-enter_vr-back = Torna all'assegnazione dei tracker
onboarding-enter_vr-title = È ora di entrare in VR!
onboarding-enter_vr-description = Indossa tutti i tuoi tracker e entra in VR!
onboarding-enter_vr-ready = Sono prontə!

## Setup done

onboarding-done-title = È tutto pronto!
onboarding-done-description = Goditi la tua esperienza di full-body tracking
onboarding-done-close = Chiudi la guida

## Tracker connection setup

onboarding-connect_tracker-back = Torna alle credenziali Wi-Fi
onboarding-connect_tracker-title = Connetti i tracker
onboarding-connect_tracker-description-p0 = Ora passiamo alla parte divertente, colleghiamo tutti i tracker!
onboarding-connect_tracker-description-p1 = Collega semplicemente tutti i tracker che non sono ancora collegati tramite una porta USB.
onboarding-connect_tracker-issue-serial = Ho problemi con la connessione!
onboarding-connect_tracker-usb = Tracker USB
onboarding-connect_tracker-connection_status-connecting = Invio credenziali Wi-Fi in corso.
onboarding-connect_tracker-connection_status-connected = Connesso al Wi-Fi
onboarding-connect_tracker-connection_status-error = Impossibile connettersi al Wi-Fi
onboarding-connect_tracker-connection_status-start_connecting = Ricerca dei tracker in corso
onboarding-connect_tracker-connection_status-handshake = Connesso al Server
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] No trackers
        [one] 1 tracker
       *[other] { $amount } trackers
    } connected
onboarding-connect_tracker-next = Ho collegato tutti i miei tracker

## Tracker assignment setup

onboarding-assign_trackers-back = Torna alle credenziali Wi-Fi
onboarding-assign_trackers-title = Assegna i tracker
onboarding-assign_trackers-description = Scegliamo quale tracker va dove. Fare clic su una parte del corpo in cui si desidera assegnare un tracker
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $assigned } of { $trackers ->
        [one] 1 tracker
       *[other] { $trackers } trackers
    } assigned
onboarding-assign_trackers-advanced = Mostra impostazioni avanzate di assegnazione
onboarding-assign_trackers-next = Ho assegnato tutti i miei tracker

## Tracker manual mounting setup

onboarding-manual_mounting-back = Torna indietro per entrare in VR
onboarding-manual_mounting = Posizionamento manuale
onboarding-manual_mounting-description = Fare clic su ogni tracker e selezionare in che direzione sono montati
onboarding-manual_mounting-auto_mounting = Posizionamento automatico
onboarding-manual_mounting-next = Passo successivo

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Torna indietro per entrare in VR
onboarding-automatic_mounting-title = Calibrazione posizionamento
onboarding-automatic_mounting-description = Affinché i tracker di SlimeVR funzionino, dobbiamo configurare una rotazione di posizione di montaggio ai tuoi tracker per allinearli con la posizione di montaggio del tuo tracker fisico.
onboarding-automatic_mounting-manual_mounting = Imposta posizione manualmente
onboarding-automatic_mounting-next = Passo successivo
onboarding-automatic_mounting-prev_step = Passaggio precedente
onboarding-automatic_mounting-done-title = Rotazione delle posizioni di montaggio calibrate.
onboarding-automatic_mounting-done-description = La calibrazione della posizione é completa!
onboarding-automatic_mounting-done-restart = Torna all'inizio
onboarding-automatic_mounting-mounting_reset-title = Ripristina posizionamento
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Piegati come per sciare: con le gambe leggermente piegate e unite, la parte superiore del corpo inclinata in avanti e le braccia piegate.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Premere il pulsante "Ripristina posizionamento" e attendere 3 secondi prima che le rotazioni delle posizioni di montaggio dei tracker vengano ripristinate.
onboarding-automatic_mounting-preparation-title = Preparazione
onboarding-automatic_mounting-preparation-step-0 = 1. Stai in piedi drittə con le braccia lungo i fianchi.
onboarding-automatic_mounting-preparation-step-1 = 2. Premi il pulsante "Reset" e attendi 3 secondi prima che i tracker vengano ripristinati.
onboarding-automatic_mounting-put_trackers_on-title = Indossa i tuoi tracker
onboarding-automatic_mounting-put_trackers_on-description = Per calibrare le rotazioni delle posizioni montaggio useremo i tracker che hai appena assegnato. Indossa tutti i tuoi tracker, puoi vedere quali sono quali nella figura a destra.
onboarding-automatic_mounting-put_trackers_on-next = Sto indossando tutti i miei tracker

## Tracker manual proportions setup

onboarding-manual_proportions-back = Torna al tutorial di reset
onboarding-manual_proportions-title = Impostazione manuale delle proporzioni del corpo
onboarding-manual_proportions-precision = Regolazione di precisione
onboarding-manual_proportions-auto = Calibrazione automatica

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Torna al tutorial di reset
onboarding-automatic_proportions-title = Misura il tuo corpo
onboarding-automatic_proportions-description = Affinché i tracker di SlimeVR funzionino dobbiamo conoscere la lunghezza dei tuoi arti. Questa breve calibrazione le misurerà per te.
onboarding-automatic_proportions-manual = Calibrazione manuale
onboarding-automatic_proportions-prev_step = Passaggio precedente
onboarding-automatic_proportions-put_trackers_on-title = Indossa i tuoi tracker
onboarding-automatic_proportions-put_trackers_on-description = Per calibrare le tue proporzioni useremo i tracker che hai appena assegnato. Indossa tutti i tuoi tracker, puoi vedere quali sono quali nella figura a destra.
onboarding-automatic_proportions-put_trackers_on-next = Sto indossando tutti i miei tracker
onboarding-automatic_proportions-preparation-title = Preparazione
onboarding-automatic_proportions-preparation-description = Posiziona una sedia direttamente dietro di te all'interno della tua area di gioco. Ti verrà richiesto di sederti durante certi passaggi della calibrazione delle proporzioni.
onboarding-automatic_proportions-preparation-next = Sono davanti a una sedia
onboarding-automatic_proportions-start_recording-title = Preparati a muoverti
onboarding-automatic_proportions-start_recording-description = Ora registreremo alcune pose e movimenti specifici. Questi verranno descritte nelle schermate successive. Preparati a iniziare quando premi il pulsante!
onboarding-automatic_proportions-start_recording-next = Inizia registrazione
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Registrazione in corso...
onboarding-automatic_proportions-recording-description-p1 = Fai i movimenti mostrati di seguito:
onboarding-automatic_proportions-recording-steps-0 = Piega le ginocchia un paio di volte.
onboarding-automatic_proportions-recording-steps-1 = Siediti su una sedia e poi alzati.
onboarding-automatic_proportions-recording-steps-2 = Ruota la parte superiore del corpo a sinistra, poi piegati a destra.
onboarding-automatic_proportions-recording-steps-3 = Ruota la parte superiore del corpo a destra, poi piegati a sinistra.
onboarding-automatic_proportions-recording-steps-4 = Muoviti un poco fino allo scadere del timer.
onboarding-automatic_proportions-recording-processing = Elaborazione del risultato
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 15)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 second left
       *[other] { $time } seconds left
    }
onboarding-automatic_proportions-verify_results-title = Verifica i risultati
onboarding-automatic_proportions-verify_results-description = Controlla i risultati qui sotto, sembrano corretti?
onboarding-automatic_proportions-verify_results-results = Salvataggio dei risultati
onboarding-automatic_proportions-verify_results-processing = Elaborazione del risultato
onboarding-automatic_proportions-verify_results-redo = Rifai registrazione
onboarding-automatic_proportions-verify_results-confirm = Sono corretti
onboarding-automatic_proportions-done-title = Corpo misurato e salvato.
onboarding-automatic_proportions-done-description = La calibrazione delle proporzioni del tuo corpo è completa!

## Home

home-no_trackers = Nessun tracker rilevato o assegnato
