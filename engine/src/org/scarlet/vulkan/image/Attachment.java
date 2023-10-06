package org.scarlet.vulkan.image;

import org.scarlet.vulkan.device.LogicalDevice;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Represents an attachment.
 */
public class Attachment {
    /**
     * The image.
     */
    private final Image image;

    /**
     * The image view.
     */
    private final ImageView imageView;

    /**
     * Boolean indicating if it is a depth attachment.
     */
    private boolean depthAttachment;

    /**
     * Constructor.
     * @param logicalDevice - The logical device.
     * @param width - The image width.
     * @param height - The image height.
     * @param format - The image format.
     * @param usage - The usage.
     */
    public Attachment(LogicalDevice logicalDevice, int width, int height, int format, int usage) {
        ImageData imageData = new ImageData()
                .setWidth(width)
                .setHeight(height)
                .setUsage(usage | VK_IMAGE_USAGE_SAMPLED_BIT)
                .setFormat(format);
        image = new Image(logicalDevice, imageData);

        int aspectMask = 0;
        if ((usage & VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT) > 0) {
            aspectMask = VK_IMAGE_ASPECT_COLOR_BIT;
            depthAttachment = false;
        }
        if ((usage & VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT) > 0) {
            aspectMask = VK_IMAGE_ASPECT_DEPTH_BIT;
            depthAttachment = true;
        }

        ImageViewData imageViewData = new ImageViewData()
                .format(image.getFormat())
                .aspectMask(aspectMask);
        imageView = new ImageView(logicalDevice, image.getImage(), imageViewData);
    }

    /**
     * Cleanup resources.
     */
    public void cleanup() {
        imageView.cleanup();
        image.cleanup();
    }
}