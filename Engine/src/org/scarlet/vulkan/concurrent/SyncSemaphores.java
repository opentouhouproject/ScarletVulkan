package org.scarlet.vulkan.concurrent;

import org.scarlet.vulkan.device.LogicalDevice;

/**
 * Semaphores for swap chain images.
 */
public class SyncSemaphores {
    /**
     * The image acquisition semaphore.
     */
    private final Semaphore imageAcquisitionSemaphore;

    /**
     * The render completion semaphore.
     */
    private final Semaphore renderCompleteSemaphore;

    /**
     * Constructor.
     * @param logicalDevice The logical device.
     */
    public SyncSemaphores(LogicalDevice logicalDevice) {
        imageAcquisitionSemaphore = new Semaphore(logicalDevice);
        renderCompleteSemaphore = new Semaphore(logicalDevice);
    }

    /**
     * Release resources.
     */
    public void cleanup() {
       imageAcquisitionSemaphore.cleanup();
       renderCompleteSemaphore.cleanup();
    }

    /**
     * Get the image acquisition semaphore.
     * @return Semaphore - The semaphore.
     */
    public Semaphore getImageAcquisitionSemaphore() {
        return imageAcquisitionSemaphore;
    }

    /**
     * Get the render completion semaphore.
     * @return Semaphore - The semaphore.
     */
    public Semaphore getRenderCompleteSemaphore() {
        return renderCompleteSemaphore;
    }
}