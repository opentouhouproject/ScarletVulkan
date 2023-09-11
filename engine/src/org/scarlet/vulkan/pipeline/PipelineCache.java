package org.scarlet.vulkan.pipeline;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineCacheCreateInfo;
import org.scarlet.EngineLogger;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Representation of the pipeline cache.
 */
public class PipelineCache {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * The handle to the pipeline cache.
     */
    private final long vkPipelineCache;

    /**
     * Constructor.
     * @param logicalDevice The logical device.
     */
    public PipelineCache(LogicalDevice logicalDevice) {
        EngineLogger.getInstance().log(Level.INFO, "Creating pipeline cache.");
        this.logicalDevice = logicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPipelineCacheCreateInfo createInfo = VkPipelineCacheCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_CACHE_CREATE_INFO);
            LongBuffer lp = stack.mallocLong(1);
            vkCheck(vkCreatePipelineCache(logicalDevice.getDevice(), VkPipelineCacheCreateInfo.create(), null, lp),
                    "Error creating pipeline cache.");
            vkPipelineCache = lp.get(0);
        }
    }

    /**
     * Destroy the pipeline cache.
     */
    public void cleanup() {
        EngineLogger.getInstance().log(Level.INFO, "Destroying pipeline cache.");
        vkDestroyPipelineCache(logicalDevice.getDevice(), vkPipelineCache, null);
    }

    /**
     * Retrieve the logical device.
     * @return LogicalDevice - The logical device.
     */
    public LogicalDevice getLogicalDevice() {
        return logicalDevice;
    }

    /**
     * Retrieve the pipeline cache.
     * @return long - The pipeline cache handle.
     */
    public long getVkPipelineCache() {
        return vkPipelineCache;
    }
}