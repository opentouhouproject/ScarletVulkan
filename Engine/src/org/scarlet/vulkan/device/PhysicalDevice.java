package org.scarlet.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.scarlet.EngineLogger;

import java.nio.IntBuffer;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Class representing a physical device that implements the Vulkan interface.
 */
public class PhysicalDevice {
    /**
     * Handle to the physical device.
     */
    private final VkPhysicalDevice device;

    /**
     * The physical device properties.
     * Ex. device name, vendor, limits, etc.
     */
    private final VkPhysicalDeviceProperties deviceProperties;

    /**
     * A buffer containing a list of supported extensions (name and version).
     */
    private final VkExtensionProperties.Buffer deviceExtensions;

    /**
     * A buffer containing the queue families supported by the device.
     */
    private final VkQueueFamilyProperties.Buffer queueFamilyProperties;

    /**
     * Fine grain features supported by the device.
     * Ex. depth clamping, specific shader types, etc.
     */
    private final VkPhysicalDeviceFeatures deviceFeatures;

    /**
     * Information related to the different memory heaps supported by the device.
     */
    private final VkPhysicalDeviceMemoryProperties deviceMemoryProperties;

    /**
     * Package-private constructor.
     * @param physicalDevice The handle to the physical device.
     */
    PhysicalDevice(VkPhysicalDevice physicalDevice) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            device = physicalDevice;
            IntBuffer intBuffer = stack.mallocInt(1);

            // Get the device properties.
            deviceProperties = VkPhysicalDeviceProperties.calloc();
            vkGetPhysicalDeviceProperties(device, deviceProperties);

            // Get the device extensions.
            vkCheck(vkEnumerateDeviceExtensionProperties(device, (String) null, intBuffer, null),
                    "Failed to get the number of device extension properties.");
            deviceExtensions = VkExtensionProperties.calloc(intBuffer.get(0));
            vkCheck(vkEnumerateDeviceExtensionProperties(device, (String) null, intBuffer, deviceExtensions),
                    "Failed to get extension properties.");

            // Get queue family properties.
            vkGetPhysicalDeviceQueueFamilyProperties(device, intBuffer, null);
            queueFamilyProperties = VkQueueFamilyProperties.calloc(intBuffer.get(0));
            vkGetPhysicalDeviceQueueFamilyProperties(device, intBuffer, queueFamilyProperties);

            // Get the device features.
            deviceFeatures = VkPhysicalDeviceFeatures.calloc();
            vkGetPhysicalDeviceFeatures(device, deviceFeatures);

            // Get the memory information and properties.
            deviceMemoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
            vkGetPhysicalDeviceMemoryProperties(device, deviceMemoryProperties);
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        EngineLogger.getInstance().log(Level.INFO, "Destroying physical device [%s].", deviceProperties.deviceNameString());
        deviceMemoryProperties.free();
        deviceFeatures.free();
        queueFamilyProperties.free();
        deviceExtensions.free();
        deviceProperties.free();
    }

    /**
     * Retrieve the physical device name.
     * @return String - The name of the physical device.
     */
    public String getDeviceName() {
        return deviceProperties.deviceNameString();
    }

    /**
     * Retrieve the handle to the physical device.
     * @return VkPhysicalDevice - The handle to the physical device.
     */
    public VkPhysicalDevice getDevice() {
        return device;
    }

    /**
     * Retrieve the device properties.
     * @return VkPhysicalDeviceProperties - The physical device properties.
     */
    public VkPhysicalDeviceProperties getDeviceProperties() {
        return deviceProperties;
    }

    /**
     * Retrieve the queue family properties.
     * @return VkQueueFamilyProperties.Buffer - A buffer containing the queue family properties.
     */
    public VkQueueFamilyProperties.Buffer getQueueFamilyProperties() {
        return queueFamilyProperties;
    }

    /**
     * Retrieve the device features.
     * @return VkPhysicalDeviceFeatures - The physical device features.
     */
    public VkPhysicalDeviceFeatures getDeviceFeatures() {
        return deviceFeatures;
    }

    /**
     * Retrieve the device memory properties.
     * @return VkPhysicalDeviceMemoryProperties - The physical device memory properties.
     */
    public VkPhysicalDeviceMemoryProperties getDeviceMemoryProperties() {
        return deviceMemoryProperties;
    }

    /**
     * Checks if a supported queue family supports graphics commands.
     * @return boolean - True if a queue family supports graphics commands, false otherwise.
     */
    public boolean hasGraphicsQueueFamily() {
        // Get the number of queue families.
        int numberOfQueueFamilies = 0;
        if (queueFamilyProperties != null) {
            numberOfQueueFamilies = queueFamilyProperties.capacity();
        }

        // Check each queue family.
        for (int i = 0; i < numberOfQueueFamilies; i++) {
            VkQueueFamilyProperties properties = queueFamilyProperties.get(i);
            if ((properties.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the physical device supports the KHR swapchain extension.
     * @return boolean - True if KHR swapchain extension is supported, false otherwise.
     */
    public boolean hasKHRSwapChainExtension() {
        // Get the number of extensions.
        int numberOfExtensions = 0;
        if (deviceExtensions != null) {
            numberOfExtensions = deviceExtensions.capacity();
        }

        // Check each extension.
        for (int i = 0; i < numberOfExtensions; i++) {
            String extensionName = deviceExtensions.get(i).extensionNameString();
            if (KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME.equals(extensionName)) {
                return true;
            }
        }

        return false;
    }
}