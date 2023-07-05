package org.scarlet.vulkan.surface;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Encapsulation of the creation and disposal of Vulkan image views.
 */
public class ImageView {
    /**
     * The logical device.
     */
    private final LogicalDevice device;

    /**
     * Handle to the image view.
     */
    private final long imageView;

    /**
     * Constructor.
     * @param logicalDevice - The logical device.
     * @param image - The vulkan image.
     * @param imageViewData - The image view data.
     */
    public ImageView(LogicalDevice logicalDevice, long image, ImageViewData imageViewData) {
        this.device = logicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer longBuffer = stack.mallocLong(1);
            VkImageViewCreateInfo imageViewCreateInfo = VkImageViewCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .image(image)
                    .viewType(imageViewData.viewType())
                    .format(imageViewData.format())
                    .subresourceRange(it -> it
                            .aspectMask(imageViewData.aspectMask())
                            .baseMipLevel(0)
                            .levelCount(imageViewData.mipLevels())
                            .baseArrayLayer(imageViewData.baseArrayLayer())
                            .layerCount(imageViewData.layerCount()));

            // Create the image view.
            vkCheck(vkCreateImageView(
                    device.getDevice(), imageViewCreateInfo, null, longBuffer),
                    "Failed to create image view.");
            imageView = longBuffer.get(0);
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        vkDestroyImageView(device.getDevice(), imageView, null);
    }

    /**
     * Retrieve the handle to the image view.
     * @return long - The image view.
     */
    public long getImageView() {
        return imageView;
    }
}