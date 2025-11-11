package fi.tkgwf.ruuvi.common.utils;

public class MeasurementValueCalculator {

    /**
     * Calculates the total acceleration strength
     *
     * @param accelerationX
     * @param accelerationY
     * @param accelerationZ
     * @return The total acceleration strength
     */
    public static Double totalAcceleration(Double accelerationX, Double accelerationY, Double accelerationZ) {
        if (accelerationX == null || accelerationY == null || accelerationZ == null) {
            return null;
        }
        return Math.sqrt(accelerationX * accelerationX + accelerationY * accelerationY + accelerationZ * accelerationZ);
    }

    /**
     * Calculates the angle between a vector component and the corresponding
     * axis
     *
     * @param vectorComponent Vector component
     * @param vectorLength Vector length
     * @return Angle between the components axis and the vector, in degrees
     */
    public static Double angleBetweenVectorComponentAndAxis(Double vectorComponent, Double vectorLength) {
        if (vectorComponent == null || vectorLength == null || vectorLength == 0) {
            return null;
        }
        return Math.toDegrees(Math.acos(vectorComponent / vectorLength));
    }

    /**
     * Calculates the absolute humidity
     *
     * @param temperature Temperature in Celsius
     * @param relativeHumidity Relative humidity % (range 0-100)
     * @return The absolute humidity in g/m^3
     */
    public static Double absoluteHumidity(Double temperature, Double relativeHumidity) {
        if (temperature == null || relativeHumidity == null) {
            return null;
        }
        return equilibriumVaporPressure(temperature) * relativeHumidity * 0.021674 / (273.15 + temperature);
    }

    /**
     * Calculates the dew point
     *
     * @param temperature Temperature in Celsius
     * @param relativeHumidity Relative humidity % (range 0-100)
     * @return The dew point in Celsius
     */
    public static Double dewPoint(Double temperature, Double relativeHumidity) {
        if (temperature == null || relativeHumidity == null || relativeHumidity == 0) {
            return null;
        }
        double v = Math.log(relativeHumidity / 100 * equilibriumVaporPressure(temperature) / 611.2);
        return -243.5 * v / (v - 17.67);
    }

    /**
     * Calculates the equilibrium vapor pressure of water
     *
     * @param temperature Temperature in Celsius
     * @return The vapor pressure in Pa
     */
    public static Double equilibriumVaporPressure(Double temperature) {
        if (temperature == null) {
            return null;
        }
        return 611.2 * Math.exp(17.67 * temperature / (243.5 + temperature));
    }

    /**
     * Calculates the air density
     *
     * @param temperature Temperature in Celsius
     * @param relativeHumidity Relative humidity % (range 0-100)
     * @param pressure Pressure in pa
     * @return The air density in kg/m^3
     */
    public static Double airDensity(Double temperature, Double relativeHumidity, Double pressure) {
        if (temperature == null || relativeHumidity == null || pressure == null) {
            return null;
        }
        return 1.2929 * 273.15 / (temperature + 273.15)
                * (pressure - 0.3783 * relativeHumidity / 100 * equilibriumVaporPressure(temperature)) / 101300;
    }

    /**
     * Calculate Air Quality Index from PM2.5 and CO2 measurements.
     * <p>
     * For algorithm details, see https://github.com/ruuvi/com.ruuvi.station.webui/blob/master/src/decoder/untils.js
     *
     * @param pm25 PM 2.5, in micrograms per cubic meter.
     * @param co2 CO2 concentration, in PPM.
     * @return Air Quality Index (0-100, higher = better) or null if not available
     */
    public static Double airQualityIndex(Double pm25, Integer co2) {
        if (pm25 == null || co2 == null) {
            return null;
        }

        double pm = Math.max(0, Math.min(60, pm25));
        double co2Val = Math.max(420, Math.min(2300, co2));

        double dx = pm * (100.0 / 60.0);
        double dy = (co2Val - 420) * (100.0 / 1880.0);

        double r = Math.sqrt(dx * dx + dy * dy);
        double aqi = 100 - r;

        return Math.max(0, Math.min(100, aqi));
    }

}