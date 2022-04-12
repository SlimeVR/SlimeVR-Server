# Overlay Protocol

This is a temporary protocol that will be served via a websocket endpoint at a
particular port in the SlimeVR server. It is a temporary measure to deal with the
lack of support for unions of structs (and strings) in the flatc's rust code
generator.

Once the `slimevr_protocol` works in Rust, this protocol will be abandoned. For
this reason, the protocol is not intended for use by third party applications
and is *not* a stable target for external apps to use. We may make breaking
changes or remove it at any time. Use `slimevr_protocol` for third party apps.


## File structure

- `flatbuffers`: Holds the flatbuffer schema files that define the protocol.
- `java`: The java code for the flatbuffers. This is be consumed by gradle as a subproject.
- `rust`: The rust code for the flatbuffers. This is be consumed by `cargo` via a git dependency.
- `generate.sh`: Helper script to generate the code from the `.fbs` files.

Generated code goes in `generated` folders. These files are directly committed to the
repo, if you change the `.fbs` files, you should regenerate these folders.

## License

This software is free and open source! All code in this folder and subfolders is
dual-licensed under either:

* [MIT License](/docs/LICENSE-MIT) (or
  [http://opensource.org/licenses/MIT](http://opensource.org/licenses/MIT))
* [Apache License, Version 2.0](/docs/LICENSE-APACHE) (or
  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0))

at your option. This means you can select the license you prefer! This
dual-licensing approach is the de-facto standard in the Rust ecosystem and there
are [very good reasons](https://github.com/bevyengine/bevy/issues/2373) to
include both.

Unless you explicitly state otherwise, any contribution intentionally submitted
for inclusion in the work by you, as defined in the Apache-2.0 license, shall be
dual licensed as above, without any additional terms or conditions.
