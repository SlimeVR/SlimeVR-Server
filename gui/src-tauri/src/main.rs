#![cfg_attr(all(not(debug_assertions), windows), windows_subsystem = "windows")]
use std::env;
use std::ffi::{OsStr, OsString};
use std::io::Write;
#[cfg(windows)]
use std::os::windows::process::CommandExt;
use std::panic;
use std::path::{Path, PathBuf};
use std::process::{Child, Stdio};

use clap::Parser;
use const_format::concatcp;
use rand::{seq::SliceRandom, thread_rng};
use shadow_rs::shadow;
use tauri::api::process::Command;
use tauri::Manager;
use tempfile::Builder;

#[cfg(windows)]
/// For Commands on Windows so they dont create terminals
const CREATE_NO_WINDOW: u32 = 0x0800_0000;
/// It's an i32 because we check it through exit codes of the process
const MINIMUM_JAVA_VERSION: i32 = 17;
const JAVA_BIN: &str = if cfg!(windows) { "java.exe" } else { "java" };
static POSSIBLE_TITLES: &[&str] = &[
	"Panicking situation",
	"looking for spatula",
	"never gonna give you up",
	"never gonna let you down",
	"uwu sowwy",
];
shadow!(build);
// Tauri has a way to return the package.json version, but it's not a constant...
const VERSION: &str = if build::TAG.is_empty() {
	build::SHORT_COMMIT
} else {
	build::TAG
};
const MODIFIED: &str = if build::GIT_CLEAN { "" } else { "-dirty" };

#[derive(Debug, Parser)]
#[clap(
	version = concatcp!(VERSION, MODIFIED),
	about
)]
struct Cli {
	#[clap(short, long)]
	display_console: bool,
	#[clap(long)]
	launch_from_path: Option<PathBuf>,
	#[clap(flatten)]
	verbose: clap_verbosity_flag::Verbosity,
}

fn is_valid_path(path: &Path) -> bool {
	path.join("slimevr.jar").exists()
}

fn get_launch_path(cli: Cli) -> Option<PathBuf> {
	let paths = [
		cli.launch_from_path,
		// AppImage passes the fakeroot in `APPDIR` env var.
		env::var_os("APPDIR").map(|x| PathBuf::from(x)),
		env::current_dir().ok(),
		Some(PathBuf::from(env!("CARGO_MANIFEST_DIR"))),
		Some(PathBuf::from("/usr/share/slimevr/")),
	];
	paths
		.into_iter()
		.filter_map(|x| x)
		.find(|x: &PathBuf| is_valid_path(x))
}

fn spawn_java(java: &OsStr, java_version: &OsStr) -> std::io::Result<Child> {
	let mut cmd = std::process::Command::new(java);

	#[cfg(windows)]
	cmd.creation_flags(CREATE_NO_WINDOW);

	cmd.arg("-jar")
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
		let (recv, _child) = Command::new(java_bin.to_str().unwrap())
			.current_dir(p)
			.args(["-Xmx512M", "-jar", "slimevr.jar", "--no-gui"])
			.spawn()
			.expect("Unable to start the server jar");
		Some(recv)
	} else {
		log::warn!("No server found. We will not start the server.");
		None
	};

	let builder = tauri::Builder::default();
	#[cfg(not(target_os = "macos"))]
	let builder = builder.plugin(tauri_plugin_window_state::Builder::default().build());
	builder
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
					log::error!("Java server receiver died");
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

#[cfg(windows)]
/// Check if WebView2 exists
fn webview2_exists() -> bool {
	use winreg::enums::*;
	use winreg::RegKey;

	// First on the machine itself
	let machine: Option<String> = RegKey::predef(HKEY_LOCAL_MACHINE)
		.open_subkey(r"SOFTWARE\WOW6432Node\Microsoft\EdgeUpdate\Clients\{F3017226-FE2A-4295-8BDF-00C3A9A7E4C5}")
		.map(|r| r.get_value("pv").ok()).ok().flatten();
	let mut exists = false;
	if let Some(version) = machine {
		exists = version.split('.').any(|x| x != "0");
	}
	// Then in the current user
	if !exists {
		let user: Option<String> = RegKey::predef(HKEY_CURRENT_USER)
			.open_subkey(
				r"Software\Microsoft\EdgeUpdate\Clients\{F3017226-FE2A-4295-8BDF-00C3A9A7E4C5}",
			)
			.map(|r| r.get_value("pv").ok())
			.ok()
			.flatten();
		if let Some(version) = user {
			exists = version.split('.').any(|x| x != "0");
		}
	}
	exists
}

fn valid_java_paths() -> Vec<(OsString, i32)> {
	let mut file = Builder::new()
		.suffix(".jar")
		.tempfile()
		.expect("Couldn't generate .jar file");
	file.write_all(include_bytes!("JavaVersion.jar"))
		.expect("Couldn't write to .jar file");
	let java_version = file.into_temp_path();

	// Check if main Java is a supported version
	let main_java = if let Ok(java_home) = std::env::var("JAVA_HOME") {
		PathBuf::from(java_home)
			.join("bin")
			.join(JAVA_BIN)
			.into_os_string()
	} else {
		JAVA_BIN.into()
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
	cfg_if::cfg_if! {
		if #[cfg(target_os = "macos")] {
			// TODO: Actually use macOS paths
			let libs = which::which_all(JAVA_BIN).unwrap();
		} else if #[cfg(unix)] {
			// Linux JVMs are saved on /usr/lib/jvm from what I found out,
			// there is usually a default dir and a default-runtime dir also which are linked
			// to the current default runtime and the current default JDK (I think it's JDK)
			let libs = glob::glob(concatcp!("/usr/lib/jvm/*/bin/", JAVA_BIN))
				.unwrap()
				.filter_map(|res| res.ok());
		} else {
			let libs = which::which_all(JAVA_BIN).unwrap();
		}
	}

	for java in libs {
		let res = spawn_java(java.as_os_str(), java_version.as_os_str());

		match res {
			Ok(child) => childs.push((java.into_os_string(), child)),
			Err(e) => println!("Error on trying to spawn a Java executable: {}", e),
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
