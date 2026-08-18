$ErrorActionPreference = "Stop"

if ((Get-Command "clang-format" -ErrorAction SilentlyContinue) -eq $null) {
    $errorObj = New-Object System.IO.FileNotFoundException("clang-format is not installed, please install clang-format via your package manager (e.g. winget/chocolatey/scoop) and re-run the script")
    throw $errorObj
}

$SourceFiles = Get-ChildItem -Path '.\src\' -Recurse -Include "*.cpp","*.hpp"
foreach ($file in $SourceFiles) {
    Write-Output "Formatting $(Resolve-Path -Relative $file.FullName)"
    clang-format -i "$($file.FullName)"
}
