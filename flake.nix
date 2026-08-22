{
  description = "SlimeVR Server & GUI";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
  };

  outputs =
    inputs@{
      self,
      nixpkgs,
      flake-parts,
      ...
    }:
    flake-parts.lib.mkFlake { inherit inputs; } {
      systems = [
        "aarch64-linux"
        "x86_64-linux"
      ];

      perSystem =
        {
          system,
          lib,
          pkgs,
          ...
        }:
        let
          runtimeLibs = [
            pkgs.alsa-lib
            pkgs.libpulseaudio
            pkgs.at-spi2-atk
            pkgs.at-spi2-core
            pkgs.cairo
            pkgs.cups
            pkgs.dbus
            pkgs.expat
            pkgs.gdk-pixbuf
            pkgs.glib
            pkgs.gtk3
            pkgs.libdrm
            pkgs.libgbm
            pkgs.libglvnd
            pkgs.libnotify
            pkgs.libxkbcommon
            pkgs.mesa
            pkgs.nspr
            pkgs.nss
            pkgs.pango
            pkgs.systemd
            pkgs.vulkan-loader
            pkgs.wayland
            pkgs.libX11
            pkgs.libXcomposite
            pkgs.libXdamage
            pkgs.libXext
            pkgs.libXfixes
            pkgs.libXrandr
            pkgs.libxcb
            pkgs.libxshmfence
            pkgs.libusb1
            pkgs.udev
            pkgs.libxcrypt-legacy
          ];

          # we use static runtime
          # https://github.com/electron-userland/electron-builder/blob/a6117b3011a105204af8cc2eca02a56976d1ef29/packages/app-builder-lib/src/toolsets/linux.ts#L122
          appImageTools = pkgs.stdenvNoCC.mkDerivation {
            name = "electron-builder-appimage-runtime";
            src =
              let
                # Keep in sync with "appimage" toolset version in gui/electron/electron-builder.yml
                appimageToolsVersion = "1.0.3";
              in
              pkgs.fetchzip {
                url = "https://github.com/electron-userland/electron-builder-binaries/releases/download/appimage@${appimageToolsVersion}/appimage-tools-runtime-20251108.tar.gz";
                hash = "sha256-Iqhvyp6BNpH+tXvVO7MLgYbsJ6va4lXO7El3wtyWBII=";
                stripRoot = false;
              };

            installPhase = ''
              mkdir $out
              cp -r -t $out/ lib/ runtimes/
              ln -s -t $out \
                "${pkgs.desktop-file-utils}/bin/desktop-file-validate" \
                "${pkgs.squashfsTools}/bin/mksquashfs"
            '';
          };

          buildToolsVersion = "36.0.0";
          androidComposition = pkgs.androidenv.composeAndroidPackages {
            buildToolsVersions = [
              buildToolsVersion
            ];
            platformVersions = [
              "36"
            ];
          };
        in
        {
          # For Android SDK
          _module.args.pkgs = import nixpkgs {
            inherit system;
            config.allowUnfree = true;
            config.android_sdk.accept_license = true;
          };

          devShells.default = pkgs.mkShell rec {
            packages = [
              # for running the jar
              pkgs.jdk25

              # for build
              pkgs.electron
              pkgs.rpm
              pkgs.fpm
              pkgs.p7zip
              # For Windows GUI cross-build
              pkgs.wineWow64Packages.stable
              pkgs.zlib
              pkgs.squashfsTools
              pkgs.desktop-file-utils
              pkgs.fakeroot
              pkgs.libarchive
              pkgs.icu
              pkgs.nodejs_22
              pkgs.pnpm
              pkgs.pkg-config
              pkgs.python3
              pkgs.gcc
              pkgs.gnumake
              pkgs.binutils
              pkgs.git
              pkgs.node-gyp-build
              androidComposition.androidsdk
            ];
            buildInputs = runtimeLibs;

            shellHook = ''
              export LD_LIBRARY_PATH="${
                lib.makeLibraryPath [
                  pkgs.systemd
                  pkgs.hidapi
                ]
              }:$LD_LIBRARY_PATH"
            '';

            JAVA_HOME = "${pkgs.jdk25}/lib/openjdk";
            USE_SYSTEM_FPM = "true";
            ELECTRON_BUILDER_7ZIP_PATH = "${pkgs.p7zip}/bin/7za";
            APPIMAGE_TOOLS_PATH = "${appImageTools}";
            # for electron-vite, so `pnpm gui` works
            ELECTRON_EXEC_PATH = "${pkgs.electron}/bin/electron";
            ANDROID_HOME = "${androidComposition.androidsdk}/libexec/android-sdk";
            GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${ANDROID_HOME}/build-tools/${buildToolsVersion}/aapt2";
          };
        };
    };
}
