package org.healthapps.birthdefects.dao;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import org.healthapps.birthdefects.model.*;
import org.healthapps.birthdefects.utils.TestUtils;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class BirthDefectsDaoImplTest extends AbstractTestDao {

    private BirthDefectsDao dao;
    private User createdByUser;

    public void setUp() {
        super.setUp();
        dao = new BirthDefectsDaoImpl(PMF.get(), null);
        createdByUser = new UserBuilder().build();
    }

    @Test
    public void testShouldStoreBirthDefect() {
        BirthDefect birthDefect = new BirthDefect("spina bifida", "400");
        dao.store(birthDefect);

        final PreparedQuery preparedQuery = getPreparedQuery(BirthDefect.class.getSimpleName());
        assertEquals(1, preparedQuery.countEntities());
        final Entity entity = preparedQuery.asSingleEntity();
        assertEquals("spina bifida", entity.getProperty("name"));
    }

    @Test
    public void testShouldStoreEnvHazard() {
        final String name = "leather goods factory";
        final String description = "bad for env";
        EnvHazard envHazard = new EnvHazard(name, description,
                "[{\"lat\": 12.1 \"lon\" : 79.1}]", new SpatialExtent(new GeoLocation(13.1, 80.4), new GeoLocation(10.1, 78.4)), "11");
        dao.store(envHazard);

        final PreparedQuery preparedQuery = getPreparedQuery(EnvHazard.class.getSimpleName());
        assertEquals(1, preparedQuery.countEntities());
        final Entity entity = preparedQuery.asSingleEntity();
        assertEquals(name, entity.getProperty("name"));
        assertEquals(description, entity.getProperty("description"));
    }

    @Test
    public void testShouldDeleteEnvHazard() {
        final String name = "leather goods factory";
        final String description = "bad for env";
        EnvHazard envHazard = new EnvHazard(name, description,
                "[{\"lat\": 12.1 \"lon\" : 79.1}]", new SpatialExtent(new GeoLocation(13.1, 80.4), new GeoLocation(10.1, 78.4)), "11");
        dao.store(envHazard);
        dao.delete(envHazard);
        final PreparedQuery preparedQuery = getPreparedQuery(EnvHazard.class.getSimpleName());
        assertEquals(0, preparedQuery.countEntities());
    }

    @Test
    public void testShouldRetrieveAllBirthDefects() {
        final String name = "spina bifida";
        final String code = "400";
        BirthDefect birthDefect = new BirthDefect(name, code);
        dao.store(birthDefect);

        final Collection<BirthDefect> birthDefects = dao.allBirthDefects();
        assertEquals(1, birthDefects.size());
        final BirthDefect defect = birthDefects.iterator().next();
        assertEquals(name, defect.getName());
        assertEquals(code, defect.getCode());
    }

    @Test
    public void testFindDefectByNameShouldReturnIfNoneExists() {
        final BirthDefect birthDefectFound = dao.findBirthDefectByName("nonexistent");
        assertNull(birthDefectFound);
    }

    @Test
    public void testShouldFindDefectByName() {
        final String name = "spina bifida";
        final String code = "400";
        BirthDefect birthDefect = new BirthDefect(name, code);
        dao.store(birthDefect);

        final BirthDefect birthDefectFound = dao.findBirthDefectByName(name);
        assertNotNull(birthDefectFound);
        assertEquals(name, birthDefectFound.getName());
    }

    @Test
    public void testSpatialSearchShouldReturnPersonValuesWhenCriteriaMatch() {
        final String name = "BabyX";
        final Set<Long> defectIds = TestUtils.defectsWithOneItem(1l);
        final Date dateOfBirth = new Date();
        Person person = new Person(name, dateOfBirth, new GeoLocation(12.6655, 79.4001), defectIds, createdByUser);
        dao.store(person);
        final Collection<? extends Locatable> persons = dao.search(new SpatialExtent(new GeoLocation(13.58192, 80.2661132), new GeoLocation(12.43839, 78.753417)), 2);
        assertEquals(1, persons.size());
        final Person personFound = (Person) persons.iterator().next();
        assertEquals(name, personFound.getName());
    }

    @Test
    public void testSpatialSearchShouldReturnEnvHazardValuesWhenCriteriaMatch() {
        final String name = "leather goods factory";
        final String description = "bad for env";
        EnvHazard envHazard = new EnvHazard(name, description,
                "[{\"lat\": 12.1 \"lon\" : 79.1}]", new SpatialExtent(new GeoLocation(13.1, 80.4), new GeoLocation(10.1, 78.4)), "11");
        dao.store(envHazard);
        final Collection<? extends Locatable> hazards = dao.search(new SpatialExtent(new GeoLocation(13.58192, 80.9), new GeoLocation(9.43839, 78.05)), 2);
        assertEquals(1, hazards.size());
        final EnvHazard hazardFound = (EnvHazard) hazards.iterator().next();
        assertEquals(name, hazardFound.getName());
    }

    @Test
    public void testSpatialSearchShouldReturnNoValuesIfNoMatchesFoundForLatLon() {
        final String name = "BabyX";
        final Set<Long> defectIds = TestUtils.defectsWithOneItem(1l);
        final Date dateOfBirth = new Date();
        Person person = new Person(name, dateOfBirth, new GeoLocation(12.1655, 79.4001), defectIds, createdByUser);
        dao.store(person);
        //Lat:13.581920900545844 lon:80.26611328125;Lat:12.543839666237682 lon:78.475341796875
        Collection<? extends Locatable> persons = dao.search(new SpatialExtent(new GeoLocation(13.58192, 80.2661132), new GeoLocation(12.43839, 78.753417)), 2);
        assertEquals(0, persons.size());
        persons = dao.search(new SpatialExtent(new GeoLocation(10.58192, 80.2661132), new GeoLocation(9.43839, 78.753417)), 2);
        assertEquals(0, persons.size());
        persons = dao.search(new SpatialExtent(new GeoLocation(13.58192, 79.0661132), new GeoLocation(11.43839, 78.753417)), 2);
        assertEquals(0, persons.size());
        persons = dao.search(new SpatialExtent(new GeoLocation(13.58192, 80.2661132), new GeoLocation(11.43839, 79.753417)), 2);
        assertEquals(0, persons.size());
    }

    @Test
    public void testSearchShouldReturnValuesWhenCriteriaMatch() {
        final String name = "BabyX";
        final Set<Long> defectIds = TestUtils.defectsWithOneItem(1l);
        final Date dateOfBirth = new Date();
        Person person = new Person(name, dateOfBirth, new GeoLocation(20.1, 10.4), defectIds, createdByUser);
        dao.store(person);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.MONTH, -1);
        Date fromDate = calendar.getTime();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.MONTH, 1);
        Date toDate = calendar.getTime();
        final Collection<Person> persons = dao.search(fromDate, toDate, new Long(1), 10);
        assertEquals(1, persons.size());
        final Person personFound = persons.iterator().next();
        assertEquals(name, personFound.getName());
    }

    @Test
    public void testSearchShouldNotReturnValuesIfDateIsOutOfGivenRange() {
        final String name = "BabyX";
        final Set<Long> defectIds = TestUtils.defectsWithOneItem(1l);
        final Date dateOfBirth = new Date();
        Person person = new Person(name, dateOfBirth, new GeoLocation(20.1, 10.4), defectIds, createdByUser);
        dao.store(person);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.MONTH, 1);
        Date fromDate = calendar.getTime();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.MONTH, 2);
        Date toDate = calendar.getTime();
        final Collection<Person> persons = dao.search(fromDate, toDate, new Long(1), 10);
        assertEquals(0, persons.size());
    }

    @Test
    public void testSearchShouldNotReturnValuesIfNoMatchingDefectIds() {
        final String name = "BabyX";
        final Set<Long> defectIds = TestUtils.defectsWithOneItem(1l);
        final Date dateOfBirth = new Date();
        Person person = new Person(name, dateOfBirth, new GeoLocation(20.1, 10.4), defectIds, createdByUser);
        dao.store(person);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.MONTH, 1);
        Date fromDate = calendar.getTime();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.MONTH, 2);
        Date toDate = calendar.getTime();
        final Collection<Person> persons = dao.search(fromDate, toDate, new Long(2), 10);
        assertEquals(0, persons.size());
    }

    @Test
    public void testSearchShouldNotFilterByDefectsIfDefectIdIsNull() {
        final String name = "BabyX";
        final Set<Long> defectIds = TestUtils.defectsWithOneItem(1l);
        final Date dateOfBirth = new Date();
        Person person = new Person(name, dateOfBirth, new GeoLocation(20.1, 10.4), defectIds, createdByUser);
        dao.store(person);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.MONTH, -1);
        Date fromDate = calendar.getTime();
        calendar.setTime(dateOfBirth);
        calendar.add(Calendar.MONTH, 1);
        Date toDate = calendar.getTime();
        final Collection<Person> persons = dao.search(fromDate, toDate, null, 10);
        assertEquals(1, persons.size());
        final Person personFound = persons.iterator().next();
        assertEquals(name, personFound.getName());
    }

    @Test
    public void testShouldFindHazardByName() {
        final String name = "leather goods factory";
        final String description = "bad for env";
        EnvHazard envHazard = new EnvHazard(name, description,
                "[{\"lat\": 12.1 \"lon\" : 79.1}]", new SpatialExtent(new GeoLocation(13.1, 80.4), new GeoLocation(10.1, 78.4)), "11");
        dao.store(envHazard);
        assertNotNull(dao.findEnvHazardByName(name));
        assertNull(dao.findEnvHazardByName("invalid"));
    }

}
