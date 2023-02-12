{
  description = "Server app for SlimeVR ecosystem";

  inputs.nixpkgs.url = "nixpkgs/nixos-22.11";
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
          libayatana-appindicator-gtk3
          libffi
          libsoup
          openssl.out
          pango
          pkg-config
          treefmt
          webkitgtk
          zlib

          # Some nice things to have
          exa
          fd

          jdk # JDK17
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
