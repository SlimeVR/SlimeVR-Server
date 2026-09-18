// SPDX-License-Identifier: MIT OR Apache-2.0
// SPDX-FileCopyrightText: (c) 2026 Eiren Rain and SlimeVR Contributors
#pragma once

#include <bit>
#include <type_traits>

template <std::endian Endianness, typename T>
    requires(std::is_integral_v<T>)
inline T ConvertEndianness(T v) {
    // I don't really care about mixed endianness..
    if constexpr (std::endian::native == Endianness) {
        return v;
    } else {
        return std::byteswap(v);
    }
}