package com.ignfab.minalac.generator.renderers;

import java.util.Map;

import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable3d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.Placeable;

public class ClassifiedRenderer extends ModelRenderer<Voxelizable3d> {
    private final Map<String, Placeable> classes;
    private final Placeable defaultPlace;

    public ClassifiedRenderer(ModelSelection selection, Map<String, Placeable> classes, Placeable defaultPlace) {
        super(Voxelizable3d.class, selection);
        this.classes = classes;
        this.defaultPlace = defaultPlace;
    }

    @Override
    protected void render(Voxelizable3d model, WorldBBox3d bbox) {
        Placeable place = classes.getOrDefault((String) model.getMetadata("classification"), defaultPlace);
        for (Positioned3d v : model.voxelize3d(bbox))
            place.place(v.coords().x(), v.coords().y(), v.coords().z());
    }
}
