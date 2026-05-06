{
  description = "SlimeVR Server & GUI";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    # Pinned to a revision that still ships temurin jdk-24
    nixpkgs-jdk24.url = "github:NixOS/nixpkgs/d0fc30899600b9b3466ddb260fd83deb486c32f1";
    flake-parts.url = "github:hercules-ci/flake-parts";
  };

  outputs = inputs@{ self, nixpkgs, nixpkgs-jdk24, flake-parts, ... }:
    flake-parts.lib.mkFlake { inherit inputs; } {
      systems = [ "x86_64-linux" ];

      perSystem = { pkgs, system, ... }:
        let
          jdkPkgs = import nixpkgs-jdk24 { inherit system; };
          java = jdkPkgs.javaPackages.compiler.temurin-bin.jdk-24;

          runtimeLibs = pkgs: (with pkgs; [
            java

            alsa-lib at-spi2-atk at-spi2-core cairo cups dbus expat
            gdk-pixbuf glib gtk3 libdrm libgbm libglvnd libnotify
            libxkbcommon mesa nspr nss pango systemd vulkan-loader
            wayland libx11 libxcomposite libxdamage
            libxext libxfixes libxrandr libxcb
            libxshmfence libusb1 udev libxcrypt-legacy
            rpm fpm

            wineWow64Packages.stable
            zlib squashfsTools fakeroot libarchive icu
            nodejs_22 pnpm pkg-config python3 gcc gnumake binutils git
            node-gyp-build
          ]);

          slimeShell = pkgs.buildFHSEnv {
            name = "slimevr-env";
            targetPkgs = runtimeLibs;
            profile = ''
              export JAVA_HOME=${java}
              export PATH="${java}/bin:$PATH"

              # Tell electron-builder to use system tools instead of downloading them
              export USE_SYSTEM_FPM=true
              export USE_SYSTEM_MKSQUASHFS=true
            '';
            runScript = "bash";
          };
        in
        {
          devShells.default = slimeShell.env;
        };
    };
}
