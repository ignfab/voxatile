#!/bin/bash

# Needs pandoc to be installed
which pandoc > /dev/null || { echo "Needs 'pandoc' to be installed"; exit 1; }

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd "$SCRIPT_DIR" || { echo "Can't enter directory $SCRIPT_DIR"; exit 1; }

MD=../docs
HTML=../htmldocs

cp -r "$MD" "$HTML"
cd "$HTML" || { echo "Can't enter directory $HTML"; exit 1; }

for input in $(find . -name '*.md')
do
  echo "Converting $input"
  output=${input//.md/.html}
  pandoc --quiet --lua-filter="$SCRIPT_DIR/htmldocs_filter.lua" --standalone --css="$SCRIPT_DIR/htmldocs_style.css" -f gfm -t html5 "$input" -o "$output"
  rm $input
done
