#[cfg(windows)]
use std::os::windows::process::CommandExt;
use std::{
	env,
	ffi::{OsStr, OsString},
	io::Write,
	path::{Path, PathBuf},
	process::{Child, Stdio},
};

use clap::Parser;
use const_format::concatcp;
use flexi_logger::{style, DeferredNow};
use log::Record;
use shadow_rs::shadow;
use tempfile::Builder;

#[cfg(windows)]
/// For Commands on Windows so they dont create terminals
const CREATE_NO_WINDOW: u32 = 0x0800_0000;
/// It's an i32 because we check it through exit codes of the process
pub const MINIMUM_JAVA_VERSION: i32 = 17;
pub const JAVA_BIN: &str = if cfg!(windows) { "java.exe" } else { "java" };
pub static POSSIBLE_TITLES: &[&str] = &[
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
pub struct Cli {
	#[clap(short, long)]
	display_console: bool,
	#[clap(long)]
	launch_from_path: Option<PathBuf>,
	#[clap(flatten)]
	verbose: clap_verbosity_flag::Verbosity,
}

pub fn is_valid_path(path: &Path) -> bool {
	path.join("slimevr.jar").exists()
}

pub fn get_launch_path(cli: Cli) -> Option<PathBuf> {
	let paths = [
		cli.launch_from_path,
		// AppImage passes the fakeroot in `APPDIR` env var.
		env::var_os("APPDIR").map(PathBuf::from),
		env::current_dir().ok(),
		// getcwd in Mac can't be trusted, so let's get the executable's path
		env::current_exe()
			.map(|mut f| {
				f.pop();
				f
			})
			.ok(),
		Some(PathBuf::from(env!("CARGO_MANIFEST_DIR"))),
		// For flatpak container
		Some(PathBuf::from("/app/share/slimevr/")),
		Some(PathBuf::from("/usr/share/slimevr/")),
	];

	paths.into_iter().flatten().find(|x| is_valid_path(x))
}

pub fn spawn_java(java: &OsStr, java_version: &OsStr) -> std::io::Result<Child> {
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
pub fn show_error(text: &str) -> bool {
	use rand::{seq::SliceRandom, thread_rng};
	use rfd::{MessageButtons, MessageDialog, MessageLevel};

	MessageDialog::new()
		.set_title(&format!(
			"SlimeVR GUI crashed - {}",
			POSSIBLE_TITLES.choose(&mut thread_rng()).unwrap()
		))
		.set_description(text)
		.set_buttons(MessageButtons::Ok)
		.set_level(MessageLevel::Error)
		.show()
}

#[cfg(mobile)]
pub fn show_error(text: &str) -> bool {
	// needs to do native stuff on mobile
	false
}

#[cfg(windows)]
/// Check if WebView2 exists
pub fn webview2_exists() -> bool {
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

pub fn valid_java_paths() -> Vec<(OsString, i32)> {
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
			// macOS JVMs are saved on multiple possible places,
			// /Library/Java/JavaVirtualMachines are the ones installed by an admin
			// /Users/$USER/Library/Java/JavaVirtualMachines are the ones installed locally by the user
			let libs = glob::glob(concatcp!("/Library/Java/JavaVirtualMachines/*/Contents/Home/bin/", JAVA_BIN))
				.unwrap()
				.filter_map(|res| res.ok());
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

pub fn logger_format(
	w: &mut dyn std::io::Write,
	_now: &mut DeferredNow,
	record: &Record,
) -> Result<(), std::io::Error> {
	let level = record.level();
	let module_path = record.module_path().unwrap_or("<unnamed>");
	// optionally print target
	let target = if module_path.starts_with(record.target()) {
		"".to_string()
	} else {
		format!(", {}", record.target())
	};
	write!(
		w,
		"{} [{}{target}] {}",
		style(level).paint(level.to_string()),
		record.module_path().unwrap_or("<unnamed>"),
		style(level).paint(record.args().to_string())
	)
}
