package com.ignfab.minalac.generator.generation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.MapWriteException;

/**
 * La {@code Minimap} permet de rendre la carte du monde dans un format réduit,
 * permettant son intégration dans une mini-carte en jeu.
 */
public class Minimap {
    // La taille max qui doit faire la minimap
    private static final int SIZE_MAX = 1024;

    // Contiens les hauteurs sommé
    private int[][] heightmap;
    // Contiens le nombre d'occurence d'hauteurs sommé
    private int[][] occurrences;

    // Utilisé pour decaler a 0 les coordonées d'origine du monde
    private int worldMinX;
    private int worldMinY;

    private double samplingRate;
    private int sizeX;
    private int sizeY;

    /**
     * Create a new {@code Minimap}.
     *
     * @param bbox les informations du monde.
     */
    public Minimap(WorldBBox3d bbox) {
        worldMinX = bbox.minX();
        worldMinY = bbox.minY();

        samplingRate = (double) SIZE_MAX / Math.max(bbox.sizeX(), bbox.sizeY());
        sizeX = (int) (samplingRate * bbox.sizeX());
        sizeY = (int) (samplingRate * bbox.sizeY());

        heightmap = new int[sizeX][sizeY];
        occurrences = new int[sizeX][sizeY];
    }

    /**
     * Ajoute une hauteur à la mini carte.
     *
     * @param pos coordonnée du voxel dans le monde
     */
    public void set(WorldCoords3d pos) {
        // Convertion des coordonnées du monde vers un indice utilisé dans les tableaux
        // defini dans la classe Minimap
        int x = (int) (samplingRate * (pos.x() - worldMinX));
        int y = (int) (samplingRate * (pos.y() - worldMinY));

        // Permet de faire une moyenne des hauteurs plus tard
        heightmap[x][y] += pos.z();
        occurrences[x][y]++;
    }

    /**
     * Rend la carte des hauteurs du monde sous-échantillonnée au format PNG.
     *
     * @throws MapWriteException
     */
    public void save() throws MapWriteException {
        BufferedImage image = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < sizeX; x++) {
            double lastZ = 0;
            for (int y = 0; y < sizeY; y++) {
                double height = (double) heightmap[x][y] / occurrences[x][y];

                // Module le niveau de gris autours de la valeur '128' pour les ombres
                int diff = 128 + (int) Math.round((lastZ - height) * 10);
                if (diff > 255) diff = 255;
                if (diff < 0) diff = 0;
                lastZ = height;

                Color color = new Color(diff, diff, diff);
                image.setRGB(x, sizeY - (y + 1), color.getRGB());
            }
        }

        // Sauvegarde l'image de la minimap a la racine du projet
        try {
            javax.imageio.ImageIO.write(image, "png", new File("heightmap.png"));
        } catch (IOException e) {
            throw new MapWriteException(e);
        }
    }
}
