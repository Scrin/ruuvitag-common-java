package fi.tkgwf.ruuvi.common.parser.impl;

import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;
import fi.tkgwf.ruuvi.common.utils.ByteUtils;
import java.util.Arrays;
import fi.tkgwf.ruuvi.common.parser.DataFormatParser;

public class DataFormat5Parser implements DataFormatParser {

    private final int[] RUUVI_COMPANY_IDENTIFIER = {0x99, 0x04}; // 0x0499

    @Override
    public RuuviMeasurement parse(byte[] data) {
        if (data.length < 2 || (data[0] & 0xFF) != RUUVI_COMPANY_IDENTIFIER[0] || (data[1] & 0xFF) != RUUVI_COMPANY_IDENTIFIER[1]) {
            return null;
        }
        data = Arrays.copyOfRange(data, 2, data.length); // discard the first 2 bytes, the company identifier
        if (data.length < 24 || data[0] != 5) {
            return null;
        }
        RuuviMeasurement m = new RuuviMeasurement();
        m.setDataFormat(data[0] & 0xFF);

        if (!ByteUtils.isMaxSignedShort(data[1], data[2])) {
            m.setTemperature((data[1] << 8 | data[2] & 0xFF) / 200d);
        }

        if (!ByteUtils.isMaxUnsignedShort(data[3], data[4])) {
            m.setHumidity(((data[3] & 0xFF) << 8 | data[4] & 0xFF) / 400d);
        }

        if (!ByteUtils.isMaxUnsignedShort(data[5], data[6])) {
            m.setPressure((double) ((data[5] & 0xFF) << 8 | data[6] & 0xFF) + 50000);
        }

        if (!ByteUtils.isMaxSignedShort(data[7], data[8])) {
            m.setAccelerationX((data[7] << 8 | data[8] & 0xFF) / 1000d);
        }
        if (!ByteUtils.isMaxSignedShort(data[9], data[10])) {
            m.setAccelerationY((data[9] << 8 | data[10] & 0xFF) / 1000d);
        }
        if (!ByteUtils.isMaxSignedShort(data[11], data[2])) {
            m.setAccelerationZ((data[11] << 8 | data[12] & 0xFF) / 1000d);
        }

        int powerInfo = (data[13] & 0xFF) << 8 | data[14] & 0xFF;
        if ((powerInfo >>> 5) != 0b11111111111) {
            m.setBatteryVoltage((powerInfo >>> 5) / 1000d + 1.6d);
        }
        if ((powerInfo & 0b11111) != 0b11111) {
            m.setTxPower((Integer) (powerInfo & 0b11111) * 2 - 40);
        }

        if (!ByteUtils.isMaxUnsignedByte(data[15])) {
            m.setMovementCounter(data[15] & 0xFF);
        }
        if (!ByteUtils.isMaxSignedShort(data[16], data[17])) {
            m.setMeasurementSequenceNumber((Integer) (data[16] & 0xFF) << 8 | data[17] & 0xFF);
        }

        return m;
    }
}
