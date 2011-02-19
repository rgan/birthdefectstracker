package org.healthapps.birthdefects.model;

import org.healthapps.birthdefects.dao.UserDao;
import org.springframework.validation.BindingResult;

public class UserValidator {

    private UserDao userDao;
    private static final int MAX_NAME_LENGTH = 30;
    private static final int PWD_MIN_LENGTH = 6;
    private static final int MAX_PWD_LENGTH = 100;

    public UserValidator(UserDao userDao) {
        this.userDao = userDao;
    }

    public void validate(User user, BindingResult errors) {
        TextValidator.validate(user.getUsername(), MAX_NAME_LENGTH, 1, "username", errors);
        TextValidator.validate(user.getPassword(), MAX_PWD_LENGTH, PWD_MIN_LENGTH, "password", errors);
        if (user.getRoles() == null || user.getRoles().length == 0) {
            errors.rejectValue("roles", "atLeastOneRole", "atLeastOneRole");
        }
        if (user.isNew() && userDao.findByName(user.getUsername()) != null) {
            errors.rejectValue("username", "nameExists", "nameExists");
        }
    }

    public ErrorMessages validate(User user, boolean isAdmin) {
        final ErrorMessages errorMessages = TextValidator.validate(user.getUsername(), MAX_NAME_LENGTH,
                1, "username");
        errorMessages.add(TextValidator.validateEmail(user.getEmail(), MAX_NAME_LENGTH,
                "email"));
        errorMessages.add(TextValidator.validate(user.getPassword(), MAX_NAME_LENGTH, PWD_MIN_LENGTH, "password"));
        if (user.getRoles() == null || user.getRoles().length == 0) {
            errorMessages.add(new BDError("atLeastOneRole"));
        }
        if (user.isNew() && userDao.findByName(user.getUsername()) != null) {
            errorMessages.add(new BDError("nameExists"));
        }
        if (user.isInAdminRole() && !isAdmin) {
            errorMessages.add(new BDError("cannotCreateAdminRole"));
        }
        return errorMessages;
    }
}
