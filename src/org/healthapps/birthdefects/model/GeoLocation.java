package org.healthapps.birthdefects.model;

import geospatialweb.geohash.Geohash;

public class GeoLocation {
    private Double lat;
    private Double lon;

    public GeoLocation(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String geohash() {
        return new Geohash().encode(lat, lon);
    }

    public String toString() {
        return "Lat:" + lat + " lon:" + lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoLocation location = (GeoLocation) o;

        if (lat != null ? !lat.equals(location.lat) : location.lat != null) return false;
        if (lon != null ? !lon.equals(location.lon) : location.lon != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lat != null ? lat.hashCode() : 0;
        result = 31 * result + (lon != null ? lon.hashCode() : 0);
        return result;
    }
}
