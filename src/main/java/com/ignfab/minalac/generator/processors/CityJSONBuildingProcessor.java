package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.models.CityBuildingModel;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.PlanarPolygon;
import org.citygml4j.core.model.building.Building;
import org.citygml4j.core.model.construction.GroundSurface;
import org.citygml4j.core.model.construction.RoofSurface;
import org.citygml4j.core.model.construction.WallSurface;
import org.citygml4j.core.model.core.AbstractCityObject;
import org.citygml4j.core.model.core.AbstractGenericAttribute;
import org.citygml4j.core.model.core.AbstractGenericAttributeProperty;
import org.xmlobjects.gml.model.geometry.DirectPosition;
import org.xmlobjects.gml.model.geometry.GeometryProperty;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurface;
import org.xmlobjects.gml.model.geometry.primitives.AbstractRingProperty;
import org.xmlobjects.gml.model.geometry.primitives.LinearRing;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.model.geometry.primitives.SurfaceProperty;
import org.xmlobjects.model.Child;

import java.util.ArrayList;
import java.util.List;

public class CityJSONBuildingProcessor implements Processor<AbstractCityObject, CityBuildingModel> {
    private final MapToWorldConverter converter;

    public CityJSONBuildingProcessor(MapToWorldConverter converter) {
        this.converter = converter;
    }

    @Override
    public Class<AbstractCityObject> acceptedType() {
        return AbstractCityObject.class;
    }

    @Override
    public Class<CityBuildingModel> modelType() {
        return CityBuildingModel.class;
    }

    @Override
    public CityBuildingModel process(AbstractCityObject object) throws GenerationFailedException, IgnorableException {
        if (!(object instanceof Building building))
            return null;

        // Faces of the building (lists of planar polygons)
        List<PlanarPolygon> ground = new ArrayList<>();
        List<PlanarPolygon> walls = new ArrayList<>();
        List<PlanarPolygon> roof = new ArrayList<>();
        for (GeometryProperty<?> geometry : building.getGeometryInfo(true).getGeometries()) {
            if (geometry.getObject() instanceof MultiSurface surfaces) {
                Child parent = geometry.getParent();
                List<PlanarPolygon> polygons;
                if (parent instanceof GroundSurface)
                    polygons = ground;
                else if (parent instanceof WallSurface)
                    polygons = walls;
                else if (parent instanceof RoofSurface)
                    polygons = roof;
                else
                    continue;
                for (SurfaceProperty surfaceMember : surfaces.getSurfaceMember()) {
                    if (surfaceMember.getObject() instanceof Polygon polygon) {
                        List<PlanarPolygon.Coords3d> shell;
                        if (polygon.getExterior().getObject() instanceof LinearRing linearRing)
                            shell = linearRingToCoords(linearRing);
                        else
                            continue;
                        List<List<PlanarPolygon.Coords3d>> holes = new ArrayList<>();
                        for (AbstractRingProperty interior : polygon.getInterior())
                            if (interior.getObject() instanceof LinearRing hole)
                                holes.add(linearRingToCoords(hole));
                        try {
                            polygons.add(new PlanarPolygon(shell, holes));
                        } catch (PlanarPolygon.IllegalPolygonException e) {
                            //throw new IgnorableException(e); // Bad idea to ignore whole model because of 1 invalid polygon (often being way too small)
                        }
                    }
                }
            }
        }
        if (ground.isEmpty() && walls.isEmpty() && roof.isEmpty())
            return null;

        // Center of the building (approximated to the center of the bbox)
        DirectPosition mapCenter = building.getBoundedBy().getEnvelope().getCenter();
        WorldCoords2d center2d;
        try {
            center2d = converter.convert(new MapCoordinates(mapCenter.getValue().get(0), mapCenter.getValue().get(1)));
        } catch (TransformException e) {
            throw new IgnorableException(e);
        }
        WorldCoords3d center = center2d.to3d((int) Math.round(mapCenter.getValue().get(2)));

        CityBuildingModel model = new CityBuildingModel(ground, walls, roof, center);

        // Attributes
        for (AbstractGenericAttributeProperty attributeProperty : building.getGenericAttributes()) {
            AbstractGenericAttribute<?> attribute = attributeProperty.getObject();
            model.setMetadata(attribute.getName(), attribute.getValue());
        }
        return model;
    }

    private List<PlanarPolygon.Coords3d> linearRingToCoords(LinearRing linearRing) throws IgnorableException {
        List<Double> ring = linearRing.toCoordinateList3D();
        List<PlanarPolygon.Coords3d> coords = new ArrayList<>(ring.size() / 3);
        for (int i = 0; i < ring.size(); i += 3) {
            MapCoordinates convert;
            try {
                convert = converter.convertRaw(new MapCoordinates(ring.get(i), ring.get(i + 1)));
            } catch (TransformException e) {
                throw new IgnorableException(e);
            }
            coords.add(new PlanarPolygon.Coords3d(convert, ring.get(i + 2)));
        }
        return coords;
    }
}
