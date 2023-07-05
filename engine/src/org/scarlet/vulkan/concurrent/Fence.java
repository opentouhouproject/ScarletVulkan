package org.scarlet.vulkan.concurrent;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Mechanism used to synchronize operations between the GPU and CPU.
 */
public class Fence {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * Handle to the fence.
     */
    private final long fence;

    /**
     * Constructor.
     * @param logicalDevice The logical device.
     * @param signaled Flag indicating if fence is in the signaled state.
     */
    public Fence(LogicalDevice logicalDevice, boolean signaled) {
        this.logicalDevice = logicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkFenceCreateInfo fenceCreateInfo = VkFenceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                    .flags(signaled ? VK_FENCE_CREATE_SIGNALED_BIT : 0);

            LongBuffer longBuffer = stack.mallocLong(1);
            vkCheck(vkCreateFence(logicalDevice.getDevice(), fenceCreateInfo, null, longBuffer),
                    "Failed to create fence.");
            fence = longBuffer.get(0);
        }
    }

    /**
     * Release resources.
     */
    public void cleanup() {
        vkDestroyFence(logicalDevice.getDevice(), fence, null);
    }

    /**
     * Wait for the fence.
     */
    public void fenceWait() {
        vkWaitForFences(logicalDevice.getDevice(), fence, true, Long.MAX_VALUE);
    }

    /**
     * Get the fence.
     * @return long - The handle to the fence.
     */
    public long getFence() {
        return fence;
    }

    /**
     * Reset the fence.
     */
    public void reset() {
        vkResetFences(logicalDevice.getDevice(), fence);
    }
}