package fi.tkgwf.ruuvi.common;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;
import fi.tkgwf.ruuvi.common.parser.impl.AnyDataFormatParser;
import junit.framework.TestCase;

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

    private byte[] dataWithCompany(byte[] rawWithoutCompany) {
        byte[] data = new byte[2 + rawWithoutCompany.length];
        data[0] = (byte) 0x99; // company id
        data[1] = (byte) 0x04; // company id
        System.arraycopy(rawWithoutCompany, 0, data, 2, rawWithoutCompany.length);
        return data;
    }

    @Override
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
        RuuviMeasurement m = parser.parse(dataWithCompany("0512FC5394C37C0004FFFC040CAC364200CDCBB8334C884F"));
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
        RuuviMeasurement m = parser.parse(dataWithCompany("057FFFFFFEFFFE7FFF7FFF7FFFFFDEFEFFFECBB8334C884F"));
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
        RuuviMeasurement m = parser.parse(dataWithCompany("058001000000008001800180010000000000CBB8334C884F"));
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
        RuuviMeasurement m = parser.parse(dataWithCompany("058000FFFFFFFF800080008000FFFFFFFFFFFFFFFFFFFFFF"));
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

    /**
     * "Invalid values" official test vector
     * https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6
     */
    @Test
    public void testDataFormat6TestVectorInvalidValues() {
        RuuviMeasurement m = parser
                .parse(dataWithCompany("068000FFFFFFFFFFFFFFFFFFFFFFXXFFFFFFFFFF".replace('X', '0')));
        assertNull(m.getTemperature());
        assertNull(m.getPressure());
        assertNull(m.getHumidity());
        assertNull(m.getPm25());
        assertNull(m.getCo2());
        assertNull(m.getVocIndex());
        assertNull(m.getNoxIndex());
        assertNull(m.getLuminosity());
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals((Integer) 255, m.getMeasurementSequenceNumber());
        assertTrue(m.isCalibrationInProgress());
        assertNull(m.getBatteryVoltage());
        assertNull(m.getAccelerationX());
    }

    /**
     * "Invalid values" test vector for Data Format E1
     * Tests handling of sentinel values (0xFFFF for unsigned shorts, 0x8000 for signed short)
     */
    @Test
    public void testDataFormatE1TestVectorInvalidValues() {
        // Format E1 with all invalid sentinel values (40 bytes)
        RuuviMeasurement m = parser.parse(dataWithCompany(
                "E18000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFXXXXXXFFFFFFFEXXXXXXXXXXFFFFFFFFFFFF".replace('X', '0')));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertNull(m.getTemperature());
        assertNull(m.getPressure());
        assertNull(m.getHumidity());
        assertNull(m.getPm1());
        assertNull(m.getPm25());
        assertNull(m.getPm4());
        assertNull(m.getPm10());
        assertNull(m.getCo2());
        assertNull(m.getVocIndex());
        assertNull(m.getNoxIndex());
        assertNull(m.getLuminosity());
        assertEquals((Integer) 16777215, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_ok
     * Comprehensive test with mixed values from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetOk() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x17, (byte) 0x0C, // Temperature
                (byte) 0x56, (byte) 0x68, // Humidity
                (byte) 0xC7, (byte) 0x9E, // Pressure
                (byte) 0x00, (byte) 0x65, // PM1.0
                (byte) 0x00, (byte) 0x70, // PM2.5
                (byte) 0x04, (byte) 0xBD, // PM4.0
                (byte) 0x11, (byte) 0xCA, // PM10.0
                (byte) 0x00, (byte) 0xC9, // CO2
                (byte) 0x05, // VOC
                (byte) 0x01, // NOX
                (byte) 0x13, (byte) 0xE0, (byte) 0xAC, // Luminosity
                (byte) 0x3D, // Sound inst
                (byte) 0x4A, // Sound avg
                (byte) 0x9C, // Sound peak
                (byte) 0xDE, (byte) 0xCD, (byte) 0xEE, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(29.5, m.getTemperature(), 0.01);
        assertEquals(101102.0, m.getPressure());
        assertEquals(55.3, m.getHumidity(), 0.01);
        assertEquals(10.1, m.getPm1(), 0.01);
        assertEquals(11.2, m.getPm25(), 0.01);
        assertEquals(121.3, m.getPm4(), 0.01);
        assertEquals(455.4, m.getPm10(), 0.01);
        assertEquals((Integer) 201, m.getCo2());
        assertEquals((Integer) 10, m.getVocIndex());
        assertEquals((Integer) 2, m.getNoxIndex());
        assertEquals(13027, m.getLuminosity(), 0.01);
        // assertEquals(42.4, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(47.6, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(80.4, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0xDECDEE, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_zeroes
     * Minimum/zero values from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetZeroes() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_temperature
     * Temperature focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetTemperature() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x13, (byte) 0x88, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(25.0, m.getTemperature(), 0.01);
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_humidity
     * Humidity focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetHumidity() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x6D, (byte) 0x60, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(70.0, m.getHumidity(), 0.01);
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_pressure
     * Pressure focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetPressure() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0xC3, (byte) 0x50, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(100000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_pm1m0
     * PM1.0 focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetPm1m0() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x1B, (byte) 0x58, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(700.0, m.getPm1(), 0.01);
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_pm2m5
     * PM2.5 focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetPm2m5() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x1B, (byte) 0x58, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(700.0, m.getPm25(), 0.01);
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_pm4m0
     * PM4.0 focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetPm4m0() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x1B, (byte) 0x58, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(700.0, m.getPm4(), 0.01);
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_pm10m0
     * PM10.0 focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetPm10m0() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x1B, (byte) 0x58, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(700.0, m.getPm10(), 0.01);
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_co2
     * CO2 focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetCo2() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x88, (byte) 0xB8, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 35000, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_voc
     * VOC focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetVoc() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0xF9, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x40, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 499, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_nox
     * NOX focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetNox() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0xF8, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x80, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 497, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_luminosity
     * Luminosity focus test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetLuminosity() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0xDB, (byte) 0xBA, (byte) 0x02, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(144000.02, m.getLuminosity(), 0.01);
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0x000000, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    // /**
    // * test_ruuvi_endpoint_e1_get_sound_inst_dba
    // * Instant sound test from
    // *
    // https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
    // */
    // @Test
    // public void testDataFormatE1GetSoundInstDba() {
    // byte[] buf = new byte[] { (byte) 0xE1, // Data type
    // (byte) 0x00, (byte) 0x00, // Temperature
    // (byte) 0x00, (byte) 0x00, // Humidity
    // (byte) 0x00, (byte) 0x00, // Pressure
    // (byte) 0x00, (byte) 0x00, // PM1.0
    // (byte) 0x00, (byte) 0x00, // PM2.5
    // (byte) 0x00, (byte) 0x00, // PM4.0
    // (byte) 0x00, (byte) 0x00, // PM10.0
    // (byte) 0x00, (byte) 0x00, // CO2
    // (byte) 0x00, // VOC
    // (byte) 0x00, // NOX
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
    // (byte) 0xCF, // Sound inst
    // (byte) 0x00, // Sound avg
    // (byte) 0x00, // Sound peak
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
    // (byte) 0x08, // Flags
    // (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
    // (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
    // };
    // RuuviMeasurement m = parser.parse(dataWithCompany(buf));
    // assertEquals((Integer) 0xE1, m.getDataFormat());
    // assertEquals(101.0, m.getSoundInstantDbA(), 0.01); // Reserved
    // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
    // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
    // }

    // /**
    // * test_ruuvi_endpoint_e1_get_sound_avg_dba
    // * Average sound test from
    // *
    // https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
    // */
    // @Test
    // public void testDataFormatE1GetSoundAvgDba() {
    // byte[] buf = new byte[] { (byte) 0xE1, // Data type
    // (byte) 0x00, (byte) 0x00, // Temperature
    // (byte) 0x00, (byte) 0x00, // Humidity
    // (byte) 0x00, (byte) 0x00, // Pressure
    // (byte) 0x00, (byte) 0x00, // PM1.0
    // (byte) 0x00, (byte) 0x00, // PM2.5
    // (byte) 0x00, (byte) 0x00, // PM4.0
    // (byte) 0x00, (byte) 0x00, // PM10.0
    // (byte) 0x00, (byte) 0x00, // CO2
    // (byte) 0x00, // VOC
    // (byte) 0x00, // NOX
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
    // (byte) 0x00, // Sound inst
    // (byte) 0xCF, // Sound avg
    // (byte) 0x00, // Sound peak
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
    // (byte) 0x10, // Flags
    // (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
    // (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
    // };
    // RuuviMeasurement m = parser.parse(dataWithCompany(buf));
    // assertEquals((Integer) 0xE1, m.getDataFormat());
    // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
    // assertEquals(101.0, m.getSoundAverageDbA(), 0.01); // Reserved
    // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
    // }

    // /**
    // * test_ruuvi_endpoint_e1_get_sound_peak_spl_db
    // * Peak sound test from
    // *
    // https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
    // */
    // @Test
    // public void testDataFormatE1GetSoundPeakSplDb() {
    // byte[] buf = new byte[] { (byte) 0xE1, // Data type
    // (byte) 0x00, (byte) 0x00, // Temperature
    // (byte) 0x00, (byte) 0x00, // Humidity
    // (byte) 0x00, (byte) 0x00, // Pressure
    // (byte) 0x00, (byte) 0x00, // PM1.0
    // (byte) 0x00, (byte) 0x00, // PM2.5
    // (byte) 0x00, (byte) 0x00, // PM4.0
    // (byte) 0x00, (byte) 0x00, // PM10.0
    // (byte) 0x00, (byte) 0x00, // CO2
    // (byte) 0x00, // VOC
    // (byte) 0x00, // NOX
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
    // (byte) 0x00, // Sound inst
    // (byte) 0x00, // Sound avg
    // (byte) 0xCF, // Sound peak
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
    // (byte) 0x20, // Flags
    // (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
    // (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
    // };
    // RuuviMeasurement m = parser.parse(dataWithCompany(buf));
    // assertEquals((Integer) 0xE1, m.getDataFormat());
    // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
    // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
    // assertEquals(101.0, m.getSoundPeakSplDb(), 0.01); // Reserved
    // }

    /**
     * test_ruuvi_endpoint_e1_get_seq_cnt
     * Sequence counter test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetSeqCnt() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0xAB, (byte) 0xCD, (byte) 0xEF, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertEquals((Integer) 0xABCDEF, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_e1_get_flag_calibration_in_progress
     * Calibration flag test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetFlagCalibrationInProgress() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x01, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertTrue(m.isCalibrationInProgress());
    }

    // /**
    // * test_ruuvi_endpoint_e1_get_flag_button_pressed
    // * Button pressed flag test from
    // *
    // https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
    // */
    // @Test
    // public void testDataFormatE1GetFlagButtonPressed() {
    // byte[] buf = new byte[] { (byte) 0xE1, // Data type
    // (byte) 0x00, (byte) 0x00, // Temperature
    // (byte) 0x00, (byte) 0x00, // Humidity
    // (byte) 0x00, (byte) 0x00, // Pressure
    // (byte) 0x00, (byte) 0x00, // PM1.0
    // (byte) 0x00, (byte) 0x00, // PM2.5
    // (byte) 0x00, (byte) 0x00, // PM4.0
    // (byte) 0x00, (byte) 0x00, // PM10.0
    // (byte) 0x00, (byte) 0x00, // CO2
    // (byte) 0x00, // VOC
    // (byte) 0x00, // NOX
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
    // (byte) 0x00, // Sound inst
    // (byte) 0x00, // Sound avg
    // (byte) 0x00, // Sound peak
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
    // (byte) 0x02, // Flags
    // (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
    // (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
    // };
    // RuuviMeasurement m = parser.parse(dataWithCompany(buf));
    // assertEquals((Integer) 0xE1, m.getDataFormat());
    // assertTrue(m.isButtonPressed());
    // }

    // /**
    // * test_ruuvi_endpoint_e1_get_flag_rtc_running_on_boot
    // * RTC running on boot flag test from
    // *
    // https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
    // */
    // @Test
    // public void testDataFormatE1GetFlagRtcRunningOnBoot() {
    // byte[] buf = new byte[] { (byte) 0xE1, // Data type
    // (byte) 0x00, (byte) 0x00, // Temperature
    // (byte) 0x00, (byte) 0x00, // Humidity
    // (byte) 0x00, (byte) 0x00, // Pressure
    // (byte) 0x00, (byte) 0x00, // PM1.0
    // (byte) 0x00, (byte) 0x00, // PM2.5
    // (byte) 0x00, (byte) 0x00, // PM4.0
    // (byte) 0x00, (byte) 0x00, // PM10.0
    // (byte) 0x00, (byte) 0x00, // CO2
    // (byte) 0x00, // VOC
    // (byte) 0x00, // NOX
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
    // (byte) 0x00, // Sound inst
    // (byte) 0x00, // Sound avg
    // (byte) 0x00, // Sound peak
    // (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
    // (byte) 0x04, // Flags
    // (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
    // (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
    // };
    // RuuviMeasurement m = parser.parse(dataWithCompany(buf));
    // assertEquals((Integer) 0xE1, m.getDataFormat());
    // assertTrue(m.isRtcRunningOnBoot());
    // }

    /**
     * test_ruuvi_endpoint_e1_get_ok_max
     * Maximum values test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetOkMax() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x7F, (byte) 0xFF, // Temperature
                (byte) 0x9C, (byte) 0x40, // Humidity
                (byte) 0xFF, (byte) 0xFE, // Pressure
                (byte) 0x27, (byte) 0x10, // PM1.0
                (byte) 0x27, (byte) 0x10, // PM2.5
                (byte) 0x27, (byte) 0x10, // PM4.0
                (byte) 0x27, (byte) 0x10, // PM10.0
                (byte) 0x9C, (byte) 0x40, // CO2
                (byte) 0xFA, // VOC
                (byte) 0xFA, // NOX
                (byte) 0xDC, (byte) 0x28, (byte) 0xF0, // Luminosity
                (byte) 0xFF, // Sound inst
                (byte) 0xFF, // Sound avg
                (byte) 0xFF, // Sound peak
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, // Seq cnt
                (byte) 0x07, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(163.835, m.getTemperature(), 0.001);
        assertEquals(115534.0, m.getPressure());
        assertEquals(100.0, m.getHumidity());
        assertEquals(1000.0, m.getPm1());
        assertEquals(1000.0, m.getPm25());
        assertEquals(1000.0, m.getPm4());
        assertEquals(1000.0, m.getPm10());
        assertEquals((Integer) 40000, m.getCo2());
        assertEquals((Integer) 500, m.getVocIndex());
        assertEquals((Integer) 500, m.getNoxIndex());
        assertEquals(144284.0, m.getLuminosity(), 0.01);
        // assertEquals(120.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(120.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(120.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertTrue(m.isCalibrationInProgress());
        // assertTrue(m.isButtonPressed());
        // assertTrue(m.isRtcRunningOnBoot());
    }

    /**
     * test_ruuvi_endpoint_e1_get_ok_min
     * Minimum values test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_e1.c
     */
    @Test
    public void testDataFormatE1GetOkMin() {
        byte[] buf = new byte[] { (byte) 0xE1, // Data type
                (byte) 0x80, (byte) 0x01, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM1.0
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // PM4.0
                (byte) 0x00, (byte) 0x00, // PM10.0
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound inst
                (byte) 0x00, // Sound avg
                (byte) 0x00, // Sound peak
                (byte) 0x00, (byte) 0x00, (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // Reserved
                (byte) 0xCB, (byte) 0xB8, (byte) 0x33, (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 0xE1, m.getDataFormat());
        assertEquals(-163.835, m.getTemperature(), 0.001);
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm1());
        assertEquals(0.0, m.getPm25());
        assertEquals(0.0, m.getPm4());
        assertEquals(0.0, m.getPm10());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundInstantDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        // assertEquals(18.0, m.getSoundPeakSplDb(), 0.01); // Reserved
        assertFalse(m.isCalibrationInProgress());
        // assertFalse(m.isButtonPressed());
        // assertFalse(m.isRtcRunningOnBoot());
    }

    /**
     * test_ruuvi_endpoint_6_get_ok
     * Comprehensive test with mixed values from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetOk() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x17, (byte) 0x0C, // Temperature
                (byte) 0x56, (byte) 0x68, // Humidity
                (byte) 0xC7, (byte) 0x9E, // Pressure
                (byte) 0x00, (byte) 0x70, // PM2.5
                (byte) 0x00, (byte) 0xC9, // CO2
                (byte) 0x05, // VOC
                (byte) 0x01, // NOX
                (byte) 0xD9, // Luminosity
                (byte) 0x4A, // Sound avg dBA
                (byte) 0xCD, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(29.5, m.getTemperature(), 0.01);
        assertEquals(101102.0, m.getPressure());
        assertEquals(55.3, m.getHumidity(), 0.01);
        assertEquals(11.2, m.getPm25(), 0.01);
        assertEquals((Integer) 201, m.getCo2());
        assertEquals((Integer) 10, m.getVocIndex());
        assertEquals((Integer) 2, m.getNoxIndex());
        assertEquals(13026.6689, m.getLuminosity(), 0.01);
        // assertEquals(47.6, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 205, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_zeroes
     * Minimum/zero values from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetZeroes() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_temperature
     * Temperature field test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetTemperature() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x13, (byte) 0x88, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(25.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_humidity
     * Humidity field test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetHumidity() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x6D, (byte) 0x60, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(70.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_pressure
     * Pressure field test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetPressure() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0xC3, (byte) 0x50, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(100000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_pm2p5
     * PM2.5 field test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetPm2p5() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x1B, (byte) 0x58, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(700.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_co2
     * CO2 field test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetCo2() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x88, (byte) 0xB8, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 35000, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_voc
     * VOC field test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetVoc() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0xF9, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x40, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 499, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_nox
     * NOX field test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetNox() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0xF8, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x80, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 497, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_luminosity
     * Luminosity field test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetLuminosity() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0xFD, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(62735.0846876, m.getLuminosity(), 0.01);
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_seq_cnt
     * Sequence counter field test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetSeqCnt() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x00, (byte) 0x00, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0xFA, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x4C, (byte) 0x88, (byte) 0x4F // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(0.0, m.getTemperature());
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 250, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_ok_max
     * Maximum values test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetOkMax() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x7F, (byte) 0xFF, // Temperature
                (byte) 0x9C, (byte) 0x40, // Humidity
                (byte) 0xFF, (byte) 0xFE, // Pressure
                (byte) 0x27, (byte) 0x10, // PM2.5
                (byte) 0x9C, (byte) 0x40, // CO2
                (byte) 0xFA, // VOC
                (byte) 0xFA, // NOX
                (byte) 0xFE, // Luminosity
                (byte) 0xFF, // Sound avg dBA
                (byte) 0xFF, // Seq cnt
                (byte) 0x07, // Flags
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(163.835, m.getTemperature(), 0.001);
        assertEquals(115534.0, m.getPressure());
        assertEquals(100.0, m.getHumidity());
        assertEquals(1000.0, m.getPm25());
        assertEquals((Integer) 40000, m.getCo2());
        assertEquals((Integer) 500, m.getVocIndex());
        assertEquals((Integer) 500, m.getNoxIndex());
        assertEquals(65534.99999, m.getLuminosity(), 0.01);
        // assertEquals(120.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 255, m.getMeasurementSequenceNumber());
        assertTrue(m.isCalibrationInProgress());
    }

    /**
     * test_ruuvi_endpoint_6_get_ok_min
     * Minimum values test from
     * https://github.com/ruuvi/ruuvi.endpoints.c/blob/8183d39ae8d0cd1c284881b10804af9b066b8b42/test/test_ruuvi_endpoint_6.c
     */
    @Test
    public void testDataFormat6GetOkMin() {
        byte[] buf = new byte[] { (byte) 0x06, // Data type
                (byte) 0x80, (byte) 0x01, // Temperature
                (byte) 0x00, (byte) 0x00, // Humidity
                (byte) 0x00, (byte) 0x00, // Pressure
                (byte) 0x00, (byte) 0x00, // PM2.5
                (byte) 0x00, (byte) 0x00, // CO2
                (byte) 0x00, // VOC
                (byte) 0x00, // NOX
                (byte) 0x00, // Luminosity
                (byte) 0x00, // Sound avg dBA
                (byte) 0x00, // Seq cnt
                (byte) 0x00, // Flags
                (byte) 0x00, (byte) 0x00, (byte) 0x00 // MAC address
        };
        RuuviMeasurement m = parser.parse(dataWithCompany(buf));
        assertEquals((Integer) 6, m.getDataFormat());
        assertEquals(-163.835, m.getTemperature(), 0.001);
        assertEquals(50000.0, m.getPressure());
        assertEquals(0.0, m.getHumidity());
        assertEquals(0.0, m.getPm25());
        assertEquals((Integer) 0, m.getCo2());
        assertEquals((Integer) 0, m.getVocIndex());
        assertEquals((Integer) 0, m.getNoxIndex());
        assertEquals(0.0, m.getLuminosity());
        // assertEquals(18.0, m.getSoundAverageDbA(), 0.01); // Reserved
        assertEquals((Integer) 0, m.getMeasurementSequenceNumber());
        assertFalse(m.isCalibrationInProgress());
    }

    @Test
    public void testInvalidNotHex() {
        assertNull(parser.parse("XXX".getBytes()));
    }

    @Test
    public void testInvalidTooShort1() {
        assertNull(parser.parse("0".getBytes()));
    }

    @Test
    public void testInvalidTooShort2() {
        assertNull(parser.parse("00".getBytes()));
    }

    @Test
    public void testInvalidTooShort3() {
        assertNull(parser.parse("000".getBytes()));
    }
}
