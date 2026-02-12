{
  description = "Affordable full-body tracking for VR!";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
  };

  outputs =
    inputs@{ self, nixpkgs, flake-parts, ... }:
    flake-parts.lib.mkFlake { inherit inputs; } {
      systems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];

      perSystem = { system, lib, pkgs, ... }:
        let
          targetElectron = pkgs.electron;

          hw_deps = with pkgs; [
            udev
            libusb1
          ];

          build_tools = with pkgs; [
            p7zip
            fpm
            squashfsTools
            rpm
            desktop-file-utils # Required for mksquashfs validation
          ];
        in
        {
          devShells.default = pkgs.mkShell {
            name = "slimevr-electron";

            buildInputs = with pkgs; [
              nodejs_22
              pnpm
              pkg-config
              targetElectron
            ] ++ hw_deps
              ++ build_tools;

            shellHook = ''
              # 1. Standard Electron Overrides
              export ELECTRON_SKIP_BINARY_DOWNLOAD=1
              export ELECTRON_OVERRIDE_DIST_PATH="${targetElectron}/lib/electron"
              export ELECTRON_PATH="${targetElectron}/bin/electron"

              # 2. Fix broken local Electron binary
              LOCAL_ELECTRON="./node_modules/electron/dist/electron"
              if [ -f "$LOCAL_ELECTRON" ] && [ ! -L "$LOCAL_ELECTRON" ]; then
                rm "$LOCAL_ELECTRON"
                ln -s "${targetElectron}/bin/electron" "$LOCAL_ELECTRON"
              fi

              # 3. FIX FOR ELECTRON-BUILDER ON NIXOS (The AppImage Tools Hack)
              # Define where electron-builder stores its downloaded tools
              EB_CACHE="$HOME/.cache/electron-builder/appimage/appimage-12.0.1/linux-x64"
              mkdir -p "$EB_CACHE"

              # Symlink Nix-native tools over the broken ones
              ln -sf "${pkgs.squashfsTools}/bin/mksquashfs" "$EB_CACHE/mksquashfs"
              ln -sf "${pkgs.desktop-file-utils}/bin/desktop-file-validate" "$EB_CACHE/desktop-file-validate"

              # General system binary overrides
              export USE_SYSTEM_7ZA=true
              export USE_SYSTEM_FPM=true
              export ELECTRON_BUILDER_BINARIES_7ZA_PATH="${pkgs.p7zip}/bin/7za"

              # 4. Hardware/Linker paths
              export PKG_CONFIG_PATH="${pkgs.lib.makeSearchPath "lib/pkgconfig" hw_deps}:$PKG_CONFIG_PATH"
              export LD_LIBRARY_PATH="${pkgs.lib.makeLibraryPath (hw_deps ++ build_tools)}:$LD_LIBRARY_PATH"

              echo "--- SlimeVR Nix Environment ---"
              echo "Electron: $(electron --version)"
              echo "AppImage tools patched for NixOS."
            '';
          };
        };
    };
}
