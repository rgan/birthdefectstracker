package org.healthapps.birthdefects.model;

import junit.framework.TestCase;

public class CSquareCodeTest extends TestCase {

    public void testShouldEncodeFromLatLon() {
        CSquareCode code = CSquareCode.from(87.5, -177.5);
        // 10x10 - 108569.4 sq km
        assertEquals(7817, code.getTens());
        // 1x1 - 100 sq km
        assertEquals(477, code.getUnits());
        // 0.1 x 0.1 - 10 sq km
        assertEquals(455, code.getTenths());
    }

    public void testShouldReturnBoundingBoxForTensCode() {
        final SpatialExtent extent = CSquareCode.boundingBoxfrom(7817);
        assertEquals(90.0, extent.getNE().getLat());
        assertEquals(-170.0, extent.getNE().getLon());
        assertEquals(80.0, extent.getSW().getLat());
        assertEquals(-180.0, extent.getSW().getLon());
    }

    public void testShouldReturnBoundingBoxForTensCode2() {
        final SpatialExtent extent = CSquareCode.boundingBoxfrom(3414);
        assertEquals(-40.0, extent.getNE().getLat());
        assertEquals(150.0, extent.getNE().getLon());
        assertEquals(-50.0, extent.getSW().getLat());
        assertEquals(140.0, extent.getSW().getLon());
    }

    public void testShouldReturnOneByOneBoundingBox() {
        final SpatialExtent extent = CSquareCode.boundingBoxfrom(3414, 227);
        assertEquals(-42.0, extent.getNE().getLat());
        assertEquals(-43.0, extent.getSW().getLat());
        assertEquals(147.0, extent.getSW().getLon());
        assertEquals(148.0, extent.getNE().getLon());
    }
}
