# SlimeVR Server — Design Guidelines

This document explains the architectural choices made in the server rewrite and how to extend the system correctly.

---

## Core Principle: Reducers and State

Every major part of this server — a tracker, a device, a UDP connection, a SolarXR session — manages state the same way: immutable data, typed actions, and pure reducer functions that transform one into the other.

This is not accidental. It gives us:
- **Predictability**: state only changes through known, enumerated actions
- **Observability**: any code can `collect` the `StateFlow` and react to changes
- **Concurrency safety**: `StateFlow.update` is atomic; two concurrent dispatches never corrupt state

---

## The Context System

The `Context<S, A>` type (`context/context.kt`) is the building block of every module:

```kotlin
data class Context<S, in A>(
    val state: StateFlow<S>,        // current state, readable by anyone
    val dispatch: suspend (A) -> Unit,
    val dispatchAll: suspend (List<A>) -> Unit,
    val scope: CoroutineScope,      // lifetime of this module
)
```

`createContext` wires everything together:
1. Takes an `initialState` and a list of **reducers** (`(S, A) -> S`)
2. On each `dispatch`, folds all reducers over the current state
3. Publishes the new state on the `StateFlow`

**Never mutate state directly.** Always go through `dispatch`.

---

## Behaviours: Splitting Concerns

A `Behaviour` bundles two optional pieces:

```kotlin
data class BasicBehaviour<S, A>(
    val reducer: ((S, A) -> S)? = null,
    val observer: ((Context<S, A>) -> Unit)? = null,
)
```

- **`reducer`**: Pure function. Handles the actions it cares about, returns the rest unchanged.
- **`observer`**: Called once at construction. Launches coroutines, registers event listeners, subscribes to other state flows.

Use `CustomBehaviour<S, A, C>` when the observer needs access to more than just the context — for example, a `UDPConnection` behaviour needs the connection's `send` function and `PacketDispatcher`, not just its `Context`.

Every module follows the same construction pattern:

```kotlin
val behaviours = listOf(BehaviourA, BehaviourB, BehaviourC)

val context = createContext(
    initialState = ...,
    reducers = behaviours.map { it.reducer },
    scope = scope,
)

behaviours.map { it.observer }.forEach { it?.invoke(context) }
```

This is where observers are started. Order matters for reducers (applied top-to-bottom), but rarely matters for observers.

---

## Actions

Actions are `sealed interface`s with `data class` variants. This means:

- The compiler enforces exhaustive `when` expressions in reducers
- No stringly-typed dispatch
- Refactors are caught at compile time

Use `data class Update(val transform: State.() -> State)` when you need a flexible "update anything" action (see `TrackerActions`, `DeviceActions`). Use specific named actions when the action has semantic meaning that other behaviours need to pattern-match on (see `UDPConnectionActions.Handshake`).

---

## The PacketDispatcher Pattern

`PacketDispatcher<T>` (`solarxr/solarxr.kt`) routes incoming messages to typed listeners without a giant `when` block:

```kotlin
dispatcher.on<SensorInfo> { packet -> /* only called for SensorInfo */ }
dispatcher.onAny { packet -> /* called for everything */ }
dispatcher.emit(packet) // routes to correct listeners
```

Use this wherever you have a stream of heterogeneous messages (UDP packets, SolarXR messages). Each behaviour registers its own listener in its `observer` — the dispatcher is passed as part of the module struct.

---

## Coroutines and Lifetime

- Every module is given a `CoroutineScope` at creation. Cancelling that scope tears down all coroutines the module launched.
- Observers should use `it.context.scope.launch { ... }` so their work is scoped to the module.
- Blocking I/O goes on `Dispatchers.IO`. State updates and logic stay on the default dispatcher.
- **Avoid `runBlocking`** inside observers or handlers — it blocks the coroutine thread. The one acceptable use is synchronous listener registration before a scope is started.

---

## State vs. Out-of-Band Data

Not everything belongs in `StateFlow`. Two good examples:

- `VRServer.handleCounter` is an `AtomicInteger` — not in state — because nothing needs to react to it changing, and `incrementAndGet()` is faster and simpler than a dispatch round-trip.
- `UDPTrackerServer` has no `Context` at all. Its connection map is a plain `MutableMap` internal to the server loop. Nothing outside the loop reads it, so there is no reason to wrap it in a state machine.

Rule of thumb: put data in state if **any other code needs to react to it changing**. If it's purely an implementation detail owned by one place, keep it plain.

---

## Adding a New Module

To add a new major section of the server (say, a HID device connection):

1. **Define the state**:
   ```kotlin
   data class HIDConnectionState(
       val deviceId: Int?,
       val connected: Boolean,
   )
   ```

2. **Define sealed actions**:
   ```kotlin
   sealed interface HIDConnectionActions {
       data class Connected(val deviceId: Int) : HIDConnectionActions
       data object Disconnected : HIDConnectionActions
   }
   ```

3. **Create type aliases** (keeps signatures readable):
   ```kotlin
   typealias HIDConnectionContext = Context<HIDConnectionState, HIDConnectionActions>
   typealias HIDConnectionBehaviour = CustomBehaviour<HIDConnectionState, HIDConnectionActions, HIDConnection>
   ```

4. **Define the module struct** (holds context + extra runtime state):
   ```kotlin
   data class HIDConnection(
       val context: HIDConnectionContext,
       val serverContext: VRServer,
       val send: suspend (ByteArray) -> Unit,
   )
   ```

5. **Write behaviours** as top-level `val`s (stateless, reusable):
   ```kotlin
   val HIDHandshakeBehaviour = HIDConnectionBehaviour(
       reducer = { s, a -> when (a) {
           is HIDConnectionActions.Connected -> s.copy(deviceId = a.deviceId, connected = true)
           is HIDConnectionActions.Disconnected -> s.copy(connected = false)
       }},
       observer = { conn ->
           // launch coroutines, subscribe to events, etc.
       }
   )
   ```

6. **Write a factory function**:
   ```kotlin
   fun createHIDConnection(
       serverContext: VRServer,
       scope: CoroutineScope,
       send: suspend (ByteArray) -> Unit,
   ): HIDConnection {
       val behaviours = listOf(HIDHandshakeBehaviour, ...)
       val context = createContext(initialState = ..., reducers = behaviours.map { it.reducer }, scope = scope)
       val conn = HIDConnection(context, serverContext, send)
       behaviours.map { it.observer }.forEach { it?.invoke(conn) }
       return conn
   }
   ```

---

## Adding a New Behaviour to an Existing Module

Find the existing `createX` function, add your behaviour to the `behaviours` list. That's it. The behaviour's reducer and observer are automatically picked up.

Example: adding battery tracking to a HID connection requires only adding a `HIDBatteryBehaviour` to the list — nothing else changes.

---

## Adding a New UDP Packet Type

1. Add the packet class and its `read` function in `tracker/udp/packets.kt`
2. In the relevant behaviour's observer, register a listener:
   ```kotlin
   connection.packetEvents.on<MyNewPacket> { event ->
       // handle it
   }
   ```
3. In `tracker/udp/server.kt`, route the new packet type to `emit`.

---

## IPC

There are three IPC sockets, each serving a distinct client:

| Socket | Client | Payload encoding |
|---|---|---|
| `SlimeVRDriver` | OpenVR driver | Protobuf (Wire) |
| `SlimeVRInput` | External feeder | Protobuf (Wire) |
| `SlimeVRRpc` | SolarXR RPC | FlatBuffers (solarxr-protocol) |

### Wire framing

All three sockets share the same framing: a **LE u32 length** prefix (which includes the 4-byte header itself) followed by the raw payload bytes.

### Transport / protocol split

Platform files (`linux.kt`, `windows.kt`) own the transport layer — accepting connections, reading frames, and producing a `Flow<ByteArray>` + a `send` function. The protocol handlers in `protocol.kt` are plain `suspend fun`s that consume those two abstractions and know nothing about Unix sockets or named pipes.

This means the same handler runs on Linux (Unix domain sockets) and Windows (named pipes) without any changes.

### Connection lifetime

Each client runs in its own `launch` block. When the socket disconnects, the coroutine scope is cancelled and everything inside cleans up automatically.

### What each handler does

- **Driver** (`handleDriverConnection`): on connect, sends the protocol version and streams `TrackerAdded` + `Position` messages for every non-driver tracker. Receives user actions from the driver (resets, etc.).
- **Feeder** (`handleFeederConnection`): receives `TrackerAdded` messages to create new devices and trackers, then `Position` updates to drive their rotation.
- **SolarXR** (`handleSolarXRConnection`): creates a `SolarXRConnection` and forwards all incoming FlatBuffers messages to it.

---

## What Goes Where

| Location | Purpose |
|---|---|
| `server/core` | Protocol-agnostic business logic (trackers, devices, config, SolarXR) |
| `server/desktop` | Platform-specific entry point, IPC socket wiring, platform abstractions |
| `context/context.kt` | The `Context` / `Behaviour` / `createContext` primitives — do not add domain logic here |
| `tracker/udp/` | Everything specific to the SlimeVR UDP wire protocol |
| `solarxr/` | SolarXR WebSocket server + FlatBuffers message handling |
| `config/` | JSON config read/write with autosave; no business logic |

---

## Style Conventions

- **Prefer plain functions over extension functions.** Only use extension functions when the receiver type is genuinely the primary subject and the function would be confusing without it.
- Behaviours are top-level `val`s (or `object`s if they have no type parameters), not inner classes.
- Factory functions are named `createX`, not `XBuilder` or `X.create` (though `companion object { fun create() }` is acceptable when scoping makes it clearer, as in `VRServer.create`).
- State data classes use `copy(...)` inside reducers and `Update { copy(...) }` actions — never expose a `MutableStateFlow` directly.
- Use `sealed interface` for action types, not `sealed class`, to avoid the extra constructor overhead.