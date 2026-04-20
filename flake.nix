{
  description = "SlimeVR Server & GUI";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
  };

  outputs = inputs@{ self, nixpkgs, flake-parts, ... }:
    flake-parts.lib.mkFlake { inherit inputs; } {
      systems = [ "x86_64-linux" ];

      perSystem = { pkgs, ... }:
        let
          runtimeLibs = pkgs: (with pkgs; [
            jdk17

            # Audio
            alsa-lib libpulseaudio

            # Electron / Chromium runtime
            at-spi2-atk at-spi2-core cairo cups dbus expat
            gdk-pixbuf glib gtk3 libdrm libgbm libglvnd libnotify
            libxkbcommon mesa nspr nss pango systemd vulkan-loader
            wayland xorg.libX11 xorg.libXcomposite xorg.libXdamage
            xorg.libXext xorg.libXfixes xorg.libXrandr xorg.libxcb
            xorg.libxshmfence libusb1 udev libxcrypt-legacy

            # Packaging tools (electron-builder)
            rpm fpm zlib squashfsTools fakeroot libarchive icu
            wineWow64Packages.stable

            # Build tools
            nodejs_22 pnpm_9 pkg-config python3 gcc gnumake binutils git
            pkgs.nodePackages.node-gyp-build
          ]);

          slimeShell = pkgs.buildFHSEnv {
            name = "slimevr-env";
            targetPkgs = runtimeLibs;
            profile = ''
              export JAVA_HOME=${pkgs.jdk17}
              export PATH="${pkgs.jdk17}/bin:$PATH"

              # Tell electron-builder to use system tools instead of downloading them
              export USE_SYSTEM_FPM=true
              export USE_SYSTEM_MKSQUASHFS=true

              # Prevent electron from re-downloading itself
              export ELECTRON_SKIP_BINARY_DOWNLOAD=1

              # Wayland support for Electron (mirrors nixpkgs slimevr package)
              if [ -n "$NIXOS_OZONE_WL" ] && [ -n "$WAYLAND_DISPLAY" ]; then
                export ELECTRON_OZONE_PLATFORM_HINT=auto
              fi
            '';
            runScript = "bash";
          };
        in
        {
          devShells.default = slimeShell.env;
        };
    };
}
