package org.healthapps.birthdefects.model;

import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.healthapps.birthdefects.dao.AbstractTestDao;
import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.dao.BirthDefectsDaoImpl;
import org.healthapps.birthdefects.dao.PMF;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class BirthDefectTest extends AbstractTestDao {

    private BirthDefectsDao dao;

    public void setUp() {
        super.setUp();
        dao = new BirthDefectsDaoImpl(PMF.get(), null);
    }

    @Test
    public void testShouldReturnJson() {
        Collection<BirthDefect> birthDefects = new ArrayList<BirthDefect>();
        birthDefects.add(new BirthDefect("foo", "bar"));
        assertEquals("[{\"code\":\"bar\",\"id\":0,\"name\":\"foo\"}]", JSONArray.fromObject(birthDefects).toString());
    }

    @Test
    public void testShouldAllowInputMatchingRegex() {
        Pattern pattern = Pattern.compile("\\A[\\[\\]\\}\\{\\s.a-zA-Z0-9_-]+\\Z");
        assertTrue(pattern.matcher("foo 1").find());
        assertTrue(pattern.matcher("[{-2.1}]").find());
        assertFalse(pattern.matcher("foo<>").find());
    }

    @Test
    public void testShouldNotHaveAnyErrorsIfValid() {
        BirthDefect birthDefect = new BirthDefect("name", "401");
        final ErrorMessages errorMessages = birthDefect.validate(dao);
        assertTrue(errorMessages.isEmpty());
    }

    @Test
    public void testShouldErrorIfNameIsEmpty() {
        BirthDefect birthDefect = new BirthDefect("", "401");
        final ErrorMessages errorMessages = birthDefect.validate(dao);
        assertTrue(errorMessages.hasError("required"));
    }

    @Test
    public void testShouldNotAllowInvalidCharactersInName() {
        BirthDefect birthDefect = new BirthDefect("<script>", "401");
        final ErrorMessages errorMessages = birthDefect.validate(dao);
        assertTrue(errorMessages.hasError("invalidCharacters"));
    }

    @Test
    public void testShouldErrorIfNameLengthExceeded() {
        BirthDefect birthDefect = new BirthDefect(StringUtils.repeat("a", 90), "401");
        final ErrorMessages errorMessages = birthDefect.validate(dao);
        assertTrue(errorMessages.hasError("lengthExceeded"));
    }

    @Test
    public void testShouldErrorIfNameExists() {
        final String name = "existingName";
        BirthDefect birthDefect = new BirthDefect(name, "401");
        dao.store(birthDefect);

        birthDefect = new BirthDefect(name, "401");
        final ErrorMessages errorMessages = birthDefect.validate(dao);
        assertTrue(errorMessages.hasError("nameExists"));
    }

    @Test
    public void testShouldErrorIfCodeIsEmpty() {
        BirthDefect birthDefect = new BirthDefect("name", "");
        final ErrorMessages errorMessages = birthDefect.validate(dao);
        assertTrue(errorMessages.hasError("required"));
    }

    @Test
    public void testShouldErrorIfCodeLengthExceeded() {
        BirthDefect birthDefect = new BirthDefect("name", StringUtils.repeat("a", 30));
        final ErrorMessages errorMessages = birthDefect.validate(dao);
        assertTrue(errorMessages.hasError("lengthExceeded"));
    }
}
