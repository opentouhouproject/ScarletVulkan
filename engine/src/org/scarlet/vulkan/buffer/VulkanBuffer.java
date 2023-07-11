package org.scarlet.vulkan.buffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkMemoryAllocateInfo;
import org.lwjgl.vulkan.VkMemoryRequirements;
import org.scarlet.vulkan.VulkanUtilities;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Byte buffer in Vulkan.
 */
public class VulkanBuffer {
    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * The requested buffer size.
     */
    private final long requestedSize;

    /**
     * The allocated buffer size.
     */
    private final long allocationSize;

    /**
     * The handle to the buffer.
     */
    private final long buffer;

    /**
     * The handle to the memory allocation.
     */
    private final long memory;

    /**
     * The pointer buffer.
     */
    private final PointerBuffer pointerBuffer;

    /**
     * The handle to the mapped memory.
     */
    private long mappedMemory;

    /**
     * Constructor.
     * @param logicalDevice The logical device.
     * @param size The requested buffer size.
     * @param usage
     */
    public VulkanBuffer(LogicalDevice logicalDevice, long size, int usage, int reqMask) {
        this.logicalDevice = logicalDevice;
        requestedSize = size;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                    .size(size)
                    .usage(usage)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            LongBuffer longBuffer = stack.mallocLong(1);
            vkCheck(vkCreateBuffer(logicalDevice.getDevice(), bufferCreateInfo, null, longBuffer),
                    "Failed to create buffer.");
            buffer = longBuffer.get(0);

            VkMemoryRequirements memoryRequirements = VkMemoryRequirements.malloc(stack);
            vkGetBufferMemoryRequirements(logicalDevice.getDevice(), buffer, memoryRequirements);

            VkMemoryAllocateInfo memoryAllocateInfo = VkMemoryAllocateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                    .allocationSize(memoryRequirements.size())
                    .memoryTypeIndex(VulkanUtilities.memoryTypeFromProperties(
                            logicalDevice.getPhysicalDevice(),
                            memoryRequirements.memoryTypeBits(),
                            reqMask));

            vkCheck(vkAllocateMemory(logicalDevice.getDevice(), memoryAllocateInfo, null, longBuffer),
                    "Failed to allocate memory.");
            allocationSize = memoryAllocateInfo.allocationSize();
            memory = longBuffer.get(0);
            pointerBuffer = MemoryUtil.memAllocPointer(1);

            vkCheck(vkBindBufferMemory(logicalDevice.getDevice(), buffer, memory, 0),
                    "Failed to bind the buffer memory.");
        }
    }

    /**
     * Release the resources.
     */
    public void cleanup() {
        MemoryUtil.memFree(pointerBuffer);
        vkDestroyBuffer(logicalDevice.getDevice(), buffer, null);
        vkFreeMemory(logicalDevice.getDevice(), memory, null);
    }

    /**
     * Map a memory object into the application address space.
     * @return long - The handle to the mapped memory.
     */
    public long map() {
        if (mappedMemory == NULL) {
            vkCheck(vkMapMemory(logicalDevice.getDevice(), memory, 0, allocationSize, 0, pointerBuffer),
                    "Failed to map the buffer.");
            mappedMemory = pointerBuffer.get(0);
        }
        return mappedMemory;
    }

    /**
     * Unmap the mapped memory.
     */
    public void unMap() {
        if (mappedMemory != NULL) {
            vkUnmapMemory(logicalDevice.getDevice(), memory);
            mappedMemory = NULL;
        }
    }

    /**
     * Get the handle to the buffer.
     * @return long - The buffer handle.
     */
    public long getBuffer() {
        return buffer;
    }

    /**
     * Get the requested buffer size.
     * @return long - The requested buffer size.
     */
    public long getRequestedSize() {
        return requestedSize;
    }
}