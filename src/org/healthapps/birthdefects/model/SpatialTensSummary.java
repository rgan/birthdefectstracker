package org.healthapps.birthdefects.model;

import javax.jdo.annotations.*;
import java.util.List;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SpatialTensSummary {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
    // composite primary keys are not supported by app engine
    @Persistent
    private Integer tensCode;
    @Persistent
    private Long count;
    
    public SpatialTensSummary(Integer tensCode, Long count) {
        this.tensCode = tensCode;
        this.count = count;
    }

    public void incrementCount() {
        this.count++;
    }

    public Integer getTensCode() {
        return tensCode;
    }

    public Integer getUnitsCode() {
        return -1;
    }
    
    public List<GeoLocation> getVertices() {
        return CSquareCode.boundingBoxfrom(tensCode).getVertices(); 
    }

    public void decrementCount() {
        this.count = count - 1;
    }

    public Long getCount() {
        return count;
    }
}
