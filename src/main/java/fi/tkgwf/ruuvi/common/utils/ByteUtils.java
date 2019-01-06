package fi.tkgwf.ruuvi.common.utils;

public abstract class ByteUtils {

    /**
     * Convenience method for checking whether the supplied byte is the max
     * signed byte.
     *
     * @param b byte to check
     * @return true if the byte represents the max value a signed byte can be
     */
    public static boolean isMaxSignedByte(byte b) {
        return (b & 0xFF) == 127;
    }

    /**
     * Convenience method for checking whether the supplied byte is the max
     * unsigned byte.
     *
     * @param b byte to check
     * @return true if the byte represents the max value an unsigned byte can be
     */
    public static boolean isMaxUnsignedByte(byte b) {
        return (b & 0xFF) == 255;
    }

    /**
     * Convenience method for checking whether the supplied bytes forming a
     * 16bit short is the max signed short.
     *
     * @param b1 1st byte to check
     * @param b2 2nd byte to check
     * @return true if the pair of bytes represent the max value a signed short
     * can be
     */
    public static boolean isMaxSignedShort(byte b1, byte b2) {
        return isMaxSignedByte(b1) && isMaxUnsignedByte(b2);
    }

    /**
     * Convenience method for checking whether the supplied bytes forming a
     * 16bit short is the max unsigned short.
     *
     * @param b1 1st byte to check
     * @param b2 2nd byte to check
     * @return true if the pair of bytes represent the max value an unsigned
     * short can be
     */
    public static boolean isMaxUnsignedShort(byte b1, byte b2) {
        return isMaxUnsignedByte(b1) && isMaxUnsignedByte(b2);
    }
}
