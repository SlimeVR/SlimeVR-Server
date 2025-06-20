{
  description = "Affordable full-body tracking for VR!";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    devenv = {
      url = "github:cachix/devenv";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    nix2container = {
      url = "github:nlewo/nix2container";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    mk-shell-bin.url = "github:rrbutani/nix-mk-shell-bin";
    nixgl = {
      url = "github:guibou/nixGL";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    fenix = {
      url = "github:nix-community/fenix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  nixConfig = {
    extra-trusted-public-keys = "devenv.cachix.org-1:w1cLUi8dv3hnoSPGAuibQv+f9TZLr6cv/Hm9XgU50cw=";
    extra-substituters = "https://devenv.cachix.org";
  };

  outputs = inputs @ {
    self,
    flake-parts,
    nixgl,
    ...
  }:
    flake-parts.lib.mkFlake {inherit inputs;} {
      imports = [
        inputs.devenv.flakeModule
      ];
      systems = ["x86_64-linux" "i686-linux" "x86_64-darwin" "aarch64-linux" "aarch64-darwin"];

      perSystem = {
        config,
        self',
        inputs',
        pkgs,
        system,
        lib,
        ...
      }: {
        # Per-system attributes can be defined here. The self' and inputs'
        # module parameters provide easy access to attributes of the same
        # system.

        # Equivalent to  inputs'.nixpkgs.legacyPackages.hello;
        # packages.default = pkgs.hello;
        _module.args.pkgs = import self.inputs.nixpkgs {
          inherit system;
          overlays = [nixgl.overlay];
        };

        devenv.shells.default = let
          fenixpkgs = inputs'.fenix.packages;
          rust_toolchain = lib.importTOML ./rust-toolchain.toml;
        in {
          name = "slimevr";

          imports = [
            # This is just like the imports in devenv.nix.
            # See https://devenv.sh/guides/using-with-flake-parts/#import-a-devenv-module
            # ./devenv-foo.nix
          ];

          # https://devenv.sh/reference/options/
          packages =
            (with pkgs; [
              pkgs.nixgl.nixGLIntel
              cacert
            ])
            ++ lib.optionals pkgs.stdenv.isLinux (with pkgs; [
              appimagekit
              atk
              cairo
              dbus
              dbus.lib
              dprint
              gdk-pixbuf
              glib.out
              glib-networking
              gobject-introspection
              gtk3
              harfbuzz
              libffi
              libsoup_3
              openssl.dev
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
              freetype
              expat
              libayatana-appindicator
              libusb1
            ])
            ++ lib.optionals pkgs.stdenv.isDarwin [
              pkgs.darwin.apple_sdk.frameworks.Security
            ];

          languages.java = {
            enable = true;
            gradle.enable = true;
            jdk.package = pkgs.jdk17;
          };
          languages.kotlin.enable = true;

          languages.javascript = {
            enable = true;
            corepack.enable = true;
          };

          languages.rust = {
            enable = true;
            toolchain = fenixpkgs.fromToolchainName {
              name = rust_toolchain.toolchain.channel;
              sha256 = "sha256-VZZnlyP69+Y3crrLHQyJirqlHrTtGTsyiSnZB8jEvVo=";
            };
            components = rust_toolchain.toolchain.components;
          };

          env = {
            GIO_EXTRA_MODULES = "${pkgs.glib-networking}/lib/gio/modules:${pkgs.dconf.lib}/lib/gio/modules";
          };

          enterShell = with pkgs; ''
            # Export a LD_LIBRARY_PATH without libudev-zero as libgudev not likey
            export SLIMEVR_RUST_LD_LIBRARY_PATH="$LD_LIBRARY_PATH"
            export LD_LIBRARY_PATH="${libudev-zero}/lib:${libayatana-appindicator}/lib:$LD_LIBRARY_PATH"
            # GStreamer plugins won't be found without this
            export GST_PLUGIN_SYSTEM_PATH_1_0="${pkgs.gst_all_1.gstreamer.out}/lib/gstreamer-1.0:${pkgs.gst_all_1.gst-plugins-base}/lib/gstreamer-1.0:${pkgs.gst_all_1.gst-plugins-good}/lib/gstreamer-1.0:${pkgs.gst_all_1.gst-plugins-bad}/lib/gstreamer-1.0"
          '';
        };
      };
      flake = {
        # The usual flake attributes can be defined here, including system-
        # agnostic ones like nixosModule and system-enumerating ones, although
        # those are more easily expressed in perSystem.
      };
    };
}
