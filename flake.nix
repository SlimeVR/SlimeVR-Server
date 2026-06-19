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
        { lib, pkgs, ... }:
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

          toolDirectoryName =
            if pkgs.stdenv.hostPlatform.isDarwin then
              "darwin"
            else if pkgs.stdenv.hostPlatform.isLinux then
              {
                "armv7l-linux" = "linux-arm32";
                "aarch64-linux" = "linux-arm64";
                "i686-linux" = "linux-ia32";
                "x86_64-linux" = "linux-x64";
              }
              ."${pkgs.stdenv.hostPlatform.system}"
            else
              throw "Unsupported platform";

          # we use fuse2
          # https://github.com/electron-userland/electron-builder/blob/a6117b3011a105204af8cc2eca02a56976d1ef29/packages/app-builder-lib/src/toolsets/linux.ts#L122
          runtime = pkgs.stdenvNoCC.mkDerivation {
            name = "electron-builder-appimage-runtime";
            src = pkgs.fetchurl {
              url = "https://github.com/electron-userland/electron-builder-binaries/releases/download/appimage-12.0.1/appimage-12.0.1.7z";
              hash = "sha256-0S/3648dHsRlLKUjen+9yjOswMdYBFY2/spi3G7LjsQ=";
            };
            # https://github.com/NixOS/nixpkgs/blob/7890ba0a99c064446fe2178ef2f8e3abdf6ec42a/pkgs/by-name/lo/losslesscut-bin/build-from-windows.nix#L20
            nativeBuildInputs = [ pkgs.p7zip ];
            unpackPhase = ''
              runHook preUnpack
              7z x "$src"
              runHook postUnpack
            '';
            installPhase = ''
              mkdir $out
              cp -r -t $out/ \
                lib/ \
                runtime-*
            '';
          };

          appImageTools = pkgs.stdenvNoCC.mkDerivation {
            name = "electron-builder-appimage-tools";
            dontUnpack = true;

            installPhase = ''
              mkdir -p "$out/${toolDirectoryName}"
              ln -s -t $out/${toolDirectoryName} \
                "${pkgs.desktop-file-utils}/bin/desktop-file-validate" \
                "${pkgs.squashfsTools}/bin/mksquashfs"
              ln -s ${runtime}/runtime-* $out/
              ln -s ${runtime}/lib $out/lib
            '';
          };
        in
        {
          devShells.default = pkgs.mkShell {
            packages = [
              # for running the jar
              pkgs.jdk17
              # for build
              pkgs.electron
              pkgs.rpm
              pkgs.fpm
              pkgs.p7zip
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
            ];
            buildInputs = runtimeLibs;

            JAVA_HOME = "${pkgs.jdk17}/lib/openjdk";
            USE_SYSTEM_FPM = "true";
            ELECTRON_BUILDER_7ZIP_PATH = "${pkgs.p7zip}/bin/7za";
            APPIMAGE_TOOLS_PATH = "${appImageTools}";
            #ELECTRON_SKIP_BINARY_DOWNLOAD = true;
            #ELECTRON_DIST = "${pkgs.electron.dist}";
            #ELECTRON_VERSION = "${pkgs.electron.version}";

            # for electron-vite, so `pnpm gui` works
            ELECTRON_EXEC_PATH = "${pkgs.electron}/bin/electron";

            shellHook = ''
              export LD_LIBRARY_PATH="${
                lib.makeLibraryPath [
                  pkgs.systemd
                  pkgs.hidapi
                ]
              }:$LD_LIBRARY_PATH"
            '';
          };
        };
    };
}
