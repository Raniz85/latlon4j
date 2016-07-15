/*
 * Copyright (c) 2016, Spiideo
 */

package se.raneland.latlon4j

import spock.lang.Specification
import spock.lang.Unroll

import static spock.util.matcher.HamcrestMatchers.closeTo
import static spock.util.matcher.HamcrestSupport.that

/**
 * @author Raniz
 * @created 2016-07-15.
 */
class GreatCircleTest extends Specification {

    static final def ONE_DEGREE_IN_KM = Math.toRadians(1) * Constants.EARTH_RADIUS;

    @Unroll("That length calculations are correct ([#p1, #l1] -> [#p2, #l2] = #expectedLength)")
    def "That length calculations are correct"() {
        given: "A great circle"
        def gc = new GreatCircle(p1, l1, p2, l2)

        expect: "The length to be correct"
        that gc.length, closeTo(expectedLength, 1e-5) // 1 cm

        where: // todo: needs more verified tests
        p1 | l1 | p2 | l2 | expectedLength
        0  | 0  | 0  | 1  | ONE_DEGREE_IN_KM
        0  | 0  | 1  | 0  | ONE_DEGREE_IN_KM
    }

    @Unroll("That waypoint calculations are correct ([#p1, #l1] -> [#p2, #l2] + #distance = #expectedPoint)")
    def "That waypoint calculations are correct"() {
        given: "A great circle"
        def gc = new GreatCircle(p1, l1, p2, l2)

        when: "A waypoint is calculated"
        def wp = gc.waypoint distance

        then: "The waypoint is the expected one"
        expectedPoint.isSameLocation(wp, 1e-5) // 1 cm

        where: // todo: needs more verified tests
        p1 | l1 | p2 | l2 | distance         | expectedPoint
        0  | 0  | 0  | 1  | ONE_DEGREE_IN_KM | new LatLon(0, 1)
        0  | 0  | 1  | 0  | ONE_DEGREE_IN_KM | new LatLon(1, 0)
    }
}
