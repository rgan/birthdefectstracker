package org.healthapps.birthdefects.dao;

import org.healthapps.birthdefects.model.EnvHazard;
import org.healthapps.birthdefects.model.GeoLocation;
import org.healthapps.birthdefects.model.SpatialExtent;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class CompassSearchTest extends AbstractTestDao {

    @Test
    public void testSearchHazards() {
        BirthDefectsDao dao = new BirthDefectsDaoImpl(PMF.get(), CompassHelper.getCompass());
        final String name = "leather goods factory";
        final String description = "bad for env";
        EnvHazard envHazard = new EnvHazard(name, description,
                "[{\"lat\": 12.1 \"lon\" : 79.1}]", new SpatialExtent(new GeoLocation(13.1, 80.4), new GeoLocation(10.1, 78.4)), "11");
        dao.store(envHazard);
        Collection<EnvHazard> envHazards = dao.searchHazards(name, 10);
        assertEquals(1, envHazards.size());
        envHazards = dao.searchHazards("invalid", 10);
        assertEquals(0, envHazards.size());
    }
}
