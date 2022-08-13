# RuuviTag Common

ruuvitag-common is a library of utilities to work with RuuviTags, main purpose is providing parsers for parsing raw data from RuuviTags

Do note that this library is still being developed so comments, suggestions and other contributions are more than welcome. :)

### How to use

1. Add the repository to your pom:

```xml
<repositories>
    <repository>
        <id>ruuvitag-common-java-mvn-repo</id>
        <url>https://raw.github.com/Scrin/ruuvitag-common-java/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```

Alternative option: You can also clone this repository, then build and install the library locally with: `mvn clean install`

2. Add the dependency to your project:

```xml
<dependency>
    <groupId>fi.tkgwf.ruuvi</groupId>
    <artifactId>ruuvitag-common</artifactId>
    <version>1.0.2</version>
</dependency>
```

3. Use the library in your code:

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
