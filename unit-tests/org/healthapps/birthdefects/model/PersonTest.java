package org.healthapps.birthdefects.model;

import org.healthapps.birthdefects.dao.AbstractTestDao;
import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.dao.BirthDefectsDaoImpl;
import org.healthapps.birthdefects.dao.PMF;
import org.healthapps.birthdefects.utils.TestUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class PersonTest extends AbstractTestDao {

    private BirthDefectsDao dao;
    private User createdByUser;

    public void setUp() {
        super.setUp();
        createdByUser = new UserBuilder().build();
        dao = new BirthDefectsDaoImpl(PMF.get(), null);
    }

    @Test
    public void testShouldNotHaveErrorsIfValid() {
        Person person = new Person("name", new Date(), new GeoLocation(20.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        final ErrorMessages errorMessages = person.validate(dao);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testShouldErrorIfNameIsEmpty() {
        Person person = new Person("", new Date(), new GeoLocation(20.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        final ErrorMessages errorMessages = person.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("required"));
    }

    @Test
    public void testShouldErrorIfNameLengthExceeded() {
        Person person = new Person(StringUtils.repeat("a", 90), new Date(), new GeoLocation(20.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        final ErrorMessages errorMessages = person.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("lengthExceeded"));
    }

    @Test
    public void testShouldErrorIfInvalidCharsInName() {
        Person person = new Person("<script", new Date(), new GeoLocation(20.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        final ErrorMessages errorMessages = person.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("invalidCharacters"));
    }

    @Test
    public void testShouldErrorIfDOBIsEmpty() {
        Person person = new Person("name", null, new GeoLocation(20.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        final ErrorMessages errorMessages = person.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("required"));
    }

    @Test
    public void testShouldErrorIfDefectsIsEmpty() {
        Person person = new Person("name", new Date(), new GeoLocation(20.1, 20.1), new HashSet<Long>(), createdByUser);
        final ErrorMessages errorMessages = person.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("atLeastOneBDRequired"));
    }

    @Test
    public void testShouldErrorIfNameAlreadyExistsForNewPerson() {
        Person person = new Person("name", new Date(), new GeoLocation(20.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        dao.store(person);
        person = new Person("name", new Date(), new GeoLocation(20.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        final ErrorMessages errorMessages = person.validate(dao);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("nameExists"));
    }

    @Test
    public void testShouldNotErrorIfNameAlreadyExistsWhenEditingExistingPerson() {
        Person person = new Person("name", new Date(), new GeoLocation(20.1, 20.1), TestUtils.defectsWithOneItem(1L), createdByUser);
        dao.store(person);
        final ErrorMessages errorMessages = person.validate(dao);
        assertTrue(errorMessages.isEmpty());
    }
}
