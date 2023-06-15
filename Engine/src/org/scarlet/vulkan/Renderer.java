package org.scarlet.vulkan;

import org.scarlet.ApplicationProperties;
import org.scarlet.EngineProperties;
import org.scarlet.Window;
import org.scarlet.vulkan.device.LogicalDevice;
import org.scarlet.vulkan.device.PhysicalDevice;
import org.scarlet.vulkan.device.PhysicalDeviceFactory;
import org.scarlet.vulkan.queue.GraphicsQueue;
import org.scarlet.vulkan.surface.Surface;

/**
 * Handles rendering.
 */
public class Renderer {
    /**
     * The Vulkan instance.
     */
    private final Instance instance;

    /**
     * The physical device.
     */
    private final PhysicalDevice physicalDevice;

    /**
     * The logical device.
     */
    private final LogicalDevice logicalDevice;

    /**
     * The graphics queue.
     */
    private final GraphicsQueue graphicsQueue;

    /**
     * The surface to render results on.
     */
    private final Surface surface;

    /**
     * Constructor.
     * @param window The application window.
     * @param scene The scene to render.
     */
    public Renderer(ApplicationProperties applicationProperties, Window window, Scene scene) {
        instance = new Instance(applicationProperties, EngineProperties.getInstance());
        physicalDevice = PhysicalDeviceFactory.create(instance, EngineProperties.getInstance().getDeviceName());
        logicalDevice = new LogicalDevice(physicalDevice);
        surface = new Surface(physicalDevice, window.getWindowHandle());
        graphicsQueue = new GraphicsQueue(logicalDevice, 0);
    }

    /**
     * Cleanup resources.
     */
    public void cleanup() {
        surface.cleanup();
        logicalDevice.cleanup();
        physicalDevice.cleanup();
        instance.cleanup();
    }

    /**
     * Renders the scene into the application window.
     * @param window The application window.
     * @param scene The scene.
     */
    public void render(Window window, Scene scene) {
        // To be implemented.
    }
}