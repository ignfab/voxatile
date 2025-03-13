#!/bin/bash

# Place ourselves in script directory
cd "${0%/*}"

JAVA_CMD=${JAVA_CMD:-java}
JAR_PATH=./target/Generator.jar
PARAMS_DIR=./examples
FORMATS_DIR=$PARAMS_DIR/formats
PROCESSES_DIR=$PARAMS_DIR/processes
PLACES_DIR=$PARAMS_DIR/places

usage() {
    echo "$0 [options] <format> <process> <place> [<outputdir>]"
    echo "available options:"
    echo "-g Stops before generation"
    echo "-s Stops before saving"
    echo "-y Display Yaml configuration only"
    echo "formats:"
    ls -1 $FORMATS_DIR | grep '.yaml$' | sed -r 's/(.*)\.yaml$/\t\1/'
    echo "processes:"
    ls -1 $PROCESSES_DIR | grep '.yaml$' | sed -r 's/(.*)\.yaml$/\t\1/'
    echo "places:"
    ls -1 $PLACES_DIR | grep '.yaml$' | sed -r 's/(.*)\.yaml$/\t\1/'
    echo "outputdir is required if no option given (if directory exists, it will be emptied)"
}

output_dir_needed=1
generator_opt=""

while [[ "$1" == "-"* ]]; do
    opt=$1
    shift
    case $opt in
        -g)
            generator_opt="$generator_opt --generation-disabled"
            unset output_dir_needed
            ;;
        -s)
            generator_opt="$generator_opt --save-disabled"
            unset output_dir_needed
            ;;
        -y)
            display_only=1
            unset output_dir_needed
            ;;
        *)
            echo "Unknown option $opt"
            usage
            exit 1
            ;;
    esac
done

if [ $output_dir_needed ]; then
    nargs=4
else
    nargs=3
fi

if [[ $# -ne $nargs ]]; then
    usage
    exit 1
fi

format="$FORMATS_DIR/$1.yaml"
if [ ! -f "$format" ]; then
    echo "Format '$1' is incorrect!"
    usage
    exit 1
fi

process="$PROCESSES_DIR/$2.yaml"
if [ ! -f "$process" ]; then
    echo "Process '$2' is incorrect!"
    usage
    exit 1
fi

place="$PLACES_DIR/$3.yaml"
if [ ! -f "$place" ]; then
    echo "Place '$3' is incorrect!"
    usage
    exit 1
fi

if [ $output_dir_needed ]; then
    output_dir="$4"
    if [ -d "$output_dir" ]; then
        rm -r "$output_dir"
        if [ $? -ne 0 ]; then
            echo "Could not delete directory $output_dir"
            exit 1
        fi
    fi
    if [ -e "$output_dir" ]; then
        echo "$output_dir is not a directory"
        exit 1
    fi
    mkdir -p "$output_dir"
    if [ $? -ne 0 ]; then
        echo "Could create directory $output_dir"
        exit 1
    fi
    output_dir=$(realpath "$output_dir")
fi

params=$({
  cat "$format"; echo; cat "$process"; echo; cat "$place"
})

if [ $display_only ]; then
    echo "$params"
    exit 0
fi

MINALAC_PARAMS=$params $JAVA_CMD -jar $JAR_PATH $generator_opt "$output_dir"
