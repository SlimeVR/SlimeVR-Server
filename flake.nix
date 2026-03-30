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
          java = pkgs.javaPackages.compiler.temurin-bin.jdk-24;

          runtimeLibs = pkgs: (with pkgs; [
            java

            alsa-lib at-spi2-atk at-spi2-core cairo cups dbus expat
            gdk-pixbuf glib gtk3 libdrm libgbm libglvnd libnotify
            libxkbcommon mesa nspr nss pango systemd vulkan-loader
            wayland xorg.libX11 xorg.libXcomposite xorg.libXdamage
            xorg.libXext xorg.libXfixes xorg.libXrandr xorg.libxcb
            xorg.libxshmfence libusb1 udev libxcrypt-legacy
            rpm fpm

            wineWow64Packages.stable
            zlib squashfsTools fakeroot libarchive icu
            nodejs_22 pnpm pkg-config python3 gcc gnumake binutils git
            pkgs.nodePackages.node-gyp-build
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
