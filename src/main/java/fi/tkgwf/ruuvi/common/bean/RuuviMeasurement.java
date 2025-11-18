package fi.tkgwf.ruuvi.common.bean;

/**
 * This class contains all the possible fields/data acquirable from a RuuviTag
 * in a "human format", for example the temperature as a decimal number rather
 * than an integer meaning one 200th of a degree. Not all fields are necessarily
 * present depending on the data format and implementation.
 */
public class RuuviMeasurement {

    /**
     * Ruuvi Data format, see: https://github.com/ruuvi/ruuvi-sensor-protocols
     */
    private Integer dataFormat;
    /**
     * Temperature in Celsius
     */
    private Double temperature;
    /**
     * Relative humidity in percentage (0-100)
     */
    private Double humidity;
    /**
     * Pressure in Pa
     */
    private Double pressure;
    /**
     * Acceleration of X axis in G
     */
    private Double accelerationX;
    /**
     * Acceleration of Y axis in G
     */
    private Double accelerationY;
    /**
     * Acceleration of Z axis in G
     */
    private Double accelerationZ;
    /**
     * Battery voltage in Volts
     */
    private Double batteryVoltage;
    /**
     * TX power in dBm
     */
    private Integer txPower;
    /**
     * Movement counter (incremented by interrupts from the accelerometer)
     */
    private Integer movementCounter;
    /**
     * Measurement sequence number (incremented every time a new measurement is
     * made). Useful for measurement de-duplication.
     */
    private Integer measurementSequenceNumber;
    /**
     * PM2.5 in µg/m³
     */
    private Double pm25;
    /**
     * CO2 in ppm
     */
    private Integer co2;
    /**
     * VOC index (0-500, unitless)
     */
    private Integer vocIndex;
    /**
     * NOx index (0-500, unitless)
     */
    private Integer noxIndex;
    /**
     * Luminosity in lux
     */
    private Double luminosity;
    /**
     * PM1.0 in µg/m³
     */
    private Double pm1;
    /**
     * PM4.0 in µg/m³
     */
    private Double pm4;
    /**
     * PM10.0 in µg/m³
     */
    private Double pm10;
    /**
     * Calibration in progress flag (Format 6+, bit 0 of flags byte)
     */
    private Boolean calibrationInProgress;

    /**
     * Gets Ruuvi Data Format
     * <p>
     * See: https://github.com/ruuvi/ruuvi-sensor-protocols
     *
     * @return Ruuvi data format or null if not available
     */
    public Integer getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(Integer dataFormat) {
        this.dataFormat = dataFormat;
    }

    /**
     * Get temperature, in Celcius.
     *
     * @return temperature measurement or null if not available
     */
    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    /**
     * Get relative humidity, in percentage.
     *
     * @return relative humidity measurement or null if not available
     */
    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    /**
     * Get pressure, in Pascal.
     *
     * @return pressure measurement or null if not available
     */
    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    /**
     * Get acceleration on X axis, in g.
     * <p>
     * See https://en.wikipedia.org/wiki/Standard_gravity
     *
     * @return acceleration measurement or null if not available
     */
    public Double getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(Double accelerationX) {
        this.accelerationX = accelerationX;
    }

    /**
     * Get acceleration on Y axis, in g.
     * <p>
     * See https://en.wikipedia.org/wiki/Standard_gravity
     *
     * @return acceleration measurement or null if not available
     */
    public Double getAccelerationY() {
        return accelerationY;
    }

    public void setAccelerationY(Double accelerationY) {
        this.accelerationY = accelerationY;
    }

    /**
     * Get acceleration on Z axis, in g.
     * <p>
     * See https://en.wikipedia.org/wiki/Standard_gravity
     *
     * @return acceleration measurement or null if not available
     */
    public Double getAccelerationZ() {
        return accelerationZ;
    }

    public void setAccelerationZ(Double accelerationZ) {
        this.accelerationZ = accelerationZ;
    }

    /**
     * Get battery voltage, in Volts.
     *
     * @return battery voltage measurement or null if not available
     */
    public Double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    /**
     * Get Tx power, in dBm.
     * <p>
     * See https://en.wikipedia.org/wiki/DBm
     *
     * @return Tx power or null if not available
     */
    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    /**
     * Get movement counter value.
     * <p>
     * For details, see https://github.com/ruuvi/ruuvi-sensor-protocols
     *
     * @return movement counter value or null if not available
     */
    public Integer getMovementCounter() {
        return movementCounter;
    }

    public void setMovementCounter(Integer movementCounter) {
        this.movementCounter = movementCounter;
    }

    /**
     * Get measurement sequence number.
     * <p>
     * For details, see https://github.com/ruuvi/ruuvi-sensor-protocols
     *
     * @return measurement sequence number or null if not available
     */
    public Integer getMeasurementSequenceNumber() {
        return measurementSequenceNumber;
    }

    public void setMeasurementSequenceNumber(Integer measurementSequenceNumber) {
        this.measurementSequenceNumber = measurementSequenceNumber;
    }

    /**
     * Get PM 2.5, in micrograms per cubic meter.
     * <p>
     * For details, see https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6
     *
     * @return measurement or null if not available
     */
    public Double getPm25() {
        return pm25;
    }

    public void setPm25(Double pm25) {
        this.pm25 = pm25;
    }

    /**
     * Get CO2 concentration, in PPM.
     * <p>
     * For details, see https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6
     *
     * @return measurement or null if not available
     */
    public Integer getCo2() {
        return co2;
    }

    public void setCo2(Integer co2) {
        this.co2 = co2;
    }

    /**
     * Get VOC index, unitless.
     * <p>
     * For details, see https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6
     *
     * @return measurement or null if not available
     */
    public Integer getVocIndex() {
        return vocIndex;
    }

    public void setVocIndex(Integer vocIndex) {
        this.vocIndex = vocIndex;
    }

    /**
     * Get NOx index, unitless.
     * <p>
     * For details, see https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6
     *
     * @return measurement or null if not available
     */
    public Integer getNoxIndex() {
        return noxIndex;
    }

    public void setNoxIndex(Integer noxIndex) {
        this.noxIndex = noxIndex;
    }

    /**
     * Get luminosity, in lux.
     * <p>
     * For details, see https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6
     *
     * @return measurement or null if not available
     */
    public Double getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(Double luminosity) {
        this.luminosity = luminosity;
    }

    /**
     * Get PM 1.0, in micrograms per cubic meter.
     * <p>
     * For details, see https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6
     *
     * @return measurement or null if not available
     */
    public Double getPm1() {
        return pm1;
    }

    public void setPm1(Double pm1) {
        this.pm1 = pm1;
    }

    /**
     * Get PM 4.0, in micrograms per cubic meter.
     * <p>
     * For details, see https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6
     *
     * @return measurement or null if not available
     */
    public Double getPm4() {
        return pm4;
    }

    public void setPm4(Double pm4) {
        this.pm4 = pm4;
    }

    /**
     * Get PM 10.0, in micrograms per cubic meter.
     * <p>
     * For details, see https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6
     *
     * @return measurement or null if not available
     */
    public Double getPm10() {
        return pm10;
    }

    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }

    /**
     * Get sensor calibration in progress flag.
     * <p>
     * For details, see https://docs.ruuvi.com/communication/bluetooth-advertisements/data-format-6#flags
     *
     * @return true if calibration is in progress (values still improving), false if complete, null if not available
     */
    public Boolean isCalibrationInProgress() {
        return calibrationInProgress;
    }

    public void setCalibrationInProgress(Boolean calibrationInProgress) {
        this.calibrationInProgress = calibrationInProgress;
    }

    @Override
    public String toString() {
        return new StringBuffer("RuuviMeasurement(")
                .append("dataFormat=").append(dataFormat)
                .append(", temperature=").append(temperature)
                .append(", humidity=").append(humidity)
                .append(", pressure=").append(pressure)
                .append(", accelerationX=").append(accelerationX)
                .append(", accelerationY=").append(accelerationY)
                .append(", accelerationZ=").append(accelerationZ)
                .append(", batteryVoltage=").append(batteryVoltage)
                .append(", txPower=").append(txPower)
                .append(", movementCounter=").append(movementCounter)
                .append(", measurementSequenceNumber=").append(measurementSequenceNumber)
                .append(", pm25=").append(pm25)
                .append(", co2=").append(co2)
                .append(", vocIndex=").append(vocIndex)
                .append(", noxIndex=").append(noxIndex)
                .append(", luminosity=").append(luminosity)
                .append(", pm1=").append(pm1)
                .append(", pm4=").append(pm4)
                .append(", pm10=").append(pm10)
                .append(", calibrationInProgress=").append(calibrationInProgress)
                .append(")")
                .toString();
    }
}
