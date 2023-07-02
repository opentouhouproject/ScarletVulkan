package org.scarlet.vulkan.queue;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.device.PhysicalDevice;
import org.scarlet.vulkan.surface.Surface;

import java.nio.IntBuffer;

import static org.lwjgl.vulkan.VK10.VK_TRUE;

/**
 * En-queue the swap chain images that are ready to be presented.
 */
public class PresentQueue extends Queue {
    /**
     * Get the queue family index.
     * @param logicalDevice The logical device.
     * @param surface The surface.
     * @return int - The queue family index.
     */
    private static int getPresentQueueFamilyIndex(LogicalDevice logicalDevice, Surface surface) {
        int index = -1;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PhysicalDevice physicalDevice = logicalDevice.getPhysicalDevice();
            VkQueueFamilyProperties.Buffer queueFamilyPropertiesBuffer = physicalDevice.getQueueFamilyProperties();
            int numberOfQueueFamilies = queueFamilyPropertiesBuffer.capacity();
            IntBuffer intBuffer = stack.mallocInt(1);
            for (int i = 0; i < numberOfQueueFamilies; i++) {
                KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(
                        physicalDevice.getDevice(), i, surface.getSurface(), intBuffer);
                if (intBuffer.get(0) == VK_TRUE) {
                    index = i;
                    break;
                }
            }
        }
        if (index < 0) {
            throw new RuntimeException("Failed to get Presentation Queue family index.");
        }
        return index;
    }

    /**
     * Constructor.
     * @param logicalDevice The logical device.
     */
    public PresentQueue(LogicalDevice logicalDevice, Surface surface, int queueIndex) {
        super(logicalDevice, getPresentQueueFamilyIndex(logicalDevice, surface), queueIndex);
    }
}