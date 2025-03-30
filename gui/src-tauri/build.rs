use cfg_aliases::cfg_aliases;

fn main() -> shadow_rs::SdResult<()> {
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
