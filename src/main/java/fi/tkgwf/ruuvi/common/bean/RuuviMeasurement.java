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
     * Battery voltage in volts
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

    public Integer getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(Integer dataFormat) {
        this.dataFormat = dataFormat;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(Double accelerationX) {
        this.accelerationX = accelerationX;
    }

    public Double getAccelerationY() {
        return accelerationY;
    }

    public void setAccelerationY(Double accelerationY) {
        this.accelerationY = accelerationY;
    }

    public Double getAccelerationZ() {
        return accelerationZ;
    }

    public void setAccelerationZ(Double accelerationZ) {
        this.accelerationZ = accelerationZ;
    }

    public Double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    public Integer getMovementCounter() {
        return movementCounter;
    }

    public void setMovementCounter(Integer movementCounter) {
        this.movementCounter = movementCounter;
    }

    public Integer getMeasurementSequenceNumber() {
        return measurementSequenceNumber;
    }

    public void setMeasurementSequenceNumber(Integer measurementSequenceNumber) {
        this.measurementSequenceNumber = measurementSequenceNumber;
    }
}
