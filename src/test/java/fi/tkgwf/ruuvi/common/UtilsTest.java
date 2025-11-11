package fi.tkgwf.ruuvi.common;

import org.junit.Test;

import fi.tkgwf.ruuvi.common.utils.MeasurementValueCalculator;
import junit.framework.TestCase;

public class UtilsTest extends TestCase {

    @Test
    public void testAirQualityIndex() {
        Double pm25 = 2.3;
        Integer co2 = 832;
        assertEquals(77.75237076248787, MeasurementValueCalculator.airQualityIndex(pm25, co2), 0.01);
    }
}
