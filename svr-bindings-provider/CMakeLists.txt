cmake_minimum_required(VERSION 3.26)

project(SlimeVR-OpenVR-BindingsProvider)

include("${CMAKE_CURRENT_SOURCE_DIR}/vcpkg/scripts/buildsystems/vcpkg.cmake")
find_package(flatbuffers CONFIG REQUIRED)

set(SOLARXR_PROTOCOL_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../solarxr-protocol")
set(FLATBUFFERS_GENERATED_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../solarxr-protocol/protocol/cpp")

# These are used to determine the OpenVR library path
if(CMAKE_SIZEOF_VOID_P EQUAL 8)
    set(PROCESSOR_ARCH "64")
else()
    set(PROCESSOR_ARCH "32")
endif()
if(WIN32)
    set(OPENVR_LIBRARY_NAME "openvr_api")
    set(OPENVR_LIBRARY_EXT "dll")
    set(PLATFORM_NAME "win")
elseif(APPLE)
    set(OPENVR_LIBRARY_NAME "libopenvr_api")
    set(OPENVR_LIBRARY_EXT "dylib")
    set(PLATFORM_NAME "osx")
elseif(LINUX)
    set(OPENVR_LIBRARY_NAME "libopenvr_api")
    set(OPENVR_LIBRARY_EXT "so")
    if (CMAKE_SYSTEM_PROCESSOR STREQUAL "aarch64")
        set(PLATFORM_NAME "linuxarm")
    elseif(CMAKE_SYSTEM_PROCESSOR STREQUAL "x86_64")
        set(PLATFORM_NAME "linux")
    else()
        message(FATAL_ERROR "Unknown/unsupported CPU architecture")
    endif()
else()
    message(FATAL_ERROR "Unknown/unsupported platform")
endif()

set(OPENVR_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/openvr")

message(STATUS "OpenVR library search path: ${OPENVR_ROOT}/lib/${PLATFORM_NAME}${PROCESSOR_ARCH}")
find_library(OPENVR_LIBRARY NAMES openvr_api REQUIRED PATHS "${OPENVR_ROOT}/lib/${PLATFORM_NAME}${PROCESSOR_ARCH}/" NO_DEFAULT_PATH)
message(STATUS "Found OpenVR library at: ${OPENVR_LIBRARY}")

add_executable(BindingsProvider
    "${CMAKE_CURRENT_SOURCE_DIR}/src/paths.cpp"
    "${CMAKE_CURRENT_SOURCE_DIR}/src/logger.cpp"
    "${CMAKE_CURRENT_SOURCE_DIR}/src/solarxr.cpp"
    "${CMAKE_CURRENT_SOURCE_DIR}/src/vr_utils.cpp"
    "${CMAKE_CURRENT_SOURCE_DIR}/src/main.cpp"
)
set_target_properties(BindingsProvider PROPERTIES CXX_STANDARD 23)
target_include_directories(BindingsProvider PRIVATE "${SOLARXR_PROTOCOL_ROOT}/lib/flatbuffers/" "${FLATBUFFERS_GENERATED_ROOT}/include" "${CMAKE_CURRENT_SOURCE_DIR}/include" "${OPENVR_ROOT}/headers")
target_link_libraries(BindingsProvider PRIVATE flatbuffers::flatbuffers "${OPENVR_LIBRARY}")

set(OPENVR_RUNTIME_LIBRARY "${OPENVR_ROOT}/bin/${PLATFORM_NAME}${PROCESSOR_ARCH}/${OPENVR_LIBRARY_NAME}.${OPENVR_LIBRARY_EXT}")
message(STATUS "Found OpenVR runtime library: ${OPENVR_RUNTIME_LIBRARY}")
configure_file("${OPENVR_RUNTIME_LIBRARY}" "." COPYONLY)

# Embed the resources into resources.hpp
file(STRINGS src/resources/manifest.vrmanifest SVR_VRAPP_MANIFEST NEWLINE_CONSUME)
file(STRINGS src/resources/action_manifest.json SVR_VRAPP_ACTION_MANIFEST NEWLINE_CONSUME)
configure_file(src/resources/resources.hpp.in src/resources/resources.hpp @ONLY)
target_include_directories(BindingsProvider PRIVATE "${CMAKE_CURRENT_BINARY_DIR}/src")
