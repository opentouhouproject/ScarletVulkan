package org.scarlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK11.VK_API_VERSION_1_1;

/**
 * Properties for the engine.
 */
public class EngineProperties {
    /**
     * Engine properties file name.
     */
    private static final String FILENAME = "engine.properties";

    /**
     * Default engine name.
     */
    private static final String DEFAULT_NAME = "Scarlet Vulkan";

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
    private static final int DEFAULT_MINOR_VERSION_NUMBER = 2;

    /**
     * Default patch version number.
     */
    private static final int DEFAULT_PATCH_VERSION_NUMBER = 1;

    /**
     * Default setting for enabling the validation layers.
     */
    private static final boolean DEFAULT_VALIDATION_ENABLED = true;

    /**
     * Default target Updates Per Second (UPS)/frame rate.
     */
    private static final int DEFAULT_UPS = 30;

    /**
     * Default setting for enabling V-Sync.
     */
    private static final boolean DEFAULT_VSYNC_ENABLED = true;

    /**
     * Default preferred number of images.
     */
    private static final int DEFAULT_IMAGE_COUNT = 3;

    /**
     * Default device name.
     */
    private static final String DEFAULT_DEVICE_NAME = "";

    /**
     * Default shader recompilation setting.
     */
    private static final boolean DEFAULT_SHADER_RECOMPILATION = true;

    /**
     * Single instance of the engine properties class.
     */
    private static EngineProperties instance;

    /**
     * The engine name.
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
     * The Vulkan API version.
     */
    private int vulkanAPIVersion;

    /**
     * Flag to enable validation layers.
     */
    private boolean validationEnabled;

    /**
     * Current target Updates Per Second (UPS)/frame rate.
     */
    private int updatesPerSecond;

    /**
     * Flag to enable V-Sync.
     */
    private boolean vsyncEnabled;

    /**
     * The preferred number of images for the swap chain.
     */
    private int imageCount;

    /**
     * The preferred device name.
     */
    private String deviceName;

    /**
     * The shader recompilation setting.
     */
    private boolean shaderRecompilation;

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
        } catch (IOException | NullPointerException ex) {
            EngineLogger.getInstance().log(Level.WARNING, "Could not read [%s] properties file.", FILENAME, ex);
        }

        // Parse the properties into the EngineProperties object.
        name = properties.getOrDefault("name", DEFAULT_NAME).toString();
        variantNumber = Integer.parseInt(properties.getOrDefault("variantNumber", DEFAULT_VARIANT_NUMBER).toString());
        majorVersionNumber = Integer.parseInt(properties.getOrDefault("majorVersionNumber", DEFAULT_MAJOR_VERSION_NUMBER).toString());
        minorVersionNumber = Integer.parseInt(properties.getOrDefault("minorVersionNumber", DEFAULT_MINOR_VERSION_NUMBER).toString());
        patchVersionNumber = Integer.parseInt(properties.getOrDefault("patchVersionNumber", DEFAULT_PATCH_VERSION_NUMBER).toString());
        try {
            vulkanAPIVersion = Integer.parseInt(properties.getOrDefault("vulkanAPIVersion", VK_API_VERSION_1_1).toString());
        } catch (NumberFormatException ex) {
            vulkanAPIVersion = VK_API_VERSION_1_1;
        }
        validationEnabled = Boolean.parseBoolean(properties.getOrDefault("validationEnabled", DEFAULT_VALIDATION_ENABLED).toString());
        updatesPerSecond = Integer.parseInt(properties.getOrDefault("updatesPerSecond", DEFAULT_UPS).toString());
        vsyncEnabled = Boolean.parseBoolean(properties.getOrDefault("vsyncEnabled", DEFAULT_VSYNC_ENABLED).toString());
        imageCount = Integer.parseInt(properties.getOrDefault("imageCount", DEFAULT_IMAGE_COUNT).toString());
        deviceName = properties.getOrDefault("deviceName", DEFAULT_DEVICE_NAME).toString();
        shaderRecompilation = Boolean.parseBoolean(properties.getOrDefault("shaderRecompilation", DEFAULT_SHADER_RECOMPILATION).toString());
    }

    /**
     * Getter for the name field.
     * @return String - The engine name.
     */
    public String getEngineName() {
        return name;
    }

    /**
     * Getter for the variant number field.
     * @return int - The variant number.
     */
    public int getVariant() {
       return variantNumber;
    }

    /**
     * Getter for the major version number field.
     * @return int - The major version number.
     */
    public int getMajorVersion() {
        return majorVersionNumber;
    }

    /**
     * Getter for the minor version number field.
     * @return int - The minor version number.
     */
    public int getMinorVersion() {
        return minorVersionNumber;
    }

    /**
     * Getter for the patch version number field.
     * @return int - The patch version number.
     */
    public int getPatchVersion() {
        return patchVersionNumber;
    }

    /**
     * Getter for the Vulkan API version field.
     * @return int - The Vulkan API version.
     */
    public int getVulkanAPIVersion() {
        return vulkanAPIVersion;
    }

    /**
     * Getter for the validation enabled flag.
     * @return boolean - The flag indicating if validation is enabled.
     */
    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    /**
     * Getter for the updatesPerSecond field.
     * @return int - The target updates per second/frame rate.
     */
    public int getUpdatesPerSecond() {
        return updatesPerSecond;
    }

    /**
     * Getter for the V-Sync enabled flag.
     * @return boolean - The flag indicating if V-Sync is enabled.
     */
    public boolean isVSyncEnabled() {
        return vsyncEnabled;
    }

    /**
     * Getter for the preferred number of swap chain images.
     * @return int - The number of preferred swap chain images.
     */
    public int getImageCount() {
        return imageCount;
    }

    /**
     * Getter for the device name field.
     * @return String - The name of the preferred device.
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Getter for the shader recompilation setting.
     * @return boolean - The flag indicating if shader recompilation is enabled.
     */
    public boolean isShaderRecompilation() {
        return shaderRecompilation;
    }
}