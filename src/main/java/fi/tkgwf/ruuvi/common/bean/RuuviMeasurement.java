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
                .append(")")
                .toString();
    }
}
