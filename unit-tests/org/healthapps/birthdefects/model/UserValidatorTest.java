package org.healthapps.birthdefects.model;

import junit.framework.TestCase;
import org.healthapps.birthdefects.dao.UserDao;
import org.healthapps.birthdefects.dao.UserDaoImpl;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class UserValidatorTest extends TestCase {
    private UserDao userDao;
    private BindingResult bindingResult;

    public void setUp() {
        userDao = mock(UserDaoImpl.class);
    }

    public void testShouldNotHaveErrorsIfValid() {
        User user = new UserBuilder().build();
        bindingResult = new BeanPropertyBindingResult(user, "user");
        new UserValidator(userDao).validate(user, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    public void testShouldErrorIfNameIsEmpty() {
        User user = new UserBuilder().withUserName("").build();
        bindingResult = new BeanPropertyBindingResult(user, "user");
        new UserValidator(userDao).validate(user, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals("required", ((ObjectError) bindingResult.getAllErrors().get(0)).getCode());
    }

    public void testShouldErrorIfPasswordIsEmpty() {
        User user = new UserBuilder().withPassword("").build();
        bindingResult = new BeanPropertyBindingResult(user, "user");
        new UserValidator(userDao).validate(user, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals("required", ((ObjectError) bindingResult.getAllErrors().get(0)).getCode());
    }

    public void testShouldErrorIfPasswordIsLessThanMinimum() {
        User user = new UserBuilder().withPassword("foo").build();
        bindingResult = new BeanPropertyBindingResult(user, "user");
        new UserValidator(userDao).validate(user, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals("minLength", ((ObjectError) bindingResult.getAllErrors().get(0)).getCode());
    }

    public void testShouldErrorIfRolesIsEmpty() {
        User user = new UserBuilder().withRoles("").build();
        bindingResult = new BeanPropertyBindingResult(user, "user");
        new UserValidator(userDao).validate(user, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals("atLeastOneRole", ((ObjectError) bindingResult.getAllErrors().get(0)).getCode());
    }

    public void testShouldErrorIfUsernameExists() {
        User user = new UserBuilder().withId(null).withUserName("foo").build();
        when(userDao.findByName(eq("foo"))).thenReturn(new UserBuilder().build());
        bindingResult = new BeanPropertyBindingResult(user, "user");
        new UserValidator(userDao).validate(user, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals("nameExists", ((ObjectError) bindingResult.getAllErrors().get(0)).getCode());
    }

    public void testShouldValidateEmail() {
        User user = new UserBuilder().withRoles("user").withEmail("foo").build();
        ErrorMessages errorMessages = new UserValidator(userDao).validate(user, false);
        assertEquals(1, errorMessages.size());
        assertTrue(errorMessages.hasError("invalidEmail"));
        user = new UserBuilder().withRoles("user").withEmail("foo@bar").build();
        errorMessages = new UserValidator(userDao).validate(user, false);
        assertEquals(0, errorMessages.size());
    }
}
