# OpenCode Agent Instructions

## Environment & Tooling
- **Server (Kotlin/Java)**: Use `gradle` (`./gradlew`).
    - Run: `./gradlew run`
    - Build: `./gradlew shadowJar`
    - Format: `./gradlew spotlessApply`
- **GUI (Electron/TS)**: Use `pnpm`.
    - Install: `pnpm i`
    - Dev: `pnpm gui`
    - Build: `pnpm package:build`
    - Format: `pnpm run lint:fix`
- **SolarXR Protocol (Submodule)**: Use `bash` and `pnpm`.
    - Edit: Only `schema/` files.
    - Generate: `./generate-flatbuffer.sh` (Linux/macOS).
    - Sync: Run `pnpm i` in root after generation.

## Architecture: The Reducer/Behaviour Pattern
The server uses a reactive reducer pattern for thread safety and observability.

- **Core Primitives**: 
  - `Context<S, A>`: Holds `StateFlow<S>`, `CoroutineScope`, and `Behaviour`s.
  - `Behaviour<S, A, C>`:
    - `reduce(state, action)`: **Pure function**. No side effects.
    - `observe(receiver)`: **Side effects**. Launch coroutines using `receiver.context.scope`.
- **Lifecycle**: `Module.create()` (inert construction) $\rightarrow$ `module.startObserving()` (side-effect activation).
- **DI**: Uses `Phase1ContextProvider` $\rightarrow$ `AppContextProvider` hierarchy.
- **Communication**: Modules communicate via `dispatch(action)` or by observing another module's `StateFlow`.

## Design & Coding Preferences
- **Function over Class**: Prefer plain functions or `object` over classes for single-purpose logic.
- **Function over Extension**: Use plain functions instead of extension functions unless the receiver is the primary subject.
- **Action Selection**: 
  - Use `Update` (with a lambda) for internal state changes that require no external reaction.
  - Use **Named Actions** when multiple behaviours must react to a specific event.
- **State Management**: Only include data in `StateFlow` if it is required for observation by other modules.
- **Communication**: Decouple modules by using `dispatch` or observing `StateFlow` rather than direct method calls.

## Agent Workflow
- **Phase 1: Discovery**: Use the `Glob` $\rightarrow$ `Grep` $\rightarrow$ `Read` pattern. Never guess file paths or contents.
- **Phase 2: Planning**: For complex tasks, use `todowrite` to create a checklist. Present the plan to the user before implementation.
- **Phase 3: Implementation**: Use `edit` for existing files; `write` for new files.
- **Phase 4: Verification**: Always run the relevant linting or testing tools before marking a task as complete.

## Common Pitfalls
- **Coroutine Leaks**: Never launch coroutines in `observe` using a global scope; **always** use `receiver.context.scope`.
- **Direct State Mutation**: Never attempt to mutate state properties directly. Always use `dispatch(action)`.
- **Incomplete Cleanup**: Ensure any module owning a `Context` implements a `dispose()` method.

## Conventions
- **Files**: `module.kt` (Class/State/Actions) and `behaviours.kt` (Implementations).
- **Actions**: Use `sealed interface`.
- **State**: Data classes must use `val` only.
- **Cleanup**: We use a direct `dispose()` method on modules instead of a `Disposable` interface.
- **Detailed Conventions**: See `server/README.md` for full coding style and architecture details.

## Testing
- **Reducers**: Test by creating a `Context` with specific `Behaviour`s.
- **Modules**: Use `TestAppContext` and `buildTest...` helpers from `TestServer.kt`.

## Verification Protocol
- **Linting**: Run `./gradlew spotlessApply` (Server) or `pnpm run lint:fix` (GUI).
- **Testing**: Identify and run the relevant test suite (e.g., `./gradlew test` or specific module tests).
- **Final Audit**: Perform a final `git diff` check to ensure no unintended changes were made.
