package org.healthapps.birthdefects.model;

import org.healthapps.birthdefects.utils.TestUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import com.google.common.collect.ImmutableClassToInstanceMap;

public class PersonBuilder {
    private Long id = null;
    private String name = "aperson";
    private Date dateOfBirth = Calendar.getInstance().getTime();
    private GeoLocation location = new GeoLocation(10.1, 11.1);
    private Set<Long> defectIds = TestUtils.defectsWithOneItem(1);
    private User createdByUser = new UserBuilder().build();

    public Person build() {
        return new Person(id, name, dateOfBirth, location, defectIds, createdByUser);
    }

    public PersonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public PersonBuilder withCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
        return this;
    }

    public PersonBuilder withDOB(Date dob) {
        this.dateOfBirth = dob;
        return this;
    }

    public PersonBuilder withLocation(GeoLocation location) {
        this.location = location;
        return this;
    }
}
