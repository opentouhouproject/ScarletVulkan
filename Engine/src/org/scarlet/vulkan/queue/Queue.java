package org.scarlet.vulkan.queue;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.scarlet.EngineLogger;
import org.scarlet.vulkan.concurrent.Fence;
import org.scarlet.vulkan.device.LogicalDevice;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Representation of a Vulkan queue.
 */
public class Queue {
    /**
     * The queue family index.
     */
    private final int queueFamilyIndex;

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
        this.queueFamilyIndex = queueFamilyIndex;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pQueue = stack.mallocPointer(1);
            vkGetDeviceQueue(device.getDevice(), queueFamilyIndex, queueIndex, pQueue);
            long queueHandle = pQueue.get(0);
            queue = new VkQueue(queueHandle, device.getDevice());
        }
    }

    /**
     * Submits a sequence of semaphores or command buffers to a queue.
     * @param commandBuffers - The command buffers.
     * @param waitSemaphores - The wait semaphores.
     * @param dstStageMasks - The pipeline masks.
     * @param signalSemaphores - The signal semaphores.
     * @param fence - The fence.
     */
    public void submit(PointerBuffer commandBuffers, LongBuffer waitSemaphores,
                       IntBuffer dstStageMasks, LongBuffer signalSemaphores, Fence fence) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                    .pCommandBuffers(commandBuffers)
                    .pSignalSemaphores(signalSemaphores);
            if (waitSemaphores != null) {
                submitInfo.waitSemaphoreCount(waitSemaphores.capacity())
                        .pWaitSemaphores(waitSemaphores)
                        .pWaitDstStageMask(dstStageMasks);
            }
            else {
                submitInfo.waitSemaphoreCount(0);
            }
            long fenceHandle = fence != null ? fence.getFence() : VK_NULL_HANDLE;
            vkCheck(vkQueueSubmit(queue, submitInfo, fenceHandle),
                    "Failed to submit command to queue.");
        }
    }

    /**
     * Wait for the queue to become idle.
     */
    public void waitIdle() {
        vkQueueWaitIdle(queue);
    }

    /**
     * Get the queue family index.
     * @return int - The queue family index.
     */
    public int getQueueFamilyIndex() {
        return queueFamilyIndex;
    }

    /**
     * Retrieve the wrapper class for the Vulkan queue.
     * @return VkQueue The Vulkan queue.
     */
    public VkQueue getQueue() {
        return queue;
    }
}