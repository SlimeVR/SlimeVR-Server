#pragma once

#include <cassert>
#include <chrono>
#include <format>
#include <fstream>
#include <iomanip>
#include <iostream>

class Logger {
private:
    std::ofstream log_stream;
#ifdef _WIN32
    bool should_log_to_std_streams;
#endif

    template <bool important, typename... Args>
    void Log(std::string_view suffix, std::format_string<Args...> fmt,
             Args&&... args) {
        auto now = std::chrono::system_clock::now();
        auto time_t = std::chrono::system_clock::to_time_t(now);

        auto s = std::format(fmt, std::forward<Args>(args)...);
#ifdef _WIN32
        if (should_log_to_std_streams) {
#endif
            if constexpr (important)
                std::cerr << suffix << ' ' << s << '\n';
            else
                std::cout << suffix << ' ' << s << '\n';
#ifdef _WIN32
        }
#endif

        log_stream << std::put_time(std::localtime(&time_t), "[%F %T]") //
                   << ' ' << suffix << ' ' << s << '\n';
    }

public:
    Logger();

    template <typename... Args>
    void Debug(std::format_string<Args...> fmt, Args&&... args) {
#ifndef NDEBUG
        Log<false>("[DEBUG]", fmt, std::forward<Args>(args)...);
#endif
    }

    template <typename... Args>
    void Info(std::format_string<Args...> fmt, Args&&... args) {
        Log<false>("[INFO]", fmt, std::forward<Args>(args)...);
    }

    template <typename... Args>
    void Warning(std::format_string<Args...> fmt, Args&&... args) {
        Log<true>("[WARN]", fmt, std::forward<Args>(args)...);
    }

    template <typename... Args>
    void Error(std::format_string<Args...> fmt, Args&&... args) {
        Log<true>("[ERROR]", fmt, std::forward<Args>(args)...);
    }

    void Flush() {
#ifdef _WIN32
        if (should_log_to_std_streams) {
#endif
            std::cerr.flush();
            std::cout.flush();
#ifdef _WIN32
        }
#endif
        log_stream.flush();
    }

    static Logger& Get();
};
