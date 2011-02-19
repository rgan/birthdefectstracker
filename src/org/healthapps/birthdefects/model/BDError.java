package org.healthapps.birthdefects.model;

import org.springframework.validation.ObjectError;
import org.springframework.context.MessageSource;

public class BDError extends ObjectError {

    public BDError(String objectName, String code, Object[] arguments) {
        super(objectName, new String[] { code }, arguments, null);
    }

    public BDError(String code, Object[] arguments) {
       this("", code, arguments);
    }

    public BDError(String code) {
        this(code, new Object[] {});
    }

    public String getMessage(MessageSource messageSource) {
        return messageSource.getMessage(getCode(), getArguments(), null);
    }
}
