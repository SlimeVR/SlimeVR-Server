package dev.slimevr.android

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import dev.slimevr.AppContext
import dev.slimevr.FeatureFlags
import dev.slimevr.Phase1Context
import dev.slimevr.VRServer
import dev.slimevr.android.config.AndroidConfigStorage
import dev.slimevr.android.hid.createAndroidHIDManager
import dev.slimevr.android.ipc.createAndroidSolarXRWebsocketServer
import dev.slimevr.android.serial.AndroidFirmwareFlasher
import dev.slimevr.android.serial.createAndroidSerialServer
import dev.slimevr.android.udp.resolveAndroidUdpAddress
import dev.slimevr.android.vrchat.resolveAndroidOscQueryAddress
import dev.slimevr.bvh.BVHManager
import dev.slimevr.config.AppConfig
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.udp.UdpServer
import dev.slimevr.util.safeLaunch
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vrcosc.VRCOSCManager
import io.klogging.noCoLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel

private val logger = noCoLogger("ForegroundService")

private const val ACTION_STOP = "dev.slimevr.android.STOP_SERVER"
const val ACTION_FINISH_APP = "dev.slimevr.android.FINISH_APP"

/**
 * ForegroundService helps to keep the SlimeVR Server on Android from being killed.
 * Especially when the process is in the background when the user is playing a game
 * or something else.
 *
 * Processes with foreground services on Android are less likely to be kill per
 * https://developer.android.com/guide/components/activities/activity-lifecycle#asem
 *
 * Tested with VRChat on Quest 3 in high population worlds with all avatars on
 * for over an hour with no shut down of the SlimeVR server. Without this service,
 * the server would often be shutdown unexpectedly.
 */
class ForegroundService : Service() {
	private val CHANNEL_ID = "SlimeVrForegroundServiceChannel"
	private val NOTIFICATION_ID = 100

	private var serviceScope: CoroutineScope? = null
	private var wakeLock: PowerManager.WakeLock? = null
	private var multicastLock: WifiManager.MulticastLock? = null
	private var wifiLock: WifiManager.WifiLock? = null

	override fun onCreate() {
		super.onCreate()
		setupAndroidLogging()
		createNotificationChannel()
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		if (intent?.action == ACTION_STOP) {
			ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
			serviceScope?.cancel()
			serviceScope = null
			sendBroadcast(Intent(ACTION_FINISH_APP).setPackage(packageName))
			stopSelf()
			return START_NOT_STICKY
		}

		try {
			val notification = createNotification()
			ServiceCompat.startForeground(
				this,
				NOTIFICATION_ID,
				notification,
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
					ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
				} else {
					0
				},
			)
		} catch (e: Exception) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
				e is ForegroundServiceStartNotAllowedException
			) {
				logger.error(e, "Tried to start foreground service when not allowed")
			}
		}

		if (serviceScope == null) {
			val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
			serviceScope = scope
			scope.safeLaunch { startServer(scope) }
		}

		return START_NOT_STICKY
	}

	override fun onDestroy() {
		serviceScope?.cancel()
		serviceScope = null
		wakeLock?.release()
		wakeLock = null
		multicastLock?.release()
		multicastLock = null
		wifiLock?.release()
		wifiLock = null
		super.onDestroy()
	}

	override fun onBind(intent: Intent?): IBinder? = null

	private suspend fun startServer(scope: CoroutineScope) {
		val storage = AndroidConfigStorage(filesDir)
		val config = AppConfig.create(scope = scope, storage = storage)
		val server = VRServer.create(scope = scope)
		val serialServer = createAndroidSerialServer(context = this, scope = scope)

		val phase1 = Phase1Context(server = server, config = config, serialServer = serialServer)

		val firmwareManager = FirmwareManager.create(ctx = phase1, scope = scope, flasher = AndroidFirmwareFlasher)
		val networkProfileManager = NetworkProfileManager.create(scope = scope, isSupported = false)
		val skeleton = Skeleton.create(scope = scope, ctx = phase1)
		val provisioningManager = ProvisioningManager.create(ctx = phase1, scope = scope)
		val heightCalibrationManager = HeightCalibrationManager.create(ctx = phase1, scope = scope)
		val trackingChecklist = TrackingChecklist.create(scope = scope)
		val udpServer = UdpServer.create(scope = scope, addressResolver = ::resolveAndroidUdpAddress)
		val bvhManager = BVHManager.create(skeleton = skeleton, storage = storage, scope = scope)
		val vmcManager = VMCManager.create(skeleton = skeleton, ctx = phase1, scope = scope)
		val vrcOscManager = VRCOSCManager.create(
			ctx = phase1,
			scope = scope,
			oscQueryAddress = resolveAndroidOscQueryAddress(),
		)

		val appContext = AppContext(
			server = server,
			config = config,
			serialServer = serialServer,
			featureFlags = FeatureFlags(supportsSteamVR = false),
			skeleton = skeleton,
			firmwareManager = firmwareManager,
			vrcConfigManager = null,
			networkProfileManager = networkProfileManager,
			provisioningManager = provisioningManager,
			heightCalibrationManager = heightCalibrationManager,
			trackingChecklist = trackingChecklist,
			udpServer = udpServer,
			bvhManager = bvhManager,
			vmcManager = vmcManager,
			vrcOscManager = vrcOscManager,
		)

		acquireLocks()

		try {
			appContext.startObserving()

			scope.safeLaunch { createAndroidHIDManager(context = this@ForegroundService, appContext = appContext, scope = this) }
			scope.safeLaunch { createAndroidSolarXRWebsocketServer(appContext) }

			awaitCancellation()
		} finally {
			appContext.dispose()
		}
	}

	@SuppressLint("WakelockTimeout")
	private fun acquireLocks() {
		val power = getSystemService(Context.POWER_SERVICE) as PowerManager
		wakeLock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "slimevr:server")
			.apply { setReferenceCounted(false) }
			.also { it.acquire() }

		val wifi = getSystemService(Context.WIFI_SERVICE) as WifiManager
		multicastLock = wifi.createMulticastLock("slimevr-multicast-lock")
			.apply { setReferenceCounted(true) }
			.also { it.acquire() }
		@Suppress("DEPRECATION")
		wifiLock = wifi.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "slimevr-wifi-lock")
			.apply { setReferenceCounted(false) }
			.also { it.acquire() }
	}

	private fun stopPendingIntent(): PendingIntent = PendingIntent.getService(
		this,
		0,
		Intent(this, ForegroundService::class.java).apply { action = ACTION_STOP },
		PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
	)

	private fun createNotificationChannel() {
		val serviceChannel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManager.IMPORTANCE_LOW)
			.setName("SlimeVR Foreground Service Channel")
			.build()
		NotificationManagerCompat
			.from(this)
			.createNotificationChannel(serviceChannel)
	}

	private fun openAppPendingIntent(): PendingIntent = PendingIntent.getActivity(
		this,
		0,
		Intent(this, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
		},
		PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
	)

	private fun createNotification(): Notification = NotificationCompat.Builder(this, CHANNEL_ID)
		.setContentTitle("SlimeVR Server Running")
		.setContentText("This notification helps keep the server alive")
		.setSmallIcon(R.drawable.ic_launcher_foreground)
		.setContentIntent(openAppPendingIntent())
		.addAction(0, getString(R.string.stop_server), stopPendingIntent())
		.build()
}
