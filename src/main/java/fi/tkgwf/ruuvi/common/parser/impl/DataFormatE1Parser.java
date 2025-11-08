package fi.tkgwf.ruuvi.common.parser.impl;

import java.util.Arrays;

import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;
import fi.tkgwf.ruuvi.common.parser.DataFormatParser;
import fi.tkgwf.ruuvi.common.utils.ByteUtils;

/**
 * Parser for Ruuvi Data Format E1 (Extended v1)
 *
 * This format is used by Ruuvi Air sensors (BT5.0+ preferred)
 * It encodes sensor data in a 40-byte BLE advertisement packet.
 *
 * Format Structure:
 * Byte 0: Format identifier (0xE1)
 * Bytes 1-2: Temperature (signed int, ×0.005 for °C)
 * Bytes 3-4: Humidity (unsigned int, ×0.0025 for %)
 * Bytes 5-6: Pressure (unsigned int, offset +50000, in Pa)
 * Bytes 7-14: PM (PM1.0, PM2.5, PM4.0, PM10.0 - each 2B unsigned, ×0.1 for µg/m³)
 * Bytes 15-16: CO2 (unsigned int, in ppm)
 * Byte 17: VOC Index MSB (8 bits, LSB in flags bit 6)
 * Byte 18: NOx Index MSB (8 bits, LSB in flags bit 7)
 * Bytes 19-21: Luminosity (big-endian 24-bit, ×0.01 for lux)
 * Bytes 22-24: Reserved
 * Bytes 25-27: Measurement sequence (big-endian 24-bit counter)
 * Byte 28: Flags (bit 0: calibration, bit 6: VOC LSB, bit 7: NOx LSB)
 * Bytes 29-33: Reserved
 * Bytes 34-39: MAC Address (6 bytes)
 */
public class DataFormatE1Parser implements DataFormatParser {

    private final int[] RUUVI_COMPANY_IDENTIFIER = { 0x99, 0x04 }; // 0x0499

    @Override
    public RuuviMeasurement parse(byte[] data) {
        if (data.length < 2 || (data[0] & 0xFF) != RUUVI_COMPANY_IDENTIFIER[0]
                || (data[1] & 0xFF) != RUUVI_COMPANY_IDENTIFIER[1]) {
            return null;
        }
        data = Arrays.copyOfRange(data, 2, data.length); // discard the first 2 bytes, the company identifier
        if (data.length < 40 || data[0] != (byte) 0xE1) {
            return null;
        }

        RuuviMeasurement m = new RuuviMeasurement();
        m.setDataFormat((byte) 0xE1 & 0xFF);

        // Temperature: bytes 1-2, signed 16-bit, 0.005°C resolution, 0x8000 = invalid
        // Range: -163.835°C to +163.835°C
        if (!ByteUtils.isMinSignedShort(data[1], data[2])) {
            m.setTemperature((data[1] << 8 | data[2] & 0xFF) / 200d);
        }

        // Humidity: bytes 3-4, unsigned 16-bit, 0.0025% resolution, 0xFFFF = invalid
        // Range: 0% to 100% (values >100 indicate sensor error)
        if (!ByteUtils.isMaxUnsignedShort(data[3], data[4])) {
            m.setHumidity(((data[3] & 0xFF) << 8 | data[4] & 0xFF) / 400d);
        }

        // Pressure: bytes 5-6, unsigned 16-bit, 1 Pa resolution, 0xFFFF = invalid
        // Range: 50000 Pa to 115534 Pa (500-1155.34 hPa)
        if (!ByteUtils.isMaxUnsignedShort(data[5], data[6])) {
            m.setPressure((double) ((data[5] & 0xFF) << 8 | data[6] & 0xFF) + 50000);
        }

        // PM1.0: bytes 7-8, unsigned 16-bit, 0.1 μg/m³ resolution, 0xFFFF = invalid
        if (!ByteUtils.isMaxUnsignedShort(data[7], data[8])) {
            m.setPm1(((data[7] & 0xFF) << 8 | data[8] & 0xFF) / 10d);
        }

        // PM2.5: bytes 9-10, unsigned 16-bit, 0.1 μg/m³ resolution, 0xFFFF = invalid
        if (!ByteUtils.isMaxUnsignedShort(data[9], data[10])) {
            m.setPm25(((data[9] & 0xFF) << 8 | data[10] & 0xFF) / 10d);
        }

        // PM4.0: bytes 11-12, unsigned 16-bit, 0.1 μg/m³ resolution, 0xFFFF = invalid
        if (!ByteUtils.isMaxUnsignedShort(data[11], data[12])) {
            m.setPm4(((data[11] & 0xFF) << 8 | data[12] & 0xFF) / 10d);
        }

        // PM10.0: bytes 13-14, unsigned 16-bit, 0.1 μg/m³ resolution, 0xFFFF = invalid
        if (!ByteUtils.isMaxUnsignedShort(data[13], data[14])) {
            m.setPm10(((data[13] & 0xFF) << 8 | data[14] & 0xFF) / 10d);
        }

        // CO2: bytes 15-16, unsigned 16-bit, 1 ppm resolution, 0xFFFF = invalid
        // Range: 0 to 40000 ppm
        if (!ByteUtils.isMaxUnsignedShort(data[15], data[16])) {
            m.setCo2((data[15] & 0xFF) << 8 | data[16] & 0xFF);
        }

        // VOC Index: 9-bit value combining byte 17 (8 bits) and bit 6 of byte 28 (1 bit)
        // The 8 bits from byte 17 are shifted left by 1 to make room for the 9th bit from flags
        // 511 (0x1FF) is the reserved value indicating invalid/not available
        int vocIndex = ((data[17] & 0xFF) << 1) | ((data[28] >> 6) & 0x01);
        if (vocIndex != 511) {
            m.setVocIndex(vocIndex);
        }

        // NOx Index: 9-bit value combining byte 18 (8 bits) and bit 7 of byte 28 (1 bit)
        // The 8 bits from byte 18 are shifted left by 1 to make room for the 9th bit from flags
        // 511 (0x1FF) is the reserved value indicating invalid/not available
        int noxIndex = ((data[18] & 0xFF) << 1) | ((data[28] >> 7) & 0x01);
        if (noxIndex != 511) {
            m.setNoxIndex(noxIndex);
        }

        // Luminosity: bytes 19-21, 24-bit big-endian, 0.01 lux resolution
        // 0xFFFFFF = invalid
        int luminosityRaw = ((data[19] & 0xFF) << 16) | ((data[20] & 0xFF) << 8) | (data[21] & 0xFF);
        if (luminosityRaw != 0xFFFFFF) {
            m.setLuminosity(luminosityRaw * 0.01d);
        }

        // Measurement sequence: bytes 25-27, 24-bit big-endian counter
        // Used to detect packet loss and duplicate transmissions
        int sequenceNumber = ((data[25] & 0xFF) << 16) | ((data[26] & 0xFF) << 8) | (data[27] & 0xFF);
        m.setMeasurementSequenceNumber(sequenceNumber);

        // Calibration status: bit 0 of byte 28 (flags)
        // Bit 0: 0 = calibration complete, 1 = calibration in progress
        boolean calibrationInProgress = (data[28] & 0x01) != 0;
        m.setCalibrationInProgress(calibrationInProgress);

        return m;
    }
}
