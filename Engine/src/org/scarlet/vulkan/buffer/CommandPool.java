package org.scarlet.vulkan.buffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.scarlet.EngineLogger;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * A pool for instantiating command buffers.
 */
public class CommandPool {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * Handle to the command pool.
     */
    private final long commandPool;

    /**
     * Constructor.
     * @param logicalDevice The logical device.
     * @param queueFamilyIndex The queue family index.
     */
    public CommandPool(LogicalDevice logicalDevice, int queueFamilyIndex) {
        EngineLogger.getInstance().log(Level.INFO, "Creating Vulkan command pool.");

        this.logicalDevice = logicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo commandPoolInfo = VkCommandPoolCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                    .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
                    .queueFamilyIndex(queueFamilyIndex);

            LongBuffer longBuffer = stack.mallocLong(1);
            vkCheck(vkCreateCommandPool(logicalDevice.getDevice(), commandPoolInfo, null, longBuffer),
                    "Failed to create command pool.");
            commandPool = longBuffer.get(0);
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        vkDestroyCommandPool(logicalDevice.getDevice(), commandPool, null);
    }

    /**
     * Gets the logical device.
     * @return LogicalDevice - The logical device.
     */
    public LogicalDevice getDevice() {
        return logicalDevice;
    }

    /**
     * Gets the command pool.
     * @return long - The handle to the command pool.
     */
    public long getCommandPool() {
        return commandPool;
    }
}