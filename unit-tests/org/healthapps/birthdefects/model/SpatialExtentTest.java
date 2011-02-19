package org.healthapps.birthdefects.model;

import junit.framework.TestCase;

public class SpatialExtentTest extends TestCase {

    public void testContainsLocation()
    {
        final SpatialExtent spatialExtent = new SpatialExtent(new GeoLocation(19.1, 10.2), new GeoLocation(17.2, 8.3));
        assertTrue(spatialExtent.contains(new GeoLocation(18.0,9.0)));
        assertFalse(spatialExtent.contains(new GeoLocation(22.0,9.0)));
        assertFalse(spatialExtent.contains(new GeoLocation(18.0,12.0)));
    }
}
