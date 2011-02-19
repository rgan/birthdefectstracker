package org.healthapps.birthdefects.model;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextValidator {
    private static final String REGEX_VALID_NAME = "\\A[\\s.a-zA-Z0-9_-]+\\Z";
    private static final String EMAIL_REGEX = "[.a-zA-Z0-9_-]+[@]{1}[.a-zA-Z0-9_-]+";

    private TextValidator() {
    }

    public static ErrorMessages validateEmail(String text, int maxLength, String fieldName) {
        ErrorMessages errors = new ErrorMessages();
        errors.addAll(validate(text, maxLength, 1, fieldName, EMAIL_REGEX, "", "invalidEmail"));
        return errors;
    }

    public static ErrorMessages validate(String text, int maxLength, int minLength, String fieldName) {
        return validate(text, maxLength, minLength, fieldName, REGEX_VALID_NAME);
    }

    public static ErrorMessages validate(String text, int maxLength, int minLength, String fieldName, String regex) {
        ErrorMessages errors = new ErrorMessages();
        errors.addAll(validate(text, maxLength, minLength, fieldName, regex, "", "invalidCharacters"));
        return errors;
    }

    public static List<BDError> validate(String text, int maxLength, int minLength, String fieldName,
                                                String regex, String objectName, String invalidRegexCode) {
        List<BDError> errors = new ArrayList();

        if (!StringUtils.hasLength(text)) {
            errors.add(new BDError(objectName, "required", new Object[]{fieldName}));
        } else {
            if (text.length() < minLength) {
                errors.add(new BDError(objectName, "minLength", new Object[]{fieldName, minLength}));
            }
            Pattern pattern = Pattern.compile(regex);
            if (!pattern.matcher(text).find()) {
                errors.add(new BDError(objectName, invalidRegexCode, new Object[]{fieldName}));
            }
        }
        if (StringUtils.hasLength(text) && text.length() > maxLength) {
            errors.add(new BDError(objectName, "lengthExceeded", new Object[]{fieldName, maxLength}));
        }
        return errors;
    }

    public static void validate(String name, int maxNameLength, int minLength, String fieldName, BindingResult errors) {
        final List<BDError> bdErrors = validate(name, maxNameLength, minLength, fieldName, REGEX_VALID_NAME, errors.getObjectName(), "invalidCharacters");
        for (BDError error : bdErrors) {
            errors.addError(error);
        }
    }
}
