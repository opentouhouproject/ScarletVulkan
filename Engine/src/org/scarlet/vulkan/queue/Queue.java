package org.scarlet.vulkan.queue;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkQueue;
import org.scarlet.EngineLogger;
import org.scarlet.vulkan.device.LogicalDevice;

import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.vkGetDeviceQueue;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

/**
 * Representation of a Vulkan queue.
 */
public class Queue {
    /**
     * Wrapper class for a Vulkan queue.
     */
    private final VkQueue queue;

    /**
     * Constructor.
     * @param device The logical device.
     * @param queueFamilyIndex The index of the queue family that the queue belongs to.
     * @param queueIndex The index of the queue within the queue family.
     */
    public Queue(LogicalDevice device, int queueFamilyIndex, int queueIndex) {
        EngineLogger.getInstance().log(Level.INFO, "Creating queue.");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pQueue = stack.mallocPointer(1);
            vkGetDeviceQueue(device.getDevice(), queueFamilyIndex, queueIndex, pQueue);
            long queueHandle = pQueue.get(0);
            queue = new VkQueue(queueHandle, device.getDevice());
        }
    }

    /**
     * Retrieve the wrapper class for the Vulkan queue.
     * @return VkQueue The Vulkan queue.
     */
    public VkQueue getQueue() {
        return queue;
    }

    /**
     * Wait for the queue to become idle.
     */
    public void waitIdle() {
        vkQueueWaitIdle(queue);
    }
}