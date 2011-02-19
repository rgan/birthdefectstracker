package org.healthapps.birthdefects.web;

import junit.framework.TestCase;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.healthapps.birthdefects.model.User;
import org.healthapps.birthdefects.model.Role;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

public class BaseTestCase extends TestCase {
    protected static final String ENCRYPTION_KEY_FOR_TEST = "testkey1";

    protected void login(User user) {
        loginWithRole(Role.USER, user);
    }

    private void loginWithRole(Role role, User user) {
        Authentication auth = org.mockito.Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(auth.getAuthorities()).thenReturn(new GrantedAuthority[]{new GrantedAuthorityImpl(role.getName())});
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    protected void loginWithAdminRole(User user) {
       loginWithRole(Role.ADMIN, user);
    }

    protected void logout() {
        SecurityContextHolder.clearContext();
    }

    protected void setupWebAppContextWithEncryptionKey(HttpServletRequest request) {
        WebApplicationContext webApplicationContext = org.mockito.Mockito.mock(WebApplicationContext.class);
        ServletContext servletContext = org.mockito.Mockito.mock(ServletContext.class);
        when(servletContext.getInitParameter(org.mockito.Matchers.eq(BaseController.ENCRYPTION_KEY))).thenReturn(ENCRYPTION_KEY_FOR_TEST);
        when(webApplicationContext.getServletContext()).thenReturn(servletContext);
        when(request.getAttribute(org.mockito.Matchers.eq(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE))).thenReturn(webApplicationContext);
    }
}
