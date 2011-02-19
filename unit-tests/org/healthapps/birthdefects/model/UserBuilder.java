package org.healthapps.birthdefects.model;

import com.google.common.collect.ImmutableClassToInstanceMap;
import org.healthapps.birthdefects.utils.Encryptor;

public class UserBuilder {
    private String username = "test";
    private String password = "testtest";
    private String roles = "user,admin";
    private Long id = null;
    private boolean medicalProfessional = false;
    private boolean enabled = true;
    private String email = "foo@bar";

    public UserBuilder() {
    }

    public User build() {
        return new User(id, username, password, roles, medicalProfessional, enabled, email);
    }

    public UserBuilder withUserName(String name) {
        username = name;
        return this;
    }

    public UserBuilder withPassword(String pwd) {
        password = pwd;
        return this;
    }

    public UserBuilder withRoles(String s) {
        roles= s;
        return this;
    }

    public UserBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder withMedicalProfessional(boolean b) {
        this.medicalProfessional = b;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
}
