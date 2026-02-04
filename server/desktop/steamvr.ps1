[CmdletBinding(PositionalBinding=$false)]
param (
    [parameter(Position=0)][string]$SteamPath,
    [parameter(Position=1)][string]$DriverPath,
    [parameter(Position=2)][switch]$Uninstall = $false
)

$ErrorActionPreference = 'Stop'

# Required for System.Web.Script.Serialization.JavaScriptSerializer
[void][System.Reflection.Assembly]::LoadWithPartialName("System.Web.Extensions")

# TODO: Remove the Prune external SlimeVR driver(s) part once its sure everything with 
#       steamcleanexternaldrivers.ps1 is working fine without it.
#       This part was used to remove old SlimeVR drivers from the OpenVR config.
#       The Installation methode is not used for ~3 years now, so it should be safe to remove it.
## Prune external SlimeVR driver(s)

#$OpenVrConfigPath = "$env:LOCALAPPDATA\openvr\openvrpaths.vrpath"
## Check if the path exists to avoid errors. If the file does not exist we don't need to remove anything.
#if (Test-Path -Path $OpenVrConfigPath) {
#    $OpenVrConfig = Get-Content -Path $OpenVrConfigPath -Encoding utf8 | Out-String | ConvertFrom-Json
#    Write-Host "Checking External Drivers in '$OpenVrConfigPath' for old SlimeVR Drivers..."
#    $ExternalDriverPaths = @()
#    if ($OpenVrConfig.external_drivers -and $OpenVrConfig.external_drivers.Length) {
#        foreach ($ExternalDriverPath in $OpenVrConfig.external_drivers) {
#            if (-not (Test-Path -Path "$ExternalDriverPath\driver.vrdrivermanifest")) {
#                Write-Host "VR driver path `"$ExternalDriverPath`" has no manifest."
#                $ExternalDriverPaths += $ExternalDriverPath
#                continue
#            }
#            $DriverManifest = Get-Content -Path "$ExternalDriverPath\driver.vrdrivermanifest" -Encoding utf8 | Out-String | ConvertFrom-Json
#            if ($DriverManifest.name -eq "SlimeVR") {
#                Write-Host "Found external SlimeVR Driver in `"$ExternalDriverPath`". Removing..."
#                continue
#            }
#            $ExternalDriverPaths += $ExternalDriverPath
#        }
#    }
#    if ($ExternalDriverPaths.Length -eq 0) {
#        $OpenVrConfig.external_drivers = $null
#    } else {
#        $OpenVrConfig.external_drivers = $ExternalDriverPaths
#    }
#    [System.IO.File]::WriteAllLines($OpenVrConfigPath, (ConvertTo-Json -InputObject $OpenVrConfig -Compress))
#} else {
#    Write-Host "OpenVR config not found at `"$OpenVrConfigPath`". Skipping external driver prune."
#}

# Remove trackers on uninstall
if ($Uninstall -eq $true) {
    $SteamVrSettingsPath = "$SteamPath\config\steamvr.vrsettings"
    Write-Host "Removing trackers from `"$SteamVrSettingsPath`""
    $SteamVrSettingsContent = Get-Content -Path $SteamVrSettingsPath -Encoding utf8 | Out-String
    $JsonSerializer = New-Object -TypeName "System.Web.Script.Serialization.JavaScriptSerializer" -Property @{MaxJsonLength = [System.Int32]::MaxValue}
    $SteamVrSettings = $JsonSerializer.DeserializeObject($SteamVrSettingsContent)

    # Remove "driver_SlimeVR" entry if the driver was disabled manually
    $SteamVrSettings.Remove("driver_SlimeVR")

    if ($SteamVrSettings.trackers -and $SteamVrSettings.trackers.Count) {
        $Trackers = New-Object -TypeName "System.Collections.Generic.Dictionary[[string], [object]]"
        foreach ($Tracker in $SteamVrSettings.trackers.GetEnumerator()) {
            if ($Tracker.Key -match "^/devices/slimevr/") {
                continue
            }
            $Trackers[$Tracker.Key] = $Tracker.Value
        }
        # Why? Because you cannot just replace key value without a circular reference error
        $SteamVrSettings.Remove('trackers')
        $SteamVrSettings.Add('trackers', $Trackers)
    }

    [System.IO.File]::WriteAllLines($SteamVrSettingsPath, $JsonSerializer.Serialize($SteamVrSettings))
}

$SteamVrPaths = @("$SteamPath\steamapps\common\SteamVR")
$res = Select-String -Path "$SteamPath\steamapps\libraryfolders.vdf" -Pattern '"path"\s+"(.+?)"' -AllMatches
foreach ($Match in $res.Matches) {
    $LibraryPath = $Match.Groups[1] -replace "\\\\", "\"
    $SteamVrPaths += "$LibraryPath\steamapps\common\SteamVR"
}

Write-Host "Attempting to find SteamVR..."
$DriverFolder = Split-Path -Path $DriverPath -Leaf
foreach ($SteamVrPath in $SteamVrPaths) {
    if (Test-Path -Path "$SteamVrPath\bin") {
        $SteamVrDriverPath = "$SteamVrPath\drivers\$DriverFolder"
        if (Test-Path -Path $SteamVrDriverPath) {
            try {
                Remove-Item -Recurse -Path $SteamVrDriverPath
            } catch [System.Management.Automation.ActionPreferenceStopException] {
                Write-Host "Failed to remove SlimeVR driver. Make sure SteamVR is closed."
                exit 1
            }
        }
        if ($Uninstall -eq $true) {
            Write-Host "Deleted SlimeVR Driver from `"$SteamVrDriverPath`""
            exit 0
        }
        try {
            Copy-Item -Recurse -Force -Path $DriverPath -Destination "$SteamVrPath\drivers\"
        } catch [System.Management.Automation.ActionPreferenceStopException] {
            Write-Host "Failed to copy new SlimeVR driver. Error: $_"
            Write-Host "Make sure SteamVR is closed and there's enough free disk space."
            exit 1
        }
        Write-Host "Installed SlimeVR Driver to `"$SteamVrDriverPath`""
        exit 0
    }
}

Write-Host "No SteamVR folder was found."
exit 1