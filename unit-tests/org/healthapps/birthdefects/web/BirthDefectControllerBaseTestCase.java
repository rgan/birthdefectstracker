package org.healthapps.birthdefects.web;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.model.User;
import org.healthapps.birthdefects.model.UserBuilder;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.when;

public class BirthDefectControllerBaseTestCase extends BaseTestCase {
    protected BirthDefectController controller;
    protected HttpServletResponse response;
    protected StringWriter responseOutput;
    protected User createdByUser;
    protected MessageSource messageSource;
    protected BirthDefectsDao birthDefectsDao;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Before
    public void setUp() throws IOException {
        helper.setUp();
        birthDefectsDao = org.mockito.Mockito.mock(BirthDefectsDao.class);
        createdByUser = new UserBuilder().withId(-1L).build();
        when(birthDefectsDao.findUserById(createdByUser.getId())).thenReturn(createdByUser);
        messageSource = new MessageSourceStub();
        responseOutput = new StringWriter();
        response = org.mockito.Mockito.mock(HttpServletResponse.class);
        org.mockito.Mockito.when(response.getWriter()).thenReturn(new PrintWriter(responseOutput));
        login(createdByUser);
    }
}
