<#
.SYNOPSIS
    Helper script for Minalac Generator.
.DESCRIPTION
    This script can combine Yaml parameters from multiple files and
    launch the Generator.jar program with that parameters. Multiple
    options are available to control the process.
#>

Param (
    # Displays this help message only.
    [Parameter(ParameterSetName = "help", Mandatory)]
    [Alias("h")]
    [Switch] $Help,

    # Stops before generation.
    [Parameter(ParameterSetName = "no-output-dir")]
    [Alias("g")]
    [Switch] $DisableGeneration,

    # Stops before saving.
    [Parameter(ParameterSetName = "no-output-dir")]
    [Alias("s")]
    [Switch] $DisableSaving,

    # Displays Yaml configuration only.
    [Parameter(ParameterSetName = "no-output-dir")]
    [Alias("y")]
    [Switch] $YamlOnly,

    # Lists available formats/processes/places only.
    [Parameter(ParameterSetName = "list", Mandatory)]
    [Alias("l")]
    [ValidateSet("formats", "processes", "places")]
    [String] $ListYaml,

    # Name of the format to use.
    # Use '-ListYaml formats' option to get the list of allowed values.
    [Parameter(ParameterSetName = "regular", Position = 0, Mandatory)]
    [Parameter(ParameterSetName = "no-output-dir", Position = 0, Mandatory)]
    [String] $Format,

    # Name of the process to use.
    # Use '-ListYaml processes' option to get the list of allowed values.
    [Parameter(ParameterSetName = "regular", Position = 1, Mandatory)]
    [Parameter(ParameterSetName = "no-output-dir", Position = 1, Mandatory)]
    [String] $Process,

    # Name of the place to use.
    # Use '-ListYaml places' option to get the list of allowed values.
    [Parameter(ParameterSetName = "regular", Position = 2, Mandatory)]
    [Parameter(ParameterSetName = "no-output-dir", Position = 2, Mandatory)]
    [String] $Place,

    # Output directory where the result will be generated.
    [Parameter(ParameterSetName = "regular", Position = 3, Mandatory)]
    [String] $OutputDir
)

# Place ourselves in script directory
Set-Location $PSScriptRoot

# Check if JAVA_CMD is set, if not, set it to java.exe
If (!(Test-Path Variable:JAVA_CMD)) {
    $JAVA_CMD = "java.exe"
}
$JAR_PATH = ".\target\Generator.jar"
$PARAMS_DIR = ".\examples"
$FORMATS_DIR = "$PARAMS_DIR\formats"
$PROCESSES_DIR = "$PARAMS_DIR\processes"
$PLACES_DIR = "$PARAMS_DIR\places"

Function Get-YamlFiles {
    Param (
        [Parameter(Position = 0, Mandatory)]
        [String] $Path,
        [String] $Prefix = ""
    )
    Get-ChildItem $Path -Filter *.yaml -File | ForEach-Object {
        Write-Output "$($Prefix)$($_.BaseName)"
    }
}

Function Get-Usage {
    Get-Help $MyInvocation.ScriptName -Detailed
    $prefix = "$([Environment]::NewLine)`t"
    Write-Host "Formats: $(Get-YamlFiles $FORMATS_DIR -Prefix $prefix)"
    Write-Host "Processes: $(Get-YamlFiles $PROCESSES_DIR -Prefix $prefix)"
    Write-Host "Places: $(Get-YamlFiles $PLACES_DIR -Prefix $prefix)"
}

If ($Help) {
    Get-Usage
    Return
}

$OutputDirNeeded = $true
$GeneratorOpts = @()

If ($DisableGeneration) {
    $GeneratorOpts += "--generation-disabled"
    $OutputDirNeeded = $false
}

If ($DisableSaving) {
    $GeneratorOpts += "--save-disabled"
    $OutputDirNeeded = $false
}

If ($YamlOnly) {
    $OutputDirNeeded = $false
}

If ($ListYaml) {
    Switch ($ListYaml) {
        "formats" { Get-YamlFiles $FORMATS_DIR }
        "processes" { Get-YamlFiles $PROCESSES_DIR }
        "places" { Get-YamlFiles $PLACES_DIR }
    }
    Return
}

If ($OutputDirNeeded -And !($OutputDir)) {
    Throw "OutputDir is missing!"
}

$FormatYaml = "$FORMATS_DIR\$Format.yaml"
If (!(Test-Path $FormatYaml -PathType Leaf)) {
    Throw "Format '$Format' is incorrect!"
}

$ProcessYaml = "$PROCESSES_DIR\$Process.yaml"
If (!(Test-Path $ProcessYaml -PathType Leaf)) {
    Throw "Process '$Process' is incorrect!"
}

$PlaceYaml = "$PLACES_DIR\$Place.yaml"
If (!(Test-Path $PlaceYaml -PathType Leaf)) {
    Throw "Place '$Place' is incorrect!"
}

If ($OutputDirNeeded) {
    If (Test-Path $OutputDir -PathType Container) {
        Remove-Item -Recurse -Force $OutputDir
    }
    If (Test-Path $OutputDir) {
        Throw "'$OutputDir' is not a directory"
    }
    New-Item -Path $OutputDir -ItemType Directory -Force | Out-Null
    $OutputDir = Resolve-Path $OutputDir
} Else {
    $OutputDir = "NUL" # Fake output that won't be used
}

$FormatParams = Get-Content $FormatYaml -Encoding UTF8
$ProcessParams = Get-Content $ProcessYaml -Encoding UTF8
$PlaceParams = Get-Content $PlaceYaml -Encoding UTF8
$Params = $FormatParams + $ProcessParams + $PlaceParams -Join [Environment]::NewLine

If ($YamlOnly) {
    Write-Host $Params
    Return
}

# Temporarily set the MINALAC_PARAMS environment variable
$Prev = $Env:MINALAC_PARAMS
$Env:MINALAC_PARAMS = $Params
& $JAVA_CMD (@("-jar", $JAR_PATH) + $GeneratorOpts + @($OutputDir))
If ($Prev) {
    $Env:MINALAC_PARAMS = $Prev
} Else {
    Remove-Item Env:\MINALAC_PARAMS
}
