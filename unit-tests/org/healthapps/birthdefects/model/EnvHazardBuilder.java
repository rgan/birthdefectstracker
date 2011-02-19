package org.healthapps.birthdefects.model;

public class EnvHazardBuilder {
    private String name = "name";
    private String description = "desc";
    private String vertices = "9.1, 10.2; 10.1,9.1";
    private SpatialExtent boundingBox = new SpatialExtent(new GeoLocation(10.0, 10.0), new GeoLocation(5.0, 5.0));
    private String naicDesc = "farming";

    public EnvHazard build() {
        return new EnvHazard(name, description, vertices, boundingBox, naicDesc);
    }

    public EnvHazardBuilder withName(String name) {
        this.name = name;
        return this;
    }
}
