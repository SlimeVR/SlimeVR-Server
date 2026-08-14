#!/bin/bash
if ! command -v clang-format >/dev/null 2>&1; then
  echo "clang-format is not installed, please install clang-format via your package manager (e.g. pacman/apt/dnf/winget) and re-run the script" >&2
  exit 1
fi

find src/ \( \
  -iname '*.cpp' \
  -or -iname '*.hpp' \
\) \
  -exec echo Formatting {} \; \
  -exec clang-format -i {} \;
