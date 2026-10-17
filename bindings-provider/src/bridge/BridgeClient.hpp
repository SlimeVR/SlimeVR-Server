// SPDX-License-Identifier: MIT OR Apache-2.0
// SPDX-FileCopyrightText: (c) 2026 Eiren Rain and SlimeVR Contributors
#pragma once

#include "BridgeTransport.hpp"

/**
 * @brief Client implementation of @ref BridgeTransport for communication with SlimeVR Server using unix sockets.
 *
 * @copydoc BridgeTransport
 */
class BridgeClient : public BridgeTransport {
public:
    using BridgeTransport::BridgeTransport;

private:
    void CreateConnection() override;

    std::filesystem::path last_path_;
};
