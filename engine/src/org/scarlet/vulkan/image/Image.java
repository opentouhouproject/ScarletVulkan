package org.scarlet.vulkan.image;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.scarlet.vulkan.VulkanUtilities;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Represents an image.
 */
public class Image {
    /**
     * Logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * Image format.
     */
    private final int format;

    /**
     * MIP levels.
     */
    private final int mipLevels;

    /**
     * Handle to the image.
     */
    private final long image;

    /**
     * Handle to the image memory allocation.
     */
    private final long memory;

    /**
     * Constructor.
     * @param logicalDevice - The logical device.
     * @param imageData - The image data.
     */
    public Image(LogicalDevice logicalDevice, ImageData imageData) {
        this.logicalDevice = logicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.format = imageData.getFormat();
            this.mipLevels = imageData.getMIPLevels();

            VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                    .imageType(VK_IMAGE_TYPE_2D)
                    .format(format)
                    .extent(it -> it
                            .width(imageData.getWidth())
                            .height(imageData.getHeight())
                            .depth(1))
                    .mipLevels(mipLevels)
                    .arrayLayers(imageData.getArrayLayers())
                    .samples(imageData.getSampleCount())
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .tiling(VK_IMAGE_TILING_OPTIMAL)
                    .usage(imageData.getUsage());

            // Create the image.
            LongBuffer lp = stack.mallocLong(1);
            vkCheck(vkCreateImage(logicalDevice.getDevice(), imageCreateInfo, null, lp),
                    "Failed to create image.");
            image = lp.get(0);

            // Get the memory requirements for the image.
            VkMemoryRequirements memoryRequirements = VkMemoryRequirements.calloc(stack);
            vkGetImageMemoryRequirements(logicalDevice.getDevice(), image, memoryRequirements);

            // Select memory type and size.
            VkMemoryAllocateInfo memoryAllocateInfo = VkMemoryAllocateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                    .allocationSize(memoryRequirements.size())
                    .memoryTypeIndex(VulkanUtilities.memoryTypeFromProperties(
                            logicalDevice.getPhysicalDevice(), memoryRequirements.memoryTypeBits(), 0));

            // Allocate the memory.
            vkCheck(vkAllocateMemory(logicalDevice.getDevice(), memoryAllocateInfo, null, lp),
                    "Failed to allocate memory for image.");
            memory = lp.get(0);

            // Bind the memory.
            vkCheck(vkBindImageMemory(logicalDevice.getDevice(), image, memory, 0),
                    "Failed to bind image memory.");
        }
    }

    /**
     * Cleanup resources.
     */
    public void cleanup() {
        vkDestroyImage(logicalDevice.getDevice(), image, null);
        vkFreeMemory(logicalDevice.getDevice(), memory, null);
    }
}