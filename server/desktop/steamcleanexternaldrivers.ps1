[CmdletBinding(PositionalBinding=$false)]
param (
    [parameter(Position=0)][string]$LogFile = "$env:Public\Documents\SlimeVRUninstall_log.txt"
)

$ErrorActionPreference = 'Stop'

# Helper to log messages to $LogFile, ensuring directory exists
function Log {
    param([string]$msg)
    $logDir = Split-Path -Path $LogFile -Parent
    if ($logDir -and -not (Test-Path -Path $logDir)) {
        try { New-Item -ItemType Directory -Path $logDir -Force | Out-Null } catch { }
    }
    Add-Content -Path $LogFile -Value ("{0}" -f $msg)
}

# Required for System.Web.Script.Serialization.JavaScriptSerializer
[void][System.Reflection.Assembly]::LoadWithPartialName("System.Web.Extensions")

# Prune external SlimeVR driver(s)
$OpenVrConfigPath = "$env:LOCALAPPDATA\openvr\openvrpaths.vrpath"
if (Test-Path -Path $OpenVrConfigPath) {
    $OpenVrConfig = Get-Content -Path $OpenVrConfigPath -Encoding utf8 | Out-String | ConvertFrom-Json
    Log "Checking External Drivers in '$OpenVrConfigPath' for old SlimeVR Drivers..."
    $ExternalDriverPaths = @()
    if ($OpenVrConfig.external_drivers -and $OpenVrConfig.external_drivers.Length) {
        foreach ($ExternalDriverPath in $OpenVrConfig.external_drivers) {
            if (-not (Test-Path -Path "$ExternalDriverPath\driver.vrdrivermanifest")) {
                Log "VR driver path '$ExternalDriverPath' has no manifest."
                $ExternalDriverPaths += $ExternalDriverPath
                continue
            }
            $DriverManifest = Get-Content -Path "$ExternalDriverPath\driver.vrdrivermanifest" -Encoding utf8 | Out-String | ConvertFrom-Json
            if ($DriverManifest.name -eq "SlimeVR") {
                Log "Found external SlimeVR Driver in '$ExternalDriverPath'. Removing..."
                continue
            }
            $ExternalDriverPaths += $ExternalDriverPath
        }
    }
    if ($ExternalDriverPaths.Length -eq 0) {
        $OpenVrConfig.external_drivers = $null
    } else {
        $OpenVrConfig.external_drivers = $ExternalDriverPaths
    }
    [System.IO.File]::WriteAllLines($OpenVrConfigPath, (ConvertTo-Json -InputObject $OpenVrConfig -Compress))
    Log "Updated OpenVR config at '$OpenVrConfigPath'"
} else {
    Log "OpenVR config not found at '$OpenVrConfigPath'. Skipping external driver prune."
}
