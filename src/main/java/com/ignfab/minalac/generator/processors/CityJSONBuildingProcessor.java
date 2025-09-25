package com.ignfab.minalac.generator.processors;

import java.util.ArrayList;
import java.util.List;

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

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.models.CityBuildingModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;
import com.ignfab.minalac.generator.utils.coordinates.MapCoordinates3d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.PlanarPolygon;


public class CityJSONBuildingProcessor extends ConvertingProcessor<AbstractCityObject, CityBuildingModel> {

    public CityJSONBuildingProcessor(CoordsConverterProvider converterProvider) {
        super(converterProvider);
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
                        List<Vector3d> shell;
                        if (polygon.getExterior().getObject() instanceof LinearRing linearRing)
                            shell = linearRingToCoords(linearRing);
                        else
                            continue;
                        List<List<Vector3d>> holes = new ArrayList<>();
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
        WorldCoords3d center;
        try {
            center = converter.convert(new MapCoordinates3d(mapCenter.getValue().get(0), mapCenter.getValue().get(1), mapCenter.getValue().get(2)));
        } catch (TransformException e) {
            throw new IgnorableException(e);
        }

        CityBuildingModel model = new CityBuildingModel(ground, walls, roof, center);

        // Attributes
        for (AbstractGenericAttributeProperty attributeProperty : building.getGenericAttributes()) {
            AbstractGenericAttribute<?> attribute = attributeProperty.getObject();
            model.setMetadata(attribute.getName(), attribute.getValue());
        }
        return model;
    }

    private List<Vector3d> linearRingToCoords(LinearRing linearRing) throws IgnorableException {
        List<Double> ring = linearRing.toCoordinateList3D();
        List<Vector3d> coords = new ArrayList<>(ring.size() / 3);
        for (int i = 0; i < ring.size(); i += 3) {
            MapCoordinates3d convert;
            try {
                convert = converter.convertRaw(new MapCoordinates3d(ring.get(i), ring.get(i + 1), ring.get(i + 2)));
            } catch (TransformException e) {
                throw new IgnorableException(e);
            }
            coords.add(new Vector3d(convert.x(), convert.y(), convert.z()));
        }
        return coords;
    }
}
