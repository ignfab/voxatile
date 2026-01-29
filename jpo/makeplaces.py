#!/usr/bin/python3

import json
import unicodedata

with open('Points.geojson') as f:
    c = json.load(f)

for f in c['features']:
    name = f['properties']['Code']
    filename = f['properties']['Fichier']
    with open("places/"+filename+".yaml", 'w') as out:
        out.write("worldName: " + name + "\n")
        out.write("verticalScale: 0.5\n")
        out.write("horizontalScale: 0.5\n")
        out.write("area:\n")
        out.write("  center:\n")
        out.write("    latitude: " + str(f['geometry']['coordinates'][1]) + "\n")
        out.write("    longitude: " + str(f['geometry']['coordinates'][0]) + "\n")
        out.write("  extentX: 1400\n")
        out.write("  extentY: 1400\n")
