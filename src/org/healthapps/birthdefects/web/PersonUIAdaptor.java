package org.healthapps.birthdefects.web;

import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.model.*;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersonUIAdaptor implements CSVLine {

    private Set<String> birthDefects;
    private String lat;
    private String lon;
    private String name;
    private Long id;
    private String dateOfBirth;
    private SpatialExtent bbox;
    private String createdByUserName;
    private Long createdById;

    public PersonUIAdaptor(Person person, BirthDefectsDao dao, User loggedInUser) {
        if (loggedInUser !=null && person.isCreatedBy(loggedInUser)) {
            lat = person.getLat() != null ? person.getLat().toString() : "";
            lon = person.getLon() != null ? person.getLon().toString() : "";
        }
        CSquareCode csCode = person.getCSCode();
        bbox = CSquareCode.boundingBoxfrom(csCode.getTens(), csCode.getUnits());
        id = person.getId();
        if (loggedInUser !=null && person.isCreatedBy(loggedInUser)) {
            this.name = person.getName();
        } else {
            this.name = "";
        }
        this.dateOfBirth = getDateOfBirth(person);
        birthDefects = new HashSet<String>();
        for (Long id : person.getBirthDefectIds()) {
            BirthDefect defect = dao.getBirthDefectById(id);
            birthDefects.add(defect.getName());
        }
        createdById = person.getCreatedById();
        createdByUserName = dao.findUserById(person.getCreatedById()).getUsername();
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public List<GeoLocation> getBbox() {
        return bbox.getVertices();
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    private String getDateOfBirth(Person person) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        if (person.getDateOfBirth() == null) {
            return "";
        }
        return dateFormatter.format(person.getDateOfBirth());
    }

    public Set<String> getBirthDefects() {
        return birthDefects;
    }

    public String getCreatedBy() {
        return createdByUserName;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public static String csvHeader() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name").append(",")
                .append("Date of Birth/Hazard Type").append(",")
                .append("Birth Defects/Hazard Desc").append(",")
                .append("Latitude").append(",")
                .append("Longitude").append(",")
                .append("Bounding Box");
        return builder.toString();
    }

    public String toCsv() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append(",");
        builder.append(dateOfBirth).append(",");
        for(String birthDefect : birthDefects) {
            builder.append(birthDefect).append(";");
        }
        builder.append(",");
        if (lat != null && lon != null) {
            builder.append(lat).append(",");
            builder.append(lon).append(",");
        } else {
            builder.append(",").append(",");
        }
        builder.append("\"");
        for(GeoLocation loc : getBbox()) {
            builder.append(loc.getLat()).
                    append(",").append(loc.getLon()).append(";");
        }
        builder.append("\"");
        return builder.toString();
    }
}
