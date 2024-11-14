package com.wisdomgarden.mobile;

public class AndroidVersionUtils {

    /**
     * Checks if the current SDK version is greater than the specified version.
     *
     * @param version the version to check against
     * @return true if the current SDK version is greater than the specified version, false otherwise
     */
    public static boolean isGreaterThan(int version) {
        return android.os.Build.VERSION.SDK_INT > version;
    }

    /**
     * Checks if the current SDK version is greater than or equal to the specified version.
     *
     * @param version the minimum SDK version
     * @return true if the current SDK version is greater than or equal to the specified version, false otherwise
     */
    public static boolean isGreaterThanOrEqualTo(int version) {
        return android.os.Build.VERSION.SDK_INT >= version;
    }


    /**
     * Checks if the current SDK version is between the specified range.
     *
     * @param minVersion the minimum SDK version
     * @param maxVersion the maximum SDK version
     * @return true if the current SDK version is within the specified range, false otherwise
     */
    public static boolean isBetween(int minVersion, int maxVersion) {
        return isBetween(minVersion, maxVersion, false, false);
    }

    /**
     * Checks if the current SDK version is between the specified range,
     * allowing customization of the minimum boundary.
     *
     * @param minVersion the minimum SDK version
     * @param maxVersion the maximum SDK version
     * @param includeMin whether to include the minimum version
     * @return true if the current SDK version is within the specified range, false otherwise
     */
    public static boolean isBetween(int minVersion, int maxVersion, boolean includeMin) {
        return isBetween(minVersion, maxVersion, includeMin, false); // 默认不包含最大值
    }

    /**
     * Checks if the current SDK version is between the specified range with customizable boundaries.
     *
     * @param minVersion the minimum SDK version
     * @param maxVersion the maximum SDK version
     * @param includeMin whether to include the minimum version
     * @param includeMax whether to include the maximum version
     * @return true if the current SDK version is within the specified range, false otherwise
     */
    public static boolean isBetween(int minVersion, int maxVersion, boolean includeMin, boolean includeMax) {
        boolean minCheck = includeMin ? isGreaterThanOrEqualTo(minVersion) : isGreaterThan(minVersion);
        boolean maxCheck = includeMax ? isLessThanOrEqualTo(maxVersion) : isLessThan(maxVersion);

        return minCheck && maxCheck;
    }

    /**
     * Checks if the current SDK version is below the specified version.
     *
     * @param version the version to check against
     * @return true if the current SDK version is below the specified version, false otherwise
     */
    public static boolean isLessThan(int version) {
        return android.os.Build.VERSION.SDK_INT < version;
    }

    /**
     * Checks if the current SDK version is less than or equal to the specified version.
     *
     * @param version the version to check against
     * @return true if the current SDK version is less than or equal to the specified version, false otherwise
     */
    public static boolean isLessThanOrEqualTo(int version) {
        return android.os.Build.VERSION.SDK_INT <= version;
    }

    /**
     * Checks if the current SDK version is exactly the specified version.
     *
     * @param version the version to check against
     * @return true if the current SDK version is exactly the specified version, false otherwise
     */
    public static boolean isExactly(int version) {
        return android.os.Build.VERSION.SDK_INT == version;
    }

    /**
     * Gets the current SDK version.
     *
     * @return the current SDK version
     */
    public static int getCurrentVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }
}
