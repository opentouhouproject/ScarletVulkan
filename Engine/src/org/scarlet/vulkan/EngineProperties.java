package org.scarlet.vulkan;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Properties for the engine.
 */
public class EngineProperties {
    /**
     * Engine properties file name.
     */
    private static final String FILENAME = "eng.properties";

    /**
     * Default target Updates Per Second (UPS)/frame rate.
     */
    private static final int DEFAULT_UPS = 30;

    /**
     * Single instance of the engine properties class.
     */
    private static EngineProperties instance;

    /**
     * Current target Updates Per Second (UPS)/frame rate.
     */
    private int updatesPerSecond;

    /**
     * Retrieves the instance of the singleton class.
     * @return EngineProperties - Single instance of the class.
     */
    public static synchronized EngineProperties getInstance() {
        if (instance == null) {
            instance = new EngineProperties();
        }
        return instance;
    }

    /**
     * Private constructor.
     */
    private EngineProperties() {
        // Create the properties object.
        Properties properties = new Properties();

        // Try to load the contents of the resource.
        try (InputStream stream = EngineProperties.class.getResourceAsStream("/" + FILENAME)) {
            // Load the contents of the resource into the properties object.
            properties.load(stream);

            // Parse the properties into the EngineProperties object.
            updatesPerSecond = Integer.parseInt(properties.getOrDefault("updatesPerSecond", DEFAULT_UPS).toString());
        } catch (IOException | NullPointerException ex) {
            EngineLogger.getInstance().log(Level.SEVERE, "Could not read [%s] properties file.", FILENAME, ex);
        }
    }

    /**
     * Getter for the updatesPerSecond field.
     * @return int - The target updates per second/frame rate.
     */
    public int getUpdatesPerSecond() {
        return updatesPerSecond;
    }
}