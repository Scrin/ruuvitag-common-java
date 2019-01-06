package fi.tkgwf.ruuvi.common.parser;

import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;

/**
 * Creates {@link RuuviMeasurement} instances from raw data.
 */
public interface DataFormatParser {

    /**
     * Parse a raw data packet and create a {@link RuuviMeasurement}
     *
     * @param data the raw data bytes
     * @return an instance of a {@link RuuviMeasurement}, or null if this parser
     * cannot understand the data
     */
    RuuviMeasurement parse(byte[] data);
}
