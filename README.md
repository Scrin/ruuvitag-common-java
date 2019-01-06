# RuuviTag Common

ruuvitag-common is a library of utilities to work with RuuviTags, main purpose is providing parsers for parsing raw data from RuuviTags

### WARNING: This library is currently being developed, breaking changes will most likely happen in the near future, additionally the current code is **UNTESTED**, blindly refactored out of RuuviCollector

That being said, comments, suggestions and other contributions are more than welcome. :)

TODO: decide best proper/best naming convention
TODO: general refactoring and cleanup
TODO: check javadoc and comments
TODO: write proper documentation
TODO: write tests
TODO: publish to maven central to avoid needing to build the library locally

### How to use

1. Clone this repository
2. Build and install the library locally: `mvn clean install`
3. Add the dependency to your project:

```xml
<dependency>
    <groupId>fi.tkgwf.ruuvi</groupId>
    <artifactId>ruuvi-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

4. Use the library in your code:

```java
import fi.tkgwf.ruuvi.common.bean.RuuviMeasurement;
import fi.tkgwf.ruuvi.common.parser.DataFormatParser;
import fi.tkgwf.ruuvi.common.parser.impl.AnyDataFormatParser;

public class Example {

    public void showTemperature() {
        DataFormatParser parser = new AnyDataFormatParser();
        byte[] rawData = getRawDataFromSomewhere();
        RuuviMeasurement measurement = parser.parse(rawData);
        System.out.println("Temperature is " + measurement.getTemperature());
    }
}
```
