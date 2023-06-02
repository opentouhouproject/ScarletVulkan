package org.scarlet;

/**
 * Interface for defining application properties.
 */
public interface ApplicationProperties {
    /**
     * Get the application name.
     * @return String - The application name.
     */
    String getApplicationName();

    /**
     * Get the variant number.
     * @return int - The variant number.
     */
    int getVariant();

    /**
     * Get the major version number.
     * @return int - The major version number.
     */
    int getMajorVersion();

    /**
     * Get the minor version number.
     * @return int - The minor version number.
     */
    int getMinorVersion();

    /**
     * Get the patch version number.
     * @return int - The patch version number.
     */
    int getPatchVersion();

    /**
     * Get the window title.
     * @return String - The window title.
     */
    String getWindowTitle();
}