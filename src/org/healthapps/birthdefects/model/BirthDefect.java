package org.healthapps.birthdefects.model;

import org.healthapps.birthdefects.dao.BirthDefectsDao;

import javax.jdo.annotations.*;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BirthDefect {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Long id;
    @Persistent
    protected String name;
    @Persistent
    private String code;
    public static final int MAX_CODE_LENGTH = 20;

    public BirthDefect(String name, String code) {
        this(null, name, code);
    }

    public BirthDefect(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public ErrorMessages validate(BirthDefectsDao dao) {
        ErrorMessages errors = TextValidator.validate(name, Person.MAX_NAME_LENGTH, 1, "name");
        if (dao.findBirthDefectByName(name) != null) {
            errors.add(new BDError("nameExists"));
        }
        errors.add(TextValidator.validate(code, MAX_CODE_LENGTH, 1, "code"));
        return errors;
    }

    public String toString() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BirthDefect defect = (BirthDefect) o;

        if (code != null ? !code.equals(defect.code) : defect.code != null) return false;
        if (id != null ? !id.equals(defect.id) : defect.id != null) return false;
        if (name != null ? !name.equals(defect.name) : defect.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }
}


