package org.healthapps.birthdefects.web;

import junit.framework.TestCase;
import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.model.BirthDefect;
import org.healthapps.birthdefects.model.Person;
import org.healthapps.birthdefects.model.User;
import org.healthapps.birthdefects.utils.TestUtils;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import org.springframework.security.context.SecurityContextHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControllerAuthTest extends TestCase {
    private BirthDefectController controller;
    private HttpServletResponse response;
    private BirthDefectsDao dao;

    public void setUp() throws IOException {
        dao = mock(BirthDefectsDao.class);
        PrintWriter outputStream = new PrintWriter(new StringWriter());
        response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(outputStream);
        controller = new BirthDefectController(dao, null);
        SecurityContextHolder.getContext().setAuthentication(null);

    }

    public void testShouldNotAllowSaveUserIfNotLoggedIn() throws IOException {
        controller.savePerson(null, "valid", new Date(), 20.1, 10.1, TestUtils.defectsWithOneItem(1), response);
        verify(response).setStatus(BaseController.BAD_REQUEST);
        verify(dao, never()).store((Person) anyObject());
    }

    public void testShouldNotAllowSearchUserIfNotLoggedIn() throws IOException, ParseException {
        Date fromDate = new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01");
        Date toDate = new SimpleDateFormat("yyyy-MM-dd").parse("2009-01-01");
        controller.search(fromDate, toDate, TestUtils.defectsWithOneItem(1L), "", response);
        verify(response).setStatus(BaseController.BAD_REQUEST);
        verify(dao, never()).search((Date) anyObject(), (Date) anyObject(), (Long) anyObject(), anyInt());
    }

    public void testShouldNotAllowDeletePersonIfNotLoggedIn() throws IOException {
        controller.deletePerson(1L, response);
        verify(response).setStatus(BaseController.BAD_REQUEST);
        verify(dao, never()).delete((Person) anyObject());
    }

    public void testShouldNotAllowGetPersonIfNotLoggedIn() throws IOException {
        controller.getPerson(1L, response);
        verify(response).setStatus(BaseController.BAD_REQUEST);
        verify(dao, never()).findPersonById(anyLong(), (User) anyObject());
    }

    public void testShouldNotAllowAddBirthDefectIfNotLoggedIn() throws IOException {
        controller.addBirthDefect("defect1", "code", response);
        verify(response).setStatus(BaseController.BAD_REQUEST);
        verify(dao, never()).store((BirthDefect) anyObject());
    }
}
