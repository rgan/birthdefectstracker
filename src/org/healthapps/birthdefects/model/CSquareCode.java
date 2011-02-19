package org.healthapps.birthdefects.model;

public class CSquareCode {
    private int tens;  // 10x10
    private int units;   // 1x1
    private int tenths;   //0.1x0.1

    public CSquareCode(int tens, int units, int tenths) {
        this.tens = tens;
        this.units = units;
        this.tenths = tenths;
    }

    public CSquareCode(int tensCode) {
        this(tensCode, -1, -1);
    }

    public CSquareCode(int tensCode, int unitscode) {
        this(tensCode, unitscode, -1);
    }

    public int getTens() {
        return tens;
    }

    public int getUnits() {
        return units;
    }

    public int getTenths() {
        return tenths;
    }

    public static CSquareCode from(double lat, double lon) {
        // see http://www.marine.csiro.au/csquares/
        int firstDigit = firstDigitTensRes(lat, lon);

        double llat = Math.abs(lat);
        if (llat >= 90) {
            llat = 89.9;
        }
        double llon = Math.abs(lon);
        if (llon >= 180) {
            llon = 179.9;
        }
        int secondDigit = (int) (llat / 10);
        int thirdDigit = (int) (llon / 10);

        int tensValue = firstDigit * 1000 + secondDigit * 100 + thirdDigit;
        double latRemainder = llat - (secondDigit * 10);
        double lonRemainder = llon - (thirdDigit * 10);
        secondDigit = (int) latRemainder;
        thirdDigit = (int) lonRemainder;
        firstDigit = getFirstDigitUnitsRes(secondDigit, thirdDigit);

        int unitsValue = firstDigit * 100 + secondDigit * 10 + thirdDigit;
        latRemainder = (latRemainder - secondDigit) * 10;
        lonRemainder = (lonRemainder - thirdDigit) * 10;
        secondDigit = (int) latRemainder;
        thirdDigit = (int) lonRemainder;
        firstDigit = getFirstDigitUnitsRes(secondDigit, thirdDigit);
        int tenthsValue = firstDigit * 100 + secondDigit * 10 + thirdDigit;
        return new CSquareCode(tensValue, unitsValue, tenthsValue);
    }

    private static int getFirstDigitUnitsRes(int i, int j) {
        if (i < 5) {
            if (j < 5) {
                return 1;
            } else {
                return 2;
            }
        } else {
            if (j < 5) {
                return 3;
            } else {
                return 4;
            }
        }
    }

    private static int firstDigitTensRes(double lat, double lon) {
        if (lat >= 0) {
            if (lon >= 0)
                return 1;
            else
                return 7;
        } else {
            if (lon >= 0)
                return 3;
            else
                return 5;
        }
    }

    public static SpatialExtent boundingBoxfrom(Integer tensCode) {
        int quadrantDigit = getQuadrantDigit(tensCode);
        int latDigit = getLatDigitAtTens(tensCode, quadrantDigit);
        int lonDigit = getLonDigitsAtTens(tensCode, quadrantDigit, latDigit);
        int latGlobalQuadrant = getLatGlobalQuadrant(quadrantDigit);
        int lonGlobalQuadrant = getLonGlobalQuadrant(quadrantDigit);
        double centerLat = (latDigit * 10 + 5) * latGlobalQuadrant;
        double centerLon = (lonDigit * 10 + 5) * lonGlobalQuadrant;
        double lowerLat = centerLat - 5;
        double upperLat = centerLat + 5;
        double lowerLon = centerLon - 5;
        double upperLon = centerLon + 5;
        return new SpatialExtent(new GeoLocation(upperLat, upperLon), new GeoLocation(lowerLat, lowerLon));
    }

    private static int getQuadrantDigit(Integer tensCode) {
        return tensCode / 1000;
    }

    private static int getLonGlobalQuadrant(int lonDigit) {
        return (((lonDigit < 5 ? 0 : 10) / 10) * 2 - 1) * -1;
    }

    private static int getLatGlobalQuadrant(int quadrantDigit) {
        return (int) ((Math.abs(quadrantDigit - 4) * 2 < 5 ? 0 : 10) / 5.0) - 1;
    }

    private static int getLonDigitsAtTens(Integer tensCode, int quadrantDigit, int latDigit) {
        return (tensCode - (quadrantDigit * 1000 + latDigit * 100));
    }

    private static int getLatDigitAtTens(Integer tensCode, int quadrantDigit) {
        return (tensCode - quadrantDigit * 1000) / 100;
    }

    public static SpatialExtent boundingBoxfrom(Integer tensCode, Integer unitsCode) {
        int quadrantDigit = getQuadrantDigit(tensCode);
        int latDigit = getLatDigitAtTens(tensCode, quadrantDigit);
        int lonDigit = getLonDigitsAtTens(tensCode, quadrantDigit, latDigit);
        int latGlobalQuadrant = getLatGlobalQuadrant(quadrantDigit);
        int lonGlobalQuadrant = getLonGlobalQuadrant(quadrantDigit);
        int quadrantDigitAtOne = (int) unitsCode / 100;
        int latDigitAtOne = (unitsCode - quadrantDigitAtOne * 100) / 10;
        int lonDigitAtOne = (unitsCode - (quadrantDigitAtOne * 100 + latDigitAtOne * 10));
        double centerLat = ((latDigit * 10) + latDigitAtOne + 0.5) * latGlobalQuadrant;
        double centerLon = ((lonDigit * 10) + lonDigitAtOne + 0.5) * lonGlobalQuadrant;
        double lowerLat = centerLat - 0.5;
        double upperLat = centerLat + 0.5;
        double lowerLon = centerLon - 0.5;
        double upperLon = centerLon + 0.5;
        return new SpatialExtent(new GeoLocation(upperLat, upperLon), new GeoLocation(lowerLat, lowerLon));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CSquareCode that = (CSquareCode) o;

        if (tens != that.tens) return false;
        if (tenths != that.tenths) return false;
        if (units != that.units) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tens;
        result = 31 * result + units;
        result = 31 * result + tenths;
        return result;
    }
}
