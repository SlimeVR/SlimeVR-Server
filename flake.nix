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

            alsa-lib at-spi2-atk at-spi2-core cairo cups dbus expat
            gdk-pixbuf glib gtk3 libdrm libgbm libglvnd libnotify
            libxkbcommon mesa nspr nss pango systemd vulkan-loader
            wayland xorg.libX11 xorg.libXcomposite xorg.libXdamage
            xorg.libXext xorg.libXfixes xorg.libXrandr xorg.libxcb
            xorg.libxshmfence libusb1 udev

            wine
            zlib squashfsTools fakeroot libarchive icu
            nodejs_22 pnpm pkg-config python3 gcc gnumake binutils git
          ]);

          slimeShell = pkgs.buildFHSEnv {
            name = "slimevr-env";
            targetPkgs = runtimeLibs;
            profile = ''
              export JAVA_HOME=${pkgs.jdk17}
              # Ensures Gradle and other tools find the right Java binary
              export PATH="${pkgs.jdk17}/bin:$PATH"
            '';
            runScript = "bash";
          };
        in
        {
          devShells.default = slimeShell.env;
        };
    };
}
