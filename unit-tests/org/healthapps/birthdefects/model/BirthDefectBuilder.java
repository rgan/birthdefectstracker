package org.healthapps.birthdefects.model;

import com.google.common.collect.ImmutableClassToInstanceMap;

public class BirthDefectBuilder {
    private Long id;
    private String name = "spina bifida";
    private String code = "code";

    public BirthDefect build() {
        return new BirthDefect(id, name, code);
    }

    public BirthDefectBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public BirthDefectBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public BirthDefectBuilder withId(Long id) {
        this.id = id;
        return this;
    }
}
