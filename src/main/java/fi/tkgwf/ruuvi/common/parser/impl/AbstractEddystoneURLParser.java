package fi.tkgwf.ruuvi.common.parser.impl;

import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;
import fi.tkgwf.ruuvi.common.parser.DataFormatParser;

public abstract class AbstractEddystoneURLParser implements DataFormatParser {

    private static final String RUUVI_BASE_URL = "ruu.vi/#";

    abstract protected byte[] base64ToByteArray(String base64);

    @Override
    public RuuviMeasurement parse(byte[] data) {
        String hashPart = getRuuviUrlHashPart(data);
        if (hashPart == null) {
            return null; // not a ruuvi url
        }
        byte[] measurementData;
        try {
            measurementData = base64ToByteArray(hashPart);
        } catch (IllegalArgumentException ex) {
            return null; // V2 format will throw this when trying to parse V4 and vice versa
        }
        if (measurementData.length < 6 || measurementData[0] != 2 && measurementData[0] != 4) {
            return null; // unknown type
        }
        RuuviMeasurement measurement = new RuuviMeasurement();
        measurement.setDataFormat(measurementData[0] & 0xFF);

        measurement.setHumidity(((measurementData[1] & 0xFF)) / 2d);

        int temperatureSign = (measurementData[2] >> 7) & 1;
        int temperatureBase = measurementData[2] & 0x7F;
        double temperatureFraction = measurementData[3] / 100d;
        measurement.setTemperature(temperatureBase + temperatureFraction);
        if (temperatureSign == 1) {
            measurement.setTemperature(measurement.getTemperature() * -1);
        }

        int pressureHi = measurementData[4] & 0xFF;
        int pressureLo = measurementData[5] & 0xFF;
        measurement.setPressure((double) pressureHi * 256 + 50000 + pressureLo);
        return measurement;
    }

    private String getRuuviUrlHashPart(byte[] data) {
        if (data.length < 15) {
            return null; // too short
        }
        if ((data[0] & 0xFF) != 0xAA && (data[1] & 0xFF) != 0xFE) {
            return null; // not an eddystone UUID
        }
        if (data[2] != 0x10) {
            return null; // not an eddystone URL
        }
        if (data[4] != 0x03) {
            return null; // not https://
        }
        String basePart = new String(data, 5, data.length - (5));
        if (!basePart.startsWith(RUUVI_BASE_URL)) {
            return null; // not a ruuvi url
        }
        int preLength = 5 + RUUVI_BASE_URL.length();
        return new String(data, preLength, data.length - preLength);
    }
}
