package org.healthapps.birthdefects.model;

import javax.jdo.annotations.*;
import java.util.List;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SpatialSummary {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    // composite primary keys are not supported by app engine
    @Persistent
    private Integer tensCode;
    @Persistent
    private Integer unitsCode;
    @Persistent
    private Integer tenthsCode;
    @Persistent
    private Long count;

    public SpatialSummary(CSquareCode csCode) {
        this.tensCode = csCode.getTens();
        this.unitsCode = csCode.getUnits();
        this.tenthsCode = csCode.getTenths();
        this.count = 1L;
    }

    public Integer getTensCode() {
        return tensCode;
    }

    public Integer getUnitsCode() {
        return unitsCode;
    }

    public Integer getTenthsCode() {
        return tenthsCode;
    }

    public void incrementCount() {
        this.count++;
    }

    public List<GeoLocation> getVertices() {
        return CSquareCode.boundingBoxfrom(tensCode, unitsCode).getVertices();
    }

    public void decrementCount() {
        this.count--;
    }

    public Long getCount() {
        return count;
    }

}
