package org.scarlet.vulkan.surface;

import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkInstance;
import org.scarlet.EngineLogger;
import org.scarlet.vulkan.device.PhysicalDevice;

import java.nio.LongBuffer;
import java.util.logging.Level;

/**
 * Representation of a surface to display rendering results on.
 */
public class Surface {
    /**
     * Physical hardware device.
     */
    private final PhysicalDevice physicalDevice;

    /**
     * The handle to the Vulkan surface.
     */
    private long surface;

    /**
     * Constructor.
     * @param physicalDevice The physical hardware device.
     * @param windowHandle The handle of the window to create the surface for.
     */
    public Surface(PhysicalDevice physicalDevice, long windowHandle) {
        EngineLogger.getInstance().log(Level.INFO, "Creating Vulkan surface.");
        this.physicalDevice = physicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer pSurface = stack.mallocLong(1);
            VkInstance vulkanInstance = this.physicalDevice.getDevice().getInstance();
            GLFWVulkan.glfwCreateWindowSurface(vulkanInstance, windowHandle, null, pSurface);
            surface = pSurface.get(0);
        }
    }

    /**
     * Free resources.
     */
    public void cleanup() {
        EngineLogger.getInstance().log(Level.INFO, "Destroying Vulkan surface.");
        VkInstance vulkanInstance = physicalDevice.getDevice().getInstance();
        KHRSurface.vkDestroySurfaceKHR(vulkanInstance, surface, null);
    }

    /**
     * Retrieve the handle to the Vulkan surface.
     * @return long - The handle to the surface.
     */
    public long getSurface() {
        return surface;
    }
}