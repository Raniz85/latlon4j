/*
 * Copyright (c) 2016, Spiideo
 */

package se.raneland.latlon4j

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Raniz
 * @created 2016-07-15.
 */
class LatLonTest extends Specification {

    @Unroll("That longitudes are normalised correctly (#longitude => #expectedLongitude")
    def "That longitudes are normalised correctly"() {
        given: "A LatLon"
        def ll = new LatLon(0, longitude)

        expect: "That the longitude has been normalised correctly"
        ll.longitude == expectedLongitude

        where:
        longitude | expectedLongitude
        0         | 0
        90        | 90
        -90       | -90
        180       | 180
        -180      | -180
        -181      | 179
        181       | -179
        360       | 0
        361       | 1
        1080      | 0
        540       | 180
        -540      | -180
    }
}
