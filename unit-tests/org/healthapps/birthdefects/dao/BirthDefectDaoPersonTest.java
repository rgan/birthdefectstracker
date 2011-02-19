package org.healthapps.birthdefects.dao;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.healthapps.birthdefects.model.*;
import org.healthapps.birthdefects.utils.TestUtils;
import org.junit.Test;

import java.util.Date;
import java.util.Set;
import java.util.Collection;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class BirthDefectDaoPersonTest extends AbstractTestDao {

    private BirthDefectsDao dao;
    private User createdByUser;

    public void setUp() {
        super.setUp();
        dao = new BirthDefectsDaoImpl(PMF.get(), null);
        createdByUser = new UserBuilder().withId(1L).build();
    }

    @Test
    public void testShouldStoreNewPerson() {

        final String name = "BabyX";
        final Set<Long> defectIds = Sets.newHashSet(1L);
        final Date dateOfBirth = new Date();
        final double lat = 20.1;
        final double lon = 10.4;
        Person person = new Person(name, dateOfBirth, new GeoLocation(lat, lon), defectIds, createdByUser);
        dao.store(person);

        final PreparedQuery preparedQuery = getPreparedQuery(Person.class.getSimpleName());
        assertEquals(1, preparedQuery.countEntities());
        final Entity entity = preparedQuery.asSingleEntity();
        assertEquals(name, entity.getProperty("name"));
        assertEquals(dateOfBirth, entity.getProperty("dateOfBirth"));
        assertEquals(lat, entity.getProperty("lat"));
        assertEquals(lon, entity.getProperty("lon"));
        assertEquals(defectIds.toString(), entity.getProperty("birthDefectIds").toString());
    }

    @Test
    public void testShouldStoreTensSpatialSummaryWhenSavingPerson() {
        final String name = "BabyX";
        final Set<Long> defectIds = Sets.newHashSet(1L);
        final Date dateOfBirth = new Date();
        final double lat = 20.1;
        final double lon = 10.4;
        Person person = new Person(name, dateOfBirth, new GeoLocation(lat, lon), defectIds, createdByUser);
        dao.store(person);

        final PreparedQuery preparedQuery = getPreparedQuery(SpatialTensSummary.class.getSimpleName());
        assertEquals(1, preparedQuery.countEntities());
        final Entity entity = preparedQuery.asSingleEntity();
        assertEquals(1L, entity.getProperty("count"));
        assertEquals(person.getCSCode().getTens(), ((Long) entity.getProperty("tensCode")).intValue());
    }

    @Test
    public void testShouldStoreSpatialSummaryWhenSavingPerson() {
        final String name = "BabyX";
        final Set<Long> defectIds = Sets.newHashSet(1L);
        final Date dateOfBirth = new Date();
        final double lat = 20.1;
        final double lon = 10.4;
        Person person = new Person(name, dateOfBirth, new GeoLocation(lat, lon), defectIds, createdByUser);
        dao.store(person);

        final PreparedQuery preparedQuery = getPreparedQuery(SpatialSummary.class.getSimpleName());
        assertEquals(1, preparedQuery.countEntities());
        final Entity entity = preparedQuery.asSingleEntity();
        assertEquals(1L, entity.getProperty("count"));
        assertEquals(person.getCSCode().getTens(), ((Long) entity.getProperty("tensCode")).intValue());
        assertEquals(person.getCSCode().getUnits(), ((Long) entity.getProperty("unitsCode")).intValue());
         assertEquals(person.getCSCode().getTenths(), ((Long) entity.getProperty("tenthsCode")).intValue());
    }

    public void testShouldDeletePerson() {
        final String name = "BabyX";
        final Set<Long> defectIds = Sets.newHashSet(1L);
        final Date dateOfBirth = new Date();
        final double lat = 20.1;
        final double lon = 10.4;
        Person person = new Person(name, dateOfBirth, new GeoLocation(lat, lon), defectIds, createdByUser);
        dao.store(person);
        dao.delete(person);
        final PreparedQuery preparedQuery = getPreparedQuery(Person.class.getSimpleName());
        assertEquals(0, preparedQuery.countEntities());
    }

    public void testShouldDecrementSpatialSummariesWhenPersonIsDeleted() {
        Person person = new PersonBuilder().withCreatedByUser(createdByUser).build();
        dao.store(person);

        PreparedQuery preparedQuery = getPreparedQuery(SpatialSummary.class.getSimpleName());
        assertEquals(1, preparedQuery.countEntities());
        Entity entity = preparedQuery.asSingleEntity();
        assertEquals(1L, entity.getProperty("count"));
        
        dao.delete(person);
        
        preparedQuery = getPreparedQuery(SpatialSummary.class.getSimpleName());
        assertEquals(1, preparedQuery.countEntities());
        entity = preparedQuery.asSingleEntity();
        assertEquals(0L, entity.getProperty("count"));
        
        preparedQuery = getPreparedQuery(SpatialTensSummary.class.getSimpleName());
        assertEquals(1, preparedQuery.countEntities());
        entity = preparedQuery.asSingleEntity();
        assertEquals(0L, entity.getProperty("count"));
        assertEquals(person.getCSCode().getTens(), ((Long) entity.getProperty("tensCode")).intValue());
    }

    @Test
    public void testFindPersonByNameReturnsNullIfNoneExists() {
        final Person person = dao.findPersonByName("nonexistent");
        assertNull(person);
    }

    @Test
    public void testShouldFindPersonByName() {
        final String name = "BabyX";
        Person person = new Person(name, new Date(), new GeoLocation(20.1, 10.4), TestUtils.defectsWithOneItem(1), createdByUser);
        dao.store(person);

        final Person personFound = dao.findPersonByName(name);
        assertNotNull(personFound);
        assertEquals(name, personFound.getName());
    }

    @Test
    public void testFindPersonByIdReturnsNullIfNoneExists() {
        final Person person = dao.findPersonById(-1L, null);
        assertNull(person);
    }

    @Test
    public void testShouldFindPersonByIdIfPersonWasCreatedByUser() {
        final String name = "BabyX";
        Person person = new Person(name, new Date(), new GeoLocation(20.1, 10.4), TestUtils.defectsWithOneItem(1), createdByUser);
        dao.store(person);

        final Person personFound = dao.findPersonById(person.getId(), createdByUser);
        assertEquals(name, personFound.getName());
    }

    @Test
    public void testShouldReturnNullIfPersonWasNotCreatedByUser() {
        final String name = "BabyX";
        Person person = new Person(name, new Date(), new GeoLocation(20.1, 10.4), TestUtils.defectsWithOneItem(1), createdByUser);
        dao.store(person);

        User anotherUser = new UserBuilder().withId(2L).build();
        final Person personFound = dao.findPersonById(person.getId(), anotherUser);
        assertNull(personFound);
    }

    @Test
    public void testShouldReturnTensSummaries() {
        Person person = new Person("BabyX", new Date(), new GeoLocation(20.1, 10.4), TestUtils.defectsWithOneItem(1), createdByUser);
        dao.store(person);
        final Collection<SpatialTensSummary> summaries = dao.mapSummaries(SpatialTensSummary.class.getName());
        assertEquals(1, summaries.size());
    }

    @Test
    public void testShouldReturnDefectsSummaryByCode() {
        Person person = new PersonBuilder().build();
        dao.store(person);
        Collection<Person> persons = dao.getDefectsSummaryByCode(person.getCSCode(), 10);
        assertEquals(1, persons.size());
        persons = dao.getDefectsSummaryByCode(new CSquareCode(200), 10);
        assertEquals(0, persons.size());
    }
}
