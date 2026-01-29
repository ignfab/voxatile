#!/bin/bash

SOURCE=../data/bati-jpo.city.json
TARGET=../data/bati-jpo-selection.city.json
POINTS=centres-lambert.geojson
#POINTS=test-point.geojson

sanitize() {
   local s=$1
   s="${s//[^[:alnum:]]/-}"
   s="${s//--/-}"
   s="${s//--/-}"
   s="${s//--/-}"
   s="${s/#-}"
   s="${s/%-}"
   echo "${s,,}"
}

coords=$(jq -r '.features[]| "\(.properties.Fichier)|\(.geometry.coordinates[0])|\(.geometry.coordinates[1])"' $POINTS)

WORKDIR=tempbati

ext=750
mkdir -p $WORKDIR

IFS=$'\n'
for c in $coords; do

    title=$(echo "$c" | cut -d\| -f1)
    name=$(sanitize "$title")

    echo "title:" $title
    echo "name:" $name

    x=$(echo "$c" | cut -d\| -f2 | cut -d. -f1)
    y=$(echo "$c" | cut -d\| -f3 | cut -d. -f1)

    minX=$(expr $x - $ext)
    maxX=$(expr $x + $ext)
    minY=$(expr $y - $ext)
    maxY=$(expr $y + $ext)
    echo "$minX $minY $maxX $maxY"

    if [ -f $WORKDIR/$name.city.json ]
    then
        echo SKIPED $WORKDIR/$name.city.json "(file exists)"
    else
        echo "cjio $SOURCE subset --bbox $minX $minY $maxX $maxY save $WORKDIR/$name.city.json"
        cjio $SOURCE subset --bbox $minX $minY $maxX $maxY save $WORKDIR/$name.city.json
    fi
done

echo '{"type":"CityJSON","version":"2.0","CityObjects":{},"vertices":[],"transform":{"scale":[0.001000,0.001000,0.001000],"translate":[646079.400000,6857943.101000,-1000.000000]},"metadata":{"geographicalExtent":[0,0,0,0,0,0],"identifier":"d0df1b7f-e714-4ba3-90c4-badb3d490afe","referenceDate":"2026-01-26","referenceSystem":"https://www.opengis.net/def/crs/EPSG/0/2154"}}'> $WORKDIR/empty.city.json
#cjio $WORKDIR/empty.city.json merge "$WORKDIR/*.city.json" save $TARGET
