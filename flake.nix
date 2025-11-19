{
  description = "Affordable full-body tracking for VR!";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
    fenix = {
      url = "github:nix-community/fenix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = inputs@{ self, nixpkgs, flake-parts, fenix, ... }:
    flake-parts.lib.mkFlake { inherit inputs; } {
      systems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];

      perSystem = { system, lib, ... }:
        let
          pkgs = import nixpkgs { inherit system; };

          rust_toolchain = lib.importTOML ./rust-toolchain.toml;
          fenixPkgs = fenix.packages.${system};

          rustToolchainSet = fenixPkgs.fromToolchainName {
            name = rust_toolchain.toolchain.channel;
            sha256 = "sha256-+9FmLhAOezBZCOziO0Qct1NOrfpjNsXxc/8I0c7BdKE=";
          };
        in {

          devShells.default = pkgs.mkShell {
            name = "slimevr";

            buildInputs =
              (with pkgs; [
                cacert
              ]) ++ lib.optionals pkgs.stdenv.isLinux (with pkgs; [
                atk cairo dbus dbus.lib dprint gdk-pixbuf glib.out glib-networking
                gobject-introspection gtk3 harfbuzz libffi libsoup_3 openssl.dev pango
                pkg-config treefmt webkitgtk_4_1 zlib
                gst_all_1.gstreamer gst_all_1.gst-plugins-base
                gst_all_1.gst-plugins-good gst_all_1.gst-plugins-bad
                librsvg freetype expat libayatana-appindicator udev libusb1
              ]) ++ lib.optionals pkgs.stdenv.isDarwin [
                pkgs.darwin.apple_sdk.frameworks.Security
              ] ++ [
                pkgs.jdk17
                pkgs.kotlin
                rustToolchainSet.rustc
                rustToolchainSet.cargo
                rustToolchainSet.rustfmt
              ];

            nativeBuildInputs = with pkgs; [ pnpm nodejs_22 gradle ];

            RUST_BACKTRACE = 1;
            GIO_EXTRA_MODULES = "${pkgs.glib-networking}/lib/gio/modules:${pkgs.dconf.lib}/lib/gio/modules";

            shellHook = ''
              export SLIMEVR_RUST_LD_LIBRARY_PATH="$LD_LIBRARY_PATH"
              export LD_LIBRARY_PATH="${pkgs.udev}/lib:${pkgs.libayatana-appindicator}/lib:$LD_LIBRARY_PATH"
              export GST_PLUGIN_SYSTEM_PATH_1_0="${pkgs.gst_all_1.gstreamer.out}/lib/gstreamer-1.0:${pkgs.gst_all_1.gst-plugins-base}/lib/gstreamer-1.0:${pkgs.gst_all_1.gst-plugins-good}/lib/gstreamer-1.0:${pkgs.gst_all_1.gst-plugins-bad}/lib/gstreamer-1.0"

              # Force linker and pkg-config to use udev from nixpkgs so libgudev/hidapi
              # resolve against the correct libudev implementation at link time.
              export PKG_CONFIG_PATH="${pkgs.udev}/lib/pkgconfig:${pkgs.glib}/lib/pkgconfig:$PKG_CONFIG_PATH"
              export LIBRARY_PATH="${pkgs.udev}/lib:$LIBRARY_PATH"
              export LD_RUN_PATH="${pkgs.udev}/lib:$LD_RUN_PATH"
              export NIX_LDFLAGS="-L${pkgs.udev}/lib -ludev $NIX_LDFLAGS"
              export LDFLAGS="-L${pkgs.udev}/lib -Wl,-rpath,${pkgs.udev}/lib -ludev $LDFLAGS"
            '';
          };
        };
    };
}
