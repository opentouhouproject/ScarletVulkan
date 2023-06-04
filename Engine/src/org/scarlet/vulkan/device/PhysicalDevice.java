package org.scarlet.vulkan.device;

import org.lwjgl.vulkan.VkPhysicalDevice;

/**
 * Class representing a physical device that implements the Vulkan interface.
 */
public class PhysicalDevice {
    private final VkPhysicalDevice device;

    public PhysicalDevice(VkPhysicalDevice physicalDevice) {
        device = physicalDevice;
    }

    public void cleanup() {
        // TO DO.
    }

    public String getDeviceName() {
        // TO DO.
        return null;
    }

    public boolean hasGraphicsQueueFamily() {
        // TO DO.
        return false;
    }

    public boolean hasKHRSwapChainExtension() {
        // TO DO.
        return false;
    }
}