package org.healthapps.birthdefects.web;

import junit.framework.TestCase;
import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.model.*;
import org.healthapps.birthdefects.utils.TestUtils;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonUIAdaptorTest extends TestCase {
    private User createdByUser = new UserBuilder().withId(1L).build();
    private BirthDefectsDao birthDefectsDao;

    public void testShouldReturnBirthDefectNames() throws ParseException {
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        final long defectId = 1L;
        final String defectName = "spina bifida";
        final Person person = new Person(defectId, "aperson", dob, new GeoLocation(10.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        setupDaoForTest(defectId, defectName);
        PersonUIAdaptor personAdaptor = new PersonUIAdaptor(person, birthDefectsDao, createdByUser);
        assertEquals(1, personAdaptor.getBirthDefects().size());
        assertEquals(defectName, personAdaptor.getBirthDefects().iterator().next());
    }

    private void setupDaoForTest(long defectId, String defectName) {
        birthDefectsDao = mock(BirthDefectsDao.class);
        final BirthDefect birthDefect = new BirthDefect(defectId, defectName, "401");
        when(birthDefectsDao.getBirthDefectById(eq(defectId))).thenReturn(birthDefect);
        when(birthDefectsDao.findUserById(eq(createdByUser.getId()))).thenReturn(createdByUser);
    }

    public void testShouldThrowExceptionIfDefectIsNotFound() throws ParseException {
        try {
            final Person person = new Person(1L, "aperson", new Date(), new GeoLocation(10.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
            birthDefectsDao = mock(BirthDefectsDao.class);
            PersonUIAdaptor personAdaptor = new PersonUIAdaptor(person, birthDefectsDao, createdByUser);
            fail("Should have thrown exception");
        } catch (Exception ex) {

        }
    }

    public void testShouldReturnDateOfBirthFormatted() throws ParseException {
        final long defectId = 1L;
        final String defectName = "spina bifida";
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        final Person person = new Person(defectId, "aperson", dob, new GeoLocation(10.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        setupDaoForTest(defectId, defectName);
        PersonUIAdaptor personAdaptor = new PersonUIAdaptor(person, birthDefectsDao, createdByUser);
        assertEquals("2001-01-01", personAdaptor.getDateOfBirth());
    }

    public void testShouldReturnEmptyNameIfUserIsDifferent() throws ParseException {
        final long defectId = 1L;
        final String defectName = "spina bifida";
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        final Person person = new Person(defectId, "aperson", dob, new GeoLocation(10.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        setupDaoForTest(defectId, defectName);
        PersonUIAdaptor personAdaptor = new PersonUIAdaptor(person, birthDefectsDao, new UserBuilder().withId(2L).build());
        assertEquals("", personAdaptor.getName());
    }

    public void testShouldReturnBboxAndNoNameIfUserIsDifferent() throws ParseException {
        final long defectId = 1L;
        final String defectName = "spina bifida";
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        final Person person = new Person(defectId, "aperson", dob, new GeoLocation(10.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        setupDaoForTest(defectId, defectName);
        PersonUIAdaptor personAdaptor = new PersonUIAdaptor(person, birthDefectsDao, new UserBuilder().withId(2L).build());
        assertNull(personAdaptor.getLat());
        assertNull(personAdaptor.getLon());
        assertNotNull(personAdaptor.getBbox());
    }

    public void testShouldReturnBboxAndNoNameIfNullUser() throws ParseException {
        final long defectId = 1L;
        final String defectName = "spina bifida";
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        final Person person = new Person(defectId, "aperson", dob, new GeoLocation(10.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        setupDaoForTest(defectId, defectName);
        PersonUIAdaptor personAdaptor = new PersonUIAdaptor(person, birthDefectsDao, null);
        assertNull(personAdaptor.getLat());
        assertNull(personAdaptor.getLon());
        assertNotNull(personAdaptor.getBbox());
    }

    public void testShouldCreateCSV() throws ParseException {
        final String defectName = "spina bifida";
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01");
        final Person person = new PersonBuilder().withDOB(dob).withCreatedByUser(createdByUser).build();
        setupDaoForTest(person.getBirthDefectIds().iterator().next(), defectName);
        PersonUIAdaptor personAdaptor = new PersonUIAdaptor(person, birthDefectsDao, createdByUser);
        assertEquals("aperson,2001-01-01,spina bifida;,10.1,11.1,\"11.0,12.0;11.0,11.0;10.0,11.0;10.0,12.0;11.0,12.0;\"", personAdaptor.toCsv());
    }
}
