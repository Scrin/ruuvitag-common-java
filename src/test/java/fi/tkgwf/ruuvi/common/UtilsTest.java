package fi.tkgwf.ruuvi.common;

import org.junit.Test;

import fi.tkgwf.ruuvi.common.utils.ByteUtils;
import fi.tkgwf.ruuvi.common.utils.MeasurementValueCalculator;
import junit.framework.TestCase;

public class UtilsTest extends TestCase {

    // ============= ByteUtils Tests =============

    @Test
    public void testIsMaxSignedByte() {
        // Max signed byte is 127
        assertTrue(ByteUtils.isMaxSignedByte((byte) 127));
        assertFalse(ByteUtils.isMaxSignedByte((byte) 126));
        assertFalse(ByteUtils.isMaxSignedByte((byte) 0));
        assertFalse(ByteUtils.isMaxSignedByte((byte) -1));
        assertFalse(ByteUtils.isMaxSignedByte((byte) -128));
    }

    @Test
    public void testIsMaxUnsignedByte() {
        // Max unsigned byte is 255 (-1 as signed byte)
        assertTrue(ByteUtils.isMaxUnsignedByte((byte) -1)); // 255 in unsigned
        assertFalse(ByteUtils.isMaxUnsignedByte((byte) -2)); // 254 in unsigned
        assertFalse(ByteUtils.isMaxUnsignedByte((byte) 127));
        assertFalse(ByteUtils.isMaxUnsignedByte((byte) 0));
        assertFalse(ByteUtils.isMaxUnsignedByte((byte) -128));
    }

    @Test
    public void testIsMaxSignedShort() {
        // Max signed short is 32767 (0x7FFF)
        assertTrue(ByteUtils.isMaxSignedShort((byte) 127, (byte) -1)); // 0x7FFF
        assertFalse(ByteUtils.isMaxSignedShort((byte) 127, (byte) -2)); // 0x7FFE
        assertFalse(ByteUtils.isMaxSignedShort((byte) 126, (byte) -1)); // 0x7EFF
        assertFalse(ByteUtils.isMaxSignedShort((byte) 0, (byte) 0)); // 0x0000
        assertFalse(ByteUtils.isMaxSignedShort((byte) -128, (byte) 0)); // 0x8000
    }

    @Test
    public void testIsMinSignedShort() {
        // Min signed short is -32768 (0x8000)
        assertTrue(ByteUtils.isMinSignedShort((byte) -128, (byte) 0)); // 0x8000
        assertFalse(ByteUtils.isMinSignedShort((byte) -127, (byte) 0)); // 0x8100
        assertFalse(ByteUtils.isMinSignedShort((byte) -128, (byte) 1)); // 0x8001
        assertFalse(ByteUtils.isMinSignedShort((byte) 127, (byte) -1)); // 0x7FFF
        assertFalse(ByteUtils.isMinSignedShort((byte) 0, (byte) 0)); // 0x0000
    }

    @Test
    public void testIsMaxUnsignedShort() {
        // Max unsigned short is 65535 (0xFFFF, or -1, -1 as signed bytes)
        assertTrue(ByteUtils.isMaxUnsignedShort((byte) -1, (byte) -1)); // 0xFFFF
        assertFalse(ByteUtils.isMaxUnsignedShort((byte) -1, (byte) -2)); // 0xFFFE
        assertFalse(ByteUtils.isMaxUnsignedShort((byte) -2, (byte) -1)); // 0xFEFF
        assertFalse(ByteUtils.isMaxUnsignedShort((byte) 127, (byte) -1)); // 0x7FFF
        assertFalse(ByteUtils.isMaxUnsignedShort((byte) 0, (byte) 0)); // 0x0000
    }

    // ============= MeasurementValueCalculator Tests =============

    @Test
    public void testTotalAcceleration() {
        // Test normal case with positive values
        Double result = MeasurementValueCalculator.totalAcceleration(3.0, 4.0, 0.0);
        assertEquals(5.0, result, 0.001); // 3-4-5 triangle

        // Test with all zeros
        result = MeasurementValueCalculator.totalAcceleration(0.0, 0.0, 0.0);
        assertEquals(0.0, result, 0.001);

        // Test with negative values
        result = MeasurementValueCalculator.totalAcceleration(-3.0, -4.0, 0.0);
        assertEquals(5.0, result, 0.001);

        // Test with null values
        assertNull(MeasurementValueCalculator.totalAcceleration(null, 4.0, 0.0));
        assertNull(MeasurementValueCalculator.totalAcceleration(3.0, null, 0.0));
        assertNull(MeasurementValueCalculator.totalAcceleration(3.0, 4.0, null));
    }

    @Test
    public void testAngleBetweenVectorComponentAndAxis() {
        // Test with 0 degrees (component equals length)
        Double result = MeasurementValueCalculator.angleBetweenVectorComponentAndAxis(1.0, 1.0);
        assertEquals(0.0, result, 0.001);

        // Test with 90 degrees (component is 0)
        result = MeasurementValueCalculator.angleBetweenVectorComponentAndAxis(0.0, 1.0);
        assertEquals(90.0, result, 0.001);

        // Test with 60 degrees (component is half of length)
        result = MeasurementValueCalculator.angleBetweenVectorComponentAndAxis(0.5, 1.0);
        assertEquals(60.0, result, 0.001);

        // Test with null component
        assertNull(MeasurementValueCalculator.angleBetweenVectorComponentAndAxis(null, 1.0));

        // Test with null length
        assertNull(MeasurementValueCalculator.angleBetweenVectorComponentAndAxis(1.0, null));

        // Test with zero length
        assertNull(MeasurementValueCalculator.angleBetweenVectorComponentAndAxis(1.0, 0.0));
    }

    @Test
    public void testAbsoluteHumidity() {
        // Test with standard conditions (20°C, 50% RH)
        Double result = MeasurementValueCalculator.absoluteHumidity(20.0, 50.0);
        assertEquals(8.63909, result, 0.01);

        // Test with 0% relative humidity
        result = MeasurementValueCalculator.absoluteHumidity(20.0, 0.0);
        assertEquals(0.0, result, 0.001);

        // Test with 100% relative humidity
        result = MeasurementValueCalculator.absoluteHumidity(20.0, 100.0);
        assertEquals(17.27818, result, 0.01);

        // Test with null temperature
        assertNull(MeasurementValueCalculator.absoluteHumidity(null, 50.0));

        // Test with null humidity
        assertNull(MeasurementValueCalculator.absoluteHumidity(20.0, null));

        // Test with extreme cold
        result = MeasurementValueCalculator.absoluteHumidity(-40.0, 50.0);
        assertEquals(0.088165, result, 0.01);
    }

    @Test
    public void testDewPoint() {
        // Test with standard conditions (20°C, 50% RH)
        Double result = MeasurementValueCalculator.dewPoint(20.0, 50.0);
        assertNotNull(result);
        assertEquals(9.2700, result, 0.1);

        // Test with 100% relative humidity (dew point equals temperature)
        result = MeasurementValueCalculator.dewPoint(20.0, 100.0);
        assertEquals(20.0, result, 0.1);

        // Test with 0% relative humidity
        assertNull(MeasurementValueCalculator.dewPoint(20.0, 0.0));

        // Test with null temperature
        assertNull(MeasurementValueCalculator.dewPoint(null, 50.0));

        // Test with null humidity
        assertNull(MeasurementValueCalculator.dewPoint(20.0, null));

        // Test with low humidity (20%)
        result = MeasurementValueCalculator.dewPoint(25.0, 20.0);
        assertNotNull(result);
        assertEquals(0.494527, result, 0.1);
    }

    @Test
    public void testEquilibriumVaporPressure() {
        // Test with 0°C (freezing point)
        Double result = MeasurementValueCalculator.equilibriumVaporPressure(0.0);
        assertEquals(611.2, result, 0.001);

        // Test with 20°C (room temperature)
        result = MeasurementValueCalculator.equilibriumVaporPressure(20.0);
        assertNotNull(result);
        assertEquals(2336.9471, result, 0.1);

        // Test with negative temperature
        result = MeasurementValueCalculator.equilibriumVaporPressure(-10.0);
        assertNotNull(result);
        assertEquals(286.67958, result, 0.1);

        // Test with null temperature
        assertNull(MeasurementValueCalculator.equilibriumVaporPressure(null));
    }

    @Test
    public void testAirDensity() {
        // Test with standard conditions (15°C, 50% RH, 101300 Pa)
        Double result = MeasurementValueCalculator.airDensity(15.0, 50.0, 101300.0);
        assertNotNull(result);
        assertEquals(1.22169, result, 0.1);

        // Test with sea level conditions (0°C, 0% RH, 101300 Pa)
        result = MeasurementValueCalculator.airDensity(0.0, 0.0, 101300.0);
        assertNotNull(result);
        assertEquals(1.2929, result, 0.1);

        // Test with high altitude (lower pressure)
        result = MeasurementValueCalculator.airDensity(15.0, 50.0, 50000.0);
        assertNotNull(result);
        assertEquals(0.60103, result, 0.1);

        // Test with null temperature
        assertNull(MeasurementValueCalculator.airDensity(null, 50.0, 101300.0));

        // Test with null humidity
        assertNull(MeasurementValueCalculator.airDensity(15.0, null, 101300.0));

        // Test with null pressure
        assertNull(MeasurementValueCalculator.airDensity(15.0, 50.0, null));
    }

    @Test
    public void testAirQualityIndex() {
        // Original test case
        Double pm25 = 2.3;
        Integer co2 = 832;
        assertEquals(77.75237076248787, MeasurementValueCalculator.airQualityIndex(pm25, co2), 0.01);

        // Test with minimum values
        Double result = MeasurementValueCalculator.airQualityIndex(0.0, 420);
        assertEquals(100.0, result, 0.01); // Best air quality

        // Test with maximum values (PM2.5=60, CO2=2300)
        result = MeasurementValueCalculator.airQualityIndex(60.0, 2300);
        assertEquals(0.0, result, 0.01); // Worst air quality

        // Test with values beyond limits (should be clamped)
        result = MeasurementValueCalculator.airQualityIndex(100.0, 3000);
        assertNotNull(result);
        assertTrue(result >= 0 && result <= 100);

        // Test with null PM2.5
        assertNull(MeasurementValueCalculator.airQualityIndex(null, 832));

        // Test with null CO2
        assertNull(MeasurementValueCalculator.airQualityIndex(2.3, null));

        // Test with moderate values
        result = MeasurementValueCalculator.airQualityIndex(10.0, 1000);
        assertNotNull(result);
        assertTrue(result >= 0 && result <= 100);
    }
}
