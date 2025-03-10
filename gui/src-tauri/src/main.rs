#![cfg_attr(all(not(debug_assertions), windows), windows_subsystem = "windows")]
use std::env;
use std::panic;
use std::path::Path;
use std::sync::atomic::AtomicBool;
use std::sync::atomic::Ordering;
use std::sync::Arc;
use std::sync::Mutex;
use std::thread;
use std::time::Duration;
use std::time::Instant;

use clap::Parser;
use color_eyre::Result;
use state::WindowState;
use tauri::Emitter;
use tauri::WindowEvent;
use tauri::{Manager, RunEvent};
use tauri_plugin_shell::process::CommandChild;

use crate::util::{
	get_launch_path, show_error, valid_java_paths, Cli, JAVA_BIN, MINIMUM_JAVA_VERSION,
};

mod presence;
mod state;
mod tray;
mod util;

#[tauri::command]
fn update_window_state(
	window: tauri::Window,
	state: tauri::State<Mutex<WindowState>>,
) -> Result<(), String> {
	let mut lock = state.lock().unwrap();
	lock.update_state(&window, false)
		.map_err(|e| format!("{:?}", e))?;
	if window.is_maximized().map_err(|e| e.to_string())? {
		window.unmaximize().map_err(|e| e.to_string())?;
		lock.update_state(&window, true)
			.map_err(|e| format!("{:?}", e))?;
	}
	Ok(())
}

#[tauri::command]
fn logging(msg: String) {
	log::info!(target: "webview", "{}", msg)
}

#[tauri::command]
fn erroring(msg: String) {
	log::error!(target: "webview", "{}", msg)
}

#[tauri::command]
fn warning(msg: String) {
	log::warn!(target: "webview", "{}", msg)
}

#[tauri::command]
fn open_logs_folder(app_handle: tauri::AppHandle) {
	let config_dir = app_handle
		.path()
		.app_config_dir()
		.unwrap_or_else(|_| Path::new(".").to_path_buf());
	let path = config_dir.join("logs");
	let path_str = path.to_string_lossy().into_owned();

	if let Err(err) = open::that(path_str) {
		eprintln!("Failed to open config folder: {}", err);
	}
}

fn main() -> Result<()> {
	log_panics::init();
	let hook = panic::take_hook();
	// Make an error dialog box when panicking
	panic::set_hook(Box::new(move |panic_info| {
		show_error(&panic_info.to_string());
		hook(panic_info);
	}));

	let cli = Cli::parse();
	let tauri_context = tauri::generate_context!();

	// Set up loggers and global handlers
	let _logger = setup_logger(&tauri_context);

	// Ensure child processes die when spawned on windows
	// and then check for WebView2's existence
	#[cfg(windows)]
	setup_webview2()?;

	// Check for environment variables that can affect the server, and if so, warn in log and GUI
	check_environment_variables();

	// Spawn server process
	let exit_flag = Arc::new(AtomicBool::new(false));
	let backend = Arc::new(Mutex::new(Option::<CommandChild>::None));

	let server_info = execute_server(cli)?;
	let build_result = setup_tauri(
		tauri_context,
		server_info,
		exit_flag.clone(),
		backend.clone(),
	);

	tauri_build_result(build_result, exit_flag, backend);

	Ok(())
}

fn setup_logger(context: &tauri::Context) -> Result<flexi_logger::LoggerHandle> {
	use flexi_logger::{
		Age, Cleanup, Criterion, Duplicate, FileSpec, Logger, Naming, WriteMode,
	};
	use tauri::Error;

	// Based on https://docs.rs/tauri/2.0.0-alpha.10/src/tauri/path/desktop.rs.html#238-256
	#[cfg(target_os = "macos")]
	let path = dirs_next::home_dir()
		.ok_or(Error::UnknownPath)
		.map(|dir| dir.join("Library/Logs").join(&context.config().identifier));

	#[cfg(not(target_os = "macos"))]
	let path = dirs_next::data_dir()
		.ok_or(Error::UnknownPath)
		.map(|dir| dir.join(&context.config().identifier).join("logs"));

	Ok(Logger::try_with_env_or_str("info")?
		.log_to_file(FileSpec::default().directory(path.expect("We need a log dir")))
		.format_for_files(|w, now, record| util::logger_format(w, now, record, false))
		.format_for_stderr(|w, now, record| util::logger_format(w, now, record, true))
		.rotate(
			Criterion::Age(Age::Day),
			Naming::Timestamps,
			Cleanup::KeepLogFiles(2),
		)
		.duplicate_to_stderr(Duplicate::All)
		.write_mode(WriteMode::BufferAndFlush)
		.start()?)
}

#[cfg(windows)]
fn setup_webview2() -> Result<()> {
	use crate::util::webview2_exists;
	use win32job::{ExtendedLimitInfo, Job};

	let mut info = ExtendedLimitInfo::new();
	info.limit_kill_on_job_close();
	let job = Job::create_with_limit_info(&mut info).expect("Failed to create Job");
	job.assign_current_process()
		.expect("Failed to assign current process to Job");

	// We don't do anything with the job anymore, but we shouldn't drop it because that would
	// terminate our process tree. So we intentionally leak it instead.
	std::mem::forget(job);

	if !webview2_exists() {
		// This makes a dialog appear which let's you press Ok or Cancel
		// If you press Ok it will open the SlimeVR installer documentation
		use rfd::{MessageButtons, MessageDialog, MessageDialogResult, MessageLevel};

		let confirm = MessageDialog::new()
			.set_title("SlimeVR")
			.set_description("Couldn't find WebView2 installed. You can install it with the SlimeVR installer")
			.set_buttons(MessageButtons::OkCancel)
			.set_level(MessageLevel::Error)
			.show();
		if confirm == MessageDialogResult::Ok {
			open::that("https://docs.slimevr.dev/server-setup/installing-and-connecting.html#install-the-latest-slimevr-installer").unwrap();
		}
	}
	Ok(())
}

fn check_environment_variables() {
	use itertools::Itertools;
	const ENVS_TO_CHECK: &[&str] = &["_JAVA_OPTIONS", "JAVA_TOOL_OPTIONS"];
	let checked_envs = ENVS_TO_CHECK
		.into_iter()
		.filter_map(|e| {
			let Ok(data) = env::var(e) else {
				return None;
			};
			log::warn!("{e} is set to: {data}");
			Some(e)
		})
		.join(", ");

	if !checked_envs.is_empty() {
		rfd::MessageDialog::new()
			.set_title("SlimeVR")
			.set_description(format!("You have environment variables {} set, which may cause the SlimeVR Server to fail to launch properly.", checked_envs))
			.set_level(rfd::MessageLevel::Warning)
			.show();
	}
}

fn execute_server(
	cli: Cli,
) -> Result<Option<(std::ffi::OsString, std::path::PathBuf)>> {
	use const_format::formatcp;
	if let Some(p) = get_launch_path(cli) {
		log::info!("Server found on path: {}", p.to_str().unwrap());

		// Check if any Java already installed is compatible
		let jre = p.join("jre/bin").join(JAVA_BIN);
		let java_bin = jre
			.exists()
			.then(|| jre.into_os_string())
			.or_else(|| valid_java_paths().first().map(|x| x.0.to_owned()));
		let Some(java_bin) = java_bin else {
			show_error(formatcp!(
                "Couldn't find a compatible Java version, please download Java {} or higher",
                MINIMUM_JAVA_VERSION
            ));
			return Ok(None);
		};

		log::info!("Using Java binary: {:?}", java_bin);
		Ok(Some((java_bin, p)))
	} else {
		log::warn!("No server found. We will not start the server.");
		Ok(None)
	}
}

fn setup_tauri(
	context: tauri::Context,
	server_info: Option<(std::ffi::OsString, std::path::PathBuf)>,
	exit_flag: Arc<AtomicBool>,
	backend: Arc<Mutex<Option<CommandChild>>>,
) -> Result<tauri::App, tauri::Error> {
	let exit_flag_terminated = exit_flag.clone();
	tauri::Builder::default()
		.plugin(tauri_plugin_dialog::init())
		.plugin(tauri_plugin_fs::init())
		.plugin(tauri_plugin_os::init())
		.plugin(tauri_plugin_shell::init())
		.plugin(tauri_plugin_store::Builder::default().build())
		.invoke_handler(tauri::generate_handler![
			update_window_state,
			logging,
			erroring,
			warning,
			open_logs_folder,
			tray::update_translations,
			tray::update_tray_text,
			tray::is_tray_available,
			presence::discord_client_exists,
			presence::update_presence,
			presence::clear_presence,
			presence::create_discord_client,
		])
		.setup(move |app| {
			let window_state =
				WindowState::open_state(app.path().app_config_dir().unwrap())
					.unwrap_or_default();

			let window = tauri::WebviewWindowBuilder::new(
				app,
				"main",
				tauri::WebviewUrl::App("index.html".into()),
			)
			.title("SlimeVR")
			.inner_size(1289.0, 709.0)
			.min_inner_size(util::MIN_WINDOW_SIZE_WIDTH, util::MIN_WINDOW_SIZE_HEIGHT)
			.resizable(true)
			.visible(true)
			.decorations(false)
			.fullscreen(false)
			// This allows drag & drop via HTML5 for Windows
			.disable_drag_drop_handler()
			.build()?;
			if window_state.is_old() {
				window_state.update_window(&window.as_ref().window(), false)?;
			}

			if cfg!(desktop) {
				let handle = app.handle();
				tray::create_tray(handle)?;
				presence::create_presence(handle)?;
			} else {
				app.manage(tray::TrayAvailable(false));
			}

			app.manage(Mutex::new(window_state));

			if let Some((java_bin, p)) = server_info {
				let app_handle = app.app_handle().clone();
				tauri::async_runtime::spawn(async move {
					use tauri_plugin_shell::{process::CommandEvent, ShellExt};

					let (mut rx, child) = app_handle
						.shell()
						.command(java_bin.to_str().unwrap())
						.current_dir(p)
						.args(["-Xmx128M", "-jar", "slimevr.jar", "run"])
						.spawn()
						.expect("Unable to start the server jar");

					{
						let mut lock = backend.lock().unwrap();
						*lock = Some(child)
					}

					while let Some(cmd_event) = rx.recv().await {
						let emit_me = match cmd_event {
							CommandEvent::Stderr(v) => {
								("stderr", String::from_utf8(v).unwrap_or_default())
							}
							CommandEvent::Stdout(v) => {
								("stdout", String::from_utf8(v).unwrap_or_default())
							}
							CommandEvent::Error(s) => ("error", s),
							CommandEvent::Terminated(s) => {
								exit_flag_terminated.store(true, Ordering::Relaxed);
								("terminated", format!("{s:?}"))
							}
							_ => ("other", "".to_string()),
						};
						app_handle
							.emit("server-status", emit_me)
							.expect("Check server log files. \nFailed to emit");
					}
					log::error!("Java server receiver died");
					app_handle
						.emit("server-status", ("other", "receiver cancelled"))
						.expect("Failed to emit");
				});
			}
			Ok(())
		})
		.on_window_event(|w, e| match e {
			WindowEvent::CloseRequested { .. } => {
				let window_state = w.state::<Mutex<WindowState>>();
				if let Err(e) = update_window_state(w.clone(), window_state) {
					log::error!("failed to update window state {}", e)
				}
			}
			// See https://github.com/tauri-apps/tauri/issues/4012#issuecomment-1449499149
			// #[cfg(windows)]
			// WindowEvent::Resized(_) => std::thread::sleep(std::time::Duration::from_nanos(1)),
			_ => (),
		})
		.build(context)
}

fn tauri_build_result(
	build_result: Result<tauri::App, tauri::Error>,
	exit_flag: Arc<AtomicBool>,
	backend: Arc<Mutex<Option<CommandChild>>>,
) {
	match build_result {
		Ok(app) => {
			app.run(move |app_handle, event| match event {
				RunEvent::Exit => {
					let window_state = app_handle.state::<Mutex<WindowState>>();
					let lock = window_state.lock().unwrap();
					let config_dir = app_handle.path().app_config_dir().unwrap();
					let window_state_res = lock.save_state(config_dir);
					match window_state_res {
						Ok(()) => log::info!("saved window state"),
						Err(e) => log::error!("failed to save window state: {}", e),
					}

					let mut lock = backend.lock().unwrap();
					let Some(ref mut child) = *lock else { return };
					let write_result = child.write(b"exit\n");
					match write_result {
						Ok(()) => log::info!("send exit to backend"),
						Err(_) => log::error!("fail to send exit to backend"),
					}
					let ten_seconds = Duration::from_secs(10);
					let start_time = Instant::now();
					while start_time.elapsed() < ten_seconds {
						if exit_flag.load(Ordering::Relaxed) {
							break;
						}
						thread::sleep(Duration::from_secs(1));
					}
				}
				_ => {}
			});
		}
		#[cfg(windows)]
		// Often triggered when the user doesn't have webview2 installed
		Err(tauri::Error::Runtime(tauri_runtime::Error::CreateWebview(error))) => {
			// I should log this anyways, don't want to dig a grave by not logging the error.
			log::error!("CreateWebview error {}", error);

			use rfd::{
				MessageButtons, MessageDialog, MessageDialogResult, MessageLevel,
			};

			let confirm = MessageDialog::new()
				.set_title("SlimeVR")
				.set_description("You seem to have a faulty installation of WebView2. You can check a guide on how to fix that in the docs!")
				.set_buttons(MessageButtons::OkCancel)
				.set_level(MessageLevel::Error)
				.show();
			if confirm == MessageDialogResult::Ok {
				open::that("https://docs.slimevr.dev/common-issues.html#webview2-is-missing--slimevr-gui-crashes-immediately--panicked-at--webview2error").unwrap();
			}
		}
		Err(error) => {
			log::error!("tauri build error {}", error);
			show_error(&error.to_string());
		}
	}
}
