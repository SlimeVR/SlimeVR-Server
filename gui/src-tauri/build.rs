use std::process;

use cfg_aliases::cfg_aliases;

fn main() -> shadow_rs::SdResult<()> {
	println!("cargo:rerun-if-changed=.git/HEAD");
	println!("cargo:rerun-if-changed=.git/index");

	let commit_hash = process::Command::new("git")
		.args(&["rev-parse", "--verify", "--short", "HEAD"])
		.output()
		.map(|output| {
			if output.status.success() {
				String::from_utf8_lossy(&output.stdout).trim().to_string()
			} else {
				eprintln!("Warning: Failed to get git commit hash: {:?}", output);
				"unknown_commit".to_string()
			}
		})
		.unwrap_or_else(|e| {
			eprintln!("Warning: Could not run 'git rev-parse': {}", e);
			"no_git_commit".to_string()
		});

	let version_tag = process::Command::new("git")
		.args(&[
			"--no-pager",
			"tag",
			"--sort",
			"-taggerdate",
			"--points-at",
			"HEAD",
		])
		.output()
		.map(|output| {
			if output.status.success() {
				String::from_utf8_lossy(&output.stdout)
					.split('\n')
					.next()
					.unwrap_or("")
					.trim()
					.to_string()
			} else {
				eprintln!("Warning: Failed to get git tag: {:?}", output);
				"".to_string()
			}
		})
		.unwrap_or_else(|e| {
			eprintln!("Warning: Could not run 'git tag': {}", e);
			"".to_string()
		});

	let git_status_output = process::Command::new("git")
		.args(&["status", "--porcelain"])
		.output()
		.map(|output| {
			if output.status.success() {
				String::from_utf8_lossy(&output.stdout).trim().to_string()
			} else {
				eprintln!("Warning: Failed to get git status: {:?}", output);
				"error".to_string()
			}
		})
		.unwrap_or_else(|e| {
			eprintln!("Warning: Could not run 'git status': {}", e);
			"error".to_string()
		});

	let git_clean = git_status_output.is_empty();

	if !git_clean {
		println!(
			"cargo:warning=Git is dirty because of:\n'{}'",
			git_status_output
		);
	}

	let mut version = if !version_tag.is_empty() {
		version_tag
	} else {
		commit_hash
	};

	if !git_clean {
		version.push_str("-dirty");
	}

	println!("cargo:warning=Version is {}", version);
	println!("cargo:rustc-env=SLIMEVR_SERVER_VERSION={}", version);

	// Bypass for Nix script having libudev-zero and Tauri not liking it
	if let Some(path) = option_env!("SLIMEVR_RUST_LD_LIBRARY_PATH") {
		println!("cargo:rustc-env=LD_LIBRARY_PATH={path}");
	}

	tauri_build::build();
	cfg_aliases! {
		mobile: { any(target_os = "ios", target_os = "android") },
		desktop: { not(any(target_os = "ios", target_os = "android")) }
	}
	shadow_rs::new()
}
