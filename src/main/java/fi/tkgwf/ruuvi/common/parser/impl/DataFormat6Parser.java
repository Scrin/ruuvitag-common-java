package fi.tkgwf.ruuvi.common.parser.impl;

import java.util.Arrays;

import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;
import fi.tkgwf.ruuvi.common.parser.DataFormatParser;
import fi.tkgwf.ruuvi.common.utils.ByteUtils;

public class DataFormat6Parser implements DataFormatParser {

    private final int[] RUUVI_COMPANY_IDENTIFIER = { 0x99, 0x04 }; // 0x0499

    @Override
    public RuuviMeasurement parse(byte[] data) {
        if (data.length < 2 || (data[0] & 0xFF) != RUUVI_COMPANY_IDENTIFIER[0]
                || (data[1] & 0xFF) != RUUVI_COMPANY_IDENTIFIER[1]) {
            return null;
        }
        data = Arrays.copyOfRange(data, 2, data.length); // discard the first 2 bytes, the company identifier
        if (data.length < 20 || data[0] != 6) {
            return null;
        }
        RuuviMeasurement m = new RuuviMeasurement();
        m.setDataFormat(data[0] & 0xFF);

        // Temperature: bytes 1-2, signed 16-bit, 0.005°C resolution, 0x8000 = invalid
        // Range: -163.835°C to +163.835°C
        // Example: 0x0E38 = 3640 * 0.005 = 18.200°C
        if (!ByteUtils.isMinSignedShort(data[1], data[2])) {
            m.setTemperature((data[1] << 8 | data[2] & 0xFF) / 200d);
        }

        // Humidity: bytes 3-4, unsigned 16-bit, 0.0025% resolution, 0xFFFF = invalid
        // Range: 0% to 100% (values >100 indicate sensor error)
        // Example: 0x6F30 = 28464 * 0.0025 = 71.160%
        if (!ByteUtils.isMaxUnsignedShort(data[3], data[4])) {
            m.setHumidity(((data[3] & 0xFF) << 8 | data[4] & 0xFF) / 400d);
        }

        // Pressure: bytes 5-6, unsigned 16-bit, 1 Pa resolution, 0xFFFF = invalid
        // Range: 50000 Pa to 115534 Pa (500-1155.34 hPa)
        // Example: 0xC7A8 = 51112 + 50000 = 101112 Pa (1011.12 hPa)
        if (!ByteUtils.isMaxUnsignedShort(data[5], data[6])) {
            m.setPressure((double) ((data[5] & 0xFF) << 8 | data[6] & 0xFF) + 50000);
        }

        // PM2.5: bytes 7-8, unsigned 16-bit, 0.1 μg/m³ resolution, 0xFFFF = invalid
        // Range: 0 to 6553.4 μg/m³ (sensor limited to 1000 μg/m³)
        // Example: 0x0064 = 100 * 0.1 = 10.0 μg/m³
        if (!ByteUtils.isMaxUnsignedShort(data[7], data[8])) {
            m.setPm25(((data[7] & 0xFF) << 8 | data[8] & 0xFF) / 10d);
        }

        // CO2: bytes 9-10, unsigned 16-bit, 1 ppm resolution, 0xFFFF = invalid
        // Range: 0 to 40000 ppm (ambient typically 400-2000 ppm)
        // Example: 0x03E8 = 1000 ppm
        if (!ByteUtils.isMaxUnsignedShort(data[9], data[10])) {
            m.setCo2((data[9] & 0xFF) << 8 | data[10] & 0xFF);
        }

        // VOC index: 9-bit value combining byte 11 (8 bits) and bit 6 of byte 16 (1 bit)
        // The 8 bits from byte 11 are shifted left by 1 to make room for the 9th bit from flags
        // 511 (0x1FF) is the reserved value indicating invalid/not available
        // See: https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6#flags
        int vocIndex = ((data[11] & 0xFF) << 1) | ((data[16] >> 6) & 0x01);
        if (vocIndex != 511) {
            m.setVocIndex(vocIndex);
        }

        // NOx index: 9-bit value combining byte 12 (8 bits) and bit 7 of byte 16 (1 bit)
        // The 8 bits from byte 12 are shifted left by 1 to make room for the 9th bit from flags
        // 511 (0x1FF) is the reserved value indicating invalid/not available
        // See: https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6#flags
        int noxIndex = ((data[12] & 0xFF) << 1) | ((data[16] >> 7) & 0x01);
        if (noxIndex != 511) {
            m.setNoxIndex(noxIndex);
        }

        // Luminosity: byte 13, 8-bit logarithmic encoding, 255 = invalid
        // Range: 0-65535 lux (logarithmic scale)
        // Formula: exp(CODE * (ln(65536)/254)) - 1
        // Example: 0x80 = 244.06 lux
        if (!ByteUtils.isMaxUnsignedByte(data[13])) {
            double delta = Math.log(65536d) / 254d;
            double luminosity = Math.exp((data[13] & 0xFF) * delta) - 1;
            m.setLuminosity(luminosity);
        }

        // Measurement sequence: byte 15, 8-bit counter
        // The highest valid value is 255, there is no "invalid / not available" value
        // as this counter tracks the E1 format counter.
        // Used to detect packet loss and duplicate transmissions
        // Example: 0x2A = 42 (42nd measurement in sequence)
        m.setMeasurementSequenceNumber(data[15] & 0xff);

        // Calibration status: bit 0 of byte 16 (flags)
        // Bit 0: 0 = calibration complete, 1 = calibration in progress
        // Other bits: See VOC/NOx index calculations above
        // Example: 0x01 = calibration in progress
        boolean calibrationInProgress = (data[16] & 0x01) != 0;
        m.setCalibrationInProgress(calibrationInProgress);

        return m;
    }
}
