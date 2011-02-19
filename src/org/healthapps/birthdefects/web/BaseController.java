package org.healthapps.birthdefects.web;

import org.healthapps.birthdefects.model.Role;
import org.healthapps.birthdefects.model.User;
import org.healthapps.birthdefects.utils.Encryptor;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.bouncycastle.crypto.InvalidCipherTextException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class BaseController {
    protected static final int BAD_REQUEST = 400;
    protected static final int NOT_FOUND = 404;
    protected static final int OK = 200;
    protected static final String TEXT_PLAIN_CONTENT = "text/plain";
    public static final String ENCRYPTION_KEY = "encryptionKey";

    protected User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object obj = auth.getPrincipal();
        if (obj instanceof UserDetails) {
            return (User) obj;
        }
        return null;
    }

    protected boolean isAdmin() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }
        if (hasAdminRole(authentication.getAuthorities())) {
            return true;
        }
        return false;
    }

    private boolean hasAdminRole(GrantedAuthority[] authorities) {
        for (GrantedAuthority authority : authorities) {
            if (Role.ADMIN.getName().equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isLoggedIn() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }
        return true;
    }

    protected void writeToResponse(HttpServletResponse response, int status, String text) throws IOException {
        response.setContentType(TEXT_PLAIN_CONTENT);
        response.setStatus(status);
        response.getWriter().println(text);
    }

    protected void setupNotLoggedInResponse(HttpServletResponse response) throws IOException {
        writeToResponse(response, BAD_REQUEST, "You must be logged in.");
    }

    protected void setupEncrytionKey(HttpServletRequest request) {
        final WebApplicationContext webAppContext = (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        final String encryptionKey = webAppContext.getServletContext().getInitParameter(ENCRYPTION_KEY);
        Encryptor.setKey(encryptionKey);
    }

    protected String encrypt(String password) {
        final Encryptor encryptor = new Encryptor();
        try {
            return encryptor.encrypt(password);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }

    protected SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
}
