package org.scarlet.vulkan;

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
}