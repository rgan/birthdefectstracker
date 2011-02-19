package org.healthapps.birthdefects.model;

import org.healthapps.birthdefects.dao.BirthDefectsDao;
import org.healthapps.birthdefects.model.CSVLine;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;
import org.compass.annotations.SearchableId;

import javax.jdo.annotations.*;
import java.util.ArrayList;
import java.util.List;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@Searchable
public class EnvHazard implements Locatable, CSVLine {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @SearchableId
    private Long id;
    @Persistent
    @SearchableProperty
    private String name;
    @Persistent
    @SearchableProperty
    private String description;
    @Persistent
    private String vertices;
    @Persistent
    private Double nelat;
    @Persistent
    private Double nelon;
    @Persistent
    private Double swlat;
    @Persistent
    private Double swlon;
    @Persistent
    private Double centerLat;
    @Persistent
    private Double centerLon;
    @Persistent
    @SearchableProperty
    private String naicDesc;

    public final static int MAX_DESC_LENGTH = 200;
    public final static int MAX_VERTICES_LENGTH = 500;
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String VERTICES = "vertices";
    private static final String REGEX_VALID_VERTICES = "\\A[\\;\\,\\s:.0-9_-]+\\Z";
    public static final String LAT_FIELD = "centerLat";
    public static final String NAIC = "Industry Classification";

    public EnvHazard(String name, String description, String vertices, SpatialExtent boundingBox, String naicDesc) {
        this.name = name;
        this.description = description;
        this.vertices = vertices;
        this.nelat = boundingBox.getNE().getLat();
        this.nelon = boundingBox.getNE().getLon();
        this.swlat = boundingBox.getSW().getLat();
        this.swlon = boundingBox.getSW().getLon();
        this.centerLat = (nelat+swlat)/2.0;
        this.centerLon = (nelon + swlon)/2.0;
        this.naicDesc = naicDesc;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<GeoLocation> getVertices() {
        List<GeoLocation> result = new ArrayList<GeoLocation>();
        if ( vertices == null) {
            return result;
        }
        String[] verticesArray = vertices.split(";");
        for (String vertexStr : verticesArray) {
            String[] coords = vertexStr.split(",");
            result.add(new GeoLocation(Double.parseDouble(coords[0]), Double.parseDouble(coords[1])));
        }
        return result;
    }

    public String getVerticesAsString() {
        return vertices;
    }

    public List<GeoLocation> getBoundingBox() {
        List<GeoLocation> result = new ArrayList<GeoLocation>();
        result.add(new GeoLocation(nelat, nelon));
        result.add(new GeoLocation(swlat, swlon));
        return result;
    }

    public GeoLocation getBboxCenter() {
        if (centerLat == null) {
            return null;
        }
        return new GeoLocation(centerLat, centerLon);
    }

    public ErrorMessages validate(BirthDefectsDao dao) {
        ErrorMessages errors = TextValidator.validate(name, Person.MAX_NAME_LENGTH, 1, NAME);
        errors.add(TextValidator.validate(description, MAX_DESC_LENGTH, 1, DESCRIPTION));
        errors.add(TextValidator.validate(vertices, MAX_VERTICES_LENGTH, 1, VERTICES, REGEX_VALID_VERTICES));
        errors.add(TextValidator.validate(naicDesc, Person.MAX_NAME_LENGTH, 1, NAIC));

        if (id == null && dao.findEnvHazardByName(name) != null) {
            errors.add(new BDError("nameExists"));
        }
        return errors;
    }

    public GeoLocation geoLocation() {
        return getBboxCenter();
    }

    public String getNAICTitle() {
        return naicDesc;
    }

    public SpatialExtent getExtent() {
        return new SpatialExtent(new GeoLocation(nelat, nelon), new GeoLocation(swlat, swlon));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnvHazard envHazard = (EnvHazard) o;

        if (centerLat != null ? !centerLat.equals(envHazard.centerLat) : envHazard.centerLat != null) return false;
        if (centerLon != null ? !centerLon.equals(envHazard.centerLon) : envHazard.centerLon != null) return false;
        if (description != null ? !description.equals(envHazard.description) : envHazard.description != null)
            return false;
        if (id != null ? !id.equals(envHazard.id) : envHazard.id != null) return false;
        if (naicDesc != null ? !naicDesc.equals(envHazard.naicDesc) : envHazard.naicDesc != null) return false;
        if (name != null ? !name.equals(envHazard.name) : envHazard.name != null) return false;
        if (nelat != null ? !nelat.equals(envHazard.nelat) : envHazard.nelat != null) return false;
        if (nelon != null ? !nelon.equals(envHazard.nelon) : envHazard.nelon != null) return false;
        if (swlat != null ? !swlat.equals(envHazard.swlat) : envHazard.swlat != null) return false;
        if (swlon != null ? !swlon.equals(envHazard.swlon) : envHazard.swlon != null) return false;
        if (vertices != null ? !vertices.equals(envHazard.vertices) : envHazard.vertices != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (vertices != null ? vertices.hashCode() : 0);
        result = 31 * result + (nelat != null ? nelat.hashCode() : 0);
        result = 31 * result + (nelon != null ? nelon.hashCode() : 0);
        result = 31 * result + (swlat != null ? swlat.hashCode() : 0);
        result = 31 * result + (swlon != null ? swlon.hashCode() : 0);
        result = 31 * result + (centerLat != null ? centerLat.hashCode() : 0);
        result = 31 * result + (centerLon != null ? centerLon.hashCode() : 0);
        result = 31 * result + (naicDesc != null ? naicDesc.hashCode() : 0);
        return result;
    }

    public String toCsv() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append(",");
        builder.append(naicDesc).append(",");
        builder.append(description).append(",");
        builder.append(",").append(",");
        builder.append("\"").append(getVerticesAsString()).append("\"");
        return builder.toString();
    }
}
