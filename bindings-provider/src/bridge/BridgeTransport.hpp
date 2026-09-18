// SPDX-License-Identifier: MIT OR Apache-2.0
// SPDX-FileCopyrightText: (c) 2026 Eiren Rain and SlimeVR Contributors
#pragma once

#include <condition_variable>
#include <filesystem>
#include <shared_mutex>
#include <span>
#include <system_error>
#include <thread>
#include <variant>

// Needs to be before anything that includes Windows.h to avoid macro clashes
#include <solarxr_protocol/generated/all_generated.h>

#ifdef _WIN32
#define WIN32_LEAN_AND_MEAN
#define NOMINMAX
#include <Winsock2.h>
#include <afunix.h>
// Microsoft why
#undef ERROR
#undef SendMessage
#else
#include <fcntl.h>
#include <poll.h>
#include <sys/socket.h>
#include <sys/un.h>
#endif

#include "../logger.hpp"

/**
 * @brief Passes messages between SlimeVR Server and SteamVR Driver using Unix sockets.
 *
 * Client or Server connection handling is implemented by extending this class.
 *
 * This class provides a set of methods to start, stop an IO thread, and send messages over a Unix socket.
 *
 * When a message is received and parsed from the pipe, the on_message_received function passed in the constructor is called
 * from the bridge thread with the message as a parameter.
 *
 * @param logger A shared pointer to an Logger object to log messages from the transport.
 * @param on_message_received A function to be called from event loop thread when a message is received and parsed from the pipe.
 * @param on_connect A function to be called when the socket is connected to.
 * @param on_disconnect A function to be called when the connection is ended.
 */
class BridgeTransport {
public:
    using MessageHeader = std::variant<const solarxr_protocol::data_feed::DataFeedMessageHeader*, const solarxr_protocol::rpc::RpcMessageHeader*, const solarxr_protocol::driver_protocol::DriverMessageHeader*>;

#ifdef _WIN32
    using Socket = SOCKET;
    constexpr static Socket InvalidSocket = INVALID_SOCKET;
    constexpr static int SocketError = SOCKET_ERROR;
#else
    using Socket = int;
    constexpr static Socket InvalidSocket = -1;
    constexpr static int SocketError = -1;
#endif

    static inline int GetLastSocketError() {
#ifdef _WIN32
        return WSAGetLastError();
#else
        return errno;
#endif
    }

    static inline int CloseSocket(Socket fd) {
#ifdef _WIN32
        return closesocket(fd);
#else
        return close(fd);
#endif
    }

    static inline bool IsBlockingError(int err) {
#ifdef _WIN32
        return err == WSAEWOULDBLOCK;
#else
        return err == EAGAIN || err == EWOULDBLOCK;
#endif
    }

    static inline int SetNonBlocking(Socket fd) {
#ifdef _WIN32
        u_long mode = 1;
        return ioctlsocket(fd, FIONBIO, &mode);
#else
        return fcntl(fd, F_SETFL, O_NONBLOCK);
#endif
    }

    /**
     * @brief Exception thrown when IO is cancelled by stop token.
     */
    class Cancelled : public std::exception {
    public:
        Cancelled() noexcept { };
        ~Cancelled() { };

        const char* what() const noexcept override {
            return "IO operation cancelled";
        }
    };

    /**
     * @brief Read @a size bytes from @a fd into @a data, retrying until all data is read or an error occurs.
     *
     * @throws @ref Cancelled stop was requested on @a stop
     * @throws std::system_error error returned on socket
     * @returns @ref SocketError on fail, otherwise number of bytes read
     */
    static ptrdiff_t ReadFully(Socket fd, std::stop_token stop, void* data, size_t size) {
        ptrdiff_t ret;
        size_t received = 0;
        while (!stop.stop_requested() && received < size) {
            ret = recv(fd, reinterpret_cast<char*>(data) + received, size - received, 0);
            if (ret == 0) // EOF
                return ret;

            if (ret == SocketError) [[unlikely]] {
                int err = GetLastSocketError();
                if (IsBlockingError(err)) [[likely]] {
                    // we couldn't read because it would block, just try again
                    continue;
                } else {
                    throw std::system_error(err, std::system_category(), "recv() failed");
                }
            }

            received += ret;
        }

        if (stop.stop_requested())
            throw Cancelled();
        return received;
    }

    /**
     * @brief Write @a size bytes from @a data to @a fd, retrying until all data is written or an error occurs.
     *
     * @throws std::system_error error returned on socket
     *
     * @returns @ref SocketError on fail, otherwise number of bytes written
     */
    static inline ptrdiff_t WriteFully(Socket fd, const void* data, size_t size) {
#ifdef __linux__
        constexpr int flags = MSG_NOSIGNAL;
#else
        constexpr int flags = 0;
#endif

        ptrdiff_t ret;
        size_t sent = 0;
        while (sent < size) {
            ret = send(fd, reinterpret_cast<const char*>(data) + sent, size - sent, flags);
            if (ret == SocketError) [[unlikely]] {
                int err = GetLastSocketError();
                if (IsBlockingError(err)) [[likely]] {
                    // we couldn't write because it would block, just try again
                    continue;
                } else {
                    throw std::system_error(err, std::system_category(), "send() failed");
                }
            }

            sent += ret;
        }

        return sent;
    }

    /**
     * @brief Wait for input to be ready on @a fd, timing out after @a timeout.
     *
     * @param fd file descriptor to poll
     * @param timeout amount of time to wait before giving up
     *
     * @throws std::system_error error returned when polling
     * @throws std::runtime_error error on socket, or hang up was detected
     *
     * @returns @ref 0 if there is no data, positive value if there is data
     */
    template <typename Rep, typename Period>
    static int Poll(Socket fd, std::chrono::duration<Rep, Period> timeout) noexcept(false) {
        struct pollfd pollfd{
            .fd = fd,
            .events = POLLIN,
        };

        int ret;

#ifdef _WIN32
        ret = WSAPoll(&pollfd, 1, std::chrono::duration_cast<std::chrono::milliseconds>(timeout).count());
#else
        {
            auto s = std::chrono::duration_cast<std::chrono::seconds>(timeout);
            auto ns = std::chrono::duration_cast<std::chrono::nanoseconds>(timeout - s);
            struct timespec ts{
                .tv_sec = s.count(),
                .tv_nsec = ns.count(),
            };
            ret = ppoll(&pollfd, 1, timeout == timeout.zero() ? nullptr : &ts, nullptr);
        }
#endif

#ifdef _WIN32
        constexpr int InterruptedErrno = WSAEINTR;
#else
        constexpr int InterruptedErrno = EINTR;
#endif

        if (ret == SocketError) {
            int err = GetLastSocketError();
            throw std::system_error(err, std::system_category(), "Poll() failed");
        }

        if ((pollfd.revents & (POLLERR | POLLHUP)) != 0) {
            throw std::runtime_error("Socket connection lost");
        }

        return pollfd.revents & POLLIN;
    }

    BridgeTransport(std::function<void(MessageHeader&&)> on_message_received = {},
                    std::function<void()> on_connect = {},
                    std::function<void()> on_disconnect = {},
                    std::function<void(std::exception&)> on_error = {})
        : connect_callback_(on_connect)
        , message_callback_(on_message_received)
        , disconnect_callback_(on_disconnect)
        , error_callback_(on_error) {
#ifdef _WIN32
        WSADATA _ws_data;
        if (int ret = WSAStartup(MAKEWORD(2, 2), &_ws_data); ret != 0) {
            Logger::Get().Error("WSAStartup failed with code {}", ret);
            return;
        }
#endif
    }

    virtual ~BridgeTransport() {
        Stop();
#ifdef _WIN32
        WSACleanup();
#endif
    }

    /**
     * @brief Starts the channel by creating a thread.
     *
     * Connects and automatic reconnects with a timeout are implemented internally.
     */
    void Start();

    /**
     * @brief Stops the channel by stopping the thread and closing the connection handles.
     *
     * Blocks until the thread is stopped and the connection handles are closed.
     */
    void Stop();

    /**
     * @brief Stops the channel asynchronously and returns immediately.
     */
    void StopAsync();

    /**
     * @brief Sends a message over the channel, you must Finish() the builder before calling this.
     *
     * @param message The message to send.
     */
    void SendMessage(const flatbuffers::FlatBufferBuilder& fbb);

    /**
     * @brief Checks if the channel is connected.
     *
     * @return true if the channel is connected, false otherwise.
     */
    bool IsConnected() const {
        std::shared_lock fd_lock(fd_mutex_);
        return fd_ != InvalidSocket;
    };

protected:
    /**
     * @brief Try to create a connection.
     *
     * Called while an exclusive lock is taken on @ref fd_mutex_.
     * Make sure this doesn't block as it will hold up shutdown.
     *
     * @ref fd_ must be initialised if this returns without throwing.
     *
     * @throws std::system_error I/O error when creating connection
     * @throws std::exception failed to create connection
     */
    virtual void CreateConnection() = 0;
    /**
     * @brief Close the current socket if it's connected.
     *
     * Locks @ref fd_mutex_, do not call this with the mutex locked.
     */
    void ResetConnection();

    /**
     * @brief Closes the current connection, must be called while an exclusive lock is taken on the mutex.
     */
    void CloseConnectionHandles();

    void OnConnect() {
        if (connect_callback_)
            connect_callback_();
    }
    void OnRecv(std::span<uint8_t> event);
    void OnDisconnect() {
        if (disconnect_callback_)
            disconnect_callback_();
    }

    static std::filesystem::path GetSocketPath();

    /// Guards @ref fd_ and the condition variable @ref cv_.
    mutable std::shared_mutex fd_mutex_;
    /// Guards write operations.
    mutable std::mutex write_mutex_;
    /// Signaled on connection, or when we're exiting.
    std::condition_variable_any cv_;
    Socket fd_ = InvalidSocket;

private:
    void RunThread(std::stop_token stop);

    std::function<void()> connect_callback_;
    std::function<void(MessageHeader&&)> message_callback_;
    std::function<void()> disconnect_callback_;
    std::function<void(std::exception&)> error_callback_;

    std::jthread thread_;
    std::jthread reconnect_thread_;
    std::optional<std::error_code> last_error_;
};
