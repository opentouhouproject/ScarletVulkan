package org.scarlet.vulkan;

import org.lwjgl.vulkan.VkMemoryType;
import org.scarlet.vulkan.device.PhysicalDevice;

import static org.lwjgl.vulkan.VK10.VK_MAX_MEMORY_TYPES;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

/**
 * Utilities class for Vulkan related code.
 */
public class VulkanUtilities {
    /**
     * Private constructor.
     */
    private VulkanUtilities() {
        // Do nothing.
    }

    /**
     * Checks if a return code is not successful.
     * Throws a RuntimeException if the return code is not VK_SUCCESS.
     * @param returnCode - The Vulkan return code.
     * @param errorMessage - The error message.
     */
    public static void vkCheck(int returnCode, String errorMessage) {
        if (returnCode != VK_SUCCESS) {
            throw new RuntimeException(errorMessage + ": " + returnCode);
        }
    }

    /**
     * Get the memory type properties.
     * @param physicalDevice The physical device.
     * @param typeBits The supported memory types of the physical device.
     * @param reqMask The type of memory that is needed.
     * @return int - The index of the memory type.
     */
    public static int memoryTypeFromProperties(PhysicalDevice physicalDevice, int typeBits, int reqMask) {
        int result = -1;
        VkMemoryType.Buffer memoryTypes = physicalDevice.getDeviceMemoryProperties().memoryTypes();
        for (int i = 0; i < VK_MAX_MEMORY_TYPES; i++) {
            if ((typeBits & 1) == 1 && (memoryTypes.get(i).propertyFlags() & reqMask) == reqMask) {
                result = i;
                break;
            }
            typeBits >>= 1;
        }
        if (result < 0) {
            throw new RuntimeException("Failed to find memory type.");
        }
        return result;
    }
}