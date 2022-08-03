package fi.tkgwf.ruuvi.common;

import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;
import fi.tkgwf.ruuvi.common.parser.impl.AnyDataFormatParser;
import junit.framework.TestCase;
import org.apache.commons.codec.DecoderException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.util.Arrays;

import org.apache.commons.codec.binary.Hex;

public class ParserTest extends TestCase {
    private AnyDataFormatParser parser;

    private byte[] eddystoneData(String hash) {
        byte[] hashBytes = hash.getBytes();
        byte[] data = new byte[13 + hashBytes.length];
        // Eddystone ID 0xAA 0xFE
        data[0] = (byte) 0xAA;
        data[1] = (byte) 0xFE;
        data[2] = (byte) 0x10; // Frame type: URL
        data[3] = (byte) 0xF8; // Power
        data[4] = (byte) 0x03; // https://
        data[5] = 'r';
        data[6] = 'u';
        data[7] = 'u';
        data[8] = '.';
        data[9] = 'v';
        data[10] = 'i';
        data[11] = '/';
        data[12] = '#';
        System.arraycopy(hashBytes, 0, data, 13, hashBytes.length);
        return data;
    }

    private byte[] dataWithCompany(String rawHex) {
        try {
            byte[] rawBytes = Hex.decodeHex(rawHex);
            byte[] data = new byte[2 + rawBytes.length];
            data[0] = (byte) 0x99; // company id
            data[1] = (byte) 0x04; // company id
            System.arraycopy(rawBytes, 0, data, 2, rawBytes.length);
            return data;
        } catch (DecoderException e) {
            Assert.fail();
            return null;
        }
    }

    @Before
    public void setUp() {
        this.parser = new AnyDataFormatParser();
    }

    /**
     * test_decode_is_valid in
     * https://github.com/ttu/ruuvitag-sensor/blob/master/tests/test_decoder.py
     */
    @Test
    public void testDecodeEddystone() {
        // https://ruu.vi/#AjwYAMFc
        RuuviMeasurement m = parser.parse(eddystoneData("AjwYAMFc"));
        assertEquals(24.0, m.getTemperature());
        assertEquals(99500.0, m.getPressure());
        assertEquals(30.0, m.getHumidity());
        assertEquals((Integer) 2, m.getDataFormat());
        assertNull(m.getAccelerationY());
        assertNull(m.getAccelerationZ());
        assertNull(m.getBatteryVoltage());
        assertNull(m.getMeasurementSequenceNumber());
        assertNull(m.getMovementCounter());
        assertNull(m.getTxPower());
    }

    /**
     * test_decode_is_valid_case in
     * https://github.com/ttu/ruuvitag-sensor/blob/master/tests/test_decoder.py
     */
    @Test
    public void testDecodeEddystone2() {
        // https://ruu.vi/#AjgbAMFc
        RuuviMeasurement m = parser.parse(eddystoneData("AjgbAMFc"));
        assertEquals(27.0, m.getTemperature());
        assertEquals(99500.0, m.getPressure());
        assertEquals(28.0, m.getHumidity());
        assertEquals((Integer) 2, m.getDataFormat());
        assertNull(m.getAccelerationY());
        assertNull(m.getAccelerationZ());
        assertNull(m.getBatteryVoltage());
        assertNull(m.getMeasurementSequenceNumber());
        assertNull(m.getMovementCounter());
        assertNull(m.getTxPower());
    }

    /**
     * test_decode_is_valid_weatherstation_2017_04_12 in
     * https://github.com/ttu/ruuvitag-sensor/blob/master/tests/test_decoder.py
     */
    @Test
    public void testDecodeEddystone3() {
        // https://ruu.vi/#AjUX1MAw0
        RuuviMeasurement m = parser.parse(eddystoneData("AjUX1MAw0"));
        // assertEquals(23.828125, m.getTemperature());
        assertEquals(99200.0, m.getPressure());
        assertEquals(26.5, m.getHumidity());
        assertEquals((Integer) 2, m.getDataFormat());
        assertNull(m.getAccelerationY());
        assertNull(m.getAccelerationZ());
        assertNull(m.getBatteryVoltage());
        assertNull(m.getMeasurementSequenceNumber());
        assertNull(m.getMovementCounter());
        assertNull(m.getTxPower());
    }

    /**
     * test_df3decode_is_valid in
     * https://github.com/ttu/ruuvitag-sensor/blob/master/tests/test_decoder.py
     */
    @Test
    public void testDataFormat3() {
        // {format}{humidity}{temp}{pressure}{accX}{accY}{accZ}{batt}
        RuuviMeasurement m = parser
                .parse(dataWithCompany("03-29-1A1E-CE1E-FC18-F942-02CA-0B5300000000BB".replace("-", "")));
        assertEquals(26.3, m.getTemperature());
        assertEquals(102766.0, m.getPressure());
        assertEquals(20.5, m.getHumidity());
        assertEquals(2.8990, m.getBatteryVoltage());
        assertEquals(-1.0, m.getAccelerationX());
        assertEquals((Integer) 3, m.getDataFormat());
        assertNull(m.getMeasurementSequenceNumber());
        assertNull(m.getMovementCounter());
        assertNull(m.getTxPower());
    }

    /**
     * test_df3decode_is_valid in
     * https://github.com/ttu/ruuvitag-sensor/blob/master/tests/test_decoder.py
     */
    @Test
    public void testDataFormat3Alt() {
        // {format}{humidity}{temp}{pressure}{accX}{accY}{accZ}{batt}
        RuuviMeasurement m = parser.parse(dataWithCompany("03-29-1A1E-CE1E-FC18-F942-02CA-0B53BB".replace("-", "")));
        assertEquals(26.3, m.getTemperature());
        assertEquals(102766.0, m.getPressure());
        assertEquals(20.5, m.getHumidity());
        assertEquals(2.8990, m.getBatteryVoltage());
        assertEquals(-1.0, m.getAccelerationX());
        assertEquals((Integer) 3, m.getDataFormat());
        assertNull(m.getMeasurementSequenceNumber());
        assertNull(m.getMovementCounter());
        assertNull(m.getTxPower());
    }

    /**
     * test_df3decode_is_valid_max_values in
     * https://github.com/ttu/ruuvitag-sensor/blob/master/tests/test_decoder.py
     */
    @Test
    public void testDataFormat3MaxValues() {
        // {format}{humidity}{temp}{pressure}{accX}{accY}{accZ}{batt}
        RuuviMeasurement m = parser
                .parse(dataWithCompany("03-C8-7F63-FFFF-03E8-03E8-03E8-FFFF-00000000BB".replace("-", "")));
        assertEquals(127.99, m.getTemperature());
        assertEquals(115535.0, m.getPressure());
        assertEquals(100.0, m.getHumidity());
        assertEquals(65.535, m.getBatteryVoltage());
        assertEquals(1.0, m.getAccelerationX());
        assertEquals(1.0, m.getAccelerationY());
        assertEquals(1.0, m.getAccelerationZ());
        assertEquals((Integer) 3, m.getDataFormat());
        assertNull(m.getMeasurementSequenceNumber());
        assertNull(m.getMovementCounter());
        assertNull(m.getTxPower());
    }

    /**
     * test_df3decode_is_valid_min_values in
     * https://github.com/ttu/ruuvitag-sensor/blob/master/tests/test_decoder.py
     */
    @Test
    public void testDataFormat3MinValues() {
        // {format}{humidity}{temp}{pressure}{accX}{accY}{accZ}{batt}
        RuuviMeasurement m = parser
                .parse(dataWithCompany("03-00-FF63-0000-FC18-FC18-FC18-0000-00000000BB".replace("-", "")));
        assertEquals(-127.99, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getBatteryVoltage());
        assertEquals(-1.0, m.getAccelerationX());
        assertEquals(-1.0, m.getAccelerationY());
        assertEquals(-1.0, m.getAccelerationZ());
        assertEquals((Integer) 3, m.getDataFormat());
        assertNull(m.getMeasurementSequenceNumber());
        assertNull(m.getMovementCounter());
        assertNull(m.getTxPower());
    }

    /**
     * test_df5decode_is_valid in
     * https://github.com/ttu/ruuvitag-sensor/blob/master/tests/test_decoder.py
     */
    @Test
    public void testDataFormat5() {
        // {format}{temp}{humidity}{pressure}{accX}{accY}{accZ}{power_info}{movement_counter}{measurement_sequence}{mac}
        RuuviMeasurement m = parser
                .parse(dataWithCompany("05-12FC-5394-C37C-0004-FFFC-040C-AC36-42-00CD-CBB8334C884F".replace("-", "")));
        assertEquals(24.3, m.getTemperature());
        assertEquals(100044.0, m.getPressure());
        assertEquals(53.49, m.getHumidity());
        assertEquals(2.9770000000000003, m.getBatteryVoltage());
        assertEquals(0.004, m.getAccelerationX());
        assertEquals(-0.004, m.getAccelerationY());
        assertEquals(1.036, m.getAccelerationZ());
        assertEquals((Integer) 5, m.getDataFormat());
        assertEquals((Integer) 205, m.getMeasurementSequenceNumber());
        assertEquals((Integer) 66, m.getMovementCounter());
        assertEquals((Integer) 4, m.getTxPower());
    }

    /**
     * See https://github.com/ruuvi/ruuvi-sensor-protocols and "reference" python
     * implementation
     */
    @Test
    public void testDataFormat5SomeUnavailable() {
        // {format}{temp}{humidity}{pressure}{accX}{accY}{accZ}{power_info}{movement_counter}{measurement_sequence}{mac}
        RuuviMeasurement m = parser
                .parse(dataWithCompany("05-12FC-FFFF-C37C-8000-8000-8000-FFFF-FF-FFFF-CBB8334C884F".replace("-", "")));
        assertEquals(24.3, m.getTemperature());
        assertEquals(100044.0, m.getPressure());
        assertNull(m.getHumidity());
        assertNull(m.getBatteryVoltage());
        assertNull(m.getAccelerationX());
        assertNull(m.getAccelerationY());
        assertNull(m.getAccelerationZ());
        assertEquals((Integer) 5, m.getDataFormat());
        assertNull(m.getMeasurementSequenceNumber());
        assertNull(m.getMovementCounter());
        assertNull(m.getTxPower());
    }

    /**
     * "Valid data" official test vector
     * hhttps://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-5-rawv2
     */
    @Test
    public void testDataFormat5TestVectorValid() {
        RuuviMeasurement m = parser
                .parse(dataWithCompany("0512FC5394C37C0004FFFC040CAC364200CDCBB8334C884F"));
        assertEquals(24.3, m.getTemperature());
        assertEquals(100044.0, m.getPressure());
        assertEquals(53.49, m.getHumidity());
        assertEquals(2.9770000000000003, m.getBatteryVoltage());
        assertEquals(0.004, m.getAccelerationX());
        assertEquals(-0.004, m.getAccelerationY());
        assertEquals(1.036, m.getAccelerationZ());
        assertEquals((Integer) 5, m.getDataFormat());
        assertEquals((Integer) 205, m.getMeasurementSequenceNumber());
        assertEquals((Integer) 66, m.getMovementCounter());
        assertEquals((Integer) 4, m.getTxPower());
    }

    /**
     * "Maximum values" official test vector
     * https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-5-rawv2
     */
    @Test
    public void testDataFormat5TestVectorMaxValues() {
        RuuviMeasurement m = parser
                .parse(dataWithCompany("057FFFFFFEFFFE7FFF7FFF7FFFFFDEFEFFFECBB8334C884F"));
        assertEquals(163.835, m.getTemperature());
        assertEquals(115534.0, m.getPressure());
        assertEquals(163.8350, m.getHumidity());
        assertEquals(3.646, m.getBatteryVoltage());
        assertEquals(32.767, m.getAccelerationX());
        assertEquals(32.767, m.getAccelerationY());
        assertEquals(32.767, m.getAccelerationZ());
        assertEquals((Integer) 5, m.getDataFormat());
        assertEquals((Integer) 65534, m.getMeasurementSequenceNumber());
        assertEquals((Integer) 254, m.getMovementCounter());
        assertEquals((Integer) 20, m.getTxPower());
    }

    /**
     * "Minimum values" official test vector
     * https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-5-rawv2
     */
    @Test
    public void testDataFormat5TestVectorMinValues() {
        RuuviMeasurement m = parser
                .parse(dataWithCompany("058001000000008001800180010000000000CBB8334C884F"));
        assertEquals(-163.835, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(1.6, m.getBatteryVoltage());
        assertEquals(-32.767, m.getAccelerationX());
        assertEquals(-32.767, m.getAccelerationY());
        assertEquals(-32.767, m.getAccelerationZ());
        assertEquals((Integer) 5, m.getDataFormat());
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertEquals((Integer) 0, m.getMovementCounter());
        assertEquals((Integer) (-40), m.getTxPower());
    }

    /**
     * "Invalid values" official test vector
     * https://github.com/ruuvi/ruuvi-sensor-protocols/blob/master/dataformat_05.md
     */
    @Test
    public void testDataFormat5TestVectorInvalidValues() {
        RuuviMeasurement m = parser
                .parse(dataWithCompany("058000FFFFFFFF800080008000FFFFFFFFFFFFFFFFFFFFFF"));
        assertNull(m.getTemperature());
        assertNull(m.getPressure());
        assertNull(m.getHumidity());
        assertNull(m.getBatteryVoltage());
        assertNull(m.getAccelerationX());
        assertNull(m.getAccelerationY());
        assertNull(m.getAccelerationZ());
        assertEquals((Integer) 5, m.getDataFormat());
        assertNull(m.getMeasurementSequenceNumber());
        assertNull(m.getMovementCounter());
        assertNull(m.getTxPower());
    }

    @Test
    public void testInvalid() {
        assertNull(parser.parse("XXX".getBytes()));
    }
}
