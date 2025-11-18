package fi.tkgwf.ruuvi.common.parser.impl;

import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;
import fi.tkgwf.ruuvi.common.parser.DataFormatParser;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class AnyDataFormatParser implements DataFormatParser {

    private final List<DataFormatParser> parsers;

    public AnyDataFormatParser() {
        parsers = new LinkedList<>();
        parsers.add(new DataFormat2Parser());
        parsers.add(new DataFormat3Parser());
        parsers.add(new DataFormat4Parser());
        parsers.add(new DataFormat5Parser());
        parsers.add(new DataFormat6Parser());
        parsers.add(new DataFormatE1Parser());
    }
    
    @Override
    public RuuviMeasurement parse(byte[] data) {
        return parsers.stream().map(h -> h.parse(data)).filter(Objects::nonNull).findAny().orElse(null);
    }
}
