package org.healthapps.birthdefects.dao;

import org.compass.core.Compass;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.gps.CompassGps;
import org.compass.gps.device.jdo.Jdo2GpsDevice;
import org.compass.gps.impl.SingleCompassGps;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.JDOHelper;

public final class PMF {
    private static final PersistenceManagerFactory pmfInstance =
            JDOHelper.getPersistenceManagerFactory("transactions-optional");
    
    private PMF() {
    }

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }

}
