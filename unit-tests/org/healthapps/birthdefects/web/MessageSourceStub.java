package org.healthapps.birthdefects.web;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.MessageSourceResolvable;

import java.util.Locale;

public class MessageSourceStub implements MessageSource {
    public String getMessage(String code, Object[] args, String s1, Locale locale) {
        return code;
    }

    public String getMessage(String code, Object[] objects, Locale locale) throws NoSuchMessageException {
        return code;
    }

    public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale) throws NoSuchMessageException {
        return null;
    }
}
