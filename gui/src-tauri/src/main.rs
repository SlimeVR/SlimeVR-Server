#![cfg_attr(all(not(debug_assertions), windows), windows_subsystem = "windows")]
use std::env;
use std::panic;
use std::sync::atomic::AtomicBool;
use std::sync::atomic::Ordering;
use std::sync::Arc;
use std::thread;
use std::time::Duration;
use std::time::Instant;

use clap::Parser;
use tauri::api::process::{Command, CommandChild};
use tauri::Manager;
use tauri::RunEvent;

#[cfg(windows)]
use tauri::WindowEvent;

use crate::util::{
	get_launch_path, show_error, valid_java_paths, Cli, JAVA_BIN, MINIMUM_JAVA_VERSION,
};

mod util;

fn main() {
	// Make an error dialog box when panicking
	panic::set_hook(Box::new(|panic_info| {
		println!("{}", panic_info);
		show_error(&panic_info.to_string());
	}));

	let cli = Cli::parse();

	// Set up loggers and global handlers
	{
		if std::env::var_os("RUST_LOG").is_none() {
			std::env::set_var("RUST_LOG", "info")
		}
		pretty_env_logger::init();
	}

	// Ensure child processes die when spawned on windows
	// and then check for WebView2's existence
	#[cfg(windows)]
	{
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
			use tauri::api::dialog::{
				blocking::MessageDialogBuilder, MessageDialogButtons, MessageDialogKind,
			};

			let confirm = MessageDialogBuilder::new("SlimeVR", "Couldn't find WebView2 installed. You can install it with the SlimeVR installer")
				.buttons(MessageDialogButtons::OkCancel)
				.kind(MessageDialogKind::Error)
				.show();
			if confirm {
				open::that("https://docs.slimevr.dev/server-setup/installing-and-connecting.html#install-the-latest-slimevr-installer").unwrap();
			}
			return;
		}
	}

	// Spawn server process
	let exit_flag = Arc::new(AtomicBool::new(false));
	let mut backend: Option<CommandChild> = None;
	let run_path = get_launch_path(cli);

	let stdout_recv = if let Some(p) = run_path {
		log::info!("Server found on path: {}", p.to_str().unwrap());

		// Check if any Java already installed is compatible
		let jre = p.join("jre/bin").join(JAVA_BIN);
		let java_bin = jre
			.exists()
			.then(|| jre.into_os_string())
			.or_else(|| valid_java_paths().first().map(|x| x.0.to_owned()));
		let Some(java_bin) = java_bin else {
			show_error(&format!("Couldn't find a compatible Java version, please download Java {} or higher", MINIMUM_JAVA_VERSION));
			return;
		};

		log::info!("Using Java binary: {:?}", java_bin);
		let (recv, child) = Command::new(java_bin.to_str().unwrap())
			.current_dir(p)
			.args(["-Xmx512M", "-jar", "slimevr.jar", "run"])
			.spawn()
			.expect("Unable to start the server jar");
		backend = Some(child);
		Some(recv)
	} else {
		log::warn!("No server found. We will not start the server.");
		None
	};

	let exit_flag_terminated = exit_flag.clone();
	let build_result = tauri::Builder::default()
		.plugin(tauri_plugin_window_state::Builder::default().build())
		.setup(move |app| {
			if let Some(mut recv) = stdout_recv {
				let app_handle = app.app_handle();
				tauri::async_runtime::spawn(async move {
					use tauri::api::process::CommandEvent;

					while let Some(cmd_event) = recv.recv().await {
						let emit_me = match cmd_event {
							CommandEvent::Stderr(s) => ("stderr", s),
							CommandEvent::Stdout(s) => ("stdout", s),
							CommandEvent::Error(s) => ("error", s),
							CommandEvent::Terminated(s) => {
								exit_flag_terminated.store(true, Ordering::Relaxed);
								("terminated", format!("{s:?}"))
							}
							_ => ("other", "".to_string()),
						};
						app_handle
							.emit_all("server-status", emit_me)
							.expect("Failed to emit");
					}
					log::error!("Java server receiver died");
					app_handle
						.emit_all("server-status", ("other", "receiver cancelled"))
						.expect("Failed to emit");
				});
			}
			Ok(())
		})
		.on_window_event(|e| match e.event() {
			// See https://github.com/tauri-apps/tauri/issues/4012#issuecomment-1449499149
			#[cfg(windows)]
			WindowEvent::Resized(_) => std::thread::sleep(std::time::Duration::from_nanos(1)),
			_ => (),
		})
		.build(tauri::generate_context!());
	match build_result {
		Ok(app) => {
			app.run(move |_app_handle, event| match event {
				RunEvent::ExitRequested { .. } => {
					let Some(ref mut child) = backend else { return };
					let write_result = child.write(b"exit\n");
					match write_result {
						Ok(()) => log::info!("send exit to backend"),
						Err(_) => log::info!("fail to send exit to backend"),
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

			use tauri::api::dialog::{
				blocking::MessageDialogBuilder, MessageDialogButtons, MessageDialogKind,
			};

			let confirm = MessageDialogBuilder::new("SlimeVR", "You seem to have a faulty installation of WebView2. You can check a guide on how to fix that in the docs!")
				.buttons(MessageDialogButtons::OkCancel)
				.kind(MessageDialogKind::Error)
				.show();
			if confirm {
				open::that("https://docs.slimevr.dev/common-issues.html#webview2-is-missing--slimevr-gui-crashes-immediately--panicked-at--webview2error").unwrap();
			}
		}
		Err(error) => {
			log::error!("tauri build error {}", error);
			show_error(&error.to_string());
		}
	}
}
