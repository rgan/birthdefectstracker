package org.healthapps.birthdefects.model;

import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessages {
    private List<BDError> errors;

    public ErrorMessages() {
        this.errors = new ArrayList<BDError>();
    }

    public void add(BDError error) {
        errors.add(error);
    }

    public void add(ErrorMessages messages) {
        for (BDError s : messages.errors) {
            errors.add(s);
        }
    }

    public boolean isEmpty() {
        return errors.size() == 0;
    }

    public String getMessages(MessageSource messageSource) {
        StringBuffer result = new StringBuffer();
        for (BDError error : errors) {
            result.append(error.getMessage(messageSource)).append(". ");
        }
        return result.toString();
    }

    public int size() {
        return errors.size();
    }

    public boolean hasError(String errMsgCode) {
        for (BDError error : errors) {
            if (error.getCode().equals(errMsgCode)) {
                return true;
            }
        }
        return false;
    }

    public void addAll(List<BDError> bdErrors) {
        for (BDError error : bdErrors) {
            errors.add(error);
        }
    }
}
