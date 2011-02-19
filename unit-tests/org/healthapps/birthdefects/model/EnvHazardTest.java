package org.healthapps.birthdefects.model;

import org.apache.commons.lang.StringUtils;
import org.healthapps.birthdefects.dao.AbstractTestDao;
import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.dao.BirthDefectsDaoImpl;
import org.healthapps.birthdefects.dao.PMF;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class EnvHazardTest extends AbstractTestDao {

    private BirthDefectsDao dao;
    private SpatialExtent spatialExtent;
    private static final String VALID_VERTICES = "9.1, 10.2; 10.1,9.1";
    private static final String NAIC_DESC = "farming";

    public void setUp() {
        super.setUp();
        dao = new BirthDefectsDaoImpl(PMF.get(), null);
        spatialExtent = new SpatialExtent(new GeoLocation(10.0, 10.0), new GeoLocation(5.0, 5.0));
    }

    @Test
    public void testShouldNotHaveErrorsIfValid() {
        EnvHazard hazard = new EnvHazard("name", "desc", VALID_VERTICES, spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testShouldErrorIfNameIsEmpty() {
        EnvHazard hazard = new EnvHazard("", "desc", VALID_VERTICES, spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("required"));
    }

    @Test
    public void testShouldErrorIfNameHasInvalidChars() {
        EnvHazard hazard = new EnvHazard("<invalid", "desc", VALID_VERTICES, spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("invalidCharacters"));
    }

    @Test
    public void testShouldErrorIfDescIsEmpty() {
        EnvHazard hazard = new EnvHazard("name", "", VALID_VERTICES, spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("required"));
    }

    @Test
    public void testShouldErrorIfDescHasInvalidChars() {
        EnvHazard hazard = new EnvHazard("name", "<invalid", VALID_VERTICES, spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("invalidCharacters"));
    }

    @Test
    public void testShouldErrorIfDescIsTooLong() {
        EnvHazard hazard = new EnvHazard("name", StringUtils.repeat("a", 201), VALID_VERTICES, spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("lengthExceeded"));
    }

    @Test
    public void testShouldErrorIfVerticesIsEmpty() {
        EnvHazard hazard = new EnvHazard("name", "desc", "", spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("required"));
    }

    @Test
    public void testShouldErrorIfVerticesHasInvalidChars() {
        EnvHazard hazard = new EnvHazard("name", "desc", "<invalid", spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("invalidCharacters"));
    }

    @Test
    public void testShouldErrorIfVerticesIsTooLong() {
        EnvHazard hazard = new EnvHazard("name", "desc", StringUtils.repeat("1", EnvHazard.MAX_VERTICES_LENGTH+1), spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("lengthExceeded"));
    }

    @Test
    public void testShouldErrorIfNameAlreadyExistsForNewHazard() {
        EnvHazard hazard = new EnvHazard("name", "desc", VALID_VERTICES, spatialExtent, NAIC_DESC);
        dao.store(hazard);
        hazard = new EnvHazard("name", "desc", VALID_VERTICES, spatialExtent, NAIC_DESC);
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("nameExists"));
    }

    @Test
    public void testShouldErrorIfNAICIsEmpty() {
        EnvHazard hazard = new EnvHazard("name", "desc", VALID_VERTICES, spatialExtent, "");
        final ErrorMessages errorMessages = hazard.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("required"));
    }

    @Test
    public void testShouldReturnCsv() {
        EnvHazard hazard = new EnvHazard("name", "desc", VALID_VERTICES, spatialExtent, NAIC_DESC);
        assertEquals("name,farming,desc,,,\"9.1, 10.2; 10.1,9.1\"", hazard.toCsv());
    }

}
