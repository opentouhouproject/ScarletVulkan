package org.scarlet.main;

import org.scarlet.ApplicationProperties;
import org.scarlet.EngineLogger;
import org.scarlet.EngineProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Implements the application properties for the demo application.
 */
public class DemoProperties implements ApplicationProperties {
    /**
     * Demo properties file name.
     */
    private static final String FILENAME = "demo.properties";

    /**
     * Default name.
     */
    private static final String DEFAULT_NAME = "Scarlet Vulkan Demo";

    /**
     * Default variant number.
     */
    private static final int DEFAULT_VARIANT_NUMBER = 0;

    /**
     * Default major version number.
     */
    private static final int DEFAULT_MAJOR_VERSION_NUMBER = 0;

    /**
     * Default minor version number.
     */
    private static final int DEFAULT_MINOR_VERSION_NUMBER = 3;

    /**
     * Default patch version number.
     */
    private static final int DEFAULT_PATCH_VERSION_NUMBER = 0;

    /**
     * Default window title.
     */
    private static final String DEFAULT_WINDOW_TITLE = "Scarlet Vulkan Demo";

    /**
     * Single instance of the demo properties class.
     */
    private static DemoProperties instance;

    /**
     * The application name.
     */
    private String name;

    /**
     * The variant number.
     */
    private int variantNumber;

    /**
     * The major version number.
     */
    private int majorVersionNumber;

    /**
     * The minor version number.
     */
    private int minorVersionNumber;

    /**
     * The patch version number.
     */
    private int patchVersionNumber;

    /**
     * The window title.
     */
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
        // Create the properties object.
        Properties properties = new Properties();

        // Try to load the contents of the resource.
        try (InputStream stream = EngineProperties.class.getResourceAsStream("/" + FILENAME)) {
            // Load the contents of the resource into the properties object.
            properties.load(stream);
        } catch (IOException | NullPointerException ex) {
            EngineLogger.getInstance().log(Level.WARNING, "Could not read [%s] properties file.", FILENAME, ex);
        }

        // Parse the properties into the EngineProperties object.
        name = properties.getOrDefault("name", DEFAULT_NAME).toString();
        variantNumber = Integer.parseInt(properties.getOrDefault("variantNumber", DEFAULT_VARIANT_NUMBER).toString());
        majorVersionNumber = Integer.parseInt(properties.getOrDefault("majorVersionNumber", DEFAULT_MAJOR_VERSION_NUMBER).toString());
        minorVersionNumber = Integer.parseInt(properties.getOrDefault("minorVersionNumber", DEFAULT_MINOR_VERSION_NUMBER).toString());
        patchVersionNumber = Integer.parseInt(properties.getOrDefault("patchVersionNumber", DEFAULT_PATCH_VERSION_NUMBER).toString());
        windowTitle = properties.getOrDefault("windowTitle", DEFAULT_WINDOW_TITLE).toString();
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