fn main() -> shadow_rs::SdResult<()> {
	// Bypass for Nix script having libudev-zero and Tauri not liking it
	if let Some(path) = option_env!("SLIMEVR_RUST_LD_LIBRARY_PATH") {
		println!("cargo:rustc-env=LD_LIBRARY_PATH={path}");
	}

	tauri_build::build();
	shadow_rs::new()
}
