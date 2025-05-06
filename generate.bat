@echo off

:: Place ourselves in script directory
cd "%~dp0"

:: We use batch-style option prefix '/' instead of unix-style '-'
set OPT_PREFIX=/
set GENERATE_BAT=%~0
:: Check if JAVA_CMD is set, if not, set it to java.exe
if not defined JAVA_CMD set JAVA_CMD=java.exe
set JAR_PATH=.\target\Generator.jar
set PARAMS_DIR=.\examples
:: Must use a temp file to hold the params as variables can't contain newlines
set TEMP_PARAMETERS_YAML_FILE=%PARAMS_DIR%\temp.yaml
set FORMATS_DIR=%PARAMS_DIR%\formats
set PROCESSES_DIR=%PARAMS_DIR%\processes
set PLACES_DIR=%PARAMS_DIR%\places

set output_dir_needed=1
set generator_opt=
set display_only=

:: Count args shifted while processing options
set /a nargs=0
:process_cli_options
    set opt=%~1
    :: If no arg is present or arg does not start with OPT_PREFIX, stop processing
    if "%opt%"=="" goto cli_options_processed
    if not "%opt:~0,1%"=="%OPT_PREFIX%" goto cli_options_processed
    shift
    set /a nargs+=1
    :: Remove OPT_PREFIX from opt
    set opt=%opt:~1%
    if /i "%opt%"=="h" (
        call :usage
        exit /b 0
    ) else if /i "%opt%"=="g" (
        set generator_opt=%generator_opt% --generation-disabled
        set output_dir_needed=
    ) else if /i "%opt%"=="s" (
        set generator_opt=%generator_opt% --save-disabled
        set output_dir_needed=
    ) else if /i "%opt%"=="y" (
        set display_only=1
        set output_dir_needed=
    ) else if /i "%opt%"=="l" (
        if /i "%~1"=="formats" (
            call :list_yaml_files %FORMATS_DIR%
        ) else if /i "%~1"=="processes" (
            call :list_yaml_files %PROCESSES_DIR%
        ) else if /i "%~1"=="places" (
            call :list_yaml_files %PLACES_DIR%
        ) else (
            echo Unknown %OPT_PREFIX%l option '%~1'
            exit /b 1
        )
        exit /b 0
    ) else (
        echo Unknown option '%OPT_PREFIX%%opt%'
        call :usage
        exit /b 1
    )
:: Loop
goto process_cli_options
:: End of loop
:cli_options_processed

if defined output_dir_needed (set /a nargs+=4) else (set /a nargs+=3)
call :argc %*
if not %argc%==%nargs% (
    echo Wrong number of arguments, expected %nargs% given %argc%
    call :usage
    exit /b 1
)

set format=%FORMATS_DIR%\%~1.yaml
if not exist "%format%" (
    echo Format '%~1' is incorrect!
    call :usage
    exit /b 1
)

set process=%PROCESSES_DIR%\%~2.yaml
if not exist "%process%" (
    echo Process '%~2' is incorrect!
    call :usage
    exit /b 1
)

set place=%PLACES_DIR%\%~3.yaml
if not exist "%place%" (
    echo Place '%~3' is incorrect!
    call :usage
    exit /b 1
)

set output_dir=%~f4
if defined output_dir_needed (
    :: Backslash at the end tests for directory
    if exist "%output_dir%\" (
        rmdir /s /q "%output_dir%"
        if errorlevel 1 (
            echo Could not delete directory '%output_dir%'
            exit /b 1
        )
    )
    mkdir "%output_dir%"
    if errorlevel 1 (
        echo Could not create directory '%output_dir%'
        exit /b 1
    )
)

if exist "%TEMP_PARAMETERS_YAML_FILE%" (
    echo Temporary file '%TEMP_PARAMETERS_YAML_FILE%' already exist and would be overridden by the current operation. Aborting!
    exit /b 1
)

:: UTF-8 encoding
chcp 65001 > nul
:: Batch variables cannot hold multiline strings, thus we must use a file
type "%format%" > "%TEMP_PARAMETERS_YAML_FILE%"
echo: >> "%TEMP_PARAMETERS_YAML_FILE%"
type "%process%" >> "%TEMP_PARAMETERS_YAML_FILE%"
echo: >> "%TEMP_PARAMETERS_YAML_FILE%"
type "%place%" >> "%TEMP_PARAMETERS_YAML_FILE%"

if defined display_only (
    type "%TEMP_PARAMETERS_YAML_FILE%"
) else (
    "%JAVA_CMD%" -jar "%JAR_PATH%" %generator_opt% -p "%TEMP_PARAMETERS_YAML_FILE%" "%output_dir%"
)

del "%TEMP_PARAMETERS_YAML_FILE%"
exit /b 0

:argc
    set /a argc=0
    :argc_loop
        if "%~1"=="" exit /b
        set /a argc+=1
        shift
    goto argc_loop
exit /b

:list_yaml_files
    for %%f in ( %~1\*.yaml ) do echo %~2%%~nf
exit /b

:usage
    echo %GENERATE_BAT% [options] ^<format^> ^<process^> ^<place^> [^<outputdir^>]
    echo available options:
    echo %OPT_PREFIX%h Displays this help message only
    echo %OPT_PREFIX%g Stops before generation
    echo %OPT_PREFIX%s Stops before saving
    echo %OPT_PREFIX%y Displays Yaml configuration only
    echo %OPT_PREFIX%l (formats^|processes^|places) Lists available formats/processes/places only
    echo formats:
    call :list_yaml_files %FORMATS_DIR% "  "
    echo processes:
    call :list_yaml_files %PROCESSES_DIR% "  "
    echo places:
    call :list_yaml_files %PLACES_DIR% "  "
    echo outputdir is required if no option given (if directory exists, it will be emptied)
exit /b
