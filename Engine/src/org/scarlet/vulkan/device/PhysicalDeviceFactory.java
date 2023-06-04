package org.scarlet.vulkan.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.scarlet.EngineLogger;
import org.scarlet.EngineProperties;
import org.scarlet.vulkan.Instance;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Class for creating an instance of a physical device.
 */
public final class PhysicalDeviceFactory {
    /**
     * Private constructor.
     */
    private PhysicalDeviceFactory() {
        // Do nothing.
    }

    /**
     * Create a representation of a physical device.
     * @param instance The Vulkan instance.
     * @param preferredDeviceName The name of the preferred physical device.
     * @return PhysicalDevice - Class representing a physical device.
     */
    public static PhysicalDevice create(Instance instance, String preferredDeviceName) {
        EngineLogger.getInstance().log(Level.INFO, "Selecting physical devices.");
        PhysicalDevice selectedPhysicalDevice = null;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Get the physical devices.
            PointerBuffer physicalDevices = getPhysicalDevices(instance, stack);

            // Check the number of physical devices.
            int numberOfDevices = 0;
            if (physicalDevices != null) {
                numberOfDevices = physicalDevices.capacity();
            }
            if (numberOfDevices <= 0) {
                throw new RuntimeException("No physical devices found.");
            }

            // Populate a list of physical devices that meets requirements.
            List<PhysicalDevice> devices = new ArrayList<>();
            for (int i = 0; i < numberOfDevices; i++) {
                VkPhysicalDevice vkPhysicalDevice = new VkPhysicalDevice(physicalDevices.get(i), instance.getVulkanInstance());
                PhysicalDevice physicalDevice = new PhysicalDevice(vkPhysicalDevice);

                String deviceName = physicalDevice.getDeviceName();
                if (physicalDevice.hasGraphicsQueueFamily() && physicalDevice.hasKHRSwapChainExtension()) {
                    EngineLogger.getInstance().log(Level.INFO, "Device [%s] supports required extensions.", deviceName);
                    if (preferredDeviceName != null && preferredDeviceName.equals(deviceName)) {
                        selectedPhysicalDevice = physicalDevice;
                        break;
                    }
                    devices.add(physicalDevice);
                }
                else {
                    EngineLogger.getInstance().log(Level.INFO, "Device [%s] does not support required extensions.", deviceName);
                    physicalDevice.cleanup();
                }
            }

            // Select the preferred device.
            if (selectedPhysicalDevice == null && !devices.isEmpty()) {
                selectedPhysicalDevice = devices.remove(0);
            }

            // Cleanup unused devices.
            for (PhysicalDevice physicalDevice : devices) {
                physicalDevice.cleanup();
            }

            // Check if we have a selected physical device.
            if (selectedPhysicalDevice == null) {
                throw new RuntimeException("No suitable physical devices found.");
            }

            EngineLogger.getInstance().log(Level.INFO, "Selected device: [%s].", selectedPhysicalDevice.getDeviceName());
        }

        return selectedPhysicalDevice;
    }

    /**
     * Gets a pointer to a list of physical device handles.
     * @param instance The Vulkan instance.
     * @param stack A memory stack.
     * @return PointerBuffer - A buffer containing the physical device handles.
     */
    private static PointerBuffer getPhysicalDevices(Instance instance, MemoryStack stack) {
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