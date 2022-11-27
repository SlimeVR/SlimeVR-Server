#![cfg_attr(all(not(debug_assertions), windows), windows_subsystem = "windows")]
use std::env;
use std::ffi::{OsStr, OsString};
use std::io::Write;
use std::panic;
use std::path::PathBuf;
use std::process::{Child, Stdio};
use std::str::FromStr;

use clap::Parser;
use clap_verbosity_flag::{InfoLevel, Verbosity};
use rand::{seq::SliceRandom, thread_rng};
use tauri::api::{clap, process::Command};
use tauri::Manager;
use tempfile::Builder;
use which::which_all;

const MINIMUM_JAVA_VERSION: i32 = 17;
static POSSIBLE_TITLES: &[&str] = &[
	"Panicking situation",
	"looking for spatula",
	"never gonna give you up",
	"never gonna let you down",
	"uwu sowwy",
];

#[derive(Parser)]
#[clap(version, about)]
struct Cli {
	#[clap(short, long)]
	display_console: bool,
	#[clap(long)]
	launch_from_path: Option<PathBuf>,
	#[clap(flatten)]
	verbosity: Verbosity<InfoLevel>,
}

fn is_valid_path(path: &PathBuf) -> bool {
	// Might need to be changed in the future, at least for linux
	let server_path = path.join("slimevr.jar");

	return server_path.exists();
}

fn get_launch_path(cli: Cli) -> Option<PathBuf> {
	let mut path = cli.launch_from_path.unwrap_or_default();
	if path.exists() && is_valid_path(&path) {
		return Some(path);
	}

	path = env::current_dir().unwrap();
	if path.exists() && is_valid_path(&path) {
		return Some(path);
	}

	path = PathBuf::from(env!("CARGO_MANIFEST_DIR"));
	if path.exists() && is_valid_path(&path) {
		return Some(path);
	}

	None
}

fn spawn_java(java: &OsStr, java_version: &OsStr) -> std::io::Result<Child> {
	std::process::Command::new(java)
		.arg(java_version)
		.stdin(Stdio::null())
		.stderr(Stdio::null())
		.stdout(Stdio::null())
		.spawn()
}

#[cfg(desktop)]
fn show_error(text: &str) -> bool {
	use tauri::api::dialog::{
		blocking::MessageDialogBuilder, MessageDialogButtons, MessageDialogKind,
	};

	MessageDialogBuilder::new(
		format!(
			"SlimeVR GUI crashed - {}",
			POSSIBLE_TITLES.choose(&mut thread_rng()).unwrap()
		),
		text,
	)
	.buttons(MessageDialogButtons::Ok)
	.kind(MessageDialogKind::Error)
	.show()
}

#[cfg(mobile)]
fn show_error(text: &str) -> bool {
	// needs to do native stuff on mobile
	false
}

fn main() {
	// Make an error dialog box when panicking
	panic::set_hook(Box::new(|panic_info| {
		eprintln!("{}", panic_info);
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
	#[cfg(windows)]
	{
		use win32job::{ExtendedLimitInfo, Job};

		let mut info = ExtendedLimitInfo::new();
		info.limit_kill_on_job_close();
		let job = Job::create_with_limit_info(&mut info).expect("Failed to create Job");
		job.assign_current_process()
			.expect("Failed to assign current process to Job");

		// We don't do anything with the job anymore, but we shouldn't drop it because that would
		// terminate our process tree. So we intentionally leak it instead.
		std::mem::forget(job);
	}

	// Spawn server process
	let run_path = get_launch_path(cli);

	let stdout_recv = if let Some(p) = run_path {
		log::info!("Server found on path: {}", p.to_str().unwrap());

		// Check if any Java already installed is compatible
		let java_paths = valid_java_paths();
		let java_bin =
			java_paths
				.first()
				.map(|x| x.0.to_string_lossy())
				.or_else(|| {
					let java = p.join("jre/bin/java");
					if java.exists() {
						Some(java.to_string_lossy())
					} else {
						None
					}
				});
		if let None = java_bin {
			show_error(&format!("Couldn't find a compatible Java version, please download Java {} or higher", MINIMUM_JAVA_VERSION));
			return;
		}

		let (recv, _child) = Command::new(java_bin.unwrap())
			.current_dir(p)
			.args(["-Xmx512M", "-jar", "slimevr.jar", "--no-gui"])
			.spawn()
			.expect("Unable to start the server jar");
		Some(recv)
	} else {
		log::warn!("No server found. We will not start the server.");
		None
	};

	tauri::Builder::default()
		.plugin(tauri_plugin_window_state::Builder::default().build())
		.setup(|app| {
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
								("terminated", format!("{s:?}"))
							}
							_ => ("other", "".to_string()),
						};
						app_handle
							.emit_all("server-status", emit_me)
							.expect("Failed to emit");
					}
					app_handle
						.emit_all("server-status", ("other", "receiver cancelled"))
						.expect("Failed to emit");
				});
			}
			Ok(())
		})
		//
		.run(tauri::generate_context!())
		.expect("error while running tauri application");
}

fn valid_java_paths() -> Vec<(OsString, i32)> {
	let mut file = Builder::new()
		.suffix(".java")
		.tempfile()
		.expect("Couldn't generate .java file");
	file.write_all(include_bytes!("JavaVersion.java"))
		.expect("Couldn't write to .java file");
	let java_version = file.into_temp_path();

	// Check if main Java is a supported version
	let main_java = if let Ok(java_home) = std::env::var("JAVA_HOME") {
		PathBuf::from(java_home).join("bin/java").into_os_string()
	} else {
		OsString::from_str("java").unwrap()
	};
	if let Some(main_child) = spawn_java(&main_java, java_version.as_os_str())
		.expect("Couldn't spawn the main Java binary")
		.wait()
		.expect("Couldn't execute the main Java binary")
		.code()
	{
		if main_child >= MINIMUM_JAVA_VERSION {
			return vec![(main_java, main_child)];
		}
	}

	// Otherwise check if anything else is a supported version
	let mut childs = vec![];
	for java in which_all("java").unwrap() {
		let res = spawn_java(java.as_os_str(), java_version.as_os_str());

		match res {
			Ok(child) => childs.push((java.into_os_string(), child)),
			Err(e) => eprintln!("Error on trying to spawn a Java executable: {}", e),
		}
	}

	childs
		.into_iter()
		.filter_map(|(p, mut c)| {
			c.wait()
				.expect("Failed on executing a Java executable")
				.code()
				.map(|code| (p, code))
				.filter(|(_p, code)| *code >= MINIMUM_JAVA_VERSION)
		})
		.collect()
}
