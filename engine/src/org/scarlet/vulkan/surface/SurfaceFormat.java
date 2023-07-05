package org.scarlet.vulkan.surface;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.scarlet.vulkan.device.PhysicalDevice;

import java.nio.IntBuffer;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_SRGB;
import static org.scarlet.vulkan.VulkanUtilities.vkCheck;

/**
 * Format of the surface.
 */
public class SurfaceFormat {
    /**
     * Image format.
     */
    private int imageFormat;

    /**
     * Color space.
     */
    private int colorSpace;

    /**
     * Constructor.
     * @param device The physical device.
     * @param surface The surface.
     */
    public SurfaceFormat(PhysicalDevice device, Surface surface) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Get the number of surface formats.
            IntBuffer surfaceFormatCount = stack.mallocInt(1);
            vkCheck(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(
                    device.getDevice(), surface.getSurface(), surfaceFormatCount, null),
                    "Failed to get the number of surface formats.");
            int numberOfFormats = surfaceFormatCount.get(0);
            if (numberOfFormats <= 0) {
                throw new RuntimeException("No surface formats retrieved.");
            }

            // Get the surface formats.
            VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.calloc(numberOfFormats, stack);
            vkCheck(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(
                    device.getDevice(), surface.getSurface(), surfaceFormatCount, surfaceFormats),
                    "Failed to get surface formats.");

            // Set the surface format.
            imageFormat = VK_FORMAT_B8G8R8A8_SRGB;
            colorSpace = surfaceFormats.get(0).colorSpace();
            for (int i = 0; i < numberOfFormats; i++) {
                VkSurfaceFormatKHR surfaceFormatKHR = surfaceFormats.get(i);
                if (surfaceFormatKHR.format() == VK_FORMAT_B8G8R8A8_SRGB
                        && surfaceFormatKHR.colorSpace() == KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
                    imageFormat = surfaceFormatKHR.format();
                    colorSpace = surfaceFormatKHR.colorSpace();
                    break;
                }
            }
        }
    }

    /**
     * Get the image format.
     * @return int - The image format.
     */
    public int getImageFormat() {
        return imageFormat;
    }

    /**
     * Get the color space.
     * @return int - The color space.
     */
    public int getColorSpace() {
        return colorSpace;
    }
}