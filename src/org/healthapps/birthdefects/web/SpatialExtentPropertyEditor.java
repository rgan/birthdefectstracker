package org.healthapps.birthdefects.web;

import org.healthapps.birthdefects.model.GeoLocation;
import org.healthapps.birthdefects.model.SpatialExtent;

import java.beans.PropertyEditorSupport;

public class SpatialExtentPropertyEditor extends PropertyEditorSupport {
    // nelat, nelon, swlat, swlon
    public void setAsText(String text) throws IllegalArgumentException {
        String[] extents = text.split(",");
        try {
            GeoLocation ne = new GeoLocation(Double.parseDouble(extents[0]), Double.parseDouble(extents[1]));
            GeoLocation sw = new GeoLocation(Double.parseDouble(extents[2]), Double.parseDouble(extents[3]));
            SpatialExtent spatialExtent = new SpatialExtent(ne, sw);
            setValue(spatialExtent);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid values for extent:" + text);
        }
    }
}
