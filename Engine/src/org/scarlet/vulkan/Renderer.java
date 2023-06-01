package org.scarlet.vulkan;

import org.scarlet.ApplicationProperties;
import org.scarlet.EngineProperties;
import org.scarlet.Window;

/**
 * Handles rendering.
 */
public class Renderer {
    /**
     * The Vulkan instance.
     */
    private final Instance instance;

    /**
     * Constructor.
     * @param window The application window.
     * @param scene The scene to render.
     */
    public Renderer(ApplicationProperties applicationProperties, Window window, Scene scene) {
        instance = new Instance(applicationProperties, EngineProperties.getInstance(), true);
    }

    /**
     * Cleanup resources.
     */
    public void cleanup() {
        // To be implemented.
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