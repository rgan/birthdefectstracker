package org.healthapps.birthdefects.model;

import java.util.List;
import java.util.ArrayList;

public class SpatialExtent {
    private GeoLocation ne; // upper right
    private GeoLocation sw; // lower left

    public SpatialExtent(GeoLocation ne, GeoLocation sw) {
        this.ne = ne;
        this.sw = sw;
    }

    public GeoLocation getNE() {
        return ne;
    }

    public GeoLocation getSW() {
        return sw;
    }

    public Double getNELat() {
        return ne.getLat();
    }

    public Double getSWLat() {
        return sw.getLat();
    }

    public String toString() {
        return ne.toString() + ";" + sw.toString();
    }

    public List<GeoLocation> getVertices() {
        List<GeoLocation> vertices = new ArrayList<GeoLocation>();
        vertices.add(new GeoLocation(getNE().getLat(), getNE().getLon()));
        vertices.add(new GeoLocation(getNE().getLat(), getSW().getLon()));
        vertices.add(new GeoLocation(getSW().getLat(), getSW().getLon()));
        vertices.add(new GeoLocation(getSW().getLat(), getNE().getLon()));
        vertices.add(new GeoLocation(getNE().getLat(), getNE().getLon()));
        return vertices;    
    }

    public boolean contains(GeoLocation location) {
        return location.getLat() < ne.getLat() && location.getLat() > sw.getLat() &&
                location.getLon() < ne.getLon() && location.getLon() > sw.getLon();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpatialExtent that = (SpatialExtent) o;

        if (ne != null ? !ne.equals(that.ne) : that.ne != null) return false;
        if (sw != null ? !sw.equals(that.sw) : that.sw != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ne != null ? ne.hashCode() : 0;
        result = 31 * result + (sw != null ? sw.hashCode() : 0);
        return result;
    }
}
