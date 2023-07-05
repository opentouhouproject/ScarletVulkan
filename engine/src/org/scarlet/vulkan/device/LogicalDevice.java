package org.scarlet.vulkan.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.scarlet.EngineLogger;

import java.nio.FloatBuffer;
import java.util.logging.Level;

import static org.lwjgl.vulkan.VK10.*;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Class representing a logical device.
 * A logical device represents the interface to the hardware (physical device).
 */
public class LogicalDevice {
    /**
     * Representation of the physical hardware device.
     */
    private final PhysicalDevice physicalDevice;

    /**
     * The Vulkan logical device handle.
     */
    private final VkDevice device;

    /**
     * Constructor.
     * @param physicalDevice The physical hardware device.
     */
    public LogicalDevice(PhysicalDevice physicalDevice) {
        EngineLogger.getInstance().log(Level.INFO, "Creating Logical Device.");
        this.physicalDevice = physicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Define the required extensions.
            PointerBuffer requiredExtensions = stack.mallocPointer(1);
            requiredExtensions.put(0, stack.ASCII(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME));

            // Set up the required features.
            VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.calloc(stack);

            // Enable the queue families.
            VkQueueFamilyProperties.Buffer queueFamilyProperties = physicalDevice.getQueueFamilyProperties();
            int numberOfQueueFamilies = queueFamilyProperties.capacity();
            VkDeviceQueueCreateInfo.Buffer queueCreationInformation = VkDeviceQueueCreateInfo.calloc(numberOfQueueFamilies, stack);
            for (int i = 0; i < numberOfQueueFamilies; i++) {
                FloatBuffer priorities = stack.callocFloat(queueFamilyProperties.get(i).queueCount());
                queueCreationInformation.get(i)
                        .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .queueFamilyIndex(i)
                        .pQueuePriorities(priorities);
            }

            // Create the logical device.
            VkDeviceCreateInfo deviceCreateInformation = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .ppEnabledExtensionNames(requiredExtensions)
                    .pEnabledFeatures(features)
                    .pQueueCreateInfos(queueCreationInformation);
            PointerBuffer pointerBuffer = stack.mallocPointer(1);
            vkCheck(vkCreateDevice(physicalDevice.getDevice(), deviceCreateInformation, null, pointerBuffer),
                    "Failed to create logical device.");
            device = new VkDevice(pointerBuffer.get(0), physicalDevice.getDevice(), deviceCreateInformation);
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        EngineLogger.getInstance().log(Level.INFO, "Destroying Vulkan logical device.");
        vkDestroyDevice(device, null);
    }

    /**
     * Retrieve the physical device associated with this logical device.
     * @return PhysicalDevice - The physical device.
     */
    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    /**
     * Retrieve the Vulkan logical device handle.
     * @return VkDevice - The logical device.
     */
    public VkDevice getDevice() {
        return device;
    }

    /**
     * Wait for the logical device to become idle.
     * The logical device becomes idle when all pending operations on any queue completes.
     */
    public void waitIdle() {
        vkDeviceWaitIdle(device);
    }
}