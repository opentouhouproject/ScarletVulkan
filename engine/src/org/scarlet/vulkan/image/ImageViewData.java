package org.scarlet.vulkan.image;

import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D;

/**
 * Class containing the data for an image view.
 */
public class ImageViewData {
    /**
     * The aspect mask.
     */
    private int aspectMask;

    /**
     * The base array layer.
     */
    private int baseArrayLayer;

    /**
     * The format.
     */
    private int format;

    /**
     * The layer count.
     */
    private int layerCount;

    /**
     * The mip levels.
     */
    private int mipLevels;

    /**
     * The view type.
     */
    private int viewType;

    /**
     * Constructor.
     */
    public ImageViewData() {
        baseArrayLayer = 0;
        layerCount = 1;
        mipLevels = 1;
        viewType = VK_IMAGE_VIEW_TYPE_2D;
    }

    /**
     * Aspect mask getter.
     * @return int - The aspect mask.
     */
    public int aspectMask() {
        return aspectMask;
    }

    /**
     * Aspect mask setter.
     * @param aspectMask - The aspect mask.
     * @return ImageViewData - Object instance.
     */
    public ImageViewData aspectMask(int aspectMask) {
        this.aspectMask = aspectMask;
        return this;
    }

    /**
     * Base array layer getter.
     * @return int - The base array layer.
     */
    public int baseArrayLayer() {
        return baseArrayLayer;
    }

    /**
     * Base array layer setter.
     * @param baseArrayLayer - The base array layer.
     * @return ImageViewData - Object instance.
     */
    public ImageViewData baseArrayLayer(int baseArrayLayer) {
        this.baseArrayLayer = baseArrayLayer;
        return this;
    }

    /**
     * Format getter.
     * @return int - The format.
     */
    public int format() {
        return format;
    }

    /**
     * Format setter.
     * @param format - The format.
     * @return ImageViewData - Object instance.
     */
    public ImageViewData format(int format) {
        this.format = format;
        return this;
    }

    /**
     * Layer count getter.
     * @return int - The layer count.
     */
    public int layerCount() {
        return layerCount;
    }

    /**
     * Layer count setter.
     * @param layerCount - The layer count.
     * @return ImageViewData - Object instance.
     */
    public ImageViewData layerCount(int layerCount) {
        this.layerCount = layerCount;
        return this;
    }

    /**
     * Mip levels getter.
     * @return int - The mip levels.
     */
    public int mipLevels() {
        return mipLevels;
    }

    /**
     * Mip levels setter.
     * @param mipLevels - The mip levels.
     * @return ImageViewData - Object instance.
     */
    public ImageViewData mipLevels(int mipLevels) {
        this.mipLevels = mipLevels;
        return this;
    }

    /**
     * View-type getter.
     * @return int - The view type.
     */
    public int viewType() {
        return viewType;
    }

    /**
     * View-type setter.
     * @param viewType - The view type.
     * @return ImageViewData - Object instance.
     */
    public ImageViewData viewType(int viewType) {
        this.viewType = viewType;
        return this;
    }
}