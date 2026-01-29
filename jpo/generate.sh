#!/bin/bash

PLACES=places
PROCESS=../examples/processes/jpo.yaml
FORMAT=../examples/formats/minetest.yaml
JAR_PATH=./target/Generator.jar
GENERATOR=..
WORLDS=~/.minetest/worlds2
TMPDIR=/tmp/minalac.ongoing.generation/

# Place ourselves in script directory
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd "$SCRIPT_DIR" || exit

# Check if JAVA_CMD is set, if not, set it to java
JAVA_CMD=${JAVA_CMD:-java}

for f in $PLACES/*.yaml
do
   #echo Generating $f

   params=$({
      cat "$FORMAT"; echo; cat "$PROCESS"; echo; cat "$f"
   })

   output_dir=$(basename $f)
   output_dir=$WORLDS/${output_dir%.*}

   if [ -e $output_dir ]
   then
      echo
      echo SKIPED: $(basename $f) "(destination directory exists)"
      echo
   else
      echo
      echo GENERATING: $(basename $f)
      echo
      mkdir -p $TMPDIR
      cd $GENERATOR
      MINALAC_PARAMS=$params $JAVA_CMD -jar $JAR_PATH $TMPDIR
      if [ $? -ne 0 ]; then
         echo
         echo FAILED: $(basename $f)
         echo
         rm $TMPDIR/map.sqlite
         rm $TMPDIR/map_meta.txt
         rm $TMPDIR/world.mt
         rmdir $TMPDIR
      else
         cd $SCRIPT_DIR
         mkdir -p $TMPDIR/worldmods/
         cp -r mods/* $TMPDIR/worldmods/
         mv $TMPDIR $output_dir
      fi
      cd $SCRIPT_DIR
   fi
done
