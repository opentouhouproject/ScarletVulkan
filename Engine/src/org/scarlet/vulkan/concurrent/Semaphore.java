package org.scarlet.vulkan.concurrent;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Used to control access by multiple threads.
 */
public class Semaphore {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * The handle to the semaphore.
     */
    private final long semaphore;

    /**
     * Constructor.
     * @param logicalDevice The logical device.
     */
    public Semaphore(LogicalDevice logicalDevice) {
        this.logicalDevice = logicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
            LongBuffer longBuffer = stack.mallocLong(1);
            vkCheck(vkCreateSemaphore(logicalDevice.getDevice(), semaphoreCreateInfo, null, longBuffer),
                    "Failed to create semaphore.");
            semaphore = longBuffer.get(0);
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        vkDestroySemaphore(logicalDevice.getDevice(), semaphore, null);
    }

    /**
     * Get the semaphore.
     * @return long - The handle to the semaphore.
     */
    public long getSemaphore() {
        return semaphore;
    }
}