{
  description = "Server app for SlimeVR ecosystem";

  inputs.nixpkgs.url = "nixpkgs/nixos-unstable";
  inputs.flake-utils.url = "github:numtide/flake-utils";

  inputs.rust-overlay.url = "github:oxalica/rust-overlay";

  outputs = {
    self,
    nixpkgs,
    flake-utils,
    rust-overlay,
  }:
    flake-utils.lib.eachDefaultSystem
    (
      system: let
        overlays = [(import rust-overlay)];
        pkgs = import nixpkgs {
          inherit system overlays;
        };
        rustTarget = pkgs.rust-bin.fromRustupToolchainFile ./rust-toolchain.toml;
        nativeBuildInputs = with pkgs; [
          curl
          gcc
          openssl
          pkgconfig
          which
          zlib

          freetype
          expat
        ];
        buildInputs = with pkgs; [
          appimagekit
          atk
          cairo
          dbus
          dbus.lib
          dprint
          gdk-pixbuf
          glib.out
          gobject-introspection
          gtk3
          harfbuzz
          libffi
          libsoup_3
          openssl.out
          pango
          pkg-config
          treefmt
          webkitgtk_4_1
          zlib
          gst_all_1.gstreamer
          gst_all_1.gst-plugins-base
          gst_all_1.gst-plugins-good
          gst_all_1.gst-plugins-bad
          librsvg

          # Some nice things to have
          exa
          fd

          jdk17 # JDK17
          nodejs
          gradle
        ];
      in {
        devShells.default = pkgs.mkShell {
          nativeBuildInputs =
            nativeBuildInputs
            ++ [
            ];
          buildInputs =
            buildInputs
            ++ [
              rustTarget
            ];

          shellHook = ''
            alias ls=exa
            alias find=fd
          '';
        };
      }
    );
}
