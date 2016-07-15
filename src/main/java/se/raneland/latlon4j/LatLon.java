/*
 * Copyright (c) 2016, Spiideo
 */

package se.raneland.latlon4j;

import lombok.Data;

import static java.lang.Math.cos;

/**
 * A geographic location on the earth.
 *
 * @author Raniz
 * @created 2016-07-15
 */
@Data
public class LatLon {

    private final double latitude;

    private final double longitude;

    /**
     * Create a new LatLon.
     * @param latitude The latitude, must be within [-90, 90]
     * @param longitude The longitude, will be normalized to be within [-180,180]
     */
    public LatLon(double latitude, double longitude) {
        if(latitude > 90 || latitude < -90) {
            throw new IllegalArgumentException("Latitude must be within [-90,90]");
        }
        this.latitude = latitude;
        double tempLon = ((longitude + 180) % 360 + 360) % 360;
        if (tempLon == 0 && longitude > 0) {
            this.longitude = 180;
        } else {
            this.longitude = tempLon - 180;
        }
    }

    /**
     * Check if another LatLon describes the same location as this one.
     * @param other The other LatLon
     * @param epsilon The maximum difference in kilometers
     * @return
     */
    public boolean isSameLocation(LatLon other, double epsilon) {
        final double latE = epsilon / Constants.EARTH_RADIUS;
        final double lonE = epsilon / Constants.EARTH_RADIUS / cos(latitude);
        return Math.abs(latitude - other.latitude) < epsilon
                && Math.abs(longitude - other.longitude) < epsilon;
    }
}
