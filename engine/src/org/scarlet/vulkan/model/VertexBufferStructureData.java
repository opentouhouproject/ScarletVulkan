package org.scarlet.vulkan.model;

/**
 * Settings data for the vertex buffer structure.
 */
public class VertexBufferStructureData {
    /**
     * Byte for checking if position is enabled.
     */
    private static final byte POSITION_ENABLED = 0b00000001;

    /**
     * Byte for checking if texture coordinates are enabled.
     */
    private static final byte TEXTURE_COORDINATES_ENABLED = 0b00000010;

    /**
     * Number of attributes per vertex.
     */
    private int numberOfAttributes;

    /**
     * Enabled attributes.
     */
    private byte enabledSettings;

    /**
     * Default constructor.
     */
    public VertexBufferStructureData() {
        numberOfAttributes = 0;
        enabledSettings = 0b00000000;
        enablePosition();
        enableTextureCoordinates();
    }

    /**
     * Constructor.
     * @param settings - The settings.
     */
    public VertexBufferStructureData(byte settings) {
        numberOfAttributes = Integer.bitCount(settings);
        enabledSettings = settings;
    }

    /**
     * Enable the position attribute.
     */
    public void enablePosition() {
        if ((enabledSettings & POSITION_ENABLED) == 0) {
            numberOfAttributes++;
            enabledSettings = (byte) (enabledSettings | POSITION_ENABLED);
        }
    }

    /**
     * Enable the texture coordinate attribute.
     */
    public void enableTextureCoordinates() {
        if ((enabledSettings & TEXTURE_COORDINATES_ENABLED) == 0) {
            numberOfAttributes++;
            enabledSettings = (byte) (enabledSettings | TEXTURE_COORDINATES_ENABLED);
        }
    }

    /**
     * Check if the position attribute is enabled.
     * @return boolean - True if the position attribute is enabled, false otherwise.
     */
    public boolean isPositionEnabled() {
        return (enabledSettings & POSITION_ENABLED) == 1;
    }

    /**
     * Check if the texture coordinates attribute is enabled.
     * @return boolean - True if the texture coordinates attribute is enabled, false otherwise.
     */
    public boolean isTextureCoordinatesEnabled() {
        return (enabledSettings & TEXTURE_COORDINATES_ENABLED) == 1;
    }

    /**
     * Get the number of attributes.
     * @return int - The number of attributes.
     */
    public int getNumberOfAttributes() {
        return numberOfAttributes;
    }
}