package org.healthapps.birthdefects.web;

import org.healthapps.birthdefects.model.UserBuilder;
import org.healthapps.birthdefects.utils.Encryptor;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LoginControllerTest extends BaseTestCase {
    private LoginController loginController;
    private AuthenticationManager authenticationManager;
    private HttpServletResponse response;
    private HttpServletRequest request;

    public void setUp() throws IOException {
        authenticationManager = mock(AuthenticationManager.class);
        request = mock(HttpServletRequest.class);
        setupWebAppContextWithEncryptionKey(request);
        loginController = new LoginController(authenticationManager);
        response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
    }

    public void tearDown() {
        logout();
    }

    public void testShouldSetAuthenticationContextUponSuccessfulLogin() throws Exception {
        String username = "user";
        String password = "foobar";

        final Encryptor encryptor = new Encryptor(ENCRYPTION_KEY_FOR_TEST.getBytes());
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, encryptor.encrypt(password));
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(eq(authRequest))).thenReturn(auth);
        loginController.login(username, password, response, request);
        assertEquals(auth, SecurityContextHolder.getContext().getAuthentication());
    }

    public void testShouldNotSetAuthenticationContextOnFailedLogin() throws Exception {
        String username = "user";
        String password = "foobar";

        final Encryptor encryptor = new Encryptor(ENCRYPTION_KEY_FOR_TEST.getBytes());
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, encryptor.encrypt(password));
        when(authenticationManager.authenticate(eq(authRequest))).thenThrow(new BadCredentialsException(""));
        loginController.login(username, password, response, request);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(response).setStatus(BaseController.BAD_REQUEST);
    }

    public void testShouldClearContextOnLogout() {
        login(new UserBuilder().build());
        loginController.logout(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    public void testShouldReturnErrorIfNotLoggedIn() throws IOException {
        loginController.checkLogin(response);
        verify(response).setStatus(BaseController.NOT_FOUND);
    }

    public void testShouldReturnOKIfLoggedIn() throws IOException {
        login(new UserBuilder().build());
        loginController.checkLogin(response);
        verify(response).setStatus(BaseController.OK);
    }

}
