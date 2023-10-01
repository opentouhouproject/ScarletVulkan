package org.scarlet.vulkan.image;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_SRGB;

/**
 * Stores information about an image.
 */
public class ImageData {
    /**
     * Number of layers.
     */
    private int arrayLayers;

    /**
     * Image format.
     */
    private int format;

    /**
     * The height.
     */
    private int height;

    /**
     * The number of MIP levels.
     */
    private int mipLevels;

    /**
     * The sample count.
     */
    private int sampleCount;

    /**
     * The usage.
     */
    private int usage;

    /**
     * The width.
     */
    private int width;

    /**
     * Constructor.
     */
    public ImageData() {
        format = VK_FORMAT_R8G8B8A8_SRGB;
        mipLevels = 1;
        sampleCount = 1;
        arrayLayers = 1;
    }

    /**
     * Get the number of array layers.
     * @return int - The number of array layers.
     */
    public int getArrayLayers() {
        return arrayLayers;
    }

    /**
     * Set the number of array layers.
     * @param arrayLayers - The number of array layers.
     * @return ImageData - The image data.
     */
    public ImageData setArrayLayers(int arrayLayers) {
        this.arrayLayers = arrayLayers;
        return this;
    }

    /**
     * Get the format.
     * @return int - The format.
     */
    public int getFormat() {
        return format;
    }

    /**
     * Set the format.
     * @param format - The format.
     * @return ImageData - The image data.
     */
    public ImageData setFormat(int format) {
        this.format = format;
        return this;
    }

    /**
     * Get the height.
     * @return int - The height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the height.
     * @param height - The height.
     * @return ImageData - The image data.
     */
    public ImageData getHeight(int height) {
        this.height = height;
        return this;
    }

    /**
     * Set the number of MIP levels.
     * @return int - The number of MIP levels.
     */
    public int getMIPLevels() {
        return mipLevels;
    }

    /**
     * Get the number of MIP levels.
     * @param mipLevels - The number of MIP levels.
     * @return ImageData - The image data.
     */
    public ImageData setMIPLevels(int mipLevels) {
        this.mipLevels = mipLevels;
        return this;
    }

    /**
     * Get the sample count.
     * @return int - The sample count.
     */
    public int getSampleCount() {
        return sampleCount;
    }

    /**
     * Set the sample count.
     * @param sampleCount - The sample count.
     * @return ImageData - The image data.
     */
    public ImageData setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
        return this;
    }

    /**
     * Get the usage.
     * @return int - The usage.
     */
    public int getUsage() {
        return usage;
    }

    /**
     * Set the usage.
     * @param usage - The usage.
     * @return ImageData - The image data.
     */
    public ImageData setUsage(int usage) {
        this.usage = usage;
        return this;
    }

    /**
     * Get the width.
     * @return int - The width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the width.
     * @param width - The width.
     * @return ImageData - The image data.
     */
    public ImageData setWidth(int width) {
        this.width = width;
        return this;
    }
}