package org.healthapps.birthdefects.web;

import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

import org.healthapps.birthdefects.model.BirthDefect;
import org.healthapps.birthdefects.dao.BirthDefectsDao;

public class PersonBirthDefectsPropertyEditor extends PropertyEditorSupport {
    private BirthDefectsDao dao;

    public PersonBirthDefectsPropertyEditor(BirthDefectsDao dao) {
        this.dao = dao;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        setValue(dao.idsFromNames(text));
    }
}