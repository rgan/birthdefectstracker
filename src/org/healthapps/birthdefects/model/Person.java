package org.healthapps.birthdefects.model;

import org.healthapps.birthdefects.dao.BirthDefectsDao;

import javax.jdo.annotations.*;
import java.util.Date;
import java.util.Set;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Person implements Locatable {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    @Persistent
    private String name;
    @Persistent
    private Date dateOfBirth;
    @Persistent
    private Double lat;
    @Persistent
    private Double lon;
    @Persistent
    private Long userId;
    @Persistent
    private Integer csTens;
    @Persistent
    private Integer csUnits;
    @Persistent
    private Integer csTenths;

    // GAE does not support proper many-to-many
    @Persistent(defaultFetchGroup = "true")
    private Set<Long> birthDefectIds;

    public static final int MAX_NAME_LENGTH = 80;
    public static final String LAT_FIELD = "lat";

    public Person(String name, Date dateOfBirth, GeoLocation location, Set<Long> defectIds, User createdBy) {
        this(null, name, dateOfBirth, location, defectIds, createdBy);
    }

    public Person(Long id, String name, Date dateOfBirth, GeoLocation location, Set<Long> defectIds, User createdByUser) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        setLatLon(location);
        this.birthDefectIds = defectIds;
        this.userId = createdByUser.getId();
    }

    private void setLatLon(GeoLocation location) {
        this.lat = location.getLat();
        this.lon = location.getLon();
        CSquareCode code = CSquareCode.from(lat, lon);
        this.csTens = code.getTens();
        this.csUnits = code.getUnits();
        this.csTenths = code.getTenths();
    }

    public String getName() {
        return name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public ErrorMessages validate(BirthDefectsDao dao) {
        ErrorMessages errors = TextValidator.validate(name, MAX_NAME_LENGTH, 1, "name");
        if (id == null && dao.findPersonByName(name) != null) {
            errors.add(new BDError("nameExists"));
        }
        if (dateOfBirth == null) {
            errors.add(new BDError("required", new Object[]{"dateOfBirth"}));
        }
        if (birthDefectIds.isEmpty()) {
            errors.add(new BDError("atLeastOneBDRequired"));
        }
        return errors;
    }

    public Set<Long> getBirthDefectIds() {
        return birthDefectIds;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public boolean hasLatLon() {
        return lat != null && lon != null;
    }

    public Long getId() {
        return id;
    }

    public void updateFrom(Person person) {
        this.name = person.name;
        this.dateOfBirth = person.dateOfBirth;
        setLatLon(person.geoLocation());
        this.birthDefectIds = person.birthDefectIds;
    }

    public GeoLocation geoLocation() {
        return new GeoLocation(lat, lon);
    }

    public CSquareCode getCSCode() {
        return new CSquareCode(csTens, csUnits, csTenths);
    }

    public Long getCreatedById() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (birthDefectIds != null ? !birthDefectIds.equals(person.birthDefectIds) : person.birthDefectIds != null)
            return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(person.dateOfBirth) : person.dateOfBirth != null) return false;
        if (id != null ? !id.equals(person.id) : person.id != null) return false;
        if (lat != null ? !lat.equals(person.lat) : person.lat != null) return false;
        if (lon != null ? !lon.equals(person.lon) : person.lon != null) return false;
        if (name != null ? !name.equals(person.name) : person.name != null) return false;
        if (userId != null ? !userId.equals(person.userId) : person.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        result = 31 * result + (lon != null ? lon.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (birthDefectIds != null ? birthDefectIds.hashCode() : 0);
        return result;
    }

    public boolean isCreatedBy(User loggedInUser) {
       return loggedInUser.getId().equals(getCreatedById());
    }
}
