package org.healthapps.birthdefects.web;

import org.healthapps.birthdefects.dao.UserDao;
import org.healthapps.birthdefects.model.User;
import org.healthapps.birthdefects.model.UserBuilder;
import org.healthapps.birthdefects.model.ErrorMessages;
import org.healthapps.birthdefects.model.BDError;
import org.healthapps.birthdefects.utils.Encryptor;
import static org.mockito.Mockito.*;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class UserControllerTest extends BaseTestCase {
    private UserController userController;
    private UserDao userDao;
    private BindingResult bindingResult;
    private SessionStatus sessionStatus;
    private MessageSource messageSource;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Model model;
    private Emailer emailer;

    public void setUp() throws IOException {
        userDao = mock(UserDao.class);
        model = mock(Model.class);
        request = mock(HttpServletRequest.class);
        setupWebAppContextWithEncryptionKey(request);
        response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        messageSource = new MessageSourceStub();
        emailer = mock(Emailer.class);
        userController = new UserController(userDao, messageSource, emailer);
        bindingResult = mock(BindingResult.class);
        sessionStatus = mock(SessionStatus.class);
        when(bindingResult.getObjectName()).thenReturn("user");
        Encryptor.setKey(ENCRYPTION_KEY_FOR_TEST);
    }

    public void tearDown() {
        logout();
    }

    public void testShouldNotAllowAdminRoleIfNotAdmin() throws IOException {
        User user = new UserBuilder().build();
        setupUserParamsInRequest(request, user);
        userController.save(request, response);
        verify(response).setStatus(BaseController.BAD_REQUEST);
        verify(userDao, never()).save(user);
    }

    private void setupUserParamsInRequest(HttpServletRequest request, User user) {
        when(request.getParameter("username")).thenReturn(user.getUsername());
        when(request.getParameter("password")).thenReturn(user.getPassword());
        when(request.getParameterValues("roles")).thenReturn(user.getRoles());
        when(request.getParameter("email")).thenReturn(user.getEmail());
    }

    public void testShouldSaveAdminRoleIfAdmin() throws Exception {
        User user = new UserBuilder().build();
        loginWithAdminRole(new UserBuilder().build());
        setupUserParamsInRequest(request, user);
        userController.save(request, response);
        final String encryptedPassword = new Encryptor().encrypt(user.getPassword());
        user.setPassword(encryptedPassword);
        verify(userDao, times(1)).save(user);
    }

    public void testShouldNotSaveIfValidationErrors() throws IOException {
        User user = new UserBuilder().withUserName("").build();
        userController.save(request, response);
        verify(userDao, never()).save(user);
    }

    public void testShouldNotAllowDeleteIfNotAdmin() {
        assertEquals("users", userController.delete(model, 1L));
        verify(userDao, never()).disableUser(eq(1L));
    }

    public void testShouldAllowDeleteIfAdmin() {
        loginWithAdminRole(new UserBuilder().build());
        assertEquals("users", userController.delete(model, 1L));
        verify(userDao, times(1)).disableUser(eq(1L));
    }

    public void testShouldSendEmail() throws IOException {
        final User recipient = new UserBuilder().build();
        when(userDao.findUserById(eq(1L))).thenReturn(recipient);
        when(emailer.validate(anyString(), anyString())).thenReturn(new ErrorMessages());
        userController.sendEmail(1L, "body", "subject", request, response);
        verify(emailer, times(1)).send(recipient.getEmail(),
                 "subject","body");
    }

    public void testShouldNotSendEmailIfValidationFails() throws IOException {
        final User recipient = new UserBuilder().build();
        when(userDao.findUserById(eq(1L))).thenReturn(recipient);
        final ErrorMessages errorMessages = new ErrorMessages();
        errorMessages.add(new BDError(""));
        when(emailer.validate(anyString(), anyString())).thenReturn(errorMessages);
        userController.sendEmail(1L, "<body", "subject", request, response);
        verify(emailer, never()).send(recipient.getEmail(),
                 "subject","body");
    }
}
