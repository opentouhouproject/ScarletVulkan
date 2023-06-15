package org.scarlet.vulkan.queue;

import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.device.PhysicalDevice;

import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;

/**
 * Queue for submitting render tasks.
 */
public class GraphicsQueue extends Queue {
    private static int getGraphicsQueueFamilyIndex(LogicalDevice device) {
        int index = -1;
        PhysicalDevice physicalDevice = device.getPhysicalDevice();
        VkQueueFamilyProperties.Buffer queueFamilyPropertiesBuffer = physicalDevice.getQueueFamilyProperties();
        int numberOfQueueFamilies = queueFamilyPropertiesBuffer.capacity();
        for (int i = 0; i < numberOfQueueFamilies; i++) {
            VkQueueFamilyProperties properties = queueFamilyPropertiesBuffer.get(i);
            boolean graphicsQueue = (properties.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0;
            if (graphicsQueue) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            throw new RuntimeException("Failed to get graphics queue family index.");
        }
        return index;
    }

    /**
     * Constructor.
     * @param device The logical device.
     * @param queueIndex The index of the queue within the queue family.
     */
    public GraphicsQueue(LogicalDevice device, int queueIndex) {
        super(device, getGraphicsQueueFamilyIndex(device), queueIndex);
    }
}