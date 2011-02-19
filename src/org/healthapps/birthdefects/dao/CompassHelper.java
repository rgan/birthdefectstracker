package org.healthapps.birthdefects.dao;

import org.compass.core.Compass;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.gps.CompassGps;
import org.compass.gps.device.jdo.Jdo2GpsDevice;
import org.compass.gps.impl.SingleCompassGps;

public class CompassHelper {

    private static Compass compass;
    private static CompassGps compassGps;

    private CompassHelper() {
    }

    private static void initialize() {
        compass = new CompassConfiguration()
                .setConnection("gae://index")
                .setSetting(CompassEnvironment.ExecutorManager.EXECUTOR_MANAGER_TYPE, "disabled")
                .addScan("org.healthapps.birthdefects.model")
                .buildCompass();

        compassGps = new SingleCompassGps(compass);
        final Jdo2GpsDevice gpsDevice = new Jdo2GpsDevice("appengine", PMF.get());
        compassGps.addGpsDevice(gpsDevice);
        gpsDevice.setMirrorDataChanges(true);
        compassGps.start();
    }

    public static Compass getCompass() {
        if (compass == null) {
            initialize();
        }
        return compass;
    }

}
