package org.scarlet.main;

import org.scarlet.ApplicationProperties;

/**
 * Implements the application properties for the demo application.
 */
public class DemoProperties implements ApplicationProperties {
    /**
     * Single instance of the demo properties class.
     */
    private static DemoProperties instance;

    private String name;
    private int variantNumber;
    private int majorVersionNumber;
    private int minorVersionNumber;
    private int patchVersionNumber;
    private String windowTitle;

    /**
     * Retrieves the instance of the singleton class.
     * @return DemoProperties - Single instance of the class.
     */
    public static synchronized DemoProperties getInstance() {
        if (instance == null) {
            instance = new DemoProperties();
        }
        return instance;
    }

    /**
     * Private constructor.
     */
    private DemoProperties() {
        name = "Scarlet Vulkan Demo";
        variantNumber = 0;
        majorVersionNumber = 0;
        minorVersionNumber = 0;
        patchVersionNumber = 3;
        windowTitle = "Scarlet Vulkan Demo";
    }

    @Override
    public String getApplicationName() {
        return name;
    }

    @Override
    public int getVariant() {
        return variantNumber;
    }

    @Override
    public int getMajorVersion() {
        return majorVersionNumber;
    }

    @Override
    public int getMinorVersion() {
        return minorVersionNumber;
    }

    @Override
    public int getPatchVersion() {
        return patchVersionNumber;
    }

    @Override
    public String getWindowTitle() {
        return windowTitle;
    }
}