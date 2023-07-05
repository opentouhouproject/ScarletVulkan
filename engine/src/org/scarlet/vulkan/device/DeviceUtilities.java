package org.scarlet.vulkan.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.scarlet.EngineLogger;
import org.scarlet.vulkan.Instance;

import java.nio.IntBuffer;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Utility class for devices.
 */
public final class DeviceUtilities {
    /**
     * Private constructor.
     */
    private DeviceUtilities() {
        // Do nothing.
    }

    /**
     * Gets a pointer to a list of physical device handles.
     * @param instance The Vulkan instance.
     * @param stack A memory stack.
     * @return PointerBuffer - A buffer containing the physical device handles.
     */
    public static PointerBuffer getPhysicalDevices(Instance instance, MemoryStack stack) {
        PointerBuffer physicalDevices;

        // Get the number of physical devices.
        IntBuffer intBuffer = stack.mallocInt(1);
        vkCheck(vkEnumeratePhysicalDevices(instance.getVulkanInstance(), intBuffer, null),
                "Failed to get the number of physical devices.");
        int numberOfDevices = intBuffer.get(0);
        EngineLogger.getInstance().log(Level.INFO, "Detected [%d] physical device(s).", numberOfDevices);

        // Populate the physical device pointer list.
        physicalDevices = stack.mallocPointer(numberOfDevices);
        vkCheck(vkEnumeratePhysicalDevices(instance.getVulkanInstance(), intBuffer, physicalDevices),
                "Failed to get physical devices.");

        return physicalDevices;
    }
}