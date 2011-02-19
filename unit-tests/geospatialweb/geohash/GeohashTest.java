package geospatialweb.geohash;

import junit.framework.TestCase;

public class GeohashTest extends TestCase {

    public void testShouldCreateHashForLatLng() {
        final Geohash geohash = new Geohash();
        String encoded = geohash.encode(-30.1, 90.0);
        double[] latlon = geohash.decode(encoded);
        assertEquals(-30.1, latlon[0], 0.001);
        assertEquals(90.0, latlon[1], 0.001);
    }

    public void testShouldReturnTrueIfPointIsBeforeAnother() {
        final Geohash geohash = new Geohash();
        String encodedPt1 = geohash.encode(14.0, 79.0);
        String encodedPt2 = geohash.encode(13.0, 78.0);
        assertTrue(encodedPt2.compareTo(encodedPt1) < 0);
    }
}
