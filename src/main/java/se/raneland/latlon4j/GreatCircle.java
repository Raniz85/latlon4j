/*
 * Copyright (c) 2016, Spiideo
 */

package se.raneland.latlon4j;

import java.util.Iterator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

/**
 * A great circle.
 * @author Raniz
 * @created 2016-07-15.
 */
@Data
public class GreatCircle implements Iterable<LatLon> {

    /** The start of the great circle */
    private final LatLon start;

    /** The end of the great circle */
    private final LatLon end;

    /** The latitude of the start (in radians) (&phi;<sub>1</sub>) */
    private final double p1;

    /** The longitude of the start (in radians) (&lambda;<sub>1</sub>) */
    private final double l1;

    /** The latitude of the end (in radians) (&phi;<sub>2</sub>) */
    private final double p2;

    /** The longitude of the end (in radians) (&lambda;<sub>2</sub>) */
    private final double l2;

    /** The difference in latitude between start and end (&lambda;<sub>12</sub>) */
    private final double l12;

    /** The central angle between start and end (&sigma;<sub>12</sub>) */
    private final double s12;

    /** The distance in meters between start and end along the great circle */
    private final double length;

    /** The azimuth of the great circle at the start (&alpha<sub>0</sub>)*/
    private final double a1;

    /** The azimuth of the great circle at the end (&alpha<sub>1</sub>)*/
    private final double a2;

    /** Longitude where the great circle crosses the equator (&lambda;<sub>0</sub>) */
    private final double l0;

    /** The azimuth of the great circle where it crosses the equator (&alpha<sub>0</sub>)*/
    private final double a0;

    /** The central angle between where the great circle crosses the equator and the start (&sigma;<sub>01</sub>) */
    private final double s01;

    /**
     * Create a new great circle from two sets of coordinates.
     * @param p1 Latitude of the start
     * @param l1 Longitude of the start
     * @param p2 Latitude of the end
     * @param l2 Longitude of the end
     */
    public GreatCircle(final double p1, final double l1, final double p2, final double l2) {
        this(new LatLon(p1, l1), new LatLon(p2, l2));
    }

    /**
     * Create a new great circle from two {@link LatLon LatLons}.
     * @param start The start
     * @param end The end
     */
    public GreatCircle(final LatLon start, final LatLon end) {
        this.start = start;
        this.end = end;
        p1 = Math.toRadians(start.getLatitude());
        l1 = Math.toRadians(start.getLongitude());
        p2 = Math.toRadians(end.getLatitude());
        l2 = Math.toRadians(end.getLongitude());

        // Pre-compute useful values
        l12 = l2 - l1;

        a1 = atan2(sin(l12), cos(p1) * tan(p2) - sin(p1) * cos(l12));
        a2 = atan2(sin(l12), -cos(p2) * tan(p1) + sin(p2) * cos(l12));
        s12 = acos(sin(p1) * sin(p2) + cos(p1) * cos(p2) * cos(l12));
        length = s12 * Constants.EARTH_RADIUS;

        a0 = asin(sin(a1) * cos(start.getLatitude()));
        s01 = atan2(tan(p1), cos(a1));
        double l01 = atan2(sin(a0) * sin(s01), cos(s01));
        l0 = l1 - l01;
    }

    /**
     * Calculate a new {@link LatLon} along the great circle line at a specific distance from the start.
     * @param distance The distance from the start, may be negative or greater than the distance between start and end
     * @return
     */
    public LatLon waypoint(final double distance) {
        // Angular distance between where the great circle crosses the equator and the waypoint
        double s = s01 + distance / Constants.EARTH_RADIUS;
        // Latitude of the waypoint
        double p = asin(cos(a0) * sin(s));
        // Longitude of the waypoint
        double l = atan2(sin(a0) * sin(s), cos(s)) + l0;
        return new LatLon(Math.toDegrees(p), Math.toDegrees(l));
    }

    /**
     * Return an iterator that iterates over waypoints on this great circle with 1 meter between points.
     * @return
     */
    public Iterator<LatLon> iterator() {
        return new WaypointIterator(1);
    }

    /**
     * Return an iterable that iterates over waypoints on this great circle with a specific distance between points.
     * @param delta The distance between points in meters
     * @return
     */
    public Iterable<LatLon> iterable(double delta) {
        if (delta <= 0) {
            throw new IllegalArgumentException("Delta must be greater than zero");
        }
        return new WaypointIterable(delta);
    }

    @RequiredArgsConstructor
    private class WaypointIterable implements Iterable<LatLon> {
        private final double delta;

        @Override
        public Iterator<LatLon> iterator() {
            return new WaypointIterator(delta);
        }
    }

    @RequiredArgsConstructor
    private class WaypointIterator implements Iterator<LatLon> {
        private final double delta;
        private double nextDistance = 0;

        @Override
        public boolean hasNext() {
            return nextDistance <= length;
        }

        @Override
        public LatLon next() {
            final LatLon ll = waypoint(nextDistance);
            nextDistance = Math.max(length, nextDistance + delta);
            return ll;
        }
    }
}
