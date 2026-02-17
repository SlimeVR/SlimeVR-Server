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

          electronLibs = with pkgs; [
            alsa-lib
            at-spi2-atk
            at-spi2-core
            cairo
            cups
            dbus
            expat
            gdk-pixbuf
            glib
            gtk3
            libdrm
            libgbm
            libglvnd
            libnotify
            libxkbcommon
            mesa
            nspr
            nss
            pango
            systemd
            vulkan-loader
            wayland
            xorg.libX11
            xorg.libXcomposite
            xorg.libXdamage
            xorg.libXext
            xorg.libXfixes
            xorg.libXrandr
            xorg.libxcb
            xorg.libxshmfence
          ];

          # Shared patching logic
          patchLogic = ''
            echo "Patching SlimeVR binary for NixOS..."

            # Patch the main binary with correct interpreter and RPATH
            ${pkgs.patchelf}/bin/patchelf \
              --set-interpreter ${pkgs.glibc}/lib/ld-linux-x86-64.so.2 \
              --set-rpath "\$ORIGIN:${pkgs.lib.makeLibraryPath (electronLibs ++ hw_deps)}" \
              "$UNPACKED_DIR/slimevr"

            # Patch chrome-sandbox
            if [ -f "$UNPACKED_DIR/chrome-sandbox" ]; then
              ${pkgs.patchelf}/bin/patchelf \
                --set-interpreter ${pkgs.glibc}/lib/ld-linux-x86-64.so.2 \
                "$UNPACKED_DIR/chrome-sandbox"
            fi

            # Patch bundled shared libraries
            for lib in "$UNPACKED_DIR"/*.so; do
              if [ -f "$lib" ]; then
                echo "Patching $(basename "$lib")..."
                ${pkgs.patchelf}/bin/patchelf \
                  --set-rpath "\$ORIGIN:${pkgs.lib.makeLibraryPath electronLibs}" \
                  "$lib" 2>/dev/null || true
              fi
            done

            echo "✓ Binary patched successfully!"
          '';

          patchScript = pkgs.writeShellScriptBin "patch-slimevr" ''
            set -e
            cd "$(git rev-parse --show-toplevel)"
            UNPACKED_DIR="./gui/dist/artifacts/linux-unpacked"

            if [ ! -d "$UNPACKED_DIR" ]; then
              echo "Error: Build artifacts not found at $UNPACKED_DIR"
              echo "Please run 'pnpm run package' first"
              exit 1
            fi

            ${patchLogic}
            echo ""
            echo "You can now run: cd $UNPACKED_DIR && ./slimevr"
          '';

          buildAndPatchScript = pkgs.writeShellScriptBin "build-and-patch-slimevr" ''
            set -e
            cd "$(git rev-parse --show-toplevel)/gui"

            echo "Building Electron app..."
            ${pkgs.pnpm}/bin/pnpm run package

            cd ..
            UNPACKED_DIR="./gui/dist/artifacts/linux-unpacked"

            ${patchLogic}
            echo ""
            echo "You can now run: cd $UNPACKED_DIR && ./slimevr"
          '';

          buildPatchAndRunScript = pkgs.writeShellScriptBin "build-patch-run-slimevr" ''
            set -e
            cd "$(git rev-parse --show-toplevel)/gui"

            echo "Building Electron app..."
            ${pkgs.pnpm}/bin/pnpm run package

            cd ..
            UNPACKED_DIR="./gui/dist/artifacts/linux-unpacked"

            ${patchLogic}
            echo ""
            echo "Launching SlimeVR..."
            cd "$UNPACKED_DIR"
            export LD_LIBRARY_PATH="${pkgs.lib.makeLibraryPath (electronLibs ++ hw_deps)}:$LD_LIBRARY_PATH"
            exec ./slimevr
          '';
        in
        {
          packages = {
            patch-script = patchScript;
            build-and-patch = buildAndPatchScript;
            build-patch-run = buildPatchAndRunScript;
            default = patchScript;
          };

          devShells.default = pkgs.mkShell {
            name = "slimevr-electron";

            buildInputs = with pkgs; [
              jdk17
              kotlin
              nodejs_22
              pnpm
              pkg-config
              targetElectron
              wine
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
