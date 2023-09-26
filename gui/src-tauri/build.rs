use std::{fs::File, io::Write};

use cfg_aliases::cfg_aliases;
use const_gen::*;

fn main() -> shadow_rs::SdResult<()> {
	println!("cargo:rerun-if-changed=../../server/desktop/build/libs/slimevr.jar");
	tauri_build::build();
	cfg_aliases! {
		mobile: { any(target_os = "ios", target_os = "android") },
		desktop: { not(any(target_os = "ios", target_os = "android")) }
	}

	shadow_rs::new_hook(append_shadow_const)
}

fn append_shadow_const(mut file: &File) -> shadow_rs::SdResult<()> {
	let hash: [u8; 32] = {
		use sha2::{Digest, Sha256};
		let mut hasher = Sha256::new();
		hasher.update(include_bytes!(
			"../../server/desktop/build/libs/slimevr.jar"
		));
		hasher.finalize().into()
	};
	let const_declarations =
		vec![const_declaration!(pub ORIGINAL_SERVER_HASH = hash)].join("\n");
	writeln!(file, "{}", const_declarations)?;

	Ok(())
}
