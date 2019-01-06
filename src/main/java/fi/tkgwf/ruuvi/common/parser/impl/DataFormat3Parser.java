package fi.tkgwf.ruuvi.common.parser.impl;

import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;
import java.util.Arrays;
import fi.tkgwf.ruuvi.common.parser.DataFormatParser;

public class DataFormat3Parser implements DataFormatParser {

    private final int[] RUUVI_COMPANY_IDENTIFIER = {0x99, 0x04}; // 0x0499

    @Override
    public RuuviMeasurement parse(byte[] data) {
        if (data.length < 2 || (data[0] & 0xFF) != RUUVI_COMPANY_IDENTIFIER[0] || (data[1] & 0xFF) != RUUVI_COMPANY_IDENTIFIER[1]) {
            return null;
        }
        data = Arrays.copyOfRange(data, 2, data.length); // discard the first 2 bytes, the company identifier
        if (data.length < 14 || data[0] != 3) {
            return null;
        }
        RuuviMeasurement m = new RuuviMeasurement();
        m.setDataFormat(data[0] & 0xFF);

        m.setHumidity((Double) ((double) (data[1] & 0xFF)) / 2d);

        int temperatureSign = (data[2] >> 7) & 1;
        int temperatureBase = (data[2] & 0x7F);
        double temperatureFraction = ((float) data[3]) / 100d;
        m.setTemperature(temperatureBase + temperatureFraction);
        if (temperatureSign == 1) {
            m.setTemperature(m.getTemperature() * -1);
        }

        int pressureHi = data[4] & 0xFF;
        int pressureLo = data[5] & 0xFF;
        m.setPressure((Double) (double) pressureHi * 256 + 50000 + pressureLo);

        m.setAccelerationX((data[6] << 8 | data[7] & 0xFF) / 1000d);
        m.setAccelerationY((data[8] << 8 | data[9] & 0xFF) / 1000d);
        m.setAccelerationZ((data[10] << 8 | data[11] & 0xFF) / 1000d);

        int battHi = data[12] & 0xFF;
        int battLo = data[13] & 0xFF;
        m.setBatteryVoltage((battHi * 256 + battLo) / 1000d);
        return m;
    }
}
