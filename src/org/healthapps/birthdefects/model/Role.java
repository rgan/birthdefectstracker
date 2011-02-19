package org.healthapps.birthdefects.model;

public enum Role {
    USER("user"), ADMIN("admin");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Role from(String s) {
        if (USER.getName().equals(s)) {
            return USER;
        }
        if (ADMIN.getName().equals(s)) {
            return ADMIN;
        }
        throw new RuntimeException("Invalid value for Role enum:" + s);
    }
}
