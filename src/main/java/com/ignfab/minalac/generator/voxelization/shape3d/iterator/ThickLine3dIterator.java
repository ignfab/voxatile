package com.ignfab.minalac.generator.voxelization.shape3d.iterator;

import java.util.Iterator;

import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.ThickLine2dIterator;
import com.ignfab.minalac.generator.voxelization.shape3d.Line3d;

// Cet itérateur n'est pas tout à fait le pendant de celui en 2d.
// En Z, il se contente de faire du "thin" (voxels non connectés en Z)

/**
 * An iterator over voxels of a {@link Line3d} with thickness.
 * Beveling could be applied to line ends to connect it in a line string.
 */
public class ThickLine3dIterator implements Iterator<Positioned3d> {

    private final Line3d line;
    private final Iterator<Positioned2d> iterator;

    /**
     * Creates a new iterator on the given line voxels with ends beveling.
     *
     * @param line the line to iterator over
     * @param thickness thickness of the line in voxels
     * @param startBevelDirection beveling direction at start line end
     * @param endBevelDirection beveling direction at end line end
     */
    public ThickLine3dIterator(Line3d line, double thickness, Vector2d startBevelDirection, Vector2d endBevelDirection) {
        this.line = line;
        iterator = new ThickLine2dIterator(line.to2d(), thickness, startBevelDirection, endBevelDirection);
    }

    /**
     * Creates a new iterator on the given line voxels without beveling.
     *
     * @param line the line to iterator over
     * @param thickness thickness of the line in voxels
     */
    public ThickLine3dIterator(Line3d line, double thickness) {
       this(line, thickness, line.to2d().direction().normal(), line.to2d().direction().normal().opposite());
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Positioned3d next() {
        Positioned2d position = iterator.next();
        // A linear Z value is simply added to 2d position
        return position.coords().to3d(line.atIndex(line.indexAt(position.coords().x(), position.coords().y())).z());
    }
}
